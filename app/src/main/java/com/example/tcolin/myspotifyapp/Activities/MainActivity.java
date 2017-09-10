package com.example.tcolin.myspotifyapp.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.example.tcolin.myspotifyapp.MainApplication;
import com.example.tcolin.myspotifyapp.Observables.ObservableObject;
import com.example.tcolin.myspotifyapp.R;
import com.example.tcolin.myspotifyapp.Receivers.MyBroadcastReceiver;
import com.example.tcolin.myspotifyapp.Views.FadeInNetworkImageView;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.Observable;
import java.util.Observer;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends Activity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback, Observer
{

    // TODO: Replace with your client ID
    private static final String CLIENT_ID = "d644bb272241483e9fad4cf350b1125f";
    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "my-spotify-app://callback";

    private Player mPlayer;
    private SpotifyService spotify;

    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int REQUEST_CODE = 1337;
    private Context context;
    private MyBroadcastReceiver myBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myBroadcastReceiver = new MyBroadcastReceiver();

        context = this;

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        SpotifyApi api = new SpotifyApi();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(SpotifyApi.SPOTIFY_WEB_API_ENDPOINT)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Authorization", "Bearer " + CLIENT_ID);
                    }
                })
                .build();

        spotify = restAdapter.create(SpotifyService.class);

        LocalBroadcastManager.getInstance(context).registerReceiver(myBroadcastReceiver, new IntentFilter(MyBroadcastReceiver.BroadcastTypes.METADATA_CHANGED));
        LocalBroadcastManager.getInstance(context).registerReceiver(myBroadcastReceiver, new IntentFilter(MyBroadcastReceiver.BroadcastTypes.PLAYBACK_STATE_CHANGED));

        ObservableObject.getInstance().addObserver(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        mPlayer = spotifyPlayer;
                        mPlayer.addConnectionStateCallback(MainActivity.this);
                        mPlayer.addNotificationCallback(MainActivity.this);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });

                SpotifyApi api = new SpotifyApi();

                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setEndpoint(SpotifyApi.SPOTIFY_WEB_API_ENDPOINT)
                        .setRequestInterceptor(new RequestInterceptor() {
                            @Override
                            public void intercept(RequestFacade request) {
                                request.addHeader("Authorization", "Bearer " + response.getAccessToken());
                            }
                        })
                        .build();

                spotify = restAdapter.create(SpotifyService.class);
            }
        }
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.e("MainActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.e("MainActivity", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.e("MainActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.e("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Error error) {
        Log.e("MainActivity", error.toString());
    }

    @Override
    public void onTemporaryError() {
        Log.e("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.e("MainActivity", "Received connection message: " + message);
    }

    @Override
    public void update(Observable observable, Object o) {
        Intent intent = (Intent) o;
        final String action = intent.getAction();

        if (action.equals(MyBroadcastReceiver.BroadcastTypes.METADATA_CHANGED)) {
            Log.e("MainActivity", "trigger");
            TextView title = findViewById(R.id.title);
            title.setText(intent.getStringExtra("track"));
            Log.e("MainActivity", intent.getExtras().toString());
            final FadeInNetworkImageView cover = findViewById(R.id.cover);
            final FadeInNetworkImageView layoutView = findViewById(R.id.layoutView);
            if (!TextUtils.isEmpty(intent.getStringExtra("albumId"))) {
                spotify.getAlbum(getId(intent.getStringExtra("albumId")), new Callback<Album>() {
                    @Override
                    public void success(Album album, Response response) {
                        cover.setImageUrl(album.images.get(0).url, MainApplication.getInstance(context).getImageLoader());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("MainActivity", "cannot retreive album : " + error.getBody().toString());
                    }
                });
            }
            updateArtistCover());
            // Do something with extracted information...
        } else if (action.equals(MyBroadcastReceiver.BroadcastTypes.PLAYBACK_STATE_CHANGED)) {
            boolean playing = intent.getBooleanExtra("playing", false);
            Log.e("MainActivity", "trigger playbackStateChange ( " + playing + " )");
//            ImageView pause = findViewById(R.id.pause);
//            ImageView play = findViewById(R.id.play);
//            if (playing) {
//                play.setVisibility(View.INVISIBLE);
//                pause.setVisibility(View.VISIBLE);
//            } else {
//                pause.setVisibility(View.INVISIBLE);
//                play.setVisibility(View.VISIBLE);
//            }
            int positionInMs = intent.getIntExtra("playbackPosition", 0);
            // Do something with extracted information
        } else if (action.equals(MyBroadcastReceiver.BroadcastTypes.QUEUE_CHANGED)) {
            // Sent only as a notification, your app may want to respond accordingly.
        }

    }

    private String getId(String objectId) {
        String[] str = objectId.split(":");
        return str[str.length-1];
    }

    private void updateArtistCover(final String trackId) {
        new AsyncTask<Void, Void, Track>() {
            @Override
            protected Track doInBackground(Void... voids) {
                Track track = spotify.getTrack(trackId);
                return track;
            }

            @Override
            protected void onPostExecute(Artist artist) {
                FadeInNetworkImageView layoutView = findViewById(R.id.layoutView);
                layoutView.setImageUrl(artist.images.get(0).url, MainApplication.getInstance(context).getImageLoader());
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}