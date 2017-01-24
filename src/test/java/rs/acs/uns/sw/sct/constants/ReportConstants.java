package rs.acs.uns.sw.sct.constants;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import rs.acs.uns.sw.sct.announcements.Announcement;
import rs.acs.uns.sw.sct.users.User;

public interface ReportConstants {
    int PAGE_SIZE = 1;
    int DB_COUNT_REPORTS = 5;

    Long ID = 1L;

    String TYPE = "admin";
    String CONTENT = "Inappropriate content";
    String STATUS = "accepted";
    User REPORTER = new User().id(6L);
    String EMAIL = "user6@mail.com";

    String NEW_TYPE = "system";
    String NEW_CONTENT = "Repeated advertisement";
    String NEW_STATUS = "resolved";
    User NEW_REPORTER = new User().id(5L);
    String NEW_EMAIL = "user5@mail.com";

    String UPDATED_TYPE = "admin";
    String UPDATED_CONTENT = "Wrong details";
    String UPDATED_STATUS = "inactive";
    User UPDATED_REPORTER = new User().id(4L);
    String UPDATED_EMAIL = "user4@mail.com";

    Announcement DEFAULT_ANNOUNCEMENT = new Announcement().id(2L);

    // 1st page with 10 elements
    Pageable PAGEABLE = new PageRequest(0, 10);

    // Status of
    String FIND_STATUS = "pending";

    // Status of
    String FIND_AUTHOR_EMAIL = "user6@mail.com";
}