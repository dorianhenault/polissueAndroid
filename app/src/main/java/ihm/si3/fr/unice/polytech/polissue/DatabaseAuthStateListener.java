package ihm.si3.fr.unice.polytech.polissue;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DatabaseAuthStateListener implements AuthStateListener {
    private DatabaseReference userReference;

    public DatabaseAuthStateListener() {
        userReference = FirebaseDatabase.getInstance().getReference("users");
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null && !user.isAnonymous()) {
            updateUserInfo(user);
        }
    }

    private void updateUserInfo(FirebaseUser user) {
        HashMap<String, String> userValues = new HashMap<>();
        userValues.put("email", user.getEmail());
        userValues.put("username", user.getDisplayName());
        userValues.put("photoUrl", String.valueOf(user.getPhotoUrl()));
        userReference.child(user.getUid()).setValue(userValues);
    }
}
