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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import rs.acs.uns.sw.sct.SctServiceApplication;
import rs.acs.uns.sw.sct.companies.Company;
import rs.acs.uns.sw.sct.constants.CompanyConstants;
import rs.acs.uns.sw.sct.constants.UserConstants;
import rs.acs.uns.sw.sct.util.AuthorityRoles;
import rs.acs.uns.sw.sct.util.Constants;
import rs.acs.uns.sw.sct.util.TestUtil;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Autowired
    FilterChainProxy springSecurityFilterChain;

    private User advertiser;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserService userService;

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

}
