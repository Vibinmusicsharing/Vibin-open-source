package com.shorincity.vibin.music_sharing;

import androidx.multidex.MultiDexApplication;

import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;

// You can define the Global Variables here.
public class VibinApplication extends MultiDexApplication {
    public boolean isPipEnable = false;

    @Override
    public void onCreate() {
        super.onCreate();
        EmojiManager.install(new IosEmojiProvider());

    }

}
