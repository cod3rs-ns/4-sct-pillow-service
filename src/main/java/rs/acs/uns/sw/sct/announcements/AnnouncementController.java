package rs.acs.uns.sw.sct.announcements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.acs.uns.sw.sct.util.HeaderUtil;
import rs.acs.uns.sw.sct.util.PaginationUtil;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Announcement.
 */
@RestController
@RequestMapping("/api")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    /**
     * POST  /announcements : Create a new announcement.
     *
     * @param announcement the announcement to create
     * @return the ResponseEntity with status 201 (Created) and with body the new announcement, or with status 400 (Bad Request) if the announcement has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/announcements")
    public ResponseEntity<Announcement> createAnnouncement(@Valid @RequestBody Announcement announcement) throws URISyntaxException {
        if (announcement.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(HeaderUtil.ANNOUNCEMENT, "id_exists", "A new announcement cannot already have an ID")).body(null);
        }
        Announcement result = announcementService.save(announcement);
        return ResponseEntity.created(new URI("/api/announcements/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(HeaderUtil.ANNOUNCEMENT, result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /announcements : Updates an existing announcement.
     *
     * @param announcement the announcement to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated announcement,
     * or with status 400 (Bad Request) if the announcement is not valid,
     * or with status 500 (Internal Server Error) if the announcement couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/announcements")
    public ResponseEntity<Announcement> updateAnnouncement(@Valid @RequestBody Announcement announcement) throws URISyntaxException {
        if (announcement.getId() == null) {
            return createAnnouncement(announcement);
        }
        Announcement result = announcementService.save(announcement);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(HeaderUtil.ANNOUNCEMENT, announcement.getId().toString()))
                .body(result);
    }

    /**
     * GET  /announcements : get all the announcements.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of announcements in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/announcements")
    public ResponseEntity<List<Announcement>> getAllAnnouncements(Pageable pageable)
            throws URISyntaxException {
        Page<Announcement> page = announcementService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/announcements");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /announcements/:id : get the "id" announcement.
     *
     * @param id the id of the announcement to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the announcement, or with status 404 (Not Found)
     */
    @GetMapping("/announcements/{id}")
    public ResponseEntity<Announcement> getAnnouncement(@PathVariable Long id) {
        Announcement announcement = announcementService.findOne(id);
        return Optional.ofNullable(announcement)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /announcements/:id : delete the "id" announcement.
     *
     * @param id the id of the announcement to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/announcements/{id}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(HeaderUtil.ANNOUNCEMENT, id.toString())).build();
    }

}
