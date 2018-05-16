package ihm.si3.fr.unice.polytech.polissue.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.RemoteMessage;

import ihm.si3.fr.unice.polytech.polissue.model.IssueModel;
import ihm.si3.fr.unice.polytech.polissue.notifications.IssueNotificationBuilder;

public class NotifyUserService extends Service {

    private static final String TAG = "NotifyUserService";

    private DatabaseReference ref;
    private DatabaseReference notifRef;
    private ValueEventListener listener;
    private ValueEventListener notifListener;



    @Override
    public void onCreate() {
        Log.d(TAG, "Starting Service");
        super.onCreate();
        ref = FirebaseDatabase.getInstance().getReference();

        notifListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("notifications")){
                    notifRef = ref.child("notifications");
                    listener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                String notifier = dataSnapshot.child("notifier").getValue(String.class);
                                String notified = dataSnapshot.child("notified").getValue(String.class);
                                String issueID = dataSnapshot.child("issueID").getValue(String.class);
                                if (FirebaseAuth.getInstance().getUid().equals(notified)) {
                                    DatabaseReference issuesRef = FirebaseDatabase.getInstance().getReference().child("mishap").child(issueID);
                                    final IssueModel[] issue = new IssueModel[1];
                                    ValueEventListener issueListener = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot issueSnapshot : dataSnapshot.getChildren()) {
                                                issue[0] = issueSnapshot.getValue(IssueModel.class);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    };
                                    issuesRef.addValueEventListener(issueListener);
                                    IssueNotificationBuilder builder = new IssueNotificationBuilder(issue[0], getBaseContext());
                                    builder.build();
                                    issuesRef.removeEventListener(issueListener);
                                    notifRef.child(snapshot.getKey()).removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        ref.addListenerForSingleValueEvent(notifListener);
        if (listener != null) {
            notifRef.addValueEventListener(listener);
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ref.removeEventListener(notifListener);
        notifRef.removeEventListener(listener);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
