package qa.tools.ikeeper.test;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.Test;

import qa.tools.ikeeper.annotation.Jira;
import qa.tools.ikeeper.test.base.JiraTestBase;

public class JiraRegexpConstraintsTest extends JiraTestBase {

    private static final List<String> executed = new ArrayList<String>();

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
