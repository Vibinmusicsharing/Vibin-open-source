package com.shorincity.vibin.music_sharing.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.adapters.PlaylistSongsAdapter;
import com.shorincity.vibin.music_sharing.databinding.FragmentPlaylistSongListBinding;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;

import java.util.ArrayList;

public class RealTimeListnerSongsFragment extends MyBaseFragment implements RealTimeBaseFragment {
    private FragmentPlaylistSongListBinding binding;
    private ArrayList<PlaylistDetailModel> playlistList = new ArrayList<>();
    private String songName = "";

    public static RealTimeListnerSongsFragment getInstance() {
        return new RealTimeListnerSongsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playlist_song_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initControls();
    }

    private void initControls() {
        binding.setIsProgress(playlistList.size() <= 0);
        binding.rvSongs.setLayoutManager(new LinearLayoutManager(binding.rvSongs.getContext()));
        binding.rvSongs.setAdapter(new PlaylistSongsAdapter(binding.rvSongs.getContext(), playlistList, true, (type, position) -> {

        }));
        binding.swipelayout.setOnRefreshListener(() -> binding.swipelayout.setRefreshing(false));
        binding.tvName.setText(songName);
        binding.setIsRealTime(true);
    }

    public void setPlaylistList(ArrayList<PlaylistDetailModel> list) {
        playlistList.clear();
        playlistList.addAll(list);
        if (binding != null && binding.rvSongs.getAdapter() != null) {
            binding.rvSongs.getAdapter().notifyDataSetChanged();
            binding.setIsProgress(false);
        }
    }

    @Override
    public void setSongName(String songName) {
        this.songName = songName;
        if (binding != null)
            binding.tvName.setText(songName);
    }

}
