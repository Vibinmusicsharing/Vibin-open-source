package com.shorincity.vibin.music_sharing.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.activity.RealTimePlayerActivity;
import com.shorincity.vibin.music_sharing.adapters.ChatRecyclerViewAdapter;
import com.shorincity.vibin.music_sharing.databinding.FragmentRealtimeChatBinding;
import com.shorincity.vibin.music_sharing.model.ChatMessage;
import com.shorincity.vibin.music_sharing.utils.AppConstants;

import java.util.ArrayList;

public class RealTimePlayerChatFragment extends MyBaseFragment implements RealTimeBaseFragment {
    private FragmentRealtimeChatBinding binding;
    private ArrayList<Object> list;
    private String songName = "";

    public static RealTimePlayerChatFragment getInstance() {
        return new RealTimePlayerChatFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_realtime_chat, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initControls();
    }

    private void initControls() {
        int userId = SharedPrefManager.getInstance(binding.rvSongs.getContext()).getSharedPrefInt(AppConstants.INTENT_USER_ID);

        binding.rvSongs.setLayoutManager(new LinearLayoutManager(binding.rvSongs.getContext()));
        binding.rvSongs.setAdapter(new ChatRecyclerViewAdapter(binding.getRoot().getContext(),
                list, userId));

        binding.ivSend.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(binding.etMsg.getText())) {
                ((RealTimePlayerActivity) getActivity()).sendChatMessage(binding.etMsg.getText().toString());
                binding.etMsg.setText("");
            }
        });
        binding.rvSongs.scrollToPosition(list.size() - 1);
        binding.tvName.setText(songName);
    }

    public void setChatList(ArrayList<Object> list) {
        this.list = list;
    }

    public void notifyItemAdded() {
        if (binding.rvSongs.getAdapter() != null) {
            binding.rvSongs.getAdapter().notifyItemInserted(list.size() - 1);
            binding.rvSongs.scrollToPosition(list.size() - 1);
        }
    }

    @Override
    public void setSongName(String songName) {
        this.songName = songName;
        if (binding != null)
            binding.tvName.setText(songName);
    }
}
