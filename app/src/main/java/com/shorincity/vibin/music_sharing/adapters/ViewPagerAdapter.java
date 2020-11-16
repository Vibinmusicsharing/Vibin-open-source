package com.shorincity.vibin.music_sharing.adapters;

import com.shorincity.vibin.music_sharing.fragment.PrivacyPolicyFragment;
import com.shorincity.vibin.music_sharing.fragment.TermsAndConditionsFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


// Unused
public class ViewPagerAdapter extends FragmentPagerAdapter {

    private String[] titleName;
    public ViewPagerAdapter(FragmentManager fm,String[] titleName) {
        super(fm);
        this.titleName=titleName;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new PrivacyPolicyFragment();
        } else if (position == 1) {
            fragment = new TermsAndConditionsFragment(true);
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = titleName[position];
        return title;
    }
}