package qa.tools.ikeeper;

public class IssueDetails {

    private String id;
    private String title;
    private IssueStatus status;
    private String targetVersion;
    /**
     * Description is added from iKeeperConstraints.properties defined as id-description=text.
     */
    private String description;

    public IssueDetails() {
    }

    public IssueDetails(final String id, final String title, final IssueStatus status, final String targetVersion) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.targetVersion = targetVersion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public String getTargetVersion() {
        return targetVersion;
    }

    public void setTargetVersion(String targetVersion) {
        this.targetVersion = targetVersion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return id + ":" + title + ":" + status + ":" + description + ":" + targetVersion;
    }
}
