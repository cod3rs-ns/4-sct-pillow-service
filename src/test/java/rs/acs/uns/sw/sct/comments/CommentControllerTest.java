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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import rs.acs.uns.sw.sct.SctServiceApplication;
import rs.acs.uns.sw.sct.announcements.Announcement;
import rs.acs.uns.sw.sct.constants.CommentConstants;
import rs.acs.uns.sw.sct.users.UserService;
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
public class CommentControllerTest {

    private static final String DEFAULT_CONTENT = "CONTENT_AAA";
    private static final String UPDATED_CONTENT = "CONTENT_BBB";

    private static final Date DEFAULT_DATE = DateUtil.asDate(LocalDate.ofEpochDay(0L));
    private static final Date UPDATED_DATE = DateUtil.asDate(LocalDate.now(ZoneId.systemDefault()));

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CommentService commentService;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private UserService userService;

    private MockMvc restCommentMockMvc;

    private Comment comment;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Comment createEntity() {
        return new Comment()
                .content(DEFAULT_CONTENT)
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

    @Before
    public void initTest() {
        comment = createEntity();
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN)
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

    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = commentRepository.findAll().size();
        // set the field null
        comment.setDate(null);

        // create the Comment, which fails.

        restCommentMockMvc.perform(post("/api/comments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(comment)))
                .andExpect(status().isBadRequest());

        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(databaseSizeBeforeTest);
    }

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

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void getAllCommentsAsAdvertiser() throws Exception {
        // Get all the comments
        restCommentMockMvc.perform(get("/api/comments?sort=id,desc"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    public void getAllCommentsAsGuest() throws Exception {
        // get all the comments
        restCommentMockMvc.perform(get("/api/comments?sort=id,desc"))
                .andExpect(status().isUnauthorized());
    }

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

    @Test
    @Transactional
    public void getCommentAsGuest() throws Exception {
        // initialize the database
        commentRepository.saveAndFlush(comment);

        // Get the comment
        restCommentMockMvc.perform(get("/api/comments/{id}", comment.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.VERIFIER)
    public void getNonExistingComment() throws Exception {
        // get the comment
        restCommentMockMvc.perform(get("/api/comments/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

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

    @Test
    @Transactional
    public void getAllCommentsForAnnouncementAsGuest() throws Exception {
        // initialize the database
        long announcementId = 1L;

        comment.announcement(new Announcement().id(announcementId));
        commentService.save(comment);

        final Long commentsCount = commentRepository.findByAnnouncement_Id(announcementId, CommentConstants.PAGEABLE).getTotalElements();

        // get the comment
        restCommentMockMvc.perform(get("/api//comments/announcement/{announcementId}", announcementId)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(Math.toIntExact(commentsCount))))
                .andExpect(jsonPath("$.[*].id").value(comment.getId().intValue()))
                .andExpect(jsonPath("$.[*].content").value(DEFAULT_CONTENT))
                .andExpect(jsonPath("$.[*].date").value((int) DEFAULT_DATE.getTime()));
    }

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
