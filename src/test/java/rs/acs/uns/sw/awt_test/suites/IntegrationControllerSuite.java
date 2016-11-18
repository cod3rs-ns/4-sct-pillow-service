package rs.acs.uns.sw.awt_test.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import rs.acs.uns.sw.awt_test.announcements.AnnouncementControllerTest;
import rs.acs.uns.sw.awt_test.comments.CommentControllerTest;
import rs.acs.uns.sw.awt_test.companies.CompanyControllerTest;
import rs.acs.uns.sw.awt_test.marks.MarkControllerTest;
import rs.acs.uns.sw.awt_test.real_estates.RealEstateControllerTest;
import rs.acs.uns.sw.awt_test.reports.ReportControllerTest;
import rs.acs.uns.sw.awt_test.users.UserControllerTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AnnouncementControllerTest.class,
        CommentControllerTest.class,
        CompanyControllerTest.class,
        MarkControllerTest.class,
        RealEstateControllerTest.class,
        ReportControllerTest.class,
        UserControllerTest.class
})
public class IntegrationControllerSuite {
}
