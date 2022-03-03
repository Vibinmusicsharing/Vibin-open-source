package com.shorincity.vibin.music_sharing.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.like.LikeButton;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.UI.custom.RoundedImageView;
import com.shorincity.vibin.music_sharing.UI.youtube;
import com.shorincity.vibin.music_sharing.adapters.AddToPlaylistAdapter;
import com.shorincity.vibin.music_sharing.adapters.AutoCompleteAdapter;
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
import com.shorincity.vibin.music_sharing.utils.CommonUtils;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.shorincity.vibin.music_sharing.utils.Utility;
import com.shorincity.vibin.music_sharing.widgets.TagView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtherUserProfileFragment extends MyBaseFragment implements View.OnClickListener {
    private View view;
    private Context mContext;
    private static final String LIKE = "Like", UNLIKE = "Unlike";
    private int userId, searchedUserId, playlistId;
    private String userName, fullName, userToken;
    private ArrayList<RecentSongModel> likedSongList;
    private ArrayList<MyPlaylistModel> myPlaylists;
    private AppCompatImageView like_img;
    private RoundedImageView userDpIv;
    private TextView likeCountTv, collaboratorCountTv, usernameTv, fullNameTv;
    private RecyclerView likedSingRv, playlistRv;
    private RecentPlayedAdapter likedSongAdapter;
    private MyPlaylistAdapter myPlaylistAdapter;
    private CircularProgressButton likeBtn;
    private RelativeLayout likeRl;
    private String createnewplaylist = AppConstants.BASE_URL + "playlist/v1/create_new_playlist/";
    private RippleButton collaborateBtn;
    private ArrayList<MyPlaylistModel> collabPlaylist;
    private AddToPlaylistAdapter addToPlaylistAdapter;
    private TextView collabTextVieww;
    private ProgressBar collabProgressBar;
    private LikeButton like_btn;
    private ArrayList<String> genreList;

    private AlertDialog ass;

    public static OtherUserProfileFragment getInstance(int searchedUserId, int playlistId,
                                                       String userName, String fullName) {
        OtherUserProfileFragment fragment = new OtherUserProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(AppConstants.INTENT_SEARCHED_USER_ID, searchedUserId);
        bundle.putInt(AppConstants.INTENT_PLAYLIST_ID, playlistId);
        bundle.putString(AppConstants.INTENT_SEARCHED_USER_NAME, userName);
        bundle.putString(AppConstants.INTENT_SEARCHED_FULL_NAME, fullName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_other_user_profile, container, false);
        mContext = view.getContext();
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userId = SharedPrefManager.getInstance(mContext).getSharedPrefInt(AppConstants.INTENT_USER_ID);
        userToken = SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);

        // Basic details passing through intent to fetch and display more detail about user
        searchedUserId = getArguments().getInt(AppConstants.INTENT_SEARCHED_USER_ID, 0);
        playlistId = getArguments().getInt(AppConstants.INTENT_PLAYLIST_ID, 0);
        userName = getArguments().getString(AppConstants.INTENT_SEARCHED_USER_NAME, "");
        fullName = getArguments().getString(AppConstants.INTENT_SEARCHED_FULL_NAME, "");
        genreList = CommonUtils.getGenre();

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

    private void inItViews() {
        like_btn = view.findViewById(R.id.like);
        like_img = view.findViewById(R.id.like_img);
        userDpIv = view.findViewById(R.id.user_dp_iv);
        usernameTv = (TextView) view.findViewById(R.id.tv_username);
        fullNameTv = (TextView) view.findViewById(R.id.tv_fullname);
        likeCountTv = (TextView) view.findViewById(R.id.tv_like_count);
        collaboratorCountTv = (TextView) view.findViewById(R.id.tv_collaborator_count);
        playlistRv = (RecyclerView) view.findViewById(R.id.rv_playlist);
        likedSingRv = (RecyclerView) view.findViewById(R.id.rv_fav);
//        likeBtn = view.findViewById(R.id.like_btn);
        collaborateBtn = view.findViewById(R.id.collaborate_btn);
        likeRl = (RelativeLayout) view.findViewById(R.id.like_rl);

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
        ((AppBarLayout) view.findViewById(R.id.user_profile_appbar)).addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
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

        like_btn.setOnClickListener(this);
        likeRl.setOnClickListener(this);

    }


    private void setListAdapter() {
        likedSongList = new ArrayList<>();

        likedSingRv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        likedSingRv.setHasFixedSize(true);

        likedSongAdapter = new RecentPlayedAdapter(mContext, likedSongList);
        likedSongAdapter.setCustomItemClickListener(new RecentPlayedAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                try {
                    if (likedSongList.get(position).getSongType().equalsIgnoreCase(AppConstants.YOUTUBE)) {
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
                        bundle.putString("from", "channel");
                        bundle.putParcelableArrayList("playlist", (ArrayList<? extends Parcelable>) playlist);
                        onMusicPlay(bundle);

/*                        Intent intent = new Intent(mContext, PlayYoutubeVideoActivity.class);
                        intent.putExtra("data", bundle);
                        startActivity(intent);*/
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        likedSingRv.setAdapter(likedSongAdapter);

        // set Playlist Adapter
        myPlaylists = new ArrayList<>();

        playlistRv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        playlistRv.setHasFixedSize(true);

        myPlaylistAdapter = new MyPlaylistAdapter(mContext, myPlaylists);
        myPlaylistAdapter.setCustomItemClickListener(new MyPlaylistAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                PlaylistDetailFragmentNew fragment = PlaylistDetailFragmentNew.getInstance(myPlaylists.get(position).getId(),
                        myPlaylists.get(position).getAdmin_id(), myPlaylists.get(position), searchedUserId);
                ((youtube) getActivity()).onLoadFragment(fragment);
                /*Intent intent = new Intent(mContext, PlaylistDetailActivity.class);
                int id = myPlaylists.get(position).getId();
                intent.putExtra("id", id);
                intent.putExtra("adminname", myPlaylists.get(position).getAdminName());
                intent.putExtra(AppConstants.INTENT_PLAYLIST, myPlaylists.get(position));
                startActivity(intent);*/
            }
        });
        playlistRv.setAdapter(myPlaylistAdapter);

    }

    public void callGetUserProfileAPI(int userId, int searchedUserId) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<UserProfileModel> addLogCallback = dataAPI.getUserProfile(token, userId, searchedUserId);
        addLogCallback.enqueue(new Callback<UserProfileModel>() {
            @Override
            public void onResponse(Call<UserProfileModel> call, retrofit2.Response<UserProfileModel> response) {
                if (response != null && response.body() != null) {

                    if (response.body().getButtonStatus().equalsIgnoreCase("liked")) {
//                        likeBtn.setText(UNLIKE);
                        likeRl.setSelected(true);
//                        like_img.setBackgroundResource(R.drawable.heart_on_icon);
                        like_btn.setLiked(true);
                    } else {
//                        likeBtn.setText(LIKE);
                        likeRl.setSelected(false);
//                        like_img.setBackgroundResource(R.drawable.heart_off_icon);
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

    private void callUserPlaylistAPI(int searchedUserId) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<ArrayList<MyPlaylistModel>> callback = dataAPI.getPublicPlaylist(token, searchedUserId);
        callback.enqueue(new Callback<ArrayList<MyPlaylistModel>>() {
            @Override
            public void onResponse(Call<ArrayList<MyPlaylistModel>> call, Response<ArrayList<MyPlaylistModel>> response) {
                //((ProgressBar) view.findViewById(R.id.progressbar)).setVisibility(View.GONE);
                myPlaylists.clear();
                if (response.body() != null && response.body().size() > 0) {
                    view.findViewById(R.id.tv_playlist_placeholder).setVisibility(View.GONE);
                    playlistRv.setVisibility(View.VISIBLE);
                    myPlaylists.addAll(response.body());
                    myPlaylistAdapter.notifyDataSetChanged();
                } else {
                    view.findViewById(R.id.tv_playlist_placeholder).setVisibility(View.VISIBLE);
                    playlistRv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<MyPlaylistModel>> call, Throwable t) {

            }
        });
    }

    private void callGetUserLikedSongAPI(int userId) {

        DataAPI dataAPI = RetrofitAPI.getData();
        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);


        Call<ArrayList<RecentSongModel>> callback = dataAPI.getLikedSongs(token, userId);
        callback.enqueue(new Callback<ArrayList<RecentSongModel>>() {
            @Override
            public void onResponse(Call<ArrayList<RecentSongModel>> call, Response<ArrayList<RecentSongModel>> response) {
                likedSongList.clear();
                if (response != null && response.body() != null && response.body().size() > 0) {
                    view.findViewById(R.id.tv_recent_played_placeholder).setVisibility(View.GONE);
                    likedSingRv.setVisibility(View.VISIBLE);
                    likedSongList.addAll(response.body());
                    likedSongAdapter.notifyDataSetChanged();
                } else {
                    view.findViewById(R.id.tv_recent_played_placeholder).setVisibility(View.VISIBLE);
                    likedSingRv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<RecentSongModel>> call, Throwable t) {

            }
        });

    }

    private void callUpdateLikeStatusAPI(int userId, int searchedUserId, String likeStatus) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        //likeBtn.startAnimation();
        Call<UpdateLikeStatusModel> addLogCallback = dataAPI.updateLikeUserProfile(token, userId, searchedUserId, likeStatus);
        addLogCallback.enqueue(new Callback<UpdateLikeStatusModel>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(Call<UpdateLikeStatusModel> call, Response<UpdateLikeStatusModel> response) {

                if (response.body().getButtonStatus().equalsIgnoreCase("liked")) {
                    Logging.d("liked --->");
                    /*likeBtn.setText(getResources().getString(R.string.like));
                    like_img.setBackgroundResource(R.drawable.heart_on_icon);*/
                    likeRl.setSelected(true);
                    like_btn.setLiked(true);

                } else {
                    /*
                    likeBtn.setText(getResources().getString(R.string.unlike));
                    like_img.setBackgroundResource(R.drawable.heart_off_icon);*/
                    likeRl.setSelected(false);
                    like_btn.setLiked(false);
                }

                likeCountTv.setText(Utility.prettyCount(response.body().getHowManyLikesUser()));

            }

            @Override
            public void onFailure(Call<UpdateLikeStatusModel> call, Throwable t) {
//                likeBtn.revertAnimation();
                likeRl.setSelected(false);
                like_btn.setLiked(false);
            }
        });
    }

    private void sendLikeNotification(int userId, int searchedUserId, String likeStatus) {

        if (likeStatus.equalsIgnoreCase("True")) {

            DataAPI dataAPI = RetrofitAPI.getData();
            String headerToken = AppConstants.TOKEN + SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

            Call<APIResponse> callback = dataAPI.sendNotification(headerToken, userId, searchedUserId, 0, AppConstants.PROFILE_LIKE);
            callback.enqueue(new Callback<APIResponse>() {

                @SuppressLint("NewApi")
                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                    //progressBar.setVisibility(View.GONE);
                    if (response != null && response.body() != null) {
                        Logging.d("like success--->" + new Gson().toJson(response.body()));
                        if (response.body().getStatus().equalsIgnoreCase("success")) {
//                            like_img.setBackgroundResource(R.drawable.heart_on_icon);
                            likeRl.setSelected(true);
                            like_btn.setLiked(true);
                        } else {
//                            like_img.setBackgroundResource(R.drawable.heart_off_icon);
                            likeRl.setSelected(false);
                            like_btn.setLiked(false);
                        }
                    } else {
                        likeRl.setSelected(false);
                        like_btn.setLiked(false);
                    }
                }

                @SuppressLint("NewApi")
                @Override
                public void onFailure(Call<APIResponse> call, Throwable t) {
                    Log.i("Error: ", "ADD NOTIFICATION " + t.getMessage());
                    Logging.d("unlike onFailure--->");

//                    like_img.setBackgroundResource(R.drawable.heart_off_icon);
                    likeRl.setSelected(false);
                    like_btn.setLiked(false);
                }
            });
        }

    }

    // add video to playlist
    private void Collabdialog() {
        final AlertDialog.Builder mb = new AlertDialog.Builder(mContext);
        final View dialog = LayoutInflater.from(mContext).inflate(R.layout.dialog_add_to_playlist, null, false);

        collabPlaylist = new ArrayList<>();
        RecyclerView youtubePlayListRecyclerView = dialog.findViewById(R.id.playlists);
        collabTextVieww = dialog.findViewById(R.id.textviewplaylistplayer);
        collabProgressBar = dialog.findViewById(R.id.progressbarPlayList);
        collabProgressBar.setVisibility(View.VISIBLE);
        youtubePlayListRecyclerView.setHasFixedSize(true);
        youtubePlayListRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        // parse public playlist to collaborate with
        parsePublicPlaylist();
        addToPlaylistAdapter = new AddToPlaylistAdapter(mContext, collabPlaylist);
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

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<ArrayList<MyPlaylistModel>> callback = dataAPI.getPublicPlaylist(token, searchedUserId);
        callback.enqueue(new Callback<ArrayList<MyPlaylistModel>>() {
            @Override
            public void onResponse(Call<ArrayList<MyPlaylistModel>> call, Response<ArrayList<MyPlaylistModel>> response) {
                collabProgressBar.setVisibility(View.GONE);
                collabPlaylist.clear();
                if (response != null && response.body() != null && response.body().size() > 0) {
                    collabTextVieww.setVisibility(View.GONE);
                    collabPlaylist.addAll(response.body());
                    addToPlaylistAdapter.notifyDataSetChanged();
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

    // dialog to add playlist
    private void openCreatePLaylistDialog() {
        /*final AlertDialog.Builder mb = new AlertDialog.Builder(mContext);

        final View dialog = LayoutInflater.from(mContext).inflate(R.layout.layout_dialog, null, false);
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
                selectedGifIv.setMedia(media, RenditionType.preview, ContextCompat.getDrawable(mContext.getApplicationContext(), R.color.light_gray));
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
                                Toast.makeText(mContext, "please give playlist some name", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(selectedGifLink[0])) {
                                Toast.makeText(mContext, "please choose a GIF", Toast.LENGTH_SHORT).show();
                            } else if (!Utility.isWebUrl(selectedGifLink[0])) {
                                Toast.makeText(mContext, "please choose a valid GIF", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(playlistDesc.getText())) {
                                Toast.makeText(mContext, "please enter playlist's description", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(password)) {
                                Toast.makeText(mContext, "please enter playlist a password", Toast.LENGTH_SHORT).show();
                            } else {
                                callCreatePlaylist(playlistname, selectedGifLink[0], playlistDesc.getText().toString(), password, checking);
                            }
                        } else {
                            if (TextUtils.isEmpty(playlistname)) {
                                Toast.makeText(mContext, "please give playlist some name", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(selectedGifLink[0])) {
                                Toast.makeText(mContext, "please choose a GIF", Toast.LENGTH_SHORT).show();
                            } else if (!Utility.isWebUrl(selectedGifLink[0])) {
                                Toast.makeText(mContext, "please choose a valid GIF", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(playlistDesc.getText())) {
                                Toast.makeText(mContext, "please enter playlist's description", Toast.LENGTH_SHORT).show();
                            } else {
                                callCreatePlaylist(playlistname, selectedGifLink[0], playlistDesc.getText().toString(), "", checking);
                            }
                        }
                    }
                });
        mb.setView(dialog);
        final AlertDialog ass = mb.create();
        ass.show();*/
        openCreatePlaylistBottomsheet();
    }

    private void openCreatePlaylistBottomsheet() {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(mContext);
        View bottom_sheet = LayoutInflater.from(mContext).inflate(R.layout.bottomsheet_create_playlist, null);
        bottomSheet.setContentView(bottom_sheet);

        EditText playlistName = bottom_sheet.findViewById(R.id.dialog_playlistname);
        SearchView playlistGif = bottom_sheet.findViewById(R.id.dialog_playlist_gif);
        GifView selectedGifIv = bottom_sheet.findViewById(R.id.selected_gif_iv);
        EditText playlistDesc = bottom_sheet.findViewById(R.id.dialog_playlist_desc);
        GiphyGridView giphyGridView = bottom_sheet.findViewById(R.id.gifsGridView);
        TagView tagView = bottom_sheet.findViewById(R.id.tag_view_test);
        AppCompatImageView ivClose = bottom_sheet.findViewById(R.id.ivClose);
        Button btnCreate = bottom_sheet.findViewById(R.id.btnCreate);

        AppCompatAutoCompleteTextView autoCompleteTextView = bottom_sheet.findViewById(R.id.actTags);
        AutoCompleteAdapter adapter = new AutoCompleteAdapter(mContext, android.R.layout.simple_dropdown_item_1line, android.R.id.text1, genreList);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String tag = parent.getItemAtPosition(position).toString();
            tagView.addTag(tag);
            autoCompleteTextView.setText("");
        });

        final String[] selectedGifLink = {""};
        // setting Giphy GridView
        giphyGridView.setDirection(GiphyGridView.HORIZONTAL);
        giphyGridView.setSpanCount(2);
        giphyGridView.setCellPadding(0);
        giphyGridView.setContent(GPHContent.Companion.getTrendingGifs());

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
        ivClose.setOnClickListener(v -> bottomSheet.dismiss());

        btnCreate.setOnClickListener(v -> {
            if (TextUtils.isEmpty(playlistName.getText().toString().trim())) {
                Toast.makeText(mContext, "please give playlist some name", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(selectedGifLink[0])) {
                Toast.makeText(mContext, "please choose a GIF", Toast.LENGTH_SHORT).show();
            } else if (!Utility.isWebUrl(selectedGifLink[0])) {
                Toast.makeText(mContext, "please choose a valid GIF", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(playlistDesc.getText().toString().trim())) {
                Toast.makeText(mContext, "please enter playlist's description", Toast.LENGTH_SHORT).show();
            } else {

                ProgressDialog showMe = new ProgressDialog(mContext);
                showMe.setMessage("Please wait");
                showMe.setCancelable(true);
                showMe.setCanceledOnTouchOutside(false);
                showMe.show();

                DataAPI dataAPI = RetrofitAPI.getData();
                String token = AppConstants.TOKEN + SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
                Call<MyPlaylistModel> callback = dataAPI.callCreatePlayList(token,
                        SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_TOKEN),
                        playlistName.getText().toString().trim(), playlistDesc.getText().toString().trim(),
                        selectedGifLink[0], "false", "",
                        tagView.getSelectedTags().size() > 0 ? TextUtils.join("|", tagView.getSelectedTags()) : "All");
                callback.enqueue(new Callback<MyPlaylistModel>() {
                    @Override
                    public void onResponse(Call<MyPlaylistModel> call, retrofit2.Response<MyPlaylistModel> response) {
                        showMe.dismiss();
                        if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                            bottomSheet.dismiss();
                            collabPlaylist.add(response.body());
                            addToPlaylistAdapter.notifyItemInserted(collabPlaylist.size());
                        } else {
                            Toast.makeText(mContext,
                                    (response.body() != null && response.body().getMessage() != null) ? response.body().getMessage() : "Something went wrong!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MyPlaylistModel> call, Throwable t) {
                        showMe.dismiss();
                        Toast.makeText(mContext,
                                t.getLocalizedMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        bottomSheet.show();

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
                        Toast.makeText(mContext, "playlist created", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "you cannot create playlist of same name again", Toast.LENGTH_SHORT).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "this playlist already exists", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String token = SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
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
                String token = AppConstants.TOKEN + SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
                params.put("Authorization", token);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    private void sendCollabRequestNotification(int playlistId, int searchedUserId) {

        DataAPI dataAPI = RetrofitAPI.getData();
        String headerToken = AppConstants.TOKEN + SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        int userId = SharedPrefManager.getInstance(mContext).getSharedPrefInt(AppConstants.INTENT_USER_ID);

        Call<APIResponse> callback = dataAPI.sendNotification(headerToken, userId, searchedUserId, playlistId, AppConstants.COLLAB_REQUEST);
        callback.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, retrofit2.Response<APIResponse> response) {
                //progressBar.setVisibility(View.GONE);
                Logging.d("User Profile res-->" + new Gson().toJson(response.body()));
                if (response != null && response.body() != null) {

                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_LONG).show();

                    } else if (response.body().getStatus().equalsIgnoreCase("failed")) {
                        Toast.makeText(mContext, response.body().getMessage(),
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


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.like_rl:
            case R.id.like:
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
                Logging.d("TEST", "collaborate_btn Clicked");
                if (playlistId > 0)
                    sendCollabRequestNotification(playlistId, searchedUserId);
                    //callAddCollaborateAPI(playlistId,searchedUserId);
                else
                    Collabdialog();
                break;
        }
    }
}
