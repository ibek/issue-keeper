package qa.tools.ikeeper.client;

import java.lang.annotation.Annotation;
import java.util.List;

import qa.tools.ikeeper.IssueDetails;

public interface ITrackerClient {

    public boolean isIssueFixedInTestedVersion(IssueDetails details);
    
    public boolean canHandle(Annotation annotation);
    
    public List<IssueDetails> getIssues(Annotation annotation);

}
