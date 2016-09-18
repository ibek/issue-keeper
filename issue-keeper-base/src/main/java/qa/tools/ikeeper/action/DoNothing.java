package qa.tools.ikeeper.action;

import qa.tools.ikeeper.IssueDetails;

public class DoNothing implements IAction {

    @Override
    public boolean canRunTest(IssueDetails details) {
        return true;
    }

    @Override
    public void fail(String testName, IssueDetails details) {
        // Do nothing
    }

}
