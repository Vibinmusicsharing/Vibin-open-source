package com.shorincity.vibin.music_sharing.activity;


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
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.model.PublicPlaylistItemAdapter;
import com.shorincity.vibin.music_sharing.model.firebase.RealTimeModel;
import com.shorincity.vibin.music_sharing.model.firebase.RealTimeSession;
import com.shorincity.vibin.music_sharing.model.firebase.RealTimeUser;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.adapters.ViewCollab;
import com.shorincity.vibin.music_sharing.adapters.ViewCollabAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

// RealTime Youtube Player : for testing purpose only

public class RealTimeYoutubePlayler extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, View.OnClickListener {
    private static String TAG = RealTimeYoutubePlayler.class.getName();
    String view_collab = AppConstants.BASE_URL + "playlist/view_collab/";

    boolean isSeekChanged;
    RealTimeModel realTimeModel;

    int playlistId;
    boolean isControlledByUser = false;

    int REQUEST_VIDEO = 123;
    YouTubePlayerView youTubePlayerView;
    int lengthms = 270000;

    private RealTimeYoutubePlayler.MyPlayerStateChangeListener playerStateChangeListener;
    private RealTimeYoutubePlayler.MyPlaybackEventListener playbackEventListener;

    private YouTubePlayer player;
    Handler handler;
    Runnable my;
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

    ArrayList<String> songsIdsList;

    ProgressBar progressBar;
    Button Play_Pause;
    private BottomSheetBehavior behavior;
    private AppCompatTextView arrow;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_youtube_playler);

        initialiseRealTimeDB();

        getIntentData();

        initViews();

        setAdapter();

        inItListeners();
        statusBarColorChange();
        handler = new Handler();
        my = new Runnable() {
            @Override
            public void run() {
                //Do something after 20 seconds

                if (killMe)
                    return;

                handler.postDelayed(this, 1000);

                if (player != null) {
                    lengthms = player.getDurationMillis();
                    float current = player.getCurrentTimeMillis();
                    float wowInt = ((current / lengthms) * 100);
                    if (seekusedbyuser == false) {
                        seekbar.setProgress((int) wowInt);
                    }
                }
            }
        };
        handler.postDelayed(my, 2000);

        playerStateChangeListener = new RealTimeYoutubePlayler.MyPlayerStateChangeListener();
        playbackEventListener = new RealTimeYoutubePlayler.MyPlaybackEventListener();


        seekbar = (SeekBar) findViewById(R.id.seekBar);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                try {
                    Log.i("Length: ", "Seek: " + progress);
                    lengthms = player.getDurationMillis();
                    int to = (int) (lengthms * progress / 100);
                    realTimeModel.getSessions().get(AppConstants.SESSION_CHILD + "1").setStart_in(to);
                    myRef.setValue(realTimeModel);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekusedbyuser = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekusedbyuser = false;

                try {
                    int progress = seekBar.getProgress();
                    lengthms = player.getDurationMillis();
                    float current = player.getCurrentTimeMillis();
                    int to = (int) (lengthms * progress / 100);
                    realTimeModel.getSessions().get(AppConstants.SESSION_CHILD + "1").setElapsed_time(to);
                    realTimeModel.getSessions().get(AppConstants.SESSION_CHILD + "1").setRead_elapsed(true);
                    myRef.setValue(realTimeModel);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //player.seekToMillis(to);
            }
        });
        //seekbar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        //seekbar.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        Play_Pause = (Button) findViewById(R.id.button2);
        Play_Pause.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    realTimeModel.getSessions().get(AppConstants.SESSION_CHILD + "1").setStatus(AppConstants.PAUSE);
                    myRef.setValue(realTimeModel);
                    //player.pause();
                    Play_Pause.setBackground(getDrawable(R.drawable.ic_play_circle_filled_black_24dp));

                } else {
                    realTimeModel.getSessions().get(AppConstants.SESSION_CHILD + "1").setStatus(AppConstants.START);
                    myRef.setValue(realTimeModel);
                    //player.play();
                    Play_Pause.setBackground(getDrawable(R.drawable.ic_pause_circle_filled_black_24dp));
                }

            }
        });

        findViewById(R.id.live_streaming_btn).setOnClickListener(this);
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

                    Log.i(TAG, realTimeModel.toString());

                    RealTimeSession realTimeSession = sessionHashMap.get("session_child_1");


                    if (playlist != null && playlist.size() > 0
                            && realTimeSession.getStatus().equals(AppConstants.START)) {

                        if (realTimeSession.isRead_elapsed()) {
                            player.seekToMillis((int) realTimeSession.getElapsed_time());
                            realTimeModel.getSessions().get(AppConstants.SESSION_CHILD + "1").setRead_elapsed(false);
                            myRef.setValue(realTimeModel);
                        } else if (realTimeSession.isSong_changed()) {
                            realTimeModel.getSessions().get(AppConstants.SESSION_CHILD + "1").setSong_changed(false);
                            myRef.setValue(realTimeModel);

                            int songPosition = realTimeModel.getSessions().get(AppConstants.SESSION_CHILD + "1").getSongPosiionInList();

                            player.loadVideo(playlist.get(songPosition).getTrackId());
                        } /*else if(!realTimeSession.isSong_changed() &&
                                realTimeModel.getSessions().get(AppConstants.SESSION_CHILD+"1").getSongPosiionInList() == playlist.size()) {

                            player.pause();
                            Play_Pause.setBackground(ContextCompat.getDrawable(RealTimeYoutubePlayler.this, R.drawable.ic_play_circle_filled_black_24dp));

                        }*/ else {
                            if (!player.isPlaying()) {

                                //player.loadVideos(songsIdsList);

                                int songPosition = realTimeModel.getSessions().get(AppConstants.SESSION_CHILD + "1").getSongPosiionInList();
                                player.loadVideo(playlist.get(songPosition).getTrackId());

                                // IMP : Commented by Swati
                                //player.cueVideo(playlist.get(0).getTrackId());
                                //player.seekToMillis((int) realTimeSession.getStart_in());
                            }

                        }

                    } else if (realTimeSession.getStatus().equals(AppConstants.PAUSE)) {
                        player.pause();
                        Play_Pause.setBackground(ContextCompat.getDrawable(RealTimeYoutubePlayler.this, R.drawable.ic_play_circle_filled_black_24dp));
                    } else if (realTimeSession.getStatus().equals(AppConstants.ENDED)) {
                        try {
                            if (player.isPlaying())
                                player.pause();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        finish();
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

        // WorkingCode : commented by swati
                /*RealTimeSession realTimeSession = new RealTimeSession();
                String token = AppConstants.TOKEN + SharedPrefManager.getInstance(PlaylistDetailActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
                int userId = SharedPrefManager.getInstance(PlaylistDetailActivity.this).getSharedPrefInt(AppConstants.INTENT_USER_ID);
                NumberFormat formatter = new DecimalFormat("00");
                String duration = "";

                duration = formatter.format(myPlaylistModel.getPlaylistDurationHours()) + ":" +
                        formatter.format(myPlaylistModel.getPlaylistDurationMinutes()) + ":" +
                        formatter.format(myPlaylistModel.getPlaylistDurationSeconds());

                realTimeSession.setSession_token(token);
                realTimeSession.setAdminId(userId);
                realTimeSession.setPlaylist_id(id);
                realTimeSession.setPlaylist_time(duration);
                realTimeSession.setInvited(viewcollabList.size());
                realTimeSession.setJoined(0);
                realTimeSession.setStatus(AppConstants.WAIT);
                realTimeSession.setStart_in(10);
                realTimeSession.setElapsed_time(0);
                realTimeSession.setRead_elapsed(false);


                HashMap<String, RealTimeSession> sessionHashMap = new HashMap<>();
                sessionHashMap.put(AppConstants.SESSION_CHILD+"1",realTimeSession);

                HashMap<String, RealTimeUser> userHashMap = new HashMap<>();

                for(int i = 0; i <viewcollabList.size(); i++) {
                    RealTimeUser realTimeUser = new RealTimeUser(token,viewcollabList.get(i).getId(),AppConstants.WAIT);
                    userHashMap.put(AppConstants.USER_CHILD+i,realTimeUser);
                }

                RealTimeModel realTimeModel = new RealTimeModel();
                realTimeModel.setSessions(sessionHashMap);
                realTimeModel.setUsers(userHashMap);


                myRef.setValue(realTimeModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PlaylistDetailActivity.this, "Success",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });*/
    }

    private void inItListeners() {
    }

    private void getIntentData() {
        userId = SharedPrefManager.getInstance(RealTimeYoutubePlayler.this).getSharedPrefInt(AppConstants.INTENT_USER_ID);

        playlistId = getIntent().getExtras().getInt("id");

        String comingFrom = getIntent() == null ? "" : getIntent().getStringExtra(AppConstants.INTENT_COMING_FROM);

        if (!TextUtils.isEmpty(comingFrom) && comingFrom.equals("NOTIFICATION")) {
            isControlledByUser = false;
        } else
            isControlledByUser = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initViews() {
        arrow = findViewById(R.id.arrow_up);

        youTubePlayerView = findViewById(R.id.myYoutube);
        youTubePlayerView.initialize(AppConstants.YOUTUBE_KEY, this);
        TextView titlemain = findViewById(R.id.youtube_title);

        titlemain.setText(title);
        titlemain.setSelected(true);

        forward = findViewById(R.id.fastforward);
        rewind = findViewById(R.id.fastrewind);
        shuffle = findViewById(R.id.shuffle);
        repeatone = findViewById(R.id.repeatonce);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            forward.setBackground(getDrawable(R.drawable.ic_fast_forward_black_24dp));
            rewind.setBackground(getDrawable(R.drawable.ic_fast_rewind_black_24dp));
            shuffle.setBackground(getDrawable(R.drawable.ic_shuffle_black_24dp));
            repeatone.setBackground(getDrawable(R.drawable.ic_add_blackwhite_24dp));
        }
        forward.setOnClickListener(this);
        rewind.setOnClickListener(this);

        if (isControlledByUser) {
            findViewById(R.id.controls_hldr).setVisibility(View.VISIBLE);
            findViewById(R.id.live_streaming_btn).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.controls_hldr).setVisibility(View.GONE);
            findViewById(R.id.live_streaming_btn).setVisibility(View.GONE);
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

    private void callPlaylistDetailAPI(String playlistId) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(RealTimeYoutubePlayler.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<ArrayList<PlaylistDetailModel>> callback = dataAPI.getPublicPlaylistDetail(token,
                SharedPrefManager.getInstance(RealTimeYoutubePlayler.this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN), playlistId);
        callback.enqueue(new Callback<ArrayList<PlaylistDetailModel>>() {
            @Override
            public void onResponse(Call<ArrayList<PlaylistDetailModel>> call, retrofit2.Response<ArrayList<PlaylistDetailModel>> response) {
                playlist.clear();
                if (response != null && response.body() != null && response.body().size() > 0) {
                    playlistRv.setVisibility(View.VISIBLE);
                    playlist.addAll(response.body());
                    publicPlaylistItemAdapter.notifyDataSetChanged();

                    songsIdsList = new ArrayList<>();

                    for (int i = 0; i < playlist.size(); i++) {
                        songsIdsList.add(playlist.get(i).getTrackId());
                    }


                    if (!isControlledByUser) {
                        Handler handler = new Handler();

                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                if (realTimeModel == null && realTimeModel.getUsers() == null)
                                    handler.postDelayed(this, 1000);
                                else {
                                    realTimeModel.getUsers().get(AppConstants.USER_CHILD + "1").setJoined_status(AppConstants.JOINED);
                                    myRef.setValue(realTimeModel);
                                }
                            }
                        };

                        handler.postDelayed(runnable, 1000);


                    }

                } else {
                    //findViewById(R.id.tv_playlist_placeholder).setVisibility(View.VISIBLE);
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
                String token = SharedPrefManager.getInstance(RealTimeYoutubePlayler.this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);

                params.put("token", token);
                params.put("playlist_id", String.valueOf(playlistId));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String token = AppConstants.TOKEN + SharedPrefManager.getInstance(RealTimeYoutubePlayler.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
                params.put("Authorization", token);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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
            youTubeInitializationResult.getErrorDialog(RealTimeYoutubePlayler.this, REQUEST_VIDEO);
        } else {
            Toast.makeText(this, "ERROR!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VIDEO) {
            youTubePlayerView.initialize(AppConstants.YOUTUBE_KEY, RealTimeYoutubePlayler.this);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isControlledByUser) {
            realTimeModel.getSessions().get(AppConstants.SESSION_CHILD + "1").setStatus(AppConstants.ENDED);
            myRef.setValue(realTimeModel);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.live_streaming_btn:
                if (playlist != null && playlist.size() > 0) {
                    realTimeModel.getSessions().get(AppConstants.SESSION_CHILD + "1").setStatus(AppConstants.START);
                    myRef.setValue(realTimeModel);
                } else {
                    showMessage("Please wait for a while while session is going to prepare");
                }
                break;

            case R.id.fastforward:

                int songPosition = realTimeModel.getSessions().get(AppConstants.SESSION_CHILD + "1").getSongPosiionInList() + 1;

                if (songPosition > -1 && songPosition < playlist.size()) {
                    player.loadVideo(playlist.get(songPosition).getTrackId());
                    realTimeModel.getSessions().get(AppConstants.SESSION_CHILD + "1").setSongID(playlist.get(songPosition).getTrackId());
                    realTimeModel.getSessions().get(AppConstants.SESSION_CHILD + "1").setSongPosiionInList(songPosition);
                    realTimeModel.getSessions().get(AppConstants.SESSION_CHILD + "1").setSongType(playlist.get(songPosition).getType());
                    realTimeModel.getSessions().get(AppConstants.SESSION_CHILD + "1").setSong_changed(true);
                    myRef.setValue(realTimeModel);
                }
                break;

            case R.id.fastrewind:
                int songPosition1 = realTimeModel.getSessions().get(AppConstants.SESSION_CHILD + "1").getSongPosiionInList() - 1;

                if (songPosition1 > -1 && songPosition1 < playlist.size()) {
                    player.loadVideo(playlist.get(songPosition1).getTrackId());
                    realTimeModel.getSessions().get(AppConstants.SESSION_CHILD + "1").setSongID(playlist.get(songPosition1).getTrackId());
                    realTimeModel.getSessions().get(AppConstants.SESSION_CHILD + "1").setSongPosiionInList(songPosition1);
                    realTimeModel.getSessions().get(AppConstants.SESSION_CHILD + "1").setSongType(playlist.get(songPosition1).getType());
                    realTimeModel.getSessions().get(AppConstants.SESSION_CHILD + "1").setSong_changed(true);
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
            player.seekToMillis((int) realTimeModel.getSessions().get(AppConstants.SESSION_CHILD + "1").getStart_in());
        }

        @Override
        public void onVideoEnded() {
            // Called when the video reaches its end.

            int songPosition = realTimeModel.getSessions().get(AppConstants.SESSION_CHILD + "1").getSongPosiionInList() + 1;

            if (songPosition < playlist.size()) {
                player.loadVideo(playlist.get(songPosition).getTrackId());
                realTimeModel.getSessions().get(AppConstants.SESSION_CHILD + "1").setSongID(playlist.get(songPosition).getTrackId());
                realTimeModel.getSessions().get(AppConstants.SESSION_CHILD + "1").setSongPosiionInList(songPosition);
                realTimeModel.getSessions().get(AppConstants.SESSION_CHILD + "1").setSongType(playlist.get(songPosition).getType());
                realTimeModel.getSessions().get(AppConstants.SESSION_CHILD + "1").setSong_changed(true);
                myRef.setValue(realTimeModel);
            } else {
                realTimeModel.getSessions().get(AppConstants.SESSION_CHILD + "1").setStatus(AppConstants.ENDED);
                myRef.setValue(realTimeModel);
            }
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
        killMe = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(my);
        killMe = true;

    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(my);
        killMe = true;
    }
}


