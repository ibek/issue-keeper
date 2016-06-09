package qa.tools.ikeeper.action;

import qa.tools.ikeeper.IssueDetails;

class ActionMessage {

    public static String generate(String testName, IssueDetails details, String result) {
        String description = details.getDescription();
        String msg = testName + " - this test " + result + " due to:\n\t" + formatId(details.getId()) + " "
                + details.getTitle() + "\n\tstatus: " + details.getStatus() + formatDescription(description);
        return msg;
    }

    private static String formatId(String id) {
        return id.matches("[0-9].*") ? "BZ-" + id : id;
    }

    private static String formatDescription(String description) {
        return description == null || description.isEmpty() ? "" : "\n\tdescription: " + description;
    }
}
