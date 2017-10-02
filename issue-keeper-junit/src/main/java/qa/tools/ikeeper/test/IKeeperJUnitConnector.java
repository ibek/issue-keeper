package qa.tools.ikeeper.test;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import qa.tools.ikeeper.action.Skip;
import qa.tools.ikeeper.client.ITrackerClient;
import qa.tools.ikeeper.interceptor.IKeeperInterceptor;

public class IKeeperJUnitConnector extends IKeeperConnector implements TestRule {

    static {
        // skip is the default action
        action = new Skip();
    }

    public IKeeperJUnitConnector(ITrackerClient... clients) {
        super(clients);
    }

    public IKeeperJUnitConnector(IKeeperInterceptor interceptor, ITrackerClient... clients){
        super(interceptor,clients);
    }


    public IKeeperJUnitConnector(String testVersion, ITrackerClient... clients) {
        super(testVersion, clients);

    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                List<Annotation> annotations = new ArrayList<Annotation>();
                annotations.addAll(description.getAnnotations());
                annotations.addAll(Arrays.asList(description.getTestClass().getAnnotations()));

                interceptor.intercept(description.getMethodName(), action, annotations, clients, environmentProperties);

                base.evaluate();

            }
        };
    }

}
