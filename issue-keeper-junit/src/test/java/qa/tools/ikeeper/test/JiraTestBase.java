package qa.tools.ikeeper.test;

import org.junit.Rule;
import org.junit.rules.TestRule;

import qa.tools.ikeeper.client.JiraClient;

public class JiraTestBase {

    @Rule
    public TestRule issueKeeper;

    public JiraTestBase() {
        issueKeeper = new IKeeperJUnitConnector(new JiraClient("https://issues.jboss.org"));
    }
    
}
