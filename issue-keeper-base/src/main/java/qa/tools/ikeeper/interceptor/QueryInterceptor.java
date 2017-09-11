package qa.tools.ikeeper.interceptor;

import qa.tools.ikeeper.IssueDetails;
import qa.tools.ikeeper.action.IAction;
import qa.tools.ikeeper.client.ITrackerClient;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * QueryInterceptor executes an action if some issues are returned by client.
 */

public class QueryInterceptor extends AbstractIKeeperInterceptor {
    @Override
    public void intercept(String testName, IAction action, List<Annotation> annotations, ITrackerClient[] clients, Map<String, String> evaluationProperties) {
        if (!enabled) {
            return;
        }
        for (Annotation annotation : annotations) {
            for (ITrackerClient c : clients) {
                if (c.canHandle(annotation)) {


                    List<IssueDetails> detailsList = c.getIssues(annotation);
                    Iterator<IssueDetails> iterator = detailsList.iterator();

                    //process issue constraints
                    while (iterator.hasNext()) {
                        IssueDetails details = iterator.next();
                        for (String key : evaluationProperties.keySet()) {
                            String constraintValue = issueConstraints.get(details.getId() + "-" + key);
                            if (constraintValue != null) {
                                List<String> cvs = Arrays.asList(constraintValue.split(","));
                                if (!cvs.contains(evaluationProperties.get(key))) {
                                    iterator.remove();
                                }
                            } else {
                                String regexpConstraintValue = issueConstraints.get(details.getId() + "-" + key + "~");
                                if (regexpConstraintValue != null) {
                                    if (!evaluationProperties.get(key).matches(regexpConstraintValue))
                                        iterator.remove();
                                }
                            }
                        }

                        // if there is some issue remaining in list - perform action
                        if (!detailsList.isEmpty()) {
                            action.perform(testName, detailsList);
                            break;
                        }

                    }
                }
            }
        }
    }
}
