package rs.acs.uns.sw.sct.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import rs.acs.uns.sw.sct.announcements.AnnouncementControllerTest;
import rs.acs.uns.sw.sct.comments.CommentControllerTest;
import rs.acs.uns.sw.sct.companies.CompanyControllerTest;
import rs.acs.uns.sw.sct.marks.MarkControllerTest;
import rs.acs.uns.sw.sct.realestates.RealEstateControllerTest;
import rs.acs.uns.sw.sct.reports.ReportControllerTest;
import rs.acs.uns.sw.sct.users.UserControllerTest;

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
