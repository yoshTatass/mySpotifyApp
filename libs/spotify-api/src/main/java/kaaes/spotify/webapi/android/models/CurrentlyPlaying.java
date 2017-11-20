package kaaes.spotify.webapi.android.models;

import android.os.Parcel;
import android.os.Parcelable;

public class CurrentlyPlaying implements Parcelable {
    public Device device;
    public String repeatState;
    public Boolean shuffleState;
    public Context context;
    public Long timestamp;
    public Integer progress_ms;
    public Boolean is_playing;
    public Track item;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.device, 0);
        dest.writeString(this.repeatState);
        dest.writeValue(this.shuffleState);
        dest.writeParcelable(this.context, 0);
        dest.writeValue(this.timestamp);
        dest.writeValue(this.progress_ms);
        dest.writeValue(this.is_playing);
        dest.writeParcelable(this.item, 0);
    }

    public CurrentlyPlaying() {
    }

    protected CurrentlyPlaying(Parcel in) {
        this.device = in.readParcelable(Device.class.getClassLoader());
        this.repeatState = in.readString();
        this.shuffleState = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.context = in.readParcelable(Context.class.getClassLoader());
        this.timestamp = in.readLong();
        this.progress_ms = in.readInt();
        this.is_playing = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.item = in.readParcelable(Track.class.getClassLoader());
    }

    public static final Parcelable.Creator<CurrentlyPlaying> CREATOR = new Parcelable.Creator<CurrentlyPlaying>() {
        public CurrentlyPlaying createFromParcel(Parcel source) {
            return new CurrentlyPlaying(source);
        }

        public CurrentlyPlaying[] newArray(int size) {
            return new CurrentlyPlaying[size];
        }
    };
}
