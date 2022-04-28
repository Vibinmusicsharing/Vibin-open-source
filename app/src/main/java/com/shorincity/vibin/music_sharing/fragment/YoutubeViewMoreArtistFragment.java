package com.shorincity.vibin.music_sharing.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.youtube;
import com.shorincity.vibin.music_sharing.adapters.YoutubeArtistPerticulerAdapter;
import com.shorincity.vibin.music_sharing.model.HomeYoutubeModel;

import java.util.ArrayList;

public class YoutubeViewMoreArtistFragment extends MyBaseFragment {
    private View view;
    private Context mContext;

    private RecyclerView artist_rv_perticular;
    private TextView txt_artist_name;
    private ProgressBar progressbar;
    private String title;
    private ArrayList<HomeYoutubeModel.YoutubeCustomModel> playlist;
    private YoutubeArtistPerticulerAdapter youtubeArtistPerticulerAdapter;

    private static final String BUNDLE_TITLE = "title";
    private static final String BUNDLE_PLAYLIST = "playlist";

    public static YoutubeViewMoreArtistFragment getInstance(String title, ArrayList<HomeYoutubeModel.YoutubeCustomModel> list) {
        YoutubeViewMoreArtistFragment fragment = new YoutubeViewMoreArtistFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_TITLE, title);
        bundle.putParcelableArrayList(BUNDLE_PLAYLIST, list);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_view_more_artist, container, false);
        mContext = view.getContext();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
        playlist = new ArrayList<>();
        getIntentData(getArguments());
        txt_artist_name.setText(title);
        setRecyclerView();
    }

    public void initView() {
        artist_rv_perticular = view.findViewById(R.id.artist_rv_perticular);
        txt_artist_name = view.findViewById(R.id.txt_artist_name);
        progressbar = view.findViewById(R.id.progressbar);
    }

    private void getIntentData(Bundle bundle) {
        if (bundle != null) {
            title = bundle.getString(BUNDLE_TITLE);
            playlist.addAll(bundle.getParcelableArrayList(BUNDLE_PLAYLIST));
        }
    }

    private void setRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3, LinearLayoutManager.VERTICAL, false);
        artist_rv_perticular.setLayoutManager(gridLayoutManager);
        artist_rv_perticular.setHasFixedSize(true);
        youtubeArtistPerticulerAdapter = new YoutubeArtistPerticulerAdapter(mContext.getApplicationContext(), playlist);
        youtubeArtistPerticulerAdapter.setAritstPerticulerCallback(new YoutubeArtistPerticulerAdapter.AritstPerticulerCallback() {
            @Override
            public void onChannelPlaylistClick(HomeYoutubeModel.YoutubeCustomModel currentItem, String genre, String thumbnail) {
                ChannelsPlaylistItemFragment fragment = ChannelsPlaylistItemFragment.getInstance(
                        currentItem, currentItem.getGenre(),
                        currentItem.getThumbnail());
                ((youtube) getActivity()).onLoadFragment(fragment);

                /*Intent intent = new Intent(mContext, ChannelsPlaylistItemActivity.class);
                intent.putExtra(AppConstants.INTENT_YOUTUBE_PLAYLIST_DATA, (Parcelable) currentItem);
                intent.putExtra("title", currentItem.getGenre());
                intent.putExtra("thumbnail", currentItem.getThumbnail());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
            }
        });
        artist_rv_perticular.setAdapter(youtubeArtistPerticulerAdapter);
    }

}
