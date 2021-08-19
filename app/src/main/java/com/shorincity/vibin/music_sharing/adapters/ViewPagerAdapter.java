package com.shorincity.vibin.music_sharing.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.shorincity.vibin.music_sharing.fragment.PrivacyPolicyFragment;
import com.shorincity.vibin.music_sharing.fragment.ProfileILikedFragment;
import com.shorincity.vibin.music_sharing.fragment.ProfileLikeIGotFragment;
import com.shorincity.vibin.music_sharing.fragment.TermsAndConditionsFragment;
import com.shorincity.vibin.music_sharing.model.UserLikeList;

import java.util.ArrayList;


// Unused
public class ViewPagerAdapter extends FragmentPagerAdapter {

    private String[] titleName;
    String isFromUserLikes = "0";
    ArrayList<UserLikeList.GotLikes> userLikeIGotLists;
    ArrayList<UserLikeList.GotLikes> userLikeILikedLists;

    public ViewPagerAdapter(FragmentManager fm, String[] titleName) {
        super(fm);
        this.titleName = titleName;
    }

    public ViewPagerAdapter(FragmentManager fm, String[] titleName, String isFromUserLikes, ArrayList<UserLikeList.GotLikes> userLikeIGotLists, ArrayList<UserLikeList.GotLikes> userLikeILikedLists) {
        super(fm);
        this.titleName = titleName;
        this.isFromUserLikes = isFromUserLikes;
        this.userLikeIGotLists = userLikeIGotLists;
        this.userLikeILikedLists = userLikeILikedLists;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (isFromUserLikes.equalsIgnoreCase("1")) {
            if (position == 0) {
                fragment = new ProfileLikeIGotFragment(userLikeIGotLists);
            } else if (position == 1) {
                fragment = new ProfileILikedFragment(userLikeILikedLists);
            }

        } else {
            if (position == 0) {
                fragment = new PrivacyPolicyFragment();
            } else if (position == 1) {
                fragment = TermsAndConditionsFragment.newInstance(true);
            }
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