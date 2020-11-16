package com.shorincity.vibin.music_sharing.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.UI.custom.RoundedImageView;
import com.shorincity.vibin.music_sharing.UI.spotify;
import com.shorincity.vibin.music_sharing.UI.youtube;
import com.shorincity.vibin.music_sharing.activity.AllRecntSongsActivity;
import com.shorincity.vibin.music_sharing.adapters.LikedSongsAdapter;
import com.shorincity.vibin.music_sharing.adapters.MyPlaylistAdapter;
import com.shorincity.vibin.music_sharing.adapters.RecentPlayedAdapter;
import com.shorincity.vibin.music_sharing.model.MyPlaylistModel;
import com.shorincity.vibin.music_sharing.model.RecentSongModel;
import com.shorincity.vibin.music_sharing.model.UserProfileModel;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.spotify_files.SpotifySongsPlayerActivity;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.CustomSlidePanLayout;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.shorincity.vibin.music_sharing.utils.Utility;
import com.shorincity.vibin.music_sharing.youtube_files.PlayYoutubeVideoActivity;
import com.shorincity.vibin.music_sharing.youtube_files.PlaylistDetailActivity;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileFragment extends Fragment {
    View view;
    int userId;
    String userName, fullName, userToken;
    ArrayList<RecentSongModel> recentSongList;
    ArrayList<RecentSongModel> likedSongList;
    ArrayList<MyPlaylistModel> myPlaylists;
    private TextView viewMoreRecentTv;
    CustomSlidePanLayout slidePanLayout;
    public UserProfileFragment() {
        // Required empty public constructor
    }

    RoundedImageView userDpIv;
    TextView likeCountTv, collaboratorCountTv, usernameTv, fullNameTv;
    RecyclerView recentSongRv, favRv, playlistRv;
    RecentPlayedAdapter recentPlayedAdapter;
    LikedSongsAdapter likedSongsAdapter;
    MyPlaylistAdapter myPlaylistAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(view == null) {
            view = inflater.inflate(R.layout.fragment_user_profile, container, false);

            userId = SharedPrefManager.getInstance(getActivity()).getSharedPrefInt(AppConstants.INTENT_USER_ID);
            userName = SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_NAME);
            fullName = SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_FULL_NAME);
            userToken = SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);

            // Calling to find Activity's child view here
            inItViews();

            // setting all the required listeners in this method
            inItListener();

            // setting all the adapters inside the method
            setListAdapter();

        }
        // calling to get My Profile info here like Likes count, Collab's count etc
        callGetUserProfileAPI(userId);

        // calling to get all playlist created by me
        callMyPlaylistAPI(userToken);

        // calling to get my liked songs data
        callGetLikeSongAPI(userId);

        // calling to get my recent played song data
        callGetRecentSongAPI(userId);

        return  view;
    }

    private void setListAdapter() {
        // setting Recent Song List Adapter
        recentSongList = new ArrayList<>();

        recentSongRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false));
        recentSongRv.setHasFixedSize(true);

        recentPlayedAdapter = new RecentPlayedAdapter(getActivity(), recentSongList);
        recentPlayedAdapter.setCustomItemClickListener(new RecentPlayedAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                if(recentSongList.get(position).getSongType().equalsIgnoreCase(AppConstants.YOUTUBE)) {
                    Intent intent = new Intent(getActivity(), PlayYoutubeVideoActivity.class);
                    intent.putExtra("title",recentSongList.get(position).getSongName());
                    intent.putExtra("description",recentSongList.get(position).getSongDetails());
                    intent.putExtra("thumbnail",recentSongList.get(position).getSongThumbnail());
                    intent.putExtra("videoId",recentSongList.get(position).getSongId());
                    startActivity(intent);
                } else {
                    Intent a=new Intent(getActivity(), SpotifySongsPlayerActivity.class);
                    a.putExtra("uri",recentSongList.get(position).getSongUri());
                    a.putExtra("image",recentSongList.get(position).getSongThumbnail());
                    a.putExtra("title",recentSongList.get(position).getSongName());
                    startActivity(a);
                }
            }
        });
        recentSongRv.setAdapter(recentPlayedAdapter);


        // setting Favourite List Adapter
        likedSongList = new ArrayList<>();

        favRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false));
        favRv.setHasFixedSize(true);

        likedSongsAdapter = new LikedSongsAdapter(getActivity(), likedSongList);
        likedSongsAdapter.setCustomItemClickListener(new LikedSongsAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                if(likedSongList.get(position).getSongType().equalsIgnoreCase(AppConstants.YOUTUBE)) {
                    Intent intent = new Intent(getActivity(), PlayYoutubeVideoActivity.class);
                    intent.putExtra("title",likedSongList.get(position).getSongName());
                    intent.putExtra("description",likedSongList.get(position).getSongDetails());
                    intent.putExtra("thumbnail",likedSongList.get(position).getSongThumbnail());
                    intent.putExtra("videoId",likedSongList.get(position).getSongId());
                    startActivity(intent);
                } else {
                    Intent a=new Intent(getActivity(), SpotifySongsPlayerActivity.class);
                    a.putExtra("uri",likedSongList.get(position).getSongUri());
                    a.putExtra("image",likedSongList.get(position).getSongThumbnail());
                    a.putExtra("title",likedSongList.get(position).getSongName());
                    startActivity(a);
                }
            }
        });
        favRv.setAdapter(likedSongsAdapter);

        // setting Playlist Adapter
        myPlaylists = new ArrayList<>();

        playlistRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false));
        playlistRv.setHasFixedSize(true);

        myPlaylistAdapter = new MyPlaylistAdapter(getActivity(), myPlaylists);
        myPlaylistAdapter.setCustomItemClickListener(new MyPlaylistAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                Intent intent = new Intent(getActivity(), PlaylistDetailActivity.class);
                int id = myPlaylists.get(position).getId();
                intent.putExtra("id", id);
                intent.putExtra(AppConstants.INTENT_PLAYLIST, myPlaylists.get(position));
                startActivity(intent);

            }
        });
        playlistRv.setAdapter(myPlaylistAdapter);

    }

    private void inItViews() {
        userDpIv =   view.findViewById(R.id.user_dp_iv);
        usernameTv = (TextView) view.findViewById(R.id.tv_username);
        fullNameTv = (TextView) view.findViewById(R.id.tv_fullname);
        likeCountTv = (TextView) view.findViewById(R.id.tv_like_count);
        collaboratorCountTv = (TextView) view.findViewById(R.id.tv_collaborator_count);
        playlistRv = (RecyclerView) view.findViewById(R.id.rv_playlist);
        recentSongRv = (RecyclerView) view.findViewById(R.id.rv_recently_played);
        favRv = (RecyclerView) view.findViewById(R.id.rv_fav);
        viewMoreRecentTv = (TextView) view.findViewById(R.id.tv_view_more_recently_played);

        usernameTv.setText(userName);
        fullNameTv.setText(fullName);



    }

    private void inItListener() {

        // Opening or closing Right Navigation drawer by clicking on Setting Button
        view.findViewById(R.id.setting_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (slidePanLayout.isOpen()) {
                    slidePanLayout.closePane();
                } else {
                    slidePanLayout.openPane();
                }
            }
        });


        // Setting offsets and animation over user profile panel when user swipe up this Screen
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
                    userDpIv.animate().scaleX((float)1).setDuration(100);
                    userDpIv.animate().scaleY((float)1).setDuration(100);
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

        viewMoreRecentTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AllRecntSongsActivity.class));
                getActivity().overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            if(slidePanLayout == null && getActivity() instanceof youtube) {
                slidePanLayout = ((youtube)getActivity()).mSlidingLayout;
            } else if( slidePanLayout == null && getActivity() instanceof spotify) {
                slidePanLayout = ((spotify)getActivity()).mSlidingLayout;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                slidePanLayout.setSlidingEnable(true);
                slidePanLayout.setSliderFadeColor(getActivity().getResources().getColor(android.R.color.transparent));
                slidePanLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void callGetUserProfileAPI(int userId) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);


        Call<UserProfileModel> addLogCallback = dataAPI.getUserProfile(token,userId,userId);
        addLogCallback.enqueue(new Callback<UserProfileModel>() {
            @Override
            public void onResponse(Call<UserProfileModel> call, retrofit2.Response<UserProfileModel> response) {
                if(response != null && response.body() != null ) {

                    Logging.dLong("Profile: "+new Gson().toJson(response.body()));
                    if (response.body().getButtonStatus().equalsIgnoreCase("disabled")) {

                    } else {}

                    likeCountTv.setText(Utility.prettyCount(response.body().getHowManyLikesUser()));
                    collaboratorCountTv.setText(Utility.prettyCount(response.body().getCollaboratorCount()));

                    String avatarUrl = response.body().getAvatarLink();

                    if(userDpIv != null) {
                        Glide.with(getActivity()).load(avatarUrl).into(userDpIv);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfileModel> call, Throwable t) {

            }
        });
    }


    private void callGetRecentSongAPI(int userId) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<ArrayList<RecentSongModel>> callback = dataAPI.getRecentSongs(token, userId);
        callback.enqueue(new Callback<ArrayList<RecentSongModel>>() {
            @Override
            public void onResponse(Call<ArrayList<RecentSongModel>> call, Response<ArrayList<RecentSongModel>> response) {
                recentSongList.clear();
                if (response != null && response.body()!= null && response.body().size() > 0) {
                    recentSongList.addAll(response.body());
                    recentPlayedAdapter.notifyDataSetChanged();
                } else {

                }
            }

            @Override
            public void onFailure(Call<ArrayList<RecentSongModel>> call, Throwable t) {

            }
        });

    }

    private void callGetLikeSongAPI(int userId) {

        DataAPI dataAPI = RetrofitAPI.getData();
        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<ArrayList<RecentSongModel>> callback = dataAPI.getLikedSongs(token, userId);
        callback.enqueue(new Callback<ArrayList<RecentSongModel>>() {
            @Override
            public void onResponse(Call<ArrayList<RecentSongModel>> call, Response<ArrayList<RecentSongModel>> response) {
                likedSongList.clear();
                if (response != null && response.body()!= null && response.body().size() > 0) {
                    likedSongList.addAll(response.body());
                    likedSongsAdapter.notifyDataSetChanged();
                } else {

                }
            }

            @Override
            public void onFailure(Call<ArrayList<RecentSongModel>> call, Throwable t) {

            }
        });

    }

    private void callMyPlaylistAPI(String userToken) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<ArrayList<MyPlaylistModel>> callback = dataAPI.getMyPlaylist(token, userToken);
        callback.enqueue(new Callback<ArrayList<MyPlaylistModel>>() {
            @Override
            public void onResponse(Call<ArrayList<MyPlaylistModel>> call, Response<ArrayList<MyPlaylistModel>> response) {
                myPlaylists.clear();
                if (response != null && response.body()!= null && response.body().size() > 0) {
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
}
