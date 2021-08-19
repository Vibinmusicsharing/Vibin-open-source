package com.shorincity.vibin.music_sharing.activity;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.adapters.ViewPagerAdapter;

import java.util.Objects;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        init();
        setupViewPager();
    }

    /**
     * Init components
     */
    private void init() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).show();
        getSupportActionBar().setTitle(getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    /**
     * Init tab and viewpager
     */
    private void setupViewPager() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        String[] titles = {getString(R.string.privacy_policy), getString(R.string.terms_and_conditions)};
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), titles);
        viewPager.setAdapter(viewPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    //navigate Up within your application's activity hierarchy from the action bar.
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}