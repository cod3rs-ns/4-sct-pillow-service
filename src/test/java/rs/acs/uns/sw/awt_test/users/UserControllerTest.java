package rs.acs.uns.sw.awt_test.users;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;
import rs.acs.uns.sw.awt_test.AwtTestSiitProject2016ApplicationTests;
import rs.acs.uns.sw.awt_test.util.Constants;
import rs.acs.uns.sw.awt_test.util.TestUtil;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
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
@SpringBootTest(classes = AwtTestSiitProject2016ApplicationTests.class)
public class UserControllerTest {

    private static final String DEFAULT_EMAIL = "user@email.com";

    private static final String DEFAULT_USERNAME = "username";

    private static final String DEFAULT_PASSWORD = "password";

    private User advertiser;

    @Autowired
    FilterChainProxy springSecurityFilterChain;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc mockMvc;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static User createEntity(EntityManager em, final String USER_TYPE) {
        return new User()
                .email(DEFAULT_EMAIL)
                .username(DEFAULT_USERNAME)
                .password(DEFAULT_PASSWORD)
                .type(USER_TYPE);
    }

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        UserController userController = new UserController();
        ReflectionTestUtils.setField(userController, "userService", userService);

        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Before
    public void initTest() {
        advertiser = createEntity(em, Constants.Roles.ADVERTISER);
    }

    @Test
    @Transactional
    public void registerUser() throws Exception {
        final int beforeDbSize = userRepository.findAll().size();

        // Create Advertiser
        mockMvc.perform(post("/api/users/")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(advertiser)))
        .andExpect(status().isCreated());

        final List<User> users = userRepository.findAll();

        assertThat(users).hasSize(beforeDbSize + 1);

        final User testUser = users.get(users.size() - 1);

        assertThat(testUser.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testUser.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testUser.getType()).isEqualTo(Constants.Roles.ADVERTISER);
        // TODO Check for password encoding
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
                .param("email", DEFAULT_EMAIL)
                .param("password", DEFAULT_PASSWORD))
                .andExpect(status().isOk());
    }

    @Test(expected = NestedServletException.class)
    @Transactional
    public void authUserFailed() throws Exception {
        // We don't have users in database
        when(mockMvc.perform(post("/api/users/auth")
                .param("email", DEFAULT_EMAIL)
                .param("password", DEFAULT_PASSWORD))
                .andExpect(status().isUnauthorized())
                .andDo(print())
        ).thenThrow(new NestedServletException("Bad credentials"));
    }

    @Test
    @Transactional
    public void getExistingUser() throws Exception {
        // Add user to database first
        userService.save(advertiser);

        mockMvc.perform(get("/api/users/{id}", advertiser.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(advertiser.getId().intValue()))
                .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME))
                .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
                .andExpect(jsonPath("$.type").value(Constants.Roles.ADVERTISER));
    }

    @Test
    @Transactional
    public void getNonExistingUser() throws Exception {
        mockMvc.perform(get("/api/users/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }


}
