package qa.tools.ikeeper.client.connector;

import qa.tools.ikeeper.IssueDetails;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractConnector implements IssueTrackingSystemConnector {

    protected static Map<String, Set<IssueDetails>> cache = new HashMap<String, Set<IssueDetails>>();
    protected static boolean active = true;

    protected String replacePlaceholders(String query, String id) {
        query = query.replace("${id}", id);
        while (query.indexOf("${") >= 0) {
            int start = query.indexOf("${");
            int stop = query.substring(start).indexOf("}") + start;
            String key = query.substring(start + 2, stop);
            String val = System.getProperty(key);
            if (val == null) {
                throw new IllegalArgumentException("can't substitute key: " + key);
            }
            query = query.replace(String.format("${%s}", key), val);
        }
        return query;
    }
}
