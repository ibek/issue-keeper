package qa.tools.ikeeper.client.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qa.tools.ikeeper.IssueDetails;
import qa.tools.ikeeper.IssueStatus;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JiraConnector implements IssueTrackingSystemConnector {

    private static final Logger LOG = LoggerFactory.getLogger(JiraConnector.class);

    private static Map<String, IssueDetails> cache = new HashMap<String, IssueDetails>();
    private static boolean active = true;

    private static final Map<Integer, IssueStatus> JIRA_STATES = new HashMap<Integer, IssueStatus>() {
        private static final long serialVersionUID = 1L;

        {
            put(1, IssueStatus.ASSIGNED); // opened
            put(3, IssueStatus.MODIFIED); // in-progress
            put(4, IssueStatus.ASSIGNED); // re-opened
            put(5, IssueStatus.ON_QA); // resolved
            put(6, IssueStatus.CLOSED); // closed
        }
    };

    private String urlDomain;

    public JiraConnector(String urlDomain) {
        this.urlDomain = urlDomain;
    }

    @Override
    public IssueDetails getIssue(String id) {
        if (cache.containsKey(id)) {
            return cache.get(id);
        }
        IssueDetails details = new IssueDetails();
        details.setId(id);

        boolean setUnknownIssue = false;

        try {
            String url = urlDomain + "/rest/api/2/issue/" + id + "?fields=summary,fixVersions,status";
            String response = get(url);

            JsonObject jsonFields = new JsonParser().parse(response).getAsJsonObject().getAsJsonObject("fields");
            JsonObject jsonStatus = jsonFields.getAsJsonObject("status");
            int statusId = Integer.parseInt(jsonStatus.get("id").getAsString());
            String summary = jsonFields.get("summary").getAsString();
            details.setTitle(summary);

            IssueStatus issueStatus = JIRA_STATES.get(statusId);
            if (issueStatus == null) {
                issueStatus = IssueStatus.UNKNOWN;
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Unknown Jira status id:" + statusId);
                }
            }
            details.setStatus(issueStatus);

            Iterator<JsonElement> itelm = jsonFields.get("fixVersions").getAsJsonArray().iterator();
            String fixVersion = null;
            if (itelm.hasNext()) {
                fixVersion = itelm.next().getAsJsonObject().get("name").getAsString();
                details.setTargetVersion(fixVersion);
            }
        } catch (Exception ex) {
            LOG.warn(ex.getClass().getName() + " " + ex.getMessage());
            setUnknownIssue = true;
        }

        if (setUnknownIssue) {
            details.setStatus(IssueStatus.UNKNOWN);
            details.setTitle("Exception in getIssue details for Jira " + id);
        }

        cache.put(id, details);

        return details;
    }

    private String get(String url) {
        if (!active) {
            return null;
        }
        String r = null;
        BufferedReader in = null;
        try {
            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed to contact Jira on URL:" + url + ", HTTP error code : "
                        + conn.getResponseCode());
            }

            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            r = response.toString();
        } catch (UnknownHostException ex) {
            String msg = "Issue Keeper - UnknownHostException: " + ex.getMessage()
                    + ", turning off - all tests will run";
            LOG.warn(msg);
            System.out.println(msg);
            active = false;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Unable to close reader for the connection to URL:" + url);
                }
            }
        }
        return r;
    }
}
