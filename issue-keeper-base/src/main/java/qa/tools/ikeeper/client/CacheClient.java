package qa.tools.ikeeper.client;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import qa.tools.ikeeper.IssueDetails;
import qa.tools.ikeeper.annotation.BZ;
import qa.tools.ikeeper.annotation.Jira;
import qa.tools.ikeeper.client.connector.CacheConnector;
import qa.tools.ikeeper.client.connector.IssueTrackingSystemConnector;

public class CacheClient implements ITrackerClient {

    private final CacheConnector issueConnector;

    private List<ITrackerClient> clients;

    public CacheClient(ITrackerClient... clients) {
        this(CacheConnector.DEFAULT_CACHE_FILE_PATH, clients);
    }

    public CacheClient(String cacheFilePath, ITrackerClient... clients) {
        issueConnector = new CacheConnector(cacheFilePath);
        this.clients = new ArrayList<ITrackerClient>();
        if (clients != null) {
            for (ITrackerClient client : clients) {
                if (!(client instanceof CacheClient)) {
                    this.clients.add(client);
                }
            }
        }
    }

    @Override
    public boolean canHandle(Annotation annotation) {
        return annotation instanceof Jira || annotation instanceof BZ;
    }

    @Override
    public List<IssueDetails> getIssues(Annotation annotation) {
        Jira jiraAnnotation = (Jira) annotation;

        String[] ids = jiraAnnotation.value();
        List<IssueDetails> detailsList = new ArrayList<IssueDetails>();
        boolean cacheChanged = false;

        for (String id : ids) {
            IssueDetails details = issueConnector.getIssue(id);
            if (details == null) {
                for (ITrackerClient client : clients) {
                    if (client.canHandle(annotation)) {
                        details = client.getIssueConnector().getIssue(id);
                        if (details != null) {
                            issueConnector.addIssueDetails(details);
                            cacheChanged = true;
                        }
                    }
                }
            }
            detailsList.add(details);
        }

        if (cacheChanged) {
            issueConnector.saveData();
        }

        return detailsList;
    }

    @Override
    public IssueTrackingSystemConnector getIssueConnector() {
        return issueConnector;
    }

}
