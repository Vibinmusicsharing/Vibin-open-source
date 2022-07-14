package com.shorincity.vibin.music_sharing.user_profile;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.shorincity.vibin.music_sharing.user_profile.repo.UserProfileRepository;

import javax.inject.Inject;

public class UserProfileViewModel extends ViewModel {

    private final UserProfileRepository repository;

    @Inject
    public UserProfileViewModel(UserProfileRepository userProfileRepository) {
        this.repository = userProfileRepository;
    }

    public void checkVM() {
        repository.checkRepo();
        Log.i("paras", "VM initialised");
    }
}