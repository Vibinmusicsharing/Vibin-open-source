package com.shorincity.vibin.music_sharing.base.di;

import androidx.lifecycle.ViewModel;

import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.user_profile.repo.UserProfileRepository;
import com.shorincity.vibin.music_sharing.user_profile.repo.UserProfileRepositoryImpl;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;
import dagger.hilt.android.scopes.ViewModelScoped;

@Module
@InstallIn(ViewModelComponent.class)
public class RepositoryModule {

    @Provides
    @ViewModelScoped
    public UserProfileRepository getUserProfileRepository(DataAPI dataAPI) {
        return new UserProfileRepositoryImpl(dataAPI);
    }
}
