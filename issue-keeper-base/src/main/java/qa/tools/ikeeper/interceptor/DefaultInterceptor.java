package qa.tools.ikeeper.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qa.tools.ikeeper.IssueDetails;
import qa.tools.ikeeper.action.IAction;
import qa.tools.ikeeper.client.ITrackerClient;
import qa.tools.ikeeper.test.IKeeperConnector;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DefaultInterceptor extends AbstractIKeeperInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultInterceptor.class);

    private Map<String, List<String>> projectStates;

    public DefaultInterceptor(Map<String, List<String>> projectStates) {
        this.projectStates = projectStates;
    }

    public void intercept(String testName, IAction action, List<Annotation> annotations, ITrackerClient[] clients, Map<String, String> evaluationProperties) {
        if (!enabled) {
            return;
        }
        for (Annotation annotation : annotations) {
            for (ITrackerClient c : clients) {
                if (c.canHandle(annotation)) {
                    List<IssueDetails> detailsList = c.getIssues(annotation);
                    for (IssueDetails details : detailsList) {
                        if (details != null) {
                            intercept(testName, details, evaluationProperties, action);
                        }
                    }
                    break;
                }
            }
        }
    }

    private void intercept(String testName, IssueDetails details, Map<String, String> evaluationProperties, IAction action) {

        List<String> versionsOrder = IKeeperConnector.getVersionsOrder();
        if (!versionsOrder.isEmpty() && details.getTargetVersion() != null && !details.getTargetVersion().isEmpty() && IKeeperConnector.getTestVersion() != null && !IKeeperConnector.getTestVersion().isEmpty()) {
            int itargetVersion = versionsOrder.indexOf(details.getTargetVersion());
            int itestVersion = versionsOrder.indexOf(IKeeperConnector.getTestVersion());
            if (itestVersion < itargetVersion) {
                // the issue is not fixed in the current testing version
                action.perform(testName, Arrays.asList(details));
                return;
            }
        }

        if (evaluationProperties.isEmpty()) {
            List<String> istates = getIssueProjectStates(details);
            if (istates.contains(details.getStatusName())) {
                action.perform(testName, Arrays.asList(details));
            }
            return; // we do not need to check any issue constraints
        }

        Boolean cfail = null;
        for (String key : evaluationProperties.keySet()) {
            String constraintValue = issueConstraints.get(details.getId() + "-" + key);
            if (constraintValue != null) {
                for (String cv : constraintValue.split(",")) {
                    boolean newCfail = cv.equals(evaluationProperties.get(key));
                    cfail = (cfail == null) ? newCfail : (cfail && newCfail);
                }
            } else {
                String regexpConstraintValue = issueConstraints.get(details.getId() + "-" + key + "~");
                if (regexpConstraintValue != null) {
                    boolean newCfail = evaluationProperties.get(key).matches(regexpConstraintValue);
                    cfail = (cfail == null) ? newCfail : (cfail && newCfail);
                }
            }
        }
        if (cfail == null) {
            List<String> istates = getIssueProjectStates(details);
            if (istates.contains(details.getStatusName())) {
                action.perform(testName, Arrays.asList(details));
            }
            return;
        }

        String issueDescription = issueConstraints.get(details.getId() + "-" + "description");
        details.setDescription(issueDescription);

        List<String> istates = getIssueProjectStates(details);
        if (cfail && istates.contains(details.getStatusName())) {
            action.perform(testName, Arrays.asList(details));
        }
    }

    private List<String> getIssueProjectStates(IssueDetails details) {
        List<String> istates = projectStates.get(details.getProject());
        if (istates == null) {
            String issueType = details.getProject().substring(0, details.getProject().indexOf("@"));
            istates = projectStates.get(issueType + "@DEFAULT");
        }
        return istates;
    }
}
