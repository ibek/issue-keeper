package qa.tools.ikeeper;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qa.tools.ikeeper.action.IAction;
import qa.tools.ikeeper.client.ITrackerClient;
import qa.tools.ikeeper.test.IKeeperConnector;

public class IKeeperInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(IKeeperInterceptor.class);

    private static final String constraintsPropFileName = "ikeeperConstraints.properties";

    private static Map<String, String> issueConstraints = new HashMap<String, String>();

    static {
        loadIssueConstraints();
    }

    private boolean enabled = true;

    public IKeeperInterceptor() {

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

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private static void loadIssueConstraints() {
        Properties envProps = new Properties();

        InputStream inputStream = IKeeperConnector.class.getClassLoader().getResourceAsStream(constraintsPropFileName);

        if (inputStream != null) {
            try {
                envProps.load(inputStream);

                for (Entry<Object, Object> e : envProps.entrySet()) {
                    issueConstraints.put((String) e.getKey(), (String) e.getValue());
                }

            } catch (IOException e) {
                LOG.error(e.getMessage());
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
                action.fail(testName, details);
                return;
            }
        }

        if (evaluationProperties.isEmpty()) {
            if (!action.canRunTest(details)) {
                action.fail(testName, details);
            }
            return; // we do not need to check any issue constraints
        }

        Boolean cfail = null;
        for (String key : evaluationProperties.keySet()) {
            String constraintValue = issueConstraints.get(details.getId() + "-" + key);
            if (constraintValue != null) {
                for (String cv : constraintValue.split(",")) {
                    boolean newCfail = Objects.equals(evaluationProperties.get(key), cv);
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
            if (!action.canRunTest(details)) {
                action.fail(testName, details);
            }
            return;
        }

        String issueDescription = issueConstraints.get(details.getId() + "-" + "description");
        details.setDescription(issueDescription);

        if (cfail && !action.canRunTest(details)) {
            action.fail(testName, details);
        }
    }

}
