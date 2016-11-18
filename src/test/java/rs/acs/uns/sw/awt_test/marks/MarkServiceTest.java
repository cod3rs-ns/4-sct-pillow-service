package rs.acs.uns.sw.awt_test.marks;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import rs.acs.uns.sw.awt_test.AwtTestSiitProject2016ApplicationTests;
import rs.acs.uns.sw.awt_test.users.User;
import rs.acs.uns.sw.awt_test.users.UserService;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static rs.acs.uns.sw.awt_test.constants.MarkConstants.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AwtTestSiitProject2016ApplicationTests.class)
public class MarkServiceTest {
    @Autowired
    private MarkService markService;

    @Autowired
    private UserService userService;

    @Autowired
    private MarkRepository markRepository;

    private Mark newMark;
    private Mark updatedMark;
    private Mark existingMark;


    private void compareMarks(Mark mark1, Mark mark2){
        if (mark1.getId() != null && mark2.getId() != null)
            assertThat(mark1.getId()).isEqualTo(mark2.getId());
        assertThat(mark1.getValue()).isEqualTo(mark2.getValue());
        assertThat(mark1.getGrader().getId()).isEqualTo(mark2.getGrader().getId());
        if (mark1.getGraded_announcer() != null && mark2.getGraded_announcer() != null)
            assertThat(mark1.getGraded_announcer().getId()).isEqualTo(mark2.getGraded_announcer().getId());
        if (mark1.getAnnouncement() != null && mark2.getAnnouncement() != null)
            assertThat(mark1.getAnnouncement().getId()).isEqualTo(mark2.getAnnouncement().getId());
    }


    @Before
    public void initTest() {
        User GRADER = userService.findOne(GRADER_ID);
        User NEW_GRADER = userService.findOne(NEW_GRADER_ID);
        User UPDATED_GRADER = userService.findOne(UPDATED_GRADER_ID);

        existingMark = new Mark(ID, VALUE, GRADER, GRADED_ANNOUNCER, ANNOUNCEMENT);
        newMark =  new Mark(null, NEW_VALUE, NEW_GRADER, NEW_GRADED_ANNOUNCER, NEW_ANNOUNCEMENT);
        updatedMark = new Mark(null, UPDATED_VALUE, UPDATED_GRADER, UPDATED_GRADED_ANNOUNCER, UPDATED_ANNOUNCEMENT);
    }

    @Test
    public void testFindAllPageable() {
        PageRequest pageRequest = new PageRequest(0, PAGE_SIZE);
        Page<Mark> marks = markRepository.findAll(pageRequest);
        assertThat(marks).hasSize(PAGE_SIZE);
    }

    @Test
    public void testFindAll() {
        List<Mark> marks = markRepository.findAll();
        assertThat(marks).hasSize(DB_COUNT_MARKS);
    }

    @Test
    public void testFindOne(){
        Mark mark = markService.findOne(ID);
        assertThat(mark).isNotNull();

        compareMarks(mark, existingMark);
    }

    @Test
    @Transactional
    public void testAdd() {
        int dbSizeBeforeAdd = markRepository.findAll().size();

        Mark dbMark = markService.save(newMark);
        assertThat(dbMark).isNotNull();

        // Validate that new mark is in the database
        List<Mark> marks = markRepository.findAll();
        assertThat(marks).hasSize(dbSizeBeforeAdd + 1);

        compareMarks(dbMark, newMark);
    }

    @Test
    @Transactional
    public void testUpdate() {
        Mark dbMark = markService.findOne(ID);

        User UPDATED_GRADER = userService.findOne(UPDATED_GRADER_ID);

        dbMark.setGrader(UPDATED_GRADER);
        dbMark.setGraded_announcer(UPDATED_GRADED_ANNOUNCER);
        dbMark.setAnnouncement(UPDATED_ANNOUNCEMENT);
        dbMark.setValue(UPDATED_VALUE);

        Mark updatedDbMark = markService.save(dbMark);
        assertThat(updatedDbMark).isNotNull();

        compareMarks(updatedDbMark, updatedMark);
    }

    @Test
    @Transactional
    public void testRemove() {
        int dbSizeBeforeRemove = markRepository.findAll().size();
        markService.delete(REMOVE_ID);

        List<Mark> marks = markRepository.findAll();
        assertThat(marks).hasSize(dbSizeBeforeRemove - 1);

        Mark dbMark = markService.findOne(REMOVE_ID);
        assertThat(dbMark).isNull();
    }

    /*
	 * Negative tests
	 */

    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullValue() {
        newMark.setValue(null);
        markService.save(newMark);
        // rollback previous value
        newMark.setValue(NEW_VALUE);
    }
}
