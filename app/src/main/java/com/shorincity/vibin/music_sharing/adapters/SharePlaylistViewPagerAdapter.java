package com.shorincity.vibin.music_sharing.adapters;

import android.app.Fragment;
import android.app.FragmentManager;

import androidx.legacy.app.FragmentPagerAdapter;

import com.shorincity.vibin.music_sharing.fragment.SharePlaylistQrCodeFragment;
import com.shorincity.vibin.music_sharing.fragment.SimpleScannerFragment;
import com.shorincity.vibin.music_sharing.model.MyPlaylistModel;

import java.util.List;


// Unused
public class SharePlaylistViewPagerAdapter extends FragmentPagerAdapter {

    private List<String> titleName;
    private MyPlaylistModel viewModel;

    public SharePlaylistViewPagerAdapter(FragmentManager fm, List<String> titleName, MyPlaylistModel viewModel) {
        super(fm);
        this.titleName = titleName;
        this.viewModel=viewModel;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        if (position == 0)
            fragment = SharePlaylistQrCodeFragment.getInstance(viewModel);
        else
            fragment = SimpleScannerFragment.getInstance();
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

    public Fragment getCurrentFragment(int position){
        return getItem(position);
    }
}