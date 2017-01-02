package rs.acs.uns.sw.sct.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import rs.acs.uns.sw.sct.companies.CompanyService;
import rs.acs.uns.sw.sct.security.TokenUtils;
import rs.acs.uns.sw.sct.security.UserSecurityUtil;
import rs.acs.uns.sw.sct.util.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing Report.
 */
@RestController
@RequestMapping("/api")
@SuppressWarnings("unused")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    CompanyService companyService;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    TokenUtils tokenUtils;

    @Autowired
    MailSender mailSender;

    @Autowired
    UserSecurityUtil userSecurityUtil;

    /**
     * POST  /users/auth : Authenticate user.
     *
     * @param username the email of user
     * @param password the password of user
     * @return the ResponseEntity with status 200 (OK) and with body the user
     * @throws AuthenticationException if the user cannot be authenticated
     */
    @PreAuthorize("permitAll()")
    @RequestMapping(
            value = "/users/auth",
            method = RequestMethod.POST
    )
    public ResponseEntity<AuthResponse> authenticate(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        final String token = tokenUtils.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(token));
    }

    /**
     * PUT  /users : Updates an existing user.
     *
     * @param user the user to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated company,
     * or with status 400 (Bad Request) if the company is not valid,
     * or with status 500 (Internal Server Error) if the company couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/users")
    public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody User user) throws URISyntaxException {
        if (user.getId() == null) {
            return registerUser(user);
        }

        UserDTO result = userService.save(user).convertToDTO();
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(Constants.EntityNames.USER, user.getId().toString()))
                .body(result);
    }


    /**
     * GET  /users/company/:companyId : get all users from one company.
     *
     * @param companyId the id of the company
     * @param pageable  the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of users in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/users/company/{companyId}")
    public ResponseEntity<List<UserDTO>> getAllUsersByCompanyId(@PathVariable Long companyId, Pageable pageable)
            throws URISyntaxException {
        Page<UserDTO> page = userService.findAllByCompany(companyId, pageable)
                .map(user -> user.convertToDTO());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/users/company");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * POST  /users : Register a new user.
     *
     * @param user the user to create
     * @return the ResponseEntity with status 201 (Created) and with body the new user, or with status 400 (Bad Request) if the user has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PreAuthorize("permitAll()")
    @PostMapping("/users/")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody User user) throws URISyntaxException {
        // TODO 6 - this method should be allowed only for guests - not logged in users
        if (user.getId() != null) {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.failure(
                            Constants.EntityNames.USER,
                            HeaderUtil.ERROR_CODE_CUSTOM_ID,
                            HeaderUtil.ERROR_MSG_CUSTOM_ID))
                    .body(null);
        }

        // OPTION 1 - provided email is already in use by another user
        if (userService.getUserByEmail(user.getEmail()) != null) {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.failure(
                            Constants.EntityNames.USER,
                            HeaderUtil.ERROR_CODE_EMAIL_ALREADY_IN_USE,
                            HeaderUtil.ERROR_MSG_EMAIL_ALREADY_IN_USE))
                    .body(null);
        }

        // OPTION 2 - provide username is already in use by another user
        if (userService.getUserByUsername(user.getUsername()) != null) {
            return ResponseEntity.
                    badRequest()
                    .headers(HeaderUtil.failure(
                            Constants.EntityNames.USER,
                            HeaderUtil.ERROR_CODE_USERNAME_ALREADY_IN_USE,
                            HeaderUtil.ERROR_MSG_USERNAME_ALREADY_IN_USE))
                    .body(null);
        }

        user.setVerified(false);
        User result = userService.save(user);

        // TODO 7 - this code is probably commented for development purpose
        mailSender.sendRegistrationMail(user.getFirstName(), user.getEmail(), null);

        return ResponseEntity.created(new URI("/api/users/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(Constants.EntityNames.USER, result.getId().toString()))
                .body(result.convertToDTO());
    }

    /**
     * GET  /users/deleted/:status : Get all users by status (deleted or not)
     *
     * @param status   deleted or not
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and with body the user, or with status 404 (Not Found)
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/users/deleted/{status}")
    public ResponseEntity<List<UserDTO>> getUsersByStatus(@PathVariable Boolean status, Pageable pageable)
            throws URISyntaxException {

        // If User is not ADMIN and want to get DELETED users
        if (!userSecurityUtil.checkAuthType(AuthorityRoles.ADMIN) && status)
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);

        Page<UserDTO> page = userService.findAllByStatus(status, pageable)
                .map(user -> user.convertToDTO());

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/users/deleted");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /users/:id : get the "id" user.
     *
     * @param id the id of the user to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the user, or with status 404 (Not Found)
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/users/{id}")
    public ResponseEntity getUser(@PathVariable Long id) {

        final User logged = userSecurityUtil.getLoggedUser();

        User user = userService.findOne(id);
        return Optional.ofNullable(user)
                .map(result -> new ResponseEntity<>(
                        (logged != null && user.getUsername().equals(logged.getUsername())) ? result : result.convertToDTO(),
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /users/search : get all the users that satisfied search params.
     *
     * @param username    username of the user
     * @param email       email of the user
     * @param firstName   first name of the user
     * @param lastName    last name of the user
     * @param phoneNumber phone number of the user
     * @param companyName company name of the user
     * @param pageable    pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of users in body
     */
    @PreAuthorize("hasAnyAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN, T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER, T(rs.acs.uns.sw.sct.util.AuthorityRoles).VERIFIER)")
    @GetMapping("/users/search")
    public ResponseEntity<List<UserDTO>> search(@RequestParam(value = "username", required = false) String username,
                                             @RequestParam(value = "email", required = false) String email,
                                             @RequestParam(value = "firstName", required = false) String firstName,
                                             @RequestParam(value = "lastName", required = false) String lastName,
                                             @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
                                             @RequestParam(value = "companyName", required = false) String companyName,
                                             Pageable pageable) {

        List<UserDTO> list = userService.findBySearchTerm(username, email, firstName, lastName, phoneNumber, companyName, pageable)
                .stream().map(user -> user.convertToDTO()).collect(Collectors.toList());

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    /**
     * GET  /users/search/typeAhead : get all the users that satisfied search params.
     *
     * @param firstName   first name of the user
     * @param lastName    last name of the user
     * @return the ResponseEntity with status 200 (OK) and the list of users in body
     */
    @PreAuthorize("hasAnyAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN, T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER, T(rs.acs.uns.sw.sct.util.AuthorityRoles).VERIFIER)")
    @GetMapping("/users/search/type-ahead")
    public ResponseEntity<List<User>> searchTypeAhead(@RequestParam(value = "firstName", required = false) String firstName,
                                             @RequestParam(value = "lastName", required = false) String lastName) {
        List<User> list = userService.findBySearchTermOR(firstName, lastName);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }


    /**
     * Method that checks if provided username is available for use by new user.
     *
     * @param username term to perform check on
     * @return the ResponseEntity with status 200 (OK) if username is available
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/users/username-available")
    public ResponseEntity<Boolean> isUsernameAvailable(@RequestParam(value = "username") String username) {
        return new ResponseEntity<>(userService.getUserByUsername(username) == null, HttpStatus.OK);
    }

    /**
     * Method that checks if provided email is available for use by new user.
     *
     * @param email term to perform check on
     * @return the ResponseEntity with status 200 (OK) if username is available
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/users/email-available")
    public ResponseEntity<Boolean> isEmailAvailable(@RequestParam(value = "email") String email) {
        return new ResponseEntity<>(userService.getUserByEmail(email) == null, HttpStatus.OK);
    }


    /**
     * Authentication response
     */
    private static class AuthResponse {
        private String token;

        public AuthResponse(final String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
