package com.example.tcolin.myspotifyapp.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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

    private String trackId, artistName, albumName, trackName = "" ;

    @Override
    public void onReceive(Context context, Intent intent) {
        // This is sent with all broadcasts, regardless of type. The value is taken from
        // System.currentTimeMillis(), which you can compare to in order to determine how
        // old the event is.
        long timeSentInMs = intent.getLongExtra("timeSent", 0L);

        String action = intent.getAction();

        if (action.equals(BroadcastTypes.METADATA_CHANGED)) {
            Log.e("MainActivity", "trigger");
            trackId = intent.getStringExtra("id");
            artistName = intent.getStringExtra("artist");
            albumName = intent.getStringExtra("album");
            trackName = intent.getStringExtra("track");
            int trackLengthInSec = intent.getIntExtra("length", 0);
            // Do something with extracted information...
        } else if (action.equals(BroadcastTypes.PLAYBACK_STATE_CHANGED)) {
            boolean playing = intent.getBooleanExtra("playing", false);
            int positionInMs = intent.getIntExtra("playbackPosition", 0);
            // Do something with extracted information
        } else if (action.equals(BroadcastTypes.QUEUE_CHANGED)) {
            // Sent only as a notification, your app may want to respond accordingly.
        }
    }

    public String getTrackId() {
        return trackId;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getTrackName() {
        return trackName;
    }
}
