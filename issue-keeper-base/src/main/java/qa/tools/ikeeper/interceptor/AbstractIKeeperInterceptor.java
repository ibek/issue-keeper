package qa.tools.ikeeper.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qa.tools.ikeeper.action.IAction;
import qa.tools.ikeeper.client.ITrackerClient;
import qa.tools.ikeeper.test.IKeeperConnector;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public abstract class AbstractIKeeperInterceptor implements IKeeperInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractIKeeperInterceptor.class);

    protected static final String constraintsPropFileName = "ikeeperConstraints.properties";

    protected static Map<String, String> issueConstraints = new HashMap<String, String>();

    static {
        loadIssueConstraints();
    }

    protected boolean enabled = true;

    public abstract void intercept(String testName, IAction action, List<Annotation> annotations, ITrackerClient[] clients, Map<String, String> evaluationProperties);

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
}
