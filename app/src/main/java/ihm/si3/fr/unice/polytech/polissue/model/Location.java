package ihm.si3.fr.unice.polytech.polissue.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class that models the location of an Issue
 */
class Location implements Parcelable{

    private String place;
    private double longitude;
    private double latitude;

    public Location() {
        // Default constructor required for calls to DataSnapshot.getValue(Location.class)
    }

    /**
     * Full constructor for a location
     * @param place the place where the issue is
     * @param longitude the longitude coordinate from the GPS
     * @param latitude the latitude coordinate from the GPS
     */
    public Location(String place, double longitude, double latitude) {
        this.place = place;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * Default constructor for a location, having only a place as a String
     * @param place the place where the issue is
     */
    public Location(String place) {
        this.place = place;
    }

    protected Location(Parcel in) {
        place = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(place);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
    }
}
