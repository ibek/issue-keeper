package qa.tools.ikeeper.action;

import org.junit.Assert;

import qa.tools.ikeeper.IssueDetails;
import qa.tools.ikeeper.IssueStatus;

public class Fail implements IAction {

    @Override
    public boolean canRunTest(IssueDetails details) {
        return (details.getStatus() == IssueStatus.CLOSED || details.getStatus() == IssueStatus.ON_QA || details
                .getStatus() == IssueStatus.VERIFIED || details.getStatus() == IssueStatus.UNKNOWN);
    }
    
    @Override
    public void fail(String testName, IssueDetails details) {
        String issueDescription = details.getDescription();
        String msg = testName + " - this test failed due to:\n\tBZ-" + details.getId() + " "
                + details.getTitle() + "\n\tstatus: " + details.getStatus() + ""
                + ((issueDescription == null || issueDescription.isEmpty()) ? "" : "\n\tdescription: " + issueDescription);
        Assert.fail(msg);
    }

}
