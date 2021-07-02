package com.shorincity.vibin.music_sharing.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.like.LikeButton;
import com.shorincity.vibin.music_sharing.model.MyPlaylistModel;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.model.PlaylistLikeModel;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.UI.youtube;
import com.shorincity.vibin.music_sharing.adapters.UserPlaylistAdapter;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.shorincity.vibin.music_sharing.utils.Utility;
import com.shorincity.vibin.music_sharing.youtube_files.PlayYoutubeVideoActivity;
import com.shorincity.vibin.music_sharing.youtube_files.PlaylistDetailActivity;
import com.shorincity.vibin.music_sharing.youtube_files.Search;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

import static android.content.ContentValues.TAG;

public class PublicPlaylistFragment extends MyBaseFragment {

    RequestQueue requestQueue;
    String url = AppConstants.BASE_URL + "playlist/create_new_playlist/";
    String url1 = AppConstants.BASE_URL + "playlist/my_playlists/";
    View view;
    Context context;
    ProgressBar progressBar;
    TextView textView;
    UserPlaylistAdapter myPlaylistAdapter;
    ArrayList<MyPlaylistModel> myPlaylists;
    RecyclerView playlistRv;
    EditText edittext;

    ArrayList<PlaylistDetailModel> Songplaylist = new ArrayList<>();

    public PublicPlaylistFragment() {
    }

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            callMyPlaylistAPI(SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_TOKEN));
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_youtube_playlist, container, false);
            context = view.getContext();
            Giphy.INSTANCE.configure(getActivity(), AppConstants.GIPHY_API_KEY, true);
            edittext = view.findViewById(R.id.edittextSearch);
            edittext.setOnEditorActionListener(editorActionListener);
            progressBar = view.findViewById(R.id.progressbar);
            requestQueue = Volley.newRequestQueue(context);
            textView = view.findViewById(R.id.textviewplaylistfragment);

            FloatingActionButton floatingActionButton = view.findViewById(R.id.fab);
            FloatingActionButton search = view.findViewById(R.id.search);
            getActivity().registerReceiver(broadcastReceiver, new IntentFilter("DeletePlaylist"));
            // set Playlist Adapter
            myPlaylists = new ArrayList<>();
            playlistRv = (RecyclerView) view.findViewById(R.id.rv_playlist);
            /*playlistRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false));*/
            playlistRv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            playlistRv.setHasFixedSize(true);
            PublicPlaylistFragment publicPlaylistFragment = new PublicPlaylistFragment();
            myPlaylistAdapter = new UserPlaylistAdapter(getActivity(), myPlaylists, publicPlaylistFragment);
            myPlaylistAdapter.setCustomItemClickListener(new UserPlaylistAdapter.CustomItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    PlaylistDetailFragment fragment = PlaylistDetailFragment.getInstance(myPlaylists.get(position).getId(),
                            myPlaylists.get(position).getAdmin_id(), myPlaylists.get(position));
                    ((youtube) getActivity()).onLoadFragment(fragment);
                    /*Intent intent = new Intent(context, PlaylistDetailActivity.class);
                    int id = myPlaylists.get(position).getId();
                    intent.putExtra("id", id);
                    intent.putExtra("admin_id", myPlaylists.get(position).getAdmin_id());
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

            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openCreatePlaylistDialog();
                }
            });

            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SearchFragment fragment = SearchFragment.getInstance("");
                    ((youtube) getActivity()).onLoadFragment(fragment);
                   /* Intent intent = new Intent(context, Search.class);
                    startActivity(intent);*/
                }
            });

            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // dialog to add playlist
    private void openCreatePlaylistDialog() {
        final AlertDialog.Builder mb = new AlertDialog.Builder(getActivity());

        final View dialog = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog, null, false);
        final EditText playlistName = dialog.findViewById(R.id.dialog_playlistname);
        final SearchView playlistGif = dialog.findViewById(R.id.dialog_playlist_gif);
        final GifView selectedGifIv = dialog.findViewById(R.id.selected_gif_iv);
        final EditText playlistDesc = dialog.findViewById(R.id.dialog_playlist_desc);
        final EditText PlaylistPassword = dialog.findViewById(R.id.dialog_password);
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
                selectedGifIv.setMedia(media, RenditionType.preview, ContextCompat.getDrawable(getActivity(), R.color.light_gray));
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

        //PlaylistPassword.setVisibility(View.GONE);
        PlaylistPassword.setAlpha(0.5f);
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
                    PlaylistPassword.setEnabled(true);
                    PlaylistPassword.setAlpha(1.0f);
                    //PlaylistPassword.setVisibility(View.VISIBLE);
                    b = true;
                } else {
                    b = false;
                    PlaylistPassword.setText("");
                    PlaylistPassword.setEnabled(false);
                    PlaylistPassword.setAlpha(0.5f);
                    //PlaylistPassword.setVisibility(View.GONE);
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
                        if (checking[0]) {
                            if (TextUtils.isEmpty(playlistname)) {
                                Toast.makeText(context, "please give playlist some name", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(selectedGifLink[0])) {
                                Toast.makeText(context, "please choose a GIF", Toast.LENGTH_SHORT).show();
                            } else if (!Utility.isWebUrl(selectedGifLink[0])) {
                                Toast.makeText(context, "please choose a valid GIF", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(playlistDesc.getText())) {
                                Toast.makeText(context, "please enter playlist's description", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(password)) {
                                Toast.makeText(context, "please enter playlist a password", Toast.LENGTH_SHORT).show();
                            } else {
                                addTexts(playlistname, selectedGifLink[0], playlistDesc.getText().toString(), password, checking);
                            }
                        } else {
                            if (TextUtils.isEmpty(playlistname)) {
                                Toast.makeText(context, "please give playlist some name", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(selectedGifLink[0])) {
                                Toast.makeText(context, "please choose a GIF", Toast.LENGTH_SHORT).show();
                            } else if (!Utility.isWebUrl(selectedGifLink[0])) {
                                Toast.makeText(context, "please choose a valid GIF", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(playlistDesc.getText())) {
                                Toast.makeText(context, "please enter playlist's description", Toast.LENGTH_SHORT).show();
                            } else {
                                addTexts(playlistname, selectedGifLink[0], playlistDesc.getText().toString(), "", checking);
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
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    Boolean PlaylistCreated = jsonObject.getBoolean("Playlist Created");
                    if (PlaylistCreated) {
                        Logging.d("TEST", "addTexts PlaylistCreated Called");
                        Toast.makeText(context, "playlist created", Toast.LENGTH_SHORT).show();
                        callMyPlaylistAPI(SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_TOKEN));
                    }

                } catch (JSONException e) {
                    Logging.d("TEST", "addTexts onErrorResponse JSONException Called");
                    e.printStackTrace();
                    Toast.makeText(context, "you cannot create playlist of same name", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logging.d("TEST", "addTexts onErrorResponse else Called");
                Toast.makeText(context, "this playlist already exists", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String token = SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
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
                String token = AppConstants.TOKEN + SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
                params.put("Authorization", token);
                return params;
            }
        };
        requestQueue.add(stringRequest);
        callMyPlaylistAPI(SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_TOKEN));
    }

    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (actionId) {
                case EditorInfo.IME_ACTION_SEARCH: {
                    String text = edittext.getText().toString();
                    SearchFragment fragment = SearchFragment.getInstance(text);
                    ((youtube) getActivity()).onLoadFragment(fragment);
                    /*Intent intent = new Intent(context, Search.class);
                    intent.putExtra("search", text);
                    startActivity(intent);*/
                    break;
                }
            }
            return false;
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onStart() {
        super.onStart();

        if (getActivity() instanceof youtube) {
            ((youtube) getActivity()).mSlidingLayout.setSlidingEnable(false);
            ((youtube) getActivity()).mSlidingLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        callMyPlaylistAPI(SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_TOKEN));

        if (getActivity() instanceof youtube) {
            ((youtube) getActivity()).mSlidingLayout.closePane();
        }
    }

    public void callMyPlaylistAPI(String userToken) {
        textView.setVisibility(View.GONE);
        progressBar.setVisibility(view.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getData();
        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        Call<ArrayList<MyPlaylistModel>> callback = dataAPI.getMyPlaylist(token, userToken);
        callback.enqueue(new Callback<ArrayList<MyPlaylistModel>>() {
            @Override
            public void onResponse(Call<ArrayList<MyPlaylistModel>> call, retrofit2.Response<ArrayList<MyPlaylistModel>> response) {
                progressBar.setVisibility(View.GONE);
                myPlaylists.clear();
                if (response != null && response.body() != null && response.body().size() > 0) {
                    playlistRv.setVisibility(View.VISIBLE);
                    myPlaylists.addAll(response.body());
                    myPlaylistAdapter.notifyDataSetChanged();
                    Logging.d("TEST", "callMyPlaylistAPI Called");
                } else {
                    playlistRv.setVisibility(View.GONE);
                    Logging.d("TEST", "callMyPlaylistAPI Else Called");
                }

                if (myPlaylists.size() == 0) {
                    textView.setVisibility(View.VISIBLE);
                    Logging.d("TEST", "callMyPlaylistAPI Size 0");
                } else {
                    textView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<MyPlaylistModel>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Logging.d("TEST", "callMyPlaylistAPI onFailure Called");
            }
        });
    }

    private void putPublicPLaylistLike(View view, int playlistId, int position) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<PlaylistLikeModel> callback = dataAPI.putPlaylistLike(token,
                SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_TOKEN), playlistId);
        callback.enqueue(new Callback<PlaylistLikeModel>() {
            @Override
            public void onResponse(Call<PlaylistLikeModel> call, retrofit2.Response<PlaylistLikeModel> response) {
                if (response != null && response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    //ImageView likeBtn = ((ImageView) view.findViewById(R.id.like_btn));
                    LikeButton likeButton = ((LikeButton) view.findViewById(R.id.like_btn));
                    if (response.body().getLikeCount() > 0) {
                        Logging.d("TEST", "putPublicPLaylistLike onResponse Called");
                        //likeBtn.setColorFilter(ContextCompat.getColor(getActivity(), R.color.gph_white));
                        likeButton.setLiked(true);
                        myPlaylists.get(position).setLikes(myPlaylists.get(position).getLikes() + 1);
                        myPlaylistAdapter.notifyDataSetChanged();
                    } else {
                        Logging.d("TEST", "putPublicPLaylistLike onResponse else Called");
                        myPlaylists.get(position).setLikes(myPlaylists.get(position).getLikes() - 1);
                        likeButton.setLiked(false);
                        //likeBtn.setColorFilter(ContextCompat.getColor(getActivity(), R.color.light_gray));
                        myPlaylistAdapter.notifyDataSetChanged();
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<PlaylistLikeModel> call, Throwable t) {
                Logging.d("TEST", "putPublicPLaylistLike onFailure Called");
            }
        });
    }

    private void callPlaylistDetailAPI(String playlistId) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(context).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        Call<ArrayList<PlaylistDetailModel>> callback = dataAPI.getPublicPlaylistDetail(token, SharedPrefManager.getInstance(context).getSharedPrefString(AppConstants.INTENT_USER_TOKEN), playlistId);
        callback.enqueue(new Callback<ArrayList<PlaylistDetailModel>>() {
            @Override
            public void onResponse(Call<ArrayList<PlaylistDetailModel>> call, retrofit2.Response<ArrayList<PlaylistDetailModel>> response) {
                Songplaylist.clear();

                if (response != null && response.body() != null && response.body().size() > 0) {

                    Songplaylist.addAll(response.body());
                    Collections.reverse(Songplaylist);
                    int pos = 0;

                    try {
                        if (Songplaylist.get(pos).getType().equalsIgnoreCase(AppConstants.YOUTUBE)) {
                            Bundle bundle = new Bundle();
                            bundle.putInt("position", pos);
                            bundle.putString("title", Songplaylist.get(pos).getName());
                            bundle.putString("description", "");
                            bundle.putString("thumbnail", Songplaylist.get(pos).getImage());
                            bundle.putString("videoId", Songplaylist.get(pos).getTrackId());
                            bundle.putString("from", "channel");
                            bundle.putParcelableArrayList("playlist", (ArrayList<? extends Parcelable>) Songplaylist);
                            onMusicPlay(bundle);
                            /*Intent intent = new Intent(context, PlayYoutubeVideoActivity.class);
                            intent.putExtra("data", bundle);
                            startActivity(intent);*/
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<PlaylistDetailModel>> call, Throwable t) {

                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
}
