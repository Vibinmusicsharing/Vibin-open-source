package com.shorincity.vibin.music_sharing.activity;

import android.content.Context;
import android.os.Bundle;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.adapters.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

public class PrivacyPolicyActivity extends AppCompatActivity {

    private Context mContext;
    private AlertDialog mAlertDialog;
    private Toolbar mToolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        mContext = PrivacyPolicyActivity.this;
        init();
        setupViewPager();
    }

    /**
     * Init components
     */
    private void init() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).show();
        getSupportActionBar().setTitle(getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }
    /**
     * Init tab and viewpager
     */
    private void setupViewPager(){
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        String[] titles={getString(R.string.privacy_policy),getString(R.string.terms_and_conditions)};
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),titles);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
    //navigate Up within your application's activity hierarchy from the action bar.
    @Override
    public boolean onSupportNavigateUp() {
       finish();
        return true;
    }
}