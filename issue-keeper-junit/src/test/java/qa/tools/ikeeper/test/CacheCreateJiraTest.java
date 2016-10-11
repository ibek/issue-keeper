package qa.tools.ikeeper.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import qa.tools.ikeeper.annotation.Jira;
import qa.tools.ikeeper.client.CacheClient;
import qa.tools.ikeeper.client.JiraClient;
import qa.tools.ikeeper.client.connector.CacheConnector;

public class CacheCreateJiraTest {

    private static final List<String> executed = new ArrayList<String>();

    @Rule
    public TestRule issueKeeper;

    public CacheCreateJiraTest() {
        issueKeeper = new IKeeperJUnitConnector(new CacheClient(new JiraClient("https://issues.jboss.org")));
    }

    @AfterClass
    public static void checkExecutions() {
        Assertions.assertThat(executed).hasSize(2);
        Assertions.assertThat(executed).contains("runVerifiedIssueTest", "runMultipleVerifiedIssuesTest");
        File cache = new File(CacheConnector.DEFAULT_CACHE_FILE_PATH);
        Assertions.assertThat(cache).exists();
        try {
            String cacheData = new Scanner(new File(CacheConnector.DEFAULT_CACHE_FILE_PATH)).useDelimiter("\\Z").next();
            System.out.println(cacheData);
            Assertions.assertThat(cacheData).contains("JBPM-4608=jbpm-services\\: definition service does not provide type information of the variables,JIRA@JBPM,jBPM 6.4.0.Beta1,RESOLVED\n" + "JBPM-4607=Allow to inject kieContainer into registrable items (e.g. work item handlers),JIRA@JBPM,jBPM 6.4.0.Beta1,RESOLVED\n" + "JBPM-4198=Package gwt-console-rpc with OSGi metadata,JIRA@JBPM,null,OPEN");
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
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
