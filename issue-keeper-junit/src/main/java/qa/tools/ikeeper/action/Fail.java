package qa.tools.ikeeper.action;

import org.junit.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import qa.tools.ikeeper.IssueDetails;

public class Fail implements IAction {

    @Override
    public void perform(String testName, List<IssueDetails> details) {
        String message = ActionMessage.generate(testName, details, "failed");
        Assert.fail(message);
    }

}
