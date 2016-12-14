package rs.acs.uns.sw.sct.realestates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.acs.uns.sw.sct.util.Constants;
import rs.acs.uns.sw.sct.util.HeaderUtil;
import rs.acs.uns.sw.sct.util.PaginationUtil;

import javax.validation.Valid;
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

    /**
     * POST  /real-estates : Create a new realEstate.
     *
     * @param realEstate the realEstate to create
     * @return the ResponseEntity with status 201 (Created) and with body the new realEstate, or with status 400 (Bad Request) if the realEstate has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PreAuthorize("hasAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER)")
    @PostMapping("/real-estates")
    public ResponseEntity<RealEstate> createRealEstate(@Valid @RequestBody RealEstate realEstate) throws URISyntaxException {
        if (realEstate.getId() != null) {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.createFailureAlert(Constants.EntityNames.REAL_ESTATE, HeaderUtil.ERROR_CODE_CUSTOM_ID, HeaderUtil.ERROR_MSG_CUSTOM_ID))
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
     * @param status deleted or not deleted
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of realEstates in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/real-estates/deleted/{status}")
    @PreAuthorize("hasAnyAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN, T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER, T(rs.acs.uns.sw.sct.util.AuthorityRoles).VERIFIER)")
    public ResponseEntity<List<RealEstate>> getAllRealEstatesByStatus(@PathVariable Boolean status, Pageable pageable)
            throws URISyntaxException {
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

}
