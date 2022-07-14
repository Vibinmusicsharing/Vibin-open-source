package com.shorincity.vibin.music_sharing;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import dagger.hilt.android.HiltAndroidApp;

// You can define the Global Variables here.
@HiltAndroidApp
public class VibinApplication extends Application {
    public boolean isPipEnable = false;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EmojiManager.install(new IosEmojiProvider());
    }

}
