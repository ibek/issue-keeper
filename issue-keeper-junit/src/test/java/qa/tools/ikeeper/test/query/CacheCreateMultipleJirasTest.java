package qa.tools.ikeeper.test.query;


import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import qa.tools.ikeeper.annotation.Jira;
import qa.tools.ikeeper.client.CacheClient;
import qa.tools.ikeeper.client.JiraClient;
import qa.tools.ikeeper.client.connector.CacheConnector;
import qa.tools.ikeeper.interceptor.QueryInterceptor;
import qa.tools.ikeeper.test.IKeeperJUnitConnector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.fail;

public class CacheCreateMultipleJirasTest {
    private static final String QUERY = "(KEY=${id} OR KEY=\"JBPM-4608\") AND STATUS != RESOLVED AND STATUS != CLOSED";
    private static final List<String> executed = new ArrayList<String>();

    @Rule
    public TestRule issueKeeper;

    public CacheCreateMultipleJirasTest() {
        issueKeeper = new IKeeperJUnitConnector(new QueryInterceptor(), new CacheClient(
                new JiraClient("https://issues.jboss.org", QUERY)
        ));
    }

    @AfterClass
    public static void checkExecutions() {
        Assertions.assertThat(executed).hasSize(1);
        Assertions.assertThat(executed).contains("runVerifiedIssueTest");

        File cache = new File(CacheConnector.DEFAULT_CACHE_FILE_PATH);
        Assertions.assertThat(cache).exists();
        try {
            Properties data = new Properties();
            data.load(new FileInputStream(cache));
            Assertions.assertThat(data.getProperty(QUERY.replace("${id}", "JBPM-4198")))
                    .contains("JBPM-4198,Package gwt-console-rpc with OSGi metadata")
                    .doesNotContain("JBPM-4608,jbpm-services: definition service does not provide type information of the variables");
            Assertions.assertThat(data.getProperty(QUERY.replace("${id}", "JBPM-4607")))
                    .doesNotContain("JBPM-4198,Package gwt-console-rpc with OSGi metadata")
                    .doesNotContain("JBPM-4608,jbpm-services: definition service does not provide type information of the variables");
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        } finally {
           cache.delete();
        }
    }

    @Jira("JBPM-4198")
    @Test
    public void ignoreNewAndVerifiedIssuesTest() {
        System.out.println("ignoreNewAndVerifiedIssuesTest");
        fail("ignoreNewAndVerifiedIssuesTest");
    }

    @Jira("JBPM-4607")
    @Test
    public void runVerifiedIssueTest() {
        executed.add("runVerifiedIssueTest");
    }
}
