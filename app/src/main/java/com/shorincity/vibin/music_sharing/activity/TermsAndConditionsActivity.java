package com.shorincity.vibin.music_sharing.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.fragment.TermsAndConditionsFragment;

import java.util.Objects;

public class TermsAndConditionsActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);
        mToolbar = findViewById(R.id.toolbar);
        setResult(Activity.RESULT_CANCELED);
        setSupportActionBar(mToolbar);

        navigateToFragment(TermsAndConditionsFragment.newInstance(), TermsAndConditionsFragment.class.getName(), null,
                false);

    }
    public void navigateToFragment(Fragment fragment, String navigatingToFragmentName, String navigationFromFragmentName, boolean isAddToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.fragment_slide_left_enter, R.animator.fragment_slide_left_exit, R.animator.fragment_slide_right_enter, R.animator.fragment_slide_right_exit);
        ft.replace(R.id.frame_container, fragment, navigatingToFragmentName);
        if (isAddToBackStack) {
            ft.addToBackStack(navigationFromFragmentName);
        }
        ft.commitAllowingStateLoss();
    }

    public void toolbarInitialization(boolean isVisible, String toolbarTitle, String toolbarSubTitle, boolean isBackButtonVisible) {
        if (isVisible) {
            Objects.requireNonNull(getSupportActionBar()).show();
            getSupportActionBar().setTitle(toolbarTitle.toUpperCase());
            Objects.requireNonNull(getSupportActionBar()).setSubtitle(toolbarSubTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(isBackButtonVisible);
            getSupportActionBar().setHomeButtonEnabled(isBackButtonVisible);
        } else {
            mToolbar.setVisibility(View.GONE);
        }
    }
}