package com.shorincity.vibin.music_sharing.youtube_files;

import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shorincity.vibin.music_sharing.model.MyPlaylistModel;
import com.shorincity.vibin.music_sharing.model.PlaylistLikeModel;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.adapters.RecyclerItemClickListener;
import com.shorincity.vibin.music_sharing.adapters.SearchAdapter;
import com.shorincity.vibin.music_sharing.adapters.UserPlaylistAdapter;
import com.shorincity.vibin.music_sharing.adapters.searchItem;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

// Playlist Search Screen
public class Search extends AppCompatActivity {
    String key="s";
    String url = AppConstants.BASE_URL+"playlist/search_playlist/";
    String addCollab = AppConstants.BASE_URL+"playlist/add_collab/";
    Button searchButton;
    ArrayList<searchItem> exampleItemList;
    SearchAdapter exampleAdapter;
    EditText searchText;
    RecyclerView recyclerViewSearch;
    ProgressBar progressBar;
    TextView textView;
    TextView textSearched;
    static  int x = 1;

    UserPlaylistAdapter myPlaylistAdapter;
    ArrayList<MyPlaylistModel> myPlaylists;
    RecyclerView playlistRv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search2);
        textSearched = findViewById(R.id.textviewSearched);
        progressBar = findViewById(R.id.progressbarSearch);
        searchText = findViewById(R.id.edittextSearch);
        searchText.setOnEditorActionListener(editorActionListener);
        textView = findViewById(R.id.textsearchplaylista);


        // set Playlist Adapter
        myPlaylists = new ArrayList<>();
        playlistRv = (RecyclerView) findViewById(R.id.rv_playlist);
        playlistRv.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        playlistRv.setHasFixedSize(true);
        Search search = new Search();
        myPlaylistAdapter = new UserPlaylistAdapter(Search.this, myPlaylists, search);
        myPlaylistAdapter.setCustomItemClickListener(new UserPlaylistAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(Search.this, PlaylistDetailActivity.class);
                int id = myPlaylists.get(position).getId();
                intent.putExtra("id", id);
                intent.putExtra("adminname", myPlaylists.get(position).getAdminName());
                intent.putExtra(AppConstants.INTENT_PLAYLIST, myPlaylists.get(position));
                startActivity(intent);
            }

            @Override
            public void onLikeClick(View view, int position) {
                putPublicPLaylistLike(view, myPlaylists.get(position).getId(), position);
            }
            @Override
            public void onPlayClicked(View view, int position) {
                //Toast.makeText(getApplicationContext(), "Play", Toast.LENGTH_SHORT).show();
            }
        });
        playlistRv.setAdapter(myPlaylistAdapter);



        String text = getIntent().getExtras().getString("search");

        if(!text.equals("")){
            progressBar.setVisibility(View.VISIBLE);
            searchText.setText(text);
            key = text;
            parseData(key);
        }
        if(searchText.getText().toString().isEmpty()){
          textSearched.setVisibility(View.VISIBLE);
        }

        searchButton = findViewById(R.id.buttonSearch);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewSearch.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                key = searchText.getText().toString();
                parseData(key);
            }
        });
        exampleItemList = new ArrayList<>();

        recyclerViewSearch = findViewById(R.id.recyclerView);
        recyclerViewSearch.setHasFixedSize(true);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));


        exampleAdapter = new SearchAdapter(this,exampleItemList);
        recyclerViewSearch.setAdapter(exampleAdapter);

        recyclerViewSearch.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        searchItem currentItem = exampleItemList.get(position);
                        String id = currentItem.getPlaylistID();
                        String publicprivate = currentItem.getPrivatepublic();
                        if(publicprivate.equals("true")) {
                            openDialog(id);
                        }else{
                            openDialoganother(id);
                        }
                    }
                })
        );
    }

    private void openDialoganother(final String id) {
        final AlertDialog.Builder mb = new AlertDialog.Builder(this);
        final View dialog = LayoutInflater.from(this).inflate(R.layout.dialogpublic, null, false);



        mb.setView(dialog)
                .setTitle("Add Collaboration")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddCollabPublic(id);
                    }
                });


        mb.setView(dialog);
        final AlertDialog ass = mb.create();

        ass.show();
    }

    //  add collaboration dialog
    private void openDialog(final String id) {
        final AlertDialog.Builder mb = new AlertDialog.Builder(this);
        final View dialog = LayoutInflater.from(this).inflate(R.layout.dialogcollab, null, false);

        final EditText PlaylistPassword = dialog.findViewById(R.id.password);



        mb.setView(dialog)
                .setTitle("Add Collaboration")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String password = PlaylistPassword.getText().toString();
                        if (TextUtils.isEmpty(password)) {
                            Toast.makeText(Search.this, "please enter the password", Toast.LENGTH_SHORT).show();
                        } else {
                            AddCollab(id, password);
                        }
                    }
                });


        mb.setView(dialog);
        final AlertDialog ass = mb.create();

        ass.show();
    }
//  add collab to server
    private void AddCollab(final String id,final String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, addCollab, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                  JSONObject jsonObject = new JSONObject(response);
                  Boolean collab = jsonObject.getBoolean("Collab Created");

                  if(collab){
                      Toast.makeText(Search.this, "Collaboration done succesfully", Toast.LENGTH_SHORT).show();
                  }else{
                      Toast.makeText(Search.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                  }

                } catch (JSONException e) {
                    Toast.makeText(Search.this, "you cannot add collab to your playlist" , Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Search.this, "something went wrong either you have entered collab to your playlist or there some internet connection error", Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                String token = SharedPrefManager.getInstance(Search.this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
                params.put("token",token);
                params.put("playlist_id",id);
                params.put("password", password);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                String token = AppConstants.TOKEN + SharedPrefManager.getInstance(Search.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
                params.put("Authorization", token);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void AddCollabPublic(final String id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, addCollab, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean collab = jsonObject.getBoolean("Collab Created");

                    if(collab){
                        Toast.makeText(Search.this, "Collaboration done succesfully", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(Search.this, "collaboration failed due to some reasons", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(Search.this, "you cannot add collab to your playlist" , Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Search.this, "something went wrong either you have entered collab to your playlist or there some internet connection error", Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                String token = SharedPrefManager.getInstance(Search.this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
                params.put("token",token);
                params.put("playlist_id",id);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                String token = AppConstants.TOKEN + SharedPrefManager.getInstance(Search.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
                params.put("Authorization", token);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
// parse data to recycler view
    private void parseData(final String key) {
     progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getData();
        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(Search.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        String userToken = SharedPrefManager.getInstance(Search.this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);


        Call<ArrayList<MyPlaylistModel>> callback = dataAPI.getSearchedPlaylist(token, userToken, key);
        callback.enqueue(new Callback<ArrayList<MyPlaylistModel>>() {
            @Override
            public void onResponse(Call<ArrayList<MyPlaylistModel>> call, retrofit2.Response<ArrayList<MyPlaylistModel>> response) {
                progressBar.setVisibility(View.GONE);
                myPlaylists.clear();
                if (response != null && response.body()!= null && response.body().size() > 0) {
                    playlistRv.setVisibility(View.VISIBLE);
                    myPlaylists.addAll(response.body());
                    myPlaylistAdapter.notifyDataSetChanged();
                } else {
                    playlistRv.setVisibility(View.GONE);
                }

                if (myPlaylists.size() == 0) {
                    textView.setVisibility(View.VISIBLE);
                } else {
                    textView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<MyPlaylistModel>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (actionId){
                case EditorInfo.IME_ACTION_SEARCH:
                    textSearched.setVisibility(View.GONE);
                    recyclerViewSearch.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    key = searchText.getText().toString();
                    parseData(key);
            }
            return false;
        }

    };

    private void putPublicPLaylistLike(View view, int playlistId, int position) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(Search.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<PlaylistLikeModel> callback = dataAPI.putPlaylistLike(token,
                SharedPrefManager.getInstance(Search.this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN), playlistId);
        callback.enqueue(new Callback<PlaylistLikeModel>() {
            @Override
            public void onResponse(Call<PlaylistLikeModel> call, retrofit2.Response<PlaylistLikeModel> response) {
                if (response != null && response.body()!= null && response.body().getStatus().equalsIgnoreCase("success")) {
                    ImageView likeBtn = ((ImageView)view.findViewById(R.id.like_btn));
                    if(response.body().getLikeCount() > 0) {

                        likeBtn.setColorFilter(ContextCompat.getColor(Search.this, R.color.gph_white));

                        myPlaylists.get(position).setLikes(myPlaylists.get(position).getLikes() + 1);
                        myPlaylistAdapter.notifyDataSetChanged();
                    } else {
                        myPlaylists.get(position).setLikes(myPlaylists.get(position).getLikes() - 1);
                        likeBtn.setColorFilter(ContextCompat.getColor(Search.this, R.color.light_gray));

                        myPlaylistAdapter.notifyDataSetChanged();
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<PlaylistLikeModel> call, Throwable t) {

            }
        });
    }
}

