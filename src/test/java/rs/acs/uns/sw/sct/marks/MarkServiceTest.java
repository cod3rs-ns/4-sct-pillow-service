package rs.acs.uns.sw.sct.marks;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import rs.acs.uns.sw.sct.SctServiceApplication;
import rs.acs.uns.sw.sct.users.User;
import rs.acs.uns.sw.sct.users.UserService;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static rs.acs.uns.sw.sct.constants.MarkConstants.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SctServiceApplication.class)
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

    /**
     * Asserts equality of two Marks.
     *
     * @param mark1 One of the Marks to be compared
     * @param mark2 The other Marks to be compared
     */
    private void compareMarks(Mark mark1, Mark mark2) {
        if (mark1.getId() != null && mark2.getId() != null)
            assertThat(mark1.getId()).isEqualTo(mark2.getId());
        assertThat(mark1.getValue()).isEqualTo(mark2.getValue());
        assertThat(mark1.getGrader().getId()).isEqualTo(mark2.getGrader().getId());
        if (mark1.getGradedAnnouncer() != null && mark2.getGradedAnnouncer() != null)
            assertThat(mark1.getGradedAnnouncer().getId()).isEqualTo(mark2.getGradedAnnouncer().getId());
        if (mark1.getAnnouncement() != null && mark2.getAnnouncement() != null)
            assertThat(mark1.getAnnouncement().getId()).isEqualTo(mark2.getAnnouncement().getId());
    }

    /**
     * Initializes all objects required for testing
     */
    @Before
    public void initTest() {
        User grader = userService.findOne(GRADER_ID);
        User newGrader = userService.findOne(NEW_GRADER_ID);
        User updatedGrader = userService.findOne(UPDATED_GRADER_ID);

        existingMark = new Mark().id(ID).value(VALUE).grader(grader).gradedAnnouncer(GRADED_ANNOUNCER).announcement(ANNOUNCEMENT);
        newMark = new Mark().id(null).value(NEW_VALUE).grader(newGrader).gradedAnnouncer(NEW_GRADED_ANNOUNCER).announcement(NEW_ANNOUNCEMENT);
        updatedMark = new Mark().id(null).value(UPDATED_VALUE).grader(updatedGrader).gradedAnnouncer(UPDATED_GRADED_ANNOUNCER).announcement(NEW_ANNOUNCEMENT);
    }

    /**
     * Tests pageable retrieval of Marks
     * <p>
     * This test uses a PageRequest object to specify the number
     * of results it wants to receive when it requests Marks,
     * then asserts that the number of returned results matches
     * the page size in our request.
     */
    @Test
    public void testFindAllPageable() {
        PageRequest pageRequest = new PageRequest(0, PAGE_SIZE);
        Page<Mark> marks = markRepository.findAll(pageRequest);
        assertThat(marks).hasSize(PAGE_SIZE);
    }

    /**
     * Tests retrieval of all Marks
     * <p>
     * This test finds all Marks on the repository and asserts
     * that the number of returned results is equal to the number of
     * Marks on the database
     */
    @Test
    public void testFindAll() {
        List<Mark> marks = markRepository.findAll();
        assertThat(marks).hasSize(DB_COUNT_MARKS);
    }

    /**
     * Tests retrieval of a single Mark.
     * <p>
     * This test uses the id of an Mark that is in the repository
     * to search for it, then asserts that the returned value is not null
     * and compares the returned Mark to an existing Mark.
     */
    @Test
    public void testFindOne() {
        Mark mark = markService.findOne(ID);
        assertThat(mark).isNotNull();

        compareMarks(mark, existingMark);
    }

    /**
     * Tests addition of Marks
     * <p>
     * This announcement saves a new Mark using the MarkService,
     * then it finds all Marks and asserts that the size of the results
     * has increased by one. It also asserts that the new Mark that is on
     * the database equals the Mark we added.
     */
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

    /**
     * Tests updating of Marks.
     * <p>
     * This test retrieves a Mark using the service, then changes
     * its attributes and saves it to the database. Then it asserts that
     * the object on the database is not null and equals our updated Mark.
     */
    @Test
    @Transactional
    public void testUpdate() {
        Mark dbMark = markService.findOne(ID);

        User UPDATED_GRADER = userService.findOne(UPDATED_GRADER_ID);

        dbMark.setGrader(UPDATED_GRADER);
        dbMark.setGradedAnnouncer(UPDATED_GRADED_ANNOUNCER);
        dbMark.setAnnouncement(UPDATED_ANNOUNCEMENT);
        dbMark.setValue(UPDATED_VALUE);

        Mark updatedDbMark = markService.save(dbMark);
        assertThat(updatedDbMark).isNotNull();

        compareMarks(updatedDbMark, updatedMark);
    }

    /**
     * Tests removal of Marks
     * <p>
     * This test deletes a Mark using the service, then
     * asserts that the number of Marks on the database
     * has been reduced by one. It also asserts that an object
     * with the deleted Mark's id does not exists on the
     * database.
     */
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

    /**
     * Tests adding a Mark with a null value value
     * <p>
     * This test sets a Mark's value to null, then
     * attempts to add it to the database. As value is a
     * non-nullable field, the test receives a
     * Constraint Violation exception.
     */
    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullValue() {
        newMark.setValue(null);
        markService.save(newMark);
        // rollback previous value
        newMark.setValue(NEW_VALUE);
    }

    /**
     * Tests searching for Marks by their Announcement's id
     * <p>
     * This test finds all Marks tied to a Announcement's id,
     * then asserts that the number of returned results matches
     * the expected number and asserts that every one of the
     * results matches the Mark that is retrieved by searching
     * for its id.
     */
    @Test
    @Transactional
    public void testFindAllByAnnouncementId() {
        final Page<Mark> marks = markService.findAllByAnnouncement(MARKED_ANNOUNCEMENT_ID, PAGEABLE);

        assertThat(marks).hasSize(MARKED_ANNOUNCEMENT_RECORDS);

        for (final Mark mark: marks) {
            assertThat(mark.getAnnouncement().getId()).isEqualTo(MARKED_ANNOUNCEMENT_ID);
        }
    }
}
