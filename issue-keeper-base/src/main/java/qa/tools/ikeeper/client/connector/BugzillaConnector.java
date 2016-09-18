package qa.tools.ikeeper.client.connector;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qa.tools.ikeeper.IssueDetails;
import qa.tools.ikeeper.IssueStatus;

public class BugzillaConnector implements IssueTrackingSystemConnector {

    private static final Logger LOG = LoggerFactory.getLogger(BugzillaConnector.class);

    private static Map<String, IssueDetails> cache = new HashMap<String, IssueDetails>();
    private static boolean active = true;

    private String urlDomain;

    public BugzillaConnector(String urlDomain) {
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
            String url = urlDomain + "/jsonrpc.cgi?method=Bug.get&params=[{\"ids\":[" + id + "]}]";
            String bzjson = get(url);

            if (bzjson == null) {
                setUnknownIssue = true;
            } else {

                JsonObject result = new JsonParser().parse(bzjson).getAsJsonObject().getAsJsonObject("result");
                JsonObject bug = result.getAsJsonArray("bugs").get(0).getAsJsonObject();

                details.setTitle(bug.get("summary").getAsString());
                details.setTargetVersion(bug.get("target_release").getAsJsonArray().iterator().next().getAsString());
                String status = bug.get("status").getAsString();
                if (status.equals("CLOSED")) {
                    details.setStatus(IssueStatus.CLOSED);
                } else if (status.equals("VERIFIED")) {
                    details.setStatus(IssueStatus.VERIFIED);
                } else if (status.equals("ON_QA")) {
                    details.setStatus(IssueStatus.ON_QA);
                } else if (status.equals("MODIFIED")) {
                    details.setStatus(IssueStatus.MODIFIED);
                } else if (status.equals("ASSIGNED")) {
                    details.setStatus(IssueStatus.ASSIGNED);
                } else if (status.equals("NEW")) {
                    details.setStatus(IssueStatus.NEW);
                } else {
                    details.setStatus(IssueStatus.UNKNOWN);
                }
            }
        } catch (Exception ex) {
            LOG.warn(ex.getClass().getName() + " " + ex.getMessage());
            setUnknownIssue = true;
        }

        if (setUnknownIssue) {
            details.setStatus(IssueStatus.UNKNOWN);
            details.setTitle("Exception in getIssue details for BZ " + id);
        }

        cache.put(id, details);

        return details;
    }

    private String get(String url) {
        if (!active) {
            return null;
        }
        String r = null;
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

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
