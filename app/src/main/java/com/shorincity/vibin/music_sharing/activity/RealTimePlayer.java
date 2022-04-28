package com.shorincity.vibin.music_sharing.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.adapters.ViewCollab;
import com.shorincity.vibin.music_sharing.adapters.ViewCollabAdapter;
import com.shorincity.vibin.music_sharing.model.APIResponse;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.model.PublicPlaylistItemAdapter;
import com.shorincity.vibin.music_sharing.model.firebase.RealTimeModel;
import com.shorincity.vibin.music_sharing.model.firebase.RealTimeSession;
import com.shorincity.vibin.music_sharing.model.firebase.RealTimeUser;
import com.shorincity.vibin.music_sharing.model.shareplaylist.PlaylistDetailResponse;
import com.shorincity.vibin.music_sharing.ripples.RippleButton;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.shorincity.vibin.music_sharing.utils.Utility;

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
    private static final String TAG = RealTimePlayer.class.getName();
    private final String view_collab = AppConstants.BASE_URL + "playlist/v1/view_collab/";

    private RealTimeModel realTimeModel;

    private int playlistId;
    private boolean isControlledByUser = false;

    private final int REQUEST_VIDEO = 123;
    private YouTubePlayerView youTubePlayerView;
    private TextView titlemain;

    private int lengthms = 270000;

    private RealTimePlayer.MyPlayerStateChangeListener playerStateChangeListener;
    private RealTimePlayer.MyPlaybackEventListener playbackEventListener;

    private YouTubePlayer player;
    private Handler handler;
    private Runnable my;

    private boolean killMe = false;
    private boolean seekusedbyuser = false;
    private SeekBar seekbar;


    // Songs List
    private RecyclerView playlistRv;
    private ArrayList<PlaylistDetailModel> playlist;
    private PublicPlaylistItemAdapter publicPlaylistItemAdapter;

    // Collab List
    private ArrayList<ViewCollab> viewcollabList;
    private com.shorincity.vibin.music_sharing.adapters.ViewCollabAdapter ViewCollabAdapter;
    private RecyclerView viewCollabRecyclerView;
    private ProgressBar progressBarViewCollab;

    private Button Play_Pause;
    private RippleButton live_streaming_btn;

    private TextView playerCurrentDurationTv, playerTotalDurationTv;

    private String playedSongType = "";

    private BottomSheetBehavior behavior;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_VIDEO) {
            youTubePlayerView.initialize(AppConstants.YOUTUBE_KEY, RealTimePlayer.this);

        }

    }

    private boolean isPlayerPlaying() {
        boolean isPlaying = false;

        if (playedSongType.equalsIgnoreCase(AppConstants.YOUTUBE))
            isPlaying = player != null && player.isPlaying();

        return isPlaying;
    }

    private void setPlayerView(int songPosition) {

        if (playlist == null || playlist.size() == 0)
            return;

        titlemain.setText(playlist.get(songPosition).getName());
        Logging.d("SPOTIFY Sharing-->" + new Gson().toJson(playlist));
        if (playlist.get(songPosition).getType().equalsIgnoreCase(AppConstants.YOUTUBE)) {
            Logging.d("YOUTUBE Sharing");
            youTubePlayerView.setVisibility(View.VISIBLE);

        }

    }

    String sessionKey, userKey;
    int admin_id;

    private void getIntentData() {
        admin_id = getIntent().getExtras().getInt("admin_id");
        playlistId = getIntent().getExtras().getInt(AppConstants.INTENT_PLAYLIST_ID);
        sessionKey = getIntent().getExtras().getString(AppConstants.INTENT_SESSION_KEY);
        userKey = getIntent().getExtras().getString(AppConstants.INTENT_USER_KEY);

        String comingFrom = getIntent() == null ? "" : getIntent().getStringExtra(AppConstants.INTENT_COMING_FROM);

        isControlledByUser = TextUtils.isEmpty(comingFrom)
                || !comingFrom.equals("NOTIFICATION");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_youtube_playler);

        getIntentData();

        // initialiseSpotifyLogin();

        initialiseRealTimeDB();

        initViews();

        setAdapter();

        inItListeners();
        statusBarColorChange();
        processSeekBar();

        playerStateChangeListener = new RealTimePlayer.MyPlayerStateChangeListener();
        playbackEventListener = new RealTimePlayer.MyPlaybackEventListener();


        seekbar = findViewById(R.id.seekBar);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                //if (isPlayerPlaying()) {
                int to = 0;
                try {
                    Log.i("Length: ", "Seek: " + progress);

                    if (playedSongType.equalsIgnoreCase(AppConstants.YOUTUBE) && player != null) {

                        lengthms = player.getDurationMillis();
                        to = lengthms * progress / 100;

                    }
                    //realTimeModel.getSessions().get(AppConstants.SESSION_CHILD+"1").setStart_in(to);
                    RealTimeSession session = realTimeModel.getSessions().get(sessionKey);
                    if (session != null)
                        session.setStart_in(to);
                    myRef.setValue(realTimeModel);
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
                        to = lengthms * progress / 100;

                    }

                    realTimeModel.getSessions().get(sessionKey).setElapsed_time(to);
                    realTimeModel.getSessions().get(sessionKey).setRead_elapsed(true);
                    myRef.setValue(realTimeModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //   seekbar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        //   seekbar.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        Play_Pause = findViewById(R.id.button2);
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
                            to = lengthms * progress / 100;

                        }

//                        myRef.setValue(realTimeModel);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    RealTimeSession session = realTimeModel.getSessions().get(sessionKey);
                    if (session != null) {
                        session.setElapsed_time(to);
                        session.setRead_elapsed(true);
                        session.setStatus(AppConstants.PAUSE);
                        myRef.setValue(realTimeModel);
                        Play_Pause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
                    }

                } else {
                    //realTimeModel.getSessions().get(AppConstants.SESSION_CHILD+"1").setStatus(AppConstants.START);
                    RealTimeSession session = realTimeModel.getSessions().get(sessionKey);
                    if (session != null) {
                        session.setStatus(AppConstants.START);
                        myRef.setValue(realTimeModel);
                        Play_Pause.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                    }
                }

            }
        });

        live_streaming_btn.setOnRippleCompleteListener(v -> onClick(v));

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

    public void alertDialogShow(String message, boolean bothButton, boolean isFromAdmin) {

        AlertDialog alertDialog = new AlertDialog.Builder(RealTimePlayer.this).create();
        alertDialog.setTitle(getResources().getString(R.string.app_name));
        alertDialog.setMessage(message);
        if (bothButton) {
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (isFromAdmin) {
                                callToDeleteSession(sessionKey);
                            } else {
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
                    Log.i(TAG, "Session Ended:-" + sessionKey);
//                    Log.i(TAG, "Session Ended:-" + realTimeModel.getUsers().get(userKey).getSession_id());


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

                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                            alertDialogShow("Live session has been ended by admin", false, false);
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

                                    player.loadVideo(playlist.get(songPosition).getTrackId());
                                }
                            } else {

                                int songPosition = realTimeModel.getSessions().get(sessionKey).getSongPosiionInList();

                                if (songPosition < 0 || songPosition == playlist.size())
                                    return;

                                setPlayerView(songPosition);

                                if (!player.isPlaying() && playlist.get(songPosition).getType().equalsIgnoreCase(AppConstants.YOUTUBE)) {

                                    player.loadVideo(playlist.get(songPosition).getTrackId());
                                }
                            }

                        } else if (realTimeSession.getStatus().equals(AppConstants.PAUSE)) {

                            // Song will pause
                            if (player.isPlaying())
                                player.pause();

                            Play_Pause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
                        } else if (realTimeSession.getStatus().equals(AppConstants.ENDED) && isControlledByUser) {

                            // Session will end for admin only
                            realTimeModel.getSessions().remove(sessionKey);

                            myRef.setValue(realTimeModel);
                            try {
                                if (player.isPlaying())
                                    player.pause();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }


                        }
                        processSeekBar();
                    }

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
        killMe = false;
        if (player != null && playerTotalDurationTv != null) {
            try {
                lengthms = player.getDurationMillis();
                float current = player.getCurrentTimeMillis();
                float wowInt = ((current / lengthms) * 100);
                seekbar.setProgress((int) wowInt);
                processSeekBar();
                Logging.d("wowInt-->" + wowInt);
                // displaying current duration when song starts to play
                if ((player != null && player.isPlaying()) && (int) wowInt > 0) {
                    playerCurrentDurationTv.setText(Utility.convertDuration(Long.valueOf(player.getCurrentTimeMillis())));
                }
                if (player != null) {
                    if (player.isPlaying()) {
                        RealTimeSession session = realTimeModel.getSessions().get(sessionKey);
                        if (session != null) {
                            session.setElapsed_time((int) (lengthms * seekbar.getProgress() / 100));
                            session.setRead_elapsed(true);
                            session.setStatus(AppConstants.PAUSE);
                            myRef.setValue(realTimeModel);
                        }
                        player.pause();
                        Play_Pause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
                    } else {
                        RealTimeSession session = realTimeModel.getSessions().get(sessionKey);
                        if (session != null) {
                            session.setStatus(AppConstants.START);
                            myRef.setValue(realTimeModel);
                            Play_Pause.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                        }
                        player.play();
                        Play_Pause.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void processSeekBar() {
        if (handler != null) {
            handler.removeCallbacks(my);
            handler = null;
        }
        handler = new Handler();
        my = new Runnable() {
            @Override
            public void run() {
                //Do something after 2 seconds
                if (killMe)
                    return;

                handler.postDelayed(this, 1000);

                if (playedSongType.equalsIgnoreCase(AppConstants.YOUTUBE) && player != null) {

                    lengthms = player.getDurationMillis();
                    float current = player.getCurrentTimeMillis();
                    float wowInt = ((current / lengthms) * 100);
                    if (seekusedbyuser == false) {
                        seekbar.setProgress((int) wowInt);

                    }
                }
            }
        };
        handler.postDelayed(my, 1000);
    }

    private void initViews() {
        AppCompatTextView arrow = findViewById(R.id.arrow_up);

        playerCurrentDurationTv = findViewById(R.id.playerCurrentTimeText);
        playerTotalDurationTv = findViewById(R.id.playerTotalTimeText);
        live_streaming_btn = findViewById(R.id.live_streaming_btn);
        youTubePlayerView = findViewById(R.id.myYoutube);
        youTubePlayerView.initialize(AppConstants.YOUTUBE_KEY, this);
        titlemain = findViewById(R.id.youtube_title);

        String title = "";
        titlemain.setText(title);
        titlemain.setSelected(true);

        ImageView forward = findViewById(R.id.fastforward);
        ImageView rewind = findViewById(R.id.fastrewind);

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

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (behavior != null)
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                //arrow.setVisibility(View.GONE);
            }
        });

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
                if (isControlledByUser) {
                    if (position > -1 && position < playlist.size()) {
                        plaSongFromBottomSheet(position);
                        if (behavior != null)
                            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }
            }

            @Override
            public void onSelectedplaylistID(int ids, boolean selected) {

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

            @Override
            public void onSelectedcollabsID(int ids, boolean selected) {

            }
        });

        viewCollabRecyclerView.setAdapter(ViewCollabAdapter);

        parseCollaborations();
        setBottomSheetForPlaylist();
    }

    private void setBottomSheetForPlaylist() {
        View bottomSheet = findViewById(R.id.bottom_sheet_detail);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    if (playlistRv != null)
                        playlistRv.smoothScrollToPosition(0);
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        /*playlistRv = findViewById(R.id.playlist_rv);
        playlistRv.setLayoutManager(new LinearLayoutManager(RealTimeYoutubePlayler.this, LinearLayoutManager.VERTICAL, false));
        playlistRv.setHasFixedSize(true);

        publicPlaylistItemAdapter = new PublicPlaylistItemAdapter(RealTimeYoutubePlayler.this, playlist);
        publicPlaylistItemAdapter.setCustomItemClickListener(new PublicPlaylistItemAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
            }

            @Override
            public void onSelectedplaylistID(int ids, boolean selected) {

            }
        });
        playlistRv.setAdapter(publicPlaylistItemAdapter);*/

    }

    private void plaSongFromBottomSheet(int songPosition1) {
        if (handler != null) {
            handler.removeCallbacksAndMessages(my);
        }
        killMe = false;
        RealTimeSession session = realTimeModel.getSessions().get(sessionKey);
        if (session != null) {
            if (songPosition1 > -1 && songPosition1 < playlist.size()) {
                session.setSongID(playlist.get(songPosition1).getTrackId());
                session.setSongPosiionInList(songPosition1);
                session.setSongType(playlist.get(songPosition1).getType());
                session.setStart_in(0);
                session.setSong_changed(true);
                session.setStatus(AppConstants.START);
                Play_Pause.setBackgroundResource(R.drawable.ic_baseline_pause_24);

                myRef.setValue(realTimeModel);
            }
        }
    }


    private void callPlaylistDetailAPI(String playlistId) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(RealTimePlayer.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<PlaylistDetailResponse> callback = dataAPI.getPublicPlaylistDetail(token,
                SharedPrefManager.getInstance(RealTimePlayer.this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN), playlistId, AppConstants.SOURCE_TYPE_IN_APP);
        callback.enqueue(new Callback<PlaylistDetailResponse>() {
            @Override
            public void onResponse(Call<PlaylistDetailResponse> call, retrofit2.Response<PlaylistDetailResponse> response) {
                playlist.clear();
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    playlistRv.setVisibility(View.VISIBLE);
                    playlist.addAll(response.body().getTracks());
                    publicPlaylistItemAdapter.notifyDataSetChanged();

                    if (!isControlledByUser) {
                        Handler handler = new Handler();

                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                if (realTimeModel != null && realTimeModel.getUsers() != null &&
                                        realTimeModel.getUsers().get(userKey) != null) {
                                    realTimeModel.getUsers().get(userKey).setJoined_status(AppConstants.JOINED);
                                    myRef.setValue(realTimeModel);
                                } else {
                                    handler.postDelayed(this, 500);
                                }
                            }
                        };

                        handler.postDelayed(runnable, 500);
                    }

                } else {
                    playlistRv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<PlaylistDetailResponse> call, Throwable t) {

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
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer
            youTubePlayer, boolean b) {

        this.player = youTubePlayer;
        youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
        youTubePlayer.setPlaybackEventListener(playbackEventListener);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider
                                                provider, YouTubeInitializationResult youTubeInitializationResult) {
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
            alertDialogShow("Are You Sure Want To Close Session", true, true);
        } else {
            alertDialogShow("Are You Sure Want To Exit From Live Session", true, false);
        }

        // killing all the handlers
        //handler.removeCallbacksAndMessages(my);
        /*if (spotifyHandler != null)
            spotifyHandler.removeCallbacksAndMessages(spotifyRunnable);*/

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
                alertDialogShow("Are You Sure Want To Close Session", true, true);
                break;

            case R.id.fastforward:
                killMe = false;

                RealTimeSession session1 = realTimeModel.getSessions().get(sessionKey);
                if (session1 != null) {
                    int songPosition = session1.getSongPosiionInList() + 1;
                    if (songPosition > (playlist.size() - 1))
                        songPosition = 0;

                    if (songPosition > -1 && songPosition < playlist.size()) {

                        session1.setSongID(playlist.get(songPosition).getTrackId());
                        session1.setSongPosiionInList(songPosition);
                        session1.setSongType(playlist.get(songPosition).getType());
                        session1.setStart_in(0);
                        session1.setSong_changed(true);
                        session1.setStatus(AppConstants.START);
                        Play_Pause.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                        myRef.setValue(realTimeModel);
                    }
                }
                break;

            case R.id.fastrewind:
                killMe = false;
                RealTimeSession session = realTimeModel.getSessions().get(sessionKey);
                if (session != null) {
                    int songPosition1 = session.getSongPosiionInList() - 1;
                    if (songPosition1 < 0)
                        songPosition1 = playlist.size() - 1;

                    if (songPosition1 > -1 && songPosition1 < playlist.size()) {

                        session.setSongID(playlist.get(songPosition1).getTrackId());
                        session.setSongPosiionInList(songPosition1);
                        session.setSongType(playlist.get(songPosition1).getType());
                        session.setStart_in(0);
                        session.setSong_changed(true);
                        session.setStatus(AppConstants.START);
                        Play_Pause.setBackgroundResource(R.drawable.ic_baseline_pause_24);


                        myRef.setValue(realTimeModel);
                    }
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
            RealTimeSession session = realTimeModel.getSessions().get(sessionKey);
            if (session != null)
                player.seekToMillis(session.getStart_in());
        }

        @Override
        public void onVideoEnded() {
            // Called when the video reaches its end.
            RealTimeSession session = realTimeModel.getSessions().get(sessionKey);
            if (session != null) {
                int songPosition = session.getSongPosiionInList() + 1;

                if (songPosition < playlist.size()) {


                    // Preparing RealTime DB for the next Song by their track, position in list
                    session.setSongID(playlist.get(songPosition).getTrackId());
                    session.setSongPosiionInList(songPosition);

                    // SongType : Youtube or Spotify
                    session.setSongType(playlist.get(songPosition).getType());

                    // setting 0 as newly added song should play from initial state.
                    session.setStart_in(0);

                    // setting true as current song has been end and next one should be play
                    session.setSong_changed(true);

                    // Updating RealTime DB
                    myRef.setValue(realTimeModel);
                } else {
                    session.setStatus(AppConstants.ENDED);
                    myRef.setValue(realTimeModel);
                }
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
                if (response.body() != null) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        Log.i(TAG, "Session Ended:-" + sessionKey);
                        try {
                            RealTimeSession session = realTimeModel.getSessions().get(sessionKey);
                            if (session != null)
                                session.setStatus(AppConstants.ENDED);
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


