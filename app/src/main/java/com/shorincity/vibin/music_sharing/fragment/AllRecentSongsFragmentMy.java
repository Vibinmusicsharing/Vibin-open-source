package com.shorincity.vibin.music_sharing.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.adapters.RecentPlayedAdapter;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.model.RecentSongModel;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllRecentSongsFragmentMy extends MyBaseFragment {
    private View view;
    private Context mContext;
    private ArrayList<RecentSongModel> recentSongList;
    private RecentPlayedAdapter recentPlayedAdapter;
    private RecyclerView recentSongRv;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_all_recnt_songs, container, false);
        mContext = view.getContext();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recentSongRv = (RecyclerView) view.findViewById(R.id.rv_recently_played);
        setListAdapter();

        callGetRecentAllSongAPI(SharedPrefManager.getInstance(mContext).getSharedPrefInt(AppConstants.INTENT_USER_ID));
    }

    private void setListAdapter() {
        recentSongList = new ArrayList<>();

        recentSongRv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        recentSongRv.setHasFixedSize(true);

        recentPlayedAdapter = new RecentPlayedAdapter(mContext, recentSongList);
        recentPlayedAdapter.setCustomItemClickListener(new RecentPlayedAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                try {
                    if (recentSongList.get(position).getSongType().equalsIgnoreCase(AppConstants.YOUTUBE)) {
//                        Intent intent = new Intent(mContext, PlayYoutubeVideoActivity.class);
                        ArrayList<PlaylistDetailModel> playlist;
                        playlist = new ArrayList<>();
                        for (int i = 0; i < recentSongList.size(); i++) {
                            playlist.add(new PlaylistDetailModel(
                                    recentSongList.get(i).getSongName(),
                                    recentSongList.get(i).getSongThumbnail(),
                                    recentSongList.get(i).getSongId()
                            ));
                        }
                        Bundle bundle = new Bundle();
                        bundle.putInt("position", position);
                        bundle.putString("title", recentSongList.get(position).getSongName());
                        bundle.putString("description", "");
                        bundle.putString("thumbnail", recentSongList.get(position).getSongThumbnail());
                        bundle.putString("videoId", recentSongList.get(position).getSongId());
                        bundle.putParcelableArrayList("playlist", (ArrayList<? extends Parcelable>) playlist);
                        onMusicPlay(bundle);
/*                        intent.putExtra("data", bundle);
                        startActivity(intent);*/
                    }
                } catch (Exception e) {
                    Log.d("yash", "onItemClick: " + e.getMessage());
                }
                // conditions to check song type before going to play in player screen

            }
        });
        recentSongRv.setAdapter(recentPlayedAdapter);

    }

    private void callGetRecentAllSongAPI(int userId) {

        ((ProgressBar) view.findViewById(R.id.progressbar)).setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<ArrayList<RecentSongModel>> callback = dataAPI.getRecentAllSong(token, userId);
        callback.enqueue(new Callback<ArrayList<RecentSongModel>>() {
            @Override
            public void onResponse(Call<ArrayList<RecentSongModel>> call, Response<ArrayList<RecentSongModel>> response) {
                ((ProgressBar) view.findViewById(R.id.progressbar)).setVisibility(View.GONE);
                if (response != null && response.body() != null && response.body().size() > 0) {
                    recentSongList.addAll(response.body());
                    recentPlayedAdapter.notifyDataSetChanged();
                } else {

                }
            }

            @Override
            public void onFailure(Call<ArrayList<RecentSongModel>> call, Throwable t) {
                ((ProgressBar) view.findViewById(R.id.progressbar)).setVisibility(View.GONE);

            }
        });

    }
}
