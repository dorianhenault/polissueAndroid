package ihm.si3.fr.unice.polytech.polissue.model;

public class MyNotification {


    private String notifier;
    private String notified;
    private String issueID;

    public MyNotification() {
    }

    public MyNotification(String notifier, String notified, String issueID) {
        this.notifier = notifier;
        this.notified = notified;
        this.issueID = issueID;
    }

    public String getNotifier() {
        return notifier;
    }

    public void setNotifier(String notifier) {
        this.notifier = notifier;
    }

    public String getNotified() {
        return notified;
    }

    public void setNotified(String notified) {
        this.notified = notified;
    }

    public String getIssueID() {
        return issueID;
    }

    public void setIssueID(String issueID) {
        this.issueID = issueID;
    }
}
