package qa.tools.ikeeper.action;

import java.util.List;

import qa.tools.ikeeper.IssueDetails;

public class DoNothing implements IAction {

    @Override
    public void perform(String testName, List<IssueDetails> details) {
        // Do nothing
    }

}
