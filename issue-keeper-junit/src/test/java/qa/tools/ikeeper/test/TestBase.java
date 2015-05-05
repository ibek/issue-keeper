package qa.tools.ikeeper.test;

import org.junit.Rule;
import org.junit.rules.TestRule;

import qa.tools.ikeeper.client.BugzillaClient;
import qa.tools.ikeeper.test.IKeeperJUnitConnector;

public class TestBase {

    @Rule
    public TestRule issueKeeper;

    public TestBase() {
        issueKeeper = new IKeeperJUnitConnector(new BugzillaClient("https://bugzilla.redhat.com"));
    }
    
}
