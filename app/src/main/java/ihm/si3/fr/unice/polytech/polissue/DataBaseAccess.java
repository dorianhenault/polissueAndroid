package ihm.si3.fr.unice.polytech.polissue;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import ihm.si3.fr.unice.polytech.polissue.model.IssueModel;
import ihm.si3.fr.unice.polytech.polissue.model.MyNotification;

/**
 * Created by doh06 on 20/04/2018.
 */

public class DataBaseAccess {

    private static final String TAG = "DataBaseAccess";
    private FirebaseDatabase database;

    public DataBaseAccess(){
        database = FirebaseDatabase.getInstance();
    }

    public void sendData(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference();
        DatabaseReference modelUser=dbRef.child("user").child("user1");
        modelUser.child("id").setValue(0);
        modelUser.child("email").setValue("JeanBonbeur@etu.unice.fr");
        modelUser.child("firstName").setValue("Jean");
        modelUser.child("name").setValue("Bombeur");
        modelUser.child("birthDate").setValue(new Date());
        modelUser.child("password").setValue("JulJTM");

        DatabaseReference modelMishap=dbRef.child("mishap").child("mishap1");
        modelMishap.child("id").setValue(0);
        modelMishap.child("title").setValue("test");
        modelMishap.child("description").setValue("AnotherTest");
        modelMishap.child("date").setValue(new Date());
        modelMishap.child("emergency").setValue("LOW");
        modelMishap.child("location").setValue("");
        modelMishap.child("declarantId").setValue(0);
        modelMishap.child("photo").setValue("URL");

        DatabaseReference modelLocation=dbRef.child("location").child("location1");
        modelLocation.child("place").setValue("Dolines");
        modelLocation.child("longitude").setValue("0,0");
        modelLocation.child("latitude").setValue("0,0");

    }

    /**
     * Method used to post and issue to the database
     * @param issue the issue to post to the server
     */
    public void postIssue(IssueModel issue){
        DatabaseReference issueRef = database.getReference().child("mishap");
        issueRef.push().setValue(issue);
    }


    public void postNotification(MyNotification notification){
        DatabaseReference notificationRef = database.getReference().child("notifications");
        notificationRef.push().setValue(notification);
    }

}
