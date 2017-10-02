package qa.tools.ikeeper.action;

import java.util.List;
import java.util.Set;

import qa.tools.ikeeper.IssueDetails;

class ActionMessage {

    public static String generate(String testName, List<IssueDetails> details, String result) {
        StringBuilder builder = new StringBuilder(testName + " - this test " + result + " due to:\n\t");
        for(IssueDetails detail : details) {
            String description = detail.getDescription();
             builder.append(formatId(detail.getId()) + " " + detail.getTitle() + "\n\tstatus: " + detail.getStatusName() + formatDescription(description) + "\n");
        }
        return builder.toString();
    }

    private static String formatId(String id) {
        return id.matches("[0-9].*") ? "BZ-" + id : id;
    }

    private static String formatDescription(String description) {
        return description == null || description.isEmpty() ? "" : "\n\tdescription: " + description;
    }
}
