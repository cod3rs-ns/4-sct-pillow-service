package rs.acs.uns.sw.awt_test.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import rs.acs.uns.sw.awt_test.announcements.AnnouncementServiceTest;
import rs.acs.uns.sw.awt_test.comments.CommentServiceTest;
import rs.acs.uns.sw.awt_test.companies.CompanyServiceTest;
import rs.acs.uns.sw.awt_test.marks.MarkServiceTest;
import rs.acs.uns.sw.awt_test.real_estates.RealEstateServiceTest;
import rs.acs.uns.sw.awt_test.reports.ReportServiceTest;
import rs.acs.uns.sw.awt_test.users.UserServiceTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AnnouncementServiceTest.class,
        CommentServiceTest.class,
        CompanyServiceTest.class,
        MarkServiceTest.class,
        RealEstateServiceTest.class,
        ReportServiceTest.class,
        UserServiceTest.class
})
public class IntegrationServiceSuite {
}
