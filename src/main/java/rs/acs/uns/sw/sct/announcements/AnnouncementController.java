package rs.acs.uns.sw.sct.announcements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rs.acs.uns.sw.sct.search.AnnouncementSearchWrapper;
import rs.acs.uns.sw.sct.security.UserSecurityUtil;
import rs.acs.uns.sw.sct.users.User;
import rs.acs.uns.sw.sct.users.UserService;
import rs.acs.uns.sw.sct.util.AuthorityRoles;
import rs.acs.uns.sw.sct.util.Constants;
import rs.acs.uns.sw.sct.util.HeaderUtil;
import rs.acs.uns.sw.sct.util.PaginationUtil;

import javax.validation.Valid;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * REST controller for managing Announcement.
 */
@RestController
@RequestMapping("/api")
@SuppressWarnings("unused")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private UserService userService;

    @Autowired
    private SimpleDateFormat dateFormatter;

    @Autowired
    private UserSecurityUtil userSecurityUtil;

    /**
     * POST  /announcements : Create a new announcement.
     *
     * @param annDTO the announcement to create
     * @return the ResponseEntity with status 201 (Created) and with body the new announcement,
     * or with status 400 (Bad Request) if the announcement already has an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PreAuthorize("hasAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER)")
    @PostMapping("/announcements")
    public ResponseEntity<Announcement> createAnnouncement(@Valid @RequestBody AnnouncementDTO annDTO) throws URISyntaxException {
        if (annDTO.getId() != null) {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.createFailureAlert(
                            Constants.EntityNames.ANNOUNCEMENT,
                            HeaderUtil.ERROR_CODE_CUSTOM_ID,
                            HeaderUtil.ERROR_MSG_CUSTOM_ID))
                    .body(null);
        }

        final User user = userSecurityUtil.getLoggedUser();
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Announcement announcement = annDTO.convertToAnnouncement(user);

        Announcement result = announcementService.save(announcement);
        return ResponseEntity
                .created(new URI("/api/announcements/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(
                        Constants.EntityNames.ANNOUNCEMENT,
                        result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /announcements : Updates an existing announcement.
     *
     * @param announcement the announcement to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated announcement,
     * or with status 400 (Bad Request) if the announcement is not valid,
     * or with status 500 (Internal Server Error) if the announcement couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PreAuthorize("hasAnyAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN, T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER)")
    @PutMapping("/announcements")
    public ResponseEntity<Announcement> updateAnnouncement(@Valid @RequestBody Announcement announcement) throws URISyntaxException {
        if (announcement.getId() == null) {
            return createAnnouncement(announcement.convertToDTO());
        }

        // check if user has no rights to update
        // if current use of type Advertiser is not the author of the announcement
        String authorUsername = announcementService.findOne(announcement.getId()).getAuthor().getUsername();
        if (!userSecurityUtil.checkPermission(authorUsername, AuthorityRoles.ADVERTISER)) {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.createFailureAlert(
                            Constants.EntityNames.ANNOUNCEMENT,
                            HeaderUtil.ERROR_CODE_NOT_OWNER,
                            HeaderUtil.ERROR_MSG_NOT_OWNER))
                    .body(null);
        }

        Announcement result = announcementService.save(announcement);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(
                        Constants.EntityNames.ANNOUNCEMENT,
                        announcement.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /announcements/{id} : Extend the expiration date.
     *
     * @param id   the announcement to update
     * @param data the data send in RequestBody
     * @return the ResponseEntity with status 200 (OK) and with body the updated announcement,
     * or with status 400 (Bad Request) if the announcement doesn't contain expirationDate
     * attribute or date have wrong format or date is before today
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PreAuthorize("hasAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER)")
    @PutMapping("/announcements/{id}")
    public ResponseEntity<Announcement> extendExpirationDate(@PathVariable Long id, @RequestBody Map<String, String> data) throws URISyntaxException {
        if (id == null || !data.containsKey("expirationDate"))
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.createFailureAlert(
                            Constants.EntityNames.ANNOUNCEMENT,
                            HeaderUtil.ERROR_CODE_NO_EXPIRATION_DATE,
                            HeaderUtil.ERROR_MSG_NO_EXPIRATION_DATE))
                    .body(null);

        Announcement persistedAnnouncement = announcementService.findOne(id);
        if (persistedAnnouncement == null)
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .headers(HeaderUtil.createFailureAlert(
                            Constants.EntityNames.ANNOUNCEMENT,
                            HeaderUtil.ERROR_CODE_NON_EXISTING_ENTITY,
                            HeaderUtil.ERROR_MSG_NON_EXISTING_ENTITY))
                    .body(null);

        String strDate = data.get("expirationDate");
        Date extendedDate;
        try {
            extendedDate = dateFormatter.parse(strDate);
        } catch (ParseException e) {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.createFailureAlert(
                            Constants.EntityNames.ANNOUNCEMENT,
                            HeaderUtil.ERROR_CODE_INVALID_DATE_FORMAT,
                            HeaderUtil.ERROR_MSG_INVALID_DATE_FORMAT))
                    .body(null);
        }

        Date modified = new Date();
        if (extendedDate.before(modified))
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.createFailureAlert(
                            Constants.EntityNames.ANNOUNCEMENT,
                            HeaderUtil.ERROR_CODE_PAST_DATE,
                            HeaderUtil.ERROR_MSG_PAST_DATE))
                    .body(null);

        // check if user has no rights to update
        // if current use of type Advertiser is not the author of the announcement
        String authorUsername = announcementService.findOne(id).getAuthor().getUsername();
        if (!userSecurityUtil.checkPermission(authorUsername, AuthorityRoles.ADVERTISER)) {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.createFailureAlert(
                            Constants.EntityNames.ANNOUNCEMENT,
                            HeaderUtil.ERROR_CODE_NOT_OWNER,
                            HeaderUtil.ERROR_MSG_NOT_OWNER))
                    .body(null);
        }

        persistedAnnouncement.expirationDate(extendedDate)
                .dateModified(modified);

        Announcement result = announcementService.save(persistedAnnouncement);

        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(
                        Constants.EntityNames.ANNOUNCEMENT,
                        persistedAnnouncement.getId().toString()))
                .body(result);
    }

    /**
     * GET  /announcements : get all the announcements.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of announcements in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/announcements")
    public ResponseEntity<List<Announcement>> getAllAnnouncements(Pageable pageable)
            throws URISyntaxException {
        Page<Announcement> page = announcementService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/announcements");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /announcements/deleted/:status : get all the announcements by status - deleted or not.
     *
     * @param status   deleted or not deleted
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of announcements in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/announcements/deleted/{status}")
    public ResponseEntity<List<Announcement>> getAllAnnouncementsByStatus(Pageable pageable, @PathVariable Boolean status)
            throws URISyntaxException {
        Page<Announcement> page = announcementService.findAllByStatus(status, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/announcements/deleted");
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
    @PreAuthorize("permitAll()")
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
    @PreAuthorize("permitAll()")
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
    @PreAuthorize("permitAll()")
    @GetMapping("/announcements/{id}")
    public ResponseEntity<Announcement> getAnnouncement(@PathVariable Long id) {
        final Announcement announcement = announcementService.findOne(id);
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
    @PreAuthorize("hasAnyAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN, T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER)")
    @DeleteMapping("/announcements/{id}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long id) {
        if (announcementService.findOne(id) != null) {

            // check if user has no rights to delete
            // if current use of type Advertiser is not the author of the announcement
            String authorUsername = announcementService.findOne(id).getAuthor().getUsername();
            if (!userSecurityUtil.checkPermission(authorUsername, AuthorityRoles.ADVERTISER)) {
                return ResponseEntity
                        .badRequest()
                        .headers(HeaderUtil.createFailureAlert(
                                Constants.EntityNames.ANNOUNCEMENT,
                                HeaderUtil.ERROR_CODE_NOT_OWNER,
                                HeaderUtil.ERROR_MSG_NOT_OWNER))
                        .body(null);
            }

            announcementService.delete(id);

            return ResponseEntity
                    .ok()
                    .headers(HeaderUtil.createEntityDeletionAlert(Constants.EntityNames.ANNOUNCEMENT, id.toString()))
                    .build();
        } else {
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    /**
     * POST  /announcements/:id : upload file for announcement.
     *
     * @param file the file to be upload
     * @return the ResponseEntity with status 201 (Created) and with body the new file name,
     * or with status 400 (Bad Request) if the upload failed, or with status 204 (No content)
     */
    @PreAuthorize("hasAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER)")
    @PostMapping("/announcements/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                String originalFileName = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf('.'));
                String originalFileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'));
                String newFilename = originalFileName + UUID.randomUUID().toString() + originalFileExtension;

                // transfer to upload folder
                File dir = new File(Constants.FilePaths.BASE + File.separator + Constants.FilePaths.ANNOUNCEMENTS + File.separator);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File newFile = new File(dir + File.separator + newFilename);
                file.transferTo(newFile);

                return new ResponseEntity<>(newFilename, HttpStatus.OK);
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.INFO, "Unable to create folders.", e);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    /**
     * PUT  /announcements/:announcementId/verify : Updates an existing announcement.
     *
     * @param announcementId the announcement to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated announcement,
     * or with status 400 (Bad Request) if the announcement is not valid,
     * or with status 500 (Internal Server Error) if the announcement couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PreAuthorize("hasAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).VERIFIER)")
    @PutMapping("/announcements/{announcementId}/verify")
    public ResponseEntity<Announcement> verifyAnnouncement(@PathVariable Long announcementId) throws URISyntaxException {
        Announcement announcement = announcementService.findOne(announcementId);
        if (announcement == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .headers(HeaderUtil.createFailureAlert(
                            Constants.EntityNames.ANNOUNCEMENT,
                            HeaderUtil.ERROR_CODE_NON_EXISTING_ENTITY,
                            HeaderUtil.ERROR_MSG_NON_EXISTING_ENTITY
                    ))
                    .body(null);
        }

        if (announcement.getVerified().equals(Constants.VerifiedStatuses.VERIFIED)) {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.createFailureAlert(
                            Constants.EntityNames.ANNOUNCEMENT,
                            HeaderUtil.ERROR_CODE_ALREADY_VERIFIED,
                            HeaderUtil.ERROR_MSG_ALREADY_VERIFIED
                    ))
                    .body(null);
        }

        announcement.setVerified(Constants.VerifiedStatuses.VERIFIED);

        Announcement result = announcementService.save(announcement);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(
                        Constants.EntityNames.ANNOUNCEMENT,
                        announcement.getId().toString()))
                .body(result);
    }


    /**
     * GET  /announcements/search : get all the announcements that satisfied search params.
     *
     * @param startPrice    low limit of announcement price requirements
     * @param endPrice      top limit of announcement price requirements
     * @param phoneNumber   phone number of the Announcer
     * @param type          type of announcement
     * @param authorName    first name of the Announcer
     * @param authorSurname last name of the Announcer
     * @param startArea     low limit of real estate square area
     * @param endArea       tip limit of real estate square area
     * @param heatingType   type of heating in real estate
     * @param name          name of the announcement
     * @param country       country where real estate is located
     * @param cityRegion    city region where real estate is located
     * @param city          city where real estate is located
     * @param street        street where real estate is located
     * @param streetNumber  street number of building where is real estate
     * @param pageable      the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of announcements in body
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/announcements/search")
    public ResponseEntity<List<Announcement>> search(@RequestParam(value = "startPrice", required = false) Double startPrice,
                                                     @RequestParam(value = "endPrice", required = false) Double endPrice,
                                                     @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
                                                     @RequestParam(value = "type", required = false) String type,
                                                     @RequestParam(value = "authorName", required = false) String authorName,
                                                     @RequestParam(value = "authorSurname", required = false) String authorSurname,
                                                     @RequestParam(value = "startArea", required = false) Double startArea,
                                                     @RequestParam(value = "endArea", required = false) Double endArea,
                                                     @RequestParam(value = "heatingType", required = false) String heatingType,
                                                     @RequestParam(value = "name", required = false) String name,
                                                     @RequestParam(value = "country", required = false) String country,
                                                     @RequestParam(value = "cityRegion", required = false) String cityRegion,
                                                     @RequestParam(value = "city", required = false) String city,
                                                     @RequestParam(value = "street", required = false) String street,
                                                     @RequestParam(value = "streetNumber", required = false) String streetNumber,
                                                     Pageable pageable) {

        AnnouncementSearchWrapper wrap = new AnnouncementSearchWrapper()
                .startPrice(startPrice).endPrice(endPrice)
                .phoneNumber(phoneNumber).type(type)
                .authorName(authorName).authorSurname(authorSurname)
                .startArea(startArea).endArea(endArea)
                .heatingType(heatingType).name(name)
                .country(country).cityRegion(cityRegion)
                .city(city).street(street)
                .streetNumber(streetNumber);

        List<Announcement> list = announcementService.findBySearchTerm(wrap, pageable);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
