package com.shorincity.vibin.music_sharing.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.youtube;
import com.shorincity.vibin.music_sharing.adapters.YoutubeChannelAdapter;
import com.shorincity.vibin.music_sharing.model.YoutubeTrendingModel;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YoutubeCountryFragment extends MyBaseFragment {
    private View view;
    private Context mContext;

    private String countryCode;
    private ArrayList<YoutubeTrendingModel.Item> youtubeUSTrendingList;
    private YoutubeChannelAdapter youtubeUSTrendingAdapter;
    private RecyclerView usTrendingListRv;
    private ProgressBar progressBar;
    private String popular_next_page_token = "";

    private static final String BUNDLE_COUNTRYCODE = "countryCode";

    public static YoutubeCountryFragment getInstance(String countryCode) {
        YoutubeCountryFragment fragment = new YoutubeCountryFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_COUNTRYCODE, countryCode);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_youtube_country, container, false);
        mContext = view.getContext();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        countryCode = getArguments().getString(BUNDLE_COUNTRYCODE);

        inItViews();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2, LinearLayoutManager.VERTICAL, false);
        usTrendingListRv.setLayoutManager(gridLayoutManager);
        setListAdapter();

        callGetYoutubeMostPopularUKListAPI(countryCode, popular_next_page_token, "0");
        usTrendingListRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = gridLayoutManager.getChildCount();
                int totalItemCount = gridLayoutManager.getItemCount();
                int firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition > 0) {
                    callGetYoutubeMostPopularUKListAPI(countryCode, popular_next_page_token, "1");
                }

            }
        });
    }

    private void inItViews() {
        progressBar = view.findViewById(R.id.progressbar);
        usTrendingListRv = view.findViewById(R.id.most_popular_rv);
    }

    private void setListAdapter() {

        youtubeUSTrendingList = new ArrayList<>();

        usTrendingListRv.setHasFixedSize(true);

        youtubeUSTrendingAdapter = new YoutubeChannelAdapter(mContext, youtubeUSTrendingList);
        youtubeUSTrendingAdapter.setCustomItemClickListener(new YoutubeChannelAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                YoutubeTrendingModel.Item currentItem = youtubeUSTrendingList.get(position);
                String idvideo = currentItem.getId();
                String title = currentItem.getSnippet().getTitle();
                String thumbnail = currentItem.getSnippet().getThumbnails().getHigh().getUrl();
                ChannelsPlaylistFragment fragment = ChannelsPlaylistFragment.getInstance(
                        currentItem, currentItem.getId(),
                        currentItem.getSnippet().getThumbnails().getHigh().getUrl()
                );
                ((youtube) getActivity()).onLoadFragment(fragment);
                /*Intent intent = new Intent(mContext, ChannelsPlaylistActivity.class);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_ID, currentItem.getSnippet().getChannelId());
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_BANNER_URL, currentItem.getSnippet().getThumbnails().getHigh().getUrl());
                intent.putExtra("title", title);
                intent.putExtra("thumbnail", thumbnail);
                intent.putExtra("videoId", idvideo);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_DATA, currentItem);
                startActivity(intent);*/
            }
        });
        usTrendingListRv.setAdapter(youtubeUSTrendingAdapter);
    }

    private void callGetYoutubeMostPopularUKListAPI(String countryCode, String popularnextpagetoken, String isShow) {
        if (!isShow.equalsIgnoreCase("1")) {
            progressBar.setVisibility(View.VISIBLE);
        }
        DataAPI dataAPI = RetrofitAPI.getYoutubeData();

        Call<YoutubeTrendingModel> callback = dataAPI.getYoutubeVideosList("snippet", "mostPopular", countryCode, "30", "10", AppConstants.YOUTUBE_KEY, popularnextpagetoken);
        callback.enqueue(new Callback<YoutubeTrendingModel>() {
            @Override
            public void onResponse(Call<YoutubeTrendingModel> call, Response<YoutubeTrendingModel> response) {
                progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    Log.i("YOUTUBE_TRENDING RESULT", response.toString());
                    popular_next_page_token = response.body().getNextPageToken();
                    if (response.body().getItems() != null
                            && response.body().getItems().size() > 0) {
                        youtubeUSTrendingList.addAll(response.body().getItems());
                        youtubeUSTrendingAdapter.notifyDataSetChanged();
                    } else {
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<YoutubeTrendingModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

}
