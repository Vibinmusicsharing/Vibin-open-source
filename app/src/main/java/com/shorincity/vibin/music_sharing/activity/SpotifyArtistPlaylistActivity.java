package com.shorincity.vibin.music_sharing.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shorincity.vibin.music_sharing.model.SpotifyArtistModel;
import com.shorincity.vibin.music_sharing.model.SpotifyNewReleaseModel;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.spotify;
import com.shorincity.vibin.music_sharing.adapters.SpotifyArtistAdapter;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.spotify_files.SpotifySongsPlayerActivity;
import com.shorincity.vibin.music_sharing.utils.AppConstants;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Spotify Detail Screen of an artist
public class SpotifyArtistPlaylistActivity extends AppCompatActivity {

    private static String TAG = SpotifyArtistPlaylistActivity.class.getName();
    private ImageView channelBannerIv;
    private TextView channelTitleTv;
    private TextView channelDetailTv;
    private String channelId, channelBannerUrl, titleStr;
    private SpotifyNewReleaseModel.Item spotifyChannelData;
    private ArrayList<SpotifyArtistModel.Item> spotifyArtistList;
    private RecyclerView channelPlayListRv;
    private SpotifyArtistAdapter spotifyArtistAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_playlist);

        getIntentData();

        inItViews();

        inItListeners();

    }

    private void getIntentData() {

        spotifyChannelData = (SpotifyNewReleaseModel.Item) getIntent().getSerializableExtra(AppConstants.INTENT_SPOTIFY_CHANNEL_DATA);

        channelId = getIntent().getStringExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_ID);
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
        channelPlayListRv = (RecyclerView) findViewById(R.id.channel_playlist_rv);
        progressBar = (ProgressBar)findViewById(R.id.progress_circular);

        setPlayListAdapter();

        if(!TextUtils.isEmpty(titleStr))
            channelTitleTv.setText(titleStr);

        try {
            Glide.with(SpotifyArtistPlaylistActivity.this).load(channelBannerUrl).into(channelBannerIv);
        }catch (Exception e) {

        }
        callGetSpotifyArtistListAPI(channelId);
    }

    private void inItListeners() {
    }

    private void setPlayListAdapter() {

        spotifyArtistList = new ArrayList<>();
        channelPlayListRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        channelPlayListRv.setHasFixedSize(true);

        spotifyArtistAdapter = new SpotifyArtistAdapter(SpotifyArtistPlaylistActivity.this, spotifyArtistList);
        spotifyArtistAdapter.setCustomItemClickListener(new SpotifyArtistAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                Intent a=new Intent(SpotifyArtistPlaylistActivity.this, SpotifySongsPlayerActivity.class);
                a.putExtra("uri",spotifyArtistList.get(position).getUri());
                a.putExtra("image",spotifyArtistList.get(position).getImages().get(0).getUrl());
                a.putExtra("title",spotifyArtistList.get(position).getName());
                a.putExtra("songIndex",position);
                a.putExtra("SongsList",spotifyArtistList);
                startActivity(a);
            }
        });
        channelPlayListRv.setAdapter(spotifyArtistAdapter);
    }


    public void callGetSpotifyArtistListAPI(final String artistId) {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getSpotifydata();

        Call<SpotifyArtistModel> callback = dataAPI.getSpotifyArtistData("Bearer "+ spotify.spotifyAccessToken, artistId);
        callback.enqueue(new Callback<SpotifyArtistModel>() {
            @Override
            public void onResponse(Call<SpotifyArtistModel> call, Response<SpotifyArtistModel> response) {
                progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {

                    if(response.body().getItems() != null
                            && response.body().getItems().size() > 0 ) {


                        ArrayList<String> songName = new ArrayList<>();

                        spotifyArtistList.clear();

                        for(int i = 0; i< response.body().getItems().size(); i++){

                            if(!songName.contains(response.body().getItems().get(i).getName())) {
                                spotifyArtistList.add(response.body().getItems().get(i));
                            }

                            songName.add(response.body().getItems().get(i).getName());

                        }
                        spotifyArtistAdapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(SpotifyArtistPlaylistActivity.this, "Result Not Found: ", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(SpotifyArtistPlaylistActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<SpotifyArtistModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SpotifyArtistPlaylistActivity.this,"Error: " +t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }
}
