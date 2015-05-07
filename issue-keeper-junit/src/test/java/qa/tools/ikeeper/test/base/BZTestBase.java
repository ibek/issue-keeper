package qa.tools.ikeeper.test.base;

import org.junit.Rule;
import org.junit.rules.TestRule;

import qa.tools.ikeeper.client.BugzillaClient;
import qa.tools.ikeeper.test.IKeeperJUnitConnector;

public class BZTestBase {

    @Rule
    public TestRule issueKeeper;

    public BZTestBase() {
        issueKeeper = new IKeeperJUnitConnector(new BugzillaClient("https://bugzilla.redhat.com"));
    }
    
}
