package com.shorincity.vibin.music_sharing.UI;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.shorincity.vibin.music_sharing.model.APIResponse;
import com.shorincity.vibin.music_sharing.model.SotifyMeResponse;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.fragment.SpotifyErrorDialogFragment;
import com.shorincity.vibin.music_sharing.fragment.UserNotificationFragment;
import com.shorincity.vibin.music_sharing.fragment.UserProfileFragment;
import com.shorincity.vibin.music_sharing.fragment.UserSearchFragment;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.spotify_files.SpotifyHomeFragment;
import com.shorincity.vibin.music_sharing.spotify_files.SpotifyUser;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.CustomSlidePanLayout;
import com.shorincity.vibin.music_sharing.fragment.PublicPlaylistFragment;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class spotify extends AppCompatActivity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback, SpotifyErrorDialogFragment.YoutubeButtonListener {

    FrameLayout mMainFrame;

    PublicPlaylistFragment publicPlaylistFragment;
    SpotifyHomeFragment spotifyHomeFragment;
    SpotifyUser spotifyUser;

    // Swati's Client Id
    private static final String CLIENT_ID = AppConstants.SPOTIFY_KEY;
    private static final String REDIRECT_URI = "http://vibin.in/callback/";

    // Swati's Changes
    //private static final String REDIRECT_URI = "com.http://vibin.in.vibin://callback";

    private static final int REQUEST_CODE = 1337;
    public static String spotifyAccessToken = "";
    private Player mPlayer;

    ImageButton play;
    String key = "Muse";
    String url1 = "https://api.spotify.com/v1/search?q=";
    String url2 = "&type=track%2Cartist&market=US&limit=10&offset=5";
    UserSearchFragment userSearchFragment;
    UserNotificationFragment userNotificationFragment;
    BottomNavigationView bottomNavigationView;
    UserProfileFragment userProfileFragment;
    public CustomSlidePanLayout mSlidingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        setContentView(R.layout.activity_spotify);

        // Updating FCm TOKEN here
        callAddNotificationTokenAPI();

        bottomNavigationView = findViewById(R.id.nav_view);

        mSlidingLayout = (CustomSlidePanLayout) findViewById(R.id.sliding_pane_layout);
        mSlidingLayout.setSlidingEnable(false);

        mMainFrame = (FrameLayout)findViewById(R.id.spotify_frame);
        publicPlaylistFragment = new PublicPlaylistFragment();
        spotifyUser = new SpotifyUser();
        spotifyHomeFragment = new SpotifyHomeFragment();
        userSearchFragment = new UserSearchFragment();
        userNotificationFragment = new UserNotificationFragment();
        userProfileFragment = new UserProfileFragment();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        setFragment(spotifyHomeFragment);
                        return true;

                    case R.id.navigation_playlist:
                        setFragment(publicPlaylistFragment);
                        return true;

                    case R.id.navigation_search:
                        setFragment(userSearchFragment);
                        return true;

                    case R.id.navigation_notification:
                        setFragment(userNotificationFragment);
                        return true;

                    case R.id.navigation_user:
                        setFragment(userProfileFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    public void setFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(!fragment.getClass().getName().equals(SpotifyHomeFragment.class.getName()))
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        fragmentTransaction.replace(R.id.spotify_frame, fragment, fragment.getClass().getName());
        fragmentTransaction.commitAllowingStateLoss();

        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {

                Fragment f = fragmentManager.findFragmentById(R.id.spotify_frame);

                if(f instanceof SpotifyHomeFragment)
                    bottomNavigationView.getMenu().getItem(0).setChecked(true);
                else if(f instanceof PublicPlaylistFragment)
                    bottomNavigationView.getMenu().getItem(1).setChecked(true);
                else if(f instanceof UserSearchFragment)
                    bottomNavigationView.getMenu().getItem(2).setChecked(true);
                else if(f instanceof UserNotificationFragment)
                    bottomNavigationView.getMenu().getItem(3).setChecked(true);
                else if(f instanceof SpotifyUser)
                    bottomNavigationView.getMenu().getItem(4).setChecked(true);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity_youtube_2
        // The next 19 lines of the code are what you need to copy & paste! :)
        if (requestCode == REQUEST_CODE) {
            final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
//                        Intent intent1 = new Intent(spotify.this, SearchActivity.class);
//                        intent1.putExtra("token",response.getAccessToken());
//                        startActivity(intent1);
                        spotifyAccessToken = response.getAccessToken();
                        Logging.d("spotifyAccessToken:"+spotifyAccessToken);
                        spotifyPlayer.logout();
                        mPlayer = spotifyPlayer;
                        mPlayer.addConnectionStateCallback(spotify.this);
                        mPlayer.addNotificationCallback(spotify.this);

                        callSpotifyMe(spotifyAccessToken);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Logging.d(  "Could not initialize player: " + throwable.getMessage());
                    }
                });
            } else {
                showPremiumErrorAlertDialog(getResources().getString(R.string.spotify_acc_error_msg));
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
        Log.d("this", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("this", "Playback error received: " + error.name());
        switch (error) {
            // Handle error ty0pe as necessary
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("this", "User logged in");

        // This is the line that plays a song.
        //mPlayer.playUri(null, "spotify:track:7hH4dSp71EOv3XS57e8CYu", 0, 0);
    }

    @Override
    public void onLoggedOut() {
        Log.d("this", "User logged out");
    }

    @Override
    public void onLoginFailed(Error var1) {
        Log.d("this", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("this", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("this", "Received connection message: " + message);
    }


    private void callSpotifyMe(String token) {
        //progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getSpotifydata();

        Call<SotifyMeResponse> callback = dataAPI.getSpotifyMe("Bearer "+ token);
        callback.enqueue(new Callback<SotifyMeResponse>() {
            @Override
            public void onResponse(Call<SotifyMeResponse> call, Response<SotifyMeResponse> response) {
                //progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    //Logging.d("getSpotifyMe res:"+new Gson().toJson(response));

                    if(!TextUtils.isEmpty(response.body().getProduct()) && response.body().getProduct().equalsIgnoreCase("premium")) {

                        String comingFrom = getIntent() == null ? "" : getIntent().getStringExtra(AppConstants.INTENT_COMING_FROM);

                        if (!TextUtils.isEmpty(comingFrom) && comingFrom.equals(AppConstants.NOTIFICATION)) {

                            bottomNavigationView.getMenu().getItem(3).setChecked(true);
                            setFragment(userNotificationFragment);
                        } else {
                            setFragment(spotifyHomeFragment);
                        }

                    } else {
   
                        showPremiumErrorAlertDialog(getResources().getString(R.string.spotify_premium_error_msg));
                    }
                } else {
                    Toast.makeText(spotify.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<SotifyMeResponse> call, Throwable t) {
                //progressBar.setVisibility(View.GONE);
                Toast.makeText(spotify.this,"Error: " +t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    private void showPremiumErrorAlertDialog(String msg) {

        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.INTENT_MESSAGE,msg);

        SpotifyErrorDialogFragment spotifyErrorDialogFragment = new SpotifyErrorDialogFragment();
        spotifyErrorDialogFragment.setYoutubeButtonListener(this);
        spotifyErrorDialogFragment.setCancelable(false);
        spotifyErrorDialogFragment.setArguments(bundle);
        spotifyErrorDialogFragment.show(getSupportFragmentManager(),"SpotifyErrorDialog");

    }

    @Override
    public void onYoutubeClick(View view) {
        gotoYoutubeHome();
    }

    private void gotoYoutubeHome() {
        SharedPrefManager.getInstance(spotify.this).setSharedPrefString(AppConstants.INTENT_USER_PREFERRED_PLATFORM,AppConstants.YOUTUBE);
        Intent k = new Intent(spotify.this, youtube.class);
        startActivity(k.putExtra(AppConstants.INTENT_UPDATE_PLATFORM,AppConstants.YOUTUBE));
        finish();
    }

    // API to post fcmToken to Vibin Server
    String fcmToken = "";
    private void callAddNotificationTokenAPI() {

        if(SharedPrefManager.getInstance(spotify.this).getSharedPrefBoolean(AppConstants.INTENT_NOTIFICATION_TOKEN_UPDATED) == false) {

            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                return;
                            }

                            // Get new Instance ID token
                            fcmToken = task.getResult().getToken();
                            Log.i("FCM Token", ""+fcmToken);

                            DataAPI dataAPI = RetrofitAPI.getData();
                            String headerToken = AppConstants.TOKEN + SharedPrefManager.getInstance(spotify.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
                            String userToken = SharedPrefManager.getInstance(spotify.this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);

                            Call<APIResponse> callback = dataAPI.addNotificationToken(headerToken, userToken, fcmToken);
                            callback.enqueue(new Callback<APIResponse>() {
                                @Override
                                public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                                    //progressBar.setVisibility(View.GONE);
                                    if (response != null && response.body() != null) {

                                        if (response.body().getStatus().equalsIgnoreCase("success")) {

                                            SharedPrefManager.getInstance(spotify.this).setSharedPrefBoolean(AppConstants.INTENT_NOTIFICATION_TOKEN_UPDATED, true);

                                        } else {
                                        }
                                    } else {
                                    }
                                }

                                @Override
                                public void onFailure(Call<APIResponse> call, Throwable t) {
                                    Log.i("Error: " , "ADD NOTIFICATION "+t.getMessage());
                                }
                            });

                        }
                    });
        }

    }

    private void callGetNotificationUnreadCountAPI() {

        DataAPI dataAPI = RetrofitAPI.getData();
        String headerToken = AppConstants.TOKEN + SharedPrefManager.getInstance(spotify.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        int userId = SharedPrefManager.getInstance(spotify.this).getSharedPrefInt(AppConstants.INTENT_USER_ID);

        Call<APIResponse> callback = dataAPI.getUnreadCount(headerToken,  userId);
        callback.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                //progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {

                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        if(response.body().getCount() > 0) {
                            MenuItem itemCart = bottomNavigationView.getMenu().findItem(R.id.navigation_notification);
                            LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
                            showBadge(spotify.this,bottomNavigationView,R.id.navigation_notification,String.valueOf(response.body().getCount()));
                        }
                        SharedPrefManager.getInstance(spotify.this).setSharedPrefInt(AppConstants.INTENT_NOTIFICATION_UNREAD_COUNT, response.body().getCount());

                    } else {

                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                Log.i("Error: " , "ADD NOTIFICATION "+t.getMessage());
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        callGetNotificationUnreadCountAPI();
    }

    public static void showBadge(Context context, BottomNavigationView
            bottomNavigationView, @IdRes int itemId, String value) {
        BottomNavigationItemView itemView = bottomNavigationView.findViewById(itemId);
        View badge = LayoutInflater.from(context).inflate(R.layout.layout_notification_badge, bottomNavigationView, false);

        TextView text = badge.findViewById(R.id.badge_text_view);
        text.setText(value);
        itemView.addView(badge);
    }
}