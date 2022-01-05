package com.shorincity.vibin.music_sharing.UI;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.giphy.sdk.core.models.Media;
import com.giphy.sdk.core.models.enums.MediaType;
import com.giphy.sdk.core.models.enums.RatingType;
import com.giphy.sdk.core.models.enums.RenditionType;
import com.giphy.sdk.ui.GPHRequestType;
import com.giphy.sdk.ui.Giphy;
import com.giphy.sdk.ui.pagination.GPHContent;
import com.giphy.sdk.ui.views.GPHGridCallback;
import com.giphy.sdk.ui.views.GifView;
import com.giphy.sdk.ui.views.GiphyGridView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.jackandphantom.androidlikebutton.AndroidLikeButton;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.adapters.AddToPlaylistAdapter;
import com.shorincity.vibin.music_sharing.adapters.AutoCompleteAdapter;
import com.shorincity.vibin.music_sharing.adapters.PlayListDetailsAdapter;
import com.shorincity.vibin.music_sharing.adapters.Playlist;
import com.shorincity.vibin.music_sharing.adapters.PlaylistRecyclerView;
import com.shorincity.vibin.music_sharing.fragment.MyBaseFragment;
import com.shorincity.vibin.music_sharing.fragment.PlaylistDetailFragmentNew;
import com.shorincity.vibin.music_sharing.fragment.PublicPlaylistFragment;
import com.shorincity.vibin.music_sharing.fragment.UserNotificationFragment;
import com.shorincity.vibin.music_sharing.fragment.UserProfileFragment;
import com.shorincity.vibin.music_sharing.fragment.UserSearchFragment;
import com.shorincity.vibin.music_sharing.model.APIResponse;
import com.shorincity.vibin.music_sharing.model.AddSongLogModel;
import com.shorincity.vibin.music_sharing.model.Item;
import com.shorincity.vibin.music_sharing.model.ModelData;
import com.shorincity.vibin.music_sharing.model.MyPlaylistModel;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.model.RealTimeNotificationCount;
import com.shorincity.vibin.music_sharing.model.SongLikeModel;
import com.shorincity.vibin.music_sharing.model.UpdatePreferPlatformModel;
import com.shorincity.vibin.music_sharing.model.lastfm.trackinfo.TrackInfoResponse;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.CommonUtils;
import com.shorincity.vibin.music_sharing.utils.CustomSlidePanLayout;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.shorincity.vibin.music_sharing.utils.Utility;
import com.shorincity.vibin.music_sharing.websocket.NaiveSSLContext;
import com.shorincity.vibin.music_sharing.widgets.TagView;
import com.shorincity.vibin.music_sharing.youtube_files.YoutubeHomeFragment;
import com.shorincity.vibin.music_sharing.youtube_files.floating.AsyncTask.Constants;
import com.shorincity.vibin.music_sharing.youtube_files.floating.PlayerService;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.SSLContext;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Youtube Main Screen
public class youtube extends YouTubeBaseActivity implements SpotifyPlayer.NotificationCallback,
        ConnectionStateCallback, PlayListDetailsAdapter.ItemListener, YouTubePlayer.OnInitializedListener,
        View.OnClickListener, MotionLayout.TransitionListener {
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
    private int playerMode = 0; // 0=noActive,1=miniPlayer,2=PlayerFullScreen
    private Group playerGrp;
    private ConstraintLayout miniPlayer;
    private AppCompatImageView imgPlayerClose, imgMinimize;
    private MotionLayout motionLayout;
    private static String TAG = youtube.class.getName();
    private int REQUEST_VIDEO = 123;
    private YouTubePlayerView youTubePlayerView;
    private int lengthms = 270000;
    private int position = 0;

    private String createnewplaylist = AppConstants.BASE_URL + "playlist/v1/create_new_playlist/";
    private MyPlayerStateChangeListener playerStateChangeListener;
    private MyPlaybackEventListener playbackEventListener;
    private TextView titlemain;
    private YouTubePlayer mYouTubePlayer;
    private Handler handler;
    private Runnable my;
    private boolean isFromRewind = false, isFromNext = false;
    private boolean isKillMe = false;
    private ArrayList<PlaylistDetailModel> playlist;
    private boolean seekusedbyuser = false;
    private SeekBar mSeekBar;
    private Button addToPlayList;
    private PlayListDetailsAdapter playListDetailsAdapter;
    private BottomSheetBehavior behavior;
    private BottomSheetDialog bottomSheetDialog;
    private LinearLayout bottom_sheet;
    private RecyclerView recyclerView_bottom;
    private RecyclerView youtubePlayListRecyclerView;
    private PlaylistRecyclerView adapter;
    private ArrayList<MyPlaylistModel> playlistList;
    private ArrayList<String> genreList;
    private String url1 = AppConstants.BASE_URL + "playlist/v1/my_playlists/";
    private String url2 = AppConstants.BASE_URL + "playlist/v1/add_trak_to_playlist/";

    private String title, thumbnail, description;
    private int userId;
    private String videoId, songURI, playId;
    private AppCompatImageView heartIv, forward, rewind, shuffle, repeatone;
    private Boolean isAddSongLogRecorded = false;
    private AndroidLikeButton likeButton;

    private ProgressBar progressBar;
    private TextView textVieww;
    private AddToPlaylistAdapter addToPlaylistAdapter;
    private TextView playerCurrentDurationTv, playerTotalDurationTv;
    private Button Play_Pause;
    private TextView tvCreateNewPlaylist;
    private AppCompatTextView arrow;
    private Animation anim;

    private boolean isSuffleOn = false;
    private boolean isRepeatOn = false;
    private LinearLayout bottom_sheet_detail;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private WebSocket webSocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_2);

        Giphy.INSTANCE.configure(this, AppConstants.GIPHY_API_KEY, true);
        // Updating FCM TOKEN here
        callAddNotificationTokenAPI();

        getIntentData();
        playlist = new ArrayList<>();

        bottomNavigationView = findViewById(R.id.nav_view);
        playerGrp = findViewById(R.id.playerGrp);
        imgPlayerClose = findViewById(R.id.imgPlayerClose);
        imgMinimize = findViewById(R.id.imgMinimize);
        miniPlayer = findViewById(R.id.miniPlayer);
        motionLayout = findViewById(R.id.motionLayout);
        arrow = findViewById(R.id.arrow_up);
        anim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        arrow.startAnimation(anim);


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
            setFragment(userNotificationFragment, 0, 0);
        } else {
            setFragment(youtubeHomeFragment, 0, 0);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:

                        setFragment(youtubeHomeFragment, 0, 0);
                        return true;

                    case R.id.navigation_playlist:
                        setFragment(publicPlaylistFragment, 0, 0);
                        return true;

                    case R.id.navigation_search:
                        setFragment(userSearchFragment, 0, 0);
                        return true;

                    case R.id.navigation_notification:
                        setFragment(userNotificationFragment, 0, 0);
                        return true;

                    case R.id.navigation_user:
                        setFragment(userProfileFragment, 0, 0);
                        return true;
                    default:
                        return false;
                }
            }
        });
        imgPlayerClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerMode = 0;
                motionLayout.transitionToState(R.id.init);
                setPlayerMode();
            }
        });
        initViews();
        init();
    }

    private void init() {
        genreList = CommonUtils.getGenre();

        isSuffleOn = SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefBoolean(AppConstants.IS_SUFFLEON);
        isRepeatOn = SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefBoolean(AppConstants.IS_REPEATON);
        if (isSuffleOn) {
            shuffle.setColorFilter(getResources().getColor(R.color.yt_red));
            SharedPrefManager.getInstance(getApplicationContext()).setSharedPrefBoolean(AppConstants.IS_REPEATON, false);
            isRepeatOn = false;
        } else if (isRepeatOn) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                repeatone.setImageTintList(getResources().getColorStateList(R.color.yt_red));
            }
            SharedPrefManager.getInstance(getApplicationContext()).setSharedPrefBoolean(AppConstants.IS_SUFFLEON, false);
            isSuffleOn = false;
        }

        callGetSongLikeStatusAPI(userId, videoId);
        setBottomSheetForPlaylist();
        processSeekBar();
        playerStateChangeListener = new MyPlayerStateChangeListener();
        playbackEventListener = new MyPlaybackEventListener();


        playerMode = 0;

        setPlayerMode();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment f = fragmentManager.findFragmentById(R.id.youtube_frame);
                statusBarColorChange(ContextCompat.getColor(youtube.this, R.color.colorPrimaryDark));
                if (f == null) {
                    finishAffinity();
                } else if (f instanceof YoutubeHomeFragment)
                    bottomNavigationView.getMenu().getItem(0).setChecked(true);
                else if (f instanceof PublicPlaylistFragment) {
                    statusBarColorChange(ContextCompat.getColor(youtube.this, R.color.white));
                    bottomNavigationView.getMenu().getItem(1).setChecked(true);
                } else if (f instanceof UserSearchFragment)
                    bottomNavigationView.getMenu().getItem(2).setChecked(true);
                else if (f instanceof UserNotificationFragment)
                    bottomNavigationView.getMenu().getItem(3).setChecked(true);
                else if (f instanceof UserProfileFragment)
                    bottomNavigationView.getMenu().getItem(4).setChecked(true);
                else if (f instanceof PlaylistDetailFragmentNew) {
                    statusBarColorChange(ContextCompat.getColor(youtube.this, R.color.white));
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Logging.d("onBackPressed");
        Fragment f = fragmentManager.findFragmentById(R.id.youtube_frame);
        if (mSlidingLayout != null && mSlidingLayout.isOpen()) {
            mSlidingLayout.closePane();
        } else if (playerMode == 2) {
            motionLayout.transitionToStart();
        } else if ((playerMode > 0 || (mYouTubePlayer != null && mYouTubePlayer.isPlaying())) && f instanceof YoutubeHomeFragment) {
            imgPlayerClose.performClick();
        } else {
            super.onBackPressed();
        }

    }

    private void initViews() {

        youTubePlayerView = findViewById(R.id.myYoutube);
        bottom_sheet_detail = findViewById(R.id.bottom_sheet_detail);

        titlemain = findViewById(R.id.youtube_title);

        titlemain.setText(title);
        titlemain.setSelected(true);


        playerCurrentDurationTv = findViewById(R.id.playerCurrentTimeText);
        playerTotalDurationTv = findViewById(R.id.playerTotalTimeText);
        heartIv = findViewById(R.id.heart_iv);
        forward = findViewById(R.id.fastforward);
        rewind = findViewById(R.id.fastrewind);
        shuffle = findViewById(R.id.shuffle);
        repeatone = findViewById(R.id.repeatonce);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                Log.i("Length: ", "Seek: " + progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekusedbyuser = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekusedbyuser = false;
                if (mYouTubePlayer == null)
                    return;

                int progress = seekBar.getProgress();
                lengthms = mYouTubePlayer.getDurationMillis();
                float current = mYouTubePlayer.getCurrentTimeMillis();
                int to = (int) (lengthms * progress / 100);

                mYouTubePlayer.seekToMillis(to);

            }
        });

        youTubePlayerView.initialize(AppConstants.YOUTUBE_KEY, this);

        heartIv.setOnClickListener(this);

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (behavior != null)
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                //arrow.setVisibility(View.GONE);
            }
        });

        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSuffleOn) {
                    suffleSongChange();
                } else if (isRepeatOn) {
                    repeatOneSongChange();
                } else {
                    previousSongChange();
                    if (position > 0)
                        position = position - 1;
                    processSeekBar();
                }

            }
        });
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSuffleOn) {
                    suffleSongChange();
                } else if (isRepeatOn) {
                    repeatOneSongChange();
                } else {
                    nextSongChange();
                    if (position < playlist.size() - 1)
                        position = position + 1;

                    processSeekBar();
                }

            }
        });
        shuffle.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                if (isSuffleOn) {
                    SharedPrefManager.getInstance(getApplicationContext()).setSharedPrefBoolean(AppConstants.IS_SUFFLEON, false);
                    isSuffleOn = false;
                    shuffle.setColorFilter(Color.parseColor("#5D14F8"));
                    Toast.makeText(getApplicationContext(), "Suffle Off", +2000).show();
                } else {
                    SharedPrefManager.getInstance(getApplicationContext()).setSharedPrefBoolean(AppConstants.IS_SUFFLEON, true);
                    isSuffleOn = true;
                    shuffle.setColorFilter(Color.parseColor("#E20A0A"));
                    repeatone.setImageTintList(getResources().getColorStateList(R.color.toolbarColor));
                    Toast.makeText(getApplicationContext(), "Suffle On", +2000).show();
                    SharedPrefManager.getInstance(getApplicationContext()).setSharedPrefBoolean(AppConstants.IS_REPEATON, false);
                    isRepeatOn = false;

                }
            }
        });
        repeatone.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                if (isRepeatOn) {
                    SharedPrefManager.getInstance(getApplicationContext()).setSharedPrefBoolean(AppConstants.IS_REPEATON, false);
                    isRepeatOn = false;
                    repeatone.setImageTintList(getResources().getColorStateList(R.color.toolbarColor));
                    Toast.makeText(getApplicationContext(), "Repeat Off", +2000).show();
                } else {
                    SharedPrefManager.getInstance(getApplicationContext()).setSharedPrefBoolean(AppConstants.IS_REPEATON, true);
                    isRepeatOn = true;
                    shuffle.setColorFilter(Color.parseColor("#5D14F8"));
                    repeatone.setImageTintList(getResources().getColorStateList(R.color.yt_red));
                    Toast.makeText(getApplicationContext(), "Repeat On", +2000).show();
                    SharedPrefManager.getInstance(getApplicationContext()).setSharedPrefBoolean(AppConstants.IS_SUFFLEON, false);
                    isSuffleOn = false;

                }
            }
        });
        Play_Pause = (Button) findViewById(R.id.button2);
        Play_Pause.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (mYouTubePlayer != null) {
                    if (mYouTubePlayer.isPlaying()) {
                        mYouTubePlayer.pause();
                        Play_Pause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
                    } else {
                        mYouTubePlayer.play();
                        Play_Pause.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                    }
                }
            }
        });

        addToPlayList = findViewById(R.id.addToPlayList);
        addToPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mYouTubePlayer.pause();
                Play_Pause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
                String duration = mYouTubePlayer.getDurationMillis() > 0 ? String.valueOf(mYouTubePlayer.getDurationMillis()) : "";
                if (duration.equalsIgnoreCase("")) {
                    dialog("0");
                } else {
                    dialog(duration);

                }
            }
        });

        motionLayout.addTransitionListener(this);
        /*findViewById(R.id.now_play_tv).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LogNotTimber")
            @Override
            public void onClick(View view) {
                int duration = mYouTubePlayer.getCurrentTimeMillis();
                Log.d("TEST : ", "Running-->" + isServiceRunning(PlayerService.class));
                if (isServiceRunning(PlayerService.class)) {
                    PlayerService.startVid(videoId, playId, title, thumbnail, description, playlist, position);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !canDrawOverlays(youtube.this)) {
                        Log.d("TEST : ", "ACTION_MANAGE_OVERLAY_PERMISSION");
                        Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(i, OVERLAY_PERMISSION_REQ);
                    } else {
                        Log.d("TEST : ", "STARTFOREGROUND_WEB_ACTION");
                        Intent i = new Intent(youtube.this, PlayerService.class);
                        i.putExtra("VID_ID", videoId);
                        i.putExtra("PLAYLIST_ID", playId);
                        i.putExtra("SONG_DURATION", duration);
                        i.setAction(Constants.ACTION.STARTFOREGROUND_WEB_ACTION);
                        startService(i);

                        homeClicked();
                    }
                }

            }
        });*/

    }

    private int dpToPx(int dps) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
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
        userId = SharedPrefManager.getInstance(youtube.this).getSharedPrefInt(AppConstants.INTENT_USER_ID);

        preferPlatformIntent = getIntent().getStringExtra(AppConstants.INTENT_UPDATE_PLATFORM);

        if (!TextUtils.isEmpty(preferPlatformIntent) &&
                preferPlatformIntent.equalsIgnoreCase(AppConstants.YOUTUBE)) {
            callUpdatePlatformAPI();
        }
    }

    private boolean isHomeLoaded;

    // reusing the same method to load the fragments and switching between them
    public void setFragment(Fragment fragment, int enter, int exit) {
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        if (!fragment.isAdded())
            fragmentTransaction.addToBackStack(fragment.getClass().getName());

        if (fragment instanceof PublicPlaylistFragment ||
                fragment instanceof PlaylistDetailFragmentNew) {
            statusBarColorChange(ContextCompat.getColor(this, R.color.white));
        } else {
            statusBarColorChange(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        fragmentTransaction.replace(R.id.youtube_frame, fragment, fragment.getClass().getName());
        fragmentTransaction.commitAllowingStateLoss();

        isHomeLoaded = false;
    }

    private void statusBarColorChange(@ColorInt int color) {
        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(color);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (handler != null) {
            handler.removeCallbacksAndMessages(my);
        }
        //  isKillMe = true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent intent) {
        if (requestCode == REQUEST_VIDEO) {
            youTubePlayerView.initialize(AppConstants.YOUTUBE_KEY, this);

        } else if (requestCode == OVERLAY_PERMISSION_REQ) {
            int duration = mYouTubePlayer.getCurrentTimeMillis();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    needPermissionDialog(requestCode);
                } else {
                    Intent i = new Intent(this, PlayerService.class);
                    i.putExtra("VID_ID", videoId);
                    i.putExtra("PLAYLIST_ID", playId);
                    i.setAction(STARTFOREGROUND_WEB_ACTION);
                    i.putExtra("SONG_DURATION", duration);
                    startService(i);
                    homeClicked();
                }
            }
        } else if (requestCode == REQUEST_CODE) {
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
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();

        if (handler != null) {
            handler.removeCallbacksAndMessages(my);
        }
        isKillMe = true;

        if (mYouTubePlayer != null) {
            mYouTubePlayer.release();
            mYouTubePlayer = null;
        }
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
                Toast.makeText(youtube.this, getString(R.string.msg_network_failed), Toast.LENGTH_LONG).show();
            }
        });

    }

    // API to post fcmToken to Vibin Server
    String fcmToken = "";
    private String artistName = "";

    private void callAddNotificationTokenAPI() {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get new Instance ID token
                        fcmToken = task.getResult();
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
//                            showBadge(youtube.this, bottomNavigationView, R.id.navigation_notification, String.valueOf(response.body().getCount()));
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

    public void onPlayMusic(Bundle bundle) {
        if (mYouTubePlayer != null) {
            mYouTubePlayer.release();
            mYouTubePlayer = null;
        }
        playerMode = 2;
        setPlayerMode();

        playlist.clear();
        if (bundle.containsKey("from") && !bundle.getString("from").equals("mostPopular")) {
            playlist.addAll(bundle.getParcelableArrayList("playlist"));
        } else if (bundle.containsKey("playlist")) {
            // Added By Utsav
            playlist.addAll(bundle.getParcelableArrayList("playlist"));
        }

        if (bundle.containsKey("from") && bundle.getString("from").equals("channel")) {
            if (playlist.size() == 0 && arrow != null) {
                arrow.setVisibility(View.GONE);
            }
        }

        position = bundle.getInt("position");
        playId = bundle.getString("playId");
        title = bundle.getString("title");
        description = bundle.getString("description");
        thumbnail = bundle.getString("thumbnail");
        videoId = bundle.getString("videoId");
        if (bundle.containsKey("artist_name"))
            artistName = bundle.getString("artist_name");
        if (TextUtils.isEmpty(videoId)) {
            String trackName = artistName + "+" + title;
            callYoutubeSearchApi(trackName);
        } else {
            songURI = "https://www.youtube.com/watch?v=" + videoId;
            titlemain.setText(title);
            titlemain.setSelected(true);
            playerCurrentDurationTv.setText("00:00");

            callGetSongLikeStatusAPI(userId, videoId);
            youTubePlayerView.initialize(AppConstants.YOUTUBE_KEY, this);
            isAddSongLogRecorded = false;
            seekusedbyuser = false;
            processSeekBar();
            playerStateChangeListener = new MyPlayerStateChangeListener();
            playbackEventListener = new MyPlaybackEventListener();
        }
    }

    private void callYoutubeSearchApi(String trackName) {
        DataAPI dataAPI = RetrofitAPI.getYoutubeData();
        Call<ModelData> callback = dataAPI.getResurt("snippet", trackName, "50", "video", "10", "true", AppConstants.YOUTUBE_KEY);
        callback.enqueue(new Callback<ModelData>() {
            @Override
            public void onResponse(Call<ModelData> call, Response<ModelData> response) {
                ModelData modelData = response.body();
                if (modelData != null && modelData.getItems().size() > 0) {
                    Item youTubeTrack = modelData.getItems().get(0);
                    videoId = youTubeTrack.getId().getVideoId();
                    songURI = "https://www.youtube.com/watch?v=" + videoId;
                    titlemain.setText(title);
                    titlemain.setSelected(true);
                    playerCurrentDurationTv.setText("00:00");

                    callGetSongLikeStatusAPI(userId, videoId);
                    youTubePlayerView.initialize(AppConstants.YOUTUBE_KEY, youtube.this);
                    isAddSongLogRecorded = false;
                    seekusedbyuser = false;
                    processSeekBar();
                    playerStateChangeListener = new MyPlayerStateChangeListener();
                    playbackEventListener = new MyPlaybackEventListener();
                }
            }

            @Override
            public void onFailure(Call<ModelData> call, Throwable t) {
//                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void callGetTrackInfo(String artistName) {
        DataAPI dataAPI1 = RetrofitAPI.getLastFMData();
        dataAPI1.callTrackInfoApi(AppConstants.LAST_FM_KEY, title, artistName, "json")
                .enqueue(new Callback<TrackInfoResponse>() {
                    @Override
                    public void onResponse(Call<TrackInfoResponse> call, Response<TrackInfoResponse> response) {

                    }

                    @Override
                    public void onFailure(Call<TrackInfoResponse> call, Throwable t) {

                    }
                });
    }


    private void setPlayerMode() {
        // 0=noActive,1=miniPlayer,2=PlayerFullScreen
        switch (playerMode) {
            case 1: {
                imgPlayerClose.setVisibility(View.VISIBLE);
                youTubePlayerView.setVisibility(View.VISIBLE);
                motionLayout.setTransition(R.id.end, R.id.start);
                motionLayout.transitionToStart();
                break;
            }
            case 2: {
                imgPlayerClose.setVisibility(View.GONE);
                youTubePlayerView.setVisibility(View.VISIBLE);
                motionLayout.setTransition(R.id.start, R.id.end);
                motionLayout.transitionToEnd();
                break;
            }
            default: {
                if (mYouTubePlayer != null) {
                    mYouTubePlayer.release();
                    mYouTubePlayer = null;
                }
                youTubePlayerView.setVisibility(View.GONE);
                imgPlayerClose.setVisibility(View.GONE);
                bottom_sheet_detail.setVisibility(View.GONE);
                miniPlayer.setVisibility(View.GONE);

                bottomNavigationView.setVisibility(View.VISIBLE);
                mMainFrame.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initWebSocket();
//        callGetNotificationUnreadCountAPI();

        stopFloatingService();
        //        if(mYouTubePlayer!=null && playerTotalDurationTv!=null){
        //            playerTotalDurationTv.setText(Utility.convertDuration(Long.valueOf(mYouTubePlayer.getDurationMillis())));
        //            int progress = seekBar.getProgress();
        //            lengthms = mYouTubePlayer.getDurationMillis();
        //            float current = mYouTubePlayer.getCurrentTimeMillis();
        //            int to = (int) (lengthms * progress / 100);
        //
        //            mYouTubePlayer.seekToMillis(to);
        //        }

        if (mYouTubePlayer != null && playerTotalDurationTv != null && playerMode > 0) {
            Logging.d("==> Resume method call");
            try {
                lengthms = mYouTubePlayer.getDurationMillis();
                float current = mYouTubePlayer.getCurrentTimeMillis();
                float wowInt = ((current / lengthms) * 100);
                mSeekBar.setProgress((int) wowInt);
                processSeekBar();
                Logging.d("wowInt-->" + wowInt);
                // displaying current duration when song starts to play
                if (mYouTubePlayer != null) {
                    if (mYouTubePlayer.isPlaying() && (int) wowInt > 0) {
                        playerCurrentDurationTv.setText(Utility.convertDuration(Long.valueOf(mYouTubePlayer.getCurrentTimeMillis())));
                    }
                    // 0=noActive,1=miniPlayer,2=PlayerFullScreen
                    if (mYouTubePlayer.isPlaying()) {
                        playerMode = 0;
                        mYouTubePlayer.pause();
                        Play_Pause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
                    } else {
                        playerMode = 1;
                        mYouTubePlayer.play();
                        Play_Pause.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                    }
                    setPlayerMode();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webSocketClient != null)
            webSocketClient.disconnect();
    }

    private void stopFloatingService() {
        try {
            if (isServiceRunning(PlayerService.class)) {
                Intent i = new Intent(youtube.this, PlayerService.class);
                stopService(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showBadge(Context context, BottomNavigationView
            bottomNavigationView, @IdRes int itemId, int value, boolean isShow) {
        BottomNavigationItemView itemView = bottomNavigationView.findViewById(itemId);
        /*if (isShow) {
            if (badgeView == null)
                badgeView = LayoutInflater.from(context).inflate(R.layout.layout_notification_badge, bottomNavigationView, false);

            TextView text = badgeView.findViewById(R.id.badge_text_view);
            text.setText(value);
            itemView.addView(badgeView);
        } else {
            Log.d("LOG Tag", "==>removeView(badgeView)");
            if (badgeView != null)
                itemView.removeView(badgeView);
        }*/
        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(itemId);
        badgeDrawable.setNumber(value);
        badgeDrawable.setVisible(isShow);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.heart_iv:
                if (!isLiked)
                    putSongLikeAPI(userId, videoId, "True");
                else
                    putSongLikeAPI(userId, videoId, "False");
                break;

        }

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer
            youTubePlayer, boolean b) {

        if (youTubePlayer != null) {
            isAddSongLogRecorded = false;
            this.mYouTubePlayer = youTubePlayer;

            //youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
            youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
            youTubePlayer.setPlaybackEventListener(playbackEventListener);
            if (!TextUtils.isEmpty(videoId)) {
                youTubePlayer.cueVideo(videoId);
            }
            youTubePlayer.setShowFullscreenButton(false);
            if (playerMode == 1)
                youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
            else
                youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);

            youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                @Override
                public void onFullscreen(boolean isFullScreen) {
                    Logging.d("Youtube isFullScreen-->" + isFullScreen);
                }
            });
        }

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider
                                                provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(youtube.this, REQUEST_VIDEO);
        } else {
            Toast.makeText(this, "ERROR!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(PlaylistDetailModel item) {
        plaSongFromBottomSheet(item);
        if (behavior != null)
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (handler != null)
            handler.removeCallbacks(my);
        // isKillMe = true;
    }

    @Override
    public void onTransitionStarted(MotionLayout motionLayout, int i, int i1) {
        Logging.d("onTransitionStarted");
        bottom_sheet_detail.setVisibility(View.GONE);
    }

    @Override
    public void onTransitionChange(MotionLayout motionLayout, int i, int i1, float v) {

    }

    @Override
    public void onTransitionCompleted(MotionLayout motionLayout, int i) {
        Logging.d("==> onTransitionCompleted");
        if (i == R.id.end) {
            imgMinimize.setBackgroundDrawable(ContextCompat.getDrawable(youtube.this, R.drawable.background_cornerradius));
            imgMinimize.setImageDrawable(ContextCompat.getDrawable(youtube.this, R.drawable.ic_arrow_primary));

            playerMode = 2;
            if (mYouTubePlayer != null) {
                mYouTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
            }
            bottom_sheet_detail.setVisibility(View.VISIBLE);
            if (youtubeHomeFragment != null && youtubeHomeFragment.getView() != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(youtubeHomeFragment.getView().getWindowToken(), 0);
            }
        } else {
            imgMinimize.setBackgroundDrawable(ContextCompat.getDrawable(youtube.this, R.drawable.bg_mini_player));
            imgMinimize.setImageDrawable(ContextCompat.getDrawable(youtube.this, R.drawable.ic_arrow_up_24));

            bottom_sheet_detail.setVisibility(View.GONE);
            if (mYouTubePlayer != null && mYouTubePlayer.isPlaying()) {
                playerMode = 1;
                mYouTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
            } else {
                playerMode = 0;
                playerGrp.setVisibility(View.GONE);
                miniPlayer.setVisibility(View.GONE);
                youTubePlayerView.setVisibility(View.GONE);
                imgPlayerClose.setVisibility(View.GONE);

                bottomNavigationView.setVisibility(View.VISIBLE);
                mMainFrame.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onTransitionTrigger(MotionLayout motionLayout, int i, boolean b, float v) {

    }

    private void initWebSocket() {
        try {
            WebSocketFactory factory = new WebSocketFactory();
            SSLContext context = NaiveSSLContext.getInstance("TLS");
            factory.setSSLContext(context);
            factory.setVerifyHostname(false);
            factory.setServerName("staging.vibin.in");
            userId = SharedPrefManager.getInstance(youtube.this).getSharedPrefInt(AppConstants.INTENT_USER_ID);
            webSocketClient = factory.createSocket(Constants.WEB_SOCKET_URL + String.valueOf(userId) + "/");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        webSocketClient.addListener(new WebSocketAdapter() {
            @Override
            public void onTextMessage(WebSocket websocket, String message) {
                // Received a text message.
                RealTimeNotificationCount response = new Gson().fromJson(message, RealTimeNotificationCount.class);
                showBadge(youtube.this, bottomNavigationView, R.id.navigation_notification,
                        response.getNotificationCount(), response.getNotificationCount() > 0);
                SharedPrefManager.getInstance(youtube.this).setSharedPrefInt(AppConstants.INTENT_NOTIFICATION_UNREAD_COUNT, response.getNotificationCount());

            }
        });


        class WebSocketTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    // Connect to the server and perform an opening handshake.
                    // This method blocks until the opening handshake is finished.
                    webSocketClient.connect();

                }  // The certificate of the peer does not match the expected hostname.
                catch (WebSocketException e) {
                    // A violation against the WebSocket protocol was detected
                    // during the opening handshake.
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                Log.d("Log tag", "onPostExecute");
            }
        }

        new WebSocketTask().execute();
    }

    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {

        @Override
        public void onPlaying() {
            Logging.d("onPlaying");
            Play_Pause.setBackgroundResource(R.drawable.ic_baseline_pause_24);
            if (!isAddSongLogRecorded) {

                String timeDuration = "T00:00:00";

                try {
                    timeDuration = "T" + Utility.millisIntoHHMMSS(Long.valueOf(mYouTubePlayer.getDurationMillis()));

                } catch (Exception e) {
                    e.printStackTrace();
                }

                playerTotalDurationTv.setText(Utility.convertDuration(Long.valueOf(mYouTubePlayer.getDurationMillis())));
                callAddSongLogAPI(userId, AppConstants.YOUTUBE, title, videoId, songURI, thumbnail, description, timeDuration);
            }
        }


        @Override
        public void onPaused() {
            // Called when playback is paused, either due to user action or call to pause().
            //            showMessage("Paused");
            Logging.d("onPaused video");
            Play_Pause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
        }

        @Override
        public void onStopped() {
            if (mYouTubePlayer != null) {
                mYouTubePlayer.play();
                Logging.d("onResume --->" + lengthms);
            }
        }


        @Override
        public void onBuffering(boolean b) {
            // Called when buffering starts or ends.
            Logging.d("onBuffering video");
        }

        @Override
        public void onSeekTo(int i) {
            // Called when a jump in playback position occurs, either
            // due to user scrubbing or call to seekRelativeMillis() or seekToMillis()
            Logging.d("onSeekTo video");
        }
    }

    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        @Override
        public void onLoading() {
            // Called when the player is loading a video
            // At this point, it's not ready to accept commands affecting playback such as play() or pause()
        }

        @Override
        public void onLoaded(String s) {
            // Called when a video is done loading.
            mYouTubePlayer.play();
            Play_Pause.setBackgroundResource(R.drawable.ic_baseline_pause_24);
            // Playback methods such as play(), pause() or seekToMillis(int) may be called after this callback.
        }

        @Override
        public void onAdStarted() {
            // Called when playback of an advertisement starts.
        }

        @Override
        public void onVideoStarted() {
            // Called when playback of the video starts.
        }

        @Override
        public void onVideoEnded() {
            if (isRepeatOn) {
                repeatOneSongChange();
            } else if (isSuffleOn) {
                suffleSongChange();
            } else {
                automaticNextSong();
                if (position < playlist.size() - 1)
                    position = position + 1;

            }

            // Called when the video reaches its end.
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            // Called when an error occurs.
        }

    }

    private void callGetSongLikeStatusAPI(int userId, String songId) {
        if (TextUtils.isEmpty(songId))
            return;

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(youtube.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<SongLikeModel> addLogCallback = dataAPI.getSongLikeStatus(token, userId, songId);
        addLogCallback.enqueue(new Callback<SongLikeModel>() {
            @Override
            public void onResponse(Call<SongLikeModel> call, Response<SongLikeModel> response) {
                if (response != null && response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {

                    if (response.body().getLike() == null)
                        response.body().setLike(false);

                    if (response.body().getLike()) {
                        isLiked = true;
                        heartIv.setImageResource(R.drawable.like);
                        Log.i(TAG, "liked");
                    } else {
                        Log.i(TAG, "unliked");
                        isLiked = false;
                        heartIv.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    }

                } else {
                    isLiked = false;
                    heartIv.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                }
            }

            @Override
            public void onFailure(Call<SongLikeModel> call, Throwable t) {

            }
        });
    }

    private void setBottomSheetForPlaylist() {
        View bottomSheet = findViewById(R.id.bottom_sheet_detail);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    if (recyclerView_bottom != null)
                        recyclerView_bottom.smoothScrollToPosition(0);
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        recyclerView_bottom = (RecyclerView) findViewById(R.id.recyclerView_bottom);
        recyclerView_bottom.setHasFixedSize(true);
        recyclerView_bottom.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<PlaylistDetailModel> tempplaylist = new ArrayList<>();
//        tempplaylist.addAll(playlist);
//        Collections.reverse(tempplaylist);
        playListDetailsAdapter = new PlayListDetailsAdapter(getApplicationContext(), playlist, position, this);
        recyclerView_bottom.setAdapter(playListDetailsAdapter);
    }

    private void processSeekBar() {
        if (isFromNext) {
            playListDetailsAdapter.setTextViewColor(position + 1);
        } else if (isFromRewind) {
            playListDetailsAdapter.setTextViewColor(position - 1);
        } else {
            playListDetailsAdapter.setTextViewColor(position);
        }
        if (handler != null) {
            handler.removeCallbacks(my);
            handler = null;
        }
        handler = new Handler();
        my = new Runnable() {
            @Override
            public void run() {
                //Do something after 2 seconds
                if (isKillMe)
                    return;

                handler.postDelayed(this, 1000);

                if (mYouTubePlayer != null) {

                    lengthms = mYouTubePlayer.getDurationMillis();
                    float current = mYouTubePlayer.getCurrentTimeMillis();
                    float wowInt = ((current / lengthms) * 100);
                    if (seekusedbyuser == false) {
                        mSeekBar.setProgress((int) wowInt);

                        // displaying current duration when song starts to play
                        if ((mYouTubePlayer != null && mYouTubePlayer.isPlaying()) && (int) wowInt > 0) {
                            playerCurrentDurationTv.setText(Utility.convertDuration(Long.valueOf(mYouTubePlayer.getCurrentTimeMillis())));
                        }

                    }
                }
            }
        };
        handler.postDelayed(my, 2000);
    }

    private void plaSongFromBottomSheet(PlaylistDetailModel item) {
        if (handler != null) {
            handler.removeCallbacksAndMessages(my);
        }
        isKillMe = false;
        if (mYouTubePlayer != null) {
            mYouTubePlayer.release();
            mYouTubePlayer = null;
        }
        for (int i = 0; i < playlist.size(); i++) {
            if (item.getTrackId().equalsIgnoreCase(playlist.get(i).getTrackId())) {
                position = i;
            }
        }
        title = item.getName();
        description = "";
        thumbnail = item.getImage();
        videoId = item.getTrackId();
        songURI = "https://www.youtube.com/watch?v=" + videoId;
        youTubePlayerView.initialize(AppConstants.YOUTUBE_KEY, this);
        titlemain.setText(title);
        titlemain.setSelected(true);
        playerCurrentDurationTv.setText("00:00");
        isAddSongLogRecorded = false;
        seekusedbyuser = false;
        processSeekBar();
        playerStateChangeListener = new MyPlayerStateChangeListener();
        playbackEventListener = new MyPlaybackEventListener();
        callGetSongLikeStatusAPI(userId, videoId);
    }

    public void repeatOneSongChange() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(my);
        }
        isKillMe = false;
        if (mYouTubePlayer != null) {
            mYouTubePlayer.release();
            mYouTubePlayer = null;
        }

        title = playlist.get(position).getName();
        description = "";
        thumbnail = playlist.get(position).getImage();
        videoId = playlist.get(position).getTrackId();
        songURI = "https://www.youtube.com/watch?v=" + videoId;
        youTubePlayerView.initialize(AppConstants.YOUTUBE_KEY, this);
        titlemain.setText(title);
        titlemain.setSelected(true);
        playerCurrentDurationTv.setText("00:00");
        isAddSongLogRecorded = false;
        seekusedbyuser = false;
        processSeekBar();
        playerStateChangeListener = new MyPlayerStateChangeListener();
        playbackEventListener = new MyPlaybackEventListener();
        callGetSongLikeStatusAPI(userId, videoId);
    }

    private void suffleSongChange() {
        if (playlist.size() > 0) {
            Random random = new Random();
            int randPos = random.nextInt(playlist.size() - 1);
            Log.d("yash", "suffleSongChange: " + randPos);
            if (randPos == position) {
                randPos = random.nextInt(playlist.size() - 1);
                suffleSongChange();
            } else if (randPos != position) {
                position = randPos;
                if (handler != null) {
                    handler.removeCallbacksAndMessages(my);
                }
                isKillMe = false;
                if (mYouTubePlayer != null) {
                    mYouTubePlayer.release();
                    mYouTubePlayer = null;
                }
                title = playlist.get(randPos).getName();
                description = "";
                thumbnail = playlist.get(randPos).getImage();
                videoId = playlist.get(randPos).getTrackId();
                songURI = "https://www.youtube.com/watch?v=" + videoId;
                titlemain.setText(title);
                titlemain.setSelected(true);
                playerCurrentDurationTv.setText("00:00");
                youTubePlayerView.initialize(AppConstants.YOUTUBE_KEY, this);
                isAddSongLogRecorded = false;
                seekusedbyuser = false;
                processSeekBar();
                playerStateChangeListener = new MyPlayerStateChangeListener();
                playbackEventListener = new MyPlaybackEventListener();
                callGetSongLikeStatusAPI(userId, videoId);
            }
        }
    }

    private void previousSongChange() {
        if (position < playlist.size() && playlist.get(position).getTrackId().equalsIgnoreCase(videoId)) {
            if (position != 0) {
                if (handler != null) {
                    handler.removeCallbacksAndMessages(my);
                }
                isKillMe = false;
                if (mYouTubePlayer != null) {
                    mYouTubePlayer.release();
                    mYouTubePlayer = null;
                }
                title = playlist.get(position - 1).getName();
                description = "";
                thumbnail = playlist.get(position - 1).getImage();
                videoId = playlist.get(position - 1).getTrackId();
                songURI = "https://www.youtube.com/watch?v=" + videoId;
                titlemain.setText(title);
                titlemain.setSelected(true);
                playerCurrentDurationTv.setText("00:00");
                youTubePlayerView.initialize(AppConstants.YOUTUBE_KEY, this);
                isAddSongLogRecorded = false;
                seekusedbyuser = false;
                processSeekBar();
                playerStateChangeListener = new MyPlayerStateChangeListener();
                playbackEventListener = new MyPlaybackEventListener();
                callGetSongLikeStatusAPI(userId, videoId);
            } else {
                Toast.makeText(getApplicationContext(), "This is First Song Of Playlist", +2000).show();
            }
        }
    }

    private void automaticNextSong() {
        if (position > -1 && position < playlist.size()) {
            if (handler != null) {
                handler.removeCallbacksAndMessages(my);
            }
            isKillMe = false;
            if (mYouTubePlayer != null) {
                mYouTubePlayer.release();
                mYouTubePlayer = null;
            }
            title = playlist.get(position + 1).getName();
            description = "";
            thumbnail = playlist.get(position + 1).getImage();
            videoId = playlist.get(position + 1).getTrackId();
            songURI = "https://www.youtube.com/watch?v=" + videoId;
            titlemain.setText(title);
            titlemain.setSelected(true);
            playerCurrentDurationTv.setText("00:00");
            youTubePlayerView.initialize(AppConstants.YOUTUBE_KEY, this);
            isAddSongLogRecorded = false;
            seekusedbyuser = false;
            processSeekBar();
            playerStateChangeListener = new MyPlayerStateChangeListener();
            playbackEventListener = new MyPlaybackEventListener();
            callGetSongLikeStatusAPI(userId, videoId);
        } else if (position == (playlist.size() - 1)) {
            Toast.makeText(getApplicationContext(), "This is last Song Of Playlist", +2000).show();
        }
    }

    private void nextSongChange() {
        if (position < playlist.size() && playlist.get(position).getTrackId().equalsIgnoreCase(videoId)) {
            if (position != playlist.size() - 1) {
                if (handler != null) {
                    handler.removeCallbacksAndMessages(my);
                }
                isKillMe = false;
                if (mYouTubePlayer != null) {
                    mYouTubePlayer.release();
                    mYouTubePlayer = null;
                }
                title = playlist.get(position + 1).getName();
                description = "";
                thumbnail = playlist.get(position + 1).getImage();
                videoId = playlist.get(position + 1).getTrackId();
                songURI = "https://www.youtube.com/watch?v=" + videoId;
                titlemain.setText(title);
                titlemain.setSelected(true);
                playerCurrentDurationTv.setText("00:00");
                youTubePlayerView.initialize(AppConstants.YOUTUBE_KEY, this);
                isAddSongLogRecorded = false;
                seekusedbyuser = false;
                processSeekBar();
                playerStateChangeListener = new MyPlayerStateChangeListener();
                playbackEventListener = new MyPlaybackEventListener();
                callGetSongLikeStatusAPI(userId, videoId);
            } else {
                Toast.makeText(getApplicationContext(), "This is last Song Of Playlist", +2000).show();
            }
        }
    }

    /**
     * Workaround for Android O
     */
    private static boolean canDrawOverlays(Context context) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && Settings.canDrawOverlays(context))
            return true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//USING APP OPS MANAGER
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            if (manager != null) {
                try {
                    int result = manager.checkOp(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW, Binder.getCallingUid(), context.getPackageName());
                    return result == AppOpsManager.MODE_ALLOWED;
                } catch (Exception ignore) {
                }
            }
        }

        try {//IF This Fails, we definitely can't do it
            WindowManager mgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (mgr == null)
                return false; //getSystemService might return null
            View viewToAdd = new View(context);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(0, 0, Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
            viewToAdd.setLayoutParams(params);
            mgr.addView(viewToAdd, params);
            mgr.removeView(viewToAdd);
            return true;
        } catch (Exception ignore) {
        }
        return false;

    }

    private void homeClicked() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);


    }

    private static String STARTFOREGROUND_WEB_ACTION = "com.shapps.ytube.action.playingweb";

    private boolean isServiceRunning(Class<PlayerService> playerServiceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (playerServiceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private static int OVERLAY_PERMISSION_REQ = 1234;

    private void needPermissionDialog(final int requestCode) {
        if (requestCode == OVERLAY_PERMISSION_REQ) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You need to grant the permission.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(i, OVERLAY_PERMISSION_REQ);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.setCancelable(false);
            builder.show();
        }
    }

    // add video to playlist
    private void dialog(String duration) {
        /*final AlertDialog.Builder mb = new AlertDialog.Builder(this);
        final View dialog = LayoutInflater.from(this).inflate(R.layout.dialog_add_to_playlist, null, false);
*/

        BottomSheetDialog bottomSheet = new BottomSheetDialog(this);
        View bottom_sheet = getLayoutInflater().inflate(R.layout.dialog_add_to_playlist_bottom, null);
        bottomSheet.setContentView(bottom_sheet);
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) bottom_sheet.getParent());
        mBehavior.setPeekHeight((int) getResources().getDimension(R.dimen._250sdp));
        bottomSheet.show();

        playlistList = new ArrayList<>();
        youtubePlayListRecyclerView = bottom_sheet.findViewById(R.id.playlists);
        textVieww = bottom_sheet.findViewById(R.id.textviewplaylistplayer);
        progressBar = bottom_sheet.findViewById(R.id.progressbarPlayList);
        tvCreateNewPlaylist = bottom_sheet.findViewById(R.id.tvCreateNewPlaylist);
        progressBar.setVisibility(View.VISIBLE);
        youtubePlayListRecyclerView.setHasFixedSize(true);
        youtubePlayListRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        parseData();
        addToPlaylistAdapter = new AddToPlaylistAdapter(this, playlistList);
        addToPlaylistAdapter.setCustomItemClickListener((v, position) -> {
            MyPlaylistModel currentItem = playlistList.get(position);

            String videoId = youtube.this.videoId;
            int id = currentItem.getId();
            callAddTrackApi(videoId, id, title, thumbnail, duration);
        });
        youtubePlayListRecyclerView.setAdapter(addToPlaylistAdapter);

        /*mb.setView(bottom_sheet).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("create new playlist", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Logging.d("TEST", "create Called1");
                openDialog();
            }
        });*/

       /* mb.setView(bottom_sheet);
        final AlertDialog ass = mb.create();*/

        //ass.show();
        tvCreateNewPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();

            }
        });
    }

    // dialog to add playlist
    private void openDialog() {
       /* final AlertDialog.Builder mb = new AlertDialog.Builder(youtube.this);
        final View dialog = LayoutInflater.from(this).inflate(R.layout.layout_dialog, null, false);
        final EditText playlistName = dialog.findViewById(R.id.dialog_playlistname);
        final SearchView playlistGif = dialog.findViewById(R.id.dialog_playlist_gif);
        final GifView selectedGifIv = dialog.findViewById(R.id.selected_gif_iv);
        final EditText playlistDesc = dialog.findViewById(R.id.dialog_playlist_desc);
        final EditText PlaylistPassword = dialog.findViewById(R.id.dialog_password);
        final GiphyGridView giphyGridView = dialog.findViewById(R.id.gifsGridView);
        final String[] selectedGifLink = {""};
        // setting Giphy GridView
        giphyGridView.setDirection(GiphyGridView.HORIZONTAL);
        giphyGridView.setSpanCount(2);
        giphyGridView.setCellPadding(0);
        giphyGridView.setCallback(new GPHGridCallback() {
            @Override
            public void contentDidUpdate(int i) {
                Log.i("GifURL", "Position " + i);
            }

            @Override
            public void didSelectMedia(@NotNull Media media) {
                Log.i("GifURL", "BitlyGifURL " + media.getBitlyGifUrl());
                Log.i("GifURL", "BitlyURL " + media.getBitlyUrl());
                Log.i("GifURL", "Content " + media.getContentUrl());
                Log.i("GifURL", "EmbededUrl " + media.getEmbedUrl());
                Log.i("GifURL", "SourceUrl " + media.getSourcePostUrl());

                selectedGifLink[0] = media.getEmbedUrl();
                selectedGifIv.setVisibility(View.VISIBLE);
                selectedGifIv.setMedia(media, RenditionType.preview, ContextCompat.getDrawable(youtube.this, R.color.light_gray));
            }
        });
        playlistGif.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                GPHContent gphContent = new GPHContent();
                gphContent.setMediaType(MediaType.gif);
                gphContent.setRating(RatingType.pg13);
                gphContent.setRequestType(GPHRequestType.search);
                gphContent.setSearchQuery(query);
                giphyGridView.setContent(gphContent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //PlaylistPassword.setVisibility(View.GONE);
        PlaylistPassword.setAlpha(0.5f);
        final Switch toggle = dialog.findViewById(R.id.toggle);
        final TextView publicprivate = dialog.findViewById(R.id.privatepublic);
        final Boolean[] checking = new Boolean[1];
        checking[0] = false;
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Boolean b;
                if (isChecked) {
                    publicprivate.setText("Private");
                    PlaylistPassword.setEnabled(true);
                    PlaylistPassword.setAlpha(1.0f);
                    //PlaylistPassword.setVisibility(View.VISIBLE);
                    b = true;
                } else {
                    b = false;
                    PlaylistPassword.setText("");
                    PlaylistPassword.setEnabled(false);
                    PlaylistPassword.setAlpha(0.5f);
                    //PlaylistPassword.setVisibility(View.GONE);
                    publicprivate.setText("Public");
                }
                checking[0] = b;
            }
        });

        mb.setView(dialog)
                .setTitle("Create New Playlist")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String playlistname = playlistName.getText().toString();
                        String password = PlaylistPassword.getText().toString();
                        if (checking[0]) {
                            if (TextUtils.isEmpty(playlistname)) {
                                Toast.makeText(youtube.this, "please give playlist some name", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(selectedGifLink[0])) {
                                Toast.makeText(youtube.this, "please choose a GIF", Toast.LENGTH_SHORT).show();
                            } else if (!Utility.isWebUrl(selectedGifLink[0])) {
                                Toast.makeText(youtube.this, "please choose a valid GIF", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(playlistDesc.getText())) {
                                Toast.makeText(youtube.this, "please enter playlist's description", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(password)) {
                                Toast.makeText(youtube.this, "please enter playlist a password", Toast.LENGTH_SHORT).show();
                            } else {
                                addTexts(playlistname, selectedGifLink[0], playlistDesc.getText().toString(), password, checking);
                            }
                        } else {
                            if (TextUtils.isEmpty(playlistname)) {
                                Toast.makeText(youtube.this, "please give playlist some name", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(selectedGifLink[0])) {
                                Toast.makeText(youtube.this, "please choose a GIF", Toast.LENGTH_SHORT).show();
                            } else if (!Utility.isWebUrl(selectedGifLink[0])) {
                                Toast.makeText(youtube.this, "please choose a valid GIF", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(playlistDesc.getText())) {
                                Toast.makeText(youtube.this, "please enter playlist's description", Toast.LENGTH_SHORT).show();
                            } else {
                                addTexts(playlistname, selectedGifLink[0], playlistDesc.getText().toString(), "", checking);
                            }
                        }
                    }
                });
        mb.setView(dialog);
        final AlertDialog ass = mb.create();
        ass.show();*/
        openCreatePlaylistBottomsheet();
    }

    private void openCreatePlaylistBottomsheet() {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(this);
        View bottom_sheet = LayoutInflater.from(this).inflate(R.layout.bottomsheet_create_playlist, null);
        bottomSheet.setContentView(bottom_sheet);

        EditText playlistName = bottom_sheet.findViewById(R.id.dialog_playlistname);
        SearchView playlistGif = bottom_sheet.findViewById(R.id.dialog_playlist_gif);
        GifView selectedGifIv = bottom_sheet.findViewById(R.id.selected_gif_iv);
        EditText playlistDesc = bottom_sheet.findViewById(R.id.dialog_playlist_desc);
        GiphyGridView giphyGridView = bottom_sheet.findViewById(R.id.gifsGridView);
        TagView tagView = bottom_sheet.findViewById(R.id.tag_view_test);
        AppCompatImageView ivClose = bottom_sheet.findViewById(R.id.ivClose);
        Button btnCreate = bottom_sheet.findViewById(R.id.btnCreate);

        AppCompatAutoCompleteTextView autoCompleteTextView = bottom_sheet.findViewById(R.id.actTags);
        AutoCompleteAdapter adapter = new AutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line, android.R.id.text1, genreList);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String tag = parent.getItemAtPosition(position).toString();
            tagView.addTag(tag);
            autoCompleteTextView.setText("");
        });

        final String[] selectedGifLink = {""};
        // setting Giphy GridView
        giphyGridView.setDirection(GiphyGridView.HORIZONTAL);
        giphyGridView.setSpanCount(2);
        giphyGridView.setCellPadding(0);
        giphyGridView.setCallback(new GPHGridCallback() {
            @Override
            public void contentDidUpdate(int i) {
                Log.i("GifURL", "Position " + i);
            }

            @Override
            public void didSelectMedia(@NotNull Media media) {
                Log.i("GifURL", "BitlyGifURL " + media.getBitlyGifUrl());
                Log.i("GifURL", "BitlyURL " + media.getBitlyUrl());
                Log.i("GifURL", "Content " + media.getContentUrl());
                Log.i("GifURL", "EmbededUrl " + media.getEmbedUrl());
                Log.i("GifURL", "SourceUrl " + media.getSourcePostUrl());

                selectedGifLink[0] = media.getEmbedUrl();
                selectedGifIv.setVisibility(View.VISIBLE);
                selectedGifIv.setMedia(media, RenditionType.preview, ContextCompat.getDrawable(youtube.this, R.color.light_gray));
            }
        });
        playlistGif.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                GPHContent gphContent = new GPHContent();
                gphContent.setMediaType(MediaType.gif);
                gphContent.setRating(RatingType.pg13);
                gphContent.setRequestType(GPHRequestType.search);
                gphContent.setSearchQuery(query);
                giphyGridView.setContent(gphContent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        ivClose.setOnClickListener(v -> bottomSheet.dismiss());

        btnCreate.setOnClickListener(v -> {
            if (TextUtils.isEmpty(playlistName.getText().toString().trim())) {
                Toast.makeText(this, "please give playlist some name", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(selectedGifLink[0])) {
                Toast.makeText(this, "please choose a GIF", Toast.LENGTH_SHORT).show();
            } else if (!Utility.isWebUrl(selectedGifLink[0])) {
                Toast.makeText(this, "please choose a valid GIF", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(playlistDesc.getText().toString().trim())) {
                Toast.makeText(this, "please enter playlist's description", Toast.LENGTH_SHORT).show();
            } else {

                ProgressDialog showMe = new ProgressDialog(this);
                showMe.setMessage("Please wait");
                showMe.setCancelable(true);
                showMe.setCanceledOnTouchOutside(false);
                showMe.show();

                DataAPI dataAPI = RetrofitAPI.getData();
                String token = AppConstants.TOKEN + SharedPrefManager.getInstance(this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
                Call<MyPlaylistModel> callback = dataAPI.callCreatePlayList(token,
                        SharedPrefManager.getInstance(this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN),
                        playlistName.getText().toString().trim(), playlistDesc.getText().toString().trim(),
                        selectedGifLink[0], "false", "",
                        tagView.getSelectedTags().size() > 0 ? TextUtils.join("|", tagView.getSelectedTags()) : "All");
                callback.enqueue(new Callback<MyPlaylistModel>() {
                    @Override
                    public void onResponse(Call<MyPlaylistModel> call, retrofit2.Response<MyPlaylistModel> response) {
                        showMe.dismiss();
                        if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                            bottomSheet.dismiss();
                            playlistList.add(response.body());
                            addToPlaylistAdapter.notifyItemInserted(playlistList.size());
                        } else {
                            Toast.makeText(youtube.this,
                                    (response.body() != null && response.body().getMessage() != null) ? response.body().getMessage() : "Something went wrong!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MyPlaylistModel> call, Throwable t) {
                        showMe.dismiss();
                        Toast.makeText(youtube.this,
                                t.getLocalizedMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        bottomSheet.show();

    }

    // add video to server
    private void callAddTrackApi(final String videoId, final int id, final String title,
                                 final String thumbnail, String duration) {
        DataAPI dataAPI = RetrofitAPI.getData();
        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(youtube.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        String userToken = SharedPrefManager.getInstance(youtube.this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
        String timeDuration = "T" + Utility.millisIntoHHMMSS(Long.valueOf(duration));
        Call<ResponseBody> callback = dataAPI.callAddTrackApi(token, userToken,
                String.valueOf(id), "youtube", videoId, title, thumbnail, artistName, timeDuration);
        callback.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());

                    String msg = null;
                    if (jsonObject.has("message"))
                        msg = jsonObject.getString("message");
                    if (jsonObject.has("Track added")) {
                        boolean trackadded = jsonObject.getBoolean("Track added");
                        if (trackadded) {
                            sendSongAddedNotification(id);
                            Toast.makeText(youtube.this, "track successfully added", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(youtube.this,
                                msg != null ? msg : "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException | IOException e) {
                    Toast.makeText(youtube.this,
                            "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(youtube.this,
                        "Something went wrong",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // parse data to recycler view
    private void parseData() {
        DataAPI dataAPI = RetrofitAPI.getData();
        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(youtube.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        String userToken = SharedPrefManager.getInstance(youtube.this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
        Call<ArrayList<MyPlaylistModel>> callback = dataAPI.getMyPlaylist(token, userToken);
        callback.enqueue(new Callback<ArrayList<MyPlaylistModel>>() {
            @Override
            public void onResponse(Call<ArrayList<MyPlaylistModel>> call, retrofit2.Response<ArrayList<MyPlaylistModel>> response) {
                progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null && response.body().size() > 0) {
                    playlistList.clear();
                    playlistList.addAll(response.body());
                    addToPlaylistAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    if (playlistList.size() == 0) {
                        textVieww.setVisibility(View.VISIBLE);
                    } else {
                        textVieww.setVisibility(View.GONE);
                    }

                    Logging.d("TEST", "callMyPlaylistAPI Called");
                } else {
                    Logging.d("TEST", "callMyPlaylistAPI Else Called");
                }

            }

            @Override
            public void onFailure(Call<ArrayList<MyPlaylistModel>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Logging.d("TEST", "callMyPlaylistAPI onFailure Called");
            }
        });
    }

    private void callAddSongLogAPI(int userId, String songType, String songName, String
            songId, String songURI, String songThumbnail, String detail, String duration) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(youtube.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<AddSongLogModel> addLogCallback = dataAPI.addSongLogAPI(token, userId, songType, songName, songId, songURI, songThumbnail, detail, artistName, duration);
        addLogCallback.enqueue(new Callback<AddSongLogModel>() {
            @Override
            public void onResponse(Call<AddSongLogModel> call, Response<AddSongLogModel> response) {
                if (response != null && response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    Log.i("YoutubeVideo", "Log recorded successfully for id " + songId);
                    isAddSongLogRecorded = true;
                }
            }

            @Override
            public void onFailure(Call<AddSongLogModel> call, Throwable t) {

            }
        });
    }

    // Calling to update song like status
    public void putSongLikeAPI(int userId, String songId, String likeStatus) {
        if (TextUtils.isEmpty(songId))
            return;

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(youtube.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<AddSongLogModel> addLogCallback = dataAPI.putSongLikeStatus(token, userId, songId, likeStatus);
        addLogCallback.enqueue(new Callback<AddSongLogModel>() {
            @Override
            public void onResponse(Call<AddSongLogModel> call, Response<AddSongLogModel> response) {
                if (response != null && response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    Log.i("YoutubeVideo", "Log recorded successfully for id " + songId);
                    isAddSongLogRecorded = true;

                    if (likeStatus.equalsIgnoreCase("True")) {
                        isLiked = true;
                        heartIv.setImageResource(R.drawable.like);
                    } else {
                        isLiked = false;
                        heartIv.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    }
                } else {
                    isLiked = false;
                    heartIv.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                }
            }

            @Override
            public void onFailure(Call<AddSongLogModel> call, Throwable t) {

            }
        });

        if (likeStatus.equalsIgnoreCase("True")) {
            isLiked = true;
            heartIv.setImageResource(R.drawable.like);
        } else {
            isLiked = false;
            heartIv.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
    }


    // calling to get Song's like status
    boolean isLiked = false;

    private void sendSongAddedNotification(int playlistId) {

        DataAPI dataAPI = RetrofitAPI.getData();
        String headerToken = AppConstants.TOKEN + SharedPrefManager.getInstance(youtube.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        int userId = SharedPrefManager.getInstance(youtube.this).getSharedPrefInt(AppConstants.INTENT_USER_ID);

        Call<APIResponse> callback = dataAPI.sendNotificationSongUpdate(headerToken, userId, playlistId, AppConstants.SONG_UPDATED);
        callback.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                //progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {

                    if (response.body().getStatus().equalsIgnoreCase("success")) {

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

    public void onLoadFragment(MyBaseFragment fragment) {
        setFragment(fragment, R.animator.fragment_slide_up_enter, R.animator.fragment_slide_down_enter);
    }

    public void onLoadProfile() {
        setFragment(userProfileFragment, R.animator.fragment_slide_up_enter, R.animator.fragment_slide_down_enter);
    }
}