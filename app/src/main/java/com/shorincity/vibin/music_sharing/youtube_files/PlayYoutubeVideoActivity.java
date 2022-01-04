package com.shorincity.vibin.music_sharing.youtube_files;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.UI.youtube;
import com.shorincity.vibin.music_sharing.adapters.AddToPlaylistAdapter;
import com.shorincity.vibin.music_sharing.adapters.PlayListDetailsAdapter;
import com.shorincity.vibin.music_sharing.adapters.Playlist;
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
import com.shorincity.vibin.music_sharing.youtube_files.floating.PlayerService;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;

// Youtube Player

public class PlayYoutubeVideoActivity extends YouTubeBaseActivity implements PlayListDetailsAdapter.ItemListener, YouTubePlayer.OnInitializedListener, View.OnClickListener {
    private static String TAG = PlayYoutubeVideoActivity.class.getName();
    int REQUEST_VIDEO = 123;
    YouTubePlayerView youTubePlayerView;
    int lengthms = 270000;

    private Context mContext;
    int position = 0;
    String createnewplaylist = AppConstants.BASE_URL + "playlist/v1/create_new_playlist/";
    private MyPlayerStateChangeListener playerStateChangeListener;
    private MyPlaybackEventListener playbackEventListener;
    TextView titlemain;
    private YouTubePlayer mYouTubePlayer;
    Handler handler;
    Runnable my;
    boolean isFromRewind = false, isFromNext = false;
    boolean isKillMe = false;
    ArrayList<PlaylistDetailModel> playlist;
    boolean seekusedbyuser = false;
    SeekBar mSeekBar;
    Button addToPlayList;
    PlayListDetailsAdapter playListDetailsAdapter;
    BottomSheetBehavior behavior;
    BottomSheetDialog bottomSheetDialog;
    LinearLayout bottom_sheet;
    RecyclerView recyclerView_bottom;
    RecyclerView youtubePlayListRecyclerView;
    PlaylistRecyclerView adapter;
    ArrayList<MyPlaylistModel> playlistList;
    String url1 = AppConstants.BASE_URL + "playlist/v1/my_playlists/";
    String url2 = AppConstants.BASE_URL + "playlist/v1/add_trak_to_playlist/";

    String title, thumbnail, description;
    int userId;
    String videoId, songURI, playId;
    ImageView heartIv, forward, rewind, shuffle, repeatone;
    Boolean isAddSongLogRecorded = false;
    AndroidLikeButton likeButton;

    ProgressBar progressBar;
    TextView textVieww;
    AddToPlaylistAdapter addToPlaylistAdapter;
    TextView playerCurrentDurationTv, playerTotalDurationTv;
    Button Play_Pause;
    TextView tvCreateNewPlaylist;
    boolean isSuffleOn = false;
    boolean isRepeatOn = false;
    TextView back;
    TextView arrow;
    Animation anim;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playvideo);
        mContext = PlayYoutubeVideoActivity.this;
        Logging.d("PlayYoutubeVideoActivity");
        playlist = new ArrayList<>();
        getIntentData();
        statusBarColorChange();
        // finding views


        initViews();

        isSuffleOn = SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefBoolean(AppConstants.IS_SUFFLEON);
        isRepeatOn = SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefBoolean(AppConstants.IS_REPEATON);
        if (isSuffleOn) {
            shuffle.setColorFilter(mContext.getResources().getColor(R.color.yt_red));
            SharedPrefManager.getInstance(getApplicationContext()).setSharedPrefBoolean(AppConstants.IS_REPEATON, false);
            isRepeatOn = false;
        } else if (isRepeatOn) {
            repeatone.setImageTintList(mContext.getResources().getColorStateList(R.color.yt_red));
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
                lengthms = mYouTubePlayer.getDurationMillis();
                float current = mYouTubePlayer.getCurrentTimeMillis();
                int to = (int) (lengthms * progress / 100);

                mYouTubePlayer.seekToMillis(to);

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
                }

            }
        });
        shuffle.setOnClickListener(new View.OnClickListener() {
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
                    repeatone.setImageTintList(mContext.getResources().getColorStateList(R.color.toolbarColor));
                    Toast.makeText(getApplicationContext(), "Suffle On", +2000).show();
                    SharedPrefManager.getInstance(getApplicationContext()).setSharedPrefBoolean(AppConstants.IS_REPEATON, false);
                    isRepeatOn = false;

                }
            }
        });
        repeatone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRepeatOn) {
                    SharedPrefManager.getInstance(getApplicationContext()).setSharedPrefBoolean(AppConstants.IS_REPEATON, false);
                    isRepeatOn = false;
                    repeatone.setImageTintList(mContext.getResources().getColorStateList(R.color.toolbarColor));
                    Toast.makeText(getApplicationContext(), "Repeat Off", +2000).show();
                } else {
                    SharedPrefManager.getInstance(getApplicationContext()).setSharedPrefBoolean(AppConstants.IS_REPEATON, true);
                    isRepeatOn = true;
                    shuffle.setColorFilter(Color.parseColor("#5D14F8"));
                    repeatone.setImageTintList(mContext.getResources().getColorStateList(R.color.yt_red));
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
                if (mYouTubePlayer.isPlaying()) {
                    mYouTubePlayer.pause();
                    Play_Pause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
                } else {
                    mYouTubePlayer.play();
                    Play_Pause.setBackgroundResource(R.drawable.ic_baseline_pause_24);
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
                    dialog("00:00:00");
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
                onBackPressed();
            }
        });

        // TODO : Uncomment This method for Float
//        findViewById(R.id.now_play_tv).setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("LogNotTimber")
//            @Override
//            public void onClick(View view) {
//                int duration = mYouTubePlayer.getCurrentTimeMillis();
//                Log.d("TEST : ", "Running-->" + isServiceRunning(PlayerService.class));
//                if (isServiceRunning(PlayerService.class)) {
//                    PlayerService.startVid(videoId, playId, title, thumbnail, description, playlist, position);
//                } else {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !canDrawOverlays(PlayYoutubeVideoActivity.this)) {
//                        Log.d("TEST : ", "ACTION_MANAGE_OVERLAY_PERMISSION");
//                        Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
//                        startActivityForResult(i, OVERLAY_PERMISSION_REQ);
//                    } else {
//                        Log.d("TEST : ", "STARTFOREGROUND_WEB_ACTION");
//                        Intent i = new Intent(PlayYoutubeVideoActivity.this, PlayerService.class);
//                        i.putExtra("VID_ID", videoId);
//                        i.putExtra("PLAYLIST_ID", playId);
//                        i.putExtra("SONG_DURATION", duration);
//                        i.setAction(Constants.ACTION.STARTFOREGROUND_WEB_ACTION);
//                        startService(i);
//
//                        homeClicked();
//                    }
//                }
//
//            }
//        });


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
        playListDetailsAdapter.setTextViewColor(position);
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
        ArrayList<PlaylistDetailModel> tempplaylist = new ArrayList<PlaylistDetailModel>();
//        tempplaylist.addAll(playlist);
//        Collections.reverse(tempplaylist);
        playListDetailsAdapter = new PlayListDetailsAdapter(getApplicationContext(), playlist, position, this);
        recyclerView_bottom.setAdapter(playListDetailsAdapter);


        View bottomSheet = findViewById(R.id.bottom_sheet_detail);
        behavior = BottomSheetBehavior.from(bottomSheet);
        recyclerView_bottom.smoothScrollToPosition(position);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                //playListDetailsAdapter.setTextViewColor(position);
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    if (recyclerView_bottom != null)
                        recyclerView_bottom.smoothScrollToPosition(position);

                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {


            }

        });

        if (behavior != null)
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

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
        if (playlist.get(position).getTrackId().equalsIgnoreCase(videoId)) {
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


    private void processSeekBar() {
//        if (isFromNext) {
//            playListDetailsAdapter.setTextViewColor(position + 1);
//        }else if(isFromRewind ) {
//            playListDetailsAdapter.setTextViewColor(position - 1);
//        }else {
//            playListDetailsAdapter.setTextViewColor(position);
//        }
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
                            if (mYouTubePlayer.isPlaying() && (int) wowInt > 0) {
                                playerCurrentDurationTv.setText(Utility.convertDuration(Long.valueOf(mYouTubePlayer.getCurrentTimeMillis())));
                            }

                        }
                    }
                }
            }
        };
        handler.postDelayed(my, 2000);
    }

    private void automaticNextSong() {
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
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
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
//                Play_Pause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
//            } else {
//                mYouTubePlayer.play();
//                Play_Pause.setBackgroundResource(R.drawable.ic_baseline_pause_24);
//            }
//
//        }
//    }


    //YouTubePlayerTracker mTracker = null;
    private void inItListeners() {

        heartIv.setOnClickListener(this);

    }

    private void getIntentData() {
        userId = SharedPrefManager.getInstance(PlayYoutubeVideoActivity.this).getSharedPrefInt(AppConstants.INTENT_USER_ID);
        Bundle bundle = getIntent().getBundleExtra("data");
        if (bundle != null) {
            if (bundle.containsKey("from") && bundle.getString("from").equals("channel")) {
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

        // setting icons
        forward.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_baseline_skip_next_24));
        rewind.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_baseline_skip_previous_24));
        rewind.setImageTintList(mContext.getResources().getColorStateList(R.color.colorPrimaryDark));
        repeatone.setImageDrawable(mContext.getResources().getDrawable(R.drawable.repeatonce));
        repeatone.setImageTintList(mContext.getResources().getColorStateList(R.color.toolbarColor));


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
                AddThisToPlaylist(videoId, id, title, thumbnail, duration);
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
    private void AddThisToPlaylist(final String videoId, final int id, final String title,
                                   final String thumbnail, String duration) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean trackadded = jsonObject.getBoolean("Track added");

                    if (trackadded) {
                        sendSongAddedNotification(id);
                        Toast.makeText(PlayYoutubeVideoActivity.this, "track successfully added", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(PlayYoutubeVideoActivity.this, "track already exists in the playlist", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PlayYoutubeVideoActivity.this, "track already exists in the playlist", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String token = SharedPrefManager.getInstance(PlayYoutubeVideoActivity.this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);

                params.put("token", token);
                params.put("playlist", String.valueOf(id));
                params.put("type", "youtube");
                params.put("track_id", videoId);
                params.put("name", title);
                params.put("image", thumbnail);
                String timeDuration = "T" + Utility.millisIntoHHMMSS(Long.valueOf(duration));
                params.put("song_duration", timeDuration);
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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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
        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
        youTubePlayer.setPlaybackEventListener(playbackEventListener);
        youTubePlayer.cueVideo(videoId);
        youTubePlayer.setShowFullscreenButton(false);
        youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
        youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
            @Override
            public void onFullscreen(boolean isFullScreen) {
                Logging.d("Youtube isFullScreen-->" + isFullScreen);
            }
        });
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
            case R.id.heart_iv:
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

        if (mYouTubePlayer != null && playerTotalDurationTv != null) {
            lengthms = mYouTubePlayer.getDurationMillis();
            float current = mYouTubePlayer.getCurrentTimeMillis();
            float wowInt = ((current / lengthms) * 100);
            mSeekBar.setProgress((int) wowInt);
            processSeekBar();
            Logging.d("wowInt-->" + wowInt);
            // displaying current duration when song starts to play
            if (mYouTubePlayer.isPlaying() && (int) wowInt > 0) {
                playerCurrentDurationTv.setText(Utility.convertDuration(Long.valueOf(mYouTubePlayer.getCurrentTimeMillis())));
            }
            if (mYouTubePlayer.isPlaying()) {
                mYouTubePlayer.pause();
                Play_Pause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
            } else {
                mYouTubePlayer.play();
                Play_Pause.setBackgroundResource(R.drawable.ic_baseline_pause_24);
            }
        }
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

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (handler != null)
            handler.removeCallbacks(my);
        // isKillMe = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(my);
        }
        isKillMe = true;

        if (mYouTubePlayer != null) {
            mYouTubePlayer.release();
            mYouTubePlayer = null;
        }
        mContext = null;

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
                        heartIv.setImageResource(R.drawable.like);
                    } else {
                        isLiked = false;
                        heartIv.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    }
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
}