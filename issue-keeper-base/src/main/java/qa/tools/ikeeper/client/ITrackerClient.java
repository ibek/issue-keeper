package qa.tools.ikeeper.client;

import java.lang.annotation.Annotation;
import java.util.List;

import qa.tools.ikeeper.IssueDetails;
import qa.tools.ikeeper.client.connector.IssueTrackingSystemConnector;

public interface ITrackerClient {

    public boolean canHandle(Annotation annotation);

    public List<IssueDetails> getIssues(Annotation annotation);

    public IssueTrackingSystemConnector getIssueConnector();

}
