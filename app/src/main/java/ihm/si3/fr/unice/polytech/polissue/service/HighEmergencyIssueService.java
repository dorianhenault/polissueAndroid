package ihm.si3.fr.unice.polytech.polissue.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ihm.si3.fr.unice.polytech.polissue.model.Emergency;
import ihm.si3.fr.unice.polytech.polissue.model.IssueModel;
import ihm.si3.fr.unice.polytech.polissue.notifications.IssueNotificationBuilder;

public class HighEmergencyIssueService extends Service {



    public HighEmergencyIssueService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("mishap");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    IssueModel issueModel = snapshot.getValue(IssueModel.class);
                    if (issueModel.emergency.equals(Emergency.HIGH)){
                        IssueNotificationBuilder builder = new IssueNotificationBuilder(issueModel, getApplicationContext());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
