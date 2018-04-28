package ihm.si3.fr.unice.polytech.polissue.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Class that models an issue
 */
public class IssueModel implements Parcelable{

    public String title;
    public String description;
    public Date date;
    public Emergency emergency;
    public Location location;
    //public int userID;
    //TODO temporarly replaces  the userID
    public String userName;
    public String imageURL;


    public IssueModel() {
        // Default constructor required for calls to DataSnapshot.getValue(IssueModel.class)
    }

    /**
     * Minimalistic constructor for an issue
     * @param title the title of the issue
     * @param description the description of the issue
     * @param date the date of declaration of the issue
     * @param emergency the emergency level of the issue
     */
    public IssueModel(String title, String description, Date date, Emergency emergency,String userName /*int userID*/) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.emergency = emergency;
        //this.userID = userID;
        this.userName=userName;

    }

    /**
     * Full constructor for an issue
     * @param title the title of the issue
     * @param description the description of the issue
     * @param date the date of declaration of the issue
     * @param emergency the emergency level of the issue
     * @param location the location of the issue
     * @param userName the userID who declared the issue
     * @param imageURL the image URL of the issue
     */
    public IssueModel(String title, String description, Date date, Emergency emergency, Location location,String userName /*int userID*/, String imageURL) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.emergency = emergency;
        this.location = location;
        //this.userID = userID;
        this.userName=userName;
        this.imageURL = imageURL;
    }


    protected IssueModel(Parcel in) {
        title = in.readString();
        description = in.readString();
        date = new Date(in.readLong());
        emergency = Emergency.valueOf(in.readString());
        location = in.readParcelable(Location.class.getClassLoader());
        //userID = in.readInt();
        userName=in.readString();

        imageURL = in.readString();
    }

    public static final Creator<IssueModel> CREATOR = new Creator<IssueModel>() {
        @Override
        public IssueModel createFromParcel(Parcel in) {
            return new IssueModel(in);
        }

        @Override
        public IssueModel[] newArray(int size) {
            return new IssueModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeLong(date.getTime());
        dest.writeString(emergency.name());
        dest.writeParcelable(location, PARCELABLE_WRITE_RETURN_VALUE);
        //dest.writeInt(userID);
        dest.writeString(userName);
        dest.writeString(imageURL);
    }


}
