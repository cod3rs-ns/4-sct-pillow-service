package rs.acs.uns.sw.sct.users;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import rs.acs.uns.sw.sct.SctServiceApplication;
import rs.acs.uns.sw.sct.companies.Company;
import rs.acs.uns.sw.sct.companies.CompanyService;
import rs.acs.uns.sw.sct.constants.CompanyConstants;
import rs.acs.uns.sw.sct.util.Constants;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static rs.acs.uns.sw.sct.constants.UserConstants.*;
import static rs.acs.uns.sw.sct.util.TestUtil.getRandomCaseInsensitiveSubstring;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SctServiceApplication.class)
@ActiveProfiles("test")
public class UserServiceTest {

    @Autowired
    private UserService userService;


    @Autowired
    private CompanyService companyService;


    @Autowired
    private UserRepository userRepository;

    private User newUser;
    private User updatedUser;
    private User existingUser;

    /**
     * Asserts equality of two Users.
     *
     * @param user1 One of the Users to be compared
     * @param user2 The other Users to be compared
     * @param checkPassword Determines whether passwords are compared
     */
    private void compareUsers(User user1, User user2, boolean checkPassword) {
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

    /**
     * Initializes all objects required for testing
     */
    @Before
    public void initTest() {
        newUser = createEntity(NEW_USER_EMAIL, NEW_USER_FIRST_NAME, NEW_USER_LAST_NAME, NEW_USER_USERNAME, NEW_USER_PASSWORD, NEW_USER_TELEPHONE, NEW_USER_TYPE);
        updatedUser = createEntity(UPDATED_USER_EMAIL, UPDATED_USER_FIRST_NAME, UPDATED_USER_LAST_NAME, UPDATED_USER_USERNAME, UPDATED_USER_PASSWORD, UPDATED_USER_TELEPHONE, UPDATED_USER_TYPE);
        existingUser = createEntity(USER_EMAIL, USER_FIRST_NAME, USER_LAST_NAME, USER_USERNAME, USER_PASSWORD, USER_TELEPHONE, USER_TYPE);
    }

    /**
     * Tests retrieval of all Users
     * <p>
     * This test finds all Users on the repository and asserts
     * that the number of returned results is equal to the number of
     * Users on the database
     */
    @Test
    public void testFindAll() {
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(DB_COUNT);
    }

    /**
     * Tests retrieval of a single User.
     * <p>
     * This test uses the id of an User that is in the repository
     * to search for it, then asserts that the returned value is not null
     * and compares the returned User to an existing User.
     */
    @Test
    public void testFindOne() {
        User user = userService.findOne(USER_ID);
        assertThat(user).isNotNull();

        compareUsers(user, existingUser, true);
    }

    /**
     * Tests addition of Users
     * <p>
     * This announcement saves a new User using the UserService,
     * then it finds all Users and asserts that the size of the results
     * has increased by one. It also asserts that the new User that is on
     * the database equals the User we added.
     */
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

    /**
     * Tests searching for Users by their email
     * <p>
     * This test uses the email of an User that is in the repository
     * to search for it, then asserts that the returned value is not null
     * and compares the returned User to an existing User.
     */
    @Test
    public void testFindUserByEmail() {
        User dbUser = userService.getUserByEmail(USER_EMAIL);
        assertThat(dbUser).isNotNull();

        compareUsers(dbUser, existingUser, true);
    }

    /**
     * Tests searching for Users by their Company's id
     * <p>
     * This test finds all Users tied to a Company's id,
     * then asserts that the number of returned results matches
     * the expected number
     */
    @Test
    public void testFindAllUsersByCompany() {
        Page<User> users = userService.findAllByCompany(CompanyConstants.ID, PAGEABLE);
        assertThat(users.getContent()).hasSize(USERS_IN_COMPANY);
    }

    /**
     * Tests updating of Users.
     * <p>
     * This test retrieves a User using the service, then changes
     * its attributes and saves it to the database. Then it asserts that
     * the object on the database is not null and equals our updated User.
     */
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

    /**
     * Tests adding a User with a non unique username
     * <p>
     * This test sets a User's username to one already in use, then
     * attempts to add it to the database. As usernames must be unique,
     * the test receives a
     * Data Integrity Violation exception.
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

    /**
     * Tests adding a User with a non unique email
     * <p>
     * This test sets a User's email to one already in use, then
     * attempts to add it to the database. As emails must be unique,
     * the test receives a
     * Data Integrity Violation exception.
     */
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

    /**
     * Tests adding a User with a null first name value
     * <p>
     * This test sets a User's first name to null, then
     * attempts to add it to the database. As first name is a
     * non-nullable field, the test receives a
     * Constraint Violation exception.
     */
    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullFirstName() {
        newUser.setFirstName(null);
        userService.save(newUser);
        // rollback previous first name
        newUser.setFirstName(NEW_USER_FIRST_NAME);
    }

    /**
     * Tests adding a User with a null last name value
     * <p>
     * This test sets a User's last name to null, then
     * attempts to add it to the database. As last name is a
     * non-nullable field, the test receives a
     * Constraint Violation exception.
     */
    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullLastName() {
        newUser.setLastName(null);
        userService.save(newUser);
        // rollback previous last name
        newUser.setLastName(NEW_USER_LAST_NAME);
    }

    /**
     * Tests adding a User with a null type value
     * <p>
     * This test sets a User's type to null, then
     * attempts to add it to the database. As type is a
     * non-nullable field, the test receives a
     * Constraint Violation exception.
     */
    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullType() {
        newUser.setType(null);
        userService.save(newUser);
        // rollback previous type
        newUser.setType(NEW_USER_TYPE);
    }

    /**
     * Tests adding a User with a null password value
     * <p>
     * This test sets a User's password to null, then
     * attempts to add it to the database. As password is a
     * non-nullable field, the test receives a
     * Constraint Violation exception.
     */
    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullPassword() {
        newUser.setPassword(null);
        userService.save(newUser);
        // rollback previous password
        newUser.setPassword(NEW_USER_PASSWORD);
    }

    /**
     * Tests searching for Users whose deleted value is true.
     * <p>
     * This test finds all Users whose deleted value is true,
     * then asserts that the number of returned results matches
     * the expected number and asserts that every one of the
     * results has a true deleted value.
     */
    @Test
    @Transactional
    public void testFindAllByStatusDeletedTrue() {
        final Boolean status = true;

        final Page<User> users = userService.findAllByStatus(status, PAGEABLE);

        assertThat(users.getTotalElements()).isEqualTo(DB_COUNT_USERS_DELETED_TRUE);

        for (final User user : users) {
            assertThat(user.isDeleted()).isEqualTo(status);
        }
    }

    /**
     * Tests searching for Users whose deleted value is false.
     * <p>
     * This test finds all Users whose deleted value is false,
     * then asserts that the number of returned results matches
     * the expected number and asserts that every one of the
     * results has a false deleted value.
     */
    @Test
    @Transactional
    public void testFindAllByStatusDeletedFalse() {
        final Boolean status = false;

        final Page<User> users = userService.findAllByStatus(status, PAGEABLE);

        assertThat(users.getTotalElements()).isEqualTo(DB_COUNT_USERS_DELETED_FALSE);

        for (final User user : users) {
            assertThat(user.isDeleted()).isEqualTo(status);
        }
    }

    /**
     * Tests searching for Users whose membership status is accepted
     * <p>
     * This test finds all Users tied whose membership status is accepted,
     * then asserts that the number of returned results matches
     * the expected number and asserts that every one of the
     * results has an accepted membership status value.
     */
    @Test
    @Transactional
    public void testFindAllByCompanyMembershipStatusAccepted() {
        final String status = "accepted";

        final Page<User> users = userService.findAllByCompanyMembershipStatus(USER_COMPANY_ID, status, PAGEABLE);

        assertThat(users.getTotalElements()).isEqualTo(USER_COMPANY_3_ACCEPTED);

        for (final User user : users) {
            assertThat(user.getCompanyVerified()).isEqualTo(status);
        }
    }

    /**
     * Tests searching for Users whose membership status is pending
     * <p>
     * This test finds all Users tied whose membership status is pending,
     * then asserts that the number of returned results matches
     * the expected number and asserts that every one of the
     * results has a pending membership status value.
     */
    @Test
    @Transactional
    public void testFindAllByCompanyMembershipStatusPending() {
        final String status = "pending";

        final Page<User> users = userService.findAllByCompanyMembershipStatus(USER_COMPANY_ID, status, PAGEABLE);

        assertThat(users.getTotalElements()).isEqualTo(USER_COMPANY_3_PENDING);

        for (final User user : users) {
            assertThat(user.getCompanyVerified()).isEqualTo(status);
        }
    }

    /**
     * Tests searching for Users without arguments
     * <p>
     * This test searches the database without using any arguments and
     * then asserts that the number of returned objects is equal to the number of
     * objects on the database or the number of objects per page, whichever is smaller.
     * @throws Exception
     */
    @Test
    @Transactional
    public void searchUsersWithoutAnyAttribute() throws Exception {
        final int dbSize = userService.findAllByStatus(false, null).getContent().size();
        final int requiredSize = dbSize < PAGEABLE.getPageSize() ? dbSize : PAGEABLE.getPageSize();

        List<User> result = userService.findBySearchTerm(null, null, null, null, null, null, PAGEABLE);
        assertThat(result).hasSize(requiredSize);
    }

    /**
     * Tests searching for deleted Users
     * <p>
     * This test saves a User whose deleted value is true
     * then searches without arguments for Users and asserts that none
     * of the returned results match the deleted User.
     * @throws Exception
     */
    @Test
    @Transactional
    public void searchDeletedUsers() throws Exception {
        User persisted = userRepository.saveAndFlush(
                newUser.deleted(true));

        List<User> result = userService.findBySearchTerm(null, null, null, null, null, null, PAGEABLE);

        for (User user : result) {
            assertThat(user.getId()).isNotEqualTo(persisted.getId());
        }
    }


    /**
     * Tests searching Users using arguments
     * <p>
     * This test makes a User verified in a Company, then
     * takes random substrings of that User's username, email, first name,
     * last name, Company's name and phone number,
     * and uses them as arguments to perform a search.
     * It then asserts that all of the returned results have
     * values which contain these substrings.
     * @throws Exception
     */
    @Test
    @Transactional
    public void searchUsersByUsernameAndEmailAndNameAndSurname() throws Exception {
        userRepository.saveAndFlush(newUser);


        Company company = companyService.findOne(CompanyConstants.ID);
        newUser.setCompany(company);
        newUser.setCompanyVerified(Constants.CompanyStatus.ACCEPTED);
        userRepository.saveAndFlush(newUser);

        final String randomUsername = getRandomCaseInsensitiveSubstring(newUser.getUsername());
        final String randomEmail = getRandomCaseInsensitiveSubstring(newUser.getEmail());
        final String randomFN = getRandomCaseInsensitiveSubstring(newUser.getFirstName());
        final String randomLN = getRandomCaseInsensitiveSubstring(newUser.getLastName());
        final String randomPhoneNumber = getRandomCaseInsensitiveSubstring(newUser.getPhoneNumber());
        final String randomCompanyName = getRandomCaseInsensitiveSubstring(newUser.getCompany().getName());

        List<User> result = userService.findBySearchTerm(randomUsername, randomEmail, randomFN, randomLN, randomPhoneNumber, randomCompanyName, PAGEABLE);

        for (User user : result){
            assertThat(user.getUsername()).containsIgnoringCase(randomUsername);
            assertThat(user.getEmail()).containsIgnoringCase(randomEmail);
            assertThat(user.getFirstName()).containsIgnoringCase(randomFN);
            assertThat(user.getLastName()).containsIgnoringCase(randomLN);
            assertThat(user.getPhoneNumber()).containsIgnoringCase(randomPhoneNumber);
            assertThat(user.getCompany().getName()).containsIgnoringCase(randomCompanyName);
        }
    }
}