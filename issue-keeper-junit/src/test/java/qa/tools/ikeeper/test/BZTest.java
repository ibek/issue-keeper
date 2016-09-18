package qa.tools.ikeeper.test;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.Test;

import qa.tools.ikeeper.annotation.BZ;
import qa.tools.ikeeper.test.base.BZTestBase;

public class BZTest extends BZTestBase {

    private static final List<String> executed = new ArrayList<String>();

    @AfterClass
    public static void checkExecutions() {
        Assertions.assertThat(executed).hasSize(2);
        Assertions.assertThat(executed).contains("runVerifiedIssueTest", "runMultipleVerifiedIssuesTest");
    }

    @BZ("1155593")
    @Test
    public void runVerifiedIssueTest() {
        executed.add("runVerifiedIssueTest");
    }

    @BZ({"1155593", "1203640"})
    @Test
    public void runMultipleVerifiedIssuesTest() {
        executed.add("runMultipleVerifiedIssuesTest");
    }

    @BZ("1217371")
    @Test
    public void ignoreNewIssueTest() {
        executed.add("ignoreNewIssueTest");
        System.out.println("ignoreNewIssueTest");
    }

    @BZ({"1217371", "1203640"})
    @Test
    public void ignoreNewAndVerifiedIssuesTest() {
        executed.add("ignoreNewAndVerifiedIssuesTest");
        System.out.println("ignoreNewAndVerifiedIssuesTest");
    }

}
