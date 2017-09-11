package qa.tools.ikeeper.client.connector;

import qa.tools.ikeeper.IssueDetails;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

public class CacheConnector extends AbstractConnector {

    public static final String DEFAULT_CACHE_FILE_PATH = System.getProperty("user.home") + File.separatorChar + ".ikeeperCache";
    public static Long TIME_VALID = 86400000L; // Cache is valid for 24 hours (in ms)
    public static final String DELIMITER = "$$$";

    private Properties data;
    private Long lastModifiedDate;
    private String cacheFilePath;

    private String query;

    public CacheConnector() {
        this(DEFAULT_CACHE_FILE_PATH);
    }

    public CacheConnector(String cacheFilePath) {
        this.cacheFilePath = cacheFilePath;
        if (data == null) {
            loadData();
        }
    }

    @Override
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public Set<IssueDetails> getIssue(String id) {
        if (query == null || query.isEmpty()) {
            throw new IllegalArgumentException("Client query is not set");
        }

        String idetails = data.getProperty(replacePlaceholders(query, id));
        if (idetails == null) {
            return null;
        }

        if (System.currentTimeMillis() - lastModifiedDate > TIME_VALID) {
            return null;
        }

        Set<IssueDetails> issueDetailsSet = new HashSet<IssueDetails>();
        for (String issue : idetails.split(Pattern.quote(DELIMITER))) {
            if(issue.isEmpty()){
                continue;
            }
            IssueDetails details = new IssueDetails();

            String[] iparts = issue.split(",");
            details.setId(iparts[0]);
            details.setTitle(iparts[1]);
            details.setProject(iparts[2]);
            String targetVersion = iparts[3];
            if (!targetVersion.equals("null")) {
                details.setTargetVersion(targetVersion);
            }
            String status = iparts[4];
            details.setStatusName(status);
            issueDetailsSet.add(details);

        }


        return issueDetailsSet;
    }

    public void addIssueDetails(String id, String query, Set<IssueDetails> details) {
        StringBuilder builder = new StringBuilder();
        for (IssueDetails d : details) {
            builder.append(d.getId() + "," + d.getTitle() + "," + d.getProject() + "," + d.getTargetVersion() + "," + d.getStatusName());
            builder.append(DELIMITER);
        }
        String idetail = builder.toString();
        if(!details.isEmpty()) {
            idetail = idetail.substring(0, builder.length() - DELIMITER.length());
        }

        data.setProperty(replacePlaceholders(query, id), idetail);
    }

    public void saveData() {
        try {
            data.store(new FileOutputStream(cacheFilePath), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        data = new Properties();
        File cache = new File(cacheFilePath);
        if (!cache.exists()) {
            return;
        }
        try {
            lastModifiedDate = cache.lastModified();
            data.load(new FileInputStream(cache));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
