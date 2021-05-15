package com.shorincity.vibin.music_sharing.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
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
import com.google.gson.Gson;
import com.shorincity.vibin.music_sharing.adapters.ViewCollab;
import com.shorincity.vibin.music_sharing.adapters.ViewCollabAdapter;
import com.shorincity.vibin.music_sharing.model.APIResponse;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.model.PublicPlaylistItemAdapter;
import com.shorincity.vibin.music_sharing.model.RealTimeModel;
import com.shorincity.vibin.music_sharing.model.RealTimeSession;
import com.shorincity.vibin.music_sharing.model.RealTimeUser;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.ripples.RippleButton;
import com.shorincity.vibin.music_sharing.ripples.listener.OnRippleCompleteListener;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.shorincity.vibin.music_sharing.utils.Utility;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

// RealTime Player
public class RealTimePlayer extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, View.OnClickListener {
    private static String TAG = RealTimePlayer.class.getName();
    String view_collab = AppConstants.BASE_URL + "playlist/view_collab/";

    RealTimeModel realTimeModel;

    int playlistId;
    boolean isControlledByUser = false;

    int REQUEST_VIDEO = 123;
    YouTubePlayerView youTubePlayerView;
    ImageView spotifyPlayerView;
    TextView titlemain;
    boolean isEndSessionFromAdmin = false;

    int lengthms = 270000;
    long spotifyLengthms;

    private RealTimePlayer.MyPlayerStateChangeListener playerStateChangeListener;
    private RealTimePlayer.MyPlaybackEventListener playbackEventListener;

    private YouTubePlayer player;
   /* Handler handler;
    Runnable my;*/

    Handler handler1;
    Runnable runnable;
    boolean killMe = false;
    boolean seekusedbyuser = false;
    SeekBar seekbar;

    String title = "";

    int userId;

    ImageView forward, rewind, shuffle, repeatone;
    Boolean isAddSongLogRecorded = false;

    // Songs List
    RecyclerView playlistRv;
    ArrayList<PlaylistDetailModel> playlist;
    private PublicPlaylistItemAdapter publicPlaylistItemAdapter;

    // Collab List
    ArrayList<ViewCollab> viewcollabList;
    com.shorincity.vibin.music_sharing.adapters.ViewCollabAdapter ViewCollabAdapter;
    RecyclerView viewCollabRecyclerView;
    ProgressBar progressBarViewCollab;

    ProgressBar progressBar;
    Button Play_Pause;
    RippleButton live_streaming_btn;

    // TODO: Replace with your client ID
    private static final String CLIENT_ID = "d97e6af9d329405d997632c60fe79a16";

    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "http://vibin.in/callback/";
    private static final int REQUEST_CODE = 1337;
    private Player spotifyPlayer;

    TextView playerCurrentDurationTv, playerTotalDurationTv;


    private void initialiseSpotifyLogin() {

        final AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

    }

    Handler spotifyHandler;
    Runnable spotifyRunnable;
    String playedSongType = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_VIDEO) {
            youTubePlayerView.initialize(AppConstants.YOUTUBE_KEY, RealTimePlayer.this);

        } else if (requestCode == REQUEST_CODE) {
            final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new com.spotify.sdk.android.player.SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(com.spotify.sdk.android.player.SpotifyPlayer spotifyPlayer) {

                        RealTimePlayer.this.spotifyPlayer = spotifyPlayer;
                        RealTimePlayer.this.spotifyPlayer.addConnectionStateCallback(new ConnectionStateCallback() {
                            @Override
                            public void onLoggedIn() {

                            }

                            @Override
                            public void onLoggedOut() {

                            }

                            @Override
                            public void onLoginFailed(Error error) {

                            }

                            @Override
                            public void onTemporaryError() {

                            }

                            @Override
                            public void onConnectionMessage(String s) {

                            }
                        });
                        RealTimePlayer.this.spotifyPlayer.addNotificationCallback(new Player.NotificationCallback() {
                            @Override
                            public void onPlaybackEvent(PlayerEvent playerEvent) {

                                try {
                                    if (spotifyPlayer.getPlaybackState().isPlaying)
                                        spotifyLengthms = spotifyPlayer.getMetadata().currentTrack.durationMs;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onPlaybackError(Error error) {

                            }
                        });
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }

    private boolean isPlayerPlaying() {
        boolean isPlaying = false;

        if (playedSongType.equalsIgnoreCase(AppConstants.YOUTUBE))
            isPlaying = player != null ? player.isPlaying() : false;
        else if (playedSongType.equalsIgnoreCase(AppConstants.SPOTIFY))
            isPlaying = spotifyPlayer != null ? spotifyPlayer.getPlaybackState().isPlaying : false;

        return isPlaying;
    }

    private void setPlayerView(int songPosition) {

        if (playlist == null || playlist.size() == 0)
            return;

        titlemain.setText(playlist.get(songPosition).getName());
        Logging.d("SPOTIFY Sharing-->" + new Gson().toJson(playlist));
        if (playlist.get(songPosition).getType().equalsIgnoreCase(AppConstants.YOUTUBE)) {
            Logging.d("YOUTUBE Sharing");
            findViewById(R.id.img_cardview).setVisibility(View.INVISIBLE);
            youTubePlayerView.setVisibility(View.VISIBLE);

        } else if (playlist.get(songPosition).getType().equalsIgnoreCase(AppConstants.SPOTIFY)) {
            Logging.d("SPOTIFY Sharing");
            youTubePlayerView.setVisibility(View.INVISIBLE);
            findViewById(R.id.img_cardview).setVisibility(View.VISIBLE);

            Glide.with(RealTimePlayer.this).load(playlist.get(songPosition).getImage()).into(spotifyPlayerView);

        }

    }

    String sessionKey, userKey;
    int admin_id;

    private void getIntentData() {
        userId = SharedPrefManager.getInstance(RealTimePlayer.this).getSharedPrefInt(AppConstants.INTENT_USER_ID);
        admin_id = getIntent().getExtras().getInt("admin_id");
        playlistId = getIntent().getExtras().getInt(AppConstants.INTENT_PLAYLIST_ID);
        sessionKey = getIntent().getExtras().getString(AppConstants.INTENT_SESSION_KEY);
        userKey = getIntent().getExtras().getString(AppConstants.INTENT_USER_KEY);

        String comingFrom = getIntent() == null ? "" : getIntent().getStringExtra(AppConstants.INTENT_COMING_FROM);

        if (!TextUtils.isEmpty(comingFrom)
                && comingFrom.equals("NOTIFICATION")) {
            isControlledByUser = false;
        } else
            isControlledByUser = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_youtube_playler);

        getIntentData();

        initialiseSpotifyLogin();

        initialiseRealTimeDB();

        initViews();

        setAdapter();

        inItListeners();
        statusBarColorChange();
        Handler handler1 = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "running");

                if (killMe)
                    return;

                handler1.postDelayed(this, 1000);

                if (playedSongType.equalsIgnoreCase(AppConstants.YOUTUBE) && player != null) {
                    lengthms = player.getDurationMillis();
                    float current = player.getCurrentTimeMillis();
                    float wowInt = ((current / lengthms) * 100);
                    if (seekusedbyuser == false) {
                        seekbar.setProgress((int) wowInt);
                    }

                } else if (playedSongType.equalsIgnoreCase(AppConstants.SPOTIFY) && spotifyPlayer != null) {

                    if (spotifyPlayer.getMetadata() != null && spotifyPlayer.getMetadata().currentTrack != null) {
                        spotifyLengthms = spotifyPlayer.getMetadata().currentTrack.durationMs;
                        float wow = spotifyPlayer.getPlaybackState().positionMs;
                        float wowInt = ((wow / spotifyLengthms) * 100);
                        if (seekusedbyuser == false) {
                            seekbar.setProgress((int) wowInt);
                        }
                    }
                }
            }
        };

        handler1.postDelayed(runnable, 1000);

        playerStateChangeListener = new RealTimePlayer.MyPlayerStateChangeListener();
        playbackEventListener = new RealTimePlayer.MyPlaybackEventListener();


        seekbar = (SeekBar) findViewById(R.id.seekBar);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                //if (isPlayerPlaying()) {
                int to = 0;
                try {
                    Log.i("Length: ", "Seek: " + progress);

                    if (playedSongType.equalsIgnoreCase(AppConstants.YOUTUBE) && player != null) {

                        lengthms = player.getDurationMillis();
                        to = (int) (lengthms * progress / 100);

                    } else if (playedSongType.equalsIgnoreCase(AppConstants.SPOTIFY) && spotifyPlayer != null) {

                        spotifyLengthms = spotifyPlayer.getMetadata().currentTrack.durationMs;

                        to = (int) (spotifyLengthms * progress / 100);
                    }
                    //realTimeModel.getSessions().get(AppConstants.SESSION_CHILD+"1").setStart_in(to);
                    realTimeModel.getSessions().get(sessionKey).setStart_in(to);
                    myRef.setValue(realTimeModel);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //}
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekusedbyuser = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekusedbyuser = false;

                int to = 0;
                try {
                    if (playedSongType.equalsIgnoreCase(AppConstants.YOUTUBE) && player != null) {

                        int progress = seekBar.getProgress();
                        lengthms = player.getDurationMillis();
                        float current = player.getCurrentTimeMillis();
                        to = (int) (lengthms * progress / 100);

                    } else if (playedSongType.equalsIgnoreCase(AppConstants.SPOTIFY) && spotifyPlayer != null) {

                        int progress = seekBar.getProgress();
                        spotifyLengthms = spotifyPlayer.getMetadata().currentTrack.durationMs;
                        to = (int) (spotifyLengthms * progress / 100);
                    }

                    realTimeModel.getSessions().get(sessionKey).setElapsed_time(to);
                    realTimeModel.getSessions().get(sessionKey).setRead_elapsed(true);
                    myRef.setValue(realTimeModel);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //   seekbar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        //   seekbar.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        Play_Pause = (Button) findViewById(R.id.button2);
        Play_Pause.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                if (isPlayerPlaying()) {
                    int to = 0;
                    try {
                        if (playedSongType.equalsIgnoreCase(AppConstants.YOUTUBE) && player != null) {

                            int progress = seekbar.getProgress();
                            lengthms = player.getDurationMillis();
                            float current = player.getCurrentTimeMillis();
                            to = (int) (lengthms * progress / 100);

                        } else if (playedSongType.equalsIgnoreCase(AppConstants.SPOTIFY) && spotifyPlayer != null) {

                            int progress = seekbar.getProgress();
                            spotifyLengthms = spotifyPlayer.getMetadata().currentTrack.durationMs;
                            to = (int) (spotifyLengthms * progress / 100);
                        }

                        myRef.setValue(realTimeModel);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    realTimeModel.getSessions().get(sessionKey).setElapsed_time(to);
                    realTimeModel.getSessions().get(sessionKey).setRead_elapsed(true);
                    realTimeModel.getSessions().get(sessionKey).setStatus(AppConstants.PAUSE);
                    myRef.setValue(realTimeModel);
                    Play_Pause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);

                } else {
                    //realTimeModel.getSessions().get(AppConstants.SESSION_CHILD+"1").setStatus(AppConstants.START);
                    realTimeModel.getSessions().get(sessionKey).setStatus(AppConstants.START);
                    myRef.setValue(realTimeModel);
                    Play_Pause.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                }

            }
        });

        live_streaming_btn.setOnRippleCompleteListener(new OnRippleCompleteListener() {
            @Override
            public void onComplete(View v) {
                onClick(v);
            }
        });

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

    FirebaseDatabase database;
    DatabaseReference myRef;

    public void alertDialogShow(String message, boolean bothButton,boolean isFromAdmin) {

        AlertDialog alertDialog = new AlertDialog.Builder(RealTimePlayer.this).create();
        alertDialog.setTitle(getResources().getString(R.string.app_name));
        alertDialog.setMessage(message);
        if (bothButton) {
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if(isFromAdmin){
                                callToDeleteSession(sessionKey);
                            }else {
                                finish();
                            }
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        } else {
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
        }
        alertDialog.show();
    }

    private void initialiseRealTimeDB() {
        database = FirebaseDatabase.getInstance();

        myRef = database.getReference(AppConstants.SESSION);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                try {
                    dataSnapshot.child("sessions");
                    HashMap<String, RealTimeSession> sessionHashMap = new HashMap<>();
                    HashMap<String, RealTimeUser> userHashMap = new HashMap<>();

                    for (DataSnapshot ds : dataSnapshot.child("sessions").getChildren()) {
                        RealTimeSession realTimeSession = ds.getValue(RealTimeSession.class);
                        sessionHashMap.put(ds.getKey(), realTimeSession);
                    }

                    for (DataSnapshot ds : dataSnapshot.child("users").getChildren()) {
                        RealTimeUser realTimeUser = ds.getValue(RealTimeUser.class);
                        userHashMap.put(ds.getKey(), realTimeUser);
                    }

                    realTimeModel = new RealTimeModel();
                    realTimeModel.setSessions(sessionHashMap);
                    realTimeModel.setUsers(userHashMap);

                    // admin end the session and call other users side
                    // If Session not exist, It means admin has done with session or it may deleted by him/her
                    if (!realTimeModel.getSessions().containsKey(sessionKey)) {
                        if (!(isControlledByUser) && realTimeModel.getUsers().get(userKey).getSession_id().equalsIgnoreCase(sessionKey)) {

                            // removing collab entry from RealTime DB

                            realTimeModel.getUsers().remove(userKey);
                            myRef.setValue(realTimeModel);
                            try {
                                if (player.isPlaying())
                                    player.pause();
                                else if (spotifyPlayer.getPlaybackState().isPlaying)
                                    spotifyPlayer.pause(null);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                            alertDialogShow("Live Session Is End From Admin", false,false);
                        }
                    } else { // It will call while session exist

                        RealTimeSession realTimeSession = sessionHashMap.get(sessionKey);

                        playedSongType = realTimeModel.getSessions().get(sessionKey).getSongType();


                        if (playlist != null && playlist.size() > 0
                                && realTimeSession.getStatus().equals(AppConstants.START)) {

                            // It will execute while session status is set as STARTED
                            if (realTimeSession.isRead_elapsed()) {

                                if (realTimeSession.getSongType().equalsIgnoreCase(AppConstants.YOUTUBE)) {
                                    player.seekToMillis((int) realTimeSession.getElapsed_time());
                                } else if (realTimeSession.getSongType().equalsIgnoreCase(AppConstants.SPOTIFY)) {
                                    spotifyPlayer.seekToPosition(null, (int) realTimeSession.getElapsed_time());
                                    spotifyPlayer.resume(null);
                                }
                                realTimeModel.getSessions().get(sessionKey).setRead_elapsed(false);
                                myRef.setValue(realTimeModel);

                            } else if (realTimeSession.isSong_changed()) {
                                realTimeModel.getSessions().get(sessionKey).setSong_changed(false);
                                myRef.setValue(realTimeModel);

                                int songPosition = realTimeModel.getSessions().get(sessionKey).getSongPosiionInList();

                                if (songPosition < 0 || songPosition == playlist.size())
                                    return;

                                setPlayerView(songPosition);

                                // playing the changed song
                                if (playlist.get(songPosition).getType().equalsIgnoreCase(AppConstants.YOUTUBE)) {

                                    if (spotifyPlayer != null && spotifyPlayer.getPlaybackState().isPlaying) {
                                        spotifyPlayer.pause(null);
                                    }

                                    player.loadVideo(playlist.get(songPosition).getTrackId());
                                } else if (realTimeSession.getSongType().equalsIgnoreCase(AppConstants.SPOTIFY)) {

                                    if (player != null && player.isPlaying()) {
                                        player.pause();
                                    }

                                    spotifyPlayer.playUri(null, playlist.get(songPosition).getTrackId(), 0, 0);
                                }
                            } else {

                                int songPosition = realTimeModel.getSessions().get(sessionKey).getSongPosiionInList();

                                if (songPosition < 0 || songPosition == playlist.size())
                                    return;

                                setPlayerView(songPosition);

                                if (!player.isPlaying() && playlist.get(songPosition).getType().equalsIgnoreCase(AppConstants.YOUTUBE)) {

                                    if (spotifyPlayer != null && spotifyPlayer.getPlaybackState().isPlaying) {
                                        spotifyPlayer.pause(null);
                                    }

                                    player.loadVideo(playlist.get(songPosition).getTrackId());
                                } else if (!spotifyPlayer.getPlaybackState().isPlaying && realTimeSession.getSongType().equalsIgnoreCase(AppConstants.SPOTIFY)) {

                                    if (player != null && player.isPlaying()) {
                                        player.pause();
                                    }

                                    spotifyPlayer.playUri(null, playlist.get(songPosition).getTrackId(), 0, 0);
                                }
                            }

                        } else if (realTimeSession.getStatus().equals(AppConstants.PAUSE)) {

                            // Song will pause
                            if (player.isPlaying())
                                player.pause();
                            else if (spotifyPlayer.getPlaybackState().isPlaying)
                                spotifyPlayer.pause(null);

                            Play_Pause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
                        } else if (realTimeSession.getStatus().equals(AppConstants.ENDED) && isControlledByUser) {

                            // Session will end for admin only
                            realTimeModel.getSessions().remove(sessionKey);

                            myRef.setValue(realTimeModel);
                            try {
                                if (player.isPlaying())
                                    player.pause();
                                else if (spotifyPlayer.getPlaybackState().isPlaying)
                                    spotifyPlayer.pause(null);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }


                        }
                    }

                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void inItListeners() {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initViews() {
        playerCurrentDurationTv = findViewById(R.id.playerCurrentTimeText);
        playerTotalDurationTv = findViewById(R.id.playerTotalTimeText);
        live_streaming_btn = findViewById(R.id.live_streaming_btn);
        spotifyPlayerView = findViewById(R.id.imageRsplayer);
        youTubePlayerView = findViewById(R.id.myYoutube);
        youTubePlayerView.initialize(AppConstants.YOUTUBE_KEY, this);
        titlemain = findViewById(R.id.youtube_title);

        titlemain.setText(title);
        titlemain.setSelected(true);

        forward = findViewById(R.id.fastforward);
        rewind = findViewById(R.id.fastrewind);

        forward.setBackground(ContextCompat.getDrawable(RealTimePlayer.this, R.drawable.ic_fast_forward_black_24dp));
        rewind.setBackground(ContextCompat.getDrawable(RealTimePlayer.this, R.drawable.ic_fast_rewind_black_24dp));

        forward.setOnClickListener(this);
        rewind.setOnClickListener(this);

        if (isControlledByUser) {
            findViewById(R.id.controls_hldr).setVisibility(View.VISIBLE);
            live_streaming_btn.setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.controls_hldr).setVisibility(View.GONE);
            live_streaming_btn.setVisibility(View.GONE);
        }

    }

    private void setAdapter() {
        playlist = new ArrayList<>();

        playlistRv = findViewById(R.id.playlist_rv);
        playlistRv.setLayoutManager(new LinearLayoutManager(RealTimePlayer.this, LinearLayoutManager.VERTICAL, false));
        playlistRv.setHasFixedSize(true);

        publicPlaylistItemAdapter = new PublicPlaylistItemAdapter(RealTimePlayer.this, playlist);
        publicPlaylistItemAdapter.setCustomItemClickListener(new PublicPlaylistItemAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
            }
        });
        playlistRv.setAdapter(publicPlaylistItemAdapter);

        callPlaylistDetailAPI(String.valueOf(playlistId));


        viewcollabList = new ArrayList<>();
        viewCollabRecyclerView = findViewById(R.id.view_collaborations);
        progressBarViewCollab = findViewById(R.id.progressbarViewCollab);
        progressBarViewCollab.setVisibility(View.VISIBLE);
        viewCollabRecyclerView.setHasFixedSize(true);
        viewCollabRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        ViewCollabAdapter = new ViewCollabAdapter(this, viewcollabList);
        ViewCollabAdapter.setCustomItemClickListener(new com.shorincity.vibin.music_sharing.adapters.ViewCollabAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

            }
        });

        viewCollabRecyclerView.setAdapter(ViewCollabAdapter);

        parseCollaborations();
    }

    private void callPlaylistDetailAPI(String playlistId) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(RealTimePlayer.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<ArrayList<PlaylistDetailModel>> callback = dataAPI.getPublicPlaylistDetail(token,
                SharedPrefManager.getInstance(RealTimePlayer.this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN), playlistId);
        callback.enqueue(new Callback<ArrayList<PlaylistDetailModel>>() {
            @Override
            public void onResponse(Call<ArrayList<PlaylistDetailModel>> call, retrofit2.Response<ArrayList<PlaylistDetailModel>> response) {
                playlist.clear();
                if (response != null && response.body() != null && response.body().size() > 0) {
                    playlistRv.setVisibility(View.VISIBLE);
                    playlist.addAll(response.body());
                    publicPlaylistItemAdapter.notifyDataSetChanged();

                    if (!isControlledByUser) {
                        Handler handler = new Handler();

                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                if (realTimeModel == null && realTimeModel.getUsers() == null)
                                    handler.postDelayed(this, 1000);
                                else {
                                    realTimeModel.getUsers().get(userKey).setJoined_status(AppConstants.JOINED);
                                    myRef.setValue(realTimeModel);
                                }
                            }
                        };

                        handler.postDelayed(runnable, 1000);
                    }

                } else {
                    playlistRv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<PlaylistDetailModel>> call, Throwable t) {

            }
        });
    }

    private void parseCollaborations() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, view_collab, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    viewcollabList.clear();
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String email = jsonObject.getString("email");
                        String username = jsonObject.getString("username");
                        String fullname = jsonObject.getString("fullname");
                        String avatar_link = jsonObject.getString("avatar_link");

                        ViewCollab viewCollab = new ViewCollab();
                        viewCollab.setAvatarLink(avatar_link);
                        viewCollab.setEmail(email);
                        viewCollab.setFullname(fullname);
                        viewCollab.setUsername(username);
                        viewCollab.setId(id);

                        viewcollabList.add(viewCollab);
                        ViewCollabAdapter.notifyDataSetChanged();
                    }
                    progressBarViewCollab.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String token = SharedPrefManager.getInstance(RealTimePlayer.this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);

                params.put("token", token);
                params.put("playlist_id", String.valueOf(playlistId));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String token = AppConstants.TOKEN + SharedPrefManager.getInstance(RealTimePlayer.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
                params.put("Authorization", token);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    // youtube functions

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

        isAddSongLogRecorded = false;
        this.player = youTubePlayer;
        youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
        youTubePlayer.setPlaybackEventListener(playbackEventListener);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(RealTimePlayer.this, REQUEST_VIDEO);
        } else {
            Toast.makeText(this, "ERROR!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {

        if (isControlledByUser) {
            alertDialogShow("Are You Sure Want To Close Session", true,true);
        } else {
            alertDialogShow("Are You Sure Want To Exit From Live Session", true,false);
        }

        // killing all the handlers
        //handler.removeCallbacksAndMessages(my);
        /*if (spotifyHandler != null)
            spotifyHandler.removeCallbacksAndMessages(spotifyRunnable);*/

        if (spotifyPlayer != null && spotifyPlayer.getPlaybackState().isPlaying)
            spotifyPlayer.pause(null);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.live_streaming_btn:
//                if(playlist != null && playlist.size() > 0 ) {
//                    realTimeModel.getSessions().get(sessionKey).setStatus(AppConstants.START);
//                    myRef.setValue(realTimeModel);
//                    live_streaming_btn.setEnabled(false);
//                } else {
//                    showMessage("Please wait for a while while session is going to prepare");
//                }
                alertDialogShow("Are You Sure Want To Close Session", true,true);
                break;

            case R.id.fastforward:

                int songPosition = realTimeModel.getSessions().get(sessionKey).getSongPosiionInList() + 1;

                if (songPosition > -1 && songPosition < playlist.size()) {

                    realTimeModel.getSessions().get(sessionKey).setSongID(playlist.get(songPosition).getTrackId());
                    realTimeModel.getSessions().get(sessionKey).setSongPosiionInList(songPosition);
                    realTimeModel.getSessions().get(sessionKey).setSongType(playlist.get(songPosition).getType());
                    realTimeModel.getSessions().get(sessionKey).setStart_in(0);
                    realTimeModel.getSessions().get(sessionKey).setSong_changed(true);


                    myRef.setValue(realTimeModel);
                }
                break;

            case R.id.fastrewind:
                int songPosition1 = realTimeModel.getSessions().get(sessionKey).getSongPosiionInList() - 1;

                if (songPosition1 > -1 && songPosition1 < playlist.size()) {

                    realTimeModel.getSessions().get(sessionKey).setSongID(playlist.get(songPosition1).getTrackId());
                    realTimeModel.getSessions().get(sessionKey).setSongPosiionInList(songPosition1);
                    realTimeModel.getSessions().get(sessionKey).setSongType(playlist.get(songPosition1).getType());
                    realTimeModel.getSessions().get(sessionKey).setStart_in(0);
                    realTimeModel.getSessions().get(sessionKey).setSong_changed(true);

                    myRef.setValue(realTimeModel);
                }
                break;
        }

    }

    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {

        @Override
        public void onPlaying() {
            // Called when playback starts, either due to user action or call to play().
            //            showMessage("Playing");
        }

        @Override
        public void onPaused() {
            // Called when playback is paused, either due to user action or call to pause().
//            showMessage("Paused");
        }

        @Override
        public void onStopped() {
            // Called when playback stops for a reason other than being paused.
//            showMessage("Stopped");
        }

        @Override
        public void onBuffering(boolean b) {
            // Called when buffering starts or ends.
        }

        @Override
        public void onSeekTo(int i) {
            // Called when a jump in playback position occurs, either
            // due to user scrubbing or call to seekRelativeMillis() or seekToMillis()
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
            player.play();
            // Playback methods such as play(), pause() or seekToMillis(int) may be called after this callback.
        }

        @Override
        public void onAdStarted() {
            // Called when playback of an advertisement starts.
        }

        @Override
        public void onVideoStarted() {
            // Called when playback of the video starts.

            // IMP : Commented By Swati
            //player.seekToMillis((int) realTimeModel.getSessions().get(AppConstants.SESSION_CHILD+"1").getStart_in());
            player.seekToMillis((int) realTimeModel.getSessions().get(sessionKey).getStart_in());
        }

        @Override
        public void onVideoEnded() {
            // Called when the video reaches its end.

            int songPosition = realTimeModel.getSessions().get(sessionKey).getSongPosiionInList() + 1;

            if (songPosition < playlist.size()) {


                // Preparing RealTime DB for the next Song by their track, position in list
                realTimeModel.getSessions().get(sessionKey).setSongID(playlist.get(songPosition).getTrackId());
                realTimeModel.getSessions().get(sessionKey).setSongPosiionInList(songPosition);

                // SongType : Youtube or Spotify
                realTimeModel.getSessions().get(sessionKey).setSongType(playlist.get(songPosition).getType());

                // setting 0 as newly added song should play from initial state.
                realTimeModel.getSessions().get(sessionKey).setStart_in(0);

                // setting true as current song has been end and next one should be play
                realTimeModel.getSessions().get(sessionKey).setSong_changed(true);

                // Updating RealTime DB
                myRef.setValue(realTimeModel);
            } else {
                realTimeModel.getSessions().get(sessionKey).setStatus(AppConstants.ENDED);
                myRef.setValue(realTimeModel);
            }
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            // Called when an error occurs.
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler1 != null)
            handler1.removeCallbacks(runnable);
        /*if (spotifyHandler != null)
            spotifyHandler.removeCallbacksAndMessages(spotifyRunnable);*/

        if (spotifyPlayer != null && spotifyPlayer.getPlaybackState().isPlaying)
            spotifyPlayer.pause(null);

        killMe = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (handler1 != null)
            handler1.removeCallbacks(runnable);
        /*if (spotifyHandler != null)
            spotifyHandler.removeCallbacksAndMessages(spotifyRunnable);*/

        if (spotifyPlayer != null && spotifyPlayer.getPlaybackState().isPlaying)
            spotifyPlayer.pause(null);
        killMe = true;
    }

    // Session Deleting : It will call whenever admin wants to go back
    private void callToDeleteSession(String sessionKey) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<APIResponse> callback = dataAPI.deleteRealTimeSession(token, sessionKey);
        callback.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, retrofit2.Response<APIResponse> response) {
                if (response != null
                        && response.body() != null) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        Log.i(TAG, "Session Ended");
                        try {
                            isEndSessionFromAdmin = true;
                            realTimeModel.getSessions().get(sessionKey).setStatus(AppConstants.ENDED);
                            myRef.setValue(realTimeModel);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (response.body().getStatus().equalsIgnoreCase("failed") && !TextUtils.isEmpty(response.body().getMessage()))
                        Log.i(TAG, "Session Ending Failed");
                    else
                        Log.i(TAG, "Session Ending Error");
                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                Toast.makeText(RealTimePlayer.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });

    }
}


