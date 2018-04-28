package ihm.si3.fr.unice.polytech.polissue.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Class that models a user
 */
class User implements Parcelable{

    public int id;
    public String email;
    public String lastName;
    public String firstName;
    public Date birthDate;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    /**
     * Constructor for User
     * @param id the id of the user
     * @param email the email of the user
     * @param lastName the lastName of the user
     * @param firstName the first name of the user
     * @param birthDate the birth date of the user
     */
    public User(int id, String email, String lastName, String firstName, Date birthDate) {
        this.id = id;
        this.email = email;
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthDate = birthDate;
    }

    protected User(Parcel in) {
        id = in.readInt();
        email = in.readString();
        lastName = in.readString();
        firstName = in.readString();
        birthDate = new Date(in.readLong());
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(email);
        dest.writeString(lastName);
        dest.writeString(firstName);
        dest.writeLong(birthDate.getTime());
    }
}
