package com.shorincity.vibin.music_sharing.base.di;

import com.shorincity.vibin.music_sharing.service.DataAPI;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;
import dagger.hilt.android.scopes.ViewModelScoped;
import retrofit2.Retrofit;

@Module
@InstallIn(ViewModelComponent.class)
public class ServiceModule {

    @Provides
    @ViewModelScoped
    public DataAPI provideDataApi(Retrofit retrofit) {
        return retrofit.create(DataAPI.class);
    }
}
