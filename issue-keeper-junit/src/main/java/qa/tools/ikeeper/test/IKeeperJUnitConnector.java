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

public class IKeeperJUnitConnector extends IKeeperConnector implements TestRule {

    public IKeeperJUnitConnector(ITrackerClient... clients) {
        this(null, clients);
    }

    public IKeeperJUnitConnector(String testVersion, ITrackerClient... clients) {
        super(testVersion, clients);

        action = new Skip(); // skip is the default action
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
