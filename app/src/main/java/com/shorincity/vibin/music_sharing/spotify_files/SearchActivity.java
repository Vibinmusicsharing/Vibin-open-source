package com.shorincity.vibin.music_sharing.spotify_files;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.spotify;
import com.shorincity.vibin.music_sharing.adapters.ExampleAdapter;
import com.shorincity.vibin.music_sharing.adapters.ExampleItem;
import com.shorincity.vibin.music_sharing.adapters.RecyclerItemClickListener;
import com.shorincity.vibin.music_sharing.utils.Logging;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
// search activity_youtube_2 search songs
public class SearchActivity extends AppCompatActivity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback
{

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

    String url = "https://api.spotify.com/v1/search?q="+key+"&type=track&market=US&limit=10&offset=5";
    RequestQueue requestQueue;
    Button searchButton;
    ArrayList<ExampleItem> exampleItemList;
    ExampleAdapter exampleAdapter;
    EditText searchText;
    RecyclerView recyclerViewSearch;

    String AcessToken;
    ProgressBar progressBar;
    TextView textviewnoresults;
    TextView searchsomething;
    static int x=1;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        progressBar = findViewById(R.id.progressspotify);

        textviewnoresults = findViewById(R.id.noResults);
         textviewnoresults.setVisibility(View.GONE);
        final AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        AcessToken = getIntent().getExtras().getString("token");
        requestQueue = Volley.newRequestQueue(this);

        searchsomething = findViewById(R.id.searchid);
        searchsomething.setVisibility(View.GONE);
        searchText = findViewById(R.id.edittextSearch);
        searchText.setOnEditorActionListener(editorActionListener);
        searchButton = findViewById(R.id.buttonSearchspotify);
        progressBar.setVisibility(View.GONE);
        String text = getIntent().getExtras().getString("search");
        if(!text.equals("")){
            progressBar.setVisibility(View.VISIBLE);
            searchText.setText(text);
            key = text;
            url = "https://api.spotify.com/v1/search?q="+key+"&type=track&market=US&limit=10&offset=5";
            parseData();
        }
        if(searchText.getText().toString().isEmpty()){
            searchsomething.setVisibility(View.VISIBLE);
        }

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                recyclerViewSearch.setVisibility(View.GONE);
                key = searchText.getText().toString();
                url = "https://api.spotify.com/v1/search?q="+key+"&type=track&market=US&limit=10&offset=5";
                parseData();
            }
        });

        exampleItemList = new ArrayList<>();


        recyclerViewSearch = findViewById(R.id.recyclerView);
        recyclerViewSearch.setHasFixedSize(true);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));


        exampleAdapter = new ExampleAdapter(this,exampleItemList);
        recyclerViewSearch.setAdapter(exampleAdapter);

        recyclerViewSearch.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ExampleItem currentItem = exampleItemList.get(position);
                     Logging.d("onItemClick search:"+currentItem.getUri());
                        Intent a=new Intent(SearchActivity.this,SpotifySongsPlayerActivity.class);
                        a.putExtra("uri",currentItem.getUri());
                        a.putExtra("id",currentItem.getId());
                        a.putExtra("image",currentItem.getImageUrl());
                        a.putExtra("title",currentItem.getmTitle());
                        startActivity(a);
                    }
                })
        );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SearchActivity.this, spotify.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /// search songs and parse to the recycler view
    private void parseData() {
  textviewnoresults.setVisibility(View.GONE);
        requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject tracks = jsonObject.getJSONObject("tracks");
                    //Logging.dLong("onItemClick search:"+new Gson().toJson(tracks));
                    JSONArray items = tracks.getJSONArray("items");
                   // Logging.d("onItemClick search:"+new Gson().toJson(items));
                    exampleItemList.clear();
                    for(int i=0;i<items.length();i++) {
                        JSONObject jsonObject1 = items.getJSONObject(i);
                        String name = jsonObject1.getString("name");
                        String uri = jsonObject1.getString("uri");
                        String id = jsonObject1.getString("id");
                        String imageurl="";
                        JSONObject albums = jsonObject1.getJSONObject("album");
                        JSONArray image = albums.getJSONArray("images");

                        for(int j = 0;j<image.length();j++){
                            JSONObject jsonObject2 = image.getJSONObject(j);
                            if(j == 1){
                               String url_image = jsonObject2.getString("url");
                               imageurl = url_image;
                            }
                        }
                        exampleItemList.add(new ExampleItem(imageurl,name,id,uri));
                        exampleAdapter.notifyDataSetChanged();

                        runOnUiThread(new Runnable() {
                            public void run() {
                                recyclerViewSearch.scrollTo(0,0);
                                recyclerViewSearch.smoothScrollToPosition(0);


                                try {
                                    InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
                                }catch (Exception e) {

                                }
                            }
                        });
                    }
                    progressBar.setVisibility(View.GONE);
                    if(exampleItemList.size() == 0){
                       textviewnoresults.setVisibility(View.VISIBLE);
                    }else {
                        recyclerViewSearch.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Content-Type","application/json");
                params.put("Authorization","Bearer "+AcessToken);
                return params;
            }


            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

/*
    private void parseData1() {
        textviewnoresults.setVisibility(View.GONE);
        requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject tracks = jsonObject.getJSONObject("tracks");
                    JSONArray items = tracks.getJSONArray("items");
                    exampleItemList.clear();
                    for(int i=0;i<items.length();i++) {
                        JSONObject jsonObject1 = items.getJSONObject(i);
                        String name = jsonObject1.getString("name");
                        String uri = jsonObject1.getString("uri");
                        String imageurl="";
                        JSONObject albums = jsonObject1.getJSONObject("album");
                        JSONArray image = albums.getJSONArray("images");

                        for(int j = 0;j<image.length();j++){
                            JSONObject jsonObject2 = image.getJSONObject(j);
                            if(j == 1){
                                String url_image = jsonObject2.getString("url");
                                imageurl = url_image;
                            }
                        }

                        exampleItemList.add(new ExampleItem(imageurl,name,uri));
                        exampleAdapter.notifyDataSetChanged();

                        runOnUiThread(new Runnable() {
                            public void run() {
                                recyclerViewSearch.scrollTo(0,0);
                                recyclerViewSearch.smoothScrollToPosition(0);


                                try {
                                    InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
                                }catch (Exception e) {

                                }
                            }
                        });
                    }
                    progressBar.setVisibility(View.GONE);
                    if(exampleItemList.size() == 0){
                        textviewnoresults.setVisibility(View.VISIBLE);
                    }else {
                        recyclerViewSearch.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Content-Type","application/json");
                params.put("Authorization","Bearer "+AcessToken);
                return params;
            }


            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
*/


    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (actionId){
                case EditorInfo.IME_ACTION_SEARCH:
                    if(searchText.getText().toString().isEmpty()){
                        recyclerViewSearch.setVisibility(View.GONE);
                        searchsomething.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }else {
                        searchsomething.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        recyclerViewSearch.setVisibility(View.GONE);
                        key = searchText.getText().toString();
                        url = "https://api.spotify.com/v1/search?q=" + key + "&type=track&market=US&limit=10&offset=5";
                        parseData();
                    }
                    break;
            }
            return false;
        }

    };


    // spotify functions
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
                        mPlayer.addConnectionStateCallback(SearchActivity.this);
                        mPlayer.addNotificationCallback(SearchActivity.this);
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

    }

    @Override
    public void onPlaybackError(Error error) {

    }

}
