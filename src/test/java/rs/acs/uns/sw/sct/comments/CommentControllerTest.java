package rs.acs.uns.sw.sct.comments;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import rs.acs.uns.sw.sct.SctServiceApplication;
import rs.acs.uns.sw.sct.announcements.Announcement;
import rs.acs.uns.sw.sct.constants.CommentConstants;
import rs.acs.uns.sw.sct.constants.UserConstants;
import rs.acs.uns.sw.sct.users.User;
import rs.acs.uns.sw.sct.util.AuthorityRoles;
import rs.acs.uns.sw.sct.util.DBUserMocker;
import rs.acs.uns.sw.sct.util.DateUtil;
import rs.acs.uns.sw.sct.util.TestUtil;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the CommentResource REST controller.
 *
 * @see CommentController
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SctServiceApplication.class)
@ActiveProfiles("test")
public class CommentControllerTest {

    private static final String DEFAULT_CONTENT = "CONTENT_AAA";
    private static final String UPDATED_CONTENT = "CONTENT_BBB";

    private static final Date DEFAULT_DATE = DateUtil.asDate(LocalDate.ofEpochDay(0L));
    private static final Date UPDATED_DATE = DateUtil.asDate(LocalDate.now(ZoneId.systemDefault()));
    private static final Long DEFAULT_USER = 1L;
    private static final Long DEFAULT_ANNOUNCEMENT = 1L;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private WebApplicationContext context;

    private MockMvc restCommentMockMvc;

    private Comment comment;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Comment createEntity() {

        Announcement announcement = new Announcement()
                .id(DEFAULT_ANNOUNCEMENT)
                .author(new User().id(DEFAULT_USER));

        return new Comment()
                .content(DEFAULT_CONTENT)
                .author(new User().id(DEFAULT_USER))
                .announcement(announcement)
                .date(DEFAULT_DATE);
    }


    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CommentController commentCtrl = new CommentController();
        ReflectionTestUtils.setField(commentCtrl, "commentService", commentService);
        this.restCommentMockMvc = MockMvcBuilders
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
        comment = createEntity();
    }

    /**
     * Tests addition of Comment objects to the database.
     * <p>
     * This test uses a mock User with authority to
     * add a default Comment object to the database using a POST method.
     * It then proceeds to check whether the Comment object was added successfully,
     * by comparing the number of objects in the database before and after the addition,
     * as well as the default Comment's attributes to the Comment in the database.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN, username = UserConstants.ADMIN_USERNAME)
    public void createComment() throws Exception {
        int databaseSizeBeforeCreate = commentRepository.findAll().size();

        // create the Comment
        restCommentMockMvc.perform(post("/api/comments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(comment)))
                .andExpect(status().isCreated());

        // validate the Comment in the database
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(databaseSizeBeforeCreate + 1);
        Comment testComment = comments.get(comments.size() - 1);
        assertThat(testComment.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testComment.getDate()).isEqualTo(DEFAULT_DATE);
    }

    /**
     * Tests whether the "Content" field is nullable
     * <p>
     * This test attempts to add a Comment object with a null "Content" value to the database,
     * this is forbidden as the "Content" field is non-nullable. Other than expecting a "Bad request" status,
     * the test compares the number of objects in database before and after the attempted addition.
     * @throws Exception
     */
    @Test
    @Transactional
    public void checkContentIsRequired() throws Exception {
        int databaseSizeBeforeTest = commentRepository.findAll().size();
        // set the field null
        comment.setContent(null);

        // create the Comment, which fails.

        restCommentMockMvc.perform(post("/api/comments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(comment)))
                .andExpect(status().isBadRequest());

        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(databaseSizeBeforeTest);
    }

    /**
     * Tests whether the "Date" field is nullable
     * <p>
     * This test attempts to add a Comment object with a null "Content" value to the database,
     * this is forbidden as the "Date" field is non-nullable. Other than expecting a "Bad request" status,
     * the test compares the number of objects in database before and after the attempted addition.
     * @throws Exception
     */
    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = commentRepository.findAll().size();
        // set the field null
        comment.setDate(null);

        restCommentMockMvc.perform(post("/api/comments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(comment)))
                .andExpect(status().isBadRequest());

        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(databaseSizeBeforeTest);
    }

    /**
     * Tests getting all Comments as an Admin
     * <p>
     * This test uses a mocked Admin user to request all Comments
     * from the database. It then asserts that the received results
     * match what was expected.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN)
    public void getAllCommentsAsAdmin() throws Exception {
        // initialize the database
        commentRepository.saveAndFlush(comment);

        // get all the comments
        restCommentMockMvc.perform(get("/api/comments?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(comment.getId().intValue())))
                .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
                .andExpect(jsonPath("$.[*].date").value(hasItem((int) DEFAULT_DATE.getTime())));
    }

    /**
     * Tests getting all Comments as an Advertiser
     * <p>
     * This test uses a mocked Advertiser user to request all Comments
     * from the database, which fails and returns a Forbidden status.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void getAllCommentsAsAdvertiser() throws Exception {
        // Get all the comments
        restCommentMockMvc.perform(get("/api/comments?sort=id,desc"))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests getting all Comments as a Guest
     * <p>
     * This test uses a mocked Guest user to request all Comments
     * from the database, which fails and returns an Unauthorized status.
     * @throws Exception
     */
    @Test
    @Transactional
    public void getAllCommentsAsGuest() throws Exception {
        // get all the comments
        restCommentMockMvc.perform(get("/api/comments?sort=id,desc"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests getting a single Comment by id as a Verifier
     * <p>
     * This test uses a mocked Verifier user to retrieve a
     * Comment object from the database using its ID.
     * It then checks whether the object's attributes have valid values.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.VERIFIER)
    public void getCommentAsVerifier() throws Exception {
        // initialize the database
        commentRepository.saveAndFlush(comment);

        // get the comment
        restCommentMockMvc.perform(get("/api/comments/{id}", comment.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(comment.getId().intValue()))
                .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
                .andExpect(jsonPath("$.date").value((int) DEFAULT_DATE.getTime()));
    }

    /**
     * Tests getting a single Comment by id as a Guest
     * <p>
     * This test uses a mocked Guest user to attempt to retrieve a
     * Comment object from the database using its ID.
     * This fails and returns an Unauthorized status.
     * @throws Exception
     */
    @Test
    @Transactional
    public void getCommentAsGuest() throws Exception {
        // initialize the database
        commentRepository.saveAndFlush(comment);

        // Get the comment
        restCommentMockMvc.perform(get("/api/comments/{id}", comment.getId()))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests invalid retrieval attempts
     * <p>
     * This tests attempts to retrieve a Comment object which is not in the database
     * by searching for a non-existent id. This returns an "Is not found" status.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.VERIFIER)
    public void getNonExistingComment() throws Exception {
        // get the comment
        restCommentMockMvc.perform(get("/api/comments/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests Comment updating.
     * <p>
     * This test saves a Comment object to the database,
     * then updates the values of its attributes and uses PUT to save the object.
     * Then it uses a mocked Advertiser user to
     * compare the original number of objects in the database to the new one
     * and the updated values of our modified Comment with the ones found in
     * the database.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER, username = DBUserMocker.ADVERTISER_USERNAME)
    public void updateComment() throws Exception {
        comment.author(DBUserMocker.ADVERTISER);

        // initialize the database
        commentService.save(comment);

        int databaseSizeBeforeUpdate = commentRepository.findAll().size();

        // update the comment
        Comment updatedComment = commentRepository.findOne(comment.getId());
        updatedComment
                .content(UPDATED_CONTENT)
                .date(UPDATED_DATE);

        restCommentMockMvc.perform(put("/api/comments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedComment)))
                .andExpect(status().isOk());

        // validate the Comment in the database
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(databaseSizeBeforeUpdate);
        Comment testComment = comments.get(comments.size() - 1);
        assertThat(testComment.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testComment.getDate()).isEqualTo(UPDATED_DATE);
    }

    /**
     * Tests Comment updating as an Admin.
     * <p>
     * This test saves a Comment object to the database
     * then it uses a mocked Admin user to
     * update the values of its attributes and uses PUT to save the object.
     * Then it compares the original number of objects in the database to the new one
     * and the updated values of our modified Comment with the ones found in
     * the database.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN, username = DBUserMocker.ADMIN_USERNAME)
    public void updateCommentAsAdmin() throws Exception {
        comment.author(DBUserMocker.ADVERTISER);

        // initialize the database
        commentService.save(comment);

        // update the comment
        Comment updatedComment = commentRepository.findOne(comment.getId());
        updatedComment
                .content(UPDATED_CONTENT)
                .date(UPDATED_DATE);

        int databaseSizeBeforeUpdate = commentRepository.findAll().size();

        restCommentMockMvc.perform(put("/api/comments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedComment)))
                .andExpect(status().isOk());

        // validate the Comment in the database
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(databaseSizeBeforeUpdate);
        Comment testComment = comments.get(comments.size() - 1);
        assertThat(testComment.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testComment.getDate()).isEqualTo(UPDATED_DATE);
    }

    /**
     * Tests Comment updating as someone other than the Comment's owner.
     * <p>
     * This test saves a Comment object to the database,
     * then updates the values of its attributes and uses PUT to save the object
     * using a mocked User with a different username to the Comment's owner,
     * which is not allowed. It then validates that the Comment's content and date
     * have not been changed on the database.
     * @throws Exception
     */
    @Test
    @Rollback
    @WithMockUser(authorities = AuthorityRoles.VERIFIER, username = "not_owner_username")
    public void updateCommentNotOwner() throws Exception {
        comment.author(DBUserMocker.VERIFIER);
        // initialize the database
        commentService.save(comment);

        int databaseSizeBeforeUpdate = commentRepository.findAll().size();

        // update the comment
        Comment updatedComment = commentRepository.findOne(comment.getId());
        updatedComment
                .content(UPDATED_CONTENT)
                .date(UPDATED_DATE);

        restCommentMockMvc.perform(put("/api/comments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedComment)))
                .andExpect(status().isBadRequest());

        // validate the Comment in the database
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(databaseSizeBeforeUpdate);
        Comment testComment = comments.get(comments.size() - 1);
        assertThat(testComment.getContent()).isNotEqualTo(UPDATED_CONTENT);
        assertThat(testComment.getDate()).isNotEqualTo(UPDATED_DATE);
    }

    /**
     * Tests Comment deletion as the Comment's author
     * <p>
     * This tests sets a Comment's author to a mocked Verifier
     * user that it then uses to delete it
     * on the database. It then asserts
     * that the number of objects on the database
     * after this action has been reduced by one.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.VERIFIER, username = DBUserMocker.VERIFIER_USERNAME)
    public void deleteCommentAsVerifier() throws Exception {
        comment.author(DBUserMocker.VERIFIER);

        // initialize the database
        commentService.save(comment);

        int databaseSizeBeforeDelete = commentRepository.findAll().size();

        // get the comment
        restCommentMockMvc.perform(delete("/api/comments/{id}", comment.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // validate the database is empty
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(databaseSizeBeforeDelete - 1);
    }

    /**
     * Tests Comment deletion as a Guest
     * <p>
     * This tests sets a Comment's author to a mocked Guest
     * user and then attempts to delete it on the database
     * with no authorization, which is not allowed.
     * It then asserts that the number of
     * Comments on the database has not
     * changed.
     * @throws Exception
     */
    @Test
    @Transactional
    public void deleteCommentAsGuest() throws Exception {
        comment.author(DBUserMocker.VERIFIER);

        // initialize the database
        commentService.save(comment);

        final int databaseSizeBeforeDelete = commentRepository.findAll().size();

        // get the comment
        restCommentMockMvc.perform(delete("/api/comments/{id}", comment.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isUnauthorized());

        // validate the database is empty
        final List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(databaseSizeBeforeDelete);
    }

    /**
     * Tests Comment deletion as Admin
     * <p>
     * This test uses a mocked Admin user
     * to delete an object on the database. It then asserts
     * that the number of objects on the database
     * after this action has been reduced by one.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN, username = DBUserMocker.ADMIN_USERNAME)
    public void deleteCommentAsAdmin() throws Exception {
        comment.author(DBUserMocker.VERIFIER);

        // initialize the database
        commentService.save(comment);
        commentRepository.flush();

        int databaseSizeBeforeDelete = commentRepository.findAll().size();

        // get the comment
        restCommentMockMvc.perform(delete("/api/comments/{id}", comment.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // validate the database is empty
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(databaseSizeBeforeDelete - 1);
    }

    /**
     * Tests Comment deletion as someone other than the Comment's author
     * <p>
     * This tests sets a Comment's author to a mocked Verifier
     * user and then uses another mocked Verifier user to attempt to delete
     * it on the database, which is not allowed. It then asserts that the
     * number of Comments on the database has not changed.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.VERIFIER, username = "not_owner")
    public void deleteCommentNotOwner() throws Exception {
        comment.author(DBUserMocker.VERIFIER);

        // initialize the database
        commentService.save(comment);

        int databaseSizeBeforeDelete = commentRepository.findAll().size();

        // get the comment
        restCommentMockMvc.perform(delete("/api/comments/{id}", comment.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());

        // validate the database is empty
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(databaseSizeBeforeDelete);
    }

    /**
     * Tests getting all Comments for an Announcement
     * <p>
     * This test sets an Announcement as a Comment's announcement,
     * then saves it to the database. It then then finds all Comments
     * tied to that Announcement on that database and asserts that
     * the number of Comments matches the expected number.
     * @throws Exception
     */
    @Test
    @Transactional
    public void getAllCommentsForAnnouncementAsGuest() throws Exception {
        // initialize the database
        long announcementId = 1L;
        long userId = 1L;

        comment.announcement(new Announcement().id(announcementId).author(new User().id(userId)));
        commentService.save(comment);

        final Long commentsCount = commentRepository.findByAnnouncement_Id(announcementId, CommentConstants.PAGEABLE).getTotalElements();

        // get the comment
        restCommentMockMvc.perform(get("/api//comments/announcement/{announcementId}", announcementId)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(Math.toIntExact(commentsCount))))
                .andExpect(jsonPath("$.[*].id").value(hasItem(comment.getId().intValue())))
                .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
                .andExpect(jsonPath("$.[*].date").value(hasItem((int) DEFAULT_DATE.getTime())));
    }

    /**
     * Tests getting all Comments for an Announcement that does not exist
     * <p>
     * Ths test attempts to retrieve all Comments tied to a non-existing Announcement
     * from the database, then asserts that the number of returned results equals zero.
     * @throws Exception
     */
    @Test
    @Transactional
    public void getAllCommentsForNonExistingAnnouncementAsGuest() throws Exception {
        // Initialize the database
        final Long announcementId = Long.MAX_VALUE;

        // Get the comment
        restCommentMockMvc.perform(get("/api/comments/announcement/{announcementId}", announcementId)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(Math.toIntExact(0))))
                .andExpect(status().isOk());
    }
}
