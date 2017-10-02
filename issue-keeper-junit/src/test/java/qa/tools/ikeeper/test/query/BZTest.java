package qa.tools.ikeeper.test.query;


import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import qa.tools.ikeeper.annotation.BZ;
import qa.tools.ikeeper.client.BugzillaClient;
import qa.tools.ikeeper.client.JiraClient;
import qa.tools.ikeeper.interceptor.QueryInterceptor;
import qa.tools.ikeeper.test.IKeeperJUnitConnector;

import java.util.ArrayList;
import java.util.List;

public class BZTest {

    @Rule
    public TestRule issueKeeper;

    public BZTest() {
        issueKeeper = new IKeeperJUnitConnector(new QueryInterceptor(),
                new BugzillaClient("https://bugzilla.redhat.com", "method=Bug.search&params=[{\"id\":\"${id}\",\"status\":[\"NEW\",\"ASSIGNED\",\"POST\",\"MODIFIED\"]}]"));
    }

    private static final List<String> executed = new ArrayList<String>();

    @AfterClass
    public static void checkExecutions() {
        Assertions.assertThat(executed).hasSize(2);
        Assertions.assertThat(executed).contains("runVerifiedIssueTest", "runMultipleVerifiedIssuesTest");
    }

    @BZ("1155593")
    @Test
    public void runVerifiedIssueTest() {
        executed.add("runVerifiedIssueTest");
    }

    @BZ({"1155593", "1203640"})
    @Test
    public void runMultipleVerifiedIssuesTest() {
        executed.add("runMultipleVerifiedIssuesTest");
    }

    @BZ("1217371")
    @Test
    public void ignoreNewIssueTest() {
        executed.add("ignoreNewIssueTest");
        System.out.println("ignoreNewIssueTest");
    }

    @BZ({"1217371", "1203640"})
    @Test
    public void ignoreNewAndVerifiedIssuesTest() {
        executed.add("ignoreNewAndVerifiedIssuesTest");
        System.out.println("ignoreNewAndVerifiedIssuesTest");
    }

}
