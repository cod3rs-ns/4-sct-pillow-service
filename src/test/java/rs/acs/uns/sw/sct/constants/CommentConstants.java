package rs.acs.uns.sw.sct.constants;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import rs.acs.uns.sw.sct.announcements.Announcement;
import rs.acs.uns.sw.sct.users.User;
import rs.acs.uns.sw.sct.util.DateUtil;

import java.util.Date;

public interface CommentConstants {

    int PAGE_SIZE = 2;
    int DB_COUNT_COMMENTS = 3;

    Long REMOVE_ID = 2L;

    Long ID = 1L;
    String CONTENT = "Another one comment";
    Date DATE = DateUtil.date("05-05-2016");
    Announcement ANNOUNCEMENT = new Announcement().id(2L);
    User AUTHOR = new User().id(5L);

    String NEW_CONTENT = "New comment";
    Date NEW_DATE = DateUtil.date("01-05-2016");
    Announcement NEW_ANNOUNCEMENT = new Announcement().id(2L);
    User NEW_AUTHOR = new User().id(4L);

    String UPDATED_CONTENT = "Updated comment";
    Date UPDATED_DATE = DateUtil.date("05-10-2017");
    Announcement UPDATED_ANNOUNCEMENT = new Announcement().id(2L);
    User UPDATED_AUTHOR = new User().id(1L);

    Pageable PAGEABLE = new PageRequest(0, 10);

    Long COMMENTED_ANNOUNCEMENT_ID = 2L;
    Integer COMMENTED_ANNOUNCEMENT_RECORDS = 3;
}
