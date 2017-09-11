package qa.tools.ikeeper.test.query;


import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import qa.tools.ikeeper.annotation.Jira;
import qa.tools.ikeeper.client.JiraClient;
import qa.tools.ikeeper.interceptor.QueryInterceptor;
import qa.tools.ikeeper.test.IKeeperJUnitConnector;

import java.util.ArrayList;
import java.util.List;

public class JiraTest {

    @Rule
    public TestRule issueKeeper;

    public JiraTest() {
        issueKeeper = new IKeeperJUnitConnector(new QueryInterceptor(), new JiraClient("https://issues.jboss.org", "KEY=${id} AND STATUS != CLOSED AND STATUS != RESOLVED"));
    }

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
