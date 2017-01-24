package rs.acs.uns.sw.e2e;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.boot.SpringApplication;
import rs.acs.uns.sw.e2e.tests.*;
import rs.acs.uns.sw.sct.SctServiceApplication;

import static rs.acs.uns.sw.e2e.util.Constants.PROFILE_NAME;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        SearchingTest.class,
        SigningTest.class,
        UserProfileTest.class,
        ReportingTest.class,
        AnnouncementTest.class,
        AddingAnnouncementTest.class,
        UpdatingAnnouncementTest.class,
        CompanyTest.class
})

public class E2ESuite {

    @BeforeClass
    public static void runApplicationInTestProfile() {
        SpringApplication app = new SpringApplication(SctServiceApplication.class);
        app.setAdditionalProfiles(PROFILE_NAME);
        app.run();
    }
}
