package rs.acs.uns.sw.awt_test.comments;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import rs.acs.uns.sw.awt_test.AwtTestSiitProject2016ApplicationTests;

import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static rs.acs.uns.sw.awt_test.constants.CommentConstants.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AwtTestSiitProject2016ApplicationTests.class)
public class CommentServiceTest {
    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    private Comment newComment;
    private Comment updatedComment;
    private Comment existingComment;


    private void compareComments(Comment comm1, Comment comm2){
        if (comm1.getId() != null && comm2.getId() != null)
            assertThat(comm1.getId()).isEqualTo(comm2.getId());
        assertThat(comm1.getContent()).isEqualTo(comm2.getContent());
        assertThat(comm1.getDate()).isEqualTo(comm2.getDate());
        assertThat(comm1.getAnnouncement().getId()).isEqualTo(comm2.getAnnouncement().getId());
        assertThat(comm1.getAuthor().getId()).isEqualTo(comm2.getAuthor().getId());
    }


    @Before
    public void initTest() {
        existingComment = new Comment(ID, CONTENT, DATE, ANNOUNCEMENT, AUTHOR);
        newComment = new Comment(null, NEW_CONTENT, NEW_DATE, NEW_ANNOUNCEMENT, NEW_AUTHOR);
        updatedComment = new Comment(null, UPDATED_CONTENT, UPDATED_DATE, UPDATED_ANNOUNCEMENT, UPDATED_AUTHOR);
    }

    @Test
    public void testFindAllPageable() {
        PageRequest pageRequest = new PageRequest(0, PAGE_SIZE);
        Page<Comment> comments = commentRepository.findAll(pageRequest);
        assertThat(comments).hasSize(PAGE_SIZE);
    }

    @Test
    public void testFindAll() {
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(DB_COUNT_COMMENTS);
    }

    @Test
    public void testFindOne(){
        Comment comment = commentService.findOne(ID);
        assertThat(comment).isNotNull();

        compareComments(comment, existingComment);
    }

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
}
