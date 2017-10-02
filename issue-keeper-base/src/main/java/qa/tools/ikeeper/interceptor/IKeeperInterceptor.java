package qa.tools.ikeeper.interceptor;

import qa.tools.ikeeper.action.IAction;
import qa.tools.ikeeper.client.ITrackerClient;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

public interface IKeeperInterceptor {

    void intercept(String testName, IAction action, List<Annotation> annotations, ITrackerClient[] clients, Map<String, String> evaluationProperties);

    void setEnabled(boolean enabled);
}
