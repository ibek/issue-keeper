package qa.tools.ikeeper.client.connector;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qa.tools.ikeeper.IssueDetails;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BugzillaConnector extends AbstractConnector {

    private static final Logger LOG = LoggerFactory.getLogger(BugzillaConnector.class);

    private String urlDomain;

    private String query = "method=Bug.get&params=[{\"ids\":[\"${id}\"]}]";

    public BugzillaConnector(String urlDomain) {
        this.urlDomain = urlDomain;
    }

    public BugzillaConnector(String urlDomain, String query) {
        this(urlDomain);
        if (query != null && !query.isEmpty()) {
            this.query = query;
        }
    }

    @Override
    public Set<IssueDetails> getIssue(String id) {
        String q = replacePlaceholders(query, id);
        if (cache.containsKey(q)) {
            return cache.get(q);
        }
        Set<IssueDetails> result = getByQuery(q);

        cache.put(q, result);
        return result;
    }

    private Set<IssueDetails> getByQuery(String query) {
        Set<IssueDetails> issues = new HashSet<IssueDetails>();
        String response = get(urlDomain + "/jsonrpc.cgi?" + query);
        try {
            JsonObject result = new JsonParser().parse(response).getAsJsonObject().getAsJsonObject("result");
            Iterator<JsonElement> bugs = result.getAsJsonArray("bugs").iterator();

            while (bugs.hasNext()) {
                issues.add(parseIssue(bugs.next().toString()));
            }
        }catch (NullPointerException e){
            throw new IllegalStateException("Wrong response.", e);
        }

        return issues;
    }

    private IssueDetails parseIssue(String response) {
        IssueDetails details = new IssueDetails();

        JsonObject bug = new JsonParser().parse(response).getAsJsonObject();
        details.setId(bug.get("id").getAsString());
        details.setTitle(bug.get("summary").getAsString());
        details.setTargetVersion(bug.get("target_release").getAsJsonArray().iterator().next().getAsString());
        details.setProject("BZ@" + bug.get("product").getAsString());
        String status = bug.get("status").getAsString();
        details.setStatusName(status.toUpperCase());

        return details;
    }

    @Override
    public String getQuery() {
        return query;
    }

    private String get(String url) {
        if (!active) {
            return null;
        }
        String r = null;
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            if (con.getResponseCode() != 200) {
                throw new RuntimeException("Failed to contact Bugzilla on URL:" + url + ", HTTP error code : " + con
                        .getResponseCode());
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            r = response.toString();
        } catch (UnknownHostException ex) {
            String msg = "Issue Keeper - UnknownHostException: " + ex.getMessage() + ", turning off - all tests will run";
            LOG.warn(msg);
            System.out.println(msg);
            active = false;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return r;
    }
}
