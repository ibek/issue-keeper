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
import qa.tools.ikeeper.test.base.JiraTestBase;

import java.util.ArrayList;
import java.util.List;

public class JiraRegexpConstraintsTest {

    private static final List<String> executed = new ArrayList<String>();

    @Rule
    public TestRule issueKeeper;

    public JiraRegexpConstraintsTest() {
        issueKeeper = new IKeeperJUnitConnector(new QueryInterceptor(), new JiraClient("https://issues.jboss.org", "KEY=${id} AND STATUS != CLOSED AND STATUS != RESOLVED"));
    }

    @AfterClass
    public static void checkExecutions() {
        Assertions.assertThat(executed).hasSize(1);
        Assertions.assertThat(executed).contains("runNewIssueOnOracleDBTest");

    }

    @Jira("JBPM-3558")
    @Test
    public void ignoreNewIssueTest() {
        executed.add("ignoreNewIssueTest");
        System.out.println("ignoreNewIssueTest");
    }

    @Jira("JBPM-3067")
    @Test
    public void runNewIssueOnOracleDBTest() {
        executed.add("runNewIssueOnOracleDBTest");
    }

}
