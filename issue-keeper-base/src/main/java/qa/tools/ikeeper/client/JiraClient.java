package qa.tools.ikeeper.client;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import qa.tools.ikeeper.IssueDetails;
import qa.tools.ikeeper.annotation.Jira;
import qa.tools.ikeeper.client.connector.IssueTrackingSystemConnector;
import qa.tools.ikeeper.client.connector.JiraConnector;

public class JiraClient implements ITrackerClient {

    protected final IssueTrackingSystemConnector issueConnector;

    public JiraClient(String urlDomain) {
        issueConnector = new JiraConnector(urlDomain);
    }

    @Override
    public boolean canHandle(Annotation annotation) {
        return annotation instanceof Jira;
    }

    @Override
    public List<IssueDetails> getIssues(Annotation annotation) {
        Jira jiraAnnotation = (Jira) annotation;

        String[] ids = jiraAnnotation.value();
        List<IssueDetails> detailsList = new ArrayList<IssueDetails>();
        for (String id : ids) {
            IssueDetails details = issueConnector.getIssue(id);
            detailsList.add(details);
        }

        return detailsList;
    }

    @Override
    public IssueTrackingSystemConnector getIssueConnector() {
        return issueConnector;
    }

}
