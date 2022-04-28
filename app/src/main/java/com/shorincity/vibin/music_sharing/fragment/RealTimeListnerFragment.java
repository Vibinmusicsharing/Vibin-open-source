package com.shorincity.vibin.music_sharing.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.activity.RealTimePlayerActivity;
import com.shorincity.vibin.music_sharing.adapters.RTListnerCollaboratosAdapter;
import com.shorincity.vibin.music_sharing.databinding.FragmentPlayerListnerBinding;
import com.shorincity.vibin.music_sharing.model.realtime.RTListner;
import com.shorincity.vibin.music_sharing.utils.AppConstants;

import java.util.ArrayList;

public class RealTimeListnerFragment extends MyBaseFragment implements RealTimeBaseFragment {
    private FragmentPlayerListnerBinding binding;
    private ArrayList<RTListner> rtListner = new ArrayList<>();
    private String songName = "";
    private boolean isAdmin = false;


    public static RealTimeListnerFragment getInstance(boolean isAdmin) {
        RealTimeListnerFragment fragment = new RealTimeListnerFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(AppConstants.INTENT_IS_ADMIN, isAdmin);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_player_listner, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        isAdmin = getArguments().getBoolean(AppConstants.INTENT_IS_ADMIN, false);
        initControls();
    }

    private void initControls() {
        int userId = SharedPrefManager.getInstance(binding.rvSongs.getContext()).getSharedPrefInt(AppConstants.INTENT_USER_ID);
        binding.rvSongs.setLayoutManager(new GridLayoutManager(binding.rvSongs.getContext(), 4));

        binding.rvSongs.setAdapter(new RTListnerCollaboratosAdapter(binding.getRoot().getContext(),
                rtListner, isAdmin, userId, (type, position) -> {
            if (type == 1 && position != RecyclerView.NO_POSITION) {
                ((RealTimePlayerActivity) getActivity()).removeListner(rtListner.get(position).getUserId().intValue());
            }
        }));

        ((RealTimePlayerActivity) getActivity()).callGetListner();
        binding.tvName.setText(songName);
    }

    public void setListnerList(ArrayList<RTListner> list) {
        rtListner.clear();
        rtListner.addAll(list);
        if (binding != null && binding.rvSongs.getAdapter() != null)
            binding.rvSongs.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void setSongName(String songName) {
        this.songName = songName;
        if (binding != null)
            binding.tvName.setText(songName);
    }
}
