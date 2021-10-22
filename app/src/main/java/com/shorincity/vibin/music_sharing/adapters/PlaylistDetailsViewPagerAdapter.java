package com.shorincity.vibin.music_sharing.adapters;

import android.app.Fragment;
import android.app.FragmentManager;

import androidx.legacy.app.FragmentPagerAdapter;

import com.shorincity.vibin.music_sharing.fragment.PlaylistCollaboratosFragment;
import com.shorincity.vibin.music_sharing.fragment.PlaylistSongslistFragment;
import com.shorincity.vibin.music_sharing.viewmodel.PlaylistDetailsViewModel;

import java.util.List;


// Unused
public class PlaylistDetailsViewPagerAdapter extends FragmentPagerAdapter {

    private List<String> titleName;
    private String playlistId;
    private PlaylistDetailsViewModel viewModel;

    public PlaylistDetailsViewPagerAdapter(FragmentManager fm, List<String> titleName, String playlistId, PlaylistDetailsViewModel viewModel) {
        super(fm);
        this.titleName = titleName;
        this.playlistId = playlistId;
        this.viewModel = viewModel;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        if (position == 0)
            fragment = PlaylistSongslistFragment.getInstance(playlistId, viewModel);
        else
            fragment = PlaylistCollaboratosFragment.getInstance(playlistId, viewModel);
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleName.get(position);
    }
}