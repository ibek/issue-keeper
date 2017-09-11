package qa.tools.ikeeper.client;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import qa.tools.ikeeper.IssueDetails;
import qa.tools.ikeeper.annotation.Jira;
import qa.tools.ikeeper.client.connector.IssueTrackingSystemConnector;
import qa.tools.ikeeper.client.connector.JiraConnector;

public class JiraClient implements ITrackerClient {

    protected final IssueTrackingSystemConnector issueConnector;

    public JiraClient(String urlDomain) {
        issueConnector = new JiraConnector(urlDomain, null);
    }

    public JiraClient(String urlDomain, String query) {
        issueConnector = new JiraConnector(urlDomain, query);
    }

    
    @Override
    public String getName() {
        return "JIRA";
    }
    
    @Override
    public List<String> getDefaultActionStates() {
        return Arrays.asList("NEW", "OPEN", "ASSIGNED", "CODING IN PROGRESS", "PULL REQUEST SENT", "REOPENED");
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
    public IssueTrackingSystemConnector getIssueConnector() {
        return issueConnector;
    }

}
