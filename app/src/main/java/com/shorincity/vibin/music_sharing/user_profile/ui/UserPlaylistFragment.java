package com.shorincity.vibin.music_sharing.user_profile.ui;

import static com.shorincity.vibin.music_sharing.utils.AppConstants.USER_ID;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.databinding.FragmentUserPlaylistsBinding;
import com.shorincity.vibin.music_sharing.fragment.MyBaseFragment;
import com.shorincity.vibin.music_sharing.fragment.PlaylistDetailFragmentNew;
import com.shorincity.vibin.music_sharing.model.CombinedUserPlaylist;
import com.shorincity.vibin.music_sharing.model.MyPlaylistModel;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.user_profile.adapter.UserPlaylistAdapter;
import com.shorincity.vibin.music_sharing.utils.AppConstants;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserPlaylistFragment extends MyBaseFragment {

    private FragmentUserPlaylistsBinding binding;

    private UserPlaylistAdapter pinnedPlaylistAdapter;
    private UserPlaylistAdapter privatePlaylistAdapter;
    private UserPlaylistAdapter collaborativePlaylistAdapter;

    private final ArrayList<MyPlaylistModel> pinnedPlaylists = new ArrayList<>();
    private final ArrayList<MyPlaylistModel> privatePlaylists = new ArrayList<>();
    private final ArrayList<MyPlaylistModel> collaborativePlaylists = new ArrayList<>();
    private int customerId = -1;

    public static UserPlaylistFragment getInstance() {
        return new UserPlaylistFragment();
    }

    public static UserPlaylistFragment getNewInstance(int customerId) {
        UserPlaylistFragment fragment = new UserPlaylistFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(USER_ID, customerId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_playlists, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        customerId = bundle.getInt(USER_ID, -1);

        initRecyclerViews();
        initListeners();
        getUserPlaylists();
    }

    private void initRecyclerViews() {
        binding.rvPinned.setLayoutManager(new LinearLayoutManager(binding.rvPinned.getContext(), LinearLayoutManager.VERTICAL, false));
        binding.rvPrivate.setLayoutManager(new LinearLayoutManager(binding.rvPrivate.getContext(), LinearLayoutManager.VERTICAL, false));
        binding.rvCollab.setLayoutManager(new LinearLayoutManager(binding.rvCollab.getContext(), LinearLayoutManager.VERTICAL, false));

        pinnedPlaylistAdapter = new UserPlaylistAdapter(getActivity(), pinnedPlaylists, position -> {
            openPlaylist(pinnedPlaylists.get(position));
        });

        privatePlaylistAdapter = new UserPlaylistAdapter(getActivity(), privatePlaylists, position -> {
            openPlaylist(privatePlaylists.get(position));
        });

        collaborativePlaylistAdapter = new UserPlaylistAdapter(getActivity(), collaborativePlaylists, position -> {
            openPlaylist(collaborativePlaylists.get(position));
        });

        binding.rvPinned.setAdapter(pinnedPlaylistAdapter);
        binding.rvPrivate.setAdapter(privatePlaylistAdapter);
        binding.rvCollab.setAdapter(collaborativePlaylistAdapter);
    }

    private void initListeners() {
        binding.ivPinnedArrow.setOnClickListener(view -> {
            if (binding.rvPinned.getVisibility() == View.GONE) {
                binding.ivPinnedArrow.setImageResource(R.drawable.ic_arrow_primary);
                binding.rvPinned.setVisibility(View.VISIBLE);
            } else {
                binding.rvPinned.setVisibility(View.GONE);
                binding.ivPinnedArrow.setImageResource(R.drawable.ic_arrow_up_24);
            }
        });

        binding.ivPrivateArrow.setOnClickListener(view -> {
            if (binding.rvPrivate.getVisibility() == View.GONE) {
                binding.ivPrivateArrow.setImageResource(R.drawable.ic_arrow_primary);
                binding.rvPrivate.setVisibility(View.VISIBLE);
            } else {
                binding.rvPrivate.setVisibility(View.GONE);
                binding.ivPrivateArrow.setImageResource(R.drawable.ic_arrow_up_24);
            }
        });

        binding.ivCollabArrow.setOnClickListener(view -> {
            if (binding.rvCollab.getVisibility() == View.GONE) {
                binding.ivCollabArrow.setImageResource(R.drawable.ic_arrow_primary);
                binding.rvCollab.setVisibility(View.VISIBLE);
            } else {
                binding.rvCollab.setVisibility(View.GONE);
                binding.ivCollabArrow.setImageResource(R.drawable.ic_arrow_up_24);
            }
        });
    }

    private void getUserPlaylists() {
        DataAPI dataAPI = RetrofitAPI.getData();
        String token = SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);

        dataAPI.getUserPlaylists(AppConstants.LOGIN_SIGNUP_HEADER, token, customerId, "").enqueue(new Callback<CombinedUserPlaylist>() {
            @Override
            public void onResponse(Call<CombinedUserPlaylist> call, Response<CombinedUserPlaylist> response) {
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    pinnedPlaylists.clear();
                    privatePlaylists.clear();
                    collaborativePlaylists.clear();

                    if (response.body().getPinnedPlaylists().isEmpty()) {
                        binding.groupNoPinnedPlaylist.setVisibility(View.VISIBLE);
                        binding.rvPinned.setVisibility(View.GONE);
                        binding.ivPinnedArrow.setVisibility(View.GONE);
                    } else {
                        binding.groupNoPinnedPlaylist.setVisibility(View.GONE);
                        binding.rvPinned.setVisibility(View.VISIBLE);
                        binding.ivPinnedArrow.setVisibility(View.VISIBLE);
                        pinnedPlaylists.addAll(response.body().getPinnedPlaylists());
                        pinnedPlaylistAdapter.notifyDataSetChanged();
                    }
                    if (response.body().getPrivatePlaylists().isEmpty()) {
                        binding.groupNoPrivatePlaylist.setVisibility(View.VISIBLE);
                        binding.rvPrivate.setVisibility(View.GONE);
                        binding.ivPrivateArrow.setVisibility(View.GONE);
                    } else {
                        binding.groupNoPrivatePlaylist.setVisibility(View.GONE);
                        binding.rvPrivate.setVisibility(View.VISIBLE);
                        binding.ivPrivateArrow.setVisibility(View.VISIBLE);
                        privatePlaylists.addAll(response.body().getPrivatePlaylists());
                        privatePlaylistAdapter.notifyDataSetChanged();
                    }

                    collaborativePlaylists.addAll(response.body().getPublicPlaylists());
                    collaborativePlaylistAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<CombinedUserPlaylist> call, Throwable t) {
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openPlaylist(MyPlaylistModel playlist) {
        PlaylistDetailFragmentNew playlistDetailFragmentNew = PlaylistDetailFragmentNew.getInstance(playlist.getId(), playlist.getAdmin_id(), playlist);

        ((UserPlaylistsActivity) getActivity()).openFragment(playlistDetailFragmentNew, true);
    }
}
