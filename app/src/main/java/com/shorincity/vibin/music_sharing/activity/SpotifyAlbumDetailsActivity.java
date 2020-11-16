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
import com.shorincity.vibin.music_sharing.model.SpotifyAlbumsDetailModel;
import com.shorincity.vibin.music_sharing.model.SpotifyArtistModel;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.spotify;
import com.shorincity.vibin.music_sharing.adapters.SpotifyCustomTrackModel;
import com.shorincity.vibin.music_sharing.adapters.SpotifyTrackAdapter;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.spotify_files.SpotifySongsPlayerActivity;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Spotify Detail Screen of an album
public class SpotifyAlbumDetailsActivity extends AppCompatActivity {

    private static String TAG = SpotifyAlbumDetailsActivity.class.getName();
    private ImageView channelBannerIv;
    private TextView channelTitleTv;
    private TextView channelDetailTv, releaseDateTv;
    private String id, channelBannerUrl, titleStr;
    private ArrayList<SpotifyCustomTrackModel> trackList;
    private RecyclerView channelPlayListRv;
    private SpotifyTrackAdapter spotifyTrackAdapter;
    private ProgressBar progressBar;
    private ArrayList<SpotifyArtistModel.Item> songList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_album_details);

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
            Glide.with(SpotifyAlbumDetailsActivity.this).load(channelBannerUrl).into(channelBannerIv);
        }catch (Exception e) {

        }
        callGetSpotifyAlbumDetails(id);
    }

    private void inItListeners() {
    }

    private void setPlayListAdapter() {

        trackList = new ArrayList<>();
        songList = new ArrayList<>();
        //channelPlayListRv.setHasFixedSize(true);
        channelPlayListRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        channelPlayListRv.setHasFixedSize(true);

        spotifyTrackAdapter = new SpotifyTrackAdapter(this, trackList);
        spotifyTrackAdapter.setCustomItemClickListener(new SpotifyTrackAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                Intent a=new Intent(SpotifyAlbumDetailsActivity.this, SpotifySongsPlayerActivity.class);
                a.putExtra("uri",songList.get(position).getUri());
                a.putExtra("image",songList.get(position).getImages().get(0).getUrl());
                a.putExtra("title",songList.get(position).getName());
                a.putExtra("songIndex",position);
                a.putExtra("SongsList",songList);
                startActivity(a);
            }
        });
        channelPlayListRv.setAdapter(spotifyTrackAdapter);
    }


    public void callGetSpotifyAlbumDetails(final String artistId) {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getSpotifydata();

        Logging.d("SpotifyAlbums Tk:"+ spotify.spotifyAccessToken);
        Call<SpotifyAlbumsDetailModel> callback = dataAPI.getSpotifyAlbumDetail("Bearer "+ spotify.spotifyAccessToken, artistId);
        callback.enqueue(new Callback<SpotifyAlbumsDetailModel>() {
            @Override
            public void onResponse(Call<SpotifyAlbumsDetailModel> call, Response<SpotifyAlbumsDetailModel> response) {
                progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {

                    Logging.d("SpotifyAlbums res:"+new Gson().toJson(response));

                    if(response.body().getTracks() != null
                            && response.body().getTracks().getItems().size() > 0 ) {

                        ArrayList<String> songName = new ArrayList<>();

                        //channelDetailTv.setText("By "+response.body().getArtists().get(0).getName());
                       // releaseDateTv.setText(getResources().getString(R.string.aired_on)+dateToString(stringToDate(response.body().getReleaseDate())));
                        trackList.clear();

                        // preparing Custom list
                        for(int i = 0; i< response.body().getTracks().getItems().size(); i++){

                            // Condition applied to prevent duplicate entry in list
                            if(!songName.contains(response.body().getTracks().getItems().get(i).getName())) {
                                SpotifyCustomTrackModel item = new SpotifyCustomTrackModel();
                                item.setId(response.body().getTracks().getItems().get(i).getId());
                                item.setTitle(response.body().getTracks().getItems().get(i).getName());
                                item.setSubtitle("");
                                item.setDuration(response.body().getTracks().getItems().get(i).getDurationMs());
                                item.setThumbnail("");

                                trackList.add(item);

                                // preparing song list to pass player screen
                                SpotifyArtistModel.Item songItem = new SpotifyArtistModel.Item();
                                songItem.setName(response.body().getTracks().getItems().get(i).getName());

                                SpotifyArtistModel.Image image = new SpotifyArtistModel.Image();
                                image.setUrl(channelBannerUrl);
                                ArrayList<SpotifyArtistModel.Image> images = new ArrayList<SpotifyArtistModel.Image>();
                                images.add(image);

                                songItem.setImages(images);
                                songItem.setId(response.body().getTracks().getItems().get(i).getId());
                                songItem.setUri(response.body().getTracks().getItems().get(i).getUri());
                                songItem.setReleaseDate(response.body().getReleaseDate());

                                songList.add(songItem);

                            }

                            songName.add(response.body().getTracks().getItems().get(i).getName());

                        }
                        spotifyTrackAdapter.notifyDataSetChanged();
                        //Toast.makeText(SpotifyAlbumDetailsActivity.this, "Result Received ", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(SpotifyAlbumDetailsActivity.this, "Result Not Found: ", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(SpotifyAlbumDetailsActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<SpotifyAlbumsDetailModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SpotifyAlbumDetailsActivity.this,"Error: " +t.getMessage(),Toast.LENGTH_LONG).show();
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
