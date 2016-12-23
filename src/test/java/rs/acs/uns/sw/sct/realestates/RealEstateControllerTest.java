package rs.acs.uns.sw.sct.realestates;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import rs.acs.uns.sw.sct.SctServiceApplication;
import rs.acs.uns.sw.sct.constants.RealEstateConstants;
import rs.acs.uns.sw.sct.util.AuthorityRoles;
import rs.acs.uns.sw.sct.util.TestUtil;

import javax.annotation.PostConstruct;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the RealEstate REST controller.
 *
 * @see RealEstateController
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SctServiceApplication.class)
public class RealEstateControllerTest {

    private static final String DEFAULT_NAME = "NAME_AAA";
    private static final String UPDATED_NAME = "NAME_BBB";

    private static final String DEFAULT_TYPE = "TYPE_AAA";
    private static final String UPDATED_TYPE = "TYPE_BBB";

    private static final Double DEFAULT_AREA = 1D;
    private static final Double UPDATED_AREA = 2D;

    private static final String DEFAULT_HEATING_TYPE = "HEATING_AAA";
    private static final String UPDATED_HEATING_TYPE = "HEATING_BBB";

    private static final Boolean DEFAULT_DELETED = false;
    private static final Boolean UPDATED_DELETED = true;

    @Autowired
    private RealEstateRepository realEstateRepository;

    @Autowired
    private RealEstateService realEstateService;

    @Autowired
    private WebApplicationContext context;

    private MockMvc restRealEstateMockMvc;

    private RealEstate realEstate;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RealEstate createEntity() {
        return new RealEstate()
                .name(DEFAULT_NAME)
                .type(DEFAULT_TYPE)
                .area(DEFAULT_AREA)
                .heatingType(DEFAULT_HEATING_TYPE)
                .deleted(DEFAULT_DELETED);
    }

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RealEstateController realEstateCtrl = new RealEstateController();
        ReflectionTestUtils.setField(realEstateCtrl, "realEstateService", realEstateService);
        this.restRealEstateMockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    /**
     * Initializes all objects needed for further testing.
     * <p>
     * This method is called before testing starts.
     */
    @Before
    public void initTest() {
        realEstate = createEntity();
    }

    /**
     * Tests addition of RealEstate objects as an Advertiser.
     * <p>
     * This test uses a mock Advertiser user to add a default RealEstate
     * object to the database using a POST method.
     * It then proceeds to check whether the RealEstate object was added successfully,
     * by comparing the number of objects in the database before and after the addition,
     * as well as the default RealEstate's attributes to the RealEstate in the database.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void createRealEstateAsAdvertiser() throws Exception {
        int databaseSizeBeforeCreate = realEstateRepository.findAll().size();

        // Create the RealEstate

        restRealEstateMockMvc.perform(post("/api/real-estates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(realEstate)))
                .andExpect(status().isCreated());

        // Validate the RealEstate in the database
        List<RealEstate> realEstates = realEstateRepository.findAll();
        assertThat(realEstates).hasSize(databaseSizeBeforeCreate + 1);
        RealEstate testRealEstate = realEstates.get(realEstates.size() - 1);
        assertThat(testRealEstate.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRealEstate.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testRealEstate.getArea()).isEqualTo(DEFAULT_AREA);
        assertThat(testRealEstate.getHeatingType()).isEqualTo(DEFAULT_HEATING_TYPE);
        assertThat(testRealEstate.isDeleted()).isEqualTo(DEFAULT_DELETED);
    }

    /**
     * Tests addition of RealEstate objects as an Admin
     * <p>
     * This test uses a mock Admin user to
     * add a default RealEstate object to the database using a POST method, which
     * is forbidden. It then asserts that the number of objects in the database
     * has not changed.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN)
    public void createRealEstateAsAdmin() throws Exception {
        int databaseSizeBeforeCreate = realEstateRepository.findAll().size();

        restRealEstateMockMvc.perform(post("/api/real-estates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(realEstate)))
                .andExpect(status().isForbidden());

        // Validate the RealEstate in the database
        List<RealEstate> realEstates = realEstateRepository.findAll();
        assertThat(realEstates).hasSize(databaseSizeBeforeCreate);
    }

    /**
     * Tests addition of RealEstate objects as a Guest
     * <p>
     * This test uses a mock Guest user to
     * add a default RealEstate object to the database using a POST method, for which
     * this user is unauthorized. It then asserts that the number of objects in the
     * database has not changed.
     * @throws Exception
     */
    @Test
    @Transactional
    public void createRealEstateAsGuest() throws Exception {
        int databaseSizeBeforeCreate = realEstateRepository.findAll().size();

        restRealEstateMockMvc.perform(post("/api/real-estates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(realEstate)))
                .andExpect(status().isUnauthorized());

        // Validate the RealEstate in the database
        List<RealEstate> realEstates = realEstateRepository.findAll();
        assertThat(realEstates).hasSize(databaseSizeBeforeCreate);
    }

    /**
     * Tests whether the "Name" field is nullable
     * <p>
     * This test attempts to add a RealEstate object with a null "Name" value to the database,
     * this is forbidden as the "Name" field is non-nullable. Other than expecting a "Bad request" status,
     * the test asserts that the number of objects in database has not changed.
     * @throws Exception
     */
    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = realEstateRepository.findAll().size();
        // set the field null
        realEstate.setName(null);

        // Create the RealEstate, which fails.

        restRealEstateMockMvc.perform(post("/api/real-estates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(realEstate)))
                .andExpect(status().isBadRequest());

        List<RealEstate> realEstates = realEstateRepository.findAll();
        assertThat(realEstates).hasSize(databaseSizeBeforeTest);
    }

    /**
     * Tests whether the "Type" field is nullable
     * <p>
     * This test attempts to add a RealEstate object with a null "Type" value to the database,
     * this is forbidden as the "Type" field is non-nullable. Other than expecting a "Bad request" status,
     * the test asserts that the number of objects in database has not changed.
     * @throws Exception
     */
    @Test
    @Transactional
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = realEstateRepository.findAll().size();
        // set the field null
        realEstate.setType(null);

        // Create the RealEstate, which fails.

        restRealEstateMockMvc.perform(post("/api/real-estates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(realEstate)))
                .andExpect(status().isBadRequest());

        List<RealEstate> realEstates = realEstateRepository.findAll();
        assertThat(realEstates).hasSize(databaseSizeBeforeTest);
    }

    /**
     * Tests whether the "Area" field is nullable
     * <p>
     * This test attempts to add a RealEstate object with a null "Area" value to the database,
     * this is forbidden as the "Area" field is non-nullable. Other than expecting a "Bad request" status,
     * the test asserts that the number of objects in database has not changed.
     * @throws Exception
     */
    @Test
    @Transactional
    public void checkAreaIsRequired() throws Exception {
        int databaseSizeBeforeTest = realEstateRepository.findAll().size();
        // set the field null
        realEstate.setArea(null);

        // Create the RealEstate, which fails.

        restRealEstateMockMvc.perform(post("/api/real-estates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(realEstate)))
                .andExpect(status().isBadRequest());

        List<RealEstate> realEstates = realEstateRepository.findAll();
        assertThat(realEstates).hasSize(databaseSizeBeforeTest);
    }

    /**
     * Tests whether the "HeatingType" field is nullable
     * <p>
     * This test attempts to add a RealEstate object with a null "HeatingType" value to the database,
     * this is forbidden as the "HeatingType" field is non-nullable. Other than expecting a "Bad request" status,
     * the test asserts that the number of objects in database has not changed.
     * @throws Exception
     */
    @Test
    @Transactional
    public void checkHeatingTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = realEstateRepository.findAll().size();
        // set the field null
        realEstate.setHeatingType(null);

        // Create the RealEstate, which fails.

        restRealEstateMockMvc.perform(post("/api/real-estates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(realEstate)))
                .andExpect(status().isBadRequest());

        List<RealEstate> realEstates = realEstateRepository.findAll();
        assertThat(realEstates).hasSize(databaseSizeBeforeTest);
    }

    /**
     * Tests whether the "Deleted" field is nullable
     * <p>
     * This test attempts to add a RealEstate object with a null "Deleted" value to the database,
     * this is forbidden as the "Deleted" field is non-nullable. Other than expecting a "Bad request" status,
     * the test asserts that the number of objects in database has not changed.
     * @throws Exception
     */
    @Test
    @Transactional
    public void checkDeletedIsRequired() throws Exception {
        int databaseSizeBeforeTest = realEstateRepository.findAll().size();
        // set the field null
        realEstate.setDeleted(null);

        // Create the RealEstate, which fails.

        restRealEstateMockMvc.perform(post("/api/real-estates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(realEstate)))
                .andExpect(status().isBadRequest());

        List<RealEstate> realEstates = realEstateRepository.findAll();
        assertThat(realEstates).hasSize(databaseSizeBeforeTest);
    }

    /**
     * Tests getting all RealEstates as an Admin
     * <p>
     * This test uses a mocked Admin user to request all RealEstates
     * from the database. It then asserts that the received results
     * match what was expected.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN)
    public void getAllRealEstatesAsAdmin() throws Exception {
        // Initialize the database
        realEstateRepository.saveAndFlush(realEstate);

        // Get all the realEstates
        restRealEstateMockMvc.perform(get("/api/real-estates?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(realEstate.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
                .andExpect(jsonPath("$.[*].area").value(hasItem(DEFAULT_AREA)))
                .andExpect(jsonPath("$.[*].heatingType").value(hasItem(DEFAULT_HEATING_TYPE)))
                .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED)));
    }

    /**
     * Tests getting all RealEstates as an Advertiser
     * <p>
     * This test uses a mocked Advertiser user to request all RealEstates
     * from the database, which fails and returns a Forbidden status.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void getAllRealEstatesAsAdvertiser() throws Exception {
        // Initialize the database
        realEstateRepository.saveAndFlush(realEstate);

        // Get all the realEstates
        restRealEstateMockMvc.perform(get("/api/real-estates?sort=id,desc"))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests getting all RealEstates as a Guest
     * <p>
     * This test uses a mocked Guest user to request all RealEstates
     * from the database, which fails and returns an Unauthorized status.
     * @throws Exception
     */
    @Test
    @Transactional
    public void getAllRealEstatesAsGuest() throws Exception {
        // Initialize the database
        realEstateRepository.saveAndFlush(realEstate);

        // Get all the realEstates
        restRealEstateMockMvc.perform(get("/api/real-estates?sort=id,desc"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests getting a single RealEstate by id
     * <p>
     * This test retrieves a RealEstate object from the database using its ID.
     * It then checks whether the object's attributes have valid values.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN)
    public void getRealEstate() throws Exception {
        // Initialize the database
        realEstateRepository.saveAndFlush(realEstate);

        // Get the realEstate
        restRealEstateMockMvc.perform(get("/api/real-estates/{id}", realEstate.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(realEstate.getId().intValue()))
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
                .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
                .andExpect(jsonPath("$.area").value(DEFAULT_AREA))
                .andExpect(jsonPath("$.heatingType").value(DEFAULT_HEATING_TYPE))
                .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED));
    }

    /**
     * Tests invalid retrieval attempts
     * <p>
     * This tests attempts to retrieve an RealEstate object which is not in the database
     * by searching for a non-existent id.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN)
    public void getNonExistingRealEstate() throws Exception {
        // Get the realEstate
        restRealEstateMockMvc.perform(get("/api/real-estates/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests RealEstate updating.
     * <p>
     * This test uses a mocked Advertiser user to save a RealEstate object to the database,
     * then updates the values of its attributes and uses PUT to save the object.
     * Then it compares the original number of objects in the database to the new one
     * and the updated values of our modified RealEstate with the ones found in
     * the database.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void updateRealEstate() throws Exception {
        // Initialize the database
        realEstateService.save(realEstate);

        int databaseSizeBeforeUpdate = realEstateRepository.findAll().size();

        // Update the realEstate
        RealEstate updatedRealEstate = realEstateRepository.findOne(realEstate.getId());
        updatedRealEstate
                .name(UPDATED_NAME)
                .type(UPDATED_TYPE)
                .area(UPDATED_AREA)
                .heatingType(UPDATED_HEATING_TYPE)
                .deleted(UPDATED_DELETED);

        restRealEstateMockMvc.perform(put("/api/real-estates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedRealEstate)))
                .andExpect(status().isOk());

        // Validate the RealEstate in the database
        List<RealEstate> realEstates = realEstateRepository.findAll();
        assertThat(realEstates).hasSize(databaseSizeBeforeUpdate);
        RealEstate testRealEstate = realEstates.get(realEstates.size() - 1);
        assertThat(testRealEstate.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRealEstate.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testRealEstate.getArea()).isEqualTo(UPDATED_AREA);
        assertThat(testRealEstate.getHeatingType()).isEqualTo(UPDATED_HEATING_TYPE);
        assertThat(testRealEstate.isDeleted()).isEqualTo(UPDATED_DELETED);
    }

    /**
     * Tests RealEstate updating as Guest.
     * <p>
     * This test saves a RealEstate object to the database,
     * then updates the values of its attributes and uses no authorization to
     * attempt to use PUT to save the object, which fails because the user is unauthorized.
     * The test then asserts that the number of objects in the database has not changed.
     * @throws Exception
     */
    @Test
    @Transactional
    public void updateRealEstateAsGuest() throws Exception {
        // Initialize the database
        realEstateService.save(realEstate);

        final int databaseSizeBeforeUpdate = realEstateRepository.findAll().size();

        // Update the realEstate
        RealEstate updatedRealEstate = realEstateRepository.findOne(realEstate.getId());
        updatedRealEstate
                .name(UPDATED_NAME)
                .type(UPDATED_TYPE)
                .area(UPDATED_AREA)
                .heatingType(UPDATED_HEATING_TYPE)
                .deleted(UPDATED_DELETED);

        restRealEstateMockMvc.perform(put("/api/real-estates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedRealEstate)))
                .andExpect(status().isUnauthorized());

        // Validate the RealEstate in the database
        final List<RealEstate> realEstates = realEstateRepository.findAll();
        assertThat(realEstates).hasSize(databaseSizeBeforeUpdate);
    }

    /**
     * Tests RealEstate deletion as Admin
     * <p>
     * This test uses a mocked Admin user
     * to delete an object on the database. It then asserts
     * that the number of objects on the database
     * after this action has been reduced by one.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN)
    public void deleteRealEstateAsAdmin() throws Exception {
        // Initialize the database
        realEstateService.save(realEstate);

        int databaseSizeBeforeDelete = realEstateRepository.findAll().size();

        // Get the realEstate
        restRealEstateMockMvc.perform(delete("/api/real-estates/{id}", realEstate.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<RealEstate> realEstates = realEstateRepository.findAll();
        assertThat(realEstates).hasSize(databaseSizeBeforeDelete - 1);
    }

    /**
     * Tests RealEstate deletion as a Verifier
     * <p>
     * This tests uses a mocked Verifier user to attempt
     * to delete a RealEstate on the database, which is not allowed.
     * It then asserts that the number of RealEstates on the database
     * has not changed.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.VERIFIER)
    public void deleteRealEstateAsVerifier() throws Exception {
        // Initialize the database
        realEstateService.save(realEstate);

        int databaseSizeBeforeDelete = realEstateRepository.findAll().size();

        // Get the realEstate
        restRealEstateMockMvc.perform(delete("/api/real-estates/{id}", realEstate.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isForbidden());

        // Validate the database is empty
        List<RealEstate> realEstates = realEstateRepository.findAll();
        assertThat(realEstates).hasSize(databaseSizeBeforeDelete);
    }

    /**
     * Tests RealEstate deletion as a Guest
     * <p>
     * This tests attempts to delete a RealEstate
     * on the database with no authorization,
     * which is not allowed. It then asserts
     * that the number of RealEstates on the
     * database has not changed.
     * @throws Exception
     */
    @Test
    @Transactional
    public void deleteRealEstateAsGuest() throws Exception {
        // Initialize the database
        realEstateService.save(realEstate);

        int databaseSizeBeforeDelete = realEstateRepository.findAll().size();

        // Get the realEstate
        restRealEstateMockMvc.perform(delete("/api/real-estates/{id}", realEstate.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isUnauthorized());

        // Validate the database is empty
        List<RealEstate> realEstates = realEstateRepository.findAll();
        assertThat(realEstates).hasSize(databaseSizeBeforeDelete);
    }

    /**
     * Tests getting deleted RealEstates as an Admin
     * <p>
     * This test retrieves all deleted RealEstate objects from the database
     * using an Admin's authority.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN)
    public void getAllDeletedRealEstatesAsAdmin() throws Exception {

        final boolean REAL_ESTATE_DELETED = true;

        realEstate.deleted(REAL_ESTATE_DELETED);

        // Add Deleted Announcement
        realEstateRepository.saveAndFlush(realEstate);

        final Long count = realEstateRepository.findAllByDeleted(REAL_ESTATE_DELETED, RealEstateConstants.PAGEABLE).getTotalElements();

        // Get all non deleted announcements
        restRealEstateMockMvc.perform(get("/api/real-estates/deleted/{status}", REAL_ESTATE_DELETED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(Math.toIntExact(count))))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(realEstate.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
                .andExpect(jsonPath("$.[*].area").value(hasItem(DEFAULT_AREA)))
                .andExpect(jsonPath("$.[*].heatingType").value(hasItem(DEFAULT_HEATING_TYPE)))
                .andExpect(jsonPath("$.[*].deleted").value(hasItem(REAL_ESTATE_DELETED)));
    }

    /**
     * Tests getting non-deleted RealEstates as an Advertiser
     * <p>
     * This test retrieves all undeleted RealEstate objects from the database
     * using an Advertiser's authority.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void getAllNonDeletedRealEstatesAsAdvertiser() throws Exception {

        final boolean REAL_ESTATE_DELETED = false;

        // Add Deleted Announcement
        realEstateRepository.saveAndFlush(realEstate);

        final Long count = realEstateRepository.findAllByDeleted(REAL_ESTATE_DELETED, RealEstateConstants.PAGEABLE).getTotalElements();

        // Get all non deleted announcements
        restRealEstateMockMvc.perform(get("/api/real-estates/deleted/{status}", REAL_ESTATE_DELETED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(Math.toIntExact(count))))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(realEstate.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
                .andExpect(jsonPath("$.[*].area").value(hasItem(DEFAULT_AREA)))
                .andExpect(jsonPath("$.[*].heatingType").value(hasItem(DEFAULT_HEATING_TYPE)))
                .andExpect(jsonPath("$.[*].deleted").value(hasItem(REAL_ESTATE_DELETED)));
    }

    /**
     * Tests getting deleted RealEstates as an Advertiser
     * <p>
     * This test tries to retrieve all deleted RealEstate objects from the database
     * using an Advertiser's authority. Because only Admin can get all deleted real estates
     * this test results with Method Not Allowed HTTP Status.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void getAllDeletedRealEstatesAsAdvertiser() throws Exception {

        final boolean REAL_ESTATE_DELETED = true;

        // Get all non deleted announcements
        restRealEstateMockMvc.perform(get("/api/real-estates/deleted/{status}", REAL_ESTATE_DELETED))
                .andExpect(status().isMethodNotAllowed());
    }

    /**
     * Tests getting similar RealEstates as an Advertiser
     * <p>
     * This test tries to retrieve all similar RealEstate objects from the database
     * based on custom implemented algorithm using an Advertiser's authority.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void getAllSimilarRealEstatesAsAdvertiser() throws Exception {

        Location location = new Location()
                .country(RealEstateConstants.SIMILAR_COUNTRY)
                .city(RealEstateConstants.SIMILAR_CITY)
                .cityRegion(RealEstateConstants.SIMILAR_REGION)
                .street(RealEstateConstants.SIMILAR_STREET)
                .streetNumber(RealEstateConstants.SIMILAR_STREET_NO);

        final RealEstateSimilarDTO similar = new RealEstateSimilarDTO(location, Double.parseDouble(RealEstateConstants.SIMILAR_AREA));

        final Long count = realEstateService.findAllSimilar(similar, RealEstateConstants.PAGEABLE).getTotalElements();

        restRealEstateMockMvc.perform(get("/api/real-estates/similar")
                .param("area", RealEstateConstants.SIMILAR_AREA)
                .param("country", RealEstateConstants.SIMILAR_COUNTRY)
                .param("city", RealEstateConstants.SIMILAR_CITY)
                .param("region", RealEstateConstants.SIMILAR_REGION)
                .param("street", RealEstateConstants.SIMILAR_STREET)
                .param("number", RealEstateConstants.SIMILAR_STREET_NO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(Math.toIntExact(count))))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
    }

    /**
     * Tests getting similar RealEstates as an Advertiser with wrong Area
     * <p>
     * This tests shows that number of retrieved results is 0 when there's no
     * similar area for any non-deleted real estate in database.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void getAllSimilarRealEstatesAsAdvertiserGoodAddressWrongArea() throws Exception {

        final String NOT_SIMILAR_AREA = "2110994";

        Location location = new Location()
                .country(RealEstateConstants.SIMILAR_COUNTRY)
                .city(RealEstateConstants.SIMILAR_CITY)
                .cityRegion(RealEstateConstants.SIMILAR_REGION)
                .street(RealEstateConstants.SIMILAR_STREET)
                .streetNumber(RealEstateConstants.SIMILAR_STREET_NO);

        final RealEstateSimilarDTO similar = new RealEstateSimilarDTO(location, Double.parseDouble(NOT_SIMILAR_AREA));

        final Long count = realEstateService.findAllSimilar(similar, RealEstateConstants.PAGEABLE).getTotalElements();

        // We wanted empty result
        assertThat(count).isEqualTo(0);

        restRealEstateMockMvc.perform(get("/api/real-estates/similar")
                .param("area", NOT_SIMILAR_AREA)
                .param("country", RealEstateConstants.SIMILAR_COUNTRY)
                .param("city", RealEstateConstants.SIMILAR_CITY)
                .param("region", RealEstateConstants.SIMILAR_REGION)
                .param("street", RealEstateConstants.SIMILAR_STREET)
                .param("number", RealEstateConstants.SIMILAR_STREET_NO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(Math.toIntExact(count))))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
    }

    /**
     * Tests getting similar RealEstates as an Advertiser with wrong Location
     * <p>
     * This tests shows that number of retrieved results is 0 when there's no
     * same location for any provided and database's real estate.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void getAllSimilarRealEstatesAsAdvertiserGoodAreaWrongAddress() throws Exception {

        final String NOT_SIMILAR_CITY = "Bijeljina";

        Location location = new Location()
                .country(RealEstateConstants.SIMILAR_COUNTRY)
                .city(NOT_SIMILAR_CITY)
                .cityRegion(RealEstateConstants.SIMILAR_REGION)
                .street(RealEstateConstants.SIMILAR_STREET)
                .streetNumber(RealEstateConstants.SIMILAR_STREET_NO);

        final RealEstateSimilarDTO similar = new RealEstateSimilarDTO(location, Double.parseDouble(RealEstateConstants.SIMILAR_AREA));

        final Long count = realEstateService.findAllSimilar(similar, RealEstateConstants.PAGEABLE).getTotalElements();

        // We wanted empty result
        assertThat(count).isEqualTo(0);

        restRealEstateMockMvc.perform(get("/api/real-estates/similar")
                .param("area", RealEstateConstants.SIMILAR_AREA)
                .param("country", RealEstateConstants.SIMILAR_COUNTRY)
                .param("city", NOT_SIMILAR_CITY)
                .param("region", RealEstateConstants.SIMILAR_REGION)
                .param("street", RealEstateConstants.SIMILAR_STREET)
                .param("number", RealEstateConstants.SIMILAR_STREET_NO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(Math.toIntExact(count))))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
    }

    /**
     * Tests tries to get similar RealEstates as a Verifier
     * <p>
     * Tries to find similar real estates, but because ADVERTISER has this privilege
     * it results with Forbidden HTTP status.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.VERIFIER)
    public void getAllSimilarRealEstatesAsVerifier() throws Exception {

        restRealEstateMockMvc.perform(get("/api/real-estates/similar")
                .param("area", RealEstateConstants.SIMILAR_AREA)
                .param("country", RealEstateConstants.SIMILAR_COUNTRY)
                .param("city", RealEstateConstants.SIMILAR_CITY)
                .param("region", RealEstateConstants.SIMILAR_REGION)
                .param("street", RealEstateConstants.SIMILAR_STREET)
                .param("number", RealEstateConstants.SIMILAR_STREET_NO))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests tries to get similar RealEstates as a Guest
     * <p>
     * Tries to find similar real estates, but because ADVERTISER has this privilege
     * it results with Unauthorized HTTP status.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void getAllSimilarRealEstatesAsGuest() throws Exception {

        restRealEstateMockMvc.perform(get("/api/real-estates/similar")
                .param("area", RealEstateConstants.SIMILAR_AREA)
                .param("country", RealEstateConstants.SIMILAR_COUNTRY)
                .param("city", RealEstateConstants.SIMILAR_CITY)
                .param("region", RealEstateConstants.SIMILAR_REGION)
                .param("street", RealEstateConstants.SIMILAR_STREET)
                .param("number", RealEstateConstants.SIMILAR_STREET_NO))
                .andExpect(status().isUnauthorized());
    }
}