package com.shorincity.vibin.music_sharing.user_profile.repo;

import android.util.Log;

import com.shorincity.vibin.music_sharing.base.BaseRepository;
import com.shorincity.vibin.music_sharing.service.DataAPI;

import javax.inject.Inject;

public class UserProfileRepositoryImpl extends BaseRepository implements UserProfileRepository {

    private final DataAPI dataAPI;

    @Inject
    public UserProfileRepositoryImpl(DataAPI dataAPI) {
        this.dataAPI = dataAPI;
    }

    @Override
    public void checkRepo() {
        Log.i("paras", "repo done");
    }
}
