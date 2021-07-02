package com.shorincity.vibin.music_sharing.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.like.LikeButton;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.UI.custom.RoundedImageView;
import com.shorincity.vibin.music_sharing.adapters.AddToPlaylistAdapter;
import com.shorincity.vibin.music_sharing.adapters.MyPlaylistAdapter;
import com.shorincity.vibin.music_sharing.adapters.Playlist;
import com.shorincity.vibin.music_sharing.adapters.RecentPlayedAdapter;
import com.shorincity.vibin.music_sharing.model.APIResponse;
import com.shorincity.vibin.music_sharing.model.MyPlaylistModel;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.model.RecentSongModel;
import com.shorincity.vibin.music_sharing.model.UpdateLikeStatusModel;
import com.shorincity.vibin.music_sharing.model.UserProfileModel;
import com.shorincity.vibin.music_sharing.ripples.RippleButton;
import com.shorincity.vibin.music_sharing.ripples.listener.OnRippleCompleteListener;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.shorincity.vibin.music_sharing.utils.Utility;
import com.shorincity.vibin.music_sharing.youtube_files.PlayYoutubeVideoActivity;
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
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Whenever an user tried to search and view the profile of other user, this class instance will use.
public class OtherUserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private static final String LIKE = "Like", UNLIKE = "Unlike";
    int userId, searchedUserId, playlistId;
    String userName, fullName, userToken;
    ArrayList<RecentSongModel> likedSongList;
    ArrayList<MyPlaylistModel> myPlaylists;
    private AppCompatImageView like_img;
    RoundedImageView userDpIv;
    TextView likeCountTv, collaboratorCountTv, usernameTv, fullNameTv;
    RecyclerView likedSingRv, playlistRv;
    RecentPlayedAdapter likedSongAdapter;
    MyPlaylistAdapter myPlaylistAdapter;
    private CircularProgressButton likeBtn;
    private RelativeLayout likeRl;
    String createnewplaylist = AppConstants.BASE_URL + "playlist/create_new_playlist/";
    private RippleButton collaborateBtn;
    LikeButton like_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);
        mContext = OtherUserProfileActivity.this;

        userId = SharedPrefManager.getInstance(OtherUserProfileActivity.this).getSharedPrefInt(AppConstants.INTENT_USER_ID);
        userToken = SharedPrefManager.getInstance(OtherUserProfileActivity.this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);

        // Basic details passing through intent to fetch and display more detail about user
        searchedUserId = getIntent().getIntExtra(AppConstants.INTENT_SEARCHED_USER_ID, 0);
        playlistId = getIntent().getIntExtra(AppConstants.INTENT_PLAYLIST_ID, 0);
        userName = getIntent().getStringExtra(AppConstants.INTENT_SEARCHED_USER_NAME);
        fullName = getIntent().getStringExtra(AppConstants.INTENT_SEARCHED_FULL_NAME);

        // Calling to find Activity's child view here
        inItViews();

        // setting all the required listeners in this method
        inItListener();

        // setting all the adapters inside the method
        setListAdapter();

        // calling to get USer Profile info here like Likes count, Collab's count etc
        callGetUserProfileAPI(userId, searchedUserId);

        // getting all the public playlist list created by user
        callUserPlaylistAPI(searchedUserId);

        // getting their liked song list
        callGetUserLikedSongAPI(searchedUserId);
    }

    private void setListAdapter() {
        likedSongList = new ArrayList<>();

        likedSingRv.setLayoutManager(new LinearLayoutManager(OtherUserProfileActivity.this, LinearLayoutManager.VERTICAL, false));
        likedSingRv.setHasFixedSize(true);

        likedSongAdapter = new RecentPlayedAdapter(OtherUserProfileActivity.this, likedSongList);
        likedSongAdapter.setCustomItemClickListener(new RecentPlayedAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                try {
                    if (likedSongList.get(position).getSongType().equalsIgnoreCase(AppConstants.YOUTUBE)) {
                        Intent intent = new Intent(OtherUserProfileActivity.this, PlayYoutubeVideoActivity.class);
                        ArrayList<PlaylistDetailModel> playlist;
                        playlist = new ArrayList<>();
                        for (int i = 0; i < likedSongList.size(); i++) {
                            playlist.add(new PlaylistDetailModel(
                                    likedSongList.get(i).getSongName(),
                                    likedSongList.get(i).getSongThumbnail(),
                                    likedSongList.get(i).getSongId()
                            ));
                        }
                        Bundle bundle = new Bundle();
                        bundle.putInt("position", position);
                        bundle.putString("title", likedSongList.get(position).getSongName());
                        bundle.putString("description", "");
                        bundle.putString("thumbnail", likedSongList.get(position).getSongThumbnail());
                        bundle.putString("videoId", likedSongList.get(position).getSongId());
                        bundle.putString("from","channel");
                        bundle.putParcelableArrayList("playlist", (ArrayList<? extends Parcelable>) playlist);
                        intent.putExtra("data", bundle);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        likedSingRv.setAdapter(likedSongAdapter);

        // set Playlist Adapter
        myPlaylists = new ArrayList<>();

        playlistRv.setLayoutManager(new LinearLayoutManager(OtherUserProfileActivity.this, LinearLayoutManager.HORIZONTAL, false));
        playlistRv.setHasFixedSize(true);

        myPlaylistAdapter = new MyPlaylistAdapter(OtherUserProfileActivity.this, myPlaylists);
        myPlaylistAdapter.setCustomItemClickListener(new MyPlaylistAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                Intent intent = new Intent(OtherUserProfileActivity.this, PlaylistDetailActivity.class);
                int id = myPlaylists.get(position).getId();
                intent.putExtra("id", id);
                intent.putExtra("adminname", myPlaylists.get(position).getAdminName());
                intent.putExtra(AppConstants.INTENT_PLAYLIST, myPlaylists.get(position));
                startActivity(intent);
            }
        });
        playlistRv.setAdapter(myPlaylistAdapter);

    }

    private void inItViews() {
        like_btn = findViewById(R.id.like);
        like_img = findViewById(R.id.like_img);
        userDpIv = findViewById(R.id.user_dp_iv);
        usernameTv = (TextView) findViewById(R.id.tv_username);
        fullNameTv = (TextView) findViewById(R.id.tv_fullname);
        likeCountTv = (TextView) findViewById(R.id.tv_like_count);
        collaboratorCountTv = (TextView) findViewById(R.id.tv_collaborator_count);
        playlistRv = (RecyclerView) findViewById(R.id.rv_playlist);
        likedSingRv = (RecyclerView) findViewById(R.id.rv_fav);
        likeBtn = findViewById(R.id.like_btn);
        collaborateBtn = findViewById(R.id.collaborate_btn);
        likeRl = (RelativeLayout) findViewById(R.id.like_rl);

        usernameTv.setText(userName);
        fullNameTv.setText(fullName);

        collaborateBtn.setOnRippleCompleteListener(new OnRippleCompleteListener() {
            @Override
            public void onComplete(View v) {
                onClick(v);
            }
        });

    }

    private void inItListener() {
        ((AppBarLayout) findViewById(R.id.user_profile_appbar)).addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                /**
                 * Collapsed
                 */
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    //userDpIv.animate().scaleX((float)0.4).setDuration(3000);
                    //userDpIv.animate().scaleY((float)0.4).setDuration(3000);
                    userDpIv.animate().alpha(1).setDuration(0);

                    /**
                     * Expanded
                     */
                } else if (verticalOffset == 0) {
                    userDpIv.animate().scaleX((float) 1).setDuration(100);
                    userDpIv.animate().scaleY((float) 1).setDuration(100);
                    userDpIv.animate().alpha(1).setDuration(0);
                    /**
                     * Somewhere in between
                     */
                } else {
                    final int scrollRange = appBarLayout.getTotalScrollRange();
                    float offsetFactor = (float) (-verticalOffset) / (float) scrollRange;
                    //float scaleFactor = 1F - offsetFactor * .5F;
                    float scaleFactor = 1F - offsetFactor * .3F;
                    userDpIv.animate().scaleX(scaleFactor);
                    userDpIv.animate().scaleY(scaleFactor);
                }
            }
        });

       // likeBtn.setOnClickListener(this);
        likeRl.setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void callGetUserLikedSongAPI(int userId) {

        DataAPI dataAPI = RetrofitAPI.getData();
        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(OtherUserProfileActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);


        Call<ArrayList<RecentSongModel>> callback = dataAPI.getLikedSongs(token, userId);
        callback.enqueue(new Callback<ArrayList<RecentSongModel>>() {
            @Override
            public void onResponse(Call<ArrayList<RecentSongModel>> call, Response<ArrayList<RecentSongModel>> response) {
                likedSongList.clear();
                if (response != null && response.body() != null && response.body().size() > 0) {
                    findViewById(R.id.tv_recent_played_placeholder).setVisibility(View.GONE);
                    likedSingRv.setVisibility(View.VISIBLE);
                    likedSongList.addAll(response.body());
                    likedSongAdapter.notifyDataSetChanged();
                } else {
                    findViewById(R.id.tv_recent_played_placeholder).setVisibility(View.VISIBLE);
                    likedSingRv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<RecentSongModel>> call, Throwable t) {

            }
        });

    }

    private void callUserPlaylistAPI(int searchedUserId) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(OtherUserProfileActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<ArrayList<MyPlaylistModel>> callback = dataAPI.getPublicPlaylist(token, searchedUserId);
        callback.enqueue(new Callback<ArrayList<MyPlaylistModel>>() {
            @Override
            public void onResponse(Call<ArrayList<MyPlaylistModel>> call, Response<ArrayList<MyPlaylistModel>> response) {
                //((ProgressBar) view.findViewById(R.id.progressbar)).setVisibility(View.GONE);
                myPlaylists.clear();
                if (response != null && response.body() != null && response.body().size() > 0) {
                    findViewById(R.id.tv_playlist_placeholder).setVisibility(View.GONE);
                    playlistRv.setVisibility(View.VISIBLE);
                    myPlaylists.addAll(response.body());
                    myPlaylistAdapter.notifyDataSetChanged();
                } else {
                    findViewById(R.id.tv_playlist_placeholder).setVisibility(View.VISIBLE);
                    playlistRv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<MyPlaylistModel>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.like_rl:
                Logging.d("like_rl clicked");
                String likeStatus = !likeRl.isSelected() ? "True" : "False";//likeBtn.getText().toString().equalsIgnoreCase(LIKE)?"True": "False";
                callUpdateLikeStatusAPI(userId, searchedUserId, likeStatus);
                sendLikeNotification(userId, searchedUserId, likeStatus);

                break;
            case R.id.like_btn:
                /*String likeStatus = likeBtn.getText().toString().equalsIgnoreCase(LIKE)?"True": "False";
                callUpdateLikeStatusAPI(userId,searchedUserId,likeStatus);*/
                break;
            case R.id.collaborate_btn:
Logging.d("TEST","collaborate_btn Clicked");
                if (playlistId > 0)
                    sendCollabRequestNotification(playlistId, searchedUserId);
                    //callAddCollaborateAPI(playlistId,searchedUserId);
                else
                    Collabdialog();
                break;
        }
    }

    public void callUpdateLikeStatusAPI(int userId, int searchedUserId, String likeStatus) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(OtherUserProfileActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        //likeBtn.startAnimation();
        Call<UpdateLikeStatusModel> addLogCallback = dataAPI.updateLikeUserProfile(token, userId, searchedUserId, likeStatus);
        addLogCallback.enqueue(new Callback<UpdateLikeStatusModel>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(Call<UpdateLikeStatusModel> call, retrofit2.Response<UpdateLikeStatusModel> response) {

                if (response.body().getButtonStatus().equalsIgnoreCase("liked")) {
                    Logging.d("liked --->");
                    likeBtn.setText(getResources().getString(R.string.like));
                    likeRl.setSelected(true);
                    like_img.setBackgroundResource(R.drawable.heart_on_icon);
                    like_btn.setLiked(true);

                } else {
                    likeRl.setSelected(false);
                    likeBtn.setText(getResources().getString(R.string.unlike));
                     like_img.setBackgroundResource(R.drawable.heart_off_icon);
                    like_btn.setLiked(false);
                }

                likeCountTv.setText(Utility.prettyCount(response.body().getHowManyLikesUser()));

            }

            @Override
            public void onFailure(Call<UpdateLikeStatusModel> call, Throwable t) {
                likeBtn.revertAnimation();
            }
        });
    }


    private void sendLikeNotification(int userId, int searchedUserId, String likeStatus) {

        if (likeStatus.equalsIgnoreCase("True")) {

            DataAPI dataAPI = RetrofitAPI.getData();
            String headerToken = AppConstants.TOKEN + SharedPrefManager.getInstance(OtherUserProfileActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

            Call<APIResponse> callback = dataAPI.sendNotification(headerToken, userId, searchedUserId, 0, AppConstants.PROFILE_LIKE);
            callback.enqueue(new Callback<APIResponse>() {

                @SuppressLint("NewApi")
                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                    //progressBar.setVisibility(View.GONE);
                    if (response != null && response.body() != null) {
                        Logging.d("like success--->"+new Gson().toJson(response.body()));
                        if (response.body().getStatus().equalsIgnoreCase("success")) {
                             like_img.setBackgroundResource(R.drawable.heart_on_icon);
                            likeRl.setSelected(true);
                            like_btn.setLiked(true);
                        } else {
                             like_img.setBackgroundResource(R.drawable.heart_off_icon);
                            likeRl.setSelected(false);
                            like_btn.setLiked(false);
                        }
                    } else {
                    }
                }

                @SuppressLint("NewApi")
                @Override
                public void onFailure(Call<APIResponse> call, Throwable t) {
                    Log.i("Error: ", "ADD NOTIFICATION " + t.getMessage());
                    Logging.d("unlike onFailure--->");

                     like_img.setBackgroundResource(R.drawable.heart_off_icon);
                    like_btn.setLiked(false);
                }
            });
        }

    }


    public void callGetUserProfileAPI(int userId, int searchedUserId) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(OtherUserProfileActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<UserProfileModel> addLogCallback = dataAPI.getUserProfile(token, userId, searchedUserId);
        addLogCallback.enqueue(new Callback<UserProfileModel>() {
            @Override
            public void onResponse(Call<UserProfileModel> call, retrofit2.Response<UserProfileModel> response) {
                if (response != null && response.body() != null) {

                    if (response.body().getButtonStatus().equalsIgnoreCase("liked")) {
                        likeBtn.setText(UNLIKE);
                        likeRl.setSelected(true);
                        like_img.setBackgroundResource(R.drawable.heart_on_icon);
                        like_btn.setLiked(true);
                    } else {
                        likeBtn.setText(LIKE);
                        likeRl.setSelected(false);
                        like_img.setBackgroundResource(R.drawable.heart_off_icon);
                        like_btn.setLiked(false);
                    }


                    likeCountTv.setText(Utility.prettyCount(response.body().getHowManyLikesUser()));
                    collaboratorCountTv.setText(Utility.prettyCount(response.body().getCollaboratorCount()));
                    String avatarUrl = response.body().getAvatarLink();

                    if (avatarUrl != null) {
                        try {
                            Glide.with(mContext).load(avatarUrl).into(userDpIv);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfileModel> call, Throwable t) {

            }
        });
    }

    ArrayList<Playlist> collabPlaylist;
    AddToPlaylistAdapter addToPlaylistAdapter;
    TextView collabTextVieww;
    ProgressBar collabProgressBar;

    AlertDialog ass;

    // add video to playlist
    private void Collabdialog() {
        final AlertDialog.Builder mb = new AlertDialog.Builder(this);
        final View dialog = LayoutInflater.from(this).inflate(R.layout.dialog_add_to_playlist, null, false);

        collabPlaylist = new ArrayList<>();
        RecyclerView youtubePlayListRecyclerView = dialog.findViewById(R.id.playlists);
        collabTextVieww = dialog.findViewById(R.id.textviewplaylistplayer);
        collabProgressBar = dialog.findViewById(R.id.progressbarPlayList);
        collabProgressBar.setVisibility(View.VISIBLE);
        youtubePlayListRecyclerView.setHasFixedSize(true);
        youtubePlayListRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // parse public playlist to collaborate with
        parsePublicPlaylist();
        addToPlaylistAdapter = new AddToPlaylistAdapter(this, collabPlaylist);
        addToPlaylistAdapter.setCustomItemClickListener(new AddToPlaylistAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                ass.dismiss();
                //callAddCollaborateAPI(collabPlaylist.get(position).getId(),searchedUserId);
                sendCollabRequestNotification(collabPlaylist.get(position).getId(), searchedUserId);
            }
        });
        youtubePlayListRecyclerView.setAdapter(addToPlaylistAdapter);

        mb.setView(dialog).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).setPositiveButton("create new playlist", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openCreatePLaylistDialog();
            }
        });

        mb.setView(dialog);
        ass = mb.create();
        ass.show();
    }

    private void parsePublicPlaylist() {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(OtherUserProfileActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<ArrayList<MyPlaylistModel>> callback = dataAPI.getPublicPlaylist(token, searchedUserId);
        callback.enqueue(new Callback<ArrayList<MyPlaylistModel>>() {
            @Override
            public void onResponse(Call<ArrayList<MyPlaylistModel>> call, Response<ArrayList<MyPlaylistModel>> response) {
                collabProgressBar.setVisibility(View.GONE);
                collabPlaylist.clear();
                if (response != null && response.body() != null && response.body().size() > 0) {
                    collabTextVieww.setVisibility(View.GONE);

                    for (int i = 0; i < response.body().size(); i++) {
                        collabPlaylist.add(new Playlist(response.body().get(i).getName(), response.body().get(i).getId()));
                        addToPlaylistAdapter.notifyDataSetChanged();
                    }
                } else {
                    collabTextVieww.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<MyPlaylistModel>> call, Throwable t) {
                collabProgressBar.setVisibility(View.GONE);
            }
        });
    }

/*
    private void openCreatePLaylistDialog() {
        final AlertDialog.Builder mb = new AlertDialog.Builder(OtherUserProfileActivity.this);
        final View dialog = LayoutInflater.from(OtherUserProfileActivity.this).inflate(R.layout.layout_dialog, null, false);

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
                if (checking[0])
                    if (TextUtils.isEmpty(playlistname)) {
                        Toast.makeText(OtherUserProfileActivity.this, "please give playlist some name", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(playlistGif.toString())) {
                        Toast.makeText(OtherUserProfileActivity.this, "please enter a GIF link", Toast.LENGTH_SHORT).show();
                    } else if (!Utility.isWebUrl(playlistGif.toString())) {
                        Toast.makeText(OtherUserProfileActivity.this, "please enter a valid GIF link", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(playlistDesc.getText())) {
                        Toast.makeText(OtherUserProfileActivity.this, "please enter playlist's description", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(password)) {
                        Toast.makeText(OtherUserProfileActivity.this, "please enter playlist a password", Toast.LENGTH_SHORT).show();
                    } else {

                        callCreatePlaylist(playlistname, playlistGif.toString(), playlistDesc.getText().toString(), password, checking);
                    }
                else {
                    if (TextUtils.isEmpty(playlistname)) {
                        Toast.makeText(OtherUserProfileActivity.this, "please give playlist some name", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(playlistGif.toString())) {
                        Toast.makeText(OtherUserProfileActivity.this, "please enter a GIF link", Toast.LENGTH_SHORT).show();
                    } else if (!Utility.isWebUrl(playlistGif.toString())) {
                        Toast.makeText(OtherUserProfileActivity.this, "please enter a valid GIF link", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(playlistDesc.getText())) {
                        Toast.makeText(OtherUserProfileActivity.this, "please enter playlist's description", Toast.LENGTH_SHORT).show();
                    } else {
                        callCreatePlaylist(playlistname, playlistGif.toString(), playlistDesc.getText().toString(), "", checking);
                    }
                }
            }
        });


        mb.setView(dialog);
        final AlertDialog ass = mb.create();

        ass.show();
    }*/

    // dialog to add playlist
    private void openCreatePLaylistDialog() {
        final AlertDialog.Builder mb = new AlertDialog.Builder(OtherUserProfileActivity.this);

        final View dialog = LayoutInflater.from(OtherUserProfileActivity.this).inflate(R.layout.layout_dialog, null, false);
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
                if(isChecked){
                    publicprivate.setText("Private");
                    PlaylistPassword.setEnabled(true);
                    PlaylistPassword.setAlpha(1.0f);
                    //PlaylistPassword.setVisibility(View.VISIBLE);
                    b = true;
                }else{
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
                        if(checking[0]) {
                            if (TextUtils.isEmpty(playlistname)) {
                                Toast.makeText(OtherUserProfileActivity.this, "please give playlist some name", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(selectedGifLink[0])) {
                                Toast.makeText(OtherUserProfileActivity.this, "please choose a GIF", Toast.LENGTH_SHORT).show();
                            } else if (!Utility.isWebUrl(selectedGifLink[0])) {
                                Toast.makeText(OtherUserProfileActivity.this, "please choose a valid GIF", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(playlistDesc.getText())) {
                                Toast.makeText(OtherUserProfileActivity.this, "please enter playlist's description", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(password)) {
                                Toast.makeText(OtherUserProfileActivity.this, "please enter playlist a password", Toast.LENGTH_SHORT).show();
                            } else {
                                callCreatePlaylist(playlistname, selectedGifLink[0], playlistDesc.getText().toString(), password, checking);
                            }
                        }
                        else{
                            if (TextUtils.isEmpty(playlistname)) {
                                Toast.makeText(OtherUserProfileActivity.this, "please give playlist some name", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(selectedGifLink[0])) {
                                Toast.makeText(OtherUserProfileActivity.this, "please choose a GIF", Toast.LENGTH_SHORT).show();
                            } else if (!Utility.isWebUrl(selectedGifLink[0])) {
                                Toast.makeText(OtherUserProfileActivity.this, "please choose a valid GIF", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(playlistDesc.getText())) {
                                Toast.makeText(OtherUserProfileActivity.this, "please enter playlist's description", Toast.LENGTH_SHORT).show();
                            } else{
                                callCreatePlaylist(playlistname, selectedGifLink[0],playlistDesc.getText().toString(),"",checking);
                            }
                        }
                    }
                });
        mb.setView(dialog);
        final AlertDialog ass = mb.create();
        ass.show();
    }

    //  add text to server
    public void callCreatePlaylist(final String playlistname, final String gifLink, final String description, final String password, final Boolean[] checking) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, createnewplaylist, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    Boolean PlaylistCreated = jsonObject.getBoolean("Playlist Created");
                    if (PlaylistCreated) {
                        callUserPlaylistAPI(searchedUserId);
                        Toast.makeText(OtherUserProfileActivity.this, "playlist created", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(OtherUserProfileActivity.this, "you cannot create playlist of same name again", Toast.LENGTH_SHORT).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OtherUserProfileActivity.this, "this playlist already exists", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String token = SharedPrefManager.getInstance(OtherUserProfileActivity.this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
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
                String token = AppConstants.TOKEN + SharedPrefManager.getInstance(OtherUserProfileActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
                params.put("Authorization", token);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(OtherUserProfileActivity.this);
        requestQueue.add(stringRequest);
    }

    private void sendCollabRequestNotification(int playlistId, int searchedUserId) {

        DataAPI dataAPI = RetrofitAPI.getData();
        String headerToken = AppConstants.TOKEN + SharedPrefManager.getInstance(OtherUserProfileActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        int userId = SharedPrefManager.getInstance(OtherUserProfileActivity.this).getSharedPrefInt(AppConstants.INTENT_USER_ID);

        Call<APIResponse> callback = dataAPI.sendNotification(headerToken, userId, searchedUserId, playlistId, AppConstants.COLLAB_REQUEST);
        callback.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, retrofit2.Response<APIResponse> response) {
                //progressBar.setVisibility(View.GONE);
                Logging.d("User Profile res-->"+new Gson().toJson(response.body()));
                if (response != null && response.body() != null) {

                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        Toast.makeText(OtherUserProfileActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();

                    } else if (response.body().getStatus().equalsIgnoreCase("failed")){
                        Toast.makeText(OtherUserProfileActivity.this, response.body().getMessage(),
                                Toast.LENGTH_LONG).show();
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
