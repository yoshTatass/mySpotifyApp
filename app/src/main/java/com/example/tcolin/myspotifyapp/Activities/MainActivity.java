package com.example.tcolin.myspotifyapp.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;

import jp.wasabeef.picasso.transformations.BlurTransformation;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import jp.wasabeef.picasso.transformations.GrayscaleTransformation;
import jp.wasabeef.picasso.transformations.gpu.BrightnessFilterTransformation;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.UserPrivate;
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
        UserPrivate me = getMe();
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
        final Intent intent = (Intent) o;
        final String action = intent.getAction();

        if (action.equals(MyBroadcastReceiver.BroadcastTypes.METADATA_CHANGED)) {
            Log.e("MainActivity", "trigger");
            Track track;
            try {
                track = getTrack(getId(intent.getStringExtra("id")));
                updateAlbumCover(track);
            } catch (Exception e) {
            }
            // Do something with extracted information...
        } else if (action.equals(MyBroadcastReceiver.BroadcastTypes.PLAYBACK_STATE_CHANGED)) {
            boolean playing = intent.getBooleanExtra("playing", false);
            Log.e("MainActivity", "trigger playbackStateChange ( " + playing + " )");
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

    private UserPrivate getMe() {
        try {
            return new AsyncTask<Void, Void, UserPrivate>() {
                @Override
                protected UserPrivate doInBackground(Void... voids) {
                    return spotify.getMe();
                }

                @Override
                protected void onPostExecute(UserPrivate userPrivate) {
                    ImageView user = findViewById(R.id.user);
                    Picasso.with(context).load(userPrivate.images.get(0).url).transform(new CropCircleTransformation()).into(user);
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
        } catch (Exception e) {
            return null;
        }
    }

    private Track getTrack(final String trackId) throws Exception {
        return new AsyncTask<Void, Void, Track>() {
            @Override
            protected Track doInBackground(Void... voids) {
                return spotify.getTrack(trackId);
            }

            @Override
            protected void onPostExecute(Track track) {
                TextView title = findViewById(R.id.title);
                title.setText(track.name);
                TextView album = findViewById(R.id.album);
                album.setText(track.album.name);
                TextView artist = findViewById(R.id.artist);
                artist.setText(track.artists.get(0).name);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
    }

    private void updateAlbumCover(final Track track) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return track.album.images.get(0).url;
            }

            @Override
            protected void onPostExecute(String coverUrl) {
                final FadeInNetworkImageView cover = findViewById(R.id.cover);
                cover.setImageUrl(coverUrl, MainApplication.getInstance(context).getImageLoader());

                final ImageView layoutView = findViewById(R.id.layoutView);
                Picasso.with(context).load(coverUrl)
                        .transform(new BlurTransformation(context, 15)).transform(new BrightnessFilterTransformation(context, new Float(-0.3))).into(layoutView);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}