package qa.tools.ikeeper.action;

import qa.tools.ikeeper.IssueDetails;

public interface IAction {

    void perform(String testName, IssueDetails details);

}
