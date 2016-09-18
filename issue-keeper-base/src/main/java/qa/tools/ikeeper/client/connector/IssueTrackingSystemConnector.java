package qa.tools.ikeeper.client.connector;

import qa.tools.ikeeper.IssueDetails;

/**
 * Common interface for connecting to various issue tracking systems.
 */
public interface IssueTrackingSystemConnector {

    IssueDetails getIssue(String id);
}
