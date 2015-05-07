package qa.tools.ikeeper.test;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.Test;

import qa.tools.ikeeper.annotation.BZ;
import qa.tools.ikeeper.test.base.BZTestBase;

public class BZConstraintsTest extends BZTestBase {
    
    private static final List<String> executed = new ArrayList<String>();
    
    @AfterClass
    public static void checkExecutions() {
        Assertions.assertThat(executed).contains("runVerifiedIssueInEnvTest", "runMultipleVerifiedIssuesInEnvTest", "runNewIssueOutEnvTest");
    }

    @BZ("1145046")
    @Test
    public void runVerifiedIssueInEnvTest() {
        executed.add("runVerifiedIssueInEnvTest");
    }

    @BZ({"1145046", "1107757"})
    @Test
    public void runMultipleVerifiedIssuesInEnvTest() {
        executed.add("runMultipleVerifiedIssuesInEnvTest");
    }

    @BZ("1217371")
    @Test
    public void ignoreNewIssueInEnvTest() {
        executed.add("ignoreNewIssueInEnvTest");
        System.out.println("ignoreNewIssueInEnvTest");
    }

    @BZ("844278")
    @Test
    public void runNewIssueOutEnvTest() {
        executed.add("runNewIssueOutEnvTest");
    }

    @BZ({"1217371", "1107757"})
    @Test
    public void ignoreNewAndVerifiedIssuesInEnvTest() {
        executed.add("ignoreNewAndVerifiedIssuesInEnvTest");
        System.out.println("ignoreNewAndVerifiedIssuesInEnvTest");
    }
    
}
