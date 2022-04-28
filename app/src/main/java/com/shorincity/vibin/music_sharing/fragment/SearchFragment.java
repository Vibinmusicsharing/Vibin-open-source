package com.shorincity.vibin.music_sharing.fragment;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.shorincity.vibin.music_sharing.UI.youtube;
import com.shorincity.vibin.music_sharing.adapters.RecyclerItemClickListener;
import com.shorincity.vibin.music_sharing.adapters.SearchAdapter;
import com.shorincity.vibin.music_sharing.adapters.UserPlaylistAdapter;
import com.shorincity.vibin.music_sharing.adapters.searchItem;
import com.shorincity.vibin.music_sharing.model.MyPlaylistModel;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.model.PlaylistLikeModel;
import com.shorincity.vibin.music_sharing.model.shareplaylist.PlaylistDetailResponse;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.youtube_files.PlayYoutubeVideoActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class SearchFragment extends MyBaseFragment {

    private View view;
    private Context mContext;
    private String key = "s";
    private String url = AppConstants.BASE_URL + "playlist/v1/search_playlist/";
    private String addCollab = AppConstants.BASE_URL + "playlist/v1/add_collab/";
    private Button searchButton;
    private ArrayList<searchItem> exampleItemList;
    private SearchAdapter exampleAdapter;
    private EditText searchText;
    private RecyclerView recyclerViewSearch;
    private ProgressBar progressBar;
    private TextView textView;
    private TextView textSearched;
    private static int x = 1;

    private UserPlaylistAdapter myPlaylistAdapter;
    private ArrayList<MyPlaylistModel> myPlaylists;
    private RecyclerView playlistRv;

    public static String BUNDLE_SEARCH = "search";
    ArrayList<PlaylistDetailModel> Songplaylist = new ArrayList<>();

    public static SearchFragment getInstance(String searchText) {
        SearchFragment fragment = new SearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_SEARCH, searchText);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_search2, container, false);
        mContext = view.getContext();
        return view;
    }

    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (actionId) {
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textSearched = view.findViewById(R.id.textviewSearched);
        progressBar = view.findViewById(R.id.progressbarSearch);
        searchText = view.findViewById(R.id.edittextSearch);
        searchText.setOnEditorActionListener(editorActionListener);
        textView = view.findViewById(R.id.textsearchplaylista);


        // set Playlist Adapter
        myPlaylists = new ArrayList<>();
        playlistRv = (RecyclerView) view.findViewById(R.id.rv_playlist);
        playlistRv.setLayoutManager(new GridLayoutManager(mContext.getApplicationContext(), 2));
        playlistRv.setHasFixedSize(true);
        myPlaylistAdapter = new UserPlaylistAdapter(mContext, myPlaylists);
        myPlaylistAdapter.setCustomItemClickListener(new UserPlaylistAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                PlaylistDetailFragmentNew fragment = PlaylistDetailFragmentNew.getInstance(
                        myPlaylists.get(position).getId(),
                        0, myPlaylists.get(position));
                ((youtube) getActivity()).onLoadFragment(fragment);

                /*Intent intent = new Intent(mContext, PlaylistDetailActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("adminname", myPlaylists.get(position).getAdminName());
                intent.putExtra(AppConstants.INTENT_PLAYLIST, myPlaylists.get(position));
                startActivity(intent);*/
            }

            @Override
            public void onLikeClick(View view, int position) {
                putPublicPLaylistLike(view, myPlaylists.get(position).getId(), position);
            }

            @Override
            public void onPlayClicked(View view, int position) {
                //Toast.makeText(getContext(), "Play", Toast.LENGTH_SHORT).show();
                int id = myPlaylists.get(position).getId();
                callPlaylistDetailAPI(String.valueOf(id));


            }
        });
        playlistRv.setAdapter(myPlaylistAdapter);


        String text = getArguments().getString(BUNDLE_SEARCH, "");

        if (!text.equals("")) {
            progressBar.setVisibility(View.VISIBLE);
            searchText.setText(text);
            key = text;
            parseData(key);
        }
        if (searchText.getText().toString().isEmpty()) {
            textSearched.setVisibility(View.VISIBLE);
        }

        searchButton = view.findViewById(R.id.buttonSearch);
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

        recyclerViewSearch = view.findViewById(R.id.recyclerView);
        recyclerViewSearch.setHasFixedSize(true);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));


        exampleAdapter = new SearchAdapter(mContext, exampleItemList);
        recyclerViewSearch.setAdapter(exampleAdapter);

        recyclerViewSearch.addOnItemTouchListener(
                new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        searchItem currentItem = exampleItemList.get(position);
                        String id = currentItem.getPlaylistID();
                        String publicprivate = currentItem.getPrivatepublic();
                        if (publicprivate.equals("true")) {
                            openDialog(id);
                        } else {
                            openDialoganother(id);
                        }
                    }
                })
        );
    }

    private void putPublicPLaylistLike(View view, int playlistId, int position) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<PlaylistLikeModel> callback = dataAPI.putPlaylistLike(token,
                SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_TOKEN), playlistId);
        callback.enqueue(new Callback<PlaylistLikeModel>() {
            @Override
            public void onResponse(Call<PlaylistLikeModel> call, retrofit2.Response<PlaylistLikeModel> response) {
                if (response != null && response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    ImageView likeBtn = ((ImageView) view.findViewById(R.id.like_btn));
                    if (response.body().getLikeCount() > 0) {

                        likeBtn.setColorFilter(ContextCompat.getColor(mContext, R.color.gph_white));

                        myPlaylists.get(position).setLikes(myPlaylists.get(position).getLikes() + 1);
                        myPlaylistAdapter.notifyDataSetChanged();
                    } else {
                        myPlaylists.get(position).setLikes(myPlaylists.get(position).getLikes() - 1);
                        likeBtn.setColorFilter(ContextCompat.getColor(mContext, R.color.light_gray));

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

    // parse data to recycler view
    private void parseData(final String key) {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getData();
        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        String userToken = SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);


        Call<ArrayList<MyPlaylistModel>> callback = dataAPI.getSearchedPlaylist(token, userToken, key);
        callback.enqueue(new Callback<ArrayList<MyPlaylistModel>>() {
            @Override
            public void onResponse(Call<ArrayList<MyPlaylistModel>> call, retrofit2.Response<ArrayList<MyPlaylistModel>> response) {
                progressBar.setVisibility(View.GONE);
                myPlaylists.clear();
                if (response != null && response.body() != null && response.body().size() > 0) {
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

    private void openDialoganother(final String id) {
        final AlertDialog.Builder mb = new AlertDialog.Builder(mContext);
        final View dialog = LayoutInflater.from(mContext).inflate(R.layout.dialogpublic, null, false);


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
        final AlertDialog.Builder mb = new AlertDialog.Builder(mContext);
        final View dialog = LayoutInflater.from(mContext).inflate(R.layout.dialogcollab, null, false);

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
                            Toast.makeText(mContext, "please enter the password", Toast.LENGTH_SHORT).show();
                        } else {
                            AddCollab(id, password);
                        }
                    }
                });


        mb.setView(dialog);
        final AlertDialog ass = mb.create();

        ass.show();
    }

    private void AddCollabPublic(final String id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, addCollab, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean collab = jsonObject.getBoolean("Collab Created");

                    if (collab) {
                        Toast.makeText(mContext, "Collaboration done succesfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "collaboration failed due to some reasons", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(mContext, "you cannot add collab to your playlist", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "something went wrong either you have entered collab to your playlist or there some internet connection error", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String token = SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
                params.put("token", token);
                params.put("playlist_id", id);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String token = AppConstants.TOKEN + SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
                params.put("Authorization", token);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    //  add collab to server
    private void AddCollab(final String id, final String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, addCollab, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean collab = jsonObject.getBoolean("Collab Created");

                    if (collab) {
                        Toast.makeText(mContext, "Collaboration done succesfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "Wrong Password", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(mContext, "you cannot add collab to your playlist", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "something went wrong either you have entered collab to your playlist or there some internet connection error", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String token = SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
                params.put("token", token);
                params.put("playlist_id", id);
                params.put("password", password);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String token = AppConstants.TOKEN + SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
                params.put("Authorization", token);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    private void callPlaylistDetailAPI(String playlistId) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        Call<PlaylistDetailResponse> callback = dataAPI.getPublicPlaylistDetail(token,
                SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_TOKEN),
                playlistId, AppConstants.SOURCE_TYPE_IN_APP);
        callback.enqueue(new Callback<PlaylistDetailResponse>() {
            @Override
            public void onResponse(Call<PlaylistDetailResponse> call, retrofit2.Response<PlaylistDetailResponse> response) {
                Songplaylist.clear();

                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {

                    Songplaylist.addAll(response.body().getTracks());
                    Collections.reverse(Songplaylist);
                    int pos = 0;

                    try {
                        if (Songplaylist.get(pos).getType().equalsIgnoreCase(AppConstants.YOUTUBE)) {
                            Intent intent = new Intent(mContext, PlayYoutubeVideoActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("position", pos);
                            bundle.putString("title", Songplaylist.get(pos).getName());
                            bundle.putString("description", "");
                            bundle.putString("thumbnail", Songplaylist.get(pos).getImage());
                            bundle.putString("videoId", Songplaylist.get(pos).getTrackId());
                            bundle.putString("from", "channel");
                            bundle.putParcelableArrayList("playlist", (ArrayList<? extends Parcelable>) Songplaylist);
                            intent.putExtra("data", bundle);
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<PlaylistDetailResponse> call, Throwable t) {

                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
}
