package qa.tools.ikeeper.test;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import qa.tools.ikeeper.annotation.BZ;
import qa.tools.ikeeper.client.BugzillaClient;

@BZ("1217371")
public class BZBeforeClassTest {
    
    @Rule
    @ClassRule
    public static IKeeperJUnitConnector issueKeeper;
    
    static {
        issueKeeper = new IKeeperJUnitConnector(new BugzillaClient("https://bugzilla.redhat.com"));
        // set environment properties
    }
    
    private static final List<String> executed = new ArrayList<String>();
    
    @BeforeClass
    public static void failBeforeClass() {
        Assert.fail("All tests should be skipped, beforeClass method should not be called.");
    }
    
    @AfterClass
    public static void checkExecutions() {
        Assertions.assertThat(executed).hasSize(0);
    }

    @Test
    public void ignoreNewIssueTest() {
        executed.add("ignoreNewIssueTest");
        System.out.println("ignoreNewIssueTest");
    }
    
}
