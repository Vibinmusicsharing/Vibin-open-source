package com.shorincity.vibin.music_sharing.youtube_files;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.PictureInPictureParams;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Rational;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.giphy.sdk.core.models.Media;
import com.giphy.sdk.core.models.enums.MediaType;
import com.giphy.sdk.core.models.enums.RatingType;
import com.giphy.sdk.core.models.enums.RenditionType;
import com.giphy.sdk.ui.GPHRequestType;
import com.giphy.sdk.ui.pagination.GPHContent;
import com.giphy.sdk.ui.views.GPHGridCallback;
import com.giphy.sdk.ui.views.GifView;
import com.giphy.sdk.ui.views.GiphyGridView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.jackandphantom.androidlikebutton.AndroidLikeButton;
import com.shorincity.vibin.music_sharing.BuildConfig;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.VibinApplication;
import com.shorincity.vibin.music_sharing.adapters.AddToPlaylistAdapter;
import com.shorincity.vibin.music_sharing.adapters.PlayListDetailsAdapter;
import com.shorincity.vibin.music_sharing.adapters.PlaylistRecyclerView;
import com.shorincity.vibin.music_sharing.model.APIResponse;
import com.shorincity.vibin.music_sharing.model.AddSongLogModel;
import com.shorincity.vibin.music_sharing.model.MyPlaylistModel;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.model.SongLikeModel;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.shorincity.vibin.music_sharing.utils.Utility;
import com.shorincity.vibin.music_sharing.widgets.player.PlayerConstants;
import com.shorincity.vibin.music_sharing.widgets.player.listeners.AbstractYouTubePlayerListener;
import com.shorincity.vibin.music_sharing.youtube_files.floating.PlayerService;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

// Youtube Player

public class PlayYoutubeVideoActivity extends YouTubeBaseActivity implements PlayListDetailsAdapter.ItemListener, YouTubePlayer.OnInitializedListener, View.OnClickListener {
    private static String TAG = PlayYoutubeVideoActivity.class.getName();
    int REQUEST_VIDEO = 123;
    private YouTubePlayerView youTubePlayerView;
    int lengthms = 270000;

    public static String INTENT_KILL_PLAYER = "kill_player";
    public static String INTENT_PIP_MODE = "pip_mode";

    private Context mContext;
    int position = 0;
    String createnewplaylist = AppConstants.BASE_URL + "playlist/v1/create_new_playlist/";
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
    private AppCompatImageView addToPlayList;
    private PlayListDetailsAdapter playListDetailsAdapter;
    private BottomSheetBehavior behavior;
    private BottomSheetDialog bottomSheetDialog;
    private LinearLayout bottom_sheet;
    private RecyclerView recyclerView_bottom;
    private RecyclerView youtubePlayListRecyclerView;
    private PlaylistRecyclerView adapter;
    private ArrayList<MyPlaylistModel> playlistList;

    private String title, thumbnail, description;
    int userId;
    String videoId, songURI, playId;
    private ImageView heartIv, forward, rewind, shuffle, repeatone;
    Boolean isAddSongLogRecorded = false;
    AndroidLikeButton likeButton;

    ProgressBar progressBar;
    TextView textVieww;
    AddToPlaylistAdapter addToPlaylistAdapter;
    TextView playerCurrentDurationTv, playerTotalDurationTv;
    private AppCompatImageView Play_Pause;
    TextView tvCreateNewPlaylist;
    boolean isSuffleOn = false;
    boolean isRepeatOn = false;
    private AppCompatImageView back;
    TextView arrow;
    Animation anim;
    private String artistName = "";
    private LinearLayout llShuffle, llRepeat, llAddPlayList, llLike, llArtistName;
    private AppCompatTextView tvRepeat, tvShuffle, tvArtist;
    private FrameLayout flBottomSheet;
    private com.shorincity.vibin.music_sharing.widgets.player.views.YouTubePlayerView youTubePlayerView1;
    private com.shorincity.vibin.music_sharing.widgets.player.YouTubePlayer youTubePlayer;
    private float totalDuration;
    private PlayerConstants.PlayerState state = PlayerConstants.PlayerState.UNKNOWN;
    private boolean isAppGoesBackground = false;
    private LinearLayout llSelectedSong;
    private AppCompatImageView ivSong;
    private AppCompatTextView tvSongName, tvArtistName, tvDuration;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playvideo);
        mContext = PlayYoutubeVideoActivity.this;
        Logging.d("PlayYoutubeVideoActivity");
        playlist = new ArrayList<>();
        getIntentData(getIntent());
        statusBarColorChange();
        // finding views

        initViews();

        isSuffleOn = SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefBoolean(AppConstants.IS_SUFFLEON);
        isRepeatOn = SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefBoolean(AppConstants.IS_REPEATON);
        if (isSuffleOn) {
            setSuffleOn(true);
            SharedPrefManager.getInstance(getApplicationContext()).setSharedPrefBoolean(AppConstants.IS_REPEATON, false);
            isRepeatOn = false;
        } else if (isRepeatOn) {
            setRepeatOn(true);
            SharedPrefManager.getInstance(getApplicationContext()).setSharedPrefBoolean(AppConstants.IS_SUFFLEON, false);
            isSuffleOn = false;
        }
        // calling to get Sings Like Status
        callGetSongLikeStatusAPI(userId, videoId);
        setBottomSheetForPlaylist();

        // setting buttons listeners
        inItListeners();

        processSeekBar();


        playerStateChangeListener = new MyPlayerStateChangeListener();
        playbackEventListener = new MyPlaybackEventListener();


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
//                lengthms = mYouTubePlayer.getDurationMillis();
//                float current = mYouTubePlayer.getCurrentTimeMillis();
                int to = (int) ((lengthms / 1000) * progress / 100);

//                mYouTubePlayer.seekToMillis(to);

                if (PlayYoutubeVideoActivity.this.youTubePlayer != null) {
                    PlayYoutubeVideoActivity.this.youTubePlayer.seekTo(to);

                }

            }
        });
        //seekbar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        //seekbar.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

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
        llShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSuffleOn) {
                    SharedPrefManager.getInstance(getApplicationContext()).setSharedPrefBoolean(AppConstants.IS_SUFFLEON, false);
                    isSuffleOn = false;
                    setSuffleOn(false);
                    Toast.makeText(getApplicationContext(), "Suffle Off", +2000).show();
                } else {
                    SharedPrefManager.getInstance(getApplicationContext()).setSharedPrefBoolean(AppConstants.IS_SUFFLEON, true);
                    isSuffleOn = true;
                    setSuffleOn(true);
                    setRepeatOn(false);
                    Toast.makeText(getApplicationContext(), "Suffle On", +2000).show();
                    SharedPrefManager.getInstance(getApplicationContext()).setSharedPrefBoolean(AppConstants.IS_REPEATON, false);
                    isRepeatOn = false;

                }
            }
        });
        llRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRepeatOn) {
                    SharedPrefManager.getInstance(getApplicationContext()).setSharedPrefBoolean(AppConstants.IS_REPEATON, false);
                    isRepeatOn = false;
                    setRepeatOn(false);
                    Toast.makeText(getApplicationContext(), "Repeat Off", +2000).show();
                } else {
                    SharedPrefManager.getInstance(getApplicationContext()).setSharedPrefBoolean(AppConstants.IS_REPEATON, true);
                    isRepeatOn = true;
                    setSuffleOn(false);
                    setRepeatOn(true);
                    Toast.makeText(getApplicationContext(), "Repeat On", +2000).show();
                    SharedPrefManager.getInstance(getApplicationContext()).setSharedPrefBoolean(AppConstants.IS_SUFFLEON, false);
                    isSuffleOn = false;

                }
            }
        });
        Play_Pause = findViewById(R.id.button2);
        Play_Pause.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                /*if (mYouTubePlayer.isPlaying()) {
                    mYouTubePlayer.pause();
                    youTubePlayer.pause();
                    Play_Pause.setImageResource(R.drawable.ic_player_play);
                } else {
                    mYouTubePlayer.play();
                    youTubePlayer.play();
                    Play_Pause.setImageResource(R.drawable.ic_player_pause);
                }*/

                if (youTubePlayer != null) {
                    if (state == PlayerConstants.PlayerState.PLAYING) {
                        youTubePlayer.pause();
                        Play_Pause.setImageResource(R.drawable.ic_player_play);
                    } else {
                        youTubePlayer.play();
                        Play_Pause.setImageResource(R.drawable.ic_player_pause);
                    }
                }
            }
        });

        addToPlayList = findViewById(R.id.addToPlayList);
        llAddPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mYouTubePlayer.pause();
                Play_Pause.setImageResource(R.drawable.ic_player_play);
                String duration = mYouTubePlayer.getDurationMillis() > 0 ? String.valueOf(mYouTubePlayer.getDurationMillis()) : "";
                if (duration.equalsIgnoreCase("")) {
                    dialog("0");
                } else {
                    dialog(duration);

                }
            }
        });

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (behavior != null)
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                //arrow.setVisibility(View.GONE);
            }
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    boolean supportsPIP = getPackageManager().hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE);
                    if (supportsPIP) {
                        AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
                        if (appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_PICTURE_IN_PICTURE, android.os.Process.myUid(), BuildConfig.APPLICATION_ID) == AppOpsManager.MODE_ALLOWED) {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                PictureInPictureParams params = new PictureInPictureParams.Builder().
                                        setAspectRatio(new Rational(16, 9)).build();
                                enterPictureInPictureMode(params);
                            } else {
                                enterPictureInPictureMode();
                            }
                        } else {
                            AlertDialog dialog = new AlertDialog.Builder(PlayYoutubeVideoActivity.this)
                                    .setTitle("Enable picture in picture mode")
                                    .setMessage("Kindly enable picture in picture mode from setting for this app.")
                                    .setPositiveButton("Setting", (dialog1, which) -> {
                                        dialog1.dismiss();
                                        try {
                                            startActivity(new Intent("android.settings.PICTURE_IN_PICTURE_SETTINGS"));
                                        } catch (ActivityNotFoundException e) {
                                            Toast.makeText(PlayYoutubeVideoActivity.this, "Kindly enable picture in picture mode from setting for this app.", Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        }
                    }

                } else {
                    new AlertDialog.Builder(PlayYoutubeVideoActivity.this)
                            .setTitle("Can't enter picture in picture mode")
                            .setMessage("In order to enter picture in picture mode you need a SDK version >= N.")
                            .show();
                }
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra(INTENT_KILL_PLAYER, false)) {
            ((VibinApplication) getApplication()).isPipEnable = false;
            finishAndRemoveTask();
            finishAffinity();
        } else if (intent.getBooleanExtra(INTENT_PIP_MODE, false)) {
            back.callOnClick();
        } else if (intent.hasExtra("data")) {
            getIntentData(intent);
            if (mYouTubePlayer != null) {
                mYouTubePlayer.release();
                mYouTubePlayer = null;
            }
            youTubePlayerView.initialize(AppConstants.YOUTUBE_KEY, this);

            playerStateChangeListener = new MyPlayerStateChangeListener();
            playbackEventListener = new MyPlaybackEventListener();

            processSeekBar();
            if (playListDetailsAdapter != null)
                playListDetailsAdapter.notifyDataSetChanged();

            titlemain.setText(title);
            titlemain.setSelected(true);

            callGetSongLikeStatusAPI(userId, videoId);
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        Log.d("LOG_TAG", "==> isInPictureInPictureMode =" + isInPictureInPictureMode + "isRoot= " + isTaskRoot());
        ((VibinApplication) getApplication()).isPipEnable = isInPictureInPictureMode;
        if (isInPictureInPictureMode) {
            flBottomSheet.setVisibility(View.GONE);
            youTubePlayerView1.enterFullScreen();
        } else {
            flBottomSheet.setVisibility(View.VISIBLE);
            youTubePlayerView1.exitFullScreen();
//            removeLauncherTask(getApplicationContext());
        }
    }

    @Override
    public void onItemClick(PlaylistDetailModel item) {
        plaSongFromBottomSheet(item);
        if (behavior != null)
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

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
        //recyclerView_bottom.smoothScrollToPosition(position);
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

    private void setBottomSheetForPlaylist() {

        recyclerView_bottom = (RecyclerView) findViewById(R.id.recyclerView_bottom);
        recyclerView_bottom.setHasFixedSize(true);
        recyclerView_bottom.setLayoutManager(new LinearLayoutManager(this));

        playListDetailsAdapter = new PlayListDetailsAdapter(getApplicationContext(), playlist, position, this);
        recyclerView_bottom.setAdapter(playListDetailsAdapter);


        View bottomSheet = findViewById(R.id.flBottomSheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        recyclerView_bottom.smoothScrollToPosition(position);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                //playListDetailsAdapter.setTextViewColor(position);
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    if (position < playlist.size()) {
                        llSelectedSong.setVisibility(View.VISIBLE);
                    } else {
                        llSelectedSong.setVisibility(View.GONE);
                    }
                    if (recyclerView_bottom != null) {
                        recyclerView_bottom.smoothScrollToPosition(position);
                        recyclerView_bottom.setVisibility(View.GONE);
                    }
                } else {
                    recyclerView_bottom.setVisibility(View.VISIBLE);
                    llSelectedSong.setVisibility(View.GONE);
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {


            }

        });

        if (behavior != null)
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

    }

    private void setSuffleOn(boolean suffleOn) {
        shuffle.setSelected(suffleOn);
        tvShuffle.setSelected(suffleOn);
    }

    private void setRepeatOn(boolean repeatOn) {
        repeatone.setSelected(repeatOn);
        tvRepeat.setSelected(repeatOn);
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
        if ((position - 1) >= 0 && position < playlist.size()
                && playlist.get(position).getTrackId().equalsIgnoreCase(videoId)) {
            if ((position - 1) >= 0 && position < playlist.size()) {
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

    private void processSeekBar() {
        if (playListDetailsAdapter != null) {
            if (isFromNext) {
                playListDetailsAdapter.setTextViewColor(position + 1);
            } else if (isFromRewind) {
                playListDetailsAdapter.setTextViewColor(position - 1);
            } else {
                playListDetailsAdapter.setTextViewColor(position);
            }
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
                if (mContext != null) {

/*                    if (isKillMe)
                        return;

                    handler.postDelayed(this, 1000);

                    if (mYouTubePlayer != null) {

                        lengthms = mYouTubePlayer.getDurationMillis();
                        float current = mYouTubePlayer.getCurrentTimeMillis();
                        float wowInt = ((current / lengthms) * 100);
                        if (seekusedbyuser == false) {
                            mSeekBar.setProgress((int) wowInt);

                            // displaying current duration when song starts to play
                            if (mYouTubePlayer.isPlaying() && (int) wowInt > 0) {
                                playerCurrentDurationTv.setText(Utility.convertDuration(Long.valueOf(mYouTubePlayer.getCurrentTimeMillis())));
                            }

                        }
                    }*/
                }
            }
        };
        handler.postDelayed(my, 2000);

        if (position < playlist.size()) {
            llSelectedSong.setVisibility(View.VISIBLE);
            PlaylistDetailModel item = playlist.get(position);
            tvSongName.setText(item.getName());
            tvArtistName.setText(item.getArtistName());

            tvDuration.setText(item.getSongDuration() == null ? "00:00" : item.getSongDuration());
            Glide.with(this).load(item.getImage()).into(ivSong);
        } else {
            llSelectedSong.setVisibility(View.GONE);
        }
    }

    private void automaticNextSong() {
        if ((position + 1) < playlist.size()) {
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

    public void nextSongChange() {
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
    public static boolean canDrawOverlays(Context context) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && Settings.canDrawOverlays(context))
            return true;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {//USING APP OPS MANAGER
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
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(0, 0, android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
            viewToAdd.setLayoutParams(params);
            mgr.addView(viewToAdd, params);
            mgr.removeView(viewToAdd);
            return true;
        } catch (Exception ignore) {
        }
        return false;

    }


    private void statusBarColorChange() {
        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
    }

    private void homeClicked() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);


    }

    public static String STARTFOREGROUND_WEB_ACTION = "com.shapps.ytube.action.playingweb";

    private boolean isServiceRunning(Class<PlayerService> playerServiceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (playerServiceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public static int OVERLAY_PERMISSION_REQ = 1234;

    private void needPermissionDialog(final int requestCode) {
        if (requestCode == OVERLAY_PERMISSION_REQ) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You need to grant the permission.");
            builder.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(i, OVERLAY_PERMISSION_REQ);
                }
            });
            builder.setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.setCancelable(false);
            builder.show();
        }
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (mYouTubePlayer != null) {
//            if (mYouTubePlayer.isPlaying()) {
//                mYouTubePlayer.pause();
//                Play_Pause.setImageResource(R.drawable.ic_player_play);
//            } else {
//                mYouTubePlayer.play();
//                Play_Pause.setImageResource(R.drawable.ic_player_pause);
//            }
//
//        }
//    }


    //YouTubePlayerTracker mTracker = null;
    private void inItListeners() {

        llLike.setOnClickListener(this);

    }

    private void getIntentData(Intent intent) {
        setIntent(intent);
        userId = SharedPrefManager.getInstance(PlayYoutubeVideoActivity.this).getSharedPrefInt(AppConstants.INTENT_USER_ID);
        Bundle bundle = intent.getBundleExtra("data");
        if (bundle != null) {
            playlist.clear();
            if (bundle.containsKey("from") && bundle.getString("from").equals("channel")) {
                playlist.addAll(bundle.getParcelableArrayList("playlist"));
            } else if (bundle.containsKey("playlist")) {
                // Added By Utsav
                playlist.addAll(bundle.getParcelableArrayList("playlist"));
            }

            position = bundle.getInt("position");
            playId = bundle.getString("playId");
            title = bundle.getString("title");
            description = bundle.getString("description");
            thumbnail = bundle.getString("thumbnail");
            videoId = bundle.getString("videoId");
            songURI = "https://www.youtube.com/watch?v=" + videoId;

            if (bundle.containsKey("from") && bundle.getString("from").equals("channel")) {
                if (playlist.size() == 0 && arrow != null) {
                    arrow.setVisibility(View.GONE);
                }
            } else if (bundle.containsKey("playlist")) {
                // Added By Utsav
                if (playlist.size() == 0 && arrow != null) {
                    arrow.setVisibility(View.GONE);
                }
            }
        }
        //        Logging.d("userId-->"+userId);
        //        Logging.d("playId-->"+playId);
        //        Logging.d("title-->"+title);
        //       // Logging.d("description-->"+description);
        //        Logging.d("thumbnail-->"+thumbnail);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initViews() {
        youTubePlayerView1 = findViewById(R.id.youtube_player_view);

        flBottomSheet = findViewById(R.id.flBottomSheet);
        tvShuffle = findViewById(R.id.tvShuffle);
        llShuffle = findViewById(R.id.llShuffle);
        llRepeat = findViewById(R.id.llRepeat);
        tvRepeat = findViewById(R.id.tvRepeat);
        llAddPlayList = findViewById(R.id.llAddPlayList);
        llLike = findViewById(R.id.llLike);
        llSelectedSong = findViewById(R.id.llSelectedSong);
        ivSong = findViewById(R.id.ivSong);
        tvSongName = findViewById(R.id.tvSongName);
        tvArtistName = findViewById(R.id.tvArtistName);
        tvDuration = findViewById(R.id.tvDuration);

        youTubePlayerView = findViewById(R.id.myYoutube);

        titlemain = findViewById(R.id.youtube_title);

        titlemain.setText(title);
        titlemain.setSelected(true);
        arrow = findViewById(R.id.arrow_up);
        anim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        arrow.startAnimation(anim);

        Bundle bundle = getIntent().getBundleExtra("data");
        if (bundle != null) {
            if (bundle.containsKey("from") && bundle.getString("from").equals("channel")) {
                if (playlist.size() == 0 && arrow != null) {
                    arrow.setVisibility(View.GONE);
                }
            }
        }

        playerCurrentDurationTv = findViewById(R.id.playerCurrentTimeText);
        playerTotalDurationTv = findViewById(R.id.playerTotalTimeText);
        heartIv = findViewById(R.id.heart_iv);
        forward = findViewById(R.id.fastforward);
        rewind = findViewById(R.id.fastrewind);
        shuffle = findViewById(R.id.shuffle);
        repeatone = findViewById(R.id.repeatonce);
        youTubePlayerView.initialize(AppConstants.YOUTUBE_KEY, this);

        youTubePlayerView1.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull com.shorincity.vibin.music_sharing.widgets.player.YouTubePlayer youTubePlayer) {
                PlayYoutubeVideoActivity.this.youTubePlayer = youTubePlayer;
//                PlayYoutubeVideoActivity.this.youTubePlayer.setQuality(PlayerConstants.PlaybackQuality.SMALL);
                PlayYoutubeVideoActivity.this.youTubePlayer.loadVideo(videoId, 0f);
            }

            @Override
            public void onVideoDuration(@NonNull com.shorincity.vibin.music_sharing.widgets.player.YouTubePlayer youTubePlayer, float duration) {
                super.onVideoDuration(youTubePlayer, duration);
                totalDuration = duration * 1000;
                playerTotalDurationTv.setText(Utility.convertDuration((long) totalDuration));
            }

            @Override
            public void onCurrentSecond(@NonNull com.shorincity.vibin.music_sharing.widgets.player.YouTubePlayer youTubePlayer, float second) {
                super.onCurrentSecond(youTubePlayer, second);
                float wowInt = (((second * 1000) / totalDuration) * 100);
                mSeekBar.setProgress((int) wowInt);
                playerCurrentDurationTv.setText(Utility.convertDuration((long) second * 1000));

            }

            @Override
            public void onStateChange(@NonNull com.shorincity.vibin.music_sharing.widgets.player.YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerState state) {
                super.onStateChange(youTubePlayer, state);
                PlayYoutubeVideoActivity.this.state = state;
                Logging.d("currentState==>" + state.toString());
                if (!seekusedbyuser) {
                    if (state == PlayerConstants.PlayerState.PLAYING) {
                        Play_Pause.setImageResource(R.drawable.ic_player_pause);
                        if (!isAddSongLogRecorded) {

                            String timeDuration = "T00:00:00";

                            try {
                                timeDuration = "T" + Utility.millisIntoHHMMSS((long) totalDuration);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            callAddSongLogAPI(userId, AppConstants.YOUTUBE, title, videoId, songURI, thumbnail, description, timeDuration);
                        }
                    } else if (state == PlayerConstants.PlayerState.BUFFERING) {
                    } else if (state == PlayerConstants.PlayerState.ENDED) {
                        if (isRepeatOn) {
                            repeatOneSongChange();
                        } else if (isSuffleOn) {
                            suffleSongChange();
                        } else {
                            automaticNextSong();
                            if (position < playlist.size() - 1)
                                position = position + 1;
                            processSeekBar();
                        }
                    } else {
                        Play_Pause.setImageResource(R.drawable.ic_player_play);
                    }
                }
            }
        });

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
        addToPlaylistAdapter.setCustomItemClickListener(new AddToPlaylistAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                MyPlaylistModel currentItem = playlistList.get(position);

                String videoId = PlayYoutubeVideoActivity.this.videoId;
                int id = currentItem.getId();
                callAddTrackApi(videoId, id, title, thumbnail, duration);
            }
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
        final AlertDialog.Builder mb = new AlertDialog.Builder(PlayYoutubeVideoActivity.this);
        final View dialog = LayoutInflater.from(mContext).inflate(R.layout.layout_dialog, null, false);
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
        giphyGridView.setContent(GPHContent.Companion.getTrendingGifs());

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
                selectedGifIv.setMedia(media, RenditionType.preview, ContextCompat.getDrawable(mContext, R.color.light_gray));
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
                                Toast.makeText(mContext, "please give playlist some name", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(selectedGifLink[0])) {
                                Toast.makeText(mContext, "please choose a GIF", Toast.LENGTH_SHORT).show();
                            } else if (!Utility.isWebUrl(selectedGifLink[0])) {
                                Toast.makeText(mContext, "please choose a valid GIF", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(playlistDesc.getText())) {
                                Toast.makeText(mContext, "please enter playlist's description", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(password)) {
                                Toast.makeText(mContext, "please enter playlist a password", Toast.LENGTH_SHORT).show();
                            } else {
                                addTexts(playlistname, selectedGifLink[0], playlistDesc.getText().toString(), password, checking);
                            }
                        } else {
                            if (TextUtils.isEmpty(playlistname)) {
                                Toast.makeText(mContext, "please give playlist some name", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(selectedGifLink[0])) {
                                Toast.makeText(mContext, "please choose a GIF", Toast.LENGTH_SHORT).show();
                            } else if (!Utility.isWebUrl(selectedGifLink[0])) {
                                Toast.makeText(mContext, "please choose a valid GIF", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(playlistDesc.getText())) {
                                Toast.makeText(mContext, "please enter playlist's description", Toast.LENGTH_SHORT).show();
                            } else {
                                addTexts(playlistname, selectedGifLink[0], playlistDesc.getText().toString(), "", checking);
                            }
                        }
                    }
                });
        mb.setView(dialog);
        final AlertDialog ass = mb.create();
        ass.show();
    }

    //  add text to server
    public void addTexts(final String playlistname, final String gifLink,
                         final String description, final String password, final Boolean[] checking) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, createnewplaylist, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    Boolean PlaylistCreated = jsonObject.getBoolean("Playlist Created");
                    if (PlaylistCreated) {
                        Toast.makeText(PlayYoutubeVideoActivity.this, "playlist created", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(PlayYoutubeVideoActivity.this, "you cannot create playlist of same name again", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PlayYoutubeVideoActivity.this, "this playlist already exists", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String token = SharedPrefManager.getInstance(PlayYoutubeVideoActivity.this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
                params.put("token", token);
                params.put("name", playlistname);
                params.put("description", description);
                params.put("gif_link", gifLink);
                if (checking[0]) {
                    params.put("private", "true");
                    params.put("password", password);
                } else {
                    params.put("password", "");
                    params.put("private", "false");
                }

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String token = AppConstants.TOKEN + SharedPrefManager.getInstance(PlayYoutubeVideoActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
                params.put("Authorization", token);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(PlayYoutubeVideoActivity.this);
        requestQueue.add(stringRequest);
    }

    // add video to server
    private void callAddTrackApi(final String videoId, final int id, final String title,
                                 final String thumbnail, String duration) {
        DataAPI dataAPI = RetrofitAPI.getData();
        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        String userToken = SharedPrefManager.getInstance(this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
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
                            Toast.makeText(PlayYoutubeVideoActivity.this, "track successfully added", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(PlayYoutubeVideoActivity.this,
                                msg != null ? msg : "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException | IOException e) {
                    Toast.makeText(PlayYoutubeVideoActivity.this,
                            "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(PlayYoutubeVideoActivity.this,
                        "Something went wrong",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // parse data to recycler view
    private void parseData() {
        DataAPI dataAPI = RetrofitAPI.getData();
        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(PlayYoutubeVideoActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        String userToken = SharedPrefManager.getInstance(PlayYoutubeVideoActivity.this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
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

    // youtube functions

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer
            youTubePlayer, boolean b) {

        isAddSongLogRecorded = false;
        this.mYouTubePlayer = youTubePlayer;

        //youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
//        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
//        youTubePlayer.setPlaybackEventListener(playbackEventListener);
//        youTubePlayer.cueVideo(videoId);
        youTubePlayer.setShowFullscreenButton(false);
        youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
        youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
            @Override
            public void onFullscreen(boolean isFullScreen) {
                Logging.d("Youtube isFullScreen-->" + isFullScreen);
            }
        });
        if (this.youTubePlayer != null)
            this.youTubePlayer.loadVideo(videoId, 0f);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider
                                                provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(PlayYoutubeVideoActivity.this, REQUEST_VIDEO);
        } else {
            Toast.makeText(this, "ERROR!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.llLike:
                if (!isLiked)
                    putSongLikeAPI(userId, videoId, "True");
                else
                    putSongLikeAPI(userId, videoId, "False");
                break;

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
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

       /* if (mYouTubePlayer != null && playerTotalDurationTv != null) {
            lengthms = mYouTubePlayer.getDurationMillis();
            float current = mYouTubePlayer.getCurrentTimeMillis();
            float wowInt = ((current / lengthms) * 100);
            mSeekBar.setProgress((int) wowInt);
            processSeekBar();
            Logging.d("wowInt-->" + wowInt);
            // displaying current duration when song starts to play
            *//*if (mYouTubePlayer.isPlaying() && (int) wowInt > 0) {
                playerCurrentDurationTv.setText(Utility.convertDuration(Long.valueOf(mYouTubePlayer.getCurrentTimeMillis())));
            }*//*
            if (mYouTubePlayer.isPlaying()) {
                mYouTubePlayer.pause();
                Play_Pause.setImageResource(R.drawable.ic_player_play);
            } else {
                mYouTubePlayer.play();
                Play_Pause.setImageResource(R.drawable.ic_player_pause);
            }
        }*/
    }

    private void stopFloatingService() {
        try {
            if (isServiceRunning(PlayerService.class)) {
                Intent i = new Intent(PlayYoutubeVideoActivity.this, PlayerService.class);
                stopService(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {

        @Override
        public void onPlaying() {
            Logging.d("onPlaying");
            Play_Pause.setImageResource(R.drawable.ic_player_pause);
            if (!isAddSongLogRecorded) {

                String timeDuration = "T00:00:00";

                try {
                    timeDuration = "T" + Utility.millisIntoHHMMSS(Long.valueOf(mYouTubePlayer.getDurationMillis()));

                } catch (Exception e) {
                    e.printStackTrace();
                }

//                playerTotalDurationTv.setText(Utility.convertDuration(Long.valueOf(mYouTubePlayer.getDurationMillis())));
//                callAddSongLogAPI(userId, AppConstants.YOUTUBE, title, videoId, songURI, thumbnail, description, timeDuration);
            }
        }


        @Override
        public void onPaused() {
            // Called when playback is paused, either due to user action or call to pause().
            //            showMessage("Paused");
            Logging.d("onPaused video");
            Play_Pause.setImageResource(R.drawable.ic_player_play);
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
            Play_Pause.setImageResource(R.drawable.ic_player_pause);
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

                processSeekBar();
            }

            // Called when the video reaches its end.
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            // Called when an error occurs.
        }

    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (handler != null)
            handler.removeCallbacks(my);
        // isKillMe = true;

        Logging.d("==> onUserLeaveHint method called");
        isAppGoesBackground = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((VibinApplication) getApplication()).isPipEnable = false;

        if (handler != null) {
            handler.removeCallbacksAndMessages(my);
        }
        isKillMe = true;

        if (mYouTubePlayer != null) {
            mYouTubePlayer.release();
            mYouTubePlayer = null;
        }
        mContext = null;

        youTubePlayerView1.release();

    }

    @Override
    protected void onPause() {
        super.onPause();
        Logging.d("==> onPause method called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (handler != null) {
            handler.removeCallbacksAndMessages(my);
        }
        //  isKillMe = true;

    }


    public void callAddSongLogAPI(int userId, String songType, String songName, String
            songId, String songURI, String songThumbnail, String detail, String duration) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(PlayYoutubeVideoActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<AddSongLogModel> addLogCallback = dataAPI.addSongLogAPI(token, userId, songType, songName, songId, songURI, songThumbnail, detail, "artistName", duration);
        addLogCallback.enqueue(new Callback<AddSongLogModel>() {
            @Override
            public void onResponse(Call<AddSongLogModel> call, retrofit2.Response<AddSongLogModel> response) {
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

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(PlayYoutubeVideoActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<AddSongLogModel> addLogCallback = dataAPI.putSongLikeStatus(token, userId, songId, likeStatus);
        addLogCallback.enqueue(new Callback<AddSongLogModel>() {
            @Override
            public void onResponse(Call<AddSongLogModel> call, retrofit2.Response<AddSongLogModel> response) {
                if (response != null && response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    Log.i("YoutubeVideo", "Log recorded successfully for id " + songId);
                    isAddSongLogRecorded = true;

                    if (likeStatus.equalsIgnoreCase("True")) {
                        isLiked = true;
                        heartIv.setImageResource(R.drawable.ic_heart_selected);
                    } else {
                        isLiked = false;
                        heartIv.setImageResource(R.drawable.ic_heart_unselected);
                    }
                }
            }

            @Override
            public void onFailure(Call<AddSongLogModel> call, Throwable t) {

            }
        });

        if (likeStatus.equalsIgnoreCase("True")) {
            isLiked = true;
            heartIv.setImageResource(R.drawable.ic_heart_selected);
        } else {
            isLiked = false;
            heartIv.setImageResource(R.drawable.ic_heart_unselected);
        }
    }


    // calling to get Song's like status
    boolean isLiked = false;

    public void callGetSongLikeStatusAPI(int userId, String songId) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(PlayYoutubeVideoActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<SongLikeModel> addLogCallback = dataAPI.getSongLikeStatus(token, userId, songId);
        addLogCallback.enqueue(new Callback<SongLikeModel>() {
            @Override
            public void onResponse(Call<SongLikeModel> call, retrofit2.Response<SongLikeModel> response) {
                if (response != null && response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {

                    if (response.body().getLike() == null)
                        response.body().setLike(false);

                    if (response.body().getLike()) {
                        isLiked = true;
                        heartIv.setImageResource(R.drawable.ic_heart_selected);
                        Log.i(TAG, "liked");
                    } else {
                        Log.i(TAG, "unliked");
                        isLiked = false;
                        heartIv.setImageResource(R.drawable.ic_heart_unselected);
                    }

                } else {
                    isLiked = false;
                    heartIv.setImageResource(R.drawable.ic_heart_unselected);
                }
            }

            @Override
            public void onFailure(Call<SongLikeModel> call, Throwable t) {

            }
        });
    }


    private void sendSongAddedNotification(int playlistId) {

        DataAPI dataAPI = RetrofitAPI.getData();
        String headerToken = AppConstants.TOKEN + SharedPrefManager.getInstance(PlayYoutubeVideoActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        int userId = SharedPrefManager.getInstance(PlayYoutubeVideoActivity.this).getSharedPrefInt(AppConstants.INTENT_USER_ID);

        Call<APIResponse> callback = dataAPI.sendNotificationSongUpdate(headerToken, userId, playlistId, AppConstants.SONG_UPDATED);
        callback.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, retrofit2.Response<APIResponse> response) {
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

    private boolean removeLauncherTask(Context appContext) {
        ActivityManager activityManager = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        // iterate app tasks available and navigate to launcher task (browse task)
        if (activityManager != null) {
            final List<ActivityManager.AppTask> appTasks = activityManager.getAppTasks();
            for (ActivityManager.AppTask task : appTasks) {
                final Intent baseIntent = task.getTaskInfo().baseIntent;
                final Set<String> categories = baseIntent.getCategories();
                if (categories != null && categories.contains(Intent.CATEGORY_LAUNCHER)) {
//                PictureInPictureActivity.mBackstackLost = true; // to keep track
                    task.setExcludeFromRecents(true);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!((VibinApplication) getApplication()).isPipEnable) {
            back.callOnClick();
        }
    }
}