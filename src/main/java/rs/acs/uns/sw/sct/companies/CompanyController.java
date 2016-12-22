package rs.acs.uns.sw.sct.companies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.acs.uns.sw.sct.security.UserSecurityUtil;
import rs.acs.uns.sw.sct.users.User;
import rs.acs.uns.sw.sct.users.UserService;
import rs.acs.uns.sw.sct.util.AuthorityRoles;
import rs.acs.uns.sw.sct.util.Constants;
import rs.acs.uns.sw.sct.util.HeaderUtil;
import rs.acs.uns.sw.sct.util.PaginationUtil;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Company.
 */
@RestController
@RequestMapping("/api")
@SuppressWarnings("unused")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserSecurityUtil userSecurityUtil;


    /**
     * POST  /companies : Create a new company.
     *
     * @param company the company to create
     * @return the ResponseEntity with status 201 (Created) and with body the new company,
     * or with status 400 (Bad Request) if the company has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PreAuthorize("hasAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN)")
    @PostMapping("/companies")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company company) throws URISyntaxException {
        if (company.getId() != null) {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.failure(
                            Constants.EntityNames.COMPANY,
                            HeaderUtil.ERROR_CODE_CUSTOM_ID,
                            HeaderUtil.ERROR_MSG_CUSTOM_ID))
                    .body(null);
        }

        Company result = companyService.save(company);
        return ResponseEntity.created(new URI("/api/companies/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(
                        Constants.EntityNames.COMPANY,
                        result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /companies : Updates an existing company.
     *
     * @param company the company to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated company,
     * or with status 400 (Bad Request) if the company is not valid,
     * or with status 500 (Internal Server Error) if the company couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PreAuthorize("hasAnyAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN, T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER)")
    @PutMapping("/companies")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company company) throws URISyntaxException {
        if (company.getId() == null) {
            return createCompany(company);
        }
        final User companyMember = userSecurityUtil.getLoggedUser();

        // OPTION 1 - user doesn't have permission to update company
        if (!userSecurityUtil.checkAuthType(AuthorityRoles.ADMIN) && (companyMember.getCompany() == null ||
                !companyMember.getCompanyVerified().equals(Constants.CompanyStatus.ACCEPTED) ||
                !companyMember.getCompany().getId().equals(company.getId()))) {
                return ResponseEntity
                        .badRequest()
                        .headers(HeaderUtil.failure(
                                Constants.EntityNames.COMPANY,
                                HeaderUtil.ERROR_CODE_NOT_MEMBER_OF_COMPANY,
                                HeaderUtil.ERROR_MSG_NOT_MEMBER_OF_COMPANY
                        ))
                        .body(null);
        }

        Company result = companyService.save(company);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(
                        Constants.EntityNames.COMPANY,
                        company.getId().toString()))
                .body(result);
    }

    /**
     * GET  /companies : get all the companies.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of companies in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/companies")
    public ResponseEntity<List<Company>> getAllCompanies(Pageable pageable)
            throws URISyntaxException {
        Page<Company> page = companyService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/companies");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /companies/:id : get the "id" company.
     *
     * @param id the id of the company to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the company, or with status 404 (Not Found)
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/companies/{id}")
    public ResponseEntity<Company> getCompany(@PathVariable Long id) {
        Company company = companyService.findOne(id);
        return Optional.ofNullable(company)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /companies/:id : delete the "id" company.
     *
     * @param id the id of the company to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @PreAuthorize("hasAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN)")
    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyService.delete(id);
        return ResponseEntity
                .ok()
                .headers(HeaderUtil.createEntityDeletionAlert(
                        Constants.EntityNames.COMPANY,
                        id.toString())).build();
    }

    /**
     * PUT  /companies/:companyId/user-request : Request membership for company with id companyId.
     *
     * @param companyId request to be member of the company with companyId
     * @param confirmed confirmed identifer
     * @return the ResponseEntity with status 200 (OK) and with body the updated company,
     * or with status 400 (Bad Request) if the company is not valid,
     * or with status 500 (Internal Server Error) if the company couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PreAuthorize("hasAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER)")
    @PutMapping("/companies/{companyId}/user-request/")
    public ResponseEntity<User> requestCompanyMembership(@PathVariable Long companyId, @RequestParam(value = "confirmed", required = false) Boolean confirmed) throws URISyntaxException {
        final User user = userSecurityUtil.getLoggedUser();

        Company company = companyService.findOne(companyId);

        // OPTION 1 - company doesn't exists
        if (company == null)
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.failure(
                            Constants.EntityNames.COMPANY,
                            HeaderUtil.ERROR_CODE_NON_EXISTING_ENTITY,
                            HeaderUtil.ERROR_MSG_NON_EXISTING_ENTITY))
                    .body(null);

        // OPTION 2 - request to change company without confirmed flag
        if (user.getCompany() != null && (confirmed == null || !confirmed)) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .headers(HeaderUtil.failure(
                            Constants.EntityNames.COMPANY,
                            HeaderUtil.ERROR_CODE_ALREADY_REQUESTED_MEMBERSHIP,
                            HeaderUtil.ERROR_MSG_ALREADY_REQUESTED_MEMBERSHIP))
                    .body(null);
        }

        user.setCompany(company);
        user.setCompanyVerified(Constants.CompanyStatus.PENDING);
        User updatedUser = userService.save(user);

        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(
                        Constants.EntityNames.USER,
                        user.getId().toString()))
                .body(updatedUser);
    }


    /**
     * GET  /companies/:companyId/users-requests : get all the companies.
     *
     * @param pageable the pagination information
     * @param status   status of membership request
     * @return the ResponseEntity with status 200 (OK) and the list of companies in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @PreAuthorize("hasAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER)")
    @GetMapping("/companies/users-requests")
    public ResponseEntity<Collection<User>> getAllUsersRequestsByStatus(@RequestParam(value = "status") String status, Pageable pageable)
            throws URISyntaxException {

        final User user = userSecurityUtil.getLoggedUser();

        // OPTION 1 - user not confirmed member of company
        if (user.getCompany() == null || !Constants.CompanyStatus.ACCEPTED.equals(user.getCompanyVerified())) {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.failure(
                            Constants.EntityNames.COMPANY,
                            HeaderUtil.ERROR_CODE_NOT_MEMBER_OF_COMPANY,
                            HeaderUtil.ERROR_MSG_NOT_MEMBER_OF_COMPANY
                    ))
                    .body(null);
        }

        Page<User> page = userService.findAllByCompanyMembershipStatus(user.getCompany().getId(), status, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/companies/user-requests");
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(page.getContent());
    }

    /**
     * PUT  /companies/resolve-request/user/:userId : Resolve membership for one user.
     *
     * @param userId   request to be member of the company with userId
     * @param accepted if True membership request is accepted, otherwise is rejected
     * @return the ResponseEntity with status 200 (OK) and with body the updated user,
     * or with status 400 (Bad Request) if the company is not valid,
     * or with status 500 (Internal Server Error) if the company couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PreAuthorize("hasAnyAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN, T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER)")
    @PutMapping("/companies/resolve-request/user/{userId}")
    public ResponseEntity<User> resolveMembershipRequest(@PathVariable Long userId, @RequestParam(value = "accepted") Boolean accepted) throws URISyntaxException { // NOSONAR
        final User companyMember = userSecurityUtil.getLoggedUser();
        final User userToResolve = userService.getUserById(userId);

        // OPTION 1 - User which request should be resolve doesn't exist
        if (userToResolve == null)
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .headers(HeaderUtil.failure(
                            Constants.EntityNames.USER,
                            HeaderUtil.ERROR_CODE_NON_EXISTING_ENTITY,
                            HeaderUtil.ERROR_MSG_NON_EXISTING_ENTITY))
                    .body(null);

        // OPTION 3 - User didn't request company membership
        if (userToResolve.getCompany() == null || userToResolve.getCompanyVerified() == null || userToResolve.getCompanyVerified().equals(Constants.CompanyStatus.ACCEPTED))
            return ResponseEntity
                    .status(HttpStatus.NOT_ACCEPTABLE)
                    .headers(HeaderUtil.failure(
                            Constants.EntityNames.USER,
                            HeaderUtil.ERROR_CODE_USER_DID_NOT_REQUEST_MEMBERSHIP,
                            HeaderUtil.ERROR_MSG_USER_DID_NOT_REQUEST_MEMBERSHIP))
                    .body(null);

        // OPTION 2 - User doesn't have permission to resolve request
        // - member of other company or not yet resolved status
        if (companyMember.getCompany() == null ||
                !companyMember.getCompanyVerified().equals(Constants.CompanyStatus.ACCEPTED) ||
                userToResolve.getCompany() == null ||
                !companyMember.getCompany().getId().equals(userToResolve.getCompany().getId()))
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.failure(
                            Constants.EntityNames.USER,
                            HeaderUtil.ERROR_CODE_NO_PERMISSION_TO_RESOLVE_MEMBERSHIP,
                            HeaderUtil.ERROR_MSG_NO_PERMISSION_TO_RESOLVE_MEMBERSHIP))
                    .body(null);

        if (accepted)
            userToResolve.setCompanyVerified(Constants.CompanyStatus.ACCEPTED);
        else
            userToResolve.setCompanyVerified(Constants.CompanyStatus.REJECTED);

        User updatedUser = userService.save(userToResolve);

        return ResponseEntity
                .ok()
                .headers(HeaderUtil.createEntityUpdateAlert(
                        Constants.EntityNames.USER,
                        userToResolve.getId().toString()))
                .body(updatedUser);
    }

    /**
     * GET  /companies/search : get all the companies that satisfied search params.
     *
     * @param name        name of the company
     * @param address     company address
     * @param phoneNumber phone number of the company
     * @param pageable    pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of companies in body
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/companies/search")
    public ResponseEntity<List<Company>> search(@RequestParam(value = "name", required = false) String name,
                                                @RequestParam(value = "address", required = false) String address,
                                                @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
                                                Pageable pageable) {

        List<Company> list = companyService.findBySearchTerm(name, address, phoneNumber, pageable);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
