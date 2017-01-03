package rs.acs.uns.sw.e2e;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import rs.acs.uns.sw.e2e.tests.SigningTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        SigningTest.class
})
public class E2ESuite {
}
