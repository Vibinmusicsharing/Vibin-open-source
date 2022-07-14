package com.shorincity.vibin.music_sharing.base.di;

import static com.shorincity.vibin.music_sharing.service.RetrofitAPI.BASE_URL;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shorincity.vibin.music_sharing.base.network.NetworkInterceptor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    @Provides
    @Singleton
    public Gson provideGson() {
        return new GsonBuilder().create();
    }

    @Provides
    @Singleton
    public GsonConverterFactory provideGsonConverter(Gson gson) {
        return GsonConverterFactory.create(gson);
    }

    @Provides
    @Singleton
    public NetworkInterceptor provideNetworkInterceptor(String authToken, String apiToken) {
        return new NetworkInterceptor(authToken, apiToken);
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(NetworkInterceptor networkInterceptor) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(networkInterceptor);
        return builder.build();
    }

    @Provides
    @Singleton
    public Retrofit.Builder provideRetrofitBuilder(GsonConverterFactory gsonConverterFactory) {
        return new Retrofit.Builder().addConverterFactory(gsonConverterFactory);
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(Retrofit.Builder builder, OkHttpClient okHttpClient) {
        return builder.baseUrl(BASE_URL).client(okHttpClient).build();
    }
}

