package ihm.si3.fr.unice.polytech.polissue;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import ihm.si3.fr.unice.polytech.polissue.model.IssueModel;
import ihm.si3.fr.unice.polytech.polissue.model.User;

/**
 * Created by doh06 on 20/04/2018.
 */

public class DataBaseAccess {

    private static final String TAG = "DataBaseAccess";
    private FirebaseDatabase database;

    public DataBaseAccess(){
        database = FirebaseDatabase.getInstance();
    }



    /**
     * Method used to post and issue to the database
     * @param issue the issue to post to the server
     */
    public void postIssue(IssueModel issue){
        DatabaseReference issueRef = database.getReference().child("mishap");
        String key = issueRef.push().getKey();
        issueRef.child(key).child("title").setValue(issue.getTitle());
        issueRef.child(key).child("description").setValue(issue.getDescription());
        issueRef.child(key).child("date").setValue(issue.getDate().getTime());
        issueRef.child(key).child("emergency").setValue(issue.getEmergency());
        issueRef.child(key).child("location").setValue(issue.getLocation());
        issueRef.child(key).child("userID").setValue(issue.getUserID());
        issueRef.child(key).child("imagePath").setValue(issue.getImagePath());
        issueRef.child(key).child("state").setValue(issue.getState());
    }

    /**
     * Posts a notification to firebase database
     * @param user the user to notify
     * @param issue the issue where the user has been notified
     */
    public void postNotification(User user, IssueModel issue){
        DatabaseReference notificationRef = database.getReference().child("notifications");
        String key = notificationRef.push().getKey();
        notificationRef.child(key).child("notifier").setValue(FirebaseAuth.getInstance().getUid());
        notificationRef.child(key).child("notified").setValue(user.getId());
        notificationRef.child(key).child("issueID").setValue(issue.getId());
    }

}
