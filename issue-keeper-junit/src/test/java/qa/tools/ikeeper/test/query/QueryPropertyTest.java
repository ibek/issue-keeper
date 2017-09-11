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

public class QueryPropertyTest {
    private static final List<String> executed = new ArrayList<String>();

    @Rule
    public TestRule issueKeeper;

    public QueryPropertyTest() {
        System.setProperty("IK.status", "STATUS != RESOLVED");
        issueKeeper = new IKeeperJUnitConnector(new QueryInterceptor(), new JiraClient("https://issues.jboss.org", "KEY=${id} AND ${IK.status}"));
    }

    @AfterClass
    public static void checkExecutions() {
        Assertions.assertThat(executed).hasSize(1);
        Assertions.assertThat(executed).contains("runVerifiedIssueTest");
    }

    @Jira("JBPM-4608")
    @Test
    public void runVerifiedIssueTest() {
        executed.add("runVerifiedIssueTest");
    }

    @Jira("JBPM-4198")
    @Test
    public void ignoreNewIssueTest() {
        executed.add("ignoreNewIssueTest");
        System.out.println("ignoreNewIssueTest");
    }
}
