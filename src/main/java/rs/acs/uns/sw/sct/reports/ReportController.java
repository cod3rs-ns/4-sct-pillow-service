package rs.acs.uns.sw.sct.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.acs.uns.sw.sct.announcements.Announcement;
import rs.acs.uns.sw.sct.announcements.AnnouncementService;
import rs.acs.uns.sw.sct.security.UserSecurityUtil;
import rs.acs.uns.sw.sct.users.User;
import rs.acs.uns.sw.sct.users.UserService;
import rs.acs.uns.sw.sct.util.Constants;
import rs.acs.uns.sw.sct.util.HeaderUtil;
import rs.acs.uns.sw.sct.util.MailSender;
import rs.acs.uns.sw.sct.util.PaginationUtil;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
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

    @Autowired
    private MailSender mailSender;

    /**
     * POST  /reports : Create a new report.
     *
     * @param report the report to create
     * @return the ResponseEntity with status 201 (Created) and with body the new report, or with status 400 (Bad Request) if the report has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PreAuthorize("permitAll()")
    @PostMapping("/reports")
    public ResponseEntity<ReportDTO> createReport(@Valid @RequestBody Report report) throws URISyntaxException {
        if (report.getId() != null) {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.failure(
                            Constants.EntityNames.REPORT,
                            HeaderUtil.ERROR_CODE_CUSTOM_ID,
                            HeaderUtil.ERROR_MSG_CUSTOM_ID))
                    .body(null);
        }

        final User user = userSecurityUtil.getLoggedUser();
        if (user != null)
            report.setEmail(user.getEmail());

        report.setCreatedAt(new Date());
        report.setReporter(user);
        report.setStatus(Constants.ReportStatus.PENDING);

        Announcement announcement = announcementService.findOne(report.getAnnouncement().getId());

        // OPTION 1 - user is trying to create report for announcement that doesn't exist
        if (announcement == null) {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.failure(
                            Constants.EntityNames.ANNOUNCEMENT,
                            HeaderUtil.ERROR_CODE_NON_EXISTING_ENTITY,
                            HeaderUtil.ERROR_MSG_NON_EXISTING_ENTITY))
                    .body(null);
        }

        // OPTION 2 - user is trying to create report for announcement that is verified
        if (announcement.getVerified().equals(Constants.VerifiedStatuses.VERIFIED))
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.failure(
                            Constants.EntityNames.REPORT,
                            HeaderUtil.ERROR_CODE_REPORT_VERIFIED_ANNOUNCEMENT,
                            HeaderUtil.ERROR_MSG_REPORT_VERIFIED_ANNOUNCEMENT))
                    .body(null);

        report.setAnnouncement(announcement);

        // OPTION 3 - user cannot post more than one report which is not resolved at the same time
        Report exists = reportService.findByReporterEmailAndStatusAndAnnouncementId(report.getEmail(), report.getStatus(), announcement.getId());
        if (exists != null)
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.failure(
                            Constants.EntityNames.REPORT,
                            HeaderUtil.ERROR_CODE_CANNOT_POST_MULTIPLE_REPORTS,
                            HeaderUtil.ERROR_MSG_CANNOT_POST_MULTIPLE_REPORTS))
                    .body(null);

        Report result = reportService.save(report);
        return ResponseEntity
                .created(new URI("/api/reports/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(Constants.EntityNames.REPORT, result.getId().toString()))
                .body(result.convertToDTO());
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
    @PreAuthorize("hasAnyAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN, T(rs.acs.uns.sw.sct.util.AuthorityRoles).VERIFIER)")
    @PutMapping("/reports")
    public ResponseEntity<ReportDTO> updateReport(@Valid @RequestBody Report report) throws URISyntaxException {
        if (report.getId() == null) {
            return createReport(report);
        }
        // TODO 5 - existing of this method should be considered
        Report result = reportService.save(report);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(Constants.EntityNames.REPORT, report.getId().toString()))
                .body(result.convertToDTO());
    }

    /**
     * GET  /reports : get all the reports.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of reports in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @PreAuthorize("hasAnyAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN, T(rs.acs.uns.sw.sct.util.AuthorityRoles).VERIFIER)")
    @GetMapping("/reports")
    public ResponseEntity<List<ReportDTO>> getAllReports(Pageable pageable)
            throws URISyntaxException {
        Page<ReportDTO> page = reportService.findAll(pageable)
                .map(report -> report.convertToDTO());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/reports");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /reports/:id : get the "id" report.
     *
     * @param id the id of the report to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the report, or with status 404 (Not Found)
     */
    @PreAuthorize("hasAnyAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN, T(rs.acs.uns.sw.sct.util.AuthorityRoles).VERIFIER)")
    @GetMapping("/reports/{id}")
    public ResponseEntity<ReportDTO> getReport(@PathVariable Long id) {
        Report report = reportService.findOne(id);
        return Optional.ofNullable(report)
                .map(result -> new ResponseEntity<>(
                        result.convertToDTO(),
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /reports/:id : delete the "id" report.
     *
     * @param id the id of the report to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @PreAuthorize("hasAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN)")
    @DeleteMapping("/reports/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        final Report report = reportService.findOne(id);
        if (report == null)
            return ResponseEntity
                    .notFound()
                    .headers(HeaderUtil.failure(
                            Constants.EntityNames.REPORT,
                            HeaderUtil.ERROR_CODE_NON_EXISTING_ENTITY,
                            HeaderUtil.ERROR_MSG_NON_EXISTING_ENTITY))
                    .build();

        reportService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(Constants.EntityNames.REPORT, id.toString())).build();
    }

    /**
     * GET  /reports/status/:status : get all the reports by status.
     *
     * @param pageable the pagination information
     * @param status   the status of report
     * @return the ResponseEntity with status 200 (OK) and the list of reports in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @PreAuthorize("hasAnyAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN, T(rs.acs.uns.sw.sct.util.AuthorityRoles).VERIFIER)")
    @GetMapping("/reports/status/{status}")
    public ResponseEntity<List<ReportDTO>> getAllReportsByStatus(Pageable pageable, @PathVariable String status)
            throws URISyntaxException {
        Page<ReportDTO> page = reportService.findByStatus(status, pageable)
                .map(report -> report.convertToDTO());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/reports/status");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /reports/author/:email : get all the reports by author email.
     *
     * @param pageable the pagination information
     * @param email    the author email
     * @return the ResponseEntity with status 200 (OK) and the list of reports in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @PreAuthorize("hasAnyAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN, T(rs.acs.uns.sw.sct.util.AuthorityRoles).VERIFIER)")
    @GetMapping("/reports/author/{email:.+}")
    public ResponseEntity<List<ReportDTO>> getAllReportsByAuthorEmail(Pageable pageable, @PathVariable String email)
            throws URISyntaxException {
        Page<ReportDTO> page = reportService.findByAuthorEmail(email, pageable)
                .map(report -> report.convertToDTO());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/reports/author");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * PUT  /reports/resolve/:id?status="statusValue" : Resolving an existing report.
     *
     * @param id     the report to be resolved
     * @param status the status of report
     * @return the ResponseEntity with status 200 (OK) and with body the updated report,
     * or with status 404 (Not Found) if the report does not exists,
     */
    @PreAuthorize("hasAnyAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN, T(rs.acs.uns.sw.sct.util.AuthorityRoles).VERIFIER)")
    @PutMapping("/reports/resolve/{id}")
    public ResponseEntity<ReportDTO> resolveReport(@PathVariable Long id, @RequestParam(value = "status") String status) {
        Report report = reportService.findOne(id);
        // OPTION 1 - user is trying to resolve report that doesn't exist
        if (report == null)
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .headers(HeaderUtil.failure(
                            Constants.EntityNames.REPORT,
                            HeaderUtil.ERROR_CODE_NON_EXISTING_ENTITY,
                            HeaderUtil.ERROR_MSG_NON_EXISTING_ENTITY))
                    .body(null);

        // OPTION 2 - user is trying to resolve report that have already been resolved
        if (!Constants.ReportStatus.PENDING.equals(report.getStatus()))
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.failure(
                            Constants.EntityNames.REPORT,
                            HeaderUtil.ERROR_CODE_REPORT_ALREADY_RESOLVED,
                            HeaderUtil.ERROR_MSG_REPORT_ALREADY_RESOLVED))
                    .body(null);

        if (Constants.ReportStatus.ACCEPTED.equals(status)) {
            Announcement announcement = announcementService.findOne(report.getAnnouncement().getId());
            announcement.deleted(true);
            Announcement persistedAnnouncement = announcementService.save(announcement);
            report.setStatus(Constants.ReportStatus.ACCEPTED);
            report.setAnnouncement(persistedAnnouncement);
        } else if (Constants.ReportStatus.REJECTED.equals(status)) {
            report.setStatus(Constants.ReportStatus.REJECTED);
        } else {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.failure(
                            Constants.EntityNames.REPORT,
                            HeaderUtil.ERROR_CODE_PROVIDED_UNKNOWN_REPORT_STATUS,
                            HeaderUtil.ERROR_MSG_PROVIDED_UNKNOWN_REPORT_STATUS))
                    .body(null);
        }

        Report result = reportService.save(report);

        // send email to author
        if (result.getStatus().equals(Constants.ReportStatus.ACCEPTED)) {
            mailSender.sendReportAcceptedMail(
                    report.getContent(),
                    report.getAnnouncement().getId(),
                    report.getAnnouncement().getName(),
                    report.getAnnouncement().getAuthor().getEmail());
        }

        return ResponseEntity
                .ok()
                .headers(HeaderUtil.createEntityUpdateAlert(Constants.EntityNames.REPORT, report.getId().toString()))
                .body(result.convertToDTO());
    }

    /**
     * GET  /reports/exists : check if report already exists.
     *
     * @param email          the author email
     * @param announcementId the id of announcement that should be reported
     * @param username       the author username
     * @return the ResponseEntity with status 200 (OK) and the boolean value in body
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/reports/exists")
    public ResponseEntity<Boolean> alreadyReported(@RequestParam(value = "email", required = false) String email,
                                                   @RequestParam(value = "id") Long announcementId,
                                                   @RequestParam(value = "username", required = false) String username) {
        String userEmail = null;
        if (username != null) {
            User user = userService.getUserByUsername(username);
            if (user != null)
                userEmail = user.getEmail();
        }

        userEmail = (userEmail != null) ? userEmail : email;

        Report exists = reportService.findByReporterEmailAndStatusAndAnnouncementId(userEmail, Constants.ReportStatus.PENDING, announcementId);
        boolean retVal = exists != null;
        return new ResponseEntity<>(retVal, HttpStatus.OK);
    }
}
