package com.shorincity.vibin.music_sharing.spotify_files;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import com.shorincity.vibin.music_sharing.model.APIResponse;
import com.shorincity.vibin.music_sharing.model.AddSongLogModel;
import com.shorincity.vibin.music_sharing.model.SongLikeModel;
import com.shorincity.vibin.music_sharing.model.SpotifyArtistModel;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.adapters.AddToPlaylistAdapter;
import com.shorincity.vibin.music_sharing.adapters.Playlist;
import com.shorincity.vibin.music_sharing.adapters.PlaylistRecyclerView;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.spotify_files.background_music.MediaPlayerService;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.shorincity.vibin.music_sharing.utils.Utility;
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
import com.google.gson.Gson;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Player.NotificationCallback;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class SpotifySongsPlayerActivity extends AppCompatActivity implements NotificationCallback, ConnectionStateCallback, View.OnClickListener {

    private Context mContext;
    private static final String TAG = SpotifySongsPlayerActivity.class.getName();

    // TODO: Replace with your client ID
    private static final String CLIENT_ID = "d97e6af9d329405d997632c60fe79a16";

    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "http://vibin.in/callback/";
    private static final int REQUEST_CODE = 1337;
    private Player mPlayer;

    long lengthms;
    String AcessToken;
    Handler handler;
    Runnable my;
    SeekBar seekbar;
    boolean seekusedbyuser = false;
    boolean istouching = false;
    boolean killMe = false;
    String ImageURL, Title;

    Button addToPlayList;

    AddToPlaylistAdapter addToPlaylistAdapter;

    RecyclerView youtubePlayListRecyclerView;
    PlaylistRecyclerView adapter;
    ArrayList<Playlist> playlistList;

    String createnewplaylist = AppConstants.BASE_URL + "playlist/create_new_playlist/";
    String myPlaylist = AppConstants.BASE_URL + "playlist/my_playlists/";
    String addtrak = AppConstants.BASE_URL + "playlist/add_trak_to_playlist/";


    TextView textView;
    ProgressBar progressBar;
    ImageView songThumbnailIv;
    TextView songTitleTv;
    private ImageView fastForwardIv, fastRewindIv;
    private int songIndex = 0;
    private String songUri = "";
    private int userId;
    private String singleSongUri = "";
    ImageView heartIv;
    TextView playerCurrentDurationTv, playerTotalDurationTv;
    private String songID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_songs_player);

        statusBarColorChange();
        mContext = SpotifySongsPlayerActivity.this;
        Giphy.INSTANCE.configure(mContext, AppConstants.GIPHY_API_KEY, true);
        getIntentData();
        inItViews();
        final AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();


        inItClickListener();
        if (songList != null && songList.size() > 0)
            setViews(songIndex);
        else {
            this.songUri = singleSongUri;
            songTitleTv.setText(Title);
            songTitleTv.setSelected(true);
            songID = getIntent().getExtras().getString("id");
            // Logging.d("songID oncr:"+songID);
            Glide.with(SpotifySongsPlayerActivity.this).load(ImageURL).into(songThumbnailIv);
            fastForwardIv.setVisibility(View.GONE);
            fastRewindIv.setVisibility(View.GONE);
        }
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        boolean playing = true;
        final Button Play_Pause = (Button) findViewById(R.id.button3);
        Play_Pause.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (mPlayer.getPlaybackState().isPlaying) {
                    mPlayer.pause(null);
                    Play_Pause.setBackground(getDrawable(R.drawable.ic_baseline_play_arrow_24));
                } else {
                    mPlayer.resume(null);
                    Play_Pause.setBackground(getDrawable(R.drawable.ic_baseline_pause_24));
                }
            }
        });

        seekbar = (SeekBar) findViewById(R.id.seekBar);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                seekusedbyuser = true;

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                int progress = seekBar.getProgress();
                seekusedbyuser = false;

                seekusedbyuser = true;

                int to = (int) (lengthms * progress / 100);

                mPlayer.seekToPosition(null, to);
                mPlayer.resume(null);

            }
        });
        // seekbar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        //seekbar.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        addToPlayList = findViewById(R.id.addToPlayList);
        addToPlayList.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                if (mPlayer.getPlaybackState().isPlaying) {
                    Play_Pause.setBackground(getDrawable(R.drawable.ic_baseline_play_arrow_24));
                    dialog(String.valueOf(mPlayer.getMetadata().currentTrack.durationMs));
                    mPlayer.pause(null);
                }
            }
        });

        ImageView repeatone = findViewById(R.id.repeatoncee);
        //        repeatone.setOnClickListener(new View.OnClickListener() {
        //            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        //            @Override
        //            public void onClick(View v) {
        //                mPlayer.pause(null);
        //                Play_Pause.setBackground(getDrawable(R.drawable.ic_play_circle_filled_black_24dp));
        //                openDialog();
        //            }
        //        });

        findViewById(R.id.now_play_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

    private ArrayList<SpotifyArtistModel.Item> songList;


    private void getIntentData() {

        singleSongUri = getIntent().getStringExtra("uri");
        songList = (ArrayList<SpotifyArtistModel.Item>) getIntent().getSerializableExtra("SongsList");
        ImageURL = getIntent().getExtras().getString("image");
        Title = getIntent().getExtras().getString("title");
        AcessToken = getIntent().getExtras().getString("token");
        songIndex = getIntent().getExtras().getInt("songIndex");
        songID = getIntent().getExtras().getString("id");
        Logging.d("songID getIntentData:" + songID);
        Logging.d("songID singleSongUri:" + singleSongUri);
        userId = SharedPrefManager.getInstance(SpotifySongsPlayerActivity.this).getSharedPrefInt(AppConstants.INTENT_USER_ID);
    }

    private void inItViews() {
        progressBar = findViewById(R.id.progressbarPlayList);
        playerCurrentDurationTv = findViewById(R.id.playerCurrentTimeText);
        playerTotalDurationTv = findViewById(R.id.playerTotalTimeText);
        heartIv = findViewById(R.id.heart_iv);
        fastForwardIv = (ImageView) findViewById(R.id.fastforward);
        fastRewindIv = (ImageView) findViewById(R.id.fastrewind);
        songThumbnailIv = findViewById(R.id.imageRsplayer);
        songTitleTv = findViewById(R.id.textviewtitlespotify);
    }

    private void inItClickListener() {
        fastForwardIv.setOnClickListener(this);
        fastRewindIv.setOnClickListener(this);
        heartIv.setOnClickListener(this);
    }

    private void setViews(int songIndex) {

        if (songList != null && songList.size() > 0) {
            this.songIndex = songIndex;
            this.songUri = songList.get(songIndex).getUri();
            songTitleTv.setText(songList.get(songIndex).getName());
            Glide.with(SpotifySongsPlayerActivity.this).load(songList.get(songIndex).getImages().get(0).getUrl()).into(songThumbnailIv);
        }
    }

    private void openDialog() {
        final AlertDialog.Builder mb = new AlertDialog.Builder(SpotifySongsPlayerActivity.this);
        final View dialog = LayoutInflater.from(SpotifySongsPlayerActivity.this).inflate(R.layout.layout_dialog, null, false);

        final EditText playlistName = dialog.findViewById(R.id.dialog_playlistname);
        final SearchView playlistGif = dialog.findViewById(R.id.dialog_playlist_gif);
        final EditText playlistDesc = dialog.findViewById(R.id.dialog_playlist_desc);
        final EditText PlaylistPassword = dialog.findViewById(R.id.dialog_password);
        final GifView selectedGifIv = dialog.findViewById(R.id.selected_gif_iv);

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
                selectedGifIv.setMedia(media, RenditionType.preview, ContextCompat.getDrawable(getApplicationContext(), R.color.light_gray));
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
        PlaylistPassword.setVisibility(View.GONE);
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
                    PlaylistPassword.setVisibility(View.VISIBLE);
                    b = true;
                } else {
                    b = false;
                    PlaylistPassword.setText("");
                    PlaylistPassword.setVisibility(View.GONE);
                    publicprivate.setText("Public");
                }
                checking[0] = b;
            }


        });


        mb.setView(dialog).setTitle("Create New Playlist").setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String playlistname = playlistName.getText().toString();
                String password = PlaylistPassword.getText().toString();
                Logging.d("### Gif Url:" + playlistGif.getQuery());
                Logging.d("### Gif Url:" + playlistDesc.getText());

                String gifUrl = selectedGifLink[0];
                Logging.d("### selectedGifLink:" + gifUrl);
                if (checking[0])
                    if (TextUtils.isEmpty(playlistname)) {
                        Toast.makeText(SpotifySongsPlayerActivity.this, "please give playlist some name", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(gifUrl)) {
                        Toast.makeText(SpotifySongsPlayerActivity.this, "please enter a GIF link", Toast.LENGTH_SHORT).show();
                    } else if (!Utility.isWebUrl(gifUrl)) {
                        Toast.makeText(SpotifySongsPlayerActivity.this, "please enter a valid GIF link", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(playlistDesc.getText())) {
                        Toast.makeText(SpotifySongsPlayerActivity.this, "please enter playlist's description", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(password)) {
                        Toast.makeText(SpotifySongsPlayerActivity.this, "please enter playlist a password", Toast.LENGTH_SHORT).show();
                    } else {

                        addTexts(playlistname, gifUrl, playlistDesc.getText().toString(), password, checking);
                    }
                else {
                    if (TextUtils.isEmpty(playlistname)) {
                        Toast.makeText(SpotifySongsPlayerActivity.this, "please give playlist some name", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(gifUrl)) {
                        Toast.makeText(SpotifySongsPlayerActivity.this, "please enter a GIF link", Toast.LENGTH_SHORT).show();
                    } else if (!Utility.isWebUrl(gifUrl)) {
                        Toast.makeText(SpotifySongsPlayerActivity.this, "please enter a valid GIF link", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(playlistDesc.getText())) {
                        Toast.makeText(SpotifySongsPlayerActivity.this, "please enter playlist's description", Toast.LENGTH_SHORT).show();
                    } else {
                        addTexts(playlistname, gifUrl, playlistDesc.getText().toString(), "", checking);
                    }
                }
            }
        });


        mb.setView(dialog);
        final AlertDialog ass = mb.create();

        ass.show();
    }

    //  add text to server
    public void addTexts(final String playlistname, final String gifLink, final String description, final String password, final Boolean[] checking) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, createnewplaylist, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    Boolean PlaylistCreated = jsonObject.getBoolean("Playlist Created");
                    if (PlaylistCreated) {
                        Toast.makeText(SpotifySongsPlayerActivity.this, "playlist created", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SpotifySongsPlayerActivity.this, "you cannot create playlist of same name again", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SpotifySongsPlayerActivity.this, "this playlist already exists", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                SharedPrefManager sharedPrefManager = new SharedPrefManager(SpotifySongsPlayerActivity.this);
                //String token = sharedPrefManager.loadToken();
                String token = SharedPrefManager.getInstance(SpotifySongsPlayerActivity.this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
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
                String token = AppConstants.TOKEN + SharedPrefManager.getInstance(SpotifySongsPlayerActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
                params.put("Authorization", token);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(SpotifySongsPlayerActivity.this);
        requestQueue.add(stringRequest);
    }


    // dialog add to playlis
    private void dialog(String duration) {
        final AlertDialog.Builder mb = new AlertDialog.Builder(this);
        final View dialog = LayoutInflater.from(this).inflate(R.layout.dialog_add_to_playlist, null, false);

        playlistList = new ArrayList<>();
        progressBar = dialog.findViewById(R.id.progressbarPlayList);
        youtubePlayListRecyclerView = dialog.findViewById(R.id.playlists);
        textView = dialog.findViewById(R.id.textviewplaylistplayer);
        youtubePlayListRecyclerView.setHasFixedSize(true);
        youtubePlayListRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        parseData();
        addToPlaylistAdapter = new AddToPlaylistAdapter(this, playlistList);
        youtubePlayListRecyclerView.setAdapter(addToPlaylistAdapter);
        addToPlaylistAdapter.setCustomItemClickListener(new AddToPlaylistAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Playlist currentItem = playlistList.get(position);
                int id = currentItem.getId();
                AddThisToPlaylist(id, Title, ImageURL, duration);
            }
        });
        mb.setView(dialog).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("create new playlist", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Logging.d("TEST", "Create called2 ... ");
                openDialog();
            }
        });

        mb.setView(dialog);
        final AlertDialog ass = mb.create();

        ass.show();
    }

    // add song to server
    private void AddThisToPlaylist(final int id, final String title, final String thumbnail, String duration) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, addtrak, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean trackadded = jsonObject.getBoolean("Track added");

                    if (trackadded) {
                        sendSongAddedNotification(id);
                        Toast.makeText(SpotifySongsPlayerActivity.this, "track succesfully added", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(SpotifySongsPlayerActivity.this, "track already exists in the playlist", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SpotifySongsPlayerActivity.this, "track already exists in the playlist", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String token = SharedPrefManager.getInstance(SpotifySongsPlayerActivity.this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
                params.put("token", token);
                params.put("playlist", String.valueOf(id));
                params.put("type", "spotify");
                params.put("track_id", mPlayer.getMetadata().contextUri);
                params.put("name", title);
                params.put("image", thumbnail);
                String timeDuration = "T" + Utility.millisIntoHHMMSS(Long.valueOf(duration));
                params.put("song_duration", timeDuration);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String token = AppConstants.TOKEN + SharedPrefManager.getInstance(SpotifySongsPlayerActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
                params.put("Authorization", token);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    // parse the playlists to recycler view
    private void parseData() {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myPlaylist, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    playlistList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String name = jsonObject.getString("name");
                        int id = jsonObject.getInt("id");
                        playlistList.add(new Playlist(name, id));
                        addToPlaylistAdapter.notifyDataSetChanged();
                    }
                    progressBar.setVisibility(View.GONE);
                    if (playlistList.size() == 0) {
                        textView.setVisibility(View.VISIBLE);
                    } else {
                        textView.setVisibility(View.GONE);
                    }

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
                SharedPrefManager sharedPrefManager = new SharedPrefManager(SpotifySongsPlayerActivity.this);
                //String token = sharedPrefManager.loadToken();
                String token = SharedPrefManager.getInstance(SpotifySongsPlayerActivity.this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
                params.put("token", token);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String token = AppConstants.TOKEN + SharedPrefManager.getInstance(SpotifySongsPlayerActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
                params.put("Authorization", token);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    // spotify fucntions

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE) {
            final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            Logging.d("onInitialized token :" + new Gson().toJson(response));
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new com.spotify.sdk.android.player.SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(com.spotify.sdk.android.player.SpotifyPlayer spotifyPlayer) {

                        Logging.d("onInitialized :" + songList);
                        Logging.d("onInitialized singleSongUri :" + singleSongUri);

                        mPlayer = spotifyPlayer;
                        mPlayer.addConnectionStateCallback(SpotifySongsPlayerActivity.this);
                        mPlayer.addNotificationCallback(SpotifySongsPlayerActivity.this);
                        if (songList != null && songList.size() > 0) {
                            totalTimeNeedToDisplay = true;
                            mPlayer.playUri(null, songList.get(songIndex).getUri(), 0, 0);

                            String timeDuration = "T00:00:00";

                            try {
                                timeDuration = "T" + Utility.millisIntoHHMMSS(Long.valueOf(mPlayer.getMetadata().currentTrack.durationMs));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Logging.d("onInitialized 11:" + songList.get(songIndex).getId());


                            callAddSongLogAPI(userId, AppConstants.SPOTIFY, songList.get(songIndex).getName(), songList.get(songIndex).getId(), songList.get(songIndex).getUri(), songList.get(songIndex).getImages().get(0).getUrl(), songList.get(songIndex).getReleaseDate(), timeDuration);
                        } else if (!TextUtils.isEmpty(singleSongUri)) {
                            totalTimeNeedToDisplay = true;
                            mPlayer.playUri(null, singleSongUri, 0, 0);
                            String timeDuration = "T00:00:00";

                            try {
                                timeDuration = "T" + Utility.millisIntoHHMMSS(Long.valueOf(mPlayer.getMetadata().currentTrack.durationMs));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            String[] songUriArr = singleSongUri.split(":");

                            Logging.d("onInitialized else :" + songID + " id1:" + songUriArr[songUriArr.length - 1]);

                            Logging.d("onInitialized 22:" + songID);


                            callAddSongLogAPI(userId, AppConstants.SPOTIFY, Title, songID,

                                    singleSongUri, ImageURL, "", timeDuration);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }

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

    boolean totalTimeNeedToDisplay = true;

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        if (mContext != null) {


            if (mPlayer != null && mPlayer.getPlaybackState().isPlaying)
                lengthms = mPlayer.getMetadata().currentTrack.durationMs;

            if (totalTimeNeedToDisplay && mPlayer != null && mPlayer.getPlaybackState() != null && mPlayer.getMetadata().currentTrack != null && mPlayer.getMetadata().currentTrack.durationMs > 0) {
                totalTimeNeedToDisplay = false;
                playerTotalDurationTv.setText(Utility.convertDuration(Long.valueOf(mPlayer.getMetadata().currentTrack.durationMs)));
            }


            getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View arg0, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        // i have been touched
                        istouching = false;

                        return true;
                    }
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        // you can implement this

                        istouching = true;
                        return true;
                    }
                    return false;
                }
            });


            handler = new Handler();
            my = new Runnable() {
                @Override
                public void run() {
                    //Do something after 20 seconds
                    if (mContext != null) {


                        if (killMe)
                            return;

                        handler.postDelayed(this, 1000);

                        float wow = mPlayer.getPlaybackState().positionMs;
                        float wowInt = ((wow / lengthms) * 100);
                        if (seekusedbyuser == false || istouching == false) {
                            seekbar.setProgress((int) wowInt);
                            if (mPlayer.getPlaybackState().isPlaying && (int) wowInt > 0)
                                playerCurrentDurationTv.setText(Utility.convertDuration(Long.valueOf(mPlayer.getPlaybackState().positionMs)));
                        }

                    }
                }
            };
            handler.postDelayed(my, 2000);
        }
    }

    @Override
    public void onPlaybackError(Error error) {


    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (handler != null)
                handler.removeCallbacksAndMessages(my);
            killMe = true;

            if (mPlayer != null) {
                mPlayer.pause(null);
                mPlayer = null;
            }

            if (mMediaControllerCompat != null && mMediaControllerCompat.getMediaController(SpotifySongsPlayerActivity.this).getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
                mMediaControllerCompat.getMediaController(SpotifySongsPlayerActivity.this).getTransportControls().pause();
            }
            if (mMediaBrowserCompat != null) {
                mMediaBrowserCompat.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mContext = null;

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (handler != null) {
            handler.removeCallbacksAndMessages(my);
        }
        killMe = true;

        if (mPlayer != null) {
            mPlayer.pause(null);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (handler != null)
            handler.removeCallbacksAndMessages(my);
        killMe = true;

        mPlayer.pause(null);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.heart_iv:
                if (!isLiked)
                    if (songList != null && songList.size() > 0) {
                        Logging.d("songID multi:" + songList.get(songIndex).getId());
                        putSongLikeAPI(userId, songList.get(songIndex).getId(), "True");
                    } else {
                        Logging.d("songID single:" + songID);
                        putSongLikeAPI(userId, songID, "True");
                    }

                else {
                    if (songList != null && songList.size() > 0) {
                        putSongLikeAPI(userId, songList.get(songIndex).getId(), "False");
                    } else {
                        putSongLikeAPI(userId, songID, "false");
                    }
                }

                break;
            case R.id.fastforward:
                ++songIndex;
                if (songIndex > 0 && songIndex < songList.size()) {

                    if (mPlayer.getPlaybackState().isPlaying)
                        mPlayer.pause(null);

                    totalTimeNeedToDisplay = true;
                    mPlayer.playUri(null, songList.get(songIndex).getUri(), 0, 0);
                    setViews(songIndex);
                    String timeDuration = "T00:00:00";
                    try {
                        timeDuration = "T" + Utility.millisIntoHHMMSS(Long.valueOf(mPlayer.getMetadata().currentTrack.durationMs));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    callAddSongLogAPI(userId, AppConstants.SPOTIFY, songList.get(songIndex).getName(), songList.get(songIndex).getId(), songList.get(songIndex).getUri(), songList.get(songIndex).getImages().get(0).getUrl(), songList.get(songIndex).getReleaseDate(), timeDuration);
                }

                break;

            case R.id.fastrewind:

                if (mPlayer.getPlaybackState().positionMs <= 10 * 1000 || songIndex == 0) {
                    if (mPlayer.getPlaybackState().isPlaying)
                        mPlayer.pause(null);

                    totalTimeNeedToDisplay = true;
                    mPlayer.playUri(null, mPlayer.getMetadata().contextUri, 0, 0);

                    String timeDuration = "T00:00:00";
                    try {
                        timeDuration = "T" + Utility.millisIntoHHMMSS(Long.valueOf(mPlayer.getMetadata().currentTrack.durationMs));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    callAddSongLogAPI(userId, AppConstants.SPOTIFY, songList.get(songIndex).getName(), songList.get(songIndex).getId(), songList.get(songIndex).getUri(), songList.get(songIndex).getImages().get(0).getUrl(), songList.get(songIndex).getReleaseDate(), timeDuration);
                } else {

                    --songIndex;
                    if (songIndex > -1 && songIndex < songList.size()) {

                        if (mPlayer.getPlaybackState().isPlaying)
                            mPlayer.pause(null);

                        totalTimeNeedToDisplay = true;
                        mPlayer.playUri(null, songList.get(songIndex).getUri(), 0, 0);
                        setViews(songIndex);

                        String timeDuration = "T00:00:00";
                        try {
                            timeDuration = "T" + Utility.millisIntoHHMMSS(Long.valueOf(mPlayer.getMetadata().currentTrack.durationMs));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        callAddSongLogAPI(userId, AppConstants.SPOTIFY, songList.get(songIndex).getName(), songList.get(songIndex).getId(), songList.get(songIndex).getUri(), songList.get(songIndex).getImages().get(0).getUrl(), songList.get(songIndex).getReleaseDate(), timeDuration);
                    }
                }

                break;

            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    public void callAddSongLogAPI(int userId, String songType, String songName, String songId, String songURI, String songThumbnail, String detail, String duration) {

        callGetSongLikeStatusAPI(userId, songId);

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(SpotifySongsPlayerActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<AddSongLogModel> addLogCallback = dataAPI.addSongLogAPI(token, userId, songType, songName, songId, songURI, songThumbnail, detail, duration);
        addLogCallback.enqueue(new Callback<AddSongLogModel>() {
            @Override
            public void onResponse(Call<AddSongLogModel> call, retrofit2.Response<AddSongLogModel> response) {
                if (response != null && response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    Log.i("YoutubeVideo", "Log recorded successfully for id " + songId);
                }
            }

            @Override
            public void onFailure(Call<AddSongLogModel> call, Throwable t) {

            }
        });
    }

    private void sendSongAddedNotification(int playlistId) {

        DataAPI dataAPI = RetrofitAPI.getData();
        String headerToken = AppConstants.TOKEN + SharedPrefManager.getInstance(SpotifySongsPlayerActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        int userId = SharedPrefManager.getInstance(SpotifySongsPlayerActivity.this).getSharedPrefInt(AppConstants.INTENT_USER_ID);

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


    public void putSongLikeAPI(int userId, String songId, String likeStatus) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(SpotifySongsPlayerActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<AddSongLogModel> addLogCallback = dataAPI.putSongLikeStatus(token, userId, songId, likeStatus);
        addLogCallback.enqueue(new Callback<AddSongLogModel>() {
            @Override
            public void onResponse(Call<AddSongLogModel> call, retrofit2.Response<AddSongLogModel> response) {
                if (response != null && response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {

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


    boolean isLiked = false;

    public void callGetSongLikeStatusAPI(int userId, String songId) {

        isLiked = false;
        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(SpotifySongsPlayerActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

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

    private static final int STATE_PAUSED = 0;
    private static final int STATE_PLAYING = 1;

    private int mCurrentState;

    private MediaBrowserCompat mMediaBrowserCompat;
    private MediaControllerCompat mMediaControllerCompat;
    private static final String FM = "rtsp://flv.ccdntech.com/live/_definst_/vod256_Live/fm";

    private void initNotiMusic() {
        mMediaBrowserCompat = new MediaBrowserCompat(this, new ComponentName(this, MediaPlayerService.class), mMediaBrowserCompatConnectionCallback, getIntent().getExtras());

        mMediaBrowserCompat.connect();
    }

    private void toggleState() {
        if (mCurrentState == STATE_PAUSED) {
            //getSupportMediaController().getTransportControls().play();
            mMediaControllerCompat.getMediaController();
            mCurrentState = STATE_PLAYING;
        } else {
                    /*if( getSupportMediaController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING ) {
                        getSupportMediaController().getTransportControls().pause();
                    }*/
            if (mCurrentState == PlaybackStateCompat.STATE_PLAYING) {
                mMediaControllerCompat.getMediaController(SpotifySongsPlayerActivity.this).getTransportControls().pause();
            } else {
                Log.e("TEST", "play bt click-->"+songID);
                //mMediaControllerCompat.getMediaController(MainActivity.this).getTransportControls().play();
                try {
                    mMediaControllerCompat.getMediaController(SpotifySongsPlayerActivity.this)
                            .getTransportControls().playFromMediaId(songID, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //  mMediaControllerCompat.getMediaController(SpotifySongsPlayerActivity.this)
                //  .getTransportControls().playFromSearch(FM,null);
            }
            mCurrentState = STATE_PAUSED;
        }
    }

    private MediaBrowserCompat.ConnectionCallback mMediaBrowserCompatConnectionCallback = new MediaBrowserCompat.ConnectionCallback() {

        @Override
        public void onConnected() {
            super.onConnected();
            try {
                Log.e("TEST", "mMediaBrowserCompatConnectionCallback  onConnected()");
                mMediaControllerCompat = new MediaControllerCompat(mContext, mMediaBrowserCompat.getSessionToken());
                mMediaControllerCompat.registerCallback(mMediaControllerCompatCallback);
                //setSupportMediaController(mMediaControllerCompat);
                mMediaControllerCompat.setMediaController(SpotifySongsPlayerActivity.this, mMediaControllerCompat);
                //getSupportMediaController().getTransportControls().playFromMediaId(String.valueOf(R.raw.warner_tautz_off_broadway), null);
                //TODO: play media
                //mMediaControllerCompat.getMediaController(MainActivity.this).getTransportControls().playFromMediaId();

            } catch (RemoteException e) {
                Log.e("TEST", "browser connected error :" + e.toString());
            }
        }
    };

    private MediaControllerCompat.Callback mMediaControllerCompatCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onSessionReady() {
            Log.e("TEST", "onPlaybackStateChanged: onSessionReady ");
            super.onSessionReady();

        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            Log.e("TEST", "onPlaybackStateChanged ");
            super.onPlaybackStateChanged(state);
            if (state == null) {
                return;
            }

            switch (state.getState()) {
                case PlaybackStateCompat.STATE_PLAYING: {
                    Log.e("TEST", "onPlaybackStateChanged :" + STATE_PLAYING);
                    mCurrentState = STATE_PLAYING;
                    break;
                }
                case PlaybackStateCompat.STATE_PAUSED: {
                    Log.e("TEST", "onPlaybackStateChanged :" + STATE_PLAYING);
                    mCurrentState = STATE_PAUSED;
                    break;
                }
            }
        }
    };


}