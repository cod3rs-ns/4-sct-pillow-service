package rs.acs.uns.sw.sct.announcements;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rs.acs.uns.sw.sct.users.User;
import rs.acs.uns.sw.sct.users.UserService;
import rs.acs.uns.sw.sct.util.Constants;
import rs.acs.uns.sw.sct.util.HeaderUtil;
import rs.acs.uns.sw.sct.util.PaginationUtil;

import javax.validation.Valid;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * REST controller for managing Announcement.
 */
@RestController
@RequestMapping("/api")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private UserService userService;

    @Autowired
    private SimpleDateFormat dateFormatter;

    /**
     * POST  /announcements : Create a new announcement.
     *
     * @param annDTO the announcement to create
     * @return the ResponseEntity with status 201 (Created) and with body the new announcement, or with status 400 (Bad Request) if the announcement has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/announcements")
    public ResponseEntity<Announcement> createAnnouncement(@Valid @RequestBody AnnouncementDTO annDTO) throws URISyntaxException {
        if (annDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(HeaderUtil.ANNOUNCEMENT, "id_exists", "A new announcement cannot already have an ID")).body(null);
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User user = userService.getUserByUsername(auth.getName());
        Announcement announcement = annDTO.convertToAnnouncement(user);

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
            return createAnnouncement(announcement.convertToDTO());
        }
        Announcement result = announcementService.save(announcement);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(HeaderUtil.ANNOUNCEMENT, announcement.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /announcements/{id} : Extend the expiration date.
     *
     * @param id the announcement to update
     * @param data the data send in RequestBody
     * @return the ResponseEntity with status 200 (OK) and with body the updated announcement,
     * or with status 400 (Bad Request) if the announcement doesn't contain expirationDate attribute or date have wrong format or date is before today
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/announcements/{id}")
    public ResponseEntity<?> extendExpirationDate(@PathVariable Long id, @RequestBody Map<String, String> data) throws URISyntaxException {
        if (id == null || !data.containsKey("expirationDate"))
            return new ResponseEntity<>("Object must contain expirationDate attribute", HttpStatus.BAD_REQUEST);

        Announcement persistedAnnouncement = announcementService.findOne(id);
        if (persistedAnnouncement == null)
            return new ResponseEntity<>("Announcement not found", HttpStatus.NOT_FOUND);

        String strDate = data.get("expirationDate");
        Date extendedDate;
        try {
            extendedDate = dateFormatter.parse(strDate);
        } catch (ParseException e) {
            return new ResponseEntity<>("Date must be in format dd/MM/yyyy", HttpStatus.BAD_REQUEST);
        }

        Date modified = new Date();
        if (extendedDate.before(modified))
            return new ResponseEntity<>("Modified date must be after today", HttpStatus.BAD_REQUEST);

        persistedAnnouncement.expirationDate(extendedDate)
                .dateModified(modified);

        Announcement result = announcementService.save(persistedAnnouncement);

        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(HeaderUtil.ANNOUNCEMENT, persistedAnnouncement.getId().toString()))
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
     * GET  /announcements/company/:companyId : get all the announcements created by users of the same company.
     *
     * @param companyId the id of the company
     * @param pageable  the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of announcements in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/announcements/company/{companyId}")
    public ResponseEntity<List<Announcement>> getAllAnnouncementsByCompanyId(@PathVariable Long companyId, Pageable pageable)
            throws URISyntaxException {
        Page<Announcement> page = announcementService.findAllByCompany(companyId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/announcements/company");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /announcements/top/company/:companyId : get top three announcements created by users of the same company.
     *
     * @param companyId the id of the company
     * @return the ResponseEntity with status 200 (OK) and the list of announcements in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/announcements/top/company/{companyId}")
    public ResponseEntity<List<Announcement>> getTopAnnouncementsByCompanyId(@PathVariable Long companyId)
            throws URISyntaxException {
        List<Announcement> list = announcementService.findTopByCompany(companyId);
        return new ResponseEntity<>(list, HttpStatus.OK);
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

    /**
     * POST  /announcements/:id : upload file for announcement.
     *
     * @param file the file to be upload
     * @return the ResponseEntity with status 201 (Created) and with body the new file name, or with status 400 (Bad Request) if the upload failed, or with status 204 (No content)
     */
    @PostMapping("/announcements/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                String originalFileName = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
                String originalFileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                String newFilename = originalFileName + UUID.randomUUID().toString() + originalFileExtension;

                // transfer to upload folder
                File dir = new File(Constants.FilePaths.BASE + File.separator + Constants.FilePaths.ANNOUNCEMENTS + File.separator);
                if (!dir.exists())
                    dir.mkdirs();
                File newFile = new File(dir + File.separator + newFilename);
                file.transferTo(newFile);

                return new ResponseEntity<>(newFilename, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
