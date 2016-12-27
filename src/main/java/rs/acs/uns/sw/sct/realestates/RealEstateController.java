package rs.acs.uns.sw.sct.realestates;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.acs.uns.sw.sct.announcements.Announcement;
import rs.acs.uns.sw.sct.announcements.Image;
import rs.acs.uns.sw.sct.security.UserSecurityUtil;
import rs.acs.uns.sw.sct.util.AuthorityRoles;
import rs.acs.uns.sw.sct.util.Constants;
import rs.acs.uns.sw.sct.util.HeaderUtil;
import rs.acs.uns.sw.sct.util.PaginationUtil;

import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing RealEstate.
 */
@RestController
@RequestMapping("/api")
@SuppressWarnings("unused")
public class RealEstateController {

    @Autowired
    private RealEstateService realEstateService;

    @Autowired
    private UserSecurityUtil userSecurityUtil;

    @Value("${sct.file_upload.path}")
    private String uploadPath;

    /**
     * POST  /real-estates : Create a new realEstate.
     *
     * @param realEstate the realEstate to create
     * @return the ResponseEntity with status 201 (Created) and with body the new realEstate,
     * or with status 400 (Bad Request) if the realEstate has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PreAuthorize("hasAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER)")
    @PostMapping("/real-estates")
    public ResponseEntity<RealEstate> createRealEstate(@Valid @RequestBody RealEstate realEstate) throws URISyntaxException {
        if (realEstate.getId() != null) {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.failure(
                            Constants.EntityNames.REAL_ESTATE,
                            HeaderUtil.ERROR_CODE_CUSTOM_ID,
                            HeaderUtil.ERROR_MSG_CUSTOM_ID))
                    .body(null);
        }

        RealEstate result = realEstateService.save(realEstate);
        return ResponseEntity.created(new URI("/api/real-estates/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(Constants.EntityNames.REAL_ESTATE, result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /real-estates : Updates an existing realEstate.
     *
     * @param realEstate the realEstate to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated realEstate,
     * or with status 400 (Bad Request) if the realEstate is not valid,
     * or with status 500 (Internal Server Error) if the realEstate couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PreAuthorize("hasAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER)")
    @PutMapping("/real-estates")
    public ResponseEntity<RealEstate> updateRealEstate(@Valid @RequestBody RealEstate realEstate) throws URISyntaxException {
        // TODO 4 - this feature need further discussion
        if (realEstate.getId() == null) {
            return createRealEstate(realEstate);
        }
        RealEstate result = realEstateService.save(realEstate);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(Constants.EntityNames.REAL_ESTATE, realEstate.getId().toString()))
                .body(result);
    }

    /**
     * GET  /real-estates : get all the realEstates.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of realEstates in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @PreAuthorize("hasAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN)")
    @GetMapping("/real-estates")
    public ResponseEntity<List<RealEstate>> getAllRealEstates(Pageable pageable)
            throws URISyntaxException {
        Page<RealEstate> page = realEstateService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/real-estates");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /real-estates/deleted/:status : get all the realEstates.
     *
     * @param status   deleted or not deleted
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of realEstates in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/real-estates/deleted/{status}")
    @PreAuthorize("hasAnyAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN, T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER, T(rs.acs.uns.sw.sct.util.AuthorityRoles).VERIFIER)")
    public ResponseEntity<List<RealEstate>> getAllRealEstatesByStatus(@PathVariable Boolean status, Pageable pageable)
            throws URISyntaxException {

        // If User is not ADMIN and want to get DELETED real estates
        if (!userSecurityUtil.checkAuthType(AuthorityRoles.ADMIN) && status)
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);

        Page<RealEstate> page = realEstateService.findAllByStatus(status, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/real-estates/deleted");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /real-estates/:id : get the "id" realEstate.
     *
     * @param id the id of the realEstate to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the realEstate, or with status 404 (Not Found)
     */
    @PreAuthorize("hasAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN)")
    @GetMapping("/real-estates/{id}")
    public ResponseEntity<RealEstate> getRealEstate(@PathVariable Long id) {
        RealEstate realEstate = realEstateService.findOne(id);
        return Optional.ofNullable(realEstate)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /real-estates/similar
     *
     * @param area     Real estate area
     * @param country  Real estate Location country
     * @param city     Real estate Location city
     * @param region   Real estate Location region
     * @param street   Real estate Location street
     * @param number   Real estate Location number
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of all similar real estates in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/real-estates/similar")
    @PreAuthorize("hasAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER)")
    public ResponseEntity<List<RealEstate>> getSimilarRealEstates(@RequestParam(value = "area") Double area,
                                                                  @RequestParam(value = "country") String country,
                                                                  @RequestParam(value = "city") String city,
                                                                  @RequestParam(value = "region") String region,
                                                                  @RequestParam(value = "street") String street,
                                                                  @RequestParam(value = "number") String number,
                                                                  Pageable pageable)
            throws URISyntaxException {

        Location location = new Location()
                .country(country)
                .city(city)
                .cityRegion(region)
                .street(street)
                .streetNumber(number);

        RealEstateSimilarDTO realEstate = new RealEstateSimilarDTO(location, area);
        Page<RealEstate> page = realEstateService.findAllSimilar(realEstate, pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/real-estates/similar");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * DELETE  /real-estates/:id : delete the "id" realEstate.
     *
     * @param id the id of the realEstate to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @PreAuthorize("hasAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN)")
    @DeleteMapping("/real-estates/{id}")
    public ResponseEntity<Void> deleteRealEstate(@PathVariable Long id) {
        realEstateService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(Constants.EntityNames.REAL_ESTATE, id.toString())).build();
    }

    /**
     * GET  /real-estates/:id/image
     *
     * @param id Id of real estate
     * @return the ResponseEntity with status 200 (OK) and the image path
     * @throws IOException if there is an error to read image from path
     */
    @RequestMapping("/real-estates/{id}/image")
    public ResponseEntity<byte[]> realEstateImage(@PathVariable Long id) throws IOException {
        RealEstate realEstate = realEstateService.findOne(id);

        if (realEstate == null)
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .headers(HeaderUtil.failure(
                            Constants.EntityNames.REAL_ESTATE,
                            HeaderUtil.ERROR_CODE_NON_EXISTING_ENTITY,
                            HeaderUtil.ERROR_MSG_NON_EXISTING_ENTITY))
                    .body(null);

        Announcement ann = realEstate.getAnnouncements().iterator().next();
        Image image = ann.getImages().iterator().next();

        File f = new File(uploadPath + File.separator + image.getImagePath());
        if (!f.exists()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(null);
        }

        InputStream in = new FileInputStream(f);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        byte[] result = IOUtils.toByteArray(in);
        in.close();

        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }
}
