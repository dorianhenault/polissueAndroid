package ihm.si3.fr.unice.polytech.polissue.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Class that models an issue
 */
public class IssueModel implements Parcelable{

    private String title;
    private String description;
    private Date date;
    private Emergency emergency;
    private Location location;
    //public int userID;
    //TODO temporarly replaces  the userID
    private String userName;
    private String imagePath;


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
     * @param imagePath the image URL of the issue
     */
    public IssueModel(String title, String description, Date date, Emergency emergency, Location location,String userName /*int userID*/, String imagePath) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.emergency = emergency;
        this.location = location;
        //this.userID = userID;
        this.userName=userName;
        this.imagePath = imagePath;
    }


    protected IssueModel(Parcel in) {
        title = in.readString();
        description = in.readString();
        date = new Date(in.readLong());
        emergency = Emergency.valueOf(in.readString());
        location = in.readParcelable(Location.class.getClassLoader());
        //userID = in.readInt();
        userName=in.readString();

        imagePath = in.readString();
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
        dest.writeString(imagePath);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Emergency getEmergency() {
        return emergency;
    }

    public void setEmergency(Emergency emergency) {
        this.emergency = emergency;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
