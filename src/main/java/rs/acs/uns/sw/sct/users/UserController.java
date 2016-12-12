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
import rs.acs.uns.sw.sct.util.HeaderUtil;
import rs.acs.uns.sw.sct.util.MailSender;
import rs.acs.uns.sw.sct.util.PaginationUtil;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) throws URISyntaxException {
        if (user.getId() == null) {
            return registerUser(user);
        }

        User result = userService.save(user);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(HeaderUtil.USER, user.getId().toString()))
                .body(result);
    }


    /**
     * GET  /users/company/:companyId : get all users from one company.
     *
     * @param companyId the id of the company
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of users in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/users/company/{companyId}")
    public ResponseEntity<List<User>> getAllUsersByCompanyId(@PathVariable Long companyId, Pageable pageable)
            throws URISyntaxException {
        Page<User> page = userService.findAllByCompany(companyId, pageable);
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
    public ResponseEntity<User> registerUser(@Valid @RequestBody User user) throws URISyntaxException {
        if (user.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(HeaderUtil.USER, "id_exists", "A new user cannot already have an ID")).body(null);
        }

        User result = userService.save(user);

        mailSender.sendRegistrationMail(user.getFirstName(), user.getEmail());

        return ResponseEntity.created(new URI("/api/users/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(HeaderUtil.USER, result.getId().toString()))
                .body(result);
    }

    /**
     * GET  /users/deleted/:status : Get all users by status (deleted or not)
     *
     * @param status deleted or not
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and with body the user, or with status 404 (Not Found)
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/users/deleted/{status}")
    public ResponseEntity<List<User>> getUsersByStatus(@PathVariable Boolean status, Pageable pageable)
            throws URISyntaxException {
        Page<User> page = userService.findAllByStatus(status, pageable);
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
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.findOne(id);
        return Optional.ofNullable(user)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
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
