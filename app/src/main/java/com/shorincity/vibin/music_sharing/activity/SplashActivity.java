package com.shorincity.vibin.music_sharing.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.fragment.SplashFragment;

import java.util.Objects;

/**
 * Created by Aditya S.Gangasagar
 * On 07-August-2020
 **/

public class SplashActivity extends AppCompatActivity {

    private AlertDialog mAlertDialog;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        statusBarColorChange();
        init();
        navigateToFragment(SplashFragment.newInstance(), SplashFragment.class.getName(), null,
                false);
    }

    /**
     * Init components
     */
    private void init() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    /**
     * Initialization of toolbar and get call from different fragments
     *
     * @param isVisible           - true/false -for visibility of toolbar
     * @param toolbarTitle        - Title of toolbar
     * @param toolbarSubTitle     - SubTitle of toolbar
     * @param isBackButtonVisible - is need to back button
     */
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

    /**
     * Method to launch Fragment as given fragment name and also check is fragment need to add on
     * BackStack
     *
     * @param fragment                   - Fragment Instance to launch fragment
     * @param navigatingToFragmentName   - name of fragment for tagging idetentification
     * @param navigationFromFragmentName -
     * @param isAddToBackStack           - Check is need to add fragment in stack process
     */
    public void navigateToFragment(Fragment fragment, String navigatingToFragmentName, String navigationFromFragmentName, boolean isAddToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.fragment_slide_left_enter, R.animator.fragment_slide_left_exit, R.animator.fragment_slide_right_enter, R.animator.fragment_slide_right_exit);
        ft.replace(R.id.frame_container, fragment, navigatingToFragmentName);
        if (isAddToBackStack) {
            ft.addToBackStack(navigationFromFragmentName);
        }
        ft.commitAllowingStateLoss();
    }

    /**
     * Method to show Exit dialog with cancel and yes button
     */
    private void showExitDialog() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
        mAlertDialog =
                new AlertDialog.Builder(SplashActivity.this, R.style.AlertDialogTheme).setTitle(getString(R.string.app_name)).
                        setMessage(getString(R.string.closeAppConfirmationMsg)).
                        setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }

                }).setIcon(R.drawable.app_logo).show();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            showExitDialog();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //navigate Up within your application's activity hierarchy from the action bar.
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void statusBarColorChange(){
        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        }
    }
}