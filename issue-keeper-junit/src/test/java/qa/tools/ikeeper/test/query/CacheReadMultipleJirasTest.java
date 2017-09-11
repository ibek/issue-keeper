package qa.tools.ikeeper.test.query;

import org.assertj.core.api.Assertions;
import org.junit.*;
import org.junit.rules.TestRule;
import qa.tools.ikeeper.annotation.Jira;
import qa.tools.ikeeper.client.CacheClient;
import qa.tools.ikeeper.client.JiraClient;
import qa.tools.ikeeper.client.connector.CacheConnector;
import qa.tools.ikeeper.interceptor.QueryInterceptor;
import qa.tools.ikeeper.test.IKeeperJUnitConnector;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class CacheReadMultipleJirasTest {
    private static final List<String> executed = new ArrayList<String>();

    @AfterClass
    public static void checkExecutions() {
        Assertions.assertThat(executed).hasSize(1);
        Assertions.assertThat(executed).contains("runVerifiedIssueTest");
    }

    private static final String QUERY = "KEY=${id} OR KEY=${anotherIssue} AND STATUS != CLOSED AND STATUS != RESOLVED";

    @Rule
    public TestRule issueKeeper;

    public CacheReadMultipleJirasTest() {
        System.setProperty("anotherIssue", "JBPM-4606");
        issueKeeper = new IKeeperJUnitConnector(new QueryInterceptor(), new CacheClient(
                new JiraClient("https://issues.jboss.org", QUERY)
        ));
    }

    @BeforeClass
    public static void prepareCache() {
        try {
            PrintWriter out = new PrintWriter(CacheConnector.DEFAULT_CACHE_FILE_PATH);
            // all JIRAs are OPEN in the cache
            String q = QUERY.replace("${anotherIssue}", "JBPM-4606").replace("=", "\\=").replace(" ", "\\ ").replace("!", "\\!");
            //nothing was returned for this query
            out.println(q.replace("${id}", "JBPM-4608") + "=");
            out.println(q.replace("${id}", "JBPM-4198") + "=" + "JBPM-4608,jbpm-services\\: definition service does not provide type information of the variables,JIRA@JBPM,jBPM 6.4.0.Beta1,RESOLVED"+ CacheConnector.DELIMITER + "JBPM-4198,Package gwt-console-rpc with OSGi metadata,JIRA@JBPM,null,OPEN");
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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

    @Jira({"JBPM-4198", "JBPM-4608"})
    @Test
    public void ignoreNewAndVerifiedIssuesTest() {
        executed.add("ignoreNewAndVerifiedIssuesTest");
        System.out.println("ignoreNewAndVerifiedIssuesTest");
    }

}
