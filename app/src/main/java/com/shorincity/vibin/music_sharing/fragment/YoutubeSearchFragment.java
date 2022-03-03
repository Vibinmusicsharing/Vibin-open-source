package com.shorincity.vibin.music_sharing.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.adapters.YoutubeSearchAdapter;
import com.shorincity.vibin.music_sharing.model.Item;
import com.shorincity.vibin.music_sharing.model.ModelData;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.model.lastfm.LastFMSearchResponse;
import com.shorincity.vibin.music_sharing.model.lastfm.Results;
import com.shorincity.vibin.music_sharing.model.lastfm.Track;
import com.shorincity.vibin.music_sharing.model.lastfm.Trackmatches;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// youtube search fragment
public class YoutubeSearchFragment extends MyBaseFragment {

    View view;
    Context mContext;
    EditText editTextsearch;
    Button searchEditText;
    ProgressBar progressBar;
    RecyclerView youtubeSearchRv;

    ArrayList<Item> youtubeSearchList;
    private YoutubeSearchAdapter youtubeSearchAdapter;


    EditText edtsearch;
    ImageView btnsearch;
    ArrayList<Item> mangitem;
    public static String idvideo;
    String title;
    String thumbnail;
    TextView noresults;

    TextView searchsomething;
    private ArrayList<Track> trackList;


    public YoutubeSearchFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_youtube_search1, container, false);
        mContext = view.getContext();
        trackList = new ArrayList<>();

        inItviews();

        String text = getArguments().getString("search");
        if (!text.equals("")) {

            edtsearch.setText(text);
            progressBar.setVisibility(View.VISIBLE);
            String tukhoa = text;
            //tukhoa =tukhoa.replace(" ","%20");
            callTrackSearchApi(tukhoa);
//            Docdulieu(tukhoa);
        }

        //edtsearch.setText("laung lanchi");
        if (edtsearch.getText().toString().isEmpty()) {
            searchsomething.setVisibility(View.VISIBLE);
        }

        edtsearch.setOnEditorActionListener(editorActionListener);
        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String tukhoa = edtsearch.getText().toString();
                //tukhoa =tukhoa.replace(" ","%20");
                callTrackSearchApi(tukhoa);
//                Docdulieu(tukhoa);
            }
        });
        setTrendingAdapter();

        return view;
    }

    private void setTrendingAdapter() {

        youtubeSearchList = new ArrayList<>();
        youtubeSearchRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        youtubeSearchRv.setHasFixedSize(true);

        youtubeSearchAdapter = new YoutubeSearchAdapter(getActivity(), trackList);
        youtubeSearchAdapter.setCustomItemClickListener((v, position) -> {
            try {
                ArrayList<PlaylistDetailModel> playlist;
                playlist = new ArrayList<>();
                for (int i = 0; i < trackList.size(); i++) {
                    playlist.add(new PlaylistDetailModel(
                            trackList.get(i).getName(),
                            trackList.get(i).getArtist(),
                            trackList.get(i).getModelData() != null && trackList.get(i).getModelData().getItems().size() > 0 ? trackList.get(i).getModelData().getItems().get(0).getSnippet().getThumbnails().getMedium().getUrl() : "",
                            trackList.get(i).getModelData() != null && trackList.get(i).getModelData().getItems().size() > 0 ? trackList.get(i).getModelData().getItems().get(0).getId().getVideoId() : ""
                    ));
                }
                Track currentItem = trackList.get(position);
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                bundle.putString("title", currentItem.getName());
                bundle.putString("description", "");
                bundle.putString("thumbnail", currentItem.getModelData() != null && currentItem.getModelData().getItems().size() > 0 ? currentItem.getModelData().getItems().get(0).getSnippet().getThumbnails().getMedium().getUrl() : (currentItem.getImage().size() > 0 ? currentItem.getImage().get(0).getText() : ""));
                bundle.putString("videoId", currentItem.getModelData() != null && currentItem.getModelData().getItems().size() > 0 ? currentItem.getModelData().getItems().get(0).getId().getVideoId() : "");
                bundle.putString("artist_name", currentItem.getArtist());
//                    bundle.putString("from", "channel");
                bundle.putParcelableArrayList("playlist", (ArrayList<? extends Parcelable>) playlist);
                onMusicPlay(bundle);

/*                    Intent intent = new Intent(getActivity(), PlayYoutubeVideoActivity.class);
                intent.putExtra("data", bundle);
                startActivity(intent);*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        youtubeSearchRv.setAdapter(youtubeSearchAdapter);

    }

    private void inItviews() {
        noresults = view.findViewById(R.id.noResultsy);
        progressBar = view.findViewById(R.id.progressbar);
        searchsomething = view.findViewById(R.id.searchidy);
        edtsearch = view.findViewById(R.id.edittextSearch);
        btnsearch = view.findViewById(R.id.ic_magnify);
        youtubeSearchRv = view.findViewById(R.id.youtube_search_rv);
    }


    private void callgetYoutubeSearchListAPI(String query) {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getYoutubeData();
        // videos?part=contentDetails&chart=mostPopular&regionCode=IN&maxResults=25&key=AIzaSyDn7GZfot4NowEcGPzRYv7h80s7LUT_vcs

        Call<ModelData> callback = dataAPI.getYoutubeSearchList("snippet", "mostPopular", query, "25", "track%20Cartist", AppConstants.YOUTUBE_KEY);
        callback.enqueue(new Callback<ModelData>() {
            @Override
            public void onResponse(Call<ModelData> call, Response<ModelData> response) {
                progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    Log.i("YOUTUBE_TRENDING RESULT", response.toString());

                    if (response.body().getItems() != null
                            && response.body().getItems().size() > 0) {
                        //youtubeSearchList.addAll(response.body().getItems());
                        //youtubeSearchAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "Result Found: " + response.body().getItems().size(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "Result Not Found: ", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ModelData> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(),  getString(R.string.msg_network_failed), Toast.LENGTH_LONG).show();
            }
        });

    }


    // parse data from youtube api to list view
    public void Docdulieu(String tukhoa) {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getYoutubeData();
        Call<ModelData> callback = dataAPI.getResurt("snippet", tukhoa, "50", "video", "10", "true", AppConstants.YOUTUBE_KEY);
        callback.enqueue(new Callback<ModelData>() {
            @Override
            public void onResponse(Call<ModelData> call, Response<ModelData> response) {
                ModelData modelData = response.body();
                if (response.body() != null) {
                    mangitem = (ArrayList<Item>) response.body().getItems();
                    if (mangitem.size() == 0) {
                        progressBar.setVisibility(View.GONE);
                        noresults.setVisibility(View.VISIBLE);
                    } else {
                        noresults.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        youtubeSearchList.clear();
                        youtubeSearchList.addAll(mangitem);
                        youtubeSearchAdapter.notifyDataSetChanged();


                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                youtubeSearchRv.scrollTo(0, 0);
                                youtubeSearchRv.smoothScrollToPosition(0);


                                try {
                                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(edtsearch.getWindowToken(), 0);
                                } catch (Exception e) {

                                }
                            });
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<ModelData> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                noresults.setVisibility(View.VISIBLE);
            }
        });

    }

    private void callTrackSearchApi(String tukhoa) {

        DataAPI dataAPI1 = RetrofitAPI.getLastFMData();
        dataAPI1.callTrackSearch(tukhoa, AppConstants.LAST_FM_KEY, "json", 5).enqueue(new Callback<LastFMSearchResponse>() {
            @Override
            public void onResponse(Call<LastFMSearchResponse> call, Response<LastFMSearchResponse> response) {
                LastFMSearchResponse response1 = response.body();
                if (response1 != null && response1.getResults() != null) {
                    Results results = response1.getResults();
                    if (results != null && results.getTrackmatches() != null) {
                        Trackmatches trackmatches = results.getTrackmatches();
                        trackList.clear();
                        trackList.addAll(trackmatches.getTrack());

                        if (trackList.size() > 0) {
                            noresults.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            youtubeSearchAdapter.notifyDataSetChanged();

                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    youtubeSearchRv.scrollTo(0, 0);
                                    youtubeSearchRv.smoothScrollToPosition(0);


                                    try {
                                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(edtsearch.getWindowToken(), 0);
                                    } catch (Exception e) {

                                    }
                                });
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            noresults.setVisibility(View.VISIBLE);
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        noresults.setVisibility(View.VISIBLE);
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    noresults.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<LastFMSearchResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                noresults.setVisibility(View.VISIBLE);
            }
        });
    }

    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (actionId) {
                case EditorInfo.IME_ACTION_SEARCH:

                    if (edtsearch.getText().toString().isEmpty()) {
                        progressBar.setVisibility(View.GONE);
                        searchsomething.setVisibility(View.VISIBLE);
                    } else {
                        noresults.setVisibility(View.GONE);
                        searchsomething.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        String tukhoa = edtsearch.getText().toString();
                        //tukhoa = tukhoa.replace(" ", "%20");
//                        Docdulieu(tukhoa);
                        callTrackSearchApi(tukhoa);
                    }
                    break;
            }
            return false;
        }

    };

}
