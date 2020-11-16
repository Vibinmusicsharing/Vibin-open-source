package com.shorincity.vibin.music_sharing.spotify_files;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.adapters.ExampleAdapter;
import com.shorincity.vibin.music_sharing.adapters.ExampleItem;
import com.shorincity.vibin.music_sharing.adapters.RecyclerItemClickListener;
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

// Unused : commented By Swati
public class SearchActivity2 extends AppCompatActivity implements
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
    String uri,title,image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        uri = getIntent().getExtras().getString("uri");
        title = getIntent().getExtras().getString("name");
        image = getIntent().getExtras().getString("image");


        final AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
//        builder.setScopes(new String[]{"user-read-private", "streaming"});
//        AuthenticationRequest request = builder.build();

//        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);


        AcessToken = getIntent().getExtras().getString("token");
        requestQueue = Volley.newRequestQueue(this);

        Intent intent1 = new Intent(SearchActivity2.this,rsplayer.class);
        intent1.putExtra("uri",uri);
        intent1.putExtra("title",title);
        intent1.putExtra("image",image);

        startActivity(intent1);

        searchText = findViewById(R.id.edittextSearch);
        searchButton = findViewById(R.id.buttonSearch);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                        Intent a=new Intent(SearchActivity2.this,rsplayer.class);
                        a.putExtra("uri",currentItem.getUri());
                        a.putExtra("image",currentItem.getImageUrl());
                        a.putExtra("title",currentItem.getmTitle());
                        startActivity(a);
                    }
                })
        );
    }

    private void parseData() {

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
                        mPlayer.addConnectionStateCallback(SearchActivity2.this);
                        mPlayer.addNotificationCallback(SearchActivity2.this);
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
