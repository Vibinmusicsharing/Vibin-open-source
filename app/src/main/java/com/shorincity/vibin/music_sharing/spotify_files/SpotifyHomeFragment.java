package com.shorincity.vibin.music_sharing.spotify_files;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shorincity.vibin.music_sharing.model.SpotfySeveralArtistModel;
import com.shorincity.vibin.music_sharing.model.SpotifyCategoryModel;
import com.shorincity.vibin.music_sharing.model.SpotifyFeaturedPlaylistModel;
import com.shorincity.vibin.music_sharing.model.SpotifyNewReleaseModel;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.spotify;
import com.shorincity.vibin.music_sharing.activity.SpotifyAlbumDetailsActivity;
import com.shorincity.vibin.music_sharing.activity.SpotifyArtistPlaylistActivity;
import com.shorincity.vibin.music_sharing.activity.SpotifyCategoryPlaylistActivity;
import com.shorincity.vibin.music_sharing.activity.SpotifyPlaylistDetailsActivity;
import com.shorincity.vibin.music_sharing.adapters.SpotifyCategoryAdapter;
import com.shorincity.vibin.music_sharing.adapters.SpotifyFeaturedPlaylistAdapter;
import com.shorincity.vibin.music_sharing.adapters.SpotifyNewReleaseAdapter;
import com.shorincity.vibin.music_sharing.adapters.SpotifySaveralArtistsAdapter;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

// spotify search fragment
public class SpotifyHomeFragment extends Fragment {

    View view;
    Context mContext;
    EditText editTextsearch;
    Button searchEditText;
    ProgressBar progressBar;
    RecyclerView trendingListRv, categoryRv, featuredRv, artistRv;
    String url = "https://api.spotify.com/v1/browse/new-releases?country=IN&limit=10&offset=5";//"https://api.spotify.com/v1/artists//albums?album_type=SINGLE&offset=20&limit=10";

    SpotifyNewReleaseAdapter spotifyNewReleaseAdapter;
    ArrayList<SpotifyNewReleaseModel.Item> spotifyNewReleaseList;

    SpotifyCategoryAdapter spotifyCategoryAdapter;
    ArrayList<SpotifyCategoryModel.Item> spotifyCategoryList;

    SpotifyFeaturedPlaylistAdapter spotifyFeaturedPlaylistAdapter;
    ArrayList<SpotifyFeaturedPlaylistModel.Item> spotifyFeaturedList;

    SpotifySaveralArtistsAdapter spotifySaveralArtistsAdapter;
    ArrayList<SpotfySeveralArtistModel.Artist> spotifyArtistList;
    String artistsIDs;
    public SpotifyHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_spotify_search, container, false);
        mContext = view.getContext();

        inItviews();

        editTextsearch = view.findViewById(R.id.edittextSearch);
        searchEditText = view.findViewById(R.id.button);
        editTextsearch.setOnEditorActionListener(editorActionListener);
        searchEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editTextsearch.getText().toString();
                Intent intent = new Intent(mContext, SpotifySearchActivity.class);
                intent.putExtra("search",text);
                startActivity(intent);
            }
        });


        Button button = view.findViewById(R.id.buttonSearch);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, SpotifySearchActivity.class));
            }
        });

        setSpotifyNewReleaseAdapter();

        callGetSpotifyNewReleaseAPI();

        callGetSpotifyCategoryAPI();

        callGetSpotifyFeaturedAPI();

        return view;
    }

    private void inItviews() {
        progressBar = view.findViewById(R.id.progressbar);
        trendingListRv = view.findViewById(R.id.trending_rv);
        categoryRv = view.findViewById(R.id.category_rv);
        featuredRv = view.findViewById(R.id.featured_playlist_rv);
        artistRv = view.findViewById(R.id.spotify_artists_rv);
    }

    private void setSpotifyNewReleaseAdapter() {

        spotifyNewReleaseList = new ArrayList<>();
        //trendingListRv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        trendingListRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false));
        trendingListRv.setHasFixedSize(true);

        spotifyNewReleaseAdapter = new SpotifyNewReleaseAdapter(getActivity(),spotifyNewReleaseList);
        spotifyNewReleaseAdapter.setCustomItemClickListener(new SpotifyNewReleaseAdapter.CustomItemClickListener() {

            @Override
            public void onItemClick(View v, int position) {
                SpotifyNewReleaseModel.Item currentItem = spotifyNewReleaseList.get(position);
                Intent intent = new Intent(getActivity(), SpotifyAlbumDetailsActivity.class);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_ID,currentItem.getId());
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_BANNER_URL,currentItem.getImages().get(0).getUrl());
                intent.putExtra(AppConstants.INTENT_TITLE,currentItem.getName());
                intent.putExtra(AppConstants.INTENT_SPOTIFY_CHANNEL_DATA, currentItem);
                startActivity(intent);
            }
        });
        trendingListRv.setAdapter(spotifyNewReleaseAdapter);

        // Spotify Category Model
        spotifyCategoryList = new ArrayList<>();
        categoryRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false));
        categoryRv.setHasFixedSize(true);

        spotifyCategoryAdapter = new SpotifyCategoryAdapter(getActivity(),spotifyCategoryList);
        spotifyCategoryAdapter.setCustomItemClickListener(new SpotifyCategoryAdapter.CustomItemClickListener() {

            @Override
            public void onItemClick(View v, int position) {
                SpotifyCategoryModel.Item currentItem = spotifyCategoryList.get(position);
                Intent intent = new Intent(getActivity(), SpotifyCategoryPlaylistActivity.class);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_ID,currentItem.getId());
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_BANNER_URL,currentItem.getIcons().get(0).getUrl());
                intent.putExtra(AppConstants.INTENT_TITLE,currentItem.getName());
                startActivity(intent);
            }
        });
        categoryRv.setAdapter(spotifyCategoryAdapter);


        //Featured Playlist
        spotifyFeaturedList = new ArrayList<>();
        featuredRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false));
        featuredRv.setHasFixedSize(true);

        spotifyFeaturedPlaylistAdapter = new SpotifyFeaturedPlaylistAdapter(getActivity(),spotifyFeaturedList);
        spotifyFeaturedPlaylistAdapter.setCustomItemClickListener(new SpotifyFeaturedPlaylistAdapter.CustomItemClickListener() {

            @Override
            public void onItemClick(View v, int position) {
                SpotifyFeaturedPlaylistModel.Item currentItem = spotifyFeaturedList.get(position);
                Intent intent = new Intent(getActivity(), SpotifyPlaylistDetailsActivity.class);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_ID,currentItem.getId());
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_BANNER_URL,currentItem.getImages().get(0).getUrl());
                intent.putExtra(AppConstants.INTENT_TITLE,currentItem.getName());
                //intent.putExtra(AppConstants.INTENT_SPOTIFY_CHANNEL_DATA, currentItem);
                startActivity(intent);
            }
        });
        featuredRv.setAdapter(spotifyFeaturedPlaylistAdapter);

        // Artist Adapter
        spotifyArtistList = new ArrayList<>();
        artistRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false));
        artistRv.setHasFixedSize(true);

        spotifySaveralArtistsAdapter = new SpotifySaveralArtistsAdapter(getActivity(),spotifyArtistList);
        spotifySaveralArtistsAdapter.setCustomItemClickListener(new SpotifySaveralArtistsAdapter.CustomItemClickListener() {

            @Override
            public void onItemClick(View v, int position) {
                SpotfySeveralArtistModel.Artist currentItem = spotifyArtistList.get(position);
                Intent intent = new Intent(getActivity(), SpotifyArtistPlaylistActivity.class);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_ID,currentItem.getId());
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_BANNER_URL,currentItem.getImages().get(0).getUrl());
                intent.putExtra(AppConstants.INTENT_TITLE,currentItem.getName());
                startActivity(intent);
            }
        });
        artistRv.setAdapter(spotifySaveralArtistsAdapter);
    }


    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (actionId){
                case EditorInfo.IME_ACTION_SEARCH:
                    String text = editTextsearch.getText().toString();
                    Intent intent = new Intent(mContext, SpotifySearchActivity.class);
                    intent.putExtra("search",text);
                    startActivity(intent);
                    break;
            }
            return false;
        }

    };


    public void callGetSpotifyNewReleaseAPI() {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getSpotifydata();

        Logging.d("SpotifyNewReleaseA TK:"+spotify.spotifyAccessToken);

        Call<SpotifyNewReleaseModel> callback = dataAPI.getSpotifyNewReleases("Bearer "+ spotify.spotifyAccessToken);
        callback.enqueue(new Callback<SpotifyNewReleaseModel>() {
            @Override
            public void onResponse(Call<SpotifyNewReleaseModel> call, retrofit2.Response<SpotifyNewReleaseModel> response) {
                progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {

                   // Logging.d("SpotifyNewReleaseA res:"+new Gson().toJson(response));


                    SpotifyNewReleaseModel spotifyNewReleaseModel = (SpotifyNewReleaseModel) response.body();

                    if (spotifyNewReleaseModel != null && spotifyNewReleaseModel.getAlbums().getItems() != null) {
                       // Logging.d("SpotifyNewReleaseA res 11:"+new Gson().toJson(response));


                        if(spotifyNewReleaseModel.getAlbums().getItems() != null
                                && spotifyNewReleaseModel.getAlbums().getItems().size() > 0 ) {

                            spotifyNewReleaseList.addAll(spotifyNewReleaseModel.getAlbums().getItems());
                            spotifyNewReleaseAdapter.notifyDataSetChanged();

                            List<String> str = new ArrayList<>();
                            for(int i=0; i < spotifyNewReleaseModel.getAlbums().getItems().size(); i++) {
                                try {
                                    str.add(spotifyNewReleaseModel.getAlbums().getItems().get(i).getArtists().get(0).getId());
                                }catch (Exception e) {
                                    e.printStackTrace();
                                    Logging.d("SpotifyNewReleaseA id err:"+Log.getStackTraceString(e));
                                }
                            }

                           // Logging.d("SpotifyNewReleaseA str:"+str);
                            artistsIDs = android.text.TextUtils.join(",", str);
                           Logging.d("SpotifyNewReleaseA artistsIDs:"+artistsIDs);
                            callGetSeveralArtistsAPI(artistsIDs);

                        } else {
                            Toast.makeText(getActivity(), "Result Not Found: ", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<SpotifyNewReleaseModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    public void callGetSpotifyCategoryAPI() {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getSpotifydata();

        Call<SpotifyCategoryModel> callback = dataAPI.getSpotifyCategory("Bearer "+ spotify.spotifyAccessToken);
        callback.enqueue(new Callback<SpotifyCategoryModel>() {
            @Override
            public void onResponse(Call<SpotifyCategoryModel> call, retrofit2.Response<SpotifyCategoryModel> response) {
                progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    Log.i("SPOTIFY TRENDING RESULT", response.toString());

                    SpotifyCategoryModel spotifyCategoryModel = (SpotifyCategoryModel) response.body();

                    if (spotifyCategoryModel != null && spotifyCategoryModel.getCategories().getItems() != null) {
                        Log.i("Spotify Release RESULT", response.toString());

                        if(spotifyCategoryModel.getCategories().getItems() != null
                                && spotifyCategoryModel.getCategories().getItems().size() > 0 ) {

                            spotifyCategoryList.addAll(spotifyCategoryModel.getCategories().getItems());

                            spotifyCategoryAdapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(getActivity(), "Result Not Found: ", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<SpotifyCategoryModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    public void callGetSpotifyFeaturedAPI() {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getSpotifydata();

        Call<SpotifyFeaturedPlaylistModel> callback = dataAPI.getSpotifyFeaturedPlaylist("Bearer "+ spotify.spotifyAccessToken);
        callback.enqueue(new Callback<SpotifyFeaturedPlaylistModel>() {
            @Override
            public void onResponse(Call<SpotifyFeaturedPlaylistModel> call, retrofit2.Response<SpotifyFeaturedPlaylistModel> response) {
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
                            Toast.makeText(getActivity(), "Result Not Found: ", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<SpotifyFeaturedPlaylistModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    public void callGetSeveralArtistsAPI(String artistsIds) {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getSpotifydata();

        Call<SpotfySeveralArtistModel> callback = dataAPI.getSpotifySaveralArtist("Bearer "+ spotify.spotifyAccessToken,artistsIds);
        callback.enqueue(new Callback<SpotfySeveralArtistModel>() {
            @Override
            public void onResponse(Call<SpotfySeveralArtistModel> call, retrofit2.Response<SpotfySeveralArtistModel> response) {
                progressBar.setVisibility(View.GONE);

                if (response != null && response.body() != null) {
                    Log.i("SPOTIFY TRENDING RESULT", response.toString());

                    SpotfySeveralArtistModel spotfySeveralArtistModel = (SpotfySeveralArtistModel) response.body();

                    if (spotfySeveralArtistModel != null && spotfySeveralArtistModel.getArtists() != null && spotfySeveralArtistModel.getArtists().size() > 0)  {
                        Logging.dLong("SpotfySeveralArtist art"+new Gson().toJson(response.body()));


                            spotifyArtistList.addAll(spotfySeveralArtistModel.getArtists());
                            spotifySaveralArtistsAdapter.notifyDataSetChanged();
                        artistRv.setAdapter(spotifySaveralArtistsAdapter);
                    } else {
                        Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<SpotfySeveralArtistModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Logging.d("SpotfySeveralArtist err", Log.getStackTraceString(t));
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        ((spotify)getActivity()).mSlidingLayout.setSlidingEnable(false);
        ((spotify)getActivity()).mSlidingLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }
}
