package kaaes.spotify.webapi.android.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by thibautcolin on 27/10/2017.
 */

public class Device implements Parcelable {

    public String id;
    public Boolean is_active;
    public Boolean is_restricted;
    public String name;
    public String type;
    public Integer volume_percent;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeValue(this.is_active);
        dest.writeValue(this.is_restricted);
        dest.writeValue(this.name);
        dest.writeValue(this.type);
        dest.writeInt(this.volume_percent);
    }

    public Device() {
    }

    protected Device(Parcel in) {
        this.id = in.readString();
        this.is_active = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.is_restricted = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.name = in.readString();
        this.type = in.readString();
        this.volume_percent = in.readInt();
    }

    public static final Parcelable.Creator<Device> CREATOR = new Parcelable.Creator<Device>() {
        public Device createFromParcel(Parcel source) {
            return new Device(source);
        }

        public Device[] newArray(int size) {
            return new Device[size];
        }
    };
}
