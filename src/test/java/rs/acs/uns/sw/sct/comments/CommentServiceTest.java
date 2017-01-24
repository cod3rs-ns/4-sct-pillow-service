package rs.acs.uns.sw.sct.comments;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import rs.acs.uns.sw.sct.SctServiceApplication;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static rs.acs.uns.sw.sct.constants.CommentConstants.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SctServiceApplication.class)
@ActiveProfiles("test")
public class CommentServiceTest {
    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    private Comment newComment;
    private Comment updatedComment;
    private Comment existingComment;

    /**
     * Asserts equality of two Comments.
     *
     * @param comm1 One of the Comments to be compared
     * @param comm2 The other Comments to be compared
     */
    private void compareComments(Comment comm1, Comment comm2) {
        if (comm1.getId() != null && comm2.getId() != null)
            assertThat(comm1.getId()).isEqualTo(comm2.getId());
        assertThat(comm1.getContent()).isEqualTo(comm2.getContent());
        assertThat(comm1.getDate()).isEqualTo(comm2.getDate());
        assertThat(comm1.getAnnouncement().getId()).isEqualTo(comm2.getAnnouncement().getId());
        assertThat(comm1.getAuthor().getId()).isEqualTo(comm2.getAuthor().getId());
    }

    /**
     * Initializes all objects required for testing
     */
    @Before
    public void initTest() {
        existingComment = new Comment()
                .id(ID)
                .content(CONTENT)
                .date(DATE)
                .announcement(ANNOUNCEMENT)
                .author(AUTHOR);
        newComment = new Comment()
                .id(null)
                .content(NEW_CONTENT)
                .date(NEW_DATE)
                .announcement(NEW_ANNOUNCEMENT)
                .author(NEW_AUTHOR);
        updatedComment = new Comment()
                .id(null)
                .content(UPDATED_CONTENT)
                .date(UPDATED_DATE)
                .announcement(UPDATED_ANNOUNCEMENT)
                .author(UPDATED_AUTHOR);
    }

    /**
     * Tests pageable retrieval of Comments
     * <p>
     * This test uses a PageRequest object to specify the number
     * of results it wants to receive when it requests Comments,
     * then asserts that the number of returned results matches
     * the page size in our request.
     */
    @Test
    public void testFindAllPageable() {
        PageRequest pageRequest = new PageRequest(0, PAGE_SIZE);
        Page<Comment> comments = commentRepository.findAll(pageRequest);
        assertThat(comments).hasSize(PAGE_SIZE);
    }

    /**
     * Tests retrieval of all Comments
     * <p>
     * This test finds all Comments on the repository and asserts
     * that the number of returned results is equal to the number of
     * Comments on the database
     */
    @Test
    public void testFindAll() {
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(DB_COUNT_COMMENTS);
    }

    /**
     * Tests retrieval of a single Comment.
     * <p>
     * This test uses the id of an Comment that is in the repository
     * to search for it, then asserts that the returned value is not null
     * and compares the returned Comment to an existing Comment.
     */
    @Test
    public void testFindOne() {
        Comment comment = commentService.findOne(ID);
        assertThat(comment).isNotNull();

        compareComments(comment, existingComment);
    }

    /**
     * Tests addition of Comments
     * <p>
     * This announcement saves a new Comment using the CommentService,
     * then it finds all Comments and asserts that the size of the results
     * has increased by one. It also asserts that the new Comment that is on
     * the database equals the Comment we added.
     */
    @Test
    @Transactional
    public void testAdd() {
        int dbSizeBeforeAdd = commentRepository.findAll().size();

        Comment dbComment = commentService.save(newComment);
        assertThat(dbComment).isNotNull();

        // Validate that new comment is in the database
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(dbSizeBeforeAdd + 1);

        compareComments(dbComment, newComment);
    }

    /**
     * Tests updating of Comments.
     * <p>
     * This test retrieves a Comment using the service, then changes
     * its attributes and saves it to the database. Then it asserts that
     * the object on the database is not null and equals our updated Comment.
     */
    @Test
    @Transactional
    public void testUpdate() {
        Comment dbComment = commentService.findOne(ID);

        dbComment.setAuthor(UPDATED_AUTHOR);
        dbComment.setAnnouncement(UPDATED_ANNOUNCEMENT);
        dbComment.setContent(UPDATED_CONTENT);
        dbComment.setDate(UPDATED_DATE);

        Comment updatedDbComment = commentService.save(dbComment);
        assertThat(updatedDbComment).isNotNull();

        compareComments(updatedDbComment, updatedComment);
    }

    /**
     * Tests removal of Comments
     * <p>
     * This test deletes a Comment using the service, then
     * asserts that the number of Comments on the database
     * has been reduced by one. It also asserts that an object
     * with the deleted Comment's id does not exists on the
     * database.
     */
    @Test
    @Transactional
    public void testRemove() {
        int dbSizeBeforeRemove = commentRepository.findAll().size();
        commentService.delete(REMOVE_ID);

        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(dbSizeBeforeRemove - 1);

        Comment dbComment = commentService.findOne(REMOVE_ID);
        assertThat(dbComment).isNull();
    }


    /*
     * Negative tests
	 */

    /**
     * Tests adding a Comment with a null content value
     * <p>
     * This test sets a Comment's content to null, then
     * attempts to add it to the database. As content is a
     * non-nullable field, the test receives a
     * Constraint Violation exception.
     */
    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullContent() {
        newComment.setContent(null);
        commentService.save(newComment);
        // rollback previous content
        newComment.setContent(NEW_CONTENT);
    }

    /**
     * Tests adding a Comment with a null date value
     * <p>
     * This test sets a Comment's date to null, then
     * attempts to add it to the database. As date is a
     * non-nullable field, the test receives a
     * Constraint Violation exception.
     */
    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullDate() {
        newComment.setDate(null);
        commentService.save(newComment);
        // rollback previous date
        newComment.setDate(NEW_DATE);
    }

    /**
     * Tests searching for Comments by their Announcement's id
     * <p>
     * This test finds all Comments tied to an Announcement's id,
     * then asserts that the number of returned results matches
     * the expected number and asserts that every one of the
     * results matches the Comment that is retrieved by searching
     * for its id.
     */
    @Test
    @Transactional
    public void testFindAllByAnnouncementId() {
        final Page<Comment> comments = commentService.findAllByAnnouncement(COMMENTED_ANNOUNCEMENT_ID, PAGEABLE);

        assertThat(comments).hasSize(COMMENTED_ANNOUNCEMENT_RECORDS);

        for (final Comment comment: comments) {
            assertThat(comment.getAnnouncement().getId()).isEqualTo(COMMENTED_ANNOUNCEMENT_ID);
        }
    }
}
