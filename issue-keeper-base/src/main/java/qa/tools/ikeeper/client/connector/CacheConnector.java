package qa.tools.ikeeper.client.connector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import qa.tools.ikeeper.IssueDetails;

public class CacheConnector implements IssueTrackingSystemConnector {

    public static final String DEFAULT_CACHE_FILE_PATH = System.getProperty("user.home") + File.separatorChar + ".ikeeperCache";
    public static Long TIME_VALID = 86400000L; // Cache is valid for 24 hours (in ms)

    private Properties data;
    private Long lastModifiedDate;
    private String cacheFilePath;

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
    public IssueDetails getIssue(String id) {

        String idetails = data.getProperty(id);
        if (idetails == null) {
            return null;
        }

        if (System.currentTimeMillis() - lastModifiedDate > TIME_VALID) {
            return null;
        }

        IssueDetails details = new IssueDetails();
        details.setId(id);

        String[] iparts = idetails.split(",");
        details.setTitle(iparts[0]);
        details.setProject(iparts[1]);
        String targetVersion = iparts[2];
        if (!targetVersion.equals("null")) {
            details.setTargetVersion(targetVersion);
        }
        String status = iparts[3];
        details.setStatusName(status);

        return details;
    }

    public void addIssueDetails(IssueDetails details) {
        data.setProperty(details.getId(), details.getTitle() + "," + details.getProject() + "," + details.getTargetVersion() + "," + details.getStatusName());
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
