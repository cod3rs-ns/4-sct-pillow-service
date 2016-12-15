package rs.acs.uns.sw.sct.util;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;


public class ContainsIgnoreCase extends TypeSafeMatcher<String> {

    private String actual;
    private String expecting;

    public ContainsIgnoreCase(String expecting) {
        this.expecting = expecting;
    }

    @Override
    protected boolean matchesSafely(String actual) {
        this.actual = actual;
        return actual.toLowerCase().contains(expecting.toLowerCase());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("\nExpecting:").appendText("\n  <\"" + actual + "\">").appendText("\nto contain:").appendText("\n  <\"" + expecting + "\">").appendText("\nignoring case");
    }

    public static ContainsIgnoreCase containsIgnoringCase(String expecting) {
        return new ContainsIgnoreCase(expecting);
    }
}
