package rs.acs.uns.sw.sct.companies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.acs.uns.sw.sct.users.User;
import rs.acs.uns.sw.sct.users.UserService;
import rs.acs.uns.sw.sct.util.Constants;
import rs.acs.uns.sw.sct.util.HeaderUtil;
import rs.acs.uns.sw.sct.util.PaginationUtil;
import rs.acs.uns.sw.sct.security.UserSecurityUtil;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
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
     * @return the ResponseEntity with status 201 (Created) and with body the new company, or with status 400 (Bad Request) if the company has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PreAuthorize("hasAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN)")
    @PostMapping("/companies")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company company) throws URISyntaxException {
        if (company.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(HeaderUtil.COMPANY, "id_exists", "A new company cannot already have an ID")).body(null);
        }
        Company result = companyService.save(company);
        return ResponseEntity.created(new URI("/api/companies/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(HeaderUtil.COMPANY, result.getId().toString()))
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
    @PreAuthorize("hasAnyAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN, T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER, T(rs.acs.uns.sw.sct.util.AuthorityRoles).VERIFIER)")
    @PutMapping("/companies")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company company) throws URISyntaxException {
        if (company.getId() == null) {
            return createCompany(company);
        }
        Company result = companyService.save(company);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(HeaderUtil.COMPANY, company.getId().toString()))
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
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
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
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(HeaderUtil.COMPANY, id.toString())).build();
    }

    /**
     * PUT  /companies/:companyId/user-request : Request membership for company with id companyId.
     *
     * @param companyId request to be member of the company with companyId
     * @return the ResponseEntity with status 200 (OK) and with body the updated company,
     * or with status 400 (Bad Request) if the company is not valid,
     * or with status 500 (Internal Server Error) if the company couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PreAuthorize("hasAnyAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER, T(rs.acs.uns.sw.sct.util.AuthorityRoles).VERIFIER)")
    @PutMapping("/companies/{companyId}/user-request/")
    public ResponseEntity<?> requestCompanyMembership(@PathVariable Long companyId, @RequestParam(value = "confirmed", required = false) Boolean confirmed) throws URISyntaxException {
        final User user = userSecurityUtil.getLoggedUser();

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Company company = companyService.findOne(companyId);

        if (company == null)
            return new ResponseEntity<>("Wrong id of company", HttpStatus.BAD_REQUEST);
        if (user.getCompany() != null && (confirmed == null || !confirmed)) {
            return new ResponseEntity<>("Already requested company membership. Set request param confirmed=True to overwrite previous request", HttpStatus.CONFLICT);
        }

        user.setCompany(company);
        user.setCompanyVerified(Constants.CompanyStatus.PENDING);
        User updatedUser = userService.save(user);

        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(HeaderUtil.USER, user.getId().toString()))
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
    @PreAuthorize("hasAnyAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER, T(rs.acs.uns.sw.sct.util.AuthorityRoles).VERIFIER)")
    @GetMapping("/companies/users-requests")
    public ResponseEntity<?> getAllUsersRequestsByStatus(@RequestParam(value = "status") String status, Pageable pageable)
            throws URISyntaxException {

        final User user = userSecurityUtil.getLoggedUser();

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (user.getCompany() == null || !Constants.CompanyStatus.ACCEPTED.equals(user.getCompanyVerified())) {
            return new ResponseEntity<>("Can't see memberships that are not from your company.", HttpStatus.UNAUTHORIZED);
        }

        Page<User> page = userService.findAllByCompanyMembershipStatus(user.getCompany().getId(), status, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/companies/user-requests");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
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
    @PreAuthorize("hasAnyAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN, T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER, T(rs.acs.uns.sw.sct.util.AuthorityRoles).VERIFIER)")
    @PutMapping("/companies/resolve-request/user/{userId}")
    public ResponseEntity<?> resolveMembershipRequest(@PathVariable Long userId, @RequestParam(value = "accepted") Boolean accepted) throws URISyntaxException {
        final User companyMember = userSecurityUtil.getLoggedUser();

        if (companyMember == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userService.getUserById(userId);

        if (user == null)
            return new ResponseEntity<>("There is no user with id " + userId, HttpStatus.NOT_FOUND);
        if (user.getCompany() == null || user.getCompanyVerified() == null || !user.getCompanyVerified().equals(Constants.CompanyStatus.PENDING))
            return new ResponseEntity<>("User with this id doesn't request membership", HttpStatus.NOT_ACCEPTABLE);
        if (companyMember.getCompany() == null || !companyMember.getCompanyVerified().equals(Constants.CompanyStatus.ACCEPTED)
                || companyMember.getCompany().getId() != user.getCompany().getId())
            return new ResponseEntity<>("You don't have permission for resolving membership status", HttpStatus.METHOD_NOT_ALLOWED);

        if (accepted)
            user.setCompanyVerified(Constants.CompanyStatus.ACCEPTED);
        else
            user.setCompanyVerified(Constants.CompanyStatus.REJECTED);

        User updatedUser = userService.save(user);

        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(HeaderUtil.USER, user.getId().toString()))
                .body(updatedUser);
    }

    /**
     * GET  /companies/search : get all the companies that satisfied search params.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of companies in body
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/companies/search")
    public ResponseEntity<List<Company>> search(@RequestParam(value = "name", required = false) String name,
                                                @RequestParam(value = "address", required = false) String address,
                                                @RequestParam(value = "phoneNumber", required = false) String phoneNumber) {

        List<Company> list = companyService.findBySearchTerm(name, address, phoneNumber);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
