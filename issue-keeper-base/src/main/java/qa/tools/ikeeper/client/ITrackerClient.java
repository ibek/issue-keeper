package qa.tools.ikeeper.client;

import java.lang.annotation.Annotation;
import java.util.List;

import qa.tools.ikeeper.IssueDetails;
import qa.tools.ikeeper.client.connector.IssueTrackingSystemConnector;

public interface ITrackerClient {
    
    public String getName();
    
    public List<String> getDefaultActionStates();

    public boolean canHandle(Annotation annotation);

    public List<IssueDetails> getIssues(Annotation annotation);

    public String getQuery();

    public IssueTrackingSystemConnector getIssueConnector();

}
