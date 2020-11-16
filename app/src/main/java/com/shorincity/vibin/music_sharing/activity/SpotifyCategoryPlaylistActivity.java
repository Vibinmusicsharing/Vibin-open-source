package com.shorincity.vibin.music_sharing.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.shorincity.vibin.music_sharing.model.SpotifyArtistModel;
import com.shorincity.vibin.music_sharing.model.SpotifyFeaturedPlaylistModel;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.spotify;
import com.shorincity.vibin.music_sharing.adapters.SpotifyFeaturedPlaylistAdapter;
import com.shorincity.vibin.music_sharing.adapters.SpotifyTrackAdapter;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpotifyCategoryPlaylistActivity extends AppCompatActivity {

    private static String TAG = SpotifyCategoryPlaylistActivity.class.getName();
    private ImageView channelBannerIv;
    private TextView channelTitleTv;
    private TextView channelDetailTv, releaseDateTv;
    private String id, channelBannerUrl, titleStr;
    private ArrayList<SpotifyFeaturedPlaylistModel.Item> trackList;
    private RecyclerView channelPlayListRv;
    private SpotifyTrackAdapter spotifyTrackAdapter;
    private ProgressBar progressBar;
    private ArrayList<SpotifyArtistModel.Item> songList;

    SpotifyFeaturedPlaylistAdapter spotifyFeaturedPlaylistAdapter;
    ArrayList<SpotifyFeaturedPlaylistModel.Item> spotifyFeaturedList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_category_playlist);

        getIntentData();

        inItViews();

        inItListeners();

    }

    private void getIntentData() {

        id = getIntent().getStringExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_ID);
        channelBannerUrl = getIntent().getStringExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_BANNER_URL);
        titleStr = getIntent().getStringExtra(AppConstants.INTENT_TITLE);

    }

    private void inItViews() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;


        channelBannerIv = (ImageView) findViewById(R.id.channel_image);
        channelBannerIv.setMinimumHeight(width);
        channelTitleTv = (TextView) findViewById(R.id.tv_channel_name);
        channelDetailTv = (TextView) findViewById(R.id.tv_channel_details);
        releaseDateTv = (TextView) findViewById(R.id.tv_release_details);
        channelPlayListRv = (RecyclerView) findViewById(R.id.channel_playlist_rv);
        progressBar = (ProgressBar)findViewById(R.id.progress_circular);

        setPlayListAdapter();

        if(!TextUtils.isEmpty(titleStr))
            channelTitleTv.setText(titleStr);

        try {
            Glide.with(SpotifyCategoryPlaylistActivity.this).load(channelBannerUrl).into(channelBannerIv);
        }catch (Exception e) {

        }
        callGetSpotifyCategoryPlaylists(id);
    }

    private void inItListeners() {
    }

    private void setPlayListAdapter() {

        spotifyFeaturedList = new ArrayList<>();
        channelPlayListRv.setLayoutManager(new GridLayoutManager(this, 2));
        channelPlayListRv.setHasFixedSize(true);

        spotifyFeaturedPlaylistAdapter = new SpotifyFeaturedPlaylistAdapter(this,spotifyFeaturedList);
        spotifyFeaturedPlaylistAdapter.setCustomItemClickListener(new SpotifyFeaturedPlaylistAdapter.CustomItemClickListener() {

            @Override
            public void onItemClick(View v, int position) {
                SpotifyFeaturedPlaylistModel.Item currentItem = spotifyFeaturedList.get(position);
                Intent intent = new Intent(SpotifyCategoryPlaylistActivity.this, SpotifyPlaylistDetailsActivity.class);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_ID,currentItem.getId());
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_BANNER_URL,currentItem.getImages().get(0).getUrl());
                intent.putExtra(AppConstants.INTENT_TITLE,currentItem.getName());
                startActivity(intent);
            }
        });
        channelPlayListRv.setAdapter(spotifyFeaturedPlaylistAdapter);



    }


    public void callGetSpotifyCategoryPlaylists(final String id) {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getSpotifydata();

        Call<SpotifyFeaturedPlaylistModel> callback = dataAPI.getSpotifyCategoryPlaylist("Bearer "+ spotify.spotifyAccessToken, id);
        callback.enqueue(new Callback<SpotifyFeaturedPlaylistModel>() {
            @Override
            public void onResponse(Call<SpotifyFeaturedPlaylistModel> call, Response<SpotifyFeaturedPlaylistModel> response) {
                progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    Log.i("SPOTIFY TRENDING RESULT", response.toString());

                    SpotifyFeaturedPlaylistModel spotifyFeaturedPlaylistModel = (SpotifyFeaturedPlaylistModel) response.body();

                    if (spotifyFeaturedPlaylistModel != null && spotifyFeaturedPlaylistModel.getPlaylists().getItems() != null) {
                        Log.i("Spotify Release RESULT", response.toString());

                        if(spotifyFeaturedPlaylistModel.getPlaylists().getItems() != null
                                && spotifyFeaturedPlaylistModel.getPlaylists().getItems().size() > 0 ) {

                            spotifyFeaturedList.addAll(spotifyFeaturedPlaylistModel.getPlaylists().getItems());
                            spotifyFeaturedPlaylistAdapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(SpotifyCategoryPlaylistActivity.this, "Result Not Found: ", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(SpotifyCategoryPlaylistActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(SpotifyCategoryPlaylistActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<SpotifyFeaturedPlaylistModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SpotifyCategoryPlaylistActivity.this,"Error: " +t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    private Date stringToDate(String dtStart) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = format.parse(dtStart);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private String dateToString(Date date) {
        String dateTime = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        dateTime = dateFormat.format(date);
        System.out.println("Current Date Time : " + dateTime);
        return dateTime;
    }
}