package rs.acs.uns.sw.sct.marks;

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
import rs.acs.uns.sw.sct.util.AuthorityRoles;
import rs.acs.uns.sw.sct.util.Constants;
import rs.acs.uns.sw.sct.util.HeaderUtil;
import rs.acs.uns.sw.sct.util.PaginationUtil;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Mark.
 */
@RestController
@RequestMapping("/api")
@SuppressWarnings("unused")
public class MarkController {

    @Autowired
    private MarkService markService;

    @Autowired
    private UserSecurityUtil userSecurityUtil;

    /**
     * POST  /marks : Create a new mark.
     *
     * @param markDTO the mark to create
     * @return the ResponseEntity with status 201 (Created) and with body the new mark,
     * or with status 400 (Bad Request) if the mark has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PreAuthorize("hasAnyAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER, T(rs.acs.uns.sw.sct.util.AuthorityRoles).VERIFIER)")
    @PostMapping("/marks")
    public ResponseEntity<MarkDTO> createMark(@Valid @RequestBody MarkDTO markDTO) throws URISyntaxException {
        if (markDTO.getId() != null) {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.failure(
                            Constants.EntityNames.MARK,
                            HeaderUtil.ERROR_CODE_CUSTOM_ID,
                            HeaderUtil.ERROR_MSG_CUSTOM_ID))
                    .body(null);
        }

        final User user = userSecurityUtil.getLoggedUser();

        final Mark mark = markDTO.convertToMark();

        mark.grader(user);

        // OPTION 1 - advertisers cannot rate announcements by company which members they are
        if (userSecurityUtil.checkAuthType(AuthorityRoles.ADVERTISER) &&
                user.getCompany() != null &&
                user.getCompany().getId().equals(mark.getAnnouncement().getAuthor().getCompany().getId())) {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.failure(
                            Constants.EntityNames.USER,
                            HeaderUtil.ERROR_CODE_CANNOT_RATE_OWN_COMPANY_ANNOUNCEMENT,
                            HeaderUtil.ERROR_MSG_CANNOT_RATE_OWN_COMPANY_ANNOUNCEMENT))
                    .body(null);
        }

        Mark result = markService.save(mark);
        return ResponseEntity
                .created(new URI("/api/marks/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(Constants.EntityNames.MARK, result.getId().toString()))
                .body(result.convertToDTO());
    }

    /**
     * PUT  /marks : Updates an existing mark.
     *
     * @param markDTO the mark to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated mark,
     * or with status 400 (Bad Request) if the mark is not valid,
     * or with status 500 (Internal Server Error) if the mark couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PreAuthorize("hasAnyAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER, T(rs.acs.uns.sw.sct.util.AuthorityRoles).VERIFIER)")
    @PutMapping("/marks")
    public ResponseEntity<MarkDTO> updateMark(@Valid @RequestBody MarkDTO markDTO) throws URISyntaxException {
        if (markDTO.getId() == null) {
            return createMark(markDTO);
        }

        // OPTION 1 - user cannot update mark created by another user
        if (!markService.findOne(markDTO.getId()).getGrader().getUsername()
                .equals(userSecurityUtil.getLoggedUserUsername())) {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.failure(
                            Constants.EntityNames.MARK,
                            HeaderUtil.ERROR_CODE_NOT_OWNER,
                            HeaderUtil.ERROR_MSG_NOT_OWNER))
                    .body(null);
        }

        final Mark mark = markDTO.convertToMark();

        Mark result = markService.save(mark);
        return ResponseEntity
                .ok()
                .headers(HeaderUtil.createEntityUpdateAlert(Constants.EntityNames.MARK, mark.getId().toString()))
                .body(result.convertToDTO());
    }

    /**
     * GET  /marks : get all the marks.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of marks in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @PreAuthorize("hasAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN)")
    @GetMapping("/marks")
    public ResponseEntity<List<MarkDTO>> getAllMarks(Pageable pageable) throws URISyntaxException {
        // TODO 3 - this option should not be allowed
        Page<MarkDTO> page = markService.findAll(pageable)
                .map(mark -> mark.convertToDTO());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/marks");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /marks/:id : get the "id" mark.
     *
     * @param id the id of the mark to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the mark, or with status 404 (Not Found)
     */
    @PreAuthorize("hasAnyAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN, T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER, T(rs.acs.uns.sw.sct.util.AuthorityRoles).VERIFIER)")
    @GetMapping("/marks/{id}")
    public ResponseEntity<MarkDTO> getMark(@PathVariable Long id) {
        Mark mark = markService.findOne(id);
        return Optional.ofNullable(mark)
                .map(result -> new ResponseEntity<>(
                        result.convertToDTO(),
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /marks/announcement/:announcementId : get all marks for one announcement.
     *
     * @param announcementId the id of the announcement
     * @param pageable       the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of marks in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/marks/announcement/{announcementId}")
    public ResponseEntity<List<MarkDTO>> getAllAnnouncementsByAnnouncementId(@PathVariable Long announcementId, Pageable pageable)
            throws URISyntaxException {
        Page<MarkDTO> page = markService.findAllByAnnouncement(announcementId, pageable)
                .map(mark -> mark.convertToDTO());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/marks/announcement");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /marks/user/:userId : get all marks for one user.
     *
     * @param userId the id of the user
     * @param pageable       the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of marks in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/marks/user/{userId}")
    public ResponseEntity<List<MarkDTO>> getAllAnnouncementsByUserId(@PathVariable Long userId, Pageable pageable)
            throws URISyntaxException {
        Page<MarkDTO> page = markService.findAllByUser(userId, pageable)
                .map(mark -> mark.convertToDTO());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/marks/user");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * DELETE  /marks/:id : delete the "id" mark.
     *
     * @param id the id of the mark to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @PreAuthorize("hasAnyAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER, T(rs.acs.uns.sw.sct.util.AuthorityRoles).VERIFIER)")
    @DeleteMapping("/marks/{id}")
    public ResponseEntity<Void> deleteMark(@PathVariable Long id) {
        // OPTION 1 - user cannot delete mark created by another user
        if (!markService.findOne(id).getGrader().getUsername()
                .equals(userSecurityUtil.getLoggedUserUsername())) {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.failure(
                            Constants.EntityNames.MARK,
                            HeaderUtil.ERROR_CODE_NOT_OWNER,
                            HeaderUtil.ERROR_MSG_NOT_OWNER))
                    .body(null);
        }
        markService.delete(id);

        return ResponseEntity
                .ok()
                .headers(HeaderUtil.createEntityDeletionAlert(Constants.EntityNames.MARK, id.toString()))
                .build();
    }
}
