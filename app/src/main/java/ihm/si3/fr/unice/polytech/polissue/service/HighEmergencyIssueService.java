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
    private static final String TAG = "HighEmergencyIssueService";
    private DatabaseReference ref;
    private ValueEventListener listener;


    @Override
    public void onCreate() {
        super.onCreate();
        ref = FirebaseDatabase.getInstance().getReference().child("mishap");
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    IssueModel issueModel = snapshot.getValue(IssueModel.class);
                    if (issueModel.getEmergency().equals(Emergency.HIGH)) {
                        IssueNotificationBuilder builder = new IssueNotificationBuilder(issueModel, getApplicationContext());
                        builder.build();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ref.addListenerForSingleValueEvent(listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ref.removeEventListener(listener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}
