package qa.tools.ikeeper.action;

import org.junit.Assert;

import qa.tools.ikeeper.IssueDetails;

public class Fail implements IAction {

    @Override
    public void perform(String testName, IssueDetails details) {
        String message = ActionMessage.generate(testName, details, "failed");
        Assert.fail(message);
    }

}
