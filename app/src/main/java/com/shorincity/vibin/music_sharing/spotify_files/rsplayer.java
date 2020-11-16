// spotify player
package com.shorincity.vibin.music_sharing.spotify_files;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;

import com.shorincity.vibin.music_sharing.adapters.AddToPlaylistAdapter;
import com.shorincity.vibin.music_sharing.adapters.ExampleAdapter;
import com.shorincity.vibin.music_sharing.adapters.ExampleItem;
import com.shorincity.vibin.music_sharing.adapters.Playlist;
import com.shorincity.vibin.music_sharing.adapters.RecyclerItemClickListener;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.shorincity.vibin.music_sharing.utils.Utility;

import com.giphy.sdk.core.models.Media;
import com.giphy.sdk.core.models.enums.MediaType;
import com.giphy.sdk.core.models.enums.RatingType;
import com.giphy.sdk.core.models.enums.RenditionType;
import com.giphy.sdk.ui.GPHRequestType;
import com.giphy.sdk.ui.pagination.GPHContent;
import com.giphy.sdk.ui.views.GPHGridCallback;
import com.giphy.sdk.ui.views.GifView;
import com.giphy.sdk.ui.views.GiphyGridView;
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
import com.spotify.sdk.android.player.SpotifyPlayer;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

// Unused : commented By Swati
public class rsplayer extends AppCompatActivity implements NotificationCallback, ConnectionStateCallback {


    private Context mContext;
    // TODO: Replace with your client ID
    private static final String CLIENT_ID = "d97e6af9d329405d997632c60fe79a16";

    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "http://vibin.in/callback/";
    private static final int REQUEST_CODE = 1337;
    private Player mPlayer;

    ImageButton play;
    String key = "Muse";
    String url1 = "https://api.spotify.com/v1/search?q=";
    String url2 = "&type=track%2Cartist&market=US&limit=10&offset=5";

    RequestQueue requestQueue;
    Button searchButton;
    ArrayList<ExampleItem> exampleItemList;
    ExampleAdapter exampleAdapter;
    EditText searchText;
    RecyclerView recyclerViewSearch;
    long lengthms;
    String AcessToken;
    Handler handler;
    Runnable my;
    SeekBar seekbar;
    boolean seekusedbyuser = false;
    boolean istouching = false;
    boolean killMe = false;
    String uri;
    String ImageURL, Title;

    Button addToPlayList;
    ImageView heart_iv;

    AddToPlaylistAdapter addToPlaylistAdapter;

    RecyclerView youtubePlayListRecyclerView;
    ArrayList<Playlist> playlistList;

    String createnewplaylist = AppConstants.BASE_URL + "playlist/create_new_playlist/";
    String myPlaylist = AppConstants.BASE_URL + "playlist/my_playlists/";
    String addtrak = AppConstants.BASE_URL + "playlist/add_trak_to_playlist/";


    TextView textView;
    ProgressBar progressBar;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsplayer);
        mContext = rsplayer.this;

        Logging.d("rsplayer--- Here");
        Intent intent = getIntent();
        uri = intent.getStringExtra("uri");
        progressBar = findViewById(R.id.progressbarPlayList);
        final AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);


        ImageURL = getIntent().getExtras().getString("image");
        Title = getIntent().getExtras().getString("title");
        ImageView image = findViewById(R.id.imageRsplayer);
        heart_iv = findViewById(R.id.heart_iv);
        TextView heading = findViewById(R.id.textviewtitlespotify);
        heading.setText(Title);
        heading.setSelected(true);
        Glide.with(this).load(ImageURL).into(image);


        AcessToken = getIntent().getExtras().getString("token");

        boolean playing = true;
        final Button Play_Pause = (Button) findViewById(R.id.button3);
        Play_Pause.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (mPlayer.getPlaybackState().isPlaying) {
                    mPlayer.pause(null);
                    Play_Pause.setBackground(getDrawable(R.drawable.ic_play_circle_filled_black_24dp));
                } else {
                    mPlayer.resume(null);
                    Play_Pause.setBackground(getDrawable(R.drawable.ic_pause_circle_filled_black_24dp));
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
        seekbar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        seekbar.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        addToPlayList = findViewById(R.id.addToPlayList);
        addToPlayList.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                mPlayer.pause(null);
                Play_Pause.setBackground(getDrawable(R.drawable.ic_play_circle_filled_black_24dp));
                dialog();
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
    }


    private void openDialog() {
        //        Dialog dialog = new Dialog();
        //        dialog.show(getFragmentManager(),"dialog");
        final AlertDialog.Builder mb = new AlertDialog.Builder(rsplayer.this);
        final View dialog = LayoutInflater.from(rsplayer.this).inflate(R.layout.layout_dialog, null, false);

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

            }
        }).setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String playlistname = playlistName.getText().toString();
                String password = PlaylistPassword.getText().toString();
                if (checking[0])
                    if (TextUtils.isEmpty(playlistname)) {
                        Toast.makeText(rsplayer.this, "please give playlist some name", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(playlistGif.toString())) {
                        Toast.makeText(rsplayer.this, "please enter a GIF link", Toast.LENGTH_SHORT).show();
                    } else if (!Utility.isWebUrl(playlistGif.toString())) {
                        Toast.makeText(rsplayer.this, "please enter a valid GIF link", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(playlistDesc.getText())) {
                        Toast.makeText(rsplayer.this, "please enter playlist's description", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(password)) {
                        Toast.makeText(rsplayer.this, "please enter playlist a password", Toast.LENGTH_SHORT).show();
                    } else {

                        addTexts(playlistname, playlistGif.toString(), playlistDesc.getText().toString(), password, checking);
                    }
                else {
                    if (TextUtils.isEmpty(playlistname)) {
                        Toast.makeText(rsplayer.this, "please give playlist some name", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(playlistGif.toString())) {
                        Toast.makeText(rsplayer.this, "please enter a GIF link", Toast.LENGTH_SHORT).show();
                    } else if (!Utility.isWebUrl(playlistGif.toString())) {
                        Toast.makeText(rsplayer.this, "please enter a valid GIF link", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(playlistDesc.getText())) {
                        Toast.makeText(rsplayer.this, "please enter playlist's description", Toast.LENGTH_SHORT).show();
                    } else {
                        addTexts(playlistname, playlistGif.toString(), playlistDesc.getText().toString(), "", checking);
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
                        Toast.makeText(rsplayer.this, "playlist created", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(rsplayer.this, "you cannot create playlist of same name again", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(rsplayer.this, "this playlist already exists", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String token = SharedPrefManager.getInstance(rsplayer.this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
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
                String token = AppConstants.TOKEN + SharedPrefManager.getInstance(rsplayer.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
                params.put("Authorization", token);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(rsplayer.this);
        requestQueue.add(stringRequest);
    }

    // dialog add to playlis
    private void dialog() {
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


        youtubePlayListRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Playlist currentItem = playlistList.get(position);
                int id = currentItem.getId();
                AddThisToPlaylist(id, Title, ImageURL);
            }
        }));

        mb.setView(dialog).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("create new playlist", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openDialog();
            }
        });

        mb.setView(dialog);
        final AlertDialog ass = mb.create();

        ass.show();
    }

    // add song to server
    private void AddThisToPlaylist(final int id, final String title, final String thumbnail) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, addtrak, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean trackadded = jsonObject.getBoolean("Track added");

                    if (trackadded) {
                        sendSongAddedNotification(id);
                        Toast.makeText(rsplayer.this, "track succesfully added", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(rsplayer.this, "track already exists in the playlist", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(rsplayer.this, "track already exists in the playlist", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String token = SharedPrefManager.getInstance(rsplayer.this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
                params.put("token", token);
                params.put("playlist", String.valueOf(id));
                params.put("type", "spotify");
                params.put("track_id", uri);
                params.put("name", title);
                params.put("image", thumbnail);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                String token = AppConstants.TOKEN + SharedPrefManager.getInstance(rsplayer.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

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
                String token = SharedPrefManager.getInstance(rsplayer.this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
                params.put("token", token);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String token = AppConstants.TOKEN + SharedPrefManager.getInstance(rsplayer.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
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
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {

                        mPlayer = spotifyPlayer;
                        mPlayer.addConnectionStateCallback(rsplayer.this);
                        mPlayer.addNotificationCallback(rsplayer.this);
                        mPlayer.playUri(null, uri, 0, 0);

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


    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {


        if (mPlayer.getPlaybackState().isPlaying)
            lengthms = mPlayer.getMetadata().currentTrack.durationMs;

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

                if (killMe)
                    return;

                handler.postDelayed(this, 1000);

                float wow = mPlayer.getPlaybackState().positionMs;
                float wowInt = ((wow / lengthms) * 100);
                if (seekusedbyuser == false || istouching == false) {
                    seekbar.setProgress((int) wowInt);
                }

            }
        };
        handler.postDelayed(my, 2000);


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
        if (handler != null)
            handler.removeCallbacksAndMessages(my);
        killMe = true;

        mPlayer.pause(null);


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (handler != null)
            handler.removeCallbacksAndMessages(my);
        killMe = true;

        mPlayer.pause(null);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (handler != null)
            handler.removeCallbacksAndMessages(my);
        killMe = true;

        mPlayer.pause(null);

    }

    private void sendSongAddedNotification(int playlistId) {

        DataAPI dataAPI = RetrofitAPI.getData();
        String headerToken = AppConstants.TOKEN + SharedPrefManager.getInstance(rsplayer.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        int userId = SharedPrefManager.getInstance(rsplayer.this).getSharedPrefInt(AppConstants.INTENT_USER_ID);

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