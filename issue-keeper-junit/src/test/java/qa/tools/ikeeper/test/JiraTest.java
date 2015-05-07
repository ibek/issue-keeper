package qa.tools.ikeeper.test;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.Test;

import qa.tools.ikeeper.annotation.Jira;
import qa.tools.ikeeper.test.base.JiraTestBase;

public class JiraTest extends JiraTestBase {
    
    private static final List<String> executed = new ArrayList<String>();
    
    @AfterClass
    public static void checkExecutions() {
        Assertions.assertThat(executed).hasSize(2);
        Assertions.assertThat(executed).contains("runVerifiedIssueTest", "runMultipleVerifiedIssuesTest");
    }

    @Jira("JBPM-4608")
    @Test
    public void runVerifiedIssueTest() {
        executed.add("runVerifiedIssueTest");
    }

    @Jira({"JBPM-4608", "JBPM-4607"})
    @Test
    public void runMultipleVerifiedIssuesTest() {
        executed.add("runMultipleVerifiedIssuesTest");
    }

    @Jira("JBPM-4198")
    @Test
    public void ignoreNewIssueTest() {
        executed.add("ignoreNewIssueTest");
        System.out.println("ignoreNewIssueTest");
    }

    @Jira({"JBPM-4198", "JBPM-4608"})
    @Test
    public void ignoreNewAndVerifiedIssuesTest() {
        executed.add("ignoreNewAndVerifiedIssuesTest");
        System.out.println("ignoreNewAndVerifiedIssuesTest");
    }
    
}
