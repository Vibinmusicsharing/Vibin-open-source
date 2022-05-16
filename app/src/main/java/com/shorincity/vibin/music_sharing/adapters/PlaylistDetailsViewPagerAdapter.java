package com.shorincity.vibin.music_sharing.adapters;

import android.app.Fragment;
import android.app.FragmentManager;

import androidx.legacy.app.FragmentPagerAdapter;

import java.util.List;


// Unused
public class PlaylistDetailsViewPagerAdapter extends FragmentPagerAdapter {

    private List<String> titleName;
    private List<Fragment> fragmentList;

    public PlaylistDetailsViewPagerAdapter(FragmentManager fm, List<String> titleName, List<Fragment> fragmentList) {
        super(fm);
        this.titleName = titleName;
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position < titleName.size())
            return titleName.get(position);
        else
            return "";
    }
}