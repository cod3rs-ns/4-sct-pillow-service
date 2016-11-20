package rs.acs.uns.sw.sct.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import rs.acs.uns.sw.sct.announcements.AnnouncementServiceTest;
import rs.acs.uns.sw.sct.comments.CommentServiceTest;
import rs.acs.uns.sw.sct.companies.CompanyServiceTest;
import rs.acs.uns.sw.sct.marks.MarkServiceTest;
import rs.acs.uns.sw.sct.realestates.RealEstateServiceTest;
import rs.acs.uns.sw.sct.reports.ReportServiceTest;
import rs.acs.uns.sw.sct.users.UserServiceTest;

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
