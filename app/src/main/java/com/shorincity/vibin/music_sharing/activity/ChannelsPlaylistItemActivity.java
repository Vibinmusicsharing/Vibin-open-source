package com.shorincity.vibin.music_sharing.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.adapters.YoutubArtistPlayListAdapter;
import com.shorincity.vibin.music_sharing.model.HomeYoutubeModel;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.model.YoutubePlaylistItemModel;
import com.shorincity.vibin.music_sharing.model.YoutubeTrendingModel;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.shorincity.vibin.music_sharing.youtube_files.PlayYoutubeVideoActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Youtube Songs/Channel/Playlist second screen
public class ChannelsPlaylistItemActivity extends AppCompatActivity {

    private static String TAG = ChannelsPlaylistItemActivity.class.getName();
    private ImageView channelBannerIv;
    private TextView channelTitleTv;
    private TextView channelDetailTv;
    private String channelId, channelBannerUrl, playlistId, videoId;
    private HomeYoutubeModel.YoutubeCustomModel youtubeChannelData;
    private ArrayList<YoutubePlaylistItemModel> youtubeChannelPLayList;
    private YoutubePlaylistItemModel playlistItemModel;
    private RecyclerView channelPlayListRv;
    private YoutubArtistPlayListAdapter youtubeListAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channels_playlist);

        // function to fetch all the data passed by previous activity by intent
        getIntentData();
        // Calling to find Activity's child view here
        inItViews();

        populateArtistInfo();
        // setting all the adapters inside the method
        inItListeners();

    }

    private String mName;
    private String mThumbnail;

    private void populateArtistInfo() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mName = bundle.getString("title");
            mThumbnail = bundle.getString("thumbnail");
        }
        Logging.d("mThumbnail:" + mThumbnail);
        channelTitleTv.setText(mName);

        if (!TextUtils.isEmpty(mThumbnail) && !mThumbnail.equalsIgnoreCase("THUMBNAIL_URI")) {

            Glide.with(ChannelsPlaylistItemActivity.this).load(mThumbnail).into(channelBannerIv);
            //.onLoadFailed(new ColorDrawable(mContext.getResources().getColor(R.color.light_gray)));
        } else
            channelBannerIv.setImageResource(R.drawable.music_placeholder);
    }

    private void getIntentData() {

        youtubeChannelData = (HomeYoutubeModel.YoutubeCustomModel) getIntent().getSerializableExtra(AppConstants.INTENT_YOUTUBE_PLAYLIST_DATA);

        String[] imgUrl = youtubeChannelData.getUrl().split("&");
        String[] videoIdArr = imgUrl[0].split("=");
        String[] playlistIdArr = imgUrl[1].split("=");

        videoId = videoIdArr[videoIdArr.length - 1];
        playlistId = playlistIdArr[playlistIdArr.length - 1];
    }

    private void inItViews() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;


        channelBannerIv = (ImageView) findViewById(R.id.channel_image);
        channelBannerIv.setMinimumHeight(width);
        channelTitleTv = (TextView) findViewById(R.id.tv_channel_name);
        channelDetailTv = (TextView) findViewById(R.id.tv_channel_details);
        channelPlayListRv = (RecyclerView) findViewById(R.id.channel_playlist_rv);
        progressBar = (ProgressBar) findViewById(R.id.progress_circular);


        callgetYoutubePlatlistItemAPI(playlistId, videoId);
    }

    private void inItListeners() {
    }

    private void setPlayListAdapter() {

        youtubeChannelPLayList = new ArrayList<>();
        channelPlayListRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        channelPlayListRv.setHasFixedSize(true);
        channelPlayListRv.setNestedScrollingEnabled(true);


        if (playlistItemModel.getItems() != null) {


            youtubeListAdapter = new YoutubArtistPlayListAdapter(ChannelsPlaylistItemActivity.this, (ArrayList<YoutubePlaylistItemModel.Item>) playlistItemModel.getItems());

            youtubeListAdapter.setCustomItemClickListener(new YoutubArtistPlayListAdapter.CustomItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    try {
                        ArrayList<PlaylistDetailModel> playlist;
                        playlist = new ArrayList<>();
                        String defaultThumbnail = "";
                        for (int i = 0; i < playlistItemModel.getItems().size(); i++) {
                            if (playlistItemModel.getItems().get(i).getSnippet().getThumbnails().getHigh() != null) {
                                defaultThumbnail = playlistItemModel.getItems().get(i).getSnippet().getThumbnails().getHigh().getUrl();
                                break;
                            }
                        }
                        for (int i = 0; i < playlistItemModel.getItems().size(); i++) {
                            if (playlistItemModel.getItems().get(i).getSnippet().getThumbnails().getHigh() != null) {
                                playlist.add(new PlaylistDetailModel(
                                        playlistItemModel.getItems().get(i).getSnippet().getTitle(),
                                        playlistItemModel.getItems().get(i).getSnippet().getThumbnails().getHigh().getUrl(),
                                        playlistItemModel.getItems().get(i).getSnippet().getResourceId().getVideoId()
                                ));
                            } else {
                                playlist.add(new PlaylistDetailModel(
                                        playlistItemModel.getItems().get(i).getSnippet().getTitle(),
                                        defaultThumbnail,
                                        playlistItemModel.getItems().get(i).getSnippet().getResourceId().getVideoId()
                                ));
                            }
                        }
                        Bundle bundle = new Bundle();
                        bundle.putInt("position", position);
                        bundle.putString("title", playlistItemModel.getItems().get(position).getSnippet().getTitle());
                        bundle.putString("description", playlistItemModel.getItems().get(position).getSnippet().getDescription());
                        if (playlistItemModel.getItems().get(position).getSnippet().getThumbnails().getHigh() != null) {
                            bundle.putString("thumbnail", playlistItemModel.getItems().get(position).getSnippet().getThumbnails().getHigh().getUrl());
                        } else {
                            bundle.putString("thumbnail", defaultThumbnail);
                        }
                        bundle.putString("videoId", playlistItemModel.getItems().get(position).getSnippet().getResourceId().getVideoId());
                        bundle.putParcelableArrayList("playlist", (ArrayList<? extends Parcelable>) playlist);
                        Intent intent = new Intent(ChannelsPlaylistItemActivity.this, PlayYoutubeVideoActivity.class);
                        intent.putExtra("data", bundle);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            channelPlayListRv.setAdapter(youtubeListAdapter);
        }
    }

    public void callgetYoutubePlatlistItemAPI(final String playlistId, String videoId) {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getYoutubeData();

        //https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&key= {API key} &maxResults=50 &playlistId = {id}

        Log.d("TEST", "playlistId: " + playlistId);


        //        Call<YoutubePlaylistItemModel> callback = dataAPI.getYoutubePlayListItem("snippet","20",playlistId,videoId,"AIzaSyDn7GZfot4NowEcGPzRYv7h80s7LUT_vcs");

        //https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&key=AIzaSyDn7GZfot4NowEcGPzRYv7h80s7LUT_vcs&maxResults=50&playlistId=PLldRXoeaXPNm2wL1iy2fBMc55c1T8ZLhp

        Call<YoutubePlaylistItemModel> callback = dataAPI.getYoutubePlayListItem("snippet", "AIzaSyDn7GZfot4NowEcGPzRYv7h80s7LUT_vcs", "50", playlistId);
        callback.enqueue(new Callback<YoutubePlaylistItemModel>() {
            @Override
            public void onResponse(Call<YoutubePlaylistItemModel> call, Response<YoutubePlaylistItemModel> response) {
                progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    // Logging.dLong("playlistId res: "+ new Gson().toJson(response));

                    if (response.body().getItems() != null && response.body().getItems().size() > 0) {

                        playlistItemModel = response.body();

                        YoutubePlaylistItemModel.Snippet snippet = playlistItemModel.getItems().get(0).getSnippet();
                        Collections.reverse(playlistItemModel.getItems());
                        if (!TextUtils.isEmpty(snippet.getChannelTitle()))
                            //channelTitleTv.setText(snippet.getChannelTitle());

                            if (!TextUtils.isEmpty(snippet.getDescription()))
                                // channelDetailTv.setText(snippet.getDescription());

                                //                        try {
                                //                            Glide.with(ChannelsPlaylistItemActivity.this).load(snippet.getThumbnails().getHigh().getUrl()).into(channelBannerIv);
                                //                        } catch (Exception e) {
                                //
                                //                        }
                                setPlayListAdapter();
                        // callgetYoutubeChannelPlatlistListAPI(playlistItemModel.getItems().get(1).getSnippet().getChannelId());

                    } else {
                        Toast.makeText(ChannelsPlaylistItemActivity.this, "Result Not Found: ", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ChannelsPlaylistItemActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<YoutubePlaylistItemModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ChannelsPlaylistItemActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }


    public void callgetYoutubeChannelPlatlistListAPI(final String channelId) {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getYoutubeData();

        Log.d("TEST", "channelId:" + channelId);
        ////https://www.googleapis.com/youtube/v3/playlists?part=snippet&channelId={ChannelID}&key={API key} & maxResults=50


        Call<YoutubeTrendingModel> callback = dataAPI.getYoutubeChannelsPlayList("snippet", channelId, "AIzaSyDn7GZfot4NowEcGPzRYv7h80s7LUT_vcs", "50");
        callback.enqueue(new Callback<YoutubeTrendingModel>() {
            @Override
            public void onResponse(Call<YoutubeTrendingModel> call, Response<YoutubeTrendingModel> response) {
                progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    Log.d("TEST", "channelId res: " + new Gson().toJson(response));

                    if (response.body().getItems() != null && response.body().getItems().size() > 0) {
                        //                        youtubeChannelPLayList.addAll(response.body().getItems());
                        //                        youtubeListAdapter.notifyDataSetChanged();
                        //Toast.makeText(ChannelsPlaylistItemActivity.this, "Result Received ", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(ChannelsPlaylistItemActivity.this, "Result Not Found: ", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ChannelsPlaylistItemActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<YoutubeTrendingModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ChannelsPlaylistItemActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
}
