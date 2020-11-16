package com.shorincity.vibin.music_sharing.spotify_files;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.UI.spotify;
import com.shorincity.vibin.music_sharing.adapters.Playlist;
import com.shorincity.vibin.music_sharing.adapters.PlaylistRecyclerView;
import com.shorincity.vibin.music_sharing.adapters.RecyclerItemClickListener;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Utility;
import com.shorincity.vibin.music_sharing.youtube_files.Search;
import com.shorincity.vibin.music_sharing.youtube_files.PlaylistDetailActivity;
import com.giphy.sdk.core.models.Media;
import com.giphy.sdk.core.models.enums.MediaType;
import com.giphy.sdk.core.models.enums.RatingType;
import com.giphy.sdk.core.models.enums.RenditionType;
import com.giphy.sdk.ui.GPHRequestType;
import com.giphy.sdk.ui.pagination.GPHContent;
import com.giphy.sdk.ui.views.GPHGridCallback;
import com.giphy.sdk.ui.views.GifView;
import com.giphy.sdk.ui.views.GiphyGridView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
//  playlist in spotify fragment playlist

// Unused : commented by swati
public class SpotifyPlaylistFragment extends Fragment {

    RequestQueue requestQueue;
    String url = AppConstants.BASE_URL+"playlist/create_new_playlist/";
    String url1= AppConstants.BASE_URL+"playlist/my_playlists/";
    View view;
    Context context;
    RecyclerView spotifyPlayListRecyclerView;
    PlaylistRecyclerView adapter;
    ArrayList<Playlist> playlistList;
    TextView textView;
    ProgressBar progressBar;

    EditText editTextsearch;
    Button searchEditText;

    public SpotifyPlaylistFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_youtube_playlist, container, false);
        context = view.getContext();


        editTextsearch = view.findViewById(R.id.aniket);
        editTextsearch.setOnEditorActionListener(editorActionListener);
//        editTextsearch.setOnEditorActionListener(editorActionListener);
//        searchEditText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String text = editTextsearch.getText().toString();
//                Intent intent = new Intent(context, SpotifySearchActivity.class);
//                intent.putExtra("search",text);
//                startActivity(intent);
//            }
//        });





        progressBar = view.findViewById(R.id.progressbar);
        requestQueue = Volley.newRequestQueue(context);
        FloatingActionButton floatingActionButton = view.findViewById(R.id.fab);
        textView  = view.findViewById(R.id.textviewplaylistfragment);
        playlistList = new ArrayList<>();

        parseData();
        spotifyPlayListRecyclerView = view.findViewById(R.id.recyclerViewPlaylist);
        spotifyPlayListRecyclerView.setHasFixedSize(true);
        spotifyPlayListRecyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));

        adapter = new PlaylistRecyclerView(context,playlistList);
        spotifyPlayListRecyclerView.setAdapter(adapter);

        spotifyPlayListRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Playlist currentItem = playlistList.get(position);
                        Intent intent = new Intent(context, PlaylistDetailActivity.class);
                        int id = currentItem.getId();
                        intent.putExtra("id",id);
                        startActivity(intent);

                    }
                })
        );
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        FloatingActionButton search = view.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Search.class);
                startActivity(intent);
            }
        });
        return view;
    }

    // parse data to recycler view
    private void parseData() {
        textView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response){
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    playlistList.clear();
                    for(int i =0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String name = jsonObject.getString("name");
                        int id = jsonObject.getInt("id");
                        playlistList.add(new Playlist(name,id));
                        adapter.notifyDataSetChanged();
                    }

                    progressBar.setVisibility(View.GONE);
                    if(playlistList.size() == 0){
                        textView.setVisibility(View.VISIBLE);
                    }else{
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
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String token = SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
                params.put("token",token);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                String token = AppConstants.TOKEN + SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
                params.put("Authorization", token);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
    // open dialog to create playlist
    private void openDialog() {
//        Dialog dialog = new Dialog();
//        dialog.show(getFragmentManager(),"dialog");
        final AlertDialog.Builder mb = new AlertDialog.Builder(getActivity());
        final View dialog = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog, null, false);

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
                Log.i("GifURL","Position "+i);
            }

            @Override
            public void didSelectMedia(@NotNull Media media) {
                Log.i("GifURL","BitlyGifURL "+media.getBitlyGifUrl());
                Log.i("GifURL","BitlyURL "+media.getBitlyUrl());
                Log.i("GifURL","Content "+media.getContentUrl());
                Log.i("GifURL","EmbededUrl "+media.getEmbedUrl());
                Log.i("GifURL","SourceUrl "+media.getSourcePostUrl());

                selectedGifLink[0] = media.getEmbedUrl();
                selectedGifIv.setVisibility(View.VISIBLE);
                selectedGifIv.setMedia(media, RenditionType.preview, ContextCompat.getDrawable(getActivity().getApplicationContext(), R.color.light_gray));
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
                if(isChecked){
                    publicprivate.setText("Private");
                    PlaylistPassword.setVisibility(View.VISIBLE);
                    b = true;
                }else{
                    b = false;
                    PlaylistPassword.setText("");
                    PlaylistPassword.setVisibility(View.GONE);
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
                        if(checking[0])
                            if (TextUtils.isEmpty(playlistname)) {
                                Toast.makeText(context, "please give playlist some name", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(playlistGif.toString())) {
                                Toast.makeText(context, "please enter a GIF link", Toast.LENGTH_SHORT).show();
                            } else if (!Utility.isWebUrl(playlistGif.toString())) {
                                Toast.makeText(context, "please enter a valid GIF link", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(playlistDesc.getText())) {
                                Toast.makeText(context, "please enter playlist's description", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(password)) {
                                Toast.makeText(context, "please enter playlist a password", Toast.LENGTH_SHORT).show();
                            } else {

                                addTexts(playlistname, playlistGif.toString(),playlistDesc.getText().toString(), password,checking);
                            }
                        else{
                            if (TextUtils.isEmpty(playlistname)) {
                                Toast.makeText(context, "please give playlist some name", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(playlistGif.toString())) {
                                Toast.makeText(context, "please enter a GIF link", Toast.LENGTH_SHORT).show();
                            } else if (!Utility.isWebUrl(playlistGif.toString())) {
                                Toast.makeText(context, "please enter a valid GIF link", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(playlistDesc.getText())) {
                                Toast.makeText(context, "please enter playlist's description", Toast.LENGTH_SHORT).show();
                            } else{
                                addTexts(playlistname, playlistGif.toString(),playlistDesc.getText().toString(),"",checking);
                            }
                        }
                    }
                });



        mb.setView(dialog);
        final AlertDialog ass = mb.create();

        ass.show();
    }

    //  add text to server
    public void addTexts(final String playlistname,  final String gifLink, final String description, final String password,final Boolean[] checking) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    Boolean PlaylistCreated = jsonObject.getBoolean("Playlist Created");
                    if(PlaylistCreated){
                        Toast.makeText(context, "playlist created", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "you cannot create playlist of same name again", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "this playlist already exists", Toast.LENGTH_SHORT).show();
            }
        } ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String token = SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);

                params.put("token",token);
                params.put("name", playlistname);
                params.put("description", description);
                params.put("gif_link", gifLink);
                if(checking[0]){
                    params.put("private","true");
                    params.put("password", password);
                }else{
                    params.put("password","");
                    params.put("private","false");
                }

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                String token = AppConstants.TOKEN + SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
                params.put("Authorization", token);
                return params;
            }
        };

        requestQueue.add(stringRequest);
        parseData();
    }
    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (actionId){
                case EditorInfo.IME_ACTION_SEARCH:
                    String text = editTextsearch.getText().toString();
                    Intent intent = new Intent(context,Search.class);
                    intent.putExtra("search",text);
                    startActivity(intent);
            }
            return false;
        }

    };

    @Override
    public void onStart() {
        super.onStart();
        ((spotify)getActivity()).mSlidingLayout.setSlidingEnable(false);
        ((spotify)getActivity()).mSlidingLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

}
