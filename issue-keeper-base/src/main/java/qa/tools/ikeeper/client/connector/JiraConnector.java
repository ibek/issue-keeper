package qa.tools.ikeeper.client.connector;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qa.tools.ikeeper.IssueDetails;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Base64;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class JiraConnector extends AbstractConnector {

    private static final Logger LOG = LoggerFactory.getLogger(JiraConnector.class);

    private String urlDomain;

    private String query = "key=${id}";
    private String username;
    private String password;

    public JiraConnector(String urlDomain) {
        this.urlDomain = urlDomain;
    }

    public JiraConnector(String urlDomain, String query) {
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

    protected Set<IssueDetails> getByQuery(String query) {
        Set<IssueDetails> issues = new HashSet<IssueDetails>();
        String response = null;
        try {
            response = get(urlDomain + "/rest/api/2/search?jql=" + URLEncoder.encode(query, "UTF8"));
        } catch (UnsupportedEncodingException e) {
            LOG.warn("can't encode query", e);
        }
        try {
            Iterator<JsonElement> itelm = new JsonParser().parse(response).getAsJsonObject().get("issues").getAsJsonArray().getAsJsonArray().iterator();
            while (itelm.hasNext()) {
                issues.add(parseIssue(itelm.next().toString()));
            }
        }catch (NullPointerException e){
            throw new IllegalStateException("Wrong response.", e);
        }

        return issues;
    }

    private IssueDetails parseIssue(String response) {
        IssueDetails details = new IssueDetails();

        JsonObject root = new JsonParser().parse(response).getAsJsonObject();
        details.setId(root.get("key").getAsString());
        JsonObject jsonFields = root.getAsJsonObject("fields");
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

            if(username != null && password != null) {
                final String userPassword = username + ":" + password;
                String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userPassword.getBytes()));
                conn.setRequestProperty("Authorization", basicAuth);
            }

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

    public void setUsername(String username){
        this.username = username;
    }

    public void setPassword(String password){
        this.password = password;

    }

    @Override
    public String getQuery() {
        return query;
    }
}
