package qa.tools.ikeeper.action;

import java.util.List;

import qa.tools.ikeeper.IssueDetails;

public interface IAction {

    void perform(String testName, List<IssueDetails> details);

}
