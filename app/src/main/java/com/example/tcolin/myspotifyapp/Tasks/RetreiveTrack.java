package com.example.tcolin.myspotifyapp.Tasks;

import android.os.AsyncTask;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by thibautcolin on 09/09/2017.
 */

public class RetreiveTrack extends AsyncTask<Void, Track, Void> {
    @Override
    protected Void doInBackground(Void... voids) {
        SpotifyApi api = new SpotifyApi();
        SpotifyService service = api.getService();
        return null;
    }
}
