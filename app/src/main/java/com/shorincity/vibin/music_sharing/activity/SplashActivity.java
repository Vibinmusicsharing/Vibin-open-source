package com.shorincity.vibin.music_sharing.activity;

import static com.shorincity.vibin.music_sharing.utils.AppConstants.KEY_CODE;
import static com.shorincity.vibin.music_sharing.utils.AppConstants.PREF_GIPHY_KEY;
import static com.shorincity.vibin.music_sharing.utils.AppConstants.PREF_LAST_FM_KEY;
import static com.shorincity.vibin.music_sharing.utils.AppConstants.PREF_YOUTUBE_KEY;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.gson.Gson;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.UI.youtube;
import com.shorincity.vibin.music_sharing.fragment.SplashFragment;
import com.shorincity.vibin.music_sharing.model.SignUpResponse;
import com.shorincity.vibin.music_sharing.model.VersionResponse;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private AlertDialog mAlertDialog;
    private Toolbar mToolbar;
    private Intent intent;
    private Runnable runnable;
    private SignUpResponse signUpResponse;
    private VersionResponse versionResponse;
    private boolean isTimerOver = false, isApiCallDone = false;
    private static final int RESULT_CODE_TERMS = 100;
    private static final int APP_UPDATE_REQUEST_CODE = 101;
    private AppUpdateManager appUpdateManager;
    private String uID="";

    private final InstallStateUpdatedListener listener = state -> {
        System.out.println("Version==>installStatus =" + state.installStatus());
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            runOnUiThread(this::popupSnackbarForCompleteUpdate);
        } else if (state.installStatus() == InstallStatus.FAILED) {
            callUserApi();
        } else if (state.installStatus() == InstallStatus.DOWNLOADING) {
            ProgressBar prg = findViewById(R.id.progressBar);
            TextView tvInstall = findViewById(R.id.tvInstall);

            prg.setVisibility(View.VISIBLE);
            tvInstall.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        appUpdateManager = AppUpdateManagerFactory.create(this);

        statusBarColorChange();
        ImageView imageView = findViewById(R.id.imageView1);
        Glide.with(this).load(R.drawable.vibin_gif).into(imageView);

        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent()).addOnSuccessListener(SplashActivity.this, new OnSuccessListener<PendingDynamicLinkData>() {
            @Override
            public void onSuccess(@NonNull PendingDynamicLinkData pendingDynamicLinkData) {
                Uri deepLink = null;
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.getLink();
                }
                try {
                    if (deepLink != null) {
                        uID = deepLink.getQueryParameter("id");
                        Logging.d("==> link id" + uID);
                    }
                } catch (Exception e) {

                }
            }
        }).addOnFailureListener(SplashActivity.this, e -> Log.w("TAG", "getDynamicLink:onFailure", e));


        init();
        /*navigateToFragment(SplashFragment.newInstance(), SplashFragment.class.getName(), null,
                false);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (appUpdateManager != null) {
            appUpdateManager
                    .getAppUpdateInfo()
                    .addOnSuccessListener(appUpdateInfo -> {
                        if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                            popupSnackbarForCompleteUpdate();
                        }
                    });
        }
    }

    /**
     * Init components
     */
    private void init() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        appUpdateManager.registerListener(listener);

        SharedPrefManager pref = SharedPrefManager.getInstance(this);
        AppConstants.YOUTUBE_KEY = pref.getVibinKey(PREF_YOUTUBE_KEY);
        AppConstants.GIPHY_API_KEY = pref.getVibinKey(PREF_GIPHY_KEY);
        AppConstants.LAST_FM_KEY = pref.getVibinKey(PREF_LAST_FM_KEY);

        //For Toolbar
        toolbarInitialization(false, "", "", true);
        onNextCheck();
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

    private void statusBarColorChange() {
        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        }
    }

    private void onNextCheck() {
        runnable = () -> {
            if (isApiCallDone && isTimerOver) {
                redirectScreen();
            }
        };
        new Handler().postDelayed(() -> {
            isTimerOver = true;
            runnable.run();
        }, AppConstants.SPLASH_DELAY);
        callVersionCheckApi();
    }

    private void redirectScreen() {
        if (SharedPrefManager.getInstance(SplashActivity.this).getSharedPrefBoolean
                (AppConstants.INTENT_SESSION_KEY)) {

            if (isApiCallDone) {
                if (signUpResponse != null && signUpResponse.isAddedPreferences()) {
                    intent = new Intent(SplashActivity.this, youtube.class).putExtra(AppConstants.PLAYLIST_UID, uID);
                } else {
                    intent = new Intent(SplashActivity.this, SelectMusicLanguageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(AppConstants.INTENT_IS_FROM_GOOGLE, true);
                    intent.putExtra(AppConstants.INTENT_USER_DATA_BUNDLE, bundle);
                    intent.putExtra(AppConstants.PLAYLIST_UID, uID);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
            }
        } else {
            if (SharedPrefManager.getInstance(SplashActivity.this).getSharedPrefBoolean(AppConstants.INTENT_IS_USER_LOGGEDIN)) {
                if (isApiCallDone) {
                    if (signUpResponse != null && signUpResponse.isAddedPreferences()) {
                        intent = new Intent(SplashActivity.this, youtube.class);
                        intent.putExtra(AppConstants.PLAYLIST_UID, uID);
                    }
                    else {
                        intent = new Intent(SplashActivity.this, SelectMusicLanguageActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(AppConstants.INTENT_IS_FROM_GOOGLE, true);
                        intent.putExtra(AppConstants.INTENT_USER_DATA_BUNDLE, bundle);
                        intent.putExtra(AppConstants.PLAYLIST_UID, uID);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                }
            } else {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
                boolean isAccepted = prefs.getBoolean(AppConstants.TERMS_COND_KEY, false);
                if (isAccepted) {
                    intent = new Intent(SplashActivity.this, SignUpEmailPassActivity.class);
                    intent.putExtra(AppConstants.PLAYLIST_UID, uID);
                } else {
                    navigateTnCActivity(RESULT_CODE_TERMS);
                }
            }
        }
        if (intent != null) {
            startActivity(intent);
            finish();
        }
    }

    private void navigateTnCActivity(int resultCode) {
        Intent intent = new Intent(SplashActivity.this, TermsAndConditionsActivity.class);
        startActivityForResult(intent, resultCode);
    }

    private void callVersionCheckApi() {
        DataAPI dataAPI = RetrofitAPI.getData();

        dataAPI.getVersionUpdate(AppConstants.LOGIN_SIGNUP_HEADER, getVersion(), KEY_CODE).enqueue(new Callback<VersionResponse>() {
            @Override
            public void onResponse(Call<VersionResponse> call, Response<VersionResponse> response) {
                Log.d("LOG_TAG", "==>Version " + new Gson().toJson(response.body()));
                if (response.body() != null &&
                        !TextUtils.isEmpty(response.body().getStatus()) &&
                        response.body().getStatus().equalsIgnoreCase("success")) {
                    versionResponse = response.body();

                    SharedPrefManager pref = SharedPrefManager.getInstance(SplashActivity.this);
                    pref.setVibinKeys(PREF_YOUTUBE_KEY, versionResponse.getYoutube());
                    pref.setVibinKeys(PREF_LAST_FM_KEY, versionResponse.getLastFm());
                    pref.setVibinKeys(PREF_GIPHY_KEY, versionResponse.getGiphy());

                    if (versionResponse.isUpdateRequired()) {
                        System.out.println("Version==>isUpdateRequired");
                        if (!TextUtils.isEmpty(versionResponse.getMessage()))
                            Toast.makeText(SplashActivity.this, versionResponse.getMessage(), Toast.LENGTH_LONG).show();

                        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
                        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                                System.out.println("Version==>UPDATE_AVAILABLE");
                                try {
                                    appUpdateManager.startUpdateFlowForResult(
                                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                            appUpdateInfo,
                                            // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                            versionResponse.isUpdateMandatory() ? AppUpdateType.IMMEDIATE : AppUpdateType.FLEXIBLE,
                                            // The current activity making the update request.
                                            SplashActivity.this,
                                            // Include a request code to later monitor this update request.
                                            APP_UPDATE_REQUEST_CODE);
                                } catch (IntentSender.SendIntentException e) {
                                    System.out.println("Version==>" + e.getMessage());
                                    e.printStackTrace();
                                    callUserApi();
                                }
                            } else {
                                System.out.println("Version==>UPDATE_AVAILABLE false");
                                callUserApi();
                            }
                        });
                    } else {
                        callUserApi();
                    }
                } else {
//                    Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_LONG).show();
                    showRetryDialog("Something went wrong!");
//                    callUserApi();
                }
            }

            @Override
            public void onFailure(Call<VersionResponse> call, Throwable t) {
                /*Toast.makeText(mContext, getString(R.string.msg_network_failed), Toast.LENGTH_LONG).show();
                callUserApi();*/
                showRetryDialog(getString(R.string.msg_network_failed));

            }
        });
    }

    private void showRetryDialog(String msg) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SplashActivity.this);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Retry", (dialog, id) -> {
                    callVersionCheckApi();
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    dialog.cancel();
                    finish();
                });

        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void callUserApi() {
        if (SharedPrefManager.getInstance(SplashActivity.this).getSharedPrefBoolean(AppConstants.INTENT_IS_USER_LOGGEDIN)) {
            DataAPI dataAPI = RetrofitAPI.getData();
            String token = SharedPrefManager.getInstance(SplashActivity.this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
            Call<SignUpResponse> callback = dataAPI.getUserDetail(AppConstants.LOGIN_SIGNUP_HEADER, token);
            callback.enqueue(new Callback<SignUpResponse>() {
                @Override
                public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                    isApiCallDone = true;
                    if (response.body() != null &&
                            response.body().getStatus().equalsIgnoreCase("success")) {
                        signUpResponse = response.body();
                    } else {
                        Toast.makeText(SplashActivity.this, response.message(), Toast.LENGTH_LONG).show();
                    }
                    runnable.run();
                }

                @Override
                public void onFailure(Call<SignUpResponse> call, Throwable t) {
                    isApiCallDone = true;
                    Logging.dLong("SignUp res:" + Log.getStackTraceString(t));
                    Toast.makeText(SplashActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                    runnable.run();
                }
            });
        } else {
            isApiCallDone = true;
            runnable.run();
        }

    }

    private int getVersion() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Displays the snackbar notification and call to action.
    private void popupSnackbarForCompleteUpdate() {
        View rootView = findViewById(R.id.llMain);
        Snackbar snackbar =
                Snackbar.make(
                        rootView,
                        "An update has just been downloaded.",
                        Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Install", view -> appUpdateManager.completeUpdate());
        snackbar.setActionTextColor(
                getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_UPDATE_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                Log.d("LOG_TAG", "Update flow failed! Result code: " + resultCode);
                // If the update is cancelled or fails,
                // you can request to start the update again.
                callUserApi();
            } else {
                ProgressBar prg = findViewById(R.id.progressBar);
                TextView tvInstall = findViewById(R.id.tvInstall);

                prg.setVisibility(View.VISIBLE);
                tvInstall.setVisibility(View.VISIBLE);
            }
        } else if (requestCode == RESULT_CODE_TERMS) {
            if (resultCode == Activity.RESULT_OK) {
                callUserApi();
            } else {
                finish();
            }
        }
    }
}