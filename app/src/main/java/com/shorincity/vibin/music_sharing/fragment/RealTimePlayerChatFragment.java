package com.shorincity.vibin.music_sharing.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.activity.RealTimePlayerActivity;
import com.shorincity.vibin.music_sharing.adapters.ChatRecyclerViewAdapter;
import com.shorincity.vibin.music_sharing.databinding.FragmentRealtimeChatBinding;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.vanniktech.emoji.EmojiPopup;

import java.util.ArrayList;

public class RealTimePlayerChatFragment extends MyBaseFragment implements RealTimeBaseFragment {
    private FragmentRealtimeChatBinding binding;
    private ArrayList<Object> list;
    private String songName = "";
    private EmojiPopup emojiPopup;

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
        emojiPopup = EmojiPopup.Builder.fromRootView(binding.getRoot())
                .setBackgroundColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.colorEmojiBg))
                .setIconColor(Color.parseColor("#8998A2"))
                .setSelectedIconColor(Color.parseColor("#556670"))
                .build(binding.etMsg);

        binding.rvSongs.setLayoutManager(new LinearLayoutManager(binding.rvSongs.getContext()));
        binding.rvSongs.setAdapter(new ChatRecyclerViewAdapter(binding.getRoot().getContext(),
                list, userId));

        binding.ivSend.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(binding.etMsg.getText())) {
                ((RealTimePlayerActivity) getActivity()).sendChatMessage(binding.etMsg.getText().toString());
                binding.etMsg.setText("");
                hideKeyboard();
            }
        });

        binding.ivEmoji.setOnClickListener(v -> {
            emojiPopup.toggle(); // Returns true when Popup is showing.
        });
        binding.rvSongs.scrollToPosition(list.size() - 1);
        binding.tvName.setText(songName);
    }

    private void hideKeyboard() {
        emojiPopup.dismiss();
        if (getView() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }

    public void setChatList(ArrayList<Object> list) {
        this.list = list;
    }

    public void notifyItemAdded() {
        if (binding.rvSongs.getAdapter() != null) {
            binding.rvSongs.getAdapter().notifyDataSetChanged();
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
