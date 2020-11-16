package com.shorincity.vibin.music_sharing.activity;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.utils.CustomSlidePanLayout;
import com.jkb.slidemenu.SlideMenuLayout;
import com.oze.music.musicbar.FixedMusicBar;
import com.oze.music.musicbar.MusicBar;
import com.oze.music.musicbar.ScrollableMusicBar;

public class TempActivity extends AppCompatActivity {

    //private SlidingPaneLayout mSlidingLayout;
    private SlideMenuLayout slideMenuLayout;
    private CustomSlidePanLayout mSlidingLayout;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        ScrollableMusicBar scrollableMusicBar = findViewById(R.id.ScrollableMusicBar);
        scrollableMusicBar.loadFrom("https://www.youtube.com/watch?v=to6xoVqKayA",1000);
        scrollableMusicBar.startAutoProgress(1.0f);
        scrollableMusicBar.show();
        scrollableMusicBar.setProgress(50);

        MusicBar musicBar = findViewById(R.id.MusicBar);
        musicBar.startAutoProgress(1.0f);
        musicBar.show();
        musicBar.setProgress(50);

        FixedMusicBar fixedMusicBar = findViewById(R.id.FixedMusicBar);
        fixedMusicBar.startAutoProgress(1.0f);
        fixedMusicBar.show();
        fixedMusicBar.setProgress(50);

    }

}