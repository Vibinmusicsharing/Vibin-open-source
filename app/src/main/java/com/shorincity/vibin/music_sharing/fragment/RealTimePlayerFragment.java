package com.shorincity.vibin.music_sharing.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.activity.RealTimePlayerActivity;
import com.shorincity.vibin.music_sharing.adapters.PlaylistCollaboratosAdapter;
import com.shorincity.vibin.music_sharing.adapters.PlaylistSongsAdapter;
import com.shorincity.vibin.music_sharing.adapters.ViewCollab;
import com.shorincity.vibin.music_sharing.callbackclick.PlaylistDetailCallback;
import com.shorincity.vibin.music_sharing.databinding.FragmentPlayerListnerBinding;
import com.shorincity.vibin.music_sharing.databinding.FragmentRealtimePlayerBinding;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.model.firebase.RealTimeSession;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Utility;
import com.shorincity.vibin.music_sharing.viewmodel.PlaylistDetailsViewModel;

import java.util.ArrayList;

public class RealTimePlayerFragment extends MyBaseFragment implements RealTimeBaseFragment {
    private FragmentRealtimePlayerBinding binding;
    private static final String PLAYLIST_ID = "playlist_id";
    private PlaylistDetailsViewModel viewModel = new PlaylistDetailsViewModel();
    private int playlistId;
    private ArrayList<PlaylistDetailModel> playlistList = new ArrayList<>();
    private String songName = "";
    private String trackId = "";
    private boolean isPlay = false;
    private boolean isRepeat = false, isShuffle = false;
    private int progress = 0, currentProgress = 0, totalTime = 0;


    public static RealTimePlayerFragment getInstance(int playlistId) {
        RealTimePlayerFragment fragment = new RealTimePlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(PLAYLIST_ID, playlistId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_realtime_player, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initControls();
    }

    private void initControls() {
        binding.rvSongs.setLayoutManager(new LinearLayoutManager(binding.rvSongs.getContext()));
        binding.rvSongs.setAdapter(new PlaylistSongsAdapter(binding.rvSongs.getContext(), playlistList, true, (type, position) -> {
            if (type == -1 && position != RecyclerView.NO_POSITION) {
                ((RealTimePlayerActivity) getActivity()).setCurrentPosition(position);
                ((RealTimePlayerActivity) getActivity()).callBroadcastUpdate(AppConstants.CHANGED);
            }
        }));
        playlistId = getArguments().getInt(PLAYLIST_ID);

        binding.ivRepeat.setOnClickListener(v -> {
            ((RealTimePlayerActivity) getActivity()).callBroadcastUpdate(AppConstants.REPEAT, true, false);
        });
        binding.ivPrev.setOnClickListener(v -> {
            ((RealTimePlayerActivity) getActivity()).setCurrentIndex(-1);
            ((RealTimePlayerActivity) getActivity()).callBroadcastUpdate(AppConstants.CHANGED);
        });
        binding.ivPlayPause.setOnClickListener(v -> {
            ((RealTimePlayerActivity) getActivity()).callBroadcastUpdate();
        });
        binding.ivNext.setOnClickListener(v -> {
            ((RealTimePlayerActivity) getActivity()).setCurrentIndex(1);
            ((RealTimePlayerActivity) getActivity()).callBroadcastUpdate(AppConstants.CHANGED);
        });
        binding.ivSuffle.setOnClickListener(v -> {
            ((RealTimePlayerActivity) getActivity()).callBroadcastUpdate(AppConstants.SUFFLE, false, true);
        });

        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ((RealTimePlayerActivity) getActivity()).setElapsedTime(progress, false);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ((RealTimePlayerActivity) getActivity()).setElapsedTime(seekBar.getProgress(), true);
            }
        });
        binding.tvName.setText(songName);
        setPlayPause(isPlay);
        setTrackId(trackId);
        setRepeatAndShuffle(isRepeat, isShuffle);
        setProgress(progress, currentProgress, totalTime);
    }

    public void setPlaylistList(ArrayList<PlaylistDetailModel> list) {
        playlistList.clear();
        playlistList.addAll(list);
        if (binding != null && binding.rvSongs.getAdapter() != null)
            binding.rvSongs.getAdapter().notifyDataSetChanged();
    }

    public void setProgress(int progress, int currentProgress, int totalTime) {
        this.progress = progress;
        this.currentProgress = currentProgress;
        this.totalTime = totalTime;
        if (binding != null) {
            binding.tvTime.setText(Utility.convertDuration(Long.valueOf(currentProgress)));
            binding.tvTotalTime.setText(Utility.convertDuration(Long.valueOf(totalTime)));
            binding.seekBar.setProgress(progress);
        }
    }

    public void setPlayPause(boolean isPlay) {
        this.isPlay = isPlay;
        if (binding != null) {
            if (isPlay) {
                binding.ivPlayPause.setImageResource(R.drawable.ic_player_pause);
            } else {
                binding.ivPlayPause.setImageResource(R.drawable.ic_player_play);
            }
        }
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
        if (binding != null && binding.rvSongs.getAdapter() != null) {
            ((PlaylistSongsAdapter) binding.rvSongs.getAdapter()).setTrackId(trackId);
            binding.rvSongs.getAdapter().notifyDataSetChanged();
        }
    }

    public void setRepeatAndShuffle(boolean isRepeat, boolean isShuffle) {
        this.isRepeat = isRepeat;
        this.isShuffle = isShuffle;
        if (binding != null) {
            binding.ivRepeat.setSelected(isRepeat);
            binding.ivSuffle.setSelected(isShuffle);
        }
    }

    @Override
    public void setSongName(String songName) {
        this.songName = songName;
        if (binding != null)
            binding.tvName.setText(songName);
    }

}
