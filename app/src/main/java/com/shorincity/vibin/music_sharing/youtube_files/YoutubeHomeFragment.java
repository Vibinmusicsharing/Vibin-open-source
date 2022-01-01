package com.shorincity.vibin.music_sharing.youtube_files;

import android.app.Fragment;
import android.app.FragmentTransaction;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.UI.youtube;
import com.shorincity.vibin.music_sharing.adapters.CountryAdapter;
import com.shorincity.vibin.music_sharing.adapters.YoutubeArtistAdapter;
import com.shorincity.vibin.music_sharing.adapters.YoutubeArtistChannelAdapter;
import com.shorincity.vibin.music_sharing.adapters.YoutubeBollywoodAdapter;
import com.shorincity.vibin.music_sharing.adapters.YoutubeChannelAdapter;
import com.shorincity.vibin.music_sharing.adapters.YoutubeChartAdapter;
import com.shorincity.vibin.music_sharing.adapters.YoutubeGenerAdapter;
import com.shorincity.vibin.music_sharing.adapters.YoutubeOtherArtistAdapter;
import com.shorincity.vibin.music_sharing.fragment.ChannelsPlaylistFragment;
import com.shorincity.vibin.music_sharing.fragment.ChannelsPlaylistItemFragment;
import com.shorincity.vibin.music_sharing.fragment.MyBaseFragment;
import com.shorincity.vibin.music_sharing.fragment.YoutubeSearchFragment;
import com.shorincity.vibin.music_sharing.fragment.YoutubeViewMoreArtistFragment;
import com.shorincity.vibin.music_sharing.model.CountryModel;
import com.shorincity.vibin.music_sharing.model.HomeYoutubeModel;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.model.YoutubeChannelModel;
import com.shorincity.vibin.music_sharing.model.YoutubeTrendingModel;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// youtube search fragment
public class YoutubeHomeFragment extends MyBaseFragment {

    View view;
    Context mContext;
    EditText editTextsearch;
    ImageView searchEditText;
    ProgressBar progressBar;
    RecyclerView indianTrendingListRv, usTrendingListRv, ukTrendingListRv, channelListRv, tokyoListRv, canadaListRv, japanListRv, ausListRv, hindi_artists_rv;
    RecyclerView chartsRv, artistsRv, othersRv, bollywoodRv, panjabiRv, countryRv, generRv;
    RelativeLayout relative_main;
    ArrayList<YoutubeTrendingModel.Item> youtubeIndianTrendingList;
    ArrayList<YoutubeTrendingModel.Item> youtubeUKTrendingList;
    ArrayList<YoutubeTrendingModel.Item> youtubeUSTrendingList;
    ArrayList<YoutubeChannelModel.Item> youtubeCannelsList;
    ArrayList<YoutubeTrendingModel.Item> youtubeCanadaTrendingList;
    ArrayList<YoutubeTrendingModel.Item> youtubeJapanTrendingList;
    ArrayList<YoutubeTrendingModel.Item> youtubeAusTrendingList;
    YoutubeChannelAdapter youtubeIndianTrendingAdapter, youtubeUSTrendingAdapter, youtubeUKTrendingAdapter, youtubeCanadaTrendingAdapter, youtubeJapanTrendingAdapter, youtubeAusTrendingAdapter;
    YoutubeArtistChannelAdapter youtubeChannelAdapter;
    String popular_next_page_token = "";


    CountryAdapter countryAdapter;
    ArrayList<CountryModel> countryList;

    YoutubeChartAdapter chartsAdapter;
    YoutubeArtistAdapter artistAdapter;
    YoutubeArtistAdapter artistHindiAdapter;
    YoutubeArtistAdapter panjabiAdapter;
    YoutubeOtherArtistAdapter othersAdapter;
    YoutubeBollywoodAdapter bollywoodAdapter;
    YoutubeGenerAdapter generAdapter;
    ArrayList<HomeYoutubeModel.YoutubeCustomModel> chartList;
    ArrayList<HomeYoutubeModel.YoutubeCustomModel> artistList;
    ArrayList<HomeYoutubeModel.YoutubeCustomModel> tempartistList;
    ArrayList<HomeYoutubeModel.YoutubeCustomModel> artistHindiList;
    ArrayList<HomeYoutubeModel.YoutubeCustomModel> tempartistHindiList;
    ArrayList<HomeYoutubeModel.YoutubeCustomModel> othersList;
    ArrayList<HomeYoutubeModel.YoutubeCustomModel> panjabiList;
    ArrayList<HomeYoutubeModel.YoutubeCustomModel> temppanjabiList;
    ArrayList<HomeYoutubeModel.YoutubeCustomModel> generList;
    ArrayList<HomeYoutubeModel.YoutubeCustomModel> bollywoodList;

    Animation anim;
    View view_search;


    public YoutubeHomeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_youtube_home, container, false);
        mContext = view.getContext();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inItviews();
//            MiniPlayer.addMiniPlayer(mContext,relative_main );
        youtubeIndianTrendingList = new ArrayList<>();
        youtubeUKTrendingList = new ArrayList<>();
        youtubeUSTrendingList = new ArrayList<>();
        youtubeAusTrendingList = new ArrayList<>();
        youtubeCanadaTrendingList = new ArrayList<>();
        youtubeCannelsList = new ArrayList<>();
        youtubeJapanTrendingList = new ArrayList<>();
        editTextsearch = view.findViewById(R.id.edittextSearch);
        searchEditText = view.findViewById(R.id.ic_magnify);
        editTextsearch.setOnEditorActionListener(editorActionListener);
        searchEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editTextsearch.getText().toString().trim();
                if (!TextUtils.isEmpty(text)) {
                    YoutubeSearchFragment youtubeSearchFragment = new YoutubeSearchFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("search", text);
                    youtubeSearchFragment.setArguments(bundle);

                    replaceFragment(youtubeSearchFragment);
                } else {
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        indianTrendingListRv.setLayoutManager(linearLayoutManager);
        indianTrendingListRv.setHasFixedSize(true);

        callGetYoutubeMostPopularIndianListAPI(popular_next_page_token, "0");

        callGetYoutubeHomePlaylistAPI();

        indianTrendingListRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition > 0) {
                    callGetYoutubeMostPopularIndianListAPI(popular_next_page_token, "1");
                }

            }
        });

        callGetYoutubeMostPopularUKListAPI(popular_next_page_token, "0");

        callGetYoutubeMostPopularUSListAPI(popular_next_page_token, "0");

        callGetYoutubeMostPopularCanadaListAPI(popular_next_page_token, "0");

        callGetYoutubeMostPopularJapanListAPI(popular_next_page_token, "0");

        callGetYoutubeMostPopularAusListAPI(popular_next_page_token, "0");


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
                setChannelListItemFrg(chartList.get(position), chartList.get(position).getGenre(), chartList.get(position).getThumbnail());

              /*  Intent intent = new Intent(getActivity(), ChannelsPlaylistItemActivity.class);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_PLAYLIST_DATA, (Parcelable) chartList.get(position));
                intent.putExtra("title", chartList.get(position).getGenre());
                intent.putExtra("thumbnail", chartList.get(position).getThumbnail());
                startActivity(intent);*/
            }
        });
        chartsRv.setAdapter(chartsAdapter);


        // English Artist...................
        artistList = new ArrayList<>();
        tempartistList = new ArrayList<>();
        artistsRv.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        artistsRv.setHasFixedSize(true);

        artistAdapter = new YoutubeArtistAdapter(getActivity(), tempartistList, artistList);
        artistAdapter.setCustomItemClickListener(new YoutubeArtistAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                setChannelListItemFrg(tempartistList.get(position), tempartistList.get(position).getGenre(), tempartistList.get(position).getThumbnail());

               /* Intent intent = new Intent(getActivity(), ChannelsPlaylistItemActivity.class);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_PLAYLIST_DATA, (Parcelable) tempartistList.get(position));
                intent.putExtra("title", tempartistList.get(position).getGenre());
                intent.putExtra("thumbnail", tempartistList.get(position).getThumbnail());
                startActivity(intent);*/
            }

            @Override
            public void onMoreClick(String title, ArrayList<HomeYoutubeModel.YoutubeCustomModel> list) {
                YoutubeViewMoreArtistFragment fragment = YoutubeViewMoreArtistFragment.getInstance(title, list);
                ((youtube) getActivity()).onLoadFragment(fragment);
            }
        });
        artistsRv.setAdapter(artistAdapter);

        // Hindi Artist...................
        artistHindiList = new ArrayList<>();
        tempartistHindiList = new ArrayList<>();
        hindi_artists_rv.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        hindi_artists_rv.setHasFixedSize(true);

        artistHindiAdapter = new YoutubeArtistAdapter(getActivity(), tempartistHindiList, artistHindiList);
        artistHindiAdapter.setCustomItemClickListener(new YoutubeArtistAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                setChannelListItemFrg(tempartistHindiList.get(position), tempartistHindiList.get(position).getGenre(), tempartistHindiList.get(position).getThumbnail());

                /*Intent intent = new Intent(getActivity(), ChannelsPlaylistItemActivity.class);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_PLAYLIST_DATA, (Parcelable) tempartistHindiList.get(position));
                intent.putExtra("title", tempartistHindiList.get(position).getGenre());
                intent.putExtra("thumbnail", tempartistHindiList.get(position).getThumbnail());
                startActivity(intent);*/
            }

            @Override
            public void onMoreClick(String title, ArrayList<HomeYoutubeModel.YoutubeCustomModel> list) {
                YoutubeViewMoreArtistFragment fragment = YoutubeViewMoreArtistFragment.getInstance(title, list);
                ((youtube) getActivity()).onLoadFragment(fragment);
            }
        });
        hindi_artists_rv.setAdapter(artistHindiAdapter);

        // Panjabi...................
        panjabiList = new ArrayList<>();
        temppanjabiList = new ArrayList<>();
        panjabiRv.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        panjabiRv.setHasFixedSize(true);

        panjabiAdapter = new YoutubeArtistAdapter(getActivity(), temppanjabiList, panjabiList);
        panjabiAdapter.setCustomItemClickListener(new YoutubeArtistAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                setChannelListItemFrg(temppanjabiList.get(position), temppanjabiList.get(position).getGenre(), temppanjabiList.get(position).getThumbnail());

                /*Intent intent = new Intent(getActivity(), ChannelsPlaylistItemActivity.class);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_PLAYLIST_DATA, (Parcelable) temppanjabiList.get(position));
                intent.putExtra("title", temppanjabiList.get(position).getGenre());
                intent.putExtra("thumbnail", temppanjabiList.get(position).getThumbnail());
                startActivity(intent);*/
            }

            @Override
            public void onMoreClick(String title, ArrayList<HomeYoutubeModel.YoutubeCustomModel> list) {
                YoutubeViewMoreArtistFragment fragment = YoutubeViewMoreArtistFragment.getInstance(title, list);
                ((youtube) getActivity()).onLoadFragment(fragment);
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
                setChannelListItemFrg(generList.get(position), generList.get(position).getGenre(), generList.get(position).getThumbnail());

                /*Intent intent = new Intent(getActivity(), ChannelsPlaylistItemActivity.class);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_PLAYLIST_DATA, (Parcelable) generList.get(position));
                intent.putExtra("title", generList.get(position).getGenre());
                intent.putExtra("thumbnail", generList.get(position).getThumbnail());

                startActivity(intent);*/
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
                setChannelListItemFrg(bollywoodList.get(position), bollywoodList.get(position).getGenre(), bollywoodList.get(position).getThumbnail());

               /* Intent intent = new Intent(getActivity(), ChannelsPlaylistItemActivity.class);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_PLAYLIST_DATA, (Parcelable) bollywoodList.get(position));
                intent.putExtra("title", bollywoodList.get(position).getGenre());
                intent.putExtra("thumbnail", bollywoodList.get(position).getThumbnail());
                startActivity(intent);*/
            }
        });
        bollywoodRv.setAdapter(bollywoodAdapter);

        // Others...................
        othersList = new ArrayList<>();
        othersRv.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.HORIZONTAL, false));
        othersRv.setHasFixedSize(true);

        othersAdapter = new YoutubeOtherArtistAdapter(getActivity(), othersList);
        othersAdapter.setCustomItemClickListener(new YoutubeOtherArtistAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                setChannelListItemFrg(othersList.get(position), othersList.get(position).getGenre(), othersList.get(position).getThumbnail());

               /* Intent intent = new Intent(getActivity(), ChannelsPlaylistItemActivity.class);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_PLAYLIST_DATA, (Parcelable) othersList.get(position));
                intent.putExtra("title", othersList.get(position).getGenre());
                intent.putExtra("thumbnail", othersList.get(position).getThumbnail());
                startActivity(intent);*/
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
//                YoutubeCountryFragment fragment = YoutubeCountryFragment.getInstance(countryList.get(position).getCountry_code());
//                ((youtube) getActivity()).onLoadFragment(fragment);
               /* Intent intent = new Intent(getActivity(), YoutubeCountryActivity.class);
                intent.putExtra("countryCode", countryList.get(position).getCountry_code());
                startActivity(intent);*/
//                YoutubeTrendingModel.Item currentItem = youtubeUKTrendingList.get(position);
//                String idvideo = currentItem.getId();
//                String title = currentItem.getSnippet().getTitle();
//                String thumbnail = currentItem.getSnippet().getThumbnails().getHigh().getUrl();
//
//                Bundle bundle = new Bundle();
//                bundle.putInt("position", position);
//                bundle.putString("title", title);
//                bundle.putString("description", currentItem.getSnippet().getDescription());
//                bundle.putString("thumbnail", thumbnail);
//                bundle.putString("videoId", idvideo);
//                bundle.putSerializable("playlist",youtubeUKTrendingList);
//                onMusicPlay(bundle);


                if (position == 0) {
                    try {
                        ArrayList<PlaylistDetailModel> playlist;
                        playlist = new ArrayList<>();
                        String defaultThumbnail = "";
                        for (int i = 0; i < youtubeUKTrendingList.size(); i++) {
                            if (youtubeUKTrendingList.get(i).getSnippet().getThumbnails().getHigh() != null) {
                                defaultThumbnail = youtubeUKTrendingList.get(i).getSnippet().getThumbnails().getHigh().getUrl();
                                break;
                            }
                        }
                        for (int i = 0; i < youtubeUKTrendingList.size(); i++) {
                            if (youtubeUKTrendingList.get(i).getSnippet().getThumbnails().getHigh() != null) {
                                String splitrl[] = youtubeUKTrendingList.get(i).getSnippet().getThumbnails().getHigh().getUrl().split("/");
                                String idvideo = splitrl[splitrl.length - 2];
                                playlist.add(new PlaylistDetailModel(
                                        youtubeUKTrendingList.get(i).getSnippet().getTitle(),
                                        youtubeUKTrendingList.get(i).getSnippet().getThumbnails().getHigh().getUrl(),
                                        idvideo
                                ));
                            } else {
                                String splitrl[] = defaultThumbnail.split("/");
                                String idvideo = splitrl[splitrl.length - 2];
                                playlist.add(new PlaylistDetailModel(
                                        youtubeUKTrendingList.get(i).getSnippet().getTitle(),
                                        defaultThumbnail,
                                        idvideo
                                ));
                            }
                        }
                        Bundle bundle = new Bundle();
                        bundle.putInt("position", position);
                        bundle.putString("title", youtubeUKTrendingList.get(position).getSnippet().getTitle());
                        bundle.putString("description", youtubeUKTrendingList.get(position).getSnippet().getDescription());
                        if (youtubeUKTrendingList.get(position).getSnippet().getThumbnails().getHigh() != null) {
                            bundle.putString("thumbnail", youtubeUKTrendingList.get(position).getSnippet().getThumbnails().getHigh().getUrl());
                            String splitrl[] = youtubeUKTrendingList.get(position).getSnippet().getThumbnails().getHigh().getUrl().split("/");
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
                } else if (position == 1) {
                    try {
                        ArrayList<PlaylistDetailModel> playlist;
                        playlist = new ArrayList<>();
                        String defaultThumbnail = "";
                        for (int i = 0; i < youtubeUSTrendingList.size(); i++) {
                            if (youtubeUSTrendingList.get(i).getSnippet().getThumbnails().getHigh() != null) {
                                defaultThumbnail = youtubeUSTrendingList.get(i).getSnippet().getThumbnails().getHigh().getUrl();
                                break;
                            }
                        }
                        for (int i = 0; i < youtubeUSTrendingList.size(); i++) {
                            if (youtubeUSTrendingList.get(i).getSnippet().getThumbnails().getHigh() != null) {
                                String splitrl[] = youtubeUSTrendingList.get(i).getSnippet().getThumbnails().getHigh().getUrl().split("/");
                                String idvideo = splitrl[splitrl.length - 2];
                                playlist.add(new PlaylistDetailModel(
                                        youtubeUSTrendingList.get(i).getSnippet().getTitle(),
                                        youtubeUSTrendingList.get(i).getSnippet().getThumbnails().getHigh().getUrl(),
                                        idvideo
                                ));
                            } else {
                                String splitrl[] = defaultThumbnail.split("/");
                                String idvideo = splitrl[splitrl.length - 2];
                                playlist.add(new PlaylistDetailModel(
                                        youtubeUSTrendingList.get(i).getSnippet().getTitle(),
                                        defaultThumbnail,
                                        idvideo
                                ));
                            }
                        }
                        Bundle bundle = new Bundle();
                        bundle.putInt("position", position);
                        bundle.putString("title", youtubeUSTrendingList.get(position).getSnippet().getTitle());
                        bundle.putString("description", youtubeUSTrendingList.get(position).getSnippet().getDescription());
                        if (youtubeUSTrendingList.get(position).getSnippet().getThumbnails().getHigh() != null) {
                            bundle.putString("thumbnail", youtubeUSTrendingList.get(position).getSnippet().getThumbnails().getHigh().getUrl());
                            String splitrl[] = youtubeUSTrendingList.get(position).getSnippet().getThumbnails().getHigh().getUrl().split("/");
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
                } else if (position == 2) {
                    try {
                        ArrayList<PlaylistDetailModel> playlist;
                        playlist = new ArrayList<>();
                        String defaultThumbnail = "";
                        for (int i = 0; i < youtubeCanadaTrendingList.size(); i++) {
                            if (youtubeCanadaTrendingList.get(i).getSnippet().getThumbnails().getHigh() != null) {
                                defaultThumbnail = youtubeCanadaTrendingList.get(i).getSnippet().getThumbnails().getHigh().getUrl();
                                break;
                            }
                        }
                        for (int i = 0; i < youtubeCanadaTrendingList.size(); i++) {
                            if (youtubeCanadaTrendingList.get(i).getSnippet().getThumbnails().getHigh() != null) {
                                String splitrl[] = youtubeCanadaTrendingList.get(i).getSnippet().getThumbnails().getHigh().getUrl().split("/");
                                String idvideo = splitrl[splitrl.length - 2];
                                playlist.add(new PlaylistDetailModel(
                                        youtubeCanadaTrendingList.get(i).getSnippet().getTitle(),
                                        youtubeCanadaTrendingList.get(i).getSnippet().getThumbnails().getHigh().getUrl(),
                                        idvideo
                                ));
                            } else {
                                String splitrl[] = defaultThumbnail.split("/");
                                String idvideo = splitrl[splitrl.length - 2];
                                playlist.add(new PlaylistDetailModel(
                                        youtubeCanadaTrendingList.get(i).getSnippet().getTitle(),
                                        defaultThumbnail,
                                        idvideo
                                ));
                            }
                        }
                        Bundle bundle = new Bundle();
                        bundle.putInt("position", position);
                        bundle.putString("title", youtubeCanadaTrendingList.get(position).getSnippet().getTitle());
                        bundle.putString("description", youtubeCanadaTrendingList.get(position).getSnippet().getDescription());
                        if (youtubeCanadaTrendingList.get(position).getSnippet().getThumbnails().getHigh() != null) {
                            bundle.putString("thumbnail", youtubeCanadaTrendingList.get(position).getSnippet().getThumbnails().getHigh().getUrl());
                            String splitrl[] = youtubeCanadaTrendingList.get(position).getSnippet().getThumbnails().getHigh().getUrl().split("/");
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
                } else if (position == 3) {
                    try {
                        ArrayList<PlaylistDetailModel> playlist;
                        playlist = new ArrayList<>();
                        String defaultThumbnail = "";
                        for (int i = 0; i < youtubeJapanTrendingList.size(); i++) {
                            if (youtubeJapanTrendingList.get(i).getSnippet().getThumbnails().getHigh() != null) {
                                defaultThumbnail = youtubeJapanTrendingList.get(i).getSnippet().getThumbnails().getHigh().getUrl();
                                break;
                            }
                        }
                        for (int i = 0; i < youtubeJapanTrendingList.size(); i++) {
                            if (youtubeJapanTrendingList.get(i).getSnippet().getThumbnails().getHigh() != null) {
                                String splitrl[] = youtubeJapanTrendingList.get(i).getSnippet().getThumbnails().getHigh().getUrl().split("/");
                                String idvideo = splitrl[splitrl.length - 2];
                                playlist.add(new PlaylistDetailModel(
                                        youtubeJapanTrendingList.get(i).getSnippet().getTitle(),
                                        youtubeJapanTrendingList.get(i).getSnippet().getThumbnails().getHigh().getUrl(),
                                        idvideo
                                ));
                            } else {
                                String splitrl[] = defaultThumbnail.split("/");
                                String idvideo = splitrl[splitrl.length - 2];
                                playlist.add(new PlaylistDetailModel(
                                        youtubeJapanTrendingList.get(i).getSnippet().getTitle(),
                                        defaultThumbnail,
                                        idvideo
                                ));
                            }
                        }
                        Bundle bundle = new Bundle();
                        bundle.putInt("position", position);
                        bundle.putString("title", youtubeJapanTrendingList.get(position).getSnippet().getTitle());
                        bundle.putString("description", youtubeJapanTrendingList.get(position).getSnippet().getDescription());
                        if (youtubeJapanTrendingList.get(position).getSnippet().getThumbnails().getHigh() != null) {
                            bundle.putString("thumbnail", youtubeJapanTrendingList.get(position).getSnippet().getThumbnails().getHigh().getUrl());
                            String splitrl[] = youtubeJapanTrendingList.get(position).getSnippet().getThumbnails().getHigh().getUrl().split("/");
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
                } else if (position == 4) {
                    try {
                        ArrayList<PlaylistDetailModel> playlist;
                        playlist = new ArrayList<>();
                        String defaultThumbnail = "";
                        for (int i = 0; i < youtubeAusTrendingList.size(); i++) {
                            if (youtubeAusTrendingList.get(i).getSnippet().getThumbnails().getHigh() != null) {
                                defaultThumbnail = youtubeAusTrendingList.get(i).getSnippet().getThumbnails().getHigh().getUrl();
                                break;
                            }
                        }
                        for (int i = 0; i < youtubeAusTrendingList.size(); i++) {
                            if (youtubeAusTrendingList.get(i).getSnippet().getThumbnails().getHigh() != null) {
                                String splitrl[] = youtubeAusTrendingList.get(i).getSnippet().getThumbnails().getHigh().getUrl().split("/");
                                String idvideo = splitrl[splitrl.length - 2];
                                playlist.add(new PlaylistDetailModel(
                                        youtubeAusTrendingList.get(i).getSnippet().getTitle(),
                                        youtubeAusTrendingList.get(i).getSnippet().getThumbnails().getHigh().getUrl(),
                                        idvideo
                                ));
                            } else {
                                String splitrl[] = defaultThumbnail.split("/");
                                String idvideo = splitrl[splitrl.length - 2];
                                playlist.add(new PlaylistDetailModel(
                                        youtubeAusTrendingList.get(i).getSnippet().getTitle(),
                                        defaultThumbnail,
                                        idvideo
                                ));
                            }
                        }
                        Bundle bundle = new Bundle();
                        bundle.putInt("position", position);
                        bundle.putString("title", youtubeAusTrendingList.get(position).getSnippet().getTitle());
                        bundle.putString("description", youtubeAusTrendingList.get(position).getSnippet().getDescription());
                        if (youtubeAusTrendingList.get(position).getSnippet().getThumbnails().getHigh() != null) {
                            bundle.putString("thumbnail", youtubeAusTrendingList.get(position).getSnippet().getThumbnails().getHigh().getUrl());
                            String splitrl[] = youtubeAusTrendingList.get(position).getSnippet().getThumbnails().getHigh().getUrl().split("/");
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


            }

        });
        countryRv.setAdapter(countryAdapter);


        youtubeIndianTrendingAdapter = new YoutubeChannelAdapter(getActivity(), youtubeIndianTrendingList);
        youtubeIndianTrendingAdapter.setCustomItemClickListener(new YoutubeChannelAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                YoutubeTrendingModel.Item currentItem = youtubeIndianTrendingList.get(position);
                String idvideo = currentItem.getId();
                String title = currentItem.getSnippet().getTitle();
                String thumbnail = currentItem.getSnippet().getThumbnails().getMedium().getUrl();
//                Intent intent = new Intent(getActivity(), ChannelsPlaylistActivity.class);
//                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_ID, currentItem.getSnippet().getChannelId());
//                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_BANNER_URL, currentItem.getSnippet().getThumbnails().getMedium().getUrl());
//                intent.putExtra("title", title);
//                intent.putExtra("thumbnail", thumbnail);
//                intent.putExtra("videoId", idvideo);
//                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_DATA, currentItem);
//                startActivity(intent);


                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                bundle.putString("title", title);
                bundle.putString("description", currentItem.getSnippet().getDescription());
                bundle.putString("thumbnail", thumbnail);
                bundle.putString("videoId", idvideo);
                bundle.putString("from", "mostPopular");
                onMusicPlay(bundle);
//                Intent intent = new Intent(getActivity(), PlayYoutubeVideoActivity.class);
//                intent.putExtra("data", bundle);
//                startActivity(intent);
            }
        });
        indianTrendingListRv.setAdapter(youtubeIndianTrendingAdapter);


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
                setChannelPlaylistFrg(currentItem);
            }
        });
        usTrendingListRv.setAdapter(youtubeUSTrendingAdapter);


        ukTrendingListRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        ukTrendingListRv.setHasFixedSize(true);

        youtubeUKTrendingAdapter = new YoutubeChannelAdapter(getActivity(), youtubeUKTrendingList);
        youtubeUKTrendingAdapter.setCustomItemClickListener(new YoutubeChannelAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                YoutubeTrendingModel.Item currentItem = youtubeUSTrendingList.get(position);
                String idvideo = currentItem.getId();
                String title = currentItem.getSnippet().getTitle();
                String thumbnail = currentItem.getSnippet().getThumbnails().getHigh().getUrl();


                // setChannelPlaylistFrg(currentItem);
            }

        });
        ukTrendingListRv.setAdapter(youtubeUKTrendingAdapter);


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
                ChannelsPlaylistFragment fragment = ChannelsPlaylistFragment.getInstance(
                        null, currentItem.getId(),
                        currentItem.getSnippet().getThumbnails().getHigh().getUrl()
                );
                ((youtube) getActivity()).onLoadFragment(fragment);
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
                setChannelPlaylistFrg(currentItem);
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
                YoutubeTrendingModel.Item currentItem = youtubeCanadaTrendingList.get(position);
                String idvideo = currentItem.getId();
                String title = currentItem.getSnippet().getTitle();
                String thumbnail = currentItem.getSnippet().getThumbnails().getHigh().getUrl();
                setChannelPlaylistFrg(currentItem);
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
                YoutubeTrendingModel.Item currentItem = youtubeCanadaTrendingList.get(position);
                String idvideo = currentItem.getId();
                String title = currentItem.getSnippet().getTitle();
                String thumbnail = currentItem.getSnippet().getThumbnails().getHigh().getUrl();
                setChannelPlaylistFrg(currentItem);
            }
        });
        ausListRv.setAdapter(youtubeAusTrendingAdapter);
    }

    private void setChannelPlaylistFrg(YoutubeTrendingModel.Item currentItem) {
        ChannelsPlaylistFragment fragment = ChannelsPlaylistFragment.getInstance(
                currentItem, currentItem.getSnippet().getChannelId(),
                currentItem.getSnippet().getThumbnails().getMedium().getUrl()
        );
        ((youtube) getActivity()).onLoadFragment(fragment);
    }

    private void setChannelListItemFrg(HomeYoutubeModel.YoutubeCustomModel youtubeChannelData, String title, String mThumbnail) {
        ChannelsPlaylistItemFragment fragment = ChannelsPlaylistItemFragment.getInstance(
                youtubeChannelData, title, mThumbnail
        );
        ((youtube) getActivity()).onLoadFragment(fragment);
    }

    private void inItviews() {
        progressBar = view.findViewById(R.id.progressbar);
        indianTrendingListRv = view.findViewById(R.id.most_popular_rv);
//        relative_main = view.findViewById(R.id.relative_main);
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

        anim = AnimationUtils.loadAnimation(mContext, R.anim.scale_animation);
        view_search = (View) view.findViewById(R.id.second_view);
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
                    } else {
                        Toast.makeText(mContext, "", Toast.LENGTH_SHORT).show();
                    }
            }
            return false;
        }
    };

    // parse data from youtube api to list view
    public void callGetYoutubeMostPopularIndianListAPI(String popularNextPageToken, String isShowProgress) {
        if (!isShowProgress.equalsIgnoreCase("1")) {
            progressBar.setVisibility(View.VISIBLE);
        }
        DataAPI dataAPI = RetrofitAPI.getYoutubeData();

        Call<YoutubeTrendingModel> callback = dataAPI.getYoutubeVideosList("snippet", "mostPopular", "IN", "25", "10", AppConstants.YOUTUBE_KEY, popularNextPageToken);
        callback.enqueue(new Callback<YoutubeTrendingModel>() {
            @Override
            public void onResponse(Call<YoutubeTrendingModel> call, Response<YoutubeTrendingModel> response) {
                progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    Log.i("YOUTUBE_TRENDING RESULT", response.toString());
                    popular_next_page_token = response.body().getNextPageToken();
                    if (response.body().getItems() != null
                            && response.body().getItems().size() > 0) {
                        youtubeIndianTrendingList.addAll(response.body().getItems());
                        youtubeIndianTrendingAdapter.notifyDataSetChanged();
                    } else {
                        if (mContext != null)
                            Toast.makeText(mContext, "Result Not Found: ", Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (mContext != null)
                        Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<YoutubeTrendingModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                if (mContext != null)
                    Toast.makeText(mContext, getString(R.string.msg_network_failed), Toast.LENGTH_LONG).show();
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
                    if (isVisible()) {
                        if (homeYoutubeModel != null) {

                            if (homeYoutubeModel.getCharts() != null
                                    && homeYoutubeModel.getCharts().size() > 0) {

                                chartList.addAll(homeYoutubeModel.getCharts());
                                chartsAdapter.notifyDataSetChanged();

                            }

                            if (homeYoutubeModel.getArtists() != null
                                    && homeYoutubeModel.getArtists().size() > 0) {


                                for (int i = 0; i < homeYoutubeModel.getArtists().size(); i++) {
                                    if (homeYoutubeModel.getArtists().get(i).getLanguage().equalsIgnoreCase("punjabi")) {
                                        panjabiList.add(homeYoutubeModel.getArtists().get(i));
                                    } else if (homeYoutubeModel.getArtists().get(i).getLanguage().equalsIgnoreCase("English")) {
                                        artistList.add(homeYoutubeModel.getArtists().get(i));
                                    } else {
                                        artistHindiList.add(homeYoutubeModel.getArtists().get(i));
                                    }
                                }


                                if (artistList.size() > 10) {
                                    tempartistList.addAll(artistList.subList(0, 10));
                                    tempartistList.add(new HomeYoutubeModel.YoutubeCustomModel("", getString(R.string.artist), "", ""));
                                } else {
                                    tempartistList.addAll(artistList);
                                }

                                if (panjabiList.size() > 10) {
                                    temppanjabiList.addAll(panjabiList.subList(0, 10));
                                    temppanjabiList.add(new HomeYoutubeModel.YoutubeCustomModel("", getString(R.string.panjabi_artist), "", ""));
                                } else {
                                    temppanjabiList.addAll(panjabiList);
                                }

                                if (artistHindiList.size() > 10) {
                                    tempartistHindiList.addAll(artistHindiList.subList(0, 10));
                                    tempartistHindiList.add(new HomeYoutubeModel.YoutubeCustomModel("", getString(R.string.artist_hindi), "", ""));
                                } else {
                                    tempartistHindiList.addAll(artistHindiList);
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
                            Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<HomeYoutubeModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(mContext, getString(R.string.msg_network_failed), Toast.LENGTH_LONG).show();
            }
        });

    }


    public void callGetYoutubeMostPopularUSListAPI(String popularNextPageToken, String isShowProgress) {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getYoutubeData();

        Call<YoutubeTrendingModel> callback = dataAPI.getYoutubeVideosList("snippet", "mostPopular", "US", "25", "10", AppConstants.YOUTUBE_KEY, popularNextPageToken);
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
                if (mContext != null && isVisible())
                    Toast.makeText(mContext, getString(R.string.msg_network_failed), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void callGetYoutubeMostPopularUKListAPI(String popularNextPageToken, String isShowProgress) {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getYoutubeData();

        Call<YoutubeTrendingModel> callback = dataAPI.getYoutubeVideosList("snippet", "mostPopular", "GB", "25", "10", AppConstants.YOUTUBE_KEY, popularNextPageToken);
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
                if (mContext != null && isVisible())
                    Toast.makeText(mContext, getString(R.string.msg_network_failed), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void callGetYoutubeMostPopularCanadaListAPI(String popularNextPageToken, String isShowProgress) {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getYoutubeData();

        Call<YoutubeTrendingModel> callback = dataAPI.getYoutubeVideosList("snippet", "mostPopular", "CA", "25", "10", AppConstants.YOUTUBE_KEY, popularNextPageToken);
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
                Toast.makeText(getActivity(), getString(R.string.msg_network_failed), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void callGetYoutubeMostPopularJapanListAPI(String popularNextPageToken, String isShowProgress) {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getYoutubeData();

        Call<YoutubeTrendingModel> callback = dataAPI.getYoutubeVideosList("snippet", "mostPopular", "JP", "25", "10", AppConstants.YOUTUBE_KEY, popularNextPageToken);
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
                Toast.makeText(getActivity(), getString(R.string.msg_network_failed), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void callGetYoutubeMostPopularAusListAPI(String popularNextPageToken, String isShowProgress) {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getYoutubeData();

        Call<YoutubeTrendingModel> callback = dataAPI.getYoutubeVideosList("snippet", "mostPopular", "AU", "25", "10", AppConstants.YOUTUBE_KEY, popularNextPageToken);
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
                Toast.makeText(getActivity(), getString(R.string.msg_network_failed), Toast.LENGTH_LONG).show();
            }
        });
    }


//    public void callGetGuideCategoryAPI() {
//        progressBar.setVisibility(View.VISIBLE);
//        DataAPI dataAPI = RetrofitAPI.getYoutubeData();
//
//        Call<YoutubeGuideCategoryModel> callback = dataAPI.getYoutubeGuideCategoryModel("snippet", "IN", "AIzaSyA_CMrJoKWFhOT0FpBshCAEKx0ueoBbyok");
//        callback.enqueue(new Callback<YoutubeGuideCategoryModel>() {
//            @Override
//            public void onResponse(Call<YoutubeGuideCategoryModel> call, Response<YoutubeGuideCategoryModel> response) {
//                progressBar.setVisibility(View.GONE);
//                if (response != null && response.body() != null) {
//                    Log.i("YOUTUBE_TRENDING RESULT", response.toString());
//
//                    if (response.body().getItems() != null
//                            && response.body().getItems().size() > 0) {
//
//                        YoutubeGuideCategoryModel youtubeGuideCategoryModel = response.body();
//                        String musicCategoryId = "";
//
//                        ArrayList<YoutubeGuideCategoryModel.Item> categoryList = new ArrayList<>();
//                        categoryList.addAll(youtubeGuideCategoryModel.getItems());
//
//                        for (int i = 0; i < categoryList.size(); i++) {
//                            if (categoryList.get(i).getSnippet().getTitle().toLowerCase().equals("music")) {
//                                musicCategoryId = categoryList.get(i).getId();
//                                break;
//                            }
//                        }
//
//                        if (!TextUtils.isEmpty(musicCategoryId))
//                            callGetYoutubeChannelsAPI(musicCategoryId);
//                    } else {
//                        Toast.makeText(getActivity(), "Result Not Found: ", Toast.LENGTH_LONG).show();
//                    }
//                } else {
//                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<YoutubeGuideCategoryModel> call, Throwable t) {
//                progressBar.setVisibility(View.GONE);
//                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//    }

    public void callGetYoutubeChannelsAPI(String categoryId) {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getYoutubeData();

        Call<YoutubeChannelModel> callback = dataAPI.getYoutubeChannelList("snippet", categoryId, "25", AppConstants.YOUTUBE_KEY);
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
                        if (mContext != null)
                            Toast.makeText(mContext, "Result Not Found: ", Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (mContext != null)
                        Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<YoutubeChannelModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                if (mContext != null)
                    Toast.makeText(mContext, getString(R.string.msg_network_failed), Toast.LENGTH_LONG).show();
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
