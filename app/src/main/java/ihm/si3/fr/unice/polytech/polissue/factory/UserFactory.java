package ihm.si3.fr.unice.polytech.polissue.factory;

import com.google.firebase.database.DataSnapshot;

import ihm.si3.fr.unice.polytech.polissue.model.User;

public class UserFactory {


    public User forge(DataSnapshot snapshot) {
        return new User(snapshot.getKey(), snapshot.child("email").getValue(String.class),
                snapshot.child("username").getValue(String.class), snapshot.child("photoUrl").getValue(String.class));
    }

}
