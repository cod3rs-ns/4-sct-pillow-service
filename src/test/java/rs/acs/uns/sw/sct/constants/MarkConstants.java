package rs.acs.uns.sw.sct.constants;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import rs.acs.uns.sw.sct.announcements.Announcement;
import rs.acs.uns.sw.sct.users.User;

public interface MarkConstants {
    int PAGE_SIZE = 2;
    int DB_COUNT_MARKS = 3;

    Long ID = 1L;
    Long REMOVE_ID = 2L;

    Integer VALUE = 4;
    Long GRADER_ID = 3L;
    User GRADED_ANNOUNCER = new User().id(1L);
    Announcement ANNOUNCEMENT = null;

    Integer NEW_VALUE = 3;
    Long NEW_GRADER_ID = 1L;
    User NEW_GRADED_ANNOUNCER = null;
    Announcement NEW_ANNOUNCEMENT = new Announcement().id(2L);

    Integer UPDATED_VALUE = 2;
    Long UPDATED_GRADER_ID = 4L;
    User UPDATED_GRADED_ANNOUNCER = null;
    Announcement UPDATED_ANNOUNCEMENT = new Announcement().id(2L);

    Pageable PAGEABLE = new PageRequest(0, 10);

    Long MARKED_ANNOUNCEMENT_ID = 2L;
    Integer MARKED_ANNOUNCEMENT_RECORDS = 3;
}
