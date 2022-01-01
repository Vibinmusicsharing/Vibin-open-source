package com.shorincity.vibin.music_sharing.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.adapters.YoutubArtistPlayListAdapter;
import com.shorincity.vibin.music_sharing.model.HomeYoutubeModel;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.model.YoutubePlaylistItemModel;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChannelsPlaylistItemFragment extends MyBaseFragment {
    private View view;
    private Context mContext;

    private static String TAG = ChannelsPlaylistItemFragment.class.getName();
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

    private String mName;
    private String mThumbnail;

    private static final String BUNDLE_TITLE = "title";
    private static final String BUNDLE_THUMBNAIL = "thumbnail";

    public static ChannelsPlaylistItemFragment getInstance(
            HomeYoutubeModel.YoutubeCustomModel youtubeChannelData,
            String title, String mThumbnail) {
        ChannelsPlaylistItemFragment fragment = new ChannelsPlaylistItemFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConstants.INTENT_YOUTUBE_PLAYLIST_DATA, youtubeChannelData);
        bundle.putString(BUNDLE_TITLE, title);
        bundle.putString(BUNDLE_THUMBNAIL, mThumbnail);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_channels_playlist, container, false);
        mContext = view.getContext();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // function to fetch all the data passed by previous activity by intent
        getIntentData(getArguments());

        // Calling to find Activity's child view here
        inItViews();

        populateArtistInfo(getArguments());
        // setting all the adapters inside the method
        inItListeners();
    }

    private void getIntentData(Bundle bundle) {

        if (bundle.containsKey(AppConstants.INTENT_YOUTUBE_PLAYLIST_DATA) &&
                bundle.getSerializable(AppConstants.INTENT_YOUTUBE_PLAYLIST_DATA) instanceof HomeYoutubeModel.YoutubeCustomModel) {
            youtubeChannelData = (HomeYoutubeModel.YoutubeCustomModel) bundle.getSerializable(AppConstants.INTENT_YOUTUBE_PLAYLIST_DATA);
            if (youtubeChannelData != null) {
                String[] imgUrl = youtubeChannelData.getUrl().split("&");
                String[] videoIdArr = imgUrl[0].split("=");
                String[] playlistIdArr = imgUrl[1].split("=");

                videoId = videoIdArr[videoIdArr.length - 1];
                playlistId = playlistIdArr[playlistIdArr.length - 1];
            }
        }
    }

    private void populateArtistInfo(Bundle bundle) {
        if (bundle != null) {
            mName = bundle.getString(BUNDLE_TITLE);
            mThumbnail = bundle.getString(BUNDLE_THUMBNAIL);
        }
        Logging.d("mThumbnail:" + mThumbnail);
        channelTitleTv.setText(mName);

        if (!TextUtils.isEmpty(mThumbnail) && !mThumbnail.equalsIgnoreCase("THUMBNAIL_URI")) {

            Glide.with(mContext).load(mThumbnail).into(channelBannerIv);
            //.onLoadFailed(new ColorDrawable(mContext.getResources().getColor(R.color.light_gray)));
        } else
            channelBannerIv.setImageResource(R.drawable.music_placeholder);
    }

    private void inItViews() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;


        channelBannerIv = (ImageView) view.findViewById(R.id.channel_image);
        channelBannerIv.setMinimumHeight(width);
        channelTitleTv = (TextView) view.findViewById(R.id.tv_channel_name);
        channelDetailTv = (TextView) view.findViewById(R.id.tv_channel_details);
        channelPlayListRv = (RecyclerView) view.findViewById(R.id.channel_playlist_rv);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_circular);


        callgetYoutubePlatlistItemAPI(playlistId, videoId);
    }

    private void inItListeners() {
    }

    private void callgetYoutubePlatlistItemAPI(final String playlistId, String videoId) {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getYoutubeData();

        //https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&key= {API key} &maxResults=50 &playlistId = {id}

        Log.d("TEST", "playlistId: " + playlistId);


        //        Call<YoutubePlaylistItemModel> callback = dataAPI.getYoutubePlayListItem("snippet","20",playlistId,videoId,"AIzaSyDn7GZfot4NowEcGPzRYv7h80s7LUT_vcs");

        //https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&key=AIzaSyDn7GZfot4NowEcGPzRYv7h80s7LUT_vcs&maxResults=50&playlistId=PLldRXoeaXPNm2wL1iy2fBMc55c1T8ZLhp

        Call<YoutubePlaylistItemModel> callback = dataAPI.getYoutubePlayListItem("snippet", AppConstants.YOUTUBE_KEY, "50", playlistId);
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
                        Toast.makeText(mContext, "Result Not Found: ", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<YoutubePlaylistItemModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(mContext,  getString(R.string.msg_network_failed), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void setPlayListAdapter() {

        youtubeChannelPLayList = new ArrayList<>();
        channelPlayListRv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        channelPlayListRv.setHasFixedSize(true);
        channelPlayListRv.setNestedScrollingEnabled(true);


        if (playlistItemModel.getItems() != null) {


            youtubeListAdapter = new YoutubArtistPlayListAdapter(mContext, (ArrayList<YoutubePlaylistItemModel.Item>) playlistItemModel.getItems());

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
                        onMusicPlay(bundle);
/*                        Intent intent = new Intent(mContext, PlayYoutubeVideoActivity.class);
                        intent.putExtra("data", bundle);
                        startActivity(intent);*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            channelPlayListRv.setAdapter(youtubeListAdapter);
        }
    }

}
