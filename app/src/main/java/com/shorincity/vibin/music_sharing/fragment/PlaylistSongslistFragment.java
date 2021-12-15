package com.shorincity.vibin.music_sharing.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.adapters.PlaylistSongsAdapter;
import com.shorincity.vibin.music_sharing.callbackclick.PlaylistDetailCallback;
import com.shorincity.vibin.music_sharing.databinding.FragmentPlaylistSongListBinding;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.model.PlaylistSongCollabDeleteModel;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.viewmodel.PlaylistDetailsViewModel;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistSongslistFragment extends MyBaseFragment {
    private FragmentPlaylistSongListBinding binding;
    private Context mContext;
    private static final String TAG = PlaylistDetailFragment.class.getName();
    private static final String PLAYLIST_ID = "playlist_id";
    private static final String VIEW_MODEL = "view_mode";
    private String playlistId;
    private PlaylistDetailsViewModel viewModel;

    public static PlaylistSongslistFragment getInstance(String playlistId, PlaylistDetailsViewModel viewModel) {
        PlaylistSongslistFragment fragment = new PlaylistSongslistFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PLAYLIST_ID, playlistId);
        bundle.putParcelable(VIEW_MODEL, viewModel);
        fragment.setArguments(bundle);
        return fragment;
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
        mContext = binding.getRoot().getContext();
        viewModel = getArguments().getParcelable(VIEW_MODEL);
        initControls();
    }

    private void initControls() {
        binding.setIsRealTime(false);
        binding.setIsProgress(true);
        binding.rvSongs.setLayoutManager(new LinearLayoutManager(binding.rvSongs.getContext()));
        binding.rvSongs.setAdapter(new PlaylistSongsAdapter(mContext, viewModel.getPlaylist(), false, (type, position) -> {
            if (position != RecyclerView.NO_POSITION) {
                ArrayList<PlaylistDetailModel> playlist = viewModel.getPlaylist();
                PlaylistDetailModel mBean = playlist.get(position);
                // Type=0:Like,1:DeleteSong, 2:AddSong, 3:Share
                switch (type) {
                    case 0: {
                        ProgressDialog showMe = new ProgressDialog(mContext);
                        showMe.setMessage("Please wait");
                        showMe.setCancelable(true);
                        showMe.setCanceledOnTouchOutside(false);
                        showMe.show();

                        DataAPI dataAPI = RetrofitAPI.getData();
                        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
                        Call<PlaylistDetailModel> callback = dataAPI.callPlaylistLike(token,
                                SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_TOKEN),
                                Integer.parseInt(playlistId), mBean.getId(), mBean.isLikedByViewer() ? "" : "True");
                        callback.enqueue(new Callback<PlaylistDetailModel>() {
                            @Override
                            public void onResponse(Call<PlaylistDetailModel> call, Response<PlaylistDetailModel> response) {
                                showMe.dismiss();
                                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                                    playlist.set(position, response.body());
                                    Collections.sort(playlist, Collections.reverseOrder());
                                    if (binding.rvSongs.getAdapter() != null)
                                        binding.rvSongs.getAdapter().notifyDataSetChanged();
                                } else {
                                    Toast.makeText(mContext,
                                            (response.body() != null && response.body().getMessage() != null) ? response.body().getMessage() : "Something went wrong!",
                                            Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<PlaylistDetailModel> call, Throwable t) {
                                showMe.dismiss();
                                Toast.makeText(mContext,
                                        t.getLocalizedMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                    }
                    case 1: {
                        callDeleteSong(String.valueOf(mBean.getId()));
                        break;
                    }
                    case 2: {
                        //TODO Add song
                        break;
                    }
                    case 3: {
                        //TODO Share
                        break;
                    }
                    default: {
                        try {
                            if (playlist.get(position).getType().equalsIgnoreCase(AppConstants.YOUTUBE)) {
                                Bundle bundle = new Bundle();
                                bundle.putInt("position", position);
                                bundle.putString("title", playlist.get(position).getName());
                                bundle.putString("description", "");
                                bundle.putString("thumbnail", playlist.get(position).getImage());
                                bundle.putString("videoId", playlist.get(position).getTrackId());
                                bundle.putString("from", "channel");
                                bundle.putParcelableArrayList("playlist", playlist);
                                onMusicPlay(bundle);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }));
        playlistId = getArguments().getString(PLAYLIST_ID);
        viewModel.getPublicPlaylistDetail(mContext, playlistId, new PlaylistDetailCallback() {
            @Override
            public void onResponse() {
                binding.setIsProgress(false);
                if (binding.rvSongs.getAdapter() != null)
                    binding.rvSongs.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onError() {
                binding.setIsProgress(false);
            }
        });
    }

    private void callDeleteSong(String songId) {
        ProgressDialog showMe = new ProgressDialog(mContext);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();

        DataAPI dataAPI = RetrofitAPI.getData();
        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        Call<PlaylistSongCollabDeleteModel> callback = dataAPI.callDeleteSongsOrCollabApi(token,
                SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_TOKEN),
                Integer.parseInt(playlistId),
                songId, "");
        callback.enqueue(new Callback<PlaylistSongCollabDeleteModel>() {
            @Override
            public void onResponse(Call<PlaylistSongCollabDeleteModel> call, retrofit2.Response<PlaylistSongCollabDeleteModel> response) {
                showMe.dismiss();
                PlaylistSongCollabDeleteModel model = response.body();
                if (model != null &&
                        model.getStatus().equalsIgnoreCase("success")) {
                    ArrayList<PlaylistDetailModel> list = viewModel.getPlaylist();
                    for (int i = 0; i < list.size(); i++) {
                        PlaylistDetailModel mBean = list.get(i);
                        if (model.getDeletedSongs().size() >= 1) {
                            int id = model.getDeletedSongs().get(0);
                            if (mBean != null && mBean.getId() == id) {
                                list.remove(mBean);
                                if (binding.rvSongs.getAdapter() != null)
                                    binding.rvSongs.getAdapter().notifyItemChanged(i);
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                } else {
                    Toast.makeText(mContext,
                            (response.body() != null && response.body().getMessage() != null) ? response.body().getMessage() : "Something went wrong!",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PlaylistSongCollabDeleteModel> call, Throwable t) {
                showMe.dismiss();
                Toast.makeText(mContext,
                        t.getLocalizedMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
