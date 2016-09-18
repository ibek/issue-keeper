package qa.tools.ikeeper.test;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import qa.tools.ikeeper.annotation.BZ;
import qa.tools.ikeeper.annotation.Jira;
import qa.tools.ikeeper.client.BugzillaClient;
import qa.tools.ikeeper.client.JiraClient;

public class MultipleClientsTest {

    private static final List<String> executed = new ArrayList<String>();

    @Rule
    public IKeeperJUnitConnector issueKeeper = new IKeeperJUnitConnector(new BugzillaClient("https://bugzilla.redhat.com"), new JiraClient("https://issues.jboss.org"));

    @AfterClass
    public static void checkExecutions() {
        Assertions.assertThat(executed).containsOnly("runVerifiedBugzillaTest", "runVerifiedJiraTest");
    }

    @BZ("1155593")
    @Test
    public void runVerifiedBugzillaTest() {
        executed.add("runVerifiedBugzillaTest");
    }

    @BZ("1217371")
    @Test
    public void ignoreNewBugzillaTest() {
        executed.add("ignoreNewBugzillaTest");
        System.out.println("ignoreNewBugzillaTest");
    }

    @Jira("JBPM-4608")
    @Test
    public void runVerifiedJiraTest() {
        executed.add("runVerifiedJiraTest");
    }

    @Jira("JBPM-4198")
    @Test
    public void ignoreNewJiraTest() {
        executed.add("ignoreNewJiraTest");
        System.out.println("ignoreNewJiraTest");
    }

}
