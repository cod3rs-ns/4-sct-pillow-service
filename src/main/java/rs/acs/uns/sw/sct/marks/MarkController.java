package rs.acs.uns.sw.sct.marks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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
public class MarkController {

    @Autowired
    private MarkService markService;

    /**
     * POST  /marks : Create a new mark.
     *
     * @param mark the mark to create
     * @return the ResponseEntity with status 201 (Created) and with body the new mark, or with status 400 (Bad Request) if the mark has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PreAuthorize("hasAnyAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN, T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER, T(rs.acs.uns.sw.sct.util.AuthorityRoles).VERIFIER)")
    @PostMapping("/marks")
    public ResponseEntity<Mark> createMark(@Valid @RequestBody Mark mark) throws URISyntaxException {
        if (mark.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(HeaderUtil.MARK, "id_exists", "A new mark cannot already have an ID")).body(null);
        }
        Mark result = markService.save(mark);
        return ResponseEntity.created(new URI("/api/marks/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(HeaderUtil.MARK, result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /marks : Updates an existing mark.
     *
     * @param mark the mark to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated mark,
     * or with status 400 (Bad Request) if the mark is not valid,
     * or with status 500 (Internal Server Error) if the mark couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PreAuthorize("hasAnyAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN, T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER, T(rs.acs.uns.sw.sct.util.AuthorityRoles).VERIFIER)")
    @PutMapping("/marks")
    public ResponseEntity<Mark> updateMark(@Valid @RequestBody Mark mark) throws URISyntaxException {
        if (mark.getId() == null) {
            return createMark(mark);
        }
        Mark result = markService.save(mark);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(HeaderUtil.MARK, mark.getId().toString()))
                .body(result);
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
    public ResponseEntity<List<Mark>> getAllMarks(Pageable pageable)
            throws URISyntaxException {
        Page<Mark> page = markService.findAll(pageable);
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
    public ResponseEntity<Mark> getMark(@PathVariable Long id) {
        Mark mark = markService.findOne(id);
        return Optional.ofNullable(mark)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /marks/announcement/:announcementId : get all marks for one announcement.
     *
     * @param announcementId the id of the announcement
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of marks in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/marks/announcement/{announcementId}")
    public ResponseEntity<List<Mark>> getAllCommentsByAnnouncementId(@PathVariable Long announcementId, Pageable pageable)
            throws URISyntaxException {
        Page<Mark> page = markService.findAllByAnnouncement(announcementId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/marks/announcement");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * DELETE  /marks/:id : delete the "id" mark.
     *
     * @param id the id of the mark to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @PreAuthorize("hasAnyAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN, T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER, T(rs.acs.uns.sw.sct.util.AuthorityRoles).VERIFIER)")
    @DeleteMapping("/marks/{id}")
    public ResponseEntity<Void> deleteMark(@PathVariable Long id) {
        markService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(HeaderUtil.MARK, id.toString())).build();
    }

}
