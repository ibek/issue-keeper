package qa.tools.ikeeper.client;

import qa.tools.ikeeper.IssueDetails;
import qa.tools.ikeeper.annotation.BZ;
import qa.tools.ikeeper.client.connector.BugzillaConnector;
import qa.tools.ikeeper.client.connector.IssueTrackingSystemConnector;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class BugzillaClient implements ITrackerClient {

    private final BugzillaConnector issueConnector;

    public BugzillaClient(String urlDomain) {
        issueConnector = new BugzillaConnector(urlDomain);
    }

    public BugzillaClient(String urlDomain, String query) {
        issueConnector = new BugzillaConnector(urlDomain, query);
    }

    @Override
    public String getName() {
        return "BZ";
    }

    @Override
    public List<String> getDefaultActionStates() {
        return Arrays.asList("NEW", "ASSIGNED", "POST", "MODIFIED");
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
            Set<IssueDetails> details = issueConnector.getIssue(id);
            detailsList.addAll(details);
        }

        return detailsList;
    }

    @Override
    public String getQuery() {
        return issueConnector.getQuery();
    }

    @Override
    public void authenticate(String username, String password) {
        throw new IllegalStateException("Authentication is provided in method params. See https://www.bugzilla.org/docs/4.4/en/html/api/Bugzilla/WebService.html#LOGGING_IN");
    }

    @Override
    public void authenticate(String personalAccessToken) {
        throw new UnsupportedOperationException("personal access token not supported");
    }

    @Override
    public IssueTrackingSystemConnector getIssueConnector() {
        return issueConnector;
    }
}
