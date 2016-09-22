package qa.tools.ikeeper.action;

import org.junit.Assume;

import qa.tools.ikeeper.IssueDetails;

public class Skip implements IAction {

    @Override
    public void perform(String testName, IssueDetails details) {
        String message = ActionMessage.generate(testName, details, "is skipped");
        Assume.assumeTrue(message, false);
    }

}
