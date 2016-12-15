package rs.acs.uns.sw.sct.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import rs.acs.uns.sw.sct.announcements.Announcement;
import rs.acs.uns.sw.sct.announcements.AnnouncementService;
import rs.acs.uns.sw.sct.users.User;
import rs.acs.uns.sw.sct.users.UserService;
import rs.acs.uns.sw.sct.util.AuthorityRoles;
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
 * REST controller for managing Report.
 */
@RestController
@RequestMapping("/api")
@SuppressWarnings("unused")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserService userService;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private UserSecurityUtil userSecurityUtil;

    /**
     * POST  /reports : Create a new report.
     *
     * @param report the report to create
     * @return the ResponseEntity with status 201 (Created) and with body the new report, or with status 400 (Bad Request) if the report has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PreAuthorize("permitAll()")
    @PostMapping("/reports")
    public ResponseEntity<Report> createReport(@Valid @RequestBody Report report) throws URISyntaxException {
        if (report.getId() != null) {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.createFailureAlert(Constants.EntityNames.REPORT, HeaderUtil.ERROR_CODE_CUSTOM_ID, HeaderUtil.ERROR_MSG_CUSTOM_ID))
                    .body(null);
        }

        final User user = userSecurityUtil.getLoggedUser();
        report.setReporter(user);

        Announcement announcement = announcementService.findOne(report.getAnnouncement().getId());
        if (announcement == null){
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.createFailureAlert(Constants.EntityNames.REPORT, HeaderUtil.ERROR_CODE_NON_EXISTING_ANNOUNCEMENT, HeaderUtil.ERROR_MSG_NON_EXISTING_ANNOUNCEMENT))
                    .body(null);
        }

        if (announcement.getVerified().equals(Constants.VerifiedStatuses.VERIFIED))
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.createFailureAlert(Constants.EntityNames.REPORT, HeaderUtil.ERROR_CODE_REPORT_VERIFED_ANNOUNCEMENT, HeaderUtil.ERROR_MSG_REPORT_VERIFIED_ANNOUNCEMENT))
                    .body(null);

        report.setAnnouncement(announcement);

        Report result = reportService.save(report);
        return ResponseEntity.created(new URI("/api/reports/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(Constants.EntityNames.REPORT, result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /reports : Updates an existing report.
     *
     * @param report the report to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated report,
     * or with status 400 (Bad Request) if the report is not valid,
     * or with status 500 (Internal Server Error) if the report couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PreAuthorize("permitAll()")
    @PutMapping("/reports")
    public ResponseEntity<Report> updateReport(@Valid @RequestBody Report report) throws URISyntaxException {
        if (report.getId() == null) {
            return createReport(report);
        }
        // check if user has no rights to update
        if (!userSecurityUtil.getLoggedUserAuthorities().contains(new SimpleGrantedAuthority(AuthorityRoles.ADMIN)) &&
                !reportService.findOne(report.getId()).getReporter().getUsername()
                        .equals(userSecurityUtil.getLoggedUserUsername())) {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.createFailureAlert(Constants.EntityNames.REPORT, HeaderUtil.ERROR_CODE_NOT_OWNER, HeaderUtil.ERROR_MSG_NOT_OWNER))
                    .body(null);
        }
        Report result = reportService.save(report);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(Constants.EntityNames.REPORT, report.getId().toString()))
                .body(result);
    }

    /**
     * GET  /reports : get all the reports.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of reports in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @PreAuthorize("hasAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN)")
    @GetMapping("/reports")
    public ResponseEntity<List<Report>> getAllReports(Pageable pageable)
            throws URISyntaxException {
        Page<Report> page = reportService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/reports");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /reports/:id : get the "id" report.
     *
     * @param id the id of the report to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the report, or with status 404 (Not Found)
     */
    @PreAuthorize("hasAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN)")
    @GetMapping("/reports/{id}")
    public ResponseEntity<Report> getReport(@PathVariable Long id) {
        Report report = reportService.findOne(id);
        return Optional.ofNullable(report)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /reports/:id : delete the "id" report.
     *
     * @param id the id of the report to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @PreAuthorize("permitAll()")
    @DeleteMapping("/reports/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {

        final Report report = reportService.findOne(id);


        if (report == null)
            return ResponseEntity
                    .notFound()
                    .build();

        // check if user has no rights to update
        if (!userSecurityUtil.getLoggedUserAuthorities().contains(new SimpleGrantedAuthority(AuthorityRoles.ADMIN)) &&
                !reportService.findOne(report.getId()).getReporter().getUsername()
                        .equals(userSecurityUtil.getLoggedUserUsername())) {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.createFailureAlert(Constants.EntityNames.COMMENT, HeaderUtil.ERROR_CODE_NOT_OWNER, HeaderUtil.ERROR_MSG_NOT_OWNER))
                    .body(null);
        }

        reportService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(Constants.EntityNames.REPORT, id.toString())).build();
    }

    /**
     * GET  /reports/status/:status : get all the reports by status.
     *
     * @param pageable the pagination information
     * @param status the status of report
     * @return the ResponseEntity with status 200 (OK) and the list of reports in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @PreAuthorize("hasAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN)")
    @GetMapping("/reports/status/{status}")
    public ResponseEntity<List<Report>> getAllReportsByStatus(Pageable pageable, @PathVariable String status)
            throws URISyntaxException {
        Page<Report> page = reportService.findByStatus(status, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/reports/status");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /reports/author/:email : get all the reports by author email.
     *
     * @param pageable the pagination information
     * @param email the author email
     * @return the ResponseEntity with status 200 (OK) and the list of reports in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/reports/author/{email:.+}")
    public ResponseEntity<List<Report>> getAllReportsByAuthorEmail(Pageable pageable, @PathVariable String email)
            throws URISyntaxException {
        Page<Report> page = reportService.findByAuthorEmail(email, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/reports/author");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
}
