package rs.acs.uns.sw.sct.users;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import rs.acs.uns.sw.sct.SctServiceApplication;
import rs.acs.uns.sw.sct.constants.CompanyConstants;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static rs.acs.uns.sw.sct.constants.UserConstants.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SctServiceApplication.class)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User newUser;
    private User updatedUser;
    private User existingUser;

    private void compareUsers(User user1, User user2, boolean checkPassword){
        if (user1.getId() != null && user2.getId() != null)
            assertThat(user1.getId()).isEqualTo(user2.getId());
        assertThat(user1.getFirstName()).isEqualTo(user2.getFirstName());
        assertThat(user1.getLastName()).isEqualTo(user2.getLastName());
        assertThat(user1.getEmail()).isEqualTo(user2.getEmail());
        if (checkPassword)
            assertThat(user1.getPassword()).isEqualTo(user2.getPassword());
        assertThat(user1.getPhoneNumber()).isEqualTo(user2.getPhoneNumber());
        assertThat(user1.getType()).isEqualTo(user2.getType());
        assertThat(user1.getUsername()).isEqualTo(user2.getUsername());
    }

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static User createEntity(String email, String firstName, String lastName, String username,
                                    String password, String telephone, String type) {
        return new User()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .password(password)
                .phoneNumber(telephone)
                .type(type);
    }

    @Before
    public void initTest() {
        newUser = createEntity(NEW_USER_EMAIL, NEW_USER_FIRST_NAME, NEW_USER_LAST_NAME, NEW_USER_USERNAME, NEW_USER_PASSWORD, NEW_USER_TELEPHONE, NEW_USER_TYPE);
        updatedUser = createEntity(UPDATED_USER_EMAIL, UPDATED_USER_FIRST_NAME, UPDATED_USER_LAST_NAME, UPDATED_USER_USERNAME, UPDATED_USER_PASSWORD, UPDATED_USER_TELEPHONE, UPDATED_USER_TYPE);
        existingUser = createEntity(USER_EMAIL, USER_FIRST_NAME, USER_LAST_NAME, USER_USERNAME, USER_PASSWORD, USER_TELEPHONE, USER_TYPE);
    }


    @Test
    public void testFindAll() {
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(DB_COUNT);
    }

    @Test
    public void testFindOne(){
        User user = userService.findOne(USER_ID);
        assertThat(user).isNotNull();

        compareUsers(user, existingUser, true);
    }

    @Test
    @Transactional
    public void testAdd() {
        int dbSizeBeforeAdd = userRepository.findAll().size();

        User dbUser = userService.save(newUser);
        assertThat(dbUser).isNotNull();

        //prepare password
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(NEW_USER_PASSWORD);

        // Validate that new user is in the database
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(dbSizeBeforeAdd + 1);

        compareUsers(dbUser, newUser, false);
        assertTrue(passwordEncoder.matches(NEW_USER_PASSWORD, hashedPassword));
    }

    @Test
    public void testFindUserByEmail() {
        User dbUser = userService.getUserByEmail(USER_EMAIL);
        assertThat(dbUser).isNotNull();

        compareUsers(dbUser, existingUser, true);
    }

    @Test
    public void testFindAllUsersByCompany() {
        Page<User> users = userService.findAllByCompany(CompanyConstants.ID, PAGEABLE);
        assertThat(users.getContent()).hasSize(USERS_IN_COMPANY);
    }

    @Test
    @Transactional
    public void testUpdate() {
        User dbUser = userService.findOne(USER_ID);

        dbUser.setFirstName(UPDATED_USER_FIRST_NAME);
        dbUser.setLastName(UPDATED_USER_LAST_NAME);
        dbUser.setEmail(UPDATED_USER_EMAIL);
        dbUser.setPassword(UPDATED_USER_PASSWORD);
        dbUser.setPhoneNumber(UPDATED_USER_TELEPHONE);
        dbUser.setType(UPDATED_USER_TYPE);
        dbUser.setUsername(UPDATED_USER_USERNAME);

        User updatedDbUser = userService.save(dbUser);
        assertThat(updatedDbUser).isNotNull();

        //prepare password
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(NEW_USER_PASSWORD);

        compareUsers(updatedDbUser, updatedUser, false);
        assertTrue(passwordEncoder.matches(NEW_USER_PASSWORD, hashedPassword));
    }

    /*
	 * Negative tests
	 */

    @Test(expected = DataIntegrityViolationException.class)
    @Transactional
    public void testAddNonUniqueUsername() {
        // set id to null to enable db to notice that we want to save same user
        existingUser.setId(null);
        existingUser.setEmail(updatedUser.getEmail());
        userService.save(existingUser);
        // rollback previous value
        existingUser.setId(USER_ID);
    }

    @Test(expected = DataIntegrityViolationException.class)
    @Transactional
    public void testAddNonUniqueEmail() {
        // set id to null to enable db to notice that we want to save same user
        existingUser.setId(null);
        existingUser.setUsername(updatedUser.getUsername());
        userService.save(existingUser);
        // rollback previous id
        existingUser.setId(USER_ID);
    }

    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullFirstName() {
        newUser.setFirstName(null);
        userService.save(newUser);
        // rollback previous first name
        newUser.setFirstName(NEW_USER_FIRST_NAME);
    }

    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullLastName() {
        newUser.setLastName(null);
        userService.save(newUser);
        // rollback previous last name
        newUser.setLastName(NEW_USER_LAST_NAME);
    }

    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullType() {
        newUser.setType(null);
        userService.save(newUser);
        // rollback previous type
        newUser.setType(NEW_USER_TYPE);
    }

    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullPassword() {
        newUser.setPassword(null);
        userService.save(newUser);
        // rollback previous password
        newUser.setPassword(NEW_USER_PASSWORD);
    }

    @Test
    @Transactional
    public void testFindAllByStatusDeletedTrue() {
        final Boolean status = true;

        final Page<User> users = userService.findAllByStatus(status, PAGEABLE);

        assertThat(users.getTotalElements()).isEqualTo(DB_COUNT_USERS_DELETED_TRUE);

        for (final User user: users) {
            assertThat(user.isDeleted()).isEqualTo(status);
        }
    }

    @Test
    @Transactional
    public void testFindAllByStatusDeletedFalse() {
        final Boolean status = false;

        final Page<User> users = userService.findAllByStatus(status, PAGEABLE);

        assertThat(users.getTotalElements()).isEqualTo(DB_COUNT_USERS_DELETED_FALSE);

        for (final User user: users) {
            assertThat(user.isDeleted()).isEqualTo(status);
        }
    }

    @Test
    @Transactional
    public void testFindAllByCompanyMembershipStatusAccepted() {
        final String status = "accepted";

        final Page<User> users = userService.findAllByCompanyMembershipStatus(USER_COMPANY_ID, status, PAGEABLE);

        assertThat(users.getTotalElements()).isEqualTo(USER_COMPANY_3_ACCEPTED);

        for (final User user: users) {
            assertThat(user.getCompanyVerified()).isEqualTo(status);
        }
    }

    @Test
    @Transactional
    public void testFindAllByCompanyMembershipStatusPending() {
        final String status = "pending";

        final Page<User> users = userService.findAllByCompanyMembershipStatus(USER_COMPANY_ID, status, PAGEABLE);

        assertThat(users.getTotalElements()).isEqualTo(USER_COMPANY_3_PENDING);

        for (final User user: users) {
            assertThat(user.getCompanyVerified()).isEqualTo(status);
        }
    }
}