package kaaes.spotify.webapi.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

/**
 * Created by thibautcolin on 19/10/2017.
 */

public class Context implements Parcelable {
    public String uri;
    public String href;
    public Map<String, String> external_urls;
    public String type;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uri);
        dest.writeString(href);
        dest.writeMap(external_urls);
        dest.writeString(type);
    }

    public Context() {
    }

    protected Context(Parcel in) {
        this.uri = in.readString();
        this.href = in.readString();
        this.external_urls = in.readHashMap(Map.class.getClassLoader());
        this.type = in.readString();
    }

    public static final Parcelable.Creator<Context> CREATOR = new Parcelable.Creator<Context>() {
        public Context createFromParcel(Parcel source) {
            return new Context(source);
        }

        public Context[] newArray(int size) {
            return new Context[size];
        }
    };
}
