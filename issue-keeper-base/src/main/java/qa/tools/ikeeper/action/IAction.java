package qa.tools.ikeeper.action;

import qa.tools.ikeeper.IssueDetails;

public interface IAction {

    /**
     * Perform a check for given issue details.
     * @param details about the issue
     * @return whether the test should be executed
     */
    boolean canRunTest(IssueDetails details);

    void fail(String testName, IssueDetails details);

}
