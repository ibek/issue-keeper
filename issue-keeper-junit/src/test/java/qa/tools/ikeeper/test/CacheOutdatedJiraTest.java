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
import qa.tools.ikeeper.client.JiraClient;
import qa.tools.ikeeper.client.connector.CacheConnector;

public class CacheOutdatedJiraTest {

    private static final List<String> executed = new ArrayList<String>();

    @Rule
    public TestRule issueKeeper;

    public CacheOutdatedJiraTest() {
        issueKeeper = new IKeeperJUnitConnector(new CacheClient(new JiraClient("https://issues.jboss.org")));
    }

    @BeforeClass
    public static void prepareCache() {
        try {
            PrintWriter out = new PrintWriter(CacheConnector.DEFAULT_CACHE_FILE_PATH);
            // all JIRAs are ASSIGNED in the old cache
            out.println("JBPM-4608=jbpm-services\\: definition service does not provide type information of the variables,jBPM 6.4.0.Beta1,ASSIGNED\n" + "JBPM-4607=Allow to inject kieContainer into registrable items (e.g. work item handlers),jBPM 6.4.0.Beta1,ASSIGNED\n" + "JBPM-4198=Package gwt-console-rpc with OSGi metadata,null,ASSIGNED");
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        File cache = new File(CacheConnector.DEFAULT_CACHE_FILE_PATH);
        // Cache is outdated - last modified 24:01:00 ago
        cache.setLastModified(System.currentTimeMillis() - CacheConnector.TIME_VALID - 60000);
    }

    @AfterClass
    public static void checkExecutions() {
        Assertions.assertThat(executed).hasSize(2);
        Assertions.assertThat(executed).contains("runVerifiedIssueTest", "runMultipleVerifiedIssuesTest");
        File cache = new File(CacheConnector.DEFAULT_CACHE_FILE_PATH);
        Assertions.assertThat(cache).exists();
        cache.delete();
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
