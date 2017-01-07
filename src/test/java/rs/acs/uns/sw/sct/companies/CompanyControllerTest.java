package rs.acs.uns.sw.sct.companies;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import rs.acs.uns.sw.sct.SctServiceApplication;
import rs.acs.uns.sw.sct.constants.CompanyConstants;
import rs.acs.uns.sw.sct.constants.UserConstants;
import rs.acs.uns.sw.sct.users.UserRepository;
import rs.acs.uns.sw.sct.util.AuthorityRoles;
import rs.acs.uns.sw.sct.util.HeaderUtil;
import rs.acs.uns.sw.sct.util.TestUtil;

import javax.annotation.PostConstruct;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static rs.acs.uns.sw.sct.util.ContainsIgnoreCase.containsIgnoringCase;
import static rs.acs.uns.sw.sct.util.TestUtil.getRandomCaseInsensitiveSubstring;

/**
 * Test class for the CompanyResource REST controller.
 *
 * @see CompanyController
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SctServiceApplication.class)
@ActiveProfiles("test")
public class CompanyControllerTest {

    private static final String DEFAULT_NAME = "NAME_AAA";
    private static final String UPDATED_NAME = "NAME_BBB";

    private static final String DEFAULT_ADDRESS = "ADDRESS_AAA";
    private static final String UPDATED_ADDRESS = "ADDRESS_BBB";

    private static final String DEFAULT_PHONE_NUMBER = "0600000000";
    private static final String UPDATED_PHONE_NUMBER = "0611111111";

    private static final int PAGE_SIZE = 5;

    /**
     * Company already in database, which Test Advertiser (username: 'test_advertiser_company_member) is member of
     */
    private static final long DB_TEST_COMPANY_ID = 4L;


    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private WebApplicationContext context;

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

        this.restCompanyMockMvc = MockMvcBuilders
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
        company = createEntity();
    }

    /**
     * Tests addition of Company objects as Admin.
     * <p>
     * This test uses a mock Admin user to add a default Company
     * object to the database using a POST method.
     * It then proceeds to check whether the Company object was added successfully,
     * by comparing the number of objects in the database before and after the addition,
     * as well as the default Company's attributes to the Company in the database.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN)
    public void createCompanyAsAdmin() throws Exception {
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

    /**
     * Tests addition of Company objects as an Advertiser
     * <p>
     * This test uses a mock Advertiser user to
     * add a default Company object to the database using a POST method, which
     * is forbidden. It then asserts that the number of objects in the database
     * has not changed.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void createCompanyAsAdvertiser() throws Exception {
        final int databaseSizeBeforeCreate = companyRepository.findAll().size();

        // Create the Company

        restCompanyMockMvc.perform(post("/api/companies")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(company)))
                .andExpect(status().isForbidden());

        // Validate the Company in the database
        final List<Company> companies = companyRepository.findAll();
        assertThat(companies).hasSize(databaseSizeBeforeCreate);
    }

    /**
     * Tests addition of Company objects as a Guest
     * <p>
     * This test uses a mock Guest user to
     * add a default Company object to the database using a POST method, for which
     * this user is unauthorized. It then asserts that the number of objects in the
     * database has not changed.
     * @throws Exception
     */
    @Test
    @Transactional
    public void createCompanyAsGuest() throws Exception {
        final int databaseSizeBeforeCreate = companyRepository.findAll().size();

        // Create the Company

        restCompanyMockMvc.perform(post("/api/companies")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(company)))
                .andExpect(status().isUnauthorized());

        // Validate the Company in the database
        final List<Company> companies = companyRepository.findAll();
        assertThat(companies).hasSize(databaseSizeBeforeCreate);
    }

    /**
     * Tests whether the "Name" field is nullable
     * <p>
     * This test attempts to add a Company object with a null "Name" value to the database,
     * this is forbidden as the "Name" field is non-nullable. Other than expecting a "Bad request" status,
     * the test asserts that the number of objects in database has not changed.
     * @throws Exception
     */
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

    /**
     * Tests whether the "Address" field is nullable
     * <p>
     * This test attempts to add a Company object with a null "Address" value to the database,
     * this is forbidden as the "Address" field is non-nullable. Other than expecting a "Bad request" status,
     * the test asserts that the number of objects in database has not changed.
     * @throws Exception
     */
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

    /**
     * Tests whether the "Telephone number" field is nullable
     * <p>
     * This test attempts to add a Company object with a null "Telephone number" value to the database,
     * this is forbidden as the "Telephone number" field is non-nullable. Other than expecting a "Bad request" status,
     * the test asserts that the number of objects in database has not changed.
     * @throws Exception
     */
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


    /**
     * Tests getting all Companies
     * <p>
     * This test requests all Companies from the database.
     * It then asserts that the received results
     * match what was expected.
     * @throws Exception
     */
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

    /**
     * Tests getting a single Company
     * <p>
     * This test retrieves a Company object from the database using its ID.
     * It then checks whether the object's attributes have valid values.
     * @throws Exception
     */
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

    /**
     * Tests invalid retrieval attempts
     * <p>
     * This tests attempts to retrieve a Company object which is not in the database
     * by searching for a non-existent id. This returns an "Is not found" status.
     * @throws Exception
     */
    @Test
    @Transactional
    public void getNonExistingCompany() throws Exception {
        // Get the company
        restCompanyMockMvc.perform(get("/api/companies/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests Company updating as a member of that Company.
     * <p>
     * This test saves a Company object to the database,
     * Then it uses a mocked Advertiser user, who is a member of the company, to
     * update the values of its attributes and use PUT to save the object.
     * Then it asserts that the number of objects in the database has not changed
     * and that the updated values of our modified Company match the ones found in
     * the database.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER, username = "test_advertiser_company_member")
    public void updateCompanyAsAdvertiser() throws Exception {
        // Initialize the database

        int databaseSizeBeforeUpdate = companyRepository.findAll().size();

        // Update the company
        Company updatedCompany = companyRepository.findOne(DB_TEST_COMPANY_ID);

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
        Company testCompany = companyService.findOne(DB_TEST_COMPANY_ID);
        assertThat(testCompany.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCompany.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testCompany.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
    }

    /**
     * Tests Company updating as a Guest.
     * <p>
     * This test saves a Company object to the database,
     * then attempts to update the values of its attributes and uses PUT to save the object
     * using a mocked Guest user, for which this user is not authorized.
     * It then validates that the Comment's content and date
     * have not been changed on the database.
     * @throws Exception
     */
    @Test
    @Transactional
    public void updateCompanyAsGuest() throws Exception {
        // Initialize the database
        companyService.save(company);

        final int databaseSizeBeforeUpdate = companyRepository.findAll().size();

        // Update the company
        Company updatedCompany = companyRepository.findOne(company.getId());

        updatedCompany
                .name(UPDATED_NAME)
                .address(UPDATED_ADDRESS)
                .phoneNumber(UPDATED_PHONE_NUMBER);

        restCompanyMockMvc.perform(put("/api/companies")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedCompany)))
                .andExpect(status().isUnauthorized());

        // Validate the Company in the database
        final List<Company> companies = companyRepository.findAll();
        assertThat(companies).hasSize(databaseSizeBeforeUpdate);
    }

    /**
     * Tests Company deletion as Admin
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
    public void deleteCompanyAsAdmin() throws Exception {
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

    /**
     * Tests Company deletion as an Advertiser
     * <p>
     * This tests uses a mocked Advertiser user to attempt
     * to delete a Company on the database, which is not allowed.
     * It then asserts that the number of Companies on the database
     * has not changed.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void deleteCompanyAsAdvertiser() throws Exception {
        // Initialize the database
        companyService.save(company);

        final int databaseSizeBeforeDelete = companyRepository.findAll().size();

        // Get the company
        restCompanyMockMvc.perform(delete("/api/companies/{id}", company.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isForbidden());

        // Validate the database is empty
        List<Company> companies = companyRepository.findAll();
        assertThat(companies).hasSize(databaseSizeBeforeDelete);
    }

    /**
     * Tests Company deletion as a Guest
     * <p>
     * This tests attempts to delete a Company
     * on the database with no authorization,
     * which is not allowed. It then asserts
     * that the number of Companies on the
     * database has not changed.
     * @throws Exception
     */
    @Test
    @Transactional
    public void deleteCompanyAsGuest() throws Exception {
        // Initialize the database
        companyService.save(company);

        final int databaseSizeBeforeDelete = companyRepository.findAll().size();

        // Get the company
        restCompanyMockMvc.perform(delete("/api/companies/{id}", company.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isUnauthorized());

        // Validate the database is empty
        List<Company> companies = companyRepository.findAll();
        assertThat(companies).hasSize(databaseSizeBeforeDelete);
    }

    /**
     * Tests making a request for a Company as an Advertiser
     * <p>
     * This test saves a company to the database then uses a
     * mocked Advertiser user to send a request for joining
     * the company and expects an "Ok" status.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER, username = UserConstants.ADVERTISER_USERNAME)
    public void sendRequestForCompanyAsAdvertiser() throws Exception {
        // Initialize the database
        companyService.save(company);

        restCompanyMockMvc.perform(put("/api/companies/{companyId}/user-request/", company.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.company.id").value(company.getId()))
                .andExpect(status().isOk());
    }


    /**
     * Tests making a request for a Company as an Admin
     * <p>
     * This test saves a company to the database then uses a
     * mocked Admin user to send a request for joining
     * the company and expects an "Forbidden" status.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN, username = UserConstants.ADVERTISER_USERNAME)
    public void sendRequestForCompanyAsAdmin() throws Exception {
        // Initialize the database
        companyService.save(company);

        restCompanyMockMvc.perform(put("/api/companies/{companyId}/user-request/", company.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isForbidden());
    }


    /**
     * Tests making a request for a Company as a Guest
     * <p>
     * This test saves a company to the database then uses a
     * mocked Guest user to send a request for joining
     * the company and expects an "Is unauthorised" status.
     * @throws Exception
     */
    @Test
    @Transactional
    public void sendRequestForCompanyAsGuest() throws Exception {
        // Initialize the database
        companyService.save(company);

        restCompanyMockMvc.perform(put("/api/companies/{companyId}/user-request/", company.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isUnauthorized());
    }


    /**
     * Tests making a request for a Company as an Advertiser who has already requested membership
     * <p>
     * This test saves a company to the database then uses a
     * mocked Advertiser user who has already requested to join the company
     * to send a request for joining the company and expects a "Conflict" status.
     * It then asserts that the received error key matches the one for
     * someone already having made a request.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER, username = "test_advertiser_company_member")
    public void sendRequestForCompanyAsAlreadyCompanyVerifiedAdvertiser() throws Exception {
        // Initialize the database
        companyService.save(company);

        final MvcResult result = restCompanyMockMvc.perform(put("/api/companies/{companyId}/user-request/", company.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isConflict())
                .andReturn();

        final String message = result.getResponse().getContentAsString();
        final Integer errorKey = Integer.valueOf(result.getResponse().getHeader(HeaderUtil.SCT_HEADER_ERROR_KEY));
        assertThat(errorKey).isEqualTo(HeaderUtil.ERROR_CODE_ALREADY_REQUESTED_MEMBERSHIP);
    }

    /**
     * Tests approval of request by an Advertiser who is already a member of the Company
     * <p>
     * This test saves a company to the database then uses a
     * mocked Advertiser user who has already joined the Company
     * to use PUT to modify a specific Request object tied to the
     * Company, setting its "Confirmed" attribute to true.
     * It then asserts that the Company whose Request was modified
     * is still in the database.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER, username = UserConstants.ADVERTISER_COMPANY_USERNAME)
    public void sendRequestForCompanyAsAlreadyCompanyVerifiedAdvertiserAndOverrideCompany() throws Exception {
        // Initialize the database
        companyService.save(company);

        restCompanyMockMvc.perform(put("/api/companies/{companyId}/user-request/", company.getId())
                .param("confirmed", "true")
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.company.id").value(company.getId()));
    }

    /**
     * Tests retrieval of Requests tied to a Company as an Advertiser who is a member of the Company
     * <p>
     * This test uses a mocked Advertiser user who is a member of a Company to search
     * for all accepted requests made to that company, then asserts that the number
     * of received is as expected.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER, username = UserConstants.ADVERTISER_COMPANY_USERNAME)
    public void getCompanyRequestsByStatusAsRightCompanyVerifiedAdvertiser() throws Exception {

        final String status = "accepted";

        // Already Existed Company
        final Long companyId = 1L;

        final Long count = userRepository.findByCompany_IdAndCompanyVerified(companyId, status, CompanyConstants.PAGEABLE).getTotalElements();

        restCompanyMockMvc.perform(get("/api/companies/users-requests", companyId)
                .param("status", status)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(Math.toIntExact(count))));
    }

    /**
     * Tests retrieval of Requests tied to a Company as an Advertiser who is not a member of the Company
     * <p>
     * This test uses a mocked Advertiser user who is not a member of a Company to search
     * for all accepted requests made to that company, which results in a bad request.
     * The test then asserts that the received error key matches the error key for
     * requests made by Advertisers who are not a member of the company.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER, username = "test_advertiser_pending_membership")
    public void getCompanyRequestsByStatusAsCompanyNotVerifiedAdvertiser() throws Exception {

        final String status = "accepted";

        // Already Existed Company
        final Long companyId = 4L;

        final MvcResult result = restCompanyMockMvc.perform(get("/api/companies/users-requests", companyId)
                .param("status", status)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andReturn();

        final String message = result.getResponse().getContentAsString();
        final Integer errorKey = Integer.valueOf(result.getResponse().getHeader(HeaderUtil.SCT_HEADER_ERROR_KEY));
        assertThat(errorKey).isEqualTo(HeaderUtil.ERROR_CODE_NOT_MEMBER_OF_COMPANY);
    }

    /**
     * Tests retrieval of Requests tied to a Company as a Guest
     * <p>
     * This test searches for all accepted requests made to a company without any authorization,
     * which results in a "Unauthorized" status.
     * @throws Exception
     */
    @Test
    @Transactional
    public void getCompanyRequestsByStatusAsGuest() throws Exception {

        final String status = "accepted";

        // Already Existed Company
        final Long companyId = 1L;

        restCompanyMockMvc.perform(get("/api/companies/users-requests", companyId)
                .param("status", status)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests retrieval of Requests tied to a Company as an Admin
     * <p>
     * This test searches for all accepted requests made to a company without using a
     * mocked Admin user, which is forbidden.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN)
    public void getCompanyRequestsByStatusAsAdmin() throws Exception {

        final String status = "accepted";

        // Already Existed Company
        final Long companyId = 1L;

        restCompanyMockMvc.perform(get("/api/companies/users-requests", companyId)
                .param("status", status)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests resolution of Requests as a Guest
     * <p>
     * This test uses no authorization to attempt to change the "accepted"
     * attribute of a Request on the database, which results in an
     * "Unauthorized" status.
     * @throws Exception
     */
    @Test
    @Transactional
    public void resolveCompanyMembershipAsGuest() throws Exception {

        final String accepted = "true";

        // Already Existed Company
        final Long userId = 1L;

        restCompanyMockMvc.perform(put("/api/companies/resolve-request/user/{userId}", userId)
                .param("accepted", accepted)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests resolution of Requests for a User that does not exist
     * <p>
     * This test uses a mocked Advertiser who is a member of the Company
     * to attempt to change the "accepted" attribute of a Request belonging to
     * a non-existing User, which results in an "Not found" status.
     * The test then asserts that the received error code matches the one
     * for requests for non-existing entities.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER, username = "test_advertiser_company_member")
    public void resolveCompanyMembershipAsAdvertiserForWrongUser() throws Exception {

        final String accepted = "true";

        // Already Existed Company
        final Long userId = Long.MAX_VALUE;

        final MvcResult result = restCompanyMockMvc.perform(put("/api/companies/resolve-request/user/{userId}", userId)
                .param("accepted", accepted)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound())
                .andReturn();

        final Integer errorKey = Integer.valueOf(result.getResponse().getHeader(HeaderUtil.SCT_HEADER_ERROR_KEY));
        assertThat(errorKey).isEqualTo(HeaderUtil.ERROR_CODE_NON_EXISTING_ENTITY);
    }

    /**
     * Tests resolution of Requests for a User that has not made a Request
     * <p>
     * This test uses a mocked Advertiser who is a member of the Company
     * to attempt to change the "accepted" attribute of a Request belonging to
     * a User who has not made a request, which results in an "Not acceptable" status.
     * The test then asserts that the received error code matches the one
     * for users who did not request membership.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER, username = "test_advertiser_company_member")
    public void resolveCompanyMembershipAsAdvertiserForUserWithoutRequest() throws Exception {

        final String accepted = "true";

        // Already Existed Company
        final Long userId = 14L;

        final MvcResult result = restCompanyMockMvc.perform(put("/api/companies/resolve-request/user/{userId}", userId)
                .param("accepted", accepted)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotAcceptable())
                .andReturn();

        final Integer errorKey = Integer.valueOf(result.getResponse().getHeader(HeaderUtil.SCT_HEADER_ERROR_KEY));
        assertThat(errorKey).isEqualTo(HeaderUtil.ERROR_CODE_USER_DID_NOT_REQUEST_MEMBERSHIP);
    }

    /**
     * Tests resolution of Requests for a User that is already a member of the Company
     * <p>
     * This test uses a mocked Advertiser who is a member of the Company
     * to attempt to change the "accepted" attribute of a Request belonging to
     * a User who has already joined the same Company, which results in an
     * "Not acceptable" status. The test then asserts that the received
     * error code matches the one for users who did not request membership.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER, username = "test_advertiser_company_member")
    public void resolveCompanyMembershipAsVerifierWithAlreadyAcceptedUser() throws Exception {

        final String accepted = "true";

        // User that's already in the same company
        final Long userId = 16L;

        final MvcResult result = restCompanyMockMvc.perform(put("/api/companies/resolve-request/user/{userId}", userId)
                .param("accepted", accepted)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotAcceptable())
                .andReturn();

        final Integer errorKey = Integer.valueOf(result.getResponse().getHeader(HeaderUtil.SCT_HEADER_ERROR_KEY));

        assertThat(errorKey).isEqualTo(HeaderUtil.ERROR_CODE_USER_DID_NOT_REQUEST_MEMBERSHIP);
    }

    /**
     * Tests resolution of Requests for a User that is already a member of another Company
     * <p>
     * This test uses a mocked Advertiser who is a member of the Company
     * to attempt to change the "accepted" attribute of a Request belonging to
     * a User who has already joined another Company, which results in an "Bad request" status.
     * The test then asserts that the received error code matches the one
     * for no permission to resolve memberships.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER, username = "test_advertiser_other_company_member")
    public void resolveCompanyMembershipAsAdvertiserForUserWithDifferentCompany() throws Exception {

        // Our user belongs to company with ID = 1

        final String accepted = "true";

        // User which pending for company with ID = 3
        final Long userId = 13L;

        final MvcResult result = restCompanyMockMvc.perform(put("/api/companies/resolve-request/user/{userId}", userId)
                .param("accepted", accepted)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andReturn();

        final Integer errorKey = Integer.valueOf(result.getResponse().getHeader(HeaderUtil.SCT_HEADER_ERROR_KEY));
        assertThat(errorKey).isEqualTo(HeaderUtil.ERROR_CODE_NO_PERMISSION_TO_RESOLVE_MEMBERSHIP);
    }

    /**
     * Tests resolution of Requests for a User who has requested membership in the Company
     * <p>
     * This test uses a mocked Advertiser who is a member of the Company
     * to attempt to change the "accepted" attribute to "true" of a Request belonging to
     * a User who has requested to join the Company, which results in an "Ok" status.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER, username = "test_advertiser_company_member")
    public void resolveCompanyMembershipAsAdvertiserAndSetToAccepted() throws Exception {

        // Our user belongs to company with ID = 4

        final String accepted = "true";

        // User which pending for company with ID = 4
        final Long userId = 13L;

        restCompanyMockMvc.perform(put("/api/companies/resolve-request/user/{userId}", userId)
                .param("accepted", accepted)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // TODO Check for updated user
    }

    /**
     * Tests resolution of Requests for a User who has requested membership in the Company
     * <p>
     * This test uses a mocked Advertiser who is a member of the Company
     * to attempt to change the "accepted" attribute to "false" of a Request belonging to
     * a User who has requested to join the Company, which results in an "Ok" status.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER, username = "test_advertiser_company_member")
    public void resolveCompanyMembershipAsAdvertiserAndSetToFalse() throws Exception {

        // Our user belongs to company with ID = 4

        final String accepted = "false";

        // User which pending for company with ID = 4
        final Long userId = 13L;

        restCompanyMockMvc.perform(put("/api/companies/resolve-request/user/{userId}", userId)
                .param("accepted", accepted)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());


        // TODO Check for updated user
    }

    /**
     * Tests searching Companies using no arguments
     * <p>
     * This test asserts that the number of Companies that
     * are returned when searching using no arguments matches the number
     * of Announcements in the database or the number of Companies
     * per page of search results, whichever is smaller.
     * @throws Exception
     */
    @Test
    @Transactional
    public void searchCompaniesWithoutAnyAttribute() throws Exception {
        final int dbSize = companyRepository.findAll().size();
        final int requiredSize = dbSize < PAGE_SIZE ? dbSize : PAGE_SIZE;

        restCompanyMockMvc.perform(get("/api/companies/search")
                .param("sort", "id,desc")
                .param("size", String.valueOf(PAGE_SIZE))
                .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(Math.toIntExact(requiredSize))))
                .andReturn();
    }

    /**
     * Tests searching Companies using arguments
     * <p>
     * This test takes random substrings of a Company's name, address and
     * phone number and uses them as arguments to perform a search.
     * It then asserts that all of the returned results have
     * values which contain these substrings.
     * @throws Exception
     */
    @Test
    @Transactional
    public void searchCompaniesByNameAndAddressAndPhoneNumber() throws Exception {
        // prepare db data
        companyService.save(company);

        final String randomName = getRandomCaseInsensitiveSubstring(company.getName());
        final String randomAddress = getRandomCaseInsensitiveSubstring(company.getAddress());
        final String randomPhoneNumber = getRandomCaseInsensitiveSubstring(company.getPhoneNumber());

        restCompanyMockMvc.perform(get("/api/companies/search")
                .param("sort", "id,desc")
                .param("size", String.valueOf(PAGE_SIZE))
                .param("page", "0")
                .param("name", randomName)
                .param("address", randomAddress)
                .param("phoneNumber", randomPhoneNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].name", everyItem(containsIgnoringCase(randomName))))
                .andExpect(jsonPath("$.[*].name", hasItem(company.getName())))
                .andExpect(jsonPath("$.[*].address", everyItem(containsIgnoringCase(randomAddress))))
                .andExpect(jsonPath("$.[*].address", hasItem(company.getAddress())))
                .andExpect(jsonPath("$.[*].phoneNumber", everyItem(containsIgnoringCase(randomPhoneNumber))))
                .andExpect(jsonPath("$.[*].phoneNumber", hasItem(company.getPhoneNumber())))
                .andReturn();
    }
}
