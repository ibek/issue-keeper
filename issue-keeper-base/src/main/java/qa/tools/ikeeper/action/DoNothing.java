package qa.tools.ikeeper.action;

import qa.tools.ikeeper.IssueDetails;

public class DoNothing implements IAction {

    @Override
    public void perform(String testName, IssueDetails details) {
        // Do nothing
    }

}
