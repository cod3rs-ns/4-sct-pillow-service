package rs.acs.uns.sw.sct.companies;

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
import rs.acs.uns.sw.sct.SctServiceApplication;
import rs.acs.uns.sw.sct.util.TestUtil;

import javax.annotation.PostConstruct;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the CompanyResource REST controller.
 *
 * @see CompanyController
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SctServiceApplication.class)
public class CompanyControllerTest {

    private static final String DEFAULT_NAME = "NAME_AAA";
    private static final String UPDATED_NAME = "NAME_BBB";

    private static final String DEFAULT_ADDRESS = "ADDRESS_AAA";
    private static final String UPDATED_ADDRESS = "ADDRESS_BBB";

    private static final String DEFAULT_PHONE_NUMBER = "0600000000";
    private static final String UPDATED_PHONE_NUMBER = "0611111111";

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restCompanyMockMvc;

    private Company company;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Company createEntity() {
        return new Company()
                .name(DEFAULT_NAME)
                .address(DEFAULT_ADDRESS)
                .phoneNumber(DEFAULT_PHONE_NUMBER);
    }

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CompanyController companyCtrl = new CompanyController();
        ReflectionTestUtils.setField(companyCtrl, "companyService", companyService);
        this.restCompanyMockMvc = MockMvcBuilders.standaloneSetup(companyCtrl)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        company = createEntity();
    }

    @Test
    @Transactional
    public void createCompany() throws Exception {
        int databaseSizeBeforeCreate = companyRepository.findAll().size();

        // Create the Company

        restCompanyMockMvc.perform(post("/api/companies")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(company)))
                .andExpect(status().isCreated());

        // Validate the Company in the database
        List<Company> companies = companyRepository.findAll();
        assertThat(companies).hasSize(databaseSizeBeforeCreate + 1);
        Company testCompany = companies.get(companies.size() - 1);
        assertThat(testCompany.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCompany.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testCompany.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = companyRepository.findAll().size();
        // set the field null
        company.setName(null);

        // Create the Company, which fails.

        restCompanyMockMvc.perform(post("/api/companies")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(company)))
                .andExpect(status().isBadRequest());

        List<Company> companies = companyRepository.findAll();
        assertThat(companies).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAddressIsRequired() throws Exception {
        int databaseSizeBeforeTest = companyRepository.findAll().size();
        // set the field null
        company.setAddress(null);

        // Create the Company, which fails.

        restCompanyMockMvc.perform(post("/api/companies")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(company)))
                .andExpect(status().isBadRequest());

        List<Company> companies = companyRepository.findAll();
        assertThat(companies).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTelephoneNoIsRequired() throws Exception {
        int databaseSizeBeforeTest = companyRepository.findAll().size();
        // set the field null
        company.setPhoneNumber(null);

        // Create the Company, which fails.

        restCompanyMockMvc.perform(post("/api/companies")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(company)))
                .andExpect(status().isBadRequest());

        List<Company> companies = companyRepository.findAll();
        assertThat(companies).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllCompanies() throws Exception {
        // Initialize the database
        companyRepository.saveAndFlush(company);

        // Get all the companies
        restCompanyMockMvc.perform(get("/api/companies?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(company.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
                .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
                .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)));
    }

    @Test
    @Transactional
    public void getCompany() throws Exception {
        // Initialize the database
        companyRepository.saveAndFlush(company);

        // Get the company
        restCompanyMockMvc.perform(get("/api/companies/{id}", company.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(company.getId().intValue()))
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
                .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
                .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER));
    }

    @Test
    @Transactional
    public void getNonExistingCompany() throws Exception {
        // Get the company
        restCompanyMockMvc.perform(get("/api/companies/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCompany() throws Exception {
        // Initialize the database
        companyService.save(company);

        int databaseSizeBeforeUpdate = companyRepository.findAll().size();

        // Update the company
        Company updatedCompany = companyRepository.findOne(company.getId());
        updatedCompany
                .name(UPDATED_NAME)
                .address(UPDATED_ADDRESS)
                .phoneNumber(UPDATED_PHONE_NUMBER);

        restCompanyMockMvc.perform(put("/api/companies")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedCompany)))
                .andExpect(status().isOk());

        // Validate the Company in the database
        List<Company> companies = companyRepository.findAll();
        assertThat(companies).hasSize(databaseSizeBeforeUpdate);
        Company testCompany = companies.get(companies.size() - 1);
        assertThat(testCompany.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCompany.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testCompany.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    public void deleteCompany() throws Exception {
        // Initialize the database
        companyService.save(company);

        int databaseSizeBeforeDelete = companyRepository.findAll().size();

        // Get the company
        restCompanyMockMvc.perform(delete("/api/companies/{id}", company.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Company> companies = companyRepository.findAll();
        assertThat(companies).hasSize(databaseSizeBeforeDelete - 1);
    }
}
