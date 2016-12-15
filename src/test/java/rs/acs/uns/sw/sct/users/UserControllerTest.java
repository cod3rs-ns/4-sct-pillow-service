package rs.acs.uns.sw.sct.users;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import rs.acs.uns.sw.sct.SctServiceApplication;
import rs.acs.uns.sw.sct.companies.Company;
import rs.acs.uns.sw.sct.companies.CompanyService;
import rs.acs.uns.sw.sct.constants.CompanyConstants;
import rs.acs.uns.sw.sct.constants.UserConstants;
import rs.acs.uns.sw.sct.util.AuthorityRoles;
import rs.acs.uns.sw.sct.util.Constants;
import rs.acs.uns.sw.sct.util.HeaderUtil;
import rs.acs.uns.sw.sct.util.TestUtil;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rs.acs.uns.sw.sct.util.ContainsIgnoreCase.containsIgnoringCase;
import static rs.acs.uns.sw.sct.util.TestUtil.getRandomCaseInsensitiveSubstring;


/**
 * Test class for the User REST controller.
 *
 * @see UserController
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SctServiceApplication.class)
public class UserControllerTest {

    private static final String DEFAULT_EMAIL = "user@email.com";

    private static final String DEFAULT_USERNAME = "username";

    private static final String DEFAULT_PASSWORD = "password";

    private static final String DEFAULT_FIRST_NAME = "Miloš";

    private static final String DEFAULT_LAST_NAME = "Teodosić";

    private static final String DEFAULT_PHONE_NUMBER = "0600000000";

    private static final int PAGE_SIZE = 5;

    @Autowired
    FilterChainProxy springSecurityFilterChain;

    private User advertiser;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    private MockMvc mockMvc;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static User createEntity(final String USER_TYPE) {
        return new User()
                .email(DEFAULT_EMAIL)
                .username(DEFAULT_USERNAME)
                .password(DEFAULT_PASSWORD)
                .type(USER_TYPE)
                .firstName(DEFAULT_FIRST_NAME)
                .lastName(DEFAULT_LAST_NAME)
                .phoneNumber(DEFAULT_PHONE_NUMBER)
                .verified(true)
                .company(new Company().id(CompanyConstants.ID));
    }

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        UserController userController = new UserController();
        ReflectionTestUtils.setField(userController, "userService", userService);

        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Before
    public void initTest() {
        advertiser = createEntity(Constants.Roles.ADVERTISER);
    }

    @Test
    @Transactional
    public void registerUser() throws Exception {
        final int beforeDbSize = userRepository.findAll().size();

        // Create Advertiser
        mockMvc.perform(post("/api/users/")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(advertiser)))
                .andDo(print())
                .andExpect(status().isCreated());

        final List<User> users = userRepository.findAll();

        assertThat(users).hasSize(beforeDbSize + 1);

        final User testUser = users.get(users.size() - 1);

        assertThat(testUser.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testUser.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testUser.getType()).isEqualTo(Constants.Roles.ADVERTISER);
        assertThat(testUser.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
    }

    @Test
    @Transactional
    public void registerUserWithExistingUsername() throws Exception {
        final int beforeDbSize = userRepository.findAll().size();

        advertiser.setUsername(UserConstants.EXISTING_USERNAME);

        // Create Advertiser
        final MvcResult result = mockMvc.perform(post("/api/users/")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(advertiser)))
                .andExpect(status().isBadRequest())
                .andReturn();

        final String message = result.getResponse().getHeader(HeaderUtil.SCT_HEADER_ALERT);
        assertThat(message).isEqualTo("Username already exists!");

        final List<User> users = userRepository.findAll();
        assertThat(users).hasSize(beforeDbSize);
    }

    @Test
    @Transactional
    public void registerUserWithExistingEmail() throws Exception {
        final int beforeDbSize = userRepository.findAll().size();

        advertiser.setEmail(UserConstants.EXISTING_EMAIL);

        // Create Advertiser
        final MvcResult result = mockMvc.perform(post("/api/users/")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(advertiser)))
                .andExpect(status().isBadRequest())
                .andReturn();

        final String message = result.getResponse().getHeader(HeaderUtil.SCT_HEADER_ALERT);
        assertThat(message).isEqualTo("Email already exists!");

        final List<User> users = userRepository.findAll();
        assertThat(users).hasSize(beforeDbSize);
    }

    @Test
    @Transactional
    public void checkValueIsRequired() throws Exception {
        final int beforeDbSize = userRepository.findAll().size();

        advertiser.setEmail(null);

        mockMvc.perform(post("/api/users/")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(advertiser)))
                .andExpect(status().isBadRequest());

        final List<User> users = userRepository.findAll();

        assertThat(users).hasSize(beforeDbSize);

    }

    @Test
    @Transactional
    public void authUserSuccess() throws Exception {
        // Add user to database first
        userService.save(advertiser);

        // Authorize with that credentials
        mockMvc.perform(post("/api/users/auth")
                .param("username", DEFAULT_USERNAME)
                .param("password", DEFAULT_PASSWORD))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void authUserFailed() throws Exception {
        // We don't have users in database
        mockMvc.perform(post("/api/users/auth")
                .param("username", DEFAULT_EMAIL)
                .param("password", DEFAULT_PASSWORD))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @Transactional
    public void getExistingUserAsGuest() throws Exception {
        // Add user to database first
        userService.save(advertiser);

        mockMvc.perform(get("/api/users/{id}", advertiser.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(advertiser.getId().intValue()))
                .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME))
                .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
                .andExpect(jsonPath("$.type").value(Constants.Roles.ADVERTISER))
                .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void getExistingUserAsGuestAsAdvertiser() throws Exception {
        // Add user to database first
        userService.save(advertiser);

        mockMvc.perform(get("/api/users/{id}", advertiser.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(advertiser.getId().intValue()))
                .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME))
                .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
                .andExpect(jsonPath("$.type").value(Constants.Roles.ADVERTISER))
                .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER));
    }

    @Test
    @Transactional
    public void getUsersByCompanyId() throws Exception {
        // Add user to database first
        userService.save(advertiser);

        mockMvc.perform(get("/api/users/company/{companyId}", advertiser.getCompany().getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[?(@.id == " + advertiser.getId() + ")]").exists());
    }

    @Test
    @Transactional
    public void getNonExistingUser() throws Exception {
        mockMvc.perform(get("/api/users/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN)
    public void getUsersByStatusDeletedAsAdmin() throws Exception {

        final String status = "true";

        advertiser.deleted(true);
        userService.save(advertiser);

        final Long usersDeletedCount = userRepository.findAllByDeleted(true, UserConstants.PAGEABLE).getTotalElements();

        mockMvc.perform(get("/api/users/deleted/{status}", status)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(Math.toIntExact(usersDeletedCount))))
                .andExpect(jsonPath("$.[*].id").value(hasItem(advertiser.getId().intValue())))
                .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME)))
                .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
                .andExpect(jsonPath("$.[*].type").value(hasItem(Constants.Roles.ADVERTISER)))
                .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)));
    }

    @Test
    @Transactional
    public void getUsersByStatusDeletedAsGuest() throws Exception {

        final String status = "false";

        advertiser.deleted(false);
        userService.save(advertiser);

        final Long usersDeletedCount = userRepository.findAllByDeleted(false, UserConstants.PAGEABLE).getTotalElements();

        mockMvc.perform(get("/api/users/deleted/{status}", status)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(Math.toIntExact(usersDeletedCount))))
                .andExpect(jsonPath("$.[*].id").value(hasItem(advertiser.getId().intValue())))
                .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME)))
                .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
                .andExpect(jsonPath("$.[*].type").value(hasItem(Constants.Roles.ADVERTISER)))
                .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)));
    }

    @Test
    @Transactional
    public void searchUsersWithoutAuthority() throws Exception {
        mockMvc.perform(get("/api/users/search")
                .param("sort", "id,desc")
                .param("size", String.valueOf(PAGE_SIZE))
                .param("page", "0"))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }


    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void searchUsersWithoutAnyAttribute() throws Exception {
        final int dbSize = userRepository.findAll().size();
        final int requiredSize = dbSize < PAGE_SIZE ? dbSize : PAGE_SIZE;

        mockMvc.perform(get("/api/users/search")
                .param("sort", "id,desc")
                .param("size", String.valueOf(PAGE_SIZE))
                .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(Math.toIntExact(requiredSize))))
                .andReturn();
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN)
    public void searchUsersByUsernameAndEmailAndNameAndSurname() throws Exception {
        userRepository.saveAndFlush(advertiser);

        final String randomUsername = getRandomCaseInsensitiveSubstring(advertiser.getUsername());
        final String randomEmail = getRandomCaseInsensitiveSubstring(advertiser.getEmail());
        final String randomFN = getRandomCaseInsensitiveSubstring(advertiser.getFirstName());
        final String randomLN = getRandomCaseInsensitiveSubstring(advertiser.getLastName());

        mockMvc.perform(get("/api/users/search")
                .param("sort", "id,desc")
                .param("size", String.valueOf(PAGE_SIZE))
                .param("page", "0")
                .param("username", randomUsername)
                .param("email", randomEmail)
                .param("firstName", randomFN)
                .param("lastName", randomLN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].username", everyItem(containsIgnoringCase(randomUsername))))
                .andExpect(jsonPath("$.[*].username", hasItem(advertiser.getUsername())))
                .andExpect(jsonPath("$.[*].email", everyItem(containsIgnoringCase(randomEmail))))
                .andExpect(jsonPath("$.[*].email", hasItem(advertiser.getEmail())))
                .andExpect(jsonPath("$.[*].firstName", everyItem(containsIgnoringCase(randomFN))))
                .andExpect(jsonPath("$.[*].firstName", hasItem(advertiser.getFirstName())))
                .andExpect(jsonPath("$.[*].lastName", everyItem(containsIgnoringCase(randomLN))))
                .andExpect(jsonPath("$.[*].lastName", hasItem(advertiser.getLastName())))
                .andReturn();
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.VERIFIER)
    public void searchDeletedUser() throws Exception {
        User persisted = userRepository.saveAndFlush(advertiser.deleted(true));

        final int dbSize = userRepository.findAllByDeleted(true, null).getContent().size();
        assertThat(dbSize).isGreaterThan(0);

        mockMvc.perform(get("/api/users/search")
                .param("sort", "id,desc")
                .param("size", String.valueOf(PAGE_SIZE))
                .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].id", everyItem(not(comparesEqualTo(Integer.valueOf(persisted.getId().intValue()))))))
                .andReturn();
    }


    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.VERIFIER)
    public void searchUsersByPhoneNumberAndCompanyName() throws Exception {
        Company company = companyService.findOne(CompanyConstants.ID);
        advertiser.setCompany(company);
        advertiser.setCompanyVerified(Constants.CompanyStatus.ACCEPTED);
        userRepository.saveAndFlush(advertiser);

        final String randomPhoneNumber = getRandomCaseInsensitiveSubstring(advertiser.getPhoneNumber());
        final String randomCompanyName = getRandomCaseInsensitiveSubstring(advertiser.getCompany().getName());

        mockMvc.perform(get("/api/users/search")
                .param("sort", "id,desc")
                .param("size", String.valueOf(PAGE_SIZE))
                .param("page", "0")
                .param("phoneNumber", randomPhoneNumber)
                .param("companyName", randomCompanyName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].phoneNumber", everyItem(containsIgnoringCase(randomPhoneNumber))))
                .andExpect(jsonPath("$.[*].phoneNumber", hasItem(advertiser.getPhoneNumber())))
                .andExpect(jsonPath("$.[*].company.name", everyItem(containsIgnoringCase(randomCompanyName))))
                .andExpect(jsonPath("$.[*].company.name", hasItem(advertiser.getCompany().getName())))
                .andReturn();

    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.VERIFIER)
    public void searchUsersByCompanyNameWhenCompanyStatusIsNotAccepted() throws Exception {
        Company company = companyService.findOne(CompanyConstants.ID);
        advertiser.setCompany(company);
        advertiser.setCompanyVerified(Constants.CompanyStatus.PENDING);
        userRepository.saveAndFlush(advertiser);

        mockMvc.perform(get("/api/users/search")
                .param("sort", "id,desc")
                .param("size", String.valueOf(PAGE_SIZE))
                .param("page", "0")
                .param("companyName", company.getName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].id", everyItem(not(equalTo(advertiser.getId().intValue())))))
                .andReturn();
    }

    @Test
    @Transactional
    public void isUsernameAvailableTrue() throws Exception {
        // We didn't save 'advertiser' to database

        final MvcResult result = mockMvc.perform(get("/api/users/username-available")
                .param("username", advertiser.getUsername()))
                .andExpect(status().isOk())
                .andReturn();

        final Boolean response = Boolean.parseBoolean(result.getResponse().getContentAsString());
        assertThat(response).isEqualTo(true);
    }

    @Test
    @Transactional
    public void isUsernameAvailableFalse() throws Exception {
        // We saved 'advertiser' to database
        userRepository.saveAndFlush(advertiser);

        final MvcResult result = mockMvc.perform(get("/api/users/username-available")
                .param("username", advertiser.getUsername()))
                .andExpect(status().isOk())
                .andReturn();

        final Boolean response = Boolean.parseBoolean(result.getResponse().getContentAsString());
        assertThat(response).isEqualTo(false);
    }

    @Test
    @Transactional
    public void isEmailAvailableTrue() throws Exception {
        // We didn't save 'advertiser' to database

        final MvcResult result = mockMvc.perform(get("/api/users/email-available")
                .param("email", advertiser.getEmail()))
                .andExpect(status().isOk())
                .andReturn();

        final Boolean response = Boolean.parseBoolean(result.getResponse().getContentAsString());
        assertThat(response).isEqualTo(true);
    }

    @Test
    @Transactional
    public void isEmailAvailableFalse() throws Exception {
        // We saved 'advertiser' to database
        userRepository.saveAndFlush(advertiser);

        final MvcResult result = mockMvc.perform(get("/api/users/email-available")
                .param("email", advertiser.getEmail()))
                .andExpect(status().isOk())
                .andReturn();

        final Boolean response = Boolean.parseBoolean(result.getResponse().getContentAsString());
        assertThat(response).isEqualTo(false);
    }

    @Test
    @Transactional
    public void tryToSetVerificationToTrueWhenRegister() throws Exception {
        // Try to verify user through bean (without sending mail)
        advertiser.verified(true);

        // Create Advertiser
        mockMvc.perform(post("/api/users/")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(advertiser)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.verified", is(false)));
    }

    @Test
    @Transactional
    public void tryAuthenticateWhenEmailVerificationIsNotConfirmed() throws Exception {
        // Try to verify user through bean (without sending mail)
        userService.save(advertiser.verified(false));

        // Try to authenticate
        mockMvc.perform(post("/api/users/auth")
                .param("username", DEFAULT_USERNAME)
                .param("password", DEFAULT_PASSWORD))
                .andExpect(status().isUnauthorized());
    }
}
