package com.example.tcolin.myspotifyapp.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.tcolin.myspotifyapp.Activities.MainActivity;
import com.example.tcolin.myspotifyapp.Observables.ObservableObject;

/**
 * Created by tcolin on 08/09/2017.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {

    public static final class BroadcastTypes {
        public static final String SPOTIFY_PACKAGE = "com.spotify.music";
        public static final String PLAYBACK_STATE_CHANGED = SPOTIFY_PACKAGE + ".playbackstatechanged";
        public static final String QUEUE_CHANGED = SPOTIFY_PACKAGE + ".queuechanged";
        public static final String METADATA_CHANGED = SPOTIFY_PACKAGE + ".metadatachanged";
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // This is sent with all broadcasts, regardless of type. The value is taken from
        // System.currentTimeMillis(), which you can compare to in order to determine how
        // old the event is.
        ObservableObject.getInstance().updateValue(intent);
    }
}
