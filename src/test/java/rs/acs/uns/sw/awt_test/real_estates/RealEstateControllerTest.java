package rs.acs.uns.sw.awt_test.real_estates;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import rs.acs.uns.sw.awt_test.AwtTestSiitProject2016ApplicationTests;
import rs.acs.uns.sw.awt_test.util.TestUtil;

import javax.annotation.PostConstruct;
<<<<<<< HEAD
=======
import javax.persistence.EntityManager;
>>>>>>> auth-tests-feature
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
<<<<<<< HEAD
 * Test class for the RealEstate REST controller.
=======
 * Test class for the RealEstateResource REST controller.
>>>>>>> auth-tests-feature
 *
 * @see RealEstateController
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AwtTestSiitProject2016ApplicationTests.class)
public class RealEstateControllerTest {

<<<<<<< HEAD
    private static final String DEFAULT_NAME = "NAME_AAA";
    private static final String UPDATED_NAME = "NAME_BBB";

    private static final String DEFAULT_TYPE = "TYPE_AAA";
    private static final String UPDATED_TYPE = "TYPE_BBB";
=======
    private static final String DEFAULT_NAME = "AA";
    private static final String UPDATED_NAME = "BB";

    private static final String DEFAULT_TYPE = "AAAAA";
    private static final String UPDATED_TYPE = "BBBBB";
>>>>>>> auth-tests-feature

    private static final Double DEFAULT_AREA = 1D;
    private static final Double UPDATED_AREA = 2D;

<<<<<<< HEAD
    private static final String DEFAULT_HEATING_TYPE = "HEATING_AAA";
    private static final String UPDATED_HEATING_TYPE = "HEATING_BBB";
=======
    private static final String DEFAULT_HEATING_TYPE = "AAAAA";
    private static final String UPDATED_HEATING_TYPE = "BBBBB";
>>>>>>> auth-tests-feature

    private static final Boolean DEFAULT_DELETED = false;
    private static final Boolean UPDATED_DELETED = true;

    @Autowired
    private RealEstateRepository realEstateRepository;

    @Autowired
    private RealEstateService realEstateService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

<<<<<<< HEAD
=======
    @Autowired
    private EntityManager em;

>>>>>>> auth-tests-feature
    private MockMvc restRealEstateMockMvc;

    private RealEstate realEstate;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
<<<<<<< HEAD
    public static RealEstate createEntity() {
        return new RealEstate()
=======
    public static RealEstate createEntity(EntityManager em) {
        RealEstate realEstate = new RealEstate()
>>>>>>> auth-tests-feature
                .name(DEFAULT_NAME)
                .type(DEFAULT_TYPE)
                .area(DEFAULT_AREA)
                .heatingType(DEFAULT_HEATING_TYPE)
                .deleted(DEFAULT_DELETED);
<<<<<<< HEAD
=======
        return realEstate;
>>>>>>> auth-tests-feature
    }

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RealEstateController realEstateCtrl = new RealEstateController();
        ReflectionTestUtils.setField(realEstateCtrl, "realEstateService", realEstateService);
        this.restRealEstateMockMvc = MockMvcBuilders.standaloneSetup(realEstateCtrl)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
<<<<<<< HEAD
        realEstate = createEntity();
=======
        realEstate = createEntity(em);
>>>>>>> auth-tests-feature
    }

    @Test
    @Transactional
    public void createRealEstate() throws Exception {
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

<<<<<<< HEAD

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

=======
>>>>>>> auth-tests-feature
    @Test
    @Transactional
    public void getAllRealEstates() throws Exception {
        // Initialize the database
        realEstateRepository.saveAndFlush(realEstate);

        // Get all the realEstates
        restRealEstateMockMvc.perform(get("/api/real-estates?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(realEstate.getId().intValue())))
<<<<<<< HEAD
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
                .andExpect(jsonPath("$.[*].area").value(hasItem(DEFAULT_AREA)))
                .andExpect(jsonPath("$.[*].heatingType").value(hasItem(DEFAULT_HEATING_TYPE)))
                .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED)));
=======
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
                .andExpect(jsonPath("$.[*].area").value(hasItem(DEFAULT_AREA.doubleValue())))
                .andExpect(jsonPath("$.[*].heatingType").value(hasItem(DEFAULT_HEATING_TYPE.toString())))
                .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));
>>>>>>> auth-tests-feature
    }

    @Test
    @Transactional
    public void getRealEstate() throws Exception {
        // Initialize the database
        realEstateRepository.saveAndFlush(realEstate);

        // Get the realEstate
        restRealEstateMockMvc.perform(get("/api/real-estates/{id}", realEstate.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(realEstate.getId().intValue()))
<<<<<<< HEAD
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
                .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
                .andExpect(jsonPath("$.area").value(DEFAULT_AREA))
                .andExpect(jsonPath("$.heatingType").value(DEFAULT_HEATING_TYPE))
                .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED));
=======
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
                .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
                .andExpect(jsonPath("$.area").value(DEFAULT_AREA.doubleValue()))
                .andExpect(jsonPath("$.heatingType").value(DEFAULT_HEATING_TYPE.toString()))
                .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED.booleanValue()));
>>>>>>> auth-tests-feature
    }

    @Test
    @Transactional
    public void getNonExistingRealEstate() throws Exception {
        // Get the realEstate
        restRealEstateMockMvc.perform(get("/api/real-estates/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
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

    @Test
    @Transactional
    public void deleteRealEstate() throws Exception {
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
}