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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.shorincity.vibin.music_sharing.model.APIResponse;
import com.shorincity.vibin.music_sharing.model.UpdatePreferPlatformModel;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.fragment.UserNotificationFragment;
import com.shorincity.vibin.music_sharing.fragment.UserProfileFragment;
import com.shorincity.vibin.music_sharing.fragment.UserSearchFragment;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.CustomSlidePanLayout;
import com.shorincity.vibin.music_sharing.fragment.PublicPlaylistFragment;
import com.shorincity.vibin.music_sharing.utils.MiniPlayer;
import com.shorincity.vibin.music_sharing.youtube_files.YoutubeHomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.spotify.sdk.android.authentication.AuthenticationClient;
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

// Youtube Main Screen
public class youtube extends AppCompatActivity implements SpotifyPlayer.NotificationCallback, ConnectionStateCallback {
    FrameLayout mMainFrame;

    PublicPlaylistFragment publicPlaylistFragment;
    //youtube_user youtube_User;
    UserSearchFragment userSearchFragment;
    UserNotificationFragment userNotificationFragment;
    UserProfileFragment userProfileFragment;
    YoutubeHomeFragment youtubeHomeFragment;

    private static final String CLIENT_ID = "d97e6af9d329405d997632c60fe79a16";


    private static final String REDIRECT_URI = "http://vibin.in/callback/";
    private static final int REQUEST_CODE = 1337;
    private Player mPlayer;

    String preferPlatformIntent;
    public CustomSlidePanLayout mSlidingLayout;
    private BottomNavigationView bottomNavigationView;
    ConstraintLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_2);

        // Updating FCM TOKEN here
        callAddNotificationTokenAPI();

        getIntentData();

        bottomNavigationView = findViewById(R.id.nav_view);

        mSlidingLayout = (CustomSlidePanLayout) findViewById(R.id.sliding_pane_layout);
        mSlidingLayout.setSlidingEnable(false);


        mMainFrame = (FrameLayout) findViewById(R.id.youtube_frame);
        container = (ConstraintLayout) findViewById(R.id.container);
        publicPlaylistFragment = new PublicPlaylistFragment();
        youtubeHomeFragment = new YoutubeHomeFragment();
        userSearchFragment = new UserSearchFragment();
        userNotificationFragment = new UserNotificationFragment();
        userProfileFragment = new UserProfileFragment();
        crashAnlyticslogUser();
        String comingFrom = getIntent() == null ? "" : getIntent().getStringExtra(AppConstants.INTENT_COMING_FROM);

        if (!TextUtils.isEmpty(comingFrom) && comingFrom.equals(AppConstants.NOTIFICATION)) {

            bottomNavigationView.getMenu().getItem(3).setChecked(true);
            setFragment(userNotificationFragment);
        } else {
            setFragment(youtubeHomeFragment);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:

                        setFragment(youtubeHomeFragment);
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

    private void crashAnlyticslogUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        if (SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefInt(AppConstants.INTENT_USER_ID) != null) {
            FirebaseCrashlytics.getInstance().setCustomKey(AppConstants.INTENT_USER_ID, SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefInt(AppConstants.INTENT_USER_ID));
        }
        if (SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefString(AppConstants.INTENT_USER_NAME) != null) {
            FirebaseCrashlytics.getInstance().setCustomKey(AppConstants.INTENT_USER_NAME, SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefString(AppConstants.INTENT_USER_NAME));
        }
        if (SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefString(AppConstants.INTENT_EMAIL) != null) {
            FirebaseCrashlytics.getInstance().setCustomKey(AppConstants.INTENT_EMAIL, SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefString(AppConstants.INTENT_EMAIL));
        }
    }

    private void getIntentData() {

        preferPlatformIntent = getIntent().getStringExtra(AppConstants.INTENT_UPDATE_PLATFORM);

        if (!TextUtils.isEmpty(preferPlatformIntent) &&
                preferPlatformIntent.equalsIgnoreCase(AppConstants.YOUTUBE)) {
            callUpdatePlatformAPI();
        }
    }

    boolean isHomeLoaded;

    // reusing the same method to load the fragments and switching between them
    public void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (!fragment.getClass().getName().equals(YoutubeHomeFragment.class.getName()))
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        fragmentTransaction.replace(R.id.youtube_frame, fragment, fragment.getClass().getName());
        fragmentTransaction.commitAllowingStateLoss();

        isHomeLoaded = false;

        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {

                Fragment f = fragmentManager.findFragmentById(R.id.youtube_frame);

                if (f instanceof YoutubeHomeFragment)
                    bottomNavigationView.getMenu().getItem(0).setChecked(true);
                else if (f instanceof PublicPlaylistFragment)
                    bottomNavigationView.getMenu().getItem(1).setChecked(true);
                else if (f instanceof UserSearchFragment)
                    bottomNavigationView.getMenu().getItem(2).setChecked(true);
                else if (f instanceof UserNotificationFragment)
                    bottomNavigationView.getMenu().getItem(3).setChecked(true);
                else if (f instanceof UserProfileFragment)
                    bottomNavigationView.getMenu().getItem(4).setChecked(true);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE) {
            final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("this", "Could not initialize player: " + throwable.getMessage());
                    }
                });
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
        mPlayer.playUri(null, "spotify:track:7hH4dSp71EOv3XS57e8CYu", 0, 0);
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


    // API to update preferred platform whenever user comes in youtube by switching the platform
    public void callUpdatePlatformAPI() {
        DataAPI dataAPI = RetrofitAPI.getData();
        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(youtube.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        int userId = SharedPrefManager.getInstance(youtube.this).getSharedPrefInt(AppConstants.INTENT_USER_ID);

        Call<UpdatePreferPlatformModel> callback = dataAPI.callUpdatePreferredPlatform(token, userId, AppConstants.YOUTUBE);
        callback.enqueue(new Callback<UpdatePreferPlatformModel>() {
            @Override
            public void onResponse(Call<UpdatePreferPlatformModel> call, Response<UpdatePreferPlatformModel> response) {
                //progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {

                    if (response.body().getUpdateStatus() == true) {

                        if (response.body().getMessage().equalsIgnoreCase(AppConstants.STATUS_UPDATED)) {
                            SharedPrefManager.getInstance(youtube.this).setSharedPrefString(AppConstants.INTENT_USER_PREFERRED_PLATFORM, AppConstants.YOUTUBE);
                        }

                    } else {
                        Toast.makeText(youtube.this, "Result Not Found: ", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(youtube.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UpdatePreferPlatformModel> call, Throwable t) {
                Toast.makeText(youtube.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    // API to post fcmToken to Vibin Server
    String fcmToken = "";

    private void callAddNotificationTokenAPI() {

        if (SharedPrefManager.getInstance(youtube.this).getSharedPrefBoolean(AppConstants.INTENT_NOTIFICATION_TOKEN_UPDATED) == false) {

            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                return;
                            }

                            // Get new Instance ID token
                            fcmToken = task.getResult().getToken();
                            Log.i("FCM Token", "" + fcmToken);

                            DataAPI dataAPI = RetrofitAPI.getData();
                            String headerToken = AppConstants.TOKEN + SharedPrefManager.getInstance(youtube.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
                            String userToken = SharedPrefManager.getInstance(youtube.this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);

                            Call<APIResponse> callback = dataAPI.addNotificationToken(headerToken, userToken, fcmToken);
                            callback.enqueue(new Callback<APIResponse>() {
                                @Override
                                public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                                    //progressBar.setVisibility(View.GONE);
                                    if (response != null && response.body() != null) {

                                        if (response.body().getStatus().equalsIgnoreCase("success")) {

                                            SharedPrefManager.getInstance(youtube.this).setSharedPrefBoolean(AppConstants.INTENT_NOTIFICATION_TOKEN_UPDATED, true);

                                        } else {
                                        }
                                    } else {
                                    }
                                }

                                @Override
                                public void onFailure(Call<APIResponse> call, Throwable t) {
                                    Log.i("Error: ", "ADD NOTIFICATION " + t.getMessage());
                                }
                            });

                        }
                    });
        }

    }

    private void callGetNotificationUnreadCountAPI() {

        DataAPI dataAPI = RetrofitAPI.getData();
        String headerToken = AppConstants.TOKEN + SharedPrefManager.getInstance(youtube.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        int userId = SharedPrefManager.getInstance(youtube.this).getSharedPrefInt(AppConstants.INTENT_USER_ID);

        Call<APIResponse> callback = dataAPI.getUnreadCount(headerToken, userId);
        callback.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                if (response != null && response.body() != null) {

                    if (response.body().getStatus().equalsIgnoreCase("success")) {

                        if (response.body().getCount() > 0) {
                            // showing notification badge count
                            MenuItem itemCart = bottomNavigationView.getMenu().findItem(R.id.navigation_notification);
                            //LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
                            showBadge(youtube.this, bottomNavigationView, R.id.navigation_notification, String.valueOf(response.body().getCount()));
                        }

                        SharedPrefManager.getInstance(youtube.this).setSharedPrefInt(AppConstants.INTENT_NOTIFICATION_UNREAD_COUNT, response.body().getCount());

                    } else {


                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                Log.i("Error: ", "ADD NOTIFICATION " + t.getMessage());
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