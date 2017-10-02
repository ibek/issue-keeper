package qa.tools.ikeeper.client;

import qa.tools.ikeeper.IssueDetails;
import qa.tools.ikeeper.annotation.BZ;
import qa.tools.ikeeper.annotation.Jira;
import qa.tools.ikeeper.client.connector.CacheConnector;
import qa.tools.ikeeper.client.connector.IssueTrackingSystemConnector;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    public List<ITrackerClient> getClients() {
        return clients;
    }

    @Override
    public String getName() {
        return "CACHE";
    }

    @Override
    public List<String> getDefaultActionStates() {
        List<String> actionStates = new ArrayList<String>();
        return actionStates;
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

            for (ITrackerClient client : clients) {
                Set<IssueDetails> details = null;
                if (client.canHandle(annotation)) {
                    issueConnector.setQuery(client.getQuery());
                    details = issueConnector.getIssue(id);
                    if (details == null) {
                        details = client.getIssueConnector().getIssue(id);
                        if (details != null) {
                            issueConnector.addIssueDetails(id, client.getQuery(), details);
                            cacheChanged = true;
                        }
                    }
                }
                detailsList.addAll(details);
            }

            if (cacheChanged) {
                issueConnector.saveData();
            }
        }
        return detailsList;
    }

    @Override
    public String getQuery() {
        return issueConnector.getQuery();
    }

    @Override
    public void authenticate(String username, String password) {
        throw new IllegalStateException("Authentication is not suitable for cache client");
    }

    @Override
    public IssueTrackingSystemConnector getIssueConnector() {
        return issueConnector;
    }
}
