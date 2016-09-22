package qa.tools.ikeeper.client.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import qa.tools.ikeeper.IssueDetails;

public class JiraConnector implements IssueTrackingSystemConnector {

    private static final Logger LOG = LoggerFactory.getLogger(JiraConnector.class);

    private static Map<String, IssueDetails> cache = new HashMap<String, IssueDetails>();
    private static boolean active = true;

    public static List<Integer> ACTION_STATES = Arrays.asList( // DEFAULT
                                                                      // behavior
            1, // opened
            2, // new
            3, // in-progress
            4 // re-opened
    );

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

        try {
            String url = urlDomain + "/rest/api/2/issue/" + id + "?fields=summary,fixVersions,status,project";
            String response = get(url);

            JsonObject jsonFields = new JsonParser().parse(response).getAsJsonObject().getAsJsonObject("fields");
            JsonObject jsonStatus = jsonFields.getAsJsonObject("status");
            String statusName = jsonStatus.get("name").getAsString();
            String summary = jsonFields.get("summary").getAsString();
            details.setTitle(summary);
            details.setStatusName(statusName.toUpperCase());
            JsonObject project = jsonFields.getAsJsonObject("project");
            details.setProject("JIRA@" + project.get("key").getAsString());

            Iterator<JsonElement> itelm = jsonFields.get("fixVersions").getAsJsonArray().iterator();
            String fixVersion = null;
            if (itelm.hasNext()) {
                fixVersion = itelm.next().getAsJsonObject().get("name").getAsString();
                details.setTargetVersion(fixVersion);
            }

            cache.put(id, details);
        } catch (Exception ex) {
            LOG.warn(ex.getClass().getName() + " " + ex.getMessage());
        }

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
                throw new RuntimeException("Failed to contact Jira on URL:" + url + ", HTTP error code : " + conn
                        .getResponseCode());
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
