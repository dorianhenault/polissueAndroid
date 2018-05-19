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
        return new IssueModel(snapshot.getKey(), snapshot.child("title").getValue(String.class),
                snapshot.child("description").getValue(String.class),
                snapshot.child("date").getValue(Date.class), snapshot.child("emergency").getValue(Emergency.class),
                snapshot.child("location").getValue(Location.class), snapshot.child("userName").getValue(String.class),
                snapshot.child("imagePath").getValue(String.class),
                snapshot.child("state").getValue(State.class)
                );
    }
}
