package qa.tools.ikeeper.action;

import qa.tools.ikeeper.IssueDetails;

public interface IAction {

    /**
     * Perform a check for given issue details.
     * 
     * @param details
     *            about the issue
     * @return whether the test should be executed
     */
    public boolean canRunTest(IssueDetails details);

    public void fail(String testName, IssueDetails details);
    
}
