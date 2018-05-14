package ihm.si3.fr.unice.polytech.polissue.service;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService{

    private static final String ALL_USERS = "all_users";

    public MyFirebaseInstanceIdService() {
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic(ALL_USERS);
    }
}
