package com.shorincity.vibin.music_sharing.fragment;

import android.app.Fragment;
import android.os.Bundle;

import com.shorincity.vibin.music_sharing.UI.youtube;

public abstract class MyBaseFragment extends Fragment {


    protected void onMusicPlay(Bundle bundle) {
        if (getActivity() instanceof youtube)
            ((youtube) getActivity()).onPlayMusic(bundle);
    }
}
