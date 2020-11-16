package com.shorincity.vibin.music_sharing.youtube_files;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shorincity.vibin.music_sharing.model.CountryModel;
import com.shorincity.vibin.music_sharing.model.HomeYoutubeModel;
import com.shorincity.vibin.music_sharing.model.YoutubeChannelModel;
import com.shorincity.vibin.music_sharing.model.YoutubeGuideCategoryModel;
import com.shorincity.vibin.music_sharing.model.YoutubeTrendingModel;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.UI.youtube;
import com.shorincity.vibin.music_sharing.activity.ChannelsPlaylistActivity;
import com.shorincity.vibin.music_sharing.activity.ChannelsPlaylistItemActivity;
import com.shorincity.vibin.music_sharing.activity.YoutubeCountryActivity;
import com.shorincity.vibin.music_sharing.adapters.CountryAdapter;
import com.shorincity.vibin.music_sharing.adapters.YoutubeArtistAdapter;
import com.shorincity.vibin.music_sharing.adapters.YoutubeArtistChannelAdapter;
import com.shorincity.vibin.music_sharing.adapters.YoutubeBollywoodAdapter;
import com.shorincity.vibin.music_sharing.adapters.YoutubeChannelAdapter;
import com.shorincity.vibin.music_sharing.adapters.YoutubeChartAdapter;
import com.shorincity.vibin.music_sharing.adapters.YoutubeGenerAdapter;
import com.shorincity.vibin.music_sharing.fragment.YoutubeSearchFragment;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// youtube search fragment
public class YoutubeHomeFragment extends Fragment {

    View view;
    Context mContext;
    EditText editTextsearch;
    Button searchEditText;
    ProgressBar progressBar;
    RecyclerView indianTrendingListRv, usTrendingListRv, ukTrendingListRv, channelListRv, tokyoListRv, canadaListRv, japanListRv, ausListRv, hindi_artists_rv;
    RecyclerView chartsRv, artistsRv, othersRv, bollywoodRv, panjabiRv, countryRv, generRv;

    ArrayList<YoutubeTrendingModel.Item> youtubeIndianTrendingList;
    ArrayList<YoutubeTrendingModel.Item> youtubeUKTrendingList;
    ArrayList<YoutubeTrendingModel.Item> youtubeUSTrendingList;
    ArrayList<YoutubeChannelModel.Item> youtubeCannelsList;
    ArrayList<YoutubeTrendingModel.Item> youtubeCanadaTrendingList;
    ArrayList<YoutubeTrendingModel.Item> youtubeJapanTrendingList;
    ArrayList<YoutubeTrendingModel.Item> youtubeAusTrendingList;
    YoutubeChannelAdapter youtubeIndianTrendingAdapter, youtubeUSTrendingAdapter, youtubeUKTrendingAdapter, youtubeCanadaTrendingAdapter, youtubeJapanTrendingAdapter, youtubeAusTrendingAdapter;
    YoutubeArtistChannelAdapter youtubeChannelAdapter;


    CountryAdapter countryAdapter;
    ArrayList<CountryModel> countryList;

    YoutubeChartAdapter chartsAdapter;
    YoutubeArtistAdapter artistAdapter;
    YoutubeArtistAdapter artistHindiAdapter;
    YoutubeArtistAdapter othersAdapter;
    YoutubeArtistAdapter panjabiAdapter;
    YoutubeBollywoodAdapter bollywoodAdapter;
    YoutubeGenerAdapter generAdapter;
    ArrayList<HomeYoutubeModel.YoutubeCustomModel> chartList;
    ArrayList<HomeYoutubeModel.YoutubeCustomModel> artistList;
    ArrayList<HomeYoutubeModel.YoutubeCustomModel> artistHindiList;
    ArrayList<HomeYoutubeModel.YoutubeCustomModel> othersList;
    ArrayList<HomeYoutubeModel.YoutubeCustomModel> panjabiList;
    ArrayList<HomeYoutubeModel.YoutubeCustomModel> generList;
    ArrayList<HomeYoutubeModel.YoutubeCustomModel> bollywoodList;

    public YoutubeHomeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_youtube_home, container, false);
            mContext = view.getContext();

            inItviews();

            editTextsearch = view.findViewById(R.id.edittextSearch);
            searchEditText = view.findViewById(R.id.button);
            editTextsearch.setOnEditorActionListener(editorActionListener);
            searchEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = editTextsearch.getText().toString().trim();
                    if (!TextUtils.isEmpty(text)) {
                        Intent intent = new Intent(mContext, YoutubeSearchActivity.class);
                        intent.putExtra("search", text);
                        startActivity(intent);
                    }{
                        Toast.makeText(mContext, "Field can't be empty", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            Button button = view.findViewById(R.id.buttonSearch);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(mContext, YoutubeSearchActivity.class));
                }
            });

            setTrendingAdapter();

            callGetYoutubeMostPopularIndianListAPI();

            callGetYoutubeHomePlaylistAPI();

            //callGetYoutubeMostPopularUKListAPI();

            //callGetYoutubeMostPopularUSListAPI();

            //callGetYoutubeMostPopularCanadaListAPI();

            //callGetYoutubeMostPopularJapanListAPI();

            //callGetYoutubeMostPopularAusListAPI();
        }
        return view;
    }

    private void setTrendingAdapter() {

        // Charts...............
        chartList = new ArrayList<>();
        chartsRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        chartsRv.setHasFixedSize(true);

        chartsAdapter = new YoutubeChartAdapter(getActivity(), chartList);
        chartsAdapter.setCustomItemClickListener(new YoutubeChartAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(getActivity(), ChannelsPlaylistItemActivity.class);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_PLAYLIST_DATA, chartList.get(position));
                intent.putExtra("title", chartList.get(position).getGenre());
                intent.putExtra("thumbnail", chartList.get(position).getThumbnail());
                startActivity(intent);
            }
        });
        chartsRv.setAdapter(chartsAdapter);


        // English Artist...................
        artistList = new ArrayList<>();
        artistsRv.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        artistsRv.setHasFixedSize(true);

        artistAdapter = new YoutubeArtistAdapter(getActivity(), artistList);
        artistAdapter.setCustomItemClickListener(new YoutubeArtistAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                Intent intent = new Intent(getActivity(), ChannelsPlaylistItemActivity.class);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_PLAYLIST_DATA, artistList.get(position));
                intent.putExtra("title", artistList.get(position).getGenre());
                intent.putExtra("thumbnail", artistList.get(position).getThumbnail());
                startActivity(intent);
            }
        });
        artistsRv.setAdapter(artistAdapter);

        // Hindi Artist...................
        artistHindiList = new ArrayList<>();
        hindi_artists_rv.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        hindi_artists_rv.setHasFixedSize(true);

        artistHindiAdapter = new YoutubeArtistAdapter(getActivity(), artistHindiList);
        artistHindiAdapter.setCustomItemClickListener(new YoutubeArtistAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                Intent intent = new Intent(getActivity(), ChannelsPlaylistItemActivity.class);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_PLAYLIST_DATA, artistHindiList.get(position));
                intent.putExtra("title", artistHindiList.get(position).getGenre());
                intent.putExtra("thumbnail", artistHindiList.get(position).getThumbnail());
                startActivity(intent);
            }
        });
        hindi_artists_rv.setAdapter(artistHindiAdapter);

        // Panjabi...................
        panjabiList = new ArrayList<>();
        panjabiRv.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        panjabiRv.setHasFixedSize(true);

        panjabiAdapter = new YoutubeArtistAdapter(getActivity(), panjabiList);
        panjabiAdapter.setCustomItemClickListener(new YoutubeArtistAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(getActivity(), ChannelsPlaylistItemActivity.class);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_PLAYLIST_DATA, panjabiList.get(position));
                intent.putExtra("title", panjabiList.get(position).getGenre());
                intent.putExtra("thumbnail", panjabiList.get(position).getThumbnail());
                startActivity(intent);
            }
        });
        panjabiRv.setAdapter(panjabiAdapter);


        // Gener...................
        generList = new ArrayList<>();
        generRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        generRv.setHasFixedSize(true);

        generAdapter = new YoutubeGenerAdapter(getActivity(), generList);
        generAdapter.setCustomItemClickListener(new YoutubeGenerAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(getActivity(), ChannelsPlaylistItemActivity.class);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_PLAYLIST_DATA, generList.get(position));
                intent.putExtra("title", generList.get(position).getGenre());
                intent.putExtra("thumbnail", generList.get(position).getThumbnail());

                startActivity(intent);
            }
        });
        generRv.setAdapter(generAdapter);


        // Bollywood................
        bollywoodList = new ArrayList<>();
        bollywoodRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        bollywoodRv.setHasFixedSize(true);

        bollywoodAdapter = new YoutubeBollywoodAdapter(getActivity(), bollywoodList);
        bollywoodAdapter.setCustomItemClickListener(new YoutubeBollywoodAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(getActivity(), ChannelsPlaylistItemActivity.class);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_PLAYLIST_DATA, bollywoodList.get(position));
                intent.putExtra("title", bollywoodList.get(position).getGenre());
                intent.putExtra("thumbnail", bollywoodList.get(position).getThumbnail());
                startActivity(intent);
            }
        });
        bollywoodRv.setAdapter(bollywoodAdapter);

        // Others...................
        othersList = new ArrayList<>();
        othersRv.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.HORIZONTAL, false));
        othersRv.setHasFixedSize(true);

        othersAdapter = new YoutubeArtistAdapter(getActivity(), othersList);
        othersAdapter.setCustomItemClickListener(new YoutubeArtistAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                Intent intent = new Intent(getActivity(), ChannelsPlaylistItemActivity.class);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_PLAYLIST_DATA, othersList.get(position));
                intent.putExtra("title", othersList.get(position).getGenre());
                intent.putExtra("thumbnail", othersList.get(position).getThumbnail());
                startActivity(intent);
            }
        });
        othersRv.setAdapter(othersAdapter);


        // Country
        countryList = new ArrayList<>();
        countryList.add(new CountryModel("UK", "GB", R.drawable.united_kingdom_flag));
        countryList.add(new CountryModel("US", "US", R.drawable.united_states_flag));
        countryList.add(new CountryModel("Canada", "CA", R.drawable.canada_flag));
        countryList.add(new CountryModel("Japan", "JP", R.drawable.japan_flag));
        countryList.add(new CountryModel("Australia", "AU", R.drawable.australia_flag));
        countryRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        countryRv.setHasFixedSize(true);

        countryAdapter = new CountryAdapter(getActivity(), countryList);
        countryAdapter.setCustomItemClickListener(new CountryAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                Intent intent = new Intent(getActivity(), YoutubeCountryActivity.class);
                intent.putExtra("countryCode", countryList.get(position).getCountry_code());
                startActivity(intent);

            }
        });
        countryRv.setAdapter(countryAdapter);


        youtubeIndianTrendingList = new ArrayList<>();
        indianTrendingListRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        indianTrendingListRv.setHasFixedSize(true);

        youtubeIndianTrendingAdapter = new YoutubeChannelAdapter(getActivity(), youtubeIndianTrendingList);
        youtubeIndianTrendingAdapter.setCustomItemClickListener(new YoutubeChannelAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                YoutubeTrendingModel.Item currentItem = youtubeIndianTrendingList.get(position);
                String idvideo = currentItem.getId();
                String title = currentItem.getSnippet().getTitle();
                String thumbnail = currentItem.getSnippet().getThumbnails().getHigh().getUrl();
                Intent intent = new Intent(getActivity(), ChannelsPlaylistActivity.class);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_ID, currentItem.getSnippet().getChannelId());
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_BANNER_URL, currentItem.getSnippet().getThumbnails().getHigh().getUrl());
                intent.putExtra("title", title);
                intent.putExtra("thumbnail", thumbnail);
                intent.putExtra("videoId", idvideo);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_DATA, currentItem);
                startActivity(intent);
            }
        });
        indianTrendingListRv.setAdapter(youtubeIndianTrendingAdapter);


        youtubeUSTrendingList = new ArrayList<>();
        usTrendingListRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        usTrendingListRv.setHasFixedSize(true);

        youtubeUSTrendingAdapter = new YoutubeChannelAdapter(getActivity(), youtubeUSTrendingList);
        youtubeUSTrendingAdapter.setCustomItemClickListener(new YoutubeChannelAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                YoutubeTrendingModel.Item currentItem = youtubeUSTrendingList.get(position);
                String idvideo = currentItem.getId();
                String title = currentItem.getSnippet().getTitle();
                String thumbnail = currentItem.getSnippet().getThumbnails().getHigh().getUrl();
                Intent intent = new Intent(getActivity(), ChannelsPlaylistActivity.class);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_ID, currentItem.getSnippet().getChannelId());
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_BANNER_URL, currentItem.getSnippet().getThumbnails().getHigh().getUrl());
                intent.putExtra("title", title);
                intent.putExtra("thumbnail", thumbnail);
                intent.putExtra("videoId", idvideo);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_DATA, currentItem);
                startActivity(intent);
            }
        });
        usTrendingListRv.setAdapter(youtubeUSTrendingAdapter);


        youtubeUKTrendingList = new ArrayList<>();
        ukTrendingListRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        ukTrendingListRv.setHasFixedSize(true);

        youtubeUKTrendingAdapter = new YoutubeChannelAdapter(getActivity(), youtubeUKTrendingList);
        youtubeUKTrendingAdapter.setCustomItemClickListener(new YoutubeChannelAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                YoutubeTrendingModel.Item currentItem = youtubeUKTrendingList.get(position);
                String idvideo = currentItem.getId();
                String title = currentItem.getSnippet().getTitle();
                String thumbnail = currentItem.getSnippet().getThumbnails().getHigh().getUrl();
                Intent intent = new Intent(getActivity(), ChannelsPlaylistActivity.class);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_ID, currentItem.getSnippet().getChannelId());
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_BANNER_URL, currentItem.getSnippet().getThumbnails().getHigh().getUrl());
                intent.putExtra("title", title);
                intent.putExtra("thumbnail", thumbnail);
                intent.putExtra("videoId", idvideo);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_DATA, currentItem);
                startActivity(intent);
            }
        });
        ukTrendingListRv.setAdapter(youtubeUKTrendingAdapter);


        // indian trending
        youtubeIndianTrendingList = new ArrayList<>();
        indianTrendingListRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        indianTrendingListRv.setHasFixedSize(true);

        youtubeIndianTrendingAdapter = new YoutubeChannelAdapter(getActivity(), youtubeIndianTrendingList);
        youtubeIndianTrendingAdapter.setCustomItemClickListener(new YoutubeChannelAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                YoutubeTrendingModel.Item currentItem = youtubeIndianTrendingList.get(position);
                String idvideo = currentItem.getId();
                String title = currentItem.getSnippet().getTitle();
                String thumbnail = currentItem.getSnippet().getThumbnails().getHigh().getUrl();
                Intent intent = new Intent(getActivity(), ChannelsPlaylistActivity.class);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_ID, currentItem.getSnippet().getChannelId());
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_BANNER_URL, currentItem.getSnippet().getThumbnails().getHigh().getUrl());
                intent.putExtra("title", title);
                intent.putExtra("thumbnail", thumbnail);
                intent.putExtra("videoId", idvideo);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_DATA, currentItem);
                startActivity(intent);
            }
        });
        indianTrendingListRv.setAdapter(youtubeIndianTrendingAdapter);


        youtubeCannelsList = new ArrayList<>();
        channelListRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        channelListRv.setHasFixedSize(true);

        youtubeChannelAdapter = new YoutubeArtistChannelAdapter(getActivity(), youtubeCannelsList);
        youtubeChannelAdapter.setCustomItemClickListener(new YoutubeArtistChannelAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                YoutubeChannelModel.Item currentItem = youtubeCannelsList.get(position);
                String idvideo = currentItem.getId();
                String title = currentItem.getSnippet().getTitle();
                String thumbnail = currentItem.getSnippet().getThumbnails().getHigh().getUrl();
                Intent intent = new Intent(getActivity(), ChannelsPlaylistActivity.class);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_ID, currentItem.getId());
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_BANNER_URL, currentItem.getSnippet().getThumbnails().getHigh().getUrl());
                intent.putExtra("title", title);
                intent.putExtra("thumbnail", thumbnail);
                intent.putExtra("videoId", idvideo);
                //intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_DATA, currentItem);
                startActivity(intent);
            }
        });
        channelListRv.setAdapter(youtubeChannelAdapter);


        // Canada
        youtubeCanadaTrendingList = new ArrayList<>();
        canadaListRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        canadaListRv.setHasFixedSize(true);

        youtubeCanadaTrendingAdapter = new YoutubeChannelAdapter(getActivity(), youtubeCanadaTrendingList);
        youtubeCanadaTrendingAdapter.setCustomItemClickListener(new YoutubeChannelAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                YoutubeTrendingModel.Item currentItem = youtubeCanadaTrendingList.get(position);
                String idvideo = currentItem.getId();
                String title = currentItem.getSnippet().getTitle();
                String thumbnail = currentItem.getSnippet().getThumbnails().getHigh().getUrl();
                Intent intent = new Intent(getActivity(), ChannelsPlaylistActivity.class);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_ID, currentItem.getSnippet().getChannelId());
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_BANNER_URL, currentItem.getSnippet().getThumbnails().getHigh().getUrl());
                intent.putExtra("title", title);
                intent.putExtra("thumbnail", thumbnail);
                intent.putExtra("videoId", idvideo);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_DATA, currentItem);
                startActivity(intent);
            }
        });
        canadaListRv.setAdapter(youtubeCanadaTrendingAdapter);

        // Japan
        youtubeJapanTrendingList = new ArrayList<>();
        japanListRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        japanListRv.setHasFixedSize(true);

        youtubeJapanTrendingAdapter = new YoutubeChannelAdapter(getActivity(), youtubeJapanTrendingList);
        youtubeJapanTrendingAdapter.setCustomItemClickListener(new YoutubeChannelAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                YoutubeTrendingModel.Item currentItem = youtubeJapanTrendingList.get(position);
                String idvideo = currentItem.getId();
                String title = currentItem.getSnippet().getTitle();
                String thumbnail = currentItem.getSnippet().getThumbnails().getHigh().getUrl();
                Intent intent = new Intent(getActivity(), ChannelsPlaylistActivity.class);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_ID, currentItem.getSnippet().getChannelId());
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_BANNER_URL, currentItem.getSnippet().getThumbnails().getHigh().getUrl());
                intent.putExtra("title", title);
                intent.putExtra("thumbnail", thumbnail);
                intent.putExtra("videoId", idvideo);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_DATA, currentItem);
                startActivity(intent);
            }
        });
        japanListRv.setAdapter(youtubeJapanTrendingAdapter);


        // Australia
        youtubeAusTrendingList = new ArrayList<>();
        ausListRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        ausListRv.setHasFixedSize(true);

        youtubeAusTrendingAdapter = new YoutubeChannelAdapter(getActivity(), youtubeAusTrendingList);
        youtubeAusTrendingAdapter.setCustomItemClickListener(new YoutubeChannelAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                YoutubeTrendingModel.Item currentItem = youtubeAusTrendingList.get(position);
                String idvideo = currentItem.getId();
                String title = currentItem.getSnippet().getTitle();
                String thumbnail = currentItem.getSnippet().getThumbnails().getHigh().getUrl();
                Intent intent = new Intent(getActivity(), ChannelsPlaylistActivity.class);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_ID, currentItem.getSnippet().getChannelId());
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_BANNER_URL, currentItem.getSnippet().getThumbnails().getHigh().getUrl());
                intent.putExtra("title", title);
                intent.putExtra("thumbnail", thumbnail);
                intent.putExtra("videoId", idvideo);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_DATA, currentItem);
                startActivity(intent);
            }
        });
        ausListRv.setAdapter(youtubeAusTrendingAdapter);
    }

    private void inItviews() {
        progressBar = view.findViewById(R.id.progressbar);
        indianTrendingListRv = view.findViewById(R.id.most_popular_rv);
        usTrendingListRv = view.findViewById(R.id.us_most_popular_rv);
        ukTrendingListRv = view.findViewById(R.id.uk_most_popular_rv);
        tokyoListRv = view.findViewById(R.id.tokyo_most_popular_rv);
        canadaListRv = view.findViewById(R.id.canada_most_popular_rv);
        japanListRv = view.findViewById(R.id.japan_most_popular_rv);
        ausListRv = view.findViewById(R.id.aus_most_popular_rv);
        channelListRv = view.findViewById(R.id.category_rv);
        chartsRv = view.findViewById(R.id.charts_rv);
        artistsRv = view.findViewById(R.id.artists_rv);
        hindi_artists_rv = view.findViewById(R.id.hindi_artists_rv);
        generRv = view.findViewById(R.id.gener_rv);
        othersRv = view.findViewById(R.id.others_rv);
        bollywoodRv = view.findViewById(R.id.bollywood_rv);
        panjabiRv = view.findViewById(R.id.panjabi_rv);
        countryRv = view.findViewById(R.id.country_rv);
    }

    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (actionId) {
                case EditorInfo.IME_ACTION_SEARCH:
                    String text = editTextsearch.getText().toString();
                    if (!TextUtils.isEmpty(text)) {
                        YoutubeSearchFragment youtubeSearchFragment = new YoutubeSearchFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("search", text);
                        youtubeSearchFragment.setArguments(bundle);

                        replaceFragment(youtubeSearchFragment);
                    }else {
                        Toast.makeText(mContext, "", Toast.LENGTH_SHORT).show();
                    }
            }
            return false;
        }
    };

    // parse data from youtube api to list view
    public void callGetYoutubeMostPopularIndianListAPI() {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getYoutubeData();

        Call<YoutubeTrendingModel> callback = dataAPI.getYoutubeVideosList("snippet", "mostPopular", "IN", "25", "10", "AIzaSyDn7GZfot4NowEcGPzRYv7h80s7LUT_vcs");
        callback.enqueue(new Callback<YoutubeTrendingModel>() {
            @Override
            public void onResponse(Call<YoutubeTrendingModel> call, Response<YoutubeTrendingModel> response) {
                progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    Log.i("YOUTUBE_TRENDING RESULT", response.toString());

                    if (response.body().getItems() != null
                            && response.body().getItems().size() > 0) {
                        youtubeIndianTrendingList.addAll(response.body().getItems());
                        youtubeIndianTrendingAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), "Result Not Found: ", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<YoutubeTrendingModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    HomeYoutubeModel homeYoutubeModel;

    public void callGetYoutubeHomePlaylistAPI() {
        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);


        Call<HomeYoutubeModel> callback = dataAPI.getYoutubePlaylistAtHome(token, AppConstants.YOUTUBE);
        callback.enqueue(new Callback<HomeYoutubeModel>() {
            @Override
            public void onResponse(Call<HomeYoutubeModel> call, Response<HomeYoutubeModel> response) {
                progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    Log.i("SPOTIFY TRENDING RESULT", response.toString());

                    homeYoutubeModel = (HomeYoutubeModel) response.body();

                    if (homeYoutubeModel != null) {

                        if (homeYoutubeModel.getCharts() != null
                                && homeYoutubeModel.getCharts().size() > 0) {

                            chartList.addAll(homeYoutubeModel.getCharts());
                            chartsAdapter.notifyDataSetChanged();

                        }

                        if (homeYoutubeModel.getArtists() != null
                                && homeYoutubeModel.getArtists().size() > 0) {


                            for (int i = 0; i < homeYoutubeModel.getArtists().size(); i++) {
                                if (homeYoutubeModel.getArtists().get(i).getLanguage().equalsIgnoreCase("punjabi"))
                                    panjabiList.add(homeYoutubeModel.getArtists().get(i));
                                else if (homeYoutubeModel.getArtists().get(i).getLanguage().equalsIgnoreCase("English")) {
                                    artistList.add(homeYoutubeModel.getArtists().get(i));
                                }else{
                                    artistHindiList.add(homeYoutubeModel.getArtists().get(i));
                                }
                            }
                            artistAdapter.notifyDataSetChanged();
                            artistHindiAdapter.notifyDataSetChanged();
                            panjabiAdapter.notifyDataSetChanged();
                        }

                        if (homeYoutubeModel.getEnglishHits() != null
                                && homeYoutubeModel.getEnglishHits().size() > 0) {

                            generList.addAll(homeYoutubeModel.getEnglishHits());
                            generAdapter.notifyDataSetChanged();

                        }

                        if (homeYoutubeModel.getBollywood() != null
                                && homeYoutubeModel.getBollywood().size() > 0) {

                            bollywoodList.addAll(homeYoutubeModel.getBollywood());
                            bollywoodAdapter.notifyDataSetChanged();

                        }

                        if (homeYoutubeModel.getOthers() != null
                                && homeYoutubeModel.getOthers().size() > 0) {

                            othersList.addAll(homeYoutubeModel.getOthers());
                            othersList.addAll(homeYoutubeModel.getPunjabi());
                            othersAdapter.notifyDataSetChanged();

                        }

                    } else {
                        Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<HomeYoutubeModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }


    public void callGetYoutubeMostPopularUSListAPI() {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getYoutubeData();

        Call<YoutubeTrendingModel> callback = dataAPI.getYoutubeVideosList("snippet", "mostPopular", "US", "25", "10", "AIzaSyDn7GZfot4NowEcGPzRYv7h80s7LUT_vcs");
        callback.enqueue(new Callback<YoutubeTrendingModel>() {
            @Override
            public void onResponse(Call<YoutubeTrendingModel> call, Response<YoutubeTrendingModel> response) {
                progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    Log.i("YOUTUBE_TRENDING RESULT", response.toString());

                    if (response.body().getItems() != null
                            && response.body().getItems().size() > 0) {
                        youtubeUSTrendingList.addAll(response.body().getItems());
                        youtubeUSTrendingAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), "Result Not Found: ", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<YoutubeTrendingModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void callGetYoutubeMostPopularUKListAPI() {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getYoutubeData();

        Call<YoutubeTrendingModel> callback = dataAPI.getYoutubeVideosList("snippet", "mostPopular", "GB", "25", "10", "AIzaSyDn7GZfot4NowEcGPzRYv7h80s7LUT_vcs");
        callback.enqueue(new Callback<YoutubeTrendingModel>() {
            @Override
            public void onResponse(Call<YoutubeTrendingModel> call, Response<YoutubeTrendingModel> response) {
                progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    Log.i("YOUTUBE_TRENDING RESULT", response.toString());

                    if (response.body().getItems() != null
                            && response.body().getItems().size() > 0) {
                        youtubeUKTrendingList.addAll(response.body().getItems());
                        youtubeUKTrendingAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), "Result Not Found: ", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<YoutubeTrendingModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void callGetYoutubeMostPopularCanadaListAPI() {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getYoutubeData();

        Call<YoutubeTrendingModel> callback = dataAPI.getYoutubeVideosList("snippet", "mostPopular", "CA", "25", "10", "AIzaSyDn7GZfot4NowEcGPzRYv7h80s7LUT_vcs");
        callback.enqueue(new Callback<YoutubeTrendingModel>() {
            @Override
            public void onResponse(Call<YoutubeTrendingModel> call, Response<YoutubeTrendingModel> response) {
                progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    Log.i("YOUTUBE_TRENDING RESULT", response.toString());

                    if (response.body().getItems() != null
                            && response.body().getItems().size() > 0) {
                        youtubeCanadaTrendingList.addAll(response.body().getItems());
                        youtubeCanadaTrendingAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), "Result Not Found: ", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<YoutubeTrendingModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void callGetYoutubeMostPopularJapanListAPI() {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getYoutubeData();

        Call<YoutubeTrendingModel> callback = dataAPI.getYoutubeVideosList("snippet", "mostPopular", "JP", "25", "10", "AIzaSyDn7GZfot4NowEcGPzRYv7h80s7LUT_vcs");
        callback.enqueue(new Callback<YoutubeTrendingModel>() {
            @Override
            public void onResponse(Call<YoutubeTrendingModel> call, Response<YoutubeTrendingModel> response) {
                progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    Log.i("YOUTUBE_TRENDING RESULT", response.toString());

                    if (response.body().getItems() != null
                            && response.body().getItems().size() > 0) {
                        youtubeJapanTrendingList.addAll(response.body().getItems());
                        youtubeJapanTrendingAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), "Result Not Found: ", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<YoutubeTrendingModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void callGetYoutubeMostPopularAusListAPI() {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getYoutubeData();

        Call<YoutubeTrendingModel> callback = dataAPI.getYoutubeVideosList("snippet", "mostPopular", "AU", "25", "10", "AIzaSyDn7GZfot4NowEcGPzRYv7h80s7LUT_vcs");
        callback.enqueue(new Callback<YoutubeTrendingModel>() {
            @Override
            public void onResponse(Call<YoutubeTrendingModel> call, Response<YoutubeTrendingModel> response) {
                progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    Log.i("YOUTUBE_TRENDING RESULT", response.toString());

                    if (response.body().getItems() != null
                            && response.body().getItems().size() > 0) {
                        youtubeAusTrendingList.addAll(response.body().getItems());
                        youtubeAusTrendingAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), "Result Not Found: ", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<YoutubeTrendingModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    public void callGetGuideCategoryAPI() {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getYoutubeData();

        Call<YoutubeGuideCategoryModel> callback = dataAPI.getYoutubeGuideCategoryModel("snippet", "IN", "AIzaSyDn7GZfot4NowEcGPzRYv7h80s7LUT_vcs");
        callback.enqueue(new Callback<YoutubeGuideCategoryModel>() {
            @Override
            public void onResponse(Call<YoutubeGuideCategoryModel> call, Response<YoutubeGuideCategoryModel> response) {
                progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    Log.i("YOUTUBE_TRENDING RESULT", response.toString());

                    if (response.body().getItems() != null
                            && response.body().getItems().size() > 0) {

                        YoutubeGuideCategoryModel youtubeGuideCategoryModel = response.body();
                        String musicCategoryId = "";

                        ArrayList<YoutubeGuideCategoryModel.Item> categoryList = new ArrayList<>();
                        categoryList.addAll(youtubeGuideCategoryModel.getItems());

                        for (int i = 0; i < categoryList.size(); i++) {
                            if (categoryList.get(i).getSnippet().getTitle().toLowerCase().equals("music")) {
                                musicCategoryId = categoryList.get(i).getId();
                                break;
                            }
                        }

                        if (!TextUtils.isEmpty(musicCategoryId))
                            callGetYoutubeChannelsAPI(musicCategoryId);
                    } else {
                        Toast.makeText(getActivity(), "Result Not Found: ", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<YoutubeGuideCategoryModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void callGetYoutubeChannelsAPI(String categoryId) {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getYoutubeData();

        Call<YoutubeChannelModel> callback = dataAPI.getYoutubeChannelList("snippet", categoryId, "25", "AIzaSyDn7GZfot4NowEcGPzRYv7h80s7LUT_vcs");
        callback.enqueue(new Callback<YoutubeChannelModel>() {
            @Override
            public void onResponse(Call<YoutubeChannelModel> call, Response<YoutubeChannelModel> response) {
                progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    Log.i("YOUTUBE_TRENDING RESULT", response.toString());

                    if (response.body().getItems() != null
                            && response.body().getItems().size() > 0) {

                        youtubeCannelsList.addAll(response.body().getItems());

                        youtubeChannelAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), "Result Not Found: ", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<YoutubeChannelModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    public void replaceFragment(Fragment someFragment) {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.youtube_frame, someFragment);
        transaction.addToBackStack(someFragment.getClass().getName());
        transaction.commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((youtube) getActivity()).mSlidingLayout.setSlidingEnable(false);
        ((youtube) getActivity()).mSlidingLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        ((youtube) getActivity()).mSlidingLayout.closePane();
    }


}
