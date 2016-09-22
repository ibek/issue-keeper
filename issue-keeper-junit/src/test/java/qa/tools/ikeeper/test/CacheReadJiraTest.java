package qa.tools.ikeeper.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import qa.tools.ikeeper.annotation.Jira;
import qa.tools.ikeeper.client.CacheClient;
import qa.tools.ikeeper.client.connector.CacheConnector;

public class CacheReadJiraTest {

    private static final List<String> executed = new ArrayList<String>();

    @Rule
    public TestRule issueKeeper;

    public CacheReadJiraTest() {
        issueKeeper = new IKeeperJUnitConnector(new CacheClient(
        //new JiraClient("https://issues.jboss.org")
        ));
    }

    @BeforeClass
    public static void prepareCache() {
        try {
            PrintWriter out = new PrintWriter(CacheConnector.DEFAULT_CACHE_FILE_PATH);
            // all JIRAs are OPEN in the cache
            out.println("JBPM-4608=jbpm-services\\: definition service does not provide type information of the variables,JIRA@JBPM,jBPM 6.4.0.Beta1,OPEN\n" + "JBPM-4607=Allow to inject kieContainer into registrable items (e.g. work item handlers),JIRA@JBPM,jBPM 6.4.0.Beta1,OPEN\n" + "JBPM-4198=Package gwt-console-rpc with OSGi metadata,JIRA@JBPM,null,OPEN");
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void checkExecutions() {
        Assertions.assertThat(executed).hasSize(0);
        File cache = new File(CacheConnector.DEFAULT_CACHE_FILE_PATH);
        Assertions.assertThat(cache).exists();
        cache.delete();
    }

    @Jira("JBPM-4608")
    @Test
    public void ignoreVerifiedIssueTest() {
        executed.add("ignoreVerifiedIssueTest");
    }

    @Jira({"JBPM-4608", "JBPM-4607"})
    @Test
    public void ignoreMultipleVerifiedIssuesTest() {
        executed.add("ignoreMultipleVerifiedIssuesTest");
    }

    @Jira("JBPM-4198")
    @Test
    public void ignoreNewIssueTest() {
        executed.add("ignoreNewIssueTest");
    }

    @Jira({"JBPM-4198", "JBPM-4608"})
    @Test
    public void ignoreNewAndVerifiedIssuesTest() {
        executed.add("ignoreNewAndVerifiedIssuesTest");
    }

}
