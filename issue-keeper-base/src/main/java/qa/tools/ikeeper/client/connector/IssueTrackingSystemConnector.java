package qa.tools.ikeeper.client.connector;

import java.util.Set;

import qa.tools.ikeeper.IssueDetails;

/**
 * Common interface for connecting to various issue tracking systems.
 */
public interface IssueTrackingSystemConnector {

    Set<IssueDetails> getIssue(String id);

    String getQuery();

}
