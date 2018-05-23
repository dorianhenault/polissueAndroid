package ihm.si3.fr.unice.polytech.polissue.factory;

import com.google.firebase.database.DataSnapshot;

import java.util.Date;

import ihm.si3.fr.unice.polytech.polissue.model.Emergency;
import ihm.si3.fr.unice.polytech.polissue.model.IssueModel;
import ihm.si3.fr.unice.polytech.polissue.model.Location;
import ihm.si3.fr.unice.polytech.polissue.model.State;

public class IssueModelFactory {

    public IssueModelFactory() {
    }

    public IssueModel forge(DataSnapshot snapshot){
        String key = snapshot.getKey();
        IssueModel model = new IssueModel(key);
        model.setTitle(snapshot.child("title").getValue(String.class));
        model.setDescription(snapshot.child("description").getValue(String.class));
        model.setDate(snapshot.child("date").getValue(Long.class));
        model.setEmergency(snapshot.child("emergency").getValue(Emergency.class));
        model.setLocation(snapshot.child("location").getValue(Location.class));
        model.setUserID(snapshot.child("userID").getValue(String.class));
        model.setImagePath(snapshot.child("imagePath").getValue(String.class));
        model.setState(snapshot.child("state").getValue(State.class));

        return model;
    }
}
