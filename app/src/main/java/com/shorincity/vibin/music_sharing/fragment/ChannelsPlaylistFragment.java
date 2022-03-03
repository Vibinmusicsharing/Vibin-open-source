package com.shorincity.vibin.music_sharing.fragment;

import android.content.Context;
import android.os.Bundle;
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
import com.shorincity.vibin.music_sharing.adapters.YoutubeTrendingAdapter;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.model.YoutubeTrendingModel;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChannelsPlaylistFragment extends MyBaseFragment {
    private View view;
    private Context mContext;

    private static String TAG = ChannelsPlaylistFragment.class.getName();
    private ImageView channelBannerIv;
    private TextView channelTitleTv;
    private TextView channelDetailTv;
    private String channelId, channelBannerUrl;
    private YoutubeTrendingModel.Item youtubeChannelData;
    private ArrayList<YoutubeTrendingModel.Item> youtubeChannelPLayList;
    private RecyclerView channelPlayListRv;
    private YoutubeTrendingAdapter youtubeListAdapter;
    private ProgressBar progressBar;

    public static ChannelsPlaylistFragment getInstance(YoutubeTrendingModel.Item item, String channelId, String channelBannerUrl) {
        ChannelsPlaylistFragment fragment = new ChannelsPlaylistFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConstants.INTENT_YOUTUBE_CHANNEL_DATA, item);
        bundle.putString(AppConstants.INTENT_YOUTUBE_CHANNEL_ID, channelId);
        bundle.putString(AppConstants.INTENT_YOUTUBE_CHANNEL_BANNER_URL, channelBannerUrl);
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
        getIntentData(getArguments());

        inItViews();

        inItListeners();

    }

    private void getIntentData(Bundle arguments) {
        youtubeChannelData = (YoutubeTrendingModel.Item) arguments.getSerializable(AppConstants.INTENT_YOUTUBE_CHANNEL_DATA);
        channelId = arguments.getString(AppConstants.INTENT_YOUTUBE_CHANNEL_ID);
        channelBannerUrl = arguments.getString(AppConstants.INTENT_YOUTUBE_CHANNEL_BANNER_URL);
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

        setPlayListAdapter();

        if (!TextUtils.isEmpty(youtubeChannelData.getSnippet().getChannelTitle()))
            channelTitleTv.setText(youtubeChannelData.getSnippet().getChannelTitle());

        if (!TextUtils.isEmpty(youtubeChannelData.getSnippet().getDescription()))
            // channelDetailTv.setText(youtubeChannelData.getSnippet().getDescription());

            try {
                Glide.with(mContext).load(channelBannerUrl).into(channelBannerIv);
            } catch (Exception e) {

            }
        callgetYoutubeChannelPlatlistListAPI(channelId);
    }

    private void inItListeners() {
    }

    private void setPlayListAdapter() {

        youtubeChannelPLayList = new ArrayList<>();
        channelPlayListRv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        channelPlayListRv.setHasFixedSize(true);

        youtubeListAdapter = new YoutubeTrendingAdapter(mContext, youtubeChannelPLayList);
        youtubeListAdapter.setCustomItemClickListener(new YoutubeTrendingAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                try {
                    ArrayList<PlaylistDetailModel> playlist;
                    playlist = new ArrayList<>();
                    String defaultThumbnail = "";
                    for (int i = 0; i < youtubeChannelPLayList.size(); i++) {
                        if (youtubeChannelPLayList.get(i).getSnippet().getThumbnails().getHigh() != null) {
                            defaultThumbnail = youtubeChannelPLayList.get(i).getSnippet().getThumbnails().getHigh().getUrl();
                            break;
                        }
                    }
                    for (int i = 0; i < youtubeChannelPLayList.size(); i++) {
                        if (youtubeChannelPLayList.get(i).getSnippet().getThumbnails().getHigh() != null) {
                            String splitrl[] = youtubeChannelPLayList.get(i).getSnippet().getThumbnails().getHigh().getUrl().split("/");
                            String idvideo = splitrl[splitrl.length - 2];
                            playlist.add(new PlaylistDetailModel(
                                    youtubeChannelPLayList.get(i).getSnippet().getTitle(),
                                    youtubeChannelPLayList.get(i).getSnippet().getThumbnails().getHigh().getUrl(),
                                    idvideo,
                                    youtubeChannelPLayList.get(i).getSnippet().getSongDuration(), ""
                            ));
                        } else {
                            String splitrl[] = defaultThumbnail.split("/");
                            String idvideo = splitrl[splitrl.length - 2];
                            playlist.add(new PlaylistDetailModel(
                                    youtubeChannelPLayList.get(i).getSnippet().getTitle(),
                                    defaultThumbnail,
                                    idvideo,
                                    youtubeChannelPLayList.get(i).getSnippet().getSongDuration(), ""
                            ));
                        }
                    }
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", position);
                    bundle.putString("title", youtubeChannelPLayList.get(position).getSnippet().getTitle());
                    bundle.putString("description", youtubeChannelPLayList.get(position).getSnippet().getDescription());
                    if (youtubeChannelPLayList.get(position).getSnippet().getThumbnails().getHigh() != null) {
                        bundle.putString("thumbnail", youtubeChannelPLayList.get(position).getSnippet().getThumbnails().getHigh().getUrl());
                        String splitrl[] = youtubeChannelPLayList.get(position).getSnippet().getThumbnails().getHigh().getUrl().split("/");
                        String idvideo = splitrl[splitrl.length - 2];
                        bundle.putString("videoId", idvideo);
                    } else {
                        bundle.putString("thumbnail", defaultThumbnail);
                        String splitrl[] = defaultThumbnail.split("/");
                        String idvideo = splitrl[splitrl.length - 2];
                        bundle.putString("videoId", idvideo);
                    }

                    bundle.putSerializable("playlist", playlist);
                    onMusicPlay(bundle);

                    /*Intent intent = new Intent(mContext, PlayYoutubeVideoActivity.class);
                    intent.putExtra("data", bundle);
                    startActivity(intent);*/
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        channelPlayListRv.setAdapter(youtubeListAdapter);
    }

    // parse data from youtube api to list view
    private void callgetYoutubeChannelPlatlistListAPI(final String channelId) {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getYoutubeData();

        Call<YoutubeTrendingModel> callback = dataAPI.getYoutubeChannelsPlayList("snippet,contentDetails", channelId, AppConstants.YOUTUBE_KEY, "50");
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
                        Toast.makeText(mContext, "Result Not Found: ", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<YoutubeTrendingModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(mContext, getString(R.string.msg_network_failed), Toast.LENGTH_LONG).show();
            }
        });

    }
}
