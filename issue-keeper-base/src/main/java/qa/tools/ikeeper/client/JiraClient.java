package qa.tools.ikeeper.client;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import qa.tools.ikeeper.IssueDetails;
import qa.tools.ikeeper.annotation.Jira;
import qa.tools.ikeeper.client.connector.IssueTrackingSystemConnector;
import qa.tools.ikeeper.client.connector.JiraConnector;

public class JiraClient implements ITrackerClient {
    
    private final IssueTrackingSystemConnector issueConnector;
    private final String testedVersion;

    public JiraClient(String urlDomain) {
        this(urlDomain, null);
    }

    public JiraClient(String urlDomain, String testedVersion) {
        issueConnector = new JiraConnector(urlDomain);
        this.testedVersion = testedVersion;
    }
    
    public boolean isIssueFixedInTestedVersion(IssueDetails details) {
        if (testedVersion == null) {
            return true;
        }
        DefaultArtifactVersion tested = new DefaultArtifactVersion(testedVersion);
        DefaultArtifactVersion target = new DefaultArtifactVersion(details.getTargetVersion());
        return tested.compareTo(target) >= 0;
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

}
