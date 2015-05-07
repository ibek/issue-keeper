package qa.tools.ikeeper.client;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import qa.tools.ikeeper.IssueDetails;
import qa.tools.ikeeper.annotation.BZ;
import qa.tools.ikeeper.client.connector.BugzillaConnector;
import qa.tools.ikeeper.client.connector.IssueTrackingSystemConnector;

public class BugzillaClient implements ITrackerClient {
    
    private final IssueTrackingSystemConnector issueConnector;

    public BugzillaClient(String urlDomain) {
        issueConnector = new BugzillaConnector(urlDomain);
    }
    
    @Override
    public boolean canHandle(Annotation annotation) {
        return annotation instanceof BZ;
    }

    @Override
    public List<IssueDetails> getIssues(Annotation annotation) {
        BZ bz = (BZ) annotation;

        String[] ids = bz.value();
        List<IssueDetails> detailsList = new ArrayList<IssueDetails>();
        for (String id : ids) {
            IssueDetails details = issueConnector.getIssue(id);
            detailsList.add(details);
        }

        return detailsList;
    }
}
