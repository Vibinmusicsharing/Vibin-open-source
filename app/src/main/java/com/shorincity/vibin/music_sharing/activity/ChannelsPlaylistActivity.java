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
import com.shorincity.vibin.music_sharing.adapters.YoutubeTrendingAdapter;
import com.shorincity.vibin.music_sharing.model.Item;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.model.YoutubeTrendingModel;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.youtube_files.PlayYoutubeVideoActivity;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChannelsPlaylistActivity extends AppCompatActivity {

    private static String TAG = ChannelsPlaylistActivity.class.getName();
    private ImageView channelBannerIv;
    private TextView channelTitleTv;
    private TextView channelDetailTv;
    private String channelId, channelBannerUrl;
    private YoutubeTrendingModel.Item youtubeChannelData;
    private ArrayList<YoutubeTrendingModel.Item> youtubeChannelPLayList;
    private RecyclerView channelPlayListRv;
    private YoutubeTrendingAdapter youtubeListAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channels_playlist);

        getIntentData();

        inItViews();

        inItListeners();

    }

    private void getIntentData() {

        youtubeChannelData = (YoutubeTrendingModel.Item) getIntent().getSerializableExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_DATA);

        channelId = getIntent().getStringExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_ID);
        channelBannerUrl = getIntent().getStringExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_BANNER_URL);

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

        setPlayListAdapter();

        if (!TextUtils.isEmpty(youtubeChannelData.getSnippet().getChannelTitle()))
            channelTitleTv.setText(youtubeChannelData.getSnippet().getChannelTitle());

        if (!TextUtils.isEmpty(youtubeChannelData.getSnippet().getDescription()))
            // channelDetailTv.setText(youtubeChannelData.getSnippet().getDescription());

            try {
                Glide.with(ChannelsPlaylistActivity.this).load(channelBannerUrl).into(channelBannerIv);
            } catch (Exception e) {

            }
        callgetYoutubeChannelPlatlistListAPI(channelId);
    }

    private void inItListeners() {
    }

    private void setPlayListAdapter() {

        youtubeChannelPLayList = new ArrayList<>();
        channelPlayListRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        channelPlayListRv.setHasFixedSize(true);

        youtubeListAdapter = new YoutubeTrendingAdapter(ChannelsPlaylistActivity.this, youtubeChannelPLayList);
        youtubeListAdapter.setCustomItemClickListener(new YoutubeTrendingAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                try {
                    ArrayList<PlaylistDetailModel> playlist;
                    playlist = new ArrayList<>();
                    String defaultThumbnail = "";
                    for (int i = 0; i < youtubeChannelPLayList.size(); i++) {
                        if (youtubeChannelPLayList.get(i).getSnippet().getThumbnails().getMedium() != null) {
                            defaultThumbnail = youtubeChannelPLayList.get(i).getSnippet().getThumbnails().getMedium().getUrl();
                            break;
                        }
                    }
                    for (int i = 0; i < youtubeChannelPLayList.size(); i++) {
                        if (youtubeChannelPLayList.get(i).getSnippet().getThumbnails().getMedium() != null) {
                            String splitrl[] = youtubeChannelPLayList.get(i).getSnippet().getThumbnails().getMedium().getUrl().split("/");
                            String idvideo = splitrl[splitrl.length - 2];
                            playlist.add(new PlaylistDetailModel(
                                    youtubeChannelPLayList.get(i).getSnippet().getTitle(),
                                    youtubeChannelPLayList.get(i).getSnippet().getThumbnails().getMedium().getUrl(),
                                    idvideo
                            ));
                        } else {
                            String splitrl[] =defaultThumbnail.split("/");
                            String idvideo = splitrl[splitrl.length - 2];
                            playlist.add(new PlaylistDetailModel(
                                    youtubeChannelPLayList.get(i).getSnippet().getTitle(),
                                    defaultThumbnail,
                                    idvideo
                            ));
                        }
                    }
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", position);
                    bundle.putString("title", youtubeChannelPLayList.get(position).getSnippet().getTitle());
                    bundle.putString("description", youtubeChannelPLayList.get(position).getSnippet().getDescription());
                    if (youtubeChannelPLayList.get(position).getSnippet().getThumbnails().getMedium() != null) {
                        bundle.putString("thumbnail", youtubeChannelPLayList.get(position).getSnippet().getThumbnails().getMedium().getUrl());
                        String splitrl[] = youtubeChannelPLayList.get(position).getSnippet().getThumbnails().getMedium().getUrl().split("/");
                        String idvideo = splitrl[splitrl.length - 2];
                        bundle.putString("videoId", idvideo);
                    } else {
                        bundle.putString("thumbnail", defaultThumbnail);
                        String splitrl[] = defaultThumbnail.split("/");
                        String idvideo = splitrl[splitrl.length - 2];
                        bundle.putString("videoId", idvideo);
                    }

                    bundle.putSerializable("playlist", playlist);
                    bundle.putString("from","channel");
                    Intent intent = new Intent(ChannelsPlaylistActivity.this, PlayYoutubeVideoActivity.class);
                    intent.putExtra("data", bundle);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        channelPlayListRv.setAdapter(youtubeListAdapter);
    }


    // parse data from youtube api to list view
    public void callgetYoutubeChannelPlatlistListAPI(final String channelId) {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getYoutubeData();

        Call<YoutubeTrendingModel> callback = dataAPI.getYoutubeChannelsPlayList("snippet", channelId, AppConstants.YOUTUBE_KEY, "50");
        callback.enqueue(new Callback<YoutubeTrendingModel>() {
            @Override
            public void onResponse(Call<YoutubeTrendingModel> call, Response<YoutubeTrendingModel> response) {
                progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    Log.i("YOUTUBE_TRENDING RESULT", response.toString());

                    if (response.body().getItems() != null
                            && response.body().getItems().size() > 0) {
                        youtubeChannelPLayList.addAll(response.body().getItems());
                        youtubeListAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ChannelsPlaylistActivity.this, "Result Not Found: ", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ChannelsPlaylistActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<YoutubeTrendingModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ChannelsPlaylistActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
}
