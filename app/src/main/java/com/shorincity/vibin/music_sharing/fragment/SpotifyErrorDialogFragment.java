package com.shorincity.vibin.music_sharing.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.utils.AppConstants;

public class SpotifyErrorDialogFragment extends DialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_spotify_premium_error,container,false);

        String msg = getArguments().getString(AppConstants.INTENT_MESSAGE);

        if(!TextUtils.isEmpty(msg)) {
            ((TextView)view.findViewById(R.id.message)).setText(msg);
        }

        view.findViewById(R.id.continue_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getYoutubeButtonListener().onYoutubeClick(view);
            }
        });

        return view;
    }

    public YoutubeButtonListener getYoutubeButtonListener() {
        return youtubeButtonListener;
    }

    public void setYoutubeButtonListener(YoutubeButtonListener youtubeButtonListener) {
        this.youtubeButtonListener = youtubeButtonListener;
    }

    YoutubeButtonListener youtubeButtonListener;

    public interface YoutubeButtonListener{
        public void onYoutubeClick(View view);
    }


    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
