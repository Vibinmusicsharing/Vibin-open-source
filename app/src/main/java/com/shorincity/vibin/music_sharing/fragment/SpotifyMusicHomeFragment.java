package com.shorincity.vibin.music_sharing.fragment;

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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shorincity.vibin.music_sharing.model.YoutubeTrendingModel;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.youtube;
import com.shorincity.vibin.music_sharing.activity.ChannelsPlaylistActivity;
import com.shorincity.vibin.music_sharing.adapters.YoutubeChannelAdapter;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.youtube_files.YoutubeSearchActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Unused
public class SpotifyMusicHomeFragment extends Fragment {

    View view;
    Context mContext;
    EditText editTextsearch;
    Button searchEditText;
    ProgressBar progressBar;
    RecyclerView trendingListRv;

    ArrayList<YoutubeTrendingModel.Item> youtubeTrendingList;
    YoutubeChannelAdapter youtubeChannelAdapter;
    public SpotifyMusicHomeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        if(view == null) {
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
                    String text = editTextsearch.getText().toString();
                    Intent intent = new Intent(mContext, YoutubeSearchActivity.class);
                    intent.putExtra("search", text);
                    startActivity(intent);
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

            callgetYoutubeArtistListAPI();
        }
        return view;
    }

    private void setTrendingAdapter() {

        youtubeTrendingList = new ArrayList<>();
        //trendingListRv.setHasFixedSize(true);
        trendingListRv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        trendingListRv.setHasFixedSize(true);

        youtubeChannelAdapter = new YoutubeChannelAdapter(getActivity(),youtubeTrendingList);
        youtubeChannelAdapter.setCustomItemClickListener(new YoutubeChannelAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                YoutubeTrendingModel.Item currentItem = youtubeTrendingList.get(position);
                String idvideo = currentItem.getId();
                String title = currentItem.getSnippet().getTitle();
                String thumbnail = currentItem.getSnippet().getThumbnails().getHigh().getUrl();
                //Intent intent = new Intent(getActivity(),PlayYoutubeVideoActivity.class);
                Intent intent = new Intent(getActivity(), ChannelsPlaylistActivity.class);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_ID,currentItem.getSnippet().getChannelId());
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_BANNER_URL,currentItem.getSnippet().getThumbnails().getHigh().getUrl());
                intent.putExtra("title",title);
                intent.putExtra("thumbnail",thumbnail);
                intent.putExtra("videoId",idvideo);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_CHANNEL_DATA, currentItem);
                startActivity(intent);
            }
        });
        trendingListRv.setAdapter(youtubeChannelAdapter);
    }

    private void inItviews() {
        progressBar = view.findViewById(R.id.progressbar);
        trendingListRv = view.findViewById(R.id.trending_rv);
    }

    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (actionId){
                case EditorInfo.IME_ACTION_SEARCH:
                    String text = editTextsearch.getText().toString();

                    YoutubeSearchFragment youtubeSearchFragment = new YoutubeSearchFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("search",text);
                    youtubeSearchFragment.setArguments(bundle);

                    replaceFragment(youtubeSearchFragment);
            }
            return false;
        }

    };

    // parse data from youtube api to list view
    public void callgetYoutubeArtistListAPI() {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getYoutubeData();

        Call<YoutubeTrendingModel> callback = dataAPI.getYoutubeVideosList("snippet","mostPopular","IN","25","10","AIzaSyDn7GZfot4NowEcGPzRYv7h80s7LUT_vcs");
        callback.enqueue(new Callback<YoutubeTrendingModel>() {
            @Override
            public void onResponse(Call<YoutubeTrendingModel> call, Response<YoutubeTrendingModel> response) {
                progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    Log.i("YOUTUBE_TRENDING RESULT", response.toString());

                    if(response.body().getItems() != null
                            && response.body().getItems().size() > 0 ) {
                        youtubeTrendingList.addAll(response.body().getItems());
                        youtubeChannelAdapter.notifyDataSetChanged();
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
                Toast.makeText(getActivity(),"Error: " +t.getMessage(),Toast.LENGTH_LONG).show();
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
        ((youtube)getActivity()).mSlidingLayout.setSlidingEnable(false);
        ((youtube)getActivity()).mSlidingLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        ((youtube)getActivity()).mSlidingLayout.closePane();
    }
}
