package qa.tools.ikeeper;

public class IssueDetails {

    private String id;
    private String title;
    private String project;
    private String statusName;
    private String targetVersion;
    /**
     * Description is added from iKeeperConstraints.properties defined as id-description=text.
     */
    private String description;

    public IssueDetails() {
    }

    public IssueDetails(final String id, final String title, final String project, final String statusName, final String targetVersion) {
        this.id = id;
        this.title = title;
        this.project = project;
        this.statusName = statusName;
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
    
    public String getProject() {
        return project;
    }
    
    public void setProject(String project) {
        this.project = project;
    }
    
    public String getStatusName() {
        return statusName;
    }
    
    public void setStatusName(String statusName) {
        this.statusName = statusName;
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
        return id + ":" + title + ":" + project + ":" + statusName + ":" + description + ":" + targetVersion;
    }
}
