package com.shorincity.vibin.music_sharing.fragment;

import static com.shorincity.vibin.music_sharing.utils.AppConstants.KEY_CODE;
import static com.shorincity.vibin.music_sharing.utils.AppConstants.PREF_GIPHY_KEY;
import static com.shorincity.vibin.music_sharing.utils.AppConstants.PREF_LAST_FM_KEY;
import static com.shorincity.vibin.music_sharing.utils.AppConstants.PREF_YOUTUBE_KEY;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.gson.Gson;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.UI.youtube;
import com.shorincity.vibin.music_sharing.activity.SelectMusicLanguageActivity;
import com.shorincity.vibin.music_sharing.activity.SignUpEmailPassActivity;
import com.shorincity.vibin.music_sharing.activity.SplashActivity;
import com.shorincity.vibin.music_sharing.activity.TermsAndConditionsActivity;
import com.shorincity.vibin.music_sharing.model.SignUpResponse;
import com.shorincity.vibin.music_sharing.model.VersionResponse;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.GlideApp;
import com.shorincity.vibin.music_sharing.utils.Logging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Aditya S.Gangasagar
 * On 08-August-2020
 **/
public class SplashFragment extends Fragment {

    private SplashActivity mActivity;
    private Context mContext;
    private View mView;
    private Intent intent;
    private Runnable runnable;
    private SignUpResponse signUpResponse;
    private VersionResponse versionResponse;
    private boolean isTimerOver = false, isApiCallDone = false;
    private static int RESULT_CODE_TERMS = 100;
    private static int APP_UPDATE_REQUEST_CODE = 101;
    private AppUpdateManager appUpdateManager;

    private final InstallStateUpdatedListener listener = state -> {
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            popupSnackbarForCompleteUpdate();
        } else if (state.installStatus() == InstallStatus.FAILED) {
            callUserApi();
        } else if (state.installStatus() == InstallStatus.DOWNLOADING) {
            ProgressBar prg = mView.findViewById(R.id.progressBar);
            TextView tvInstall = mView.findViewById(R.id.tvInstall);

            prg.setVisibility(View.VISIBLE);
            tvInstall.setVisibility(View.VISIBLE);
        }
    };

    public SplashFragment() {
    }

    public static SplashFragment newInstance() {
        SplashFragment splashFragment = new SplashFragment();
        return splashFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (SplashActivity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_splash, container, false);
        ImageView imageView = mView.findViewById(R.id.imageView1);
        GlideApp.with(this).load(R.drawable.vibin_gif).into(imageView);

        init();
        return mView;
    }

    private void init() {
        mContext = getContext();
        appUpdateManager = AppUpdateManagerFactory.create(mContext);
        appUpdateManager.registerListener(listener);

        SharedPrefManager pref = SharedPrefManager.getInstance(mContext);
        AppConstants.YOUTUBE_KEY = pref.getVibinKey(PREF_YOUTUBE_KEY);
        AppConstants.GIPHY_API_KEY = pref.getVibinKey(PREF_GIPHY_KEY);
        AppConstants.LAST_FM_KEY = pref.getVibinKey(PREF_LAST_FM_KEY);

        //For Toolbar
        mActivity.toolbarInitialization(false, "", "", true);
        onNextCheck();
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
        if (SharedPrefManager.getInstance(mContext).getSharedPrefBoolean
                (AppConstants.INTENT_SESSION_KEY)) {

            if (isApiCallDone) {
                if (signUpResponse != null && signUpResponse.isAddedPreferences()) {
                    intent = new Intent(mContext, youtube.class);
                } else {
                    intent = new Intent(mContext, SelectMusicLanguageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(AppConstants.INTENT_IS_FROM_GOOGLE, true);
                    intent.putExtra(AppConstants.INTENT_USER_DATA_BUNDLE, bundle);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent = new Intent(mContext, youtube.class);
                }
            }
        } else {
            if (SharedPrefManager.getInstance(mContext).getSharedPrefBoolean(AppConstants.INTENT_IS_USER_LOGGEDIN)) {
                if (isApiCallDone) {
                    if (signUpResponse != null && signUpResponse.isAddedPreferences())
                        intent = new Intent(mContext, youtube.class);
                    else {
                        intent = new Intent(mContext, SelectMusicLanguageActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(AppConstants.INTENT_IS_FROM_GOOGLE, true);
                        intent.putExtra(AppConstants.INTENT_USER_DATA_BUNDLE, bundle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent = new Intent(mContext, youtube.class);
                    }
                }
            } else {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                boolean isAccepted = prefs.getBoolean(AppConstants.TERMS_COND_KEY, false);
                if (isAccepted) {
                    intent = new Intent(mContext, SignUpEmailPassActivity.class);
                } else {
                    navigateTnCActivity(RESULT_CODE_TERMS);
                }
            }
        }
        if (intent != null && isVisible()) {
            startActivity(intent);
            if (getActivity() != null)
                getActivity().finish();
        }
    }

    private void navigateTnCActivity(int resultCode) {
        if (getContext() != null) {
            Intent intent = new Intent(getContext(), TermsAndConditionsActivity.class);
            startActivityForResult(intent, resultCode);
        }
    }

    private void callVersionCheckApi() {
        DataAPI dataAPI = RetrofitAPI.getData();

        dataAPI.getVersionUpdate(AppConstants.LOGIN_SIGNUP_HEADER, getVersion(), KEY_CODE).enqueue(new Callback<VersionResponse>() {
            @Override
            public void onResponse(Call<VersionResponse> call, Response<VersionResponse> response) {
                Log.d("LOG_TAG", "==> " + new Gson().toJson(response.body()));
                if (response.body() != null &&
                        !TextUtils.isEmpty(response.body().getStatus()) &&
                        response.body().getStatus().equalsIgnoreCase("success")) {
                    versionResponse = response.body();

                    SharedPrefManager pref = SharedPrefManager.getInstance(mContext);
                    pref.setVibinKeys(PREF_YOUTUBE_KEY, versionResponse.getYoutube());
                    pref.setVibinKeys(PREF_LAST_FM_KEY, versionResponse.getLastFm());
                    pref.setVibinKeys(PREF_GIPHY_KEY, versionResponse.getGiphy());

                    if (versionResponse.isUpdateRequired()) {
                        System.out.println("Version==>isUpdateRequired");
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
                                            mActivity,
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Retry", (dialog, id) -> {
                    callVersionCheckApi();
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    dialog.cancel();
                    getActivity().finish();
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void callUserApi() {
        if (SharedPrefManager.getInstance(mContext).getSharedPrefBoolean(AppConstants.INTENT_IS_USER_LOGGEDIN)) {
            DataAPI dataAPI = RetrofitAPI.getData();
            String token = SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
            Call<SignUpResponse> callback = dataAPI.getUserDetail(AppConstants.LOGIN_SIGNUP_HEADER, token);
            callback.enqueue(new Callback<SignUpResponse>() {
                @Override
                public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                    isApiCallDone = true;
                    if (response.body() != null &&
                            response.body().getStatus().equalsIgnoreCase("success")) {
                        signUpResponse = response.body();
                    } else {
                        Toast.makeText(mContext, response.message(), Toast.LENGTH_LONG).show();
                    }
                    runnable.run();
                }

                @Override
                public void onFailure(Call<SignUpResponse> call, Throwable t) {
                    isApiCallDone = true;
                    Logging.dLong("SignUp res:" + Log.getStackTraceString(t));
                    Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_LONG).show();
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
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Displays the snackbar notification and call to action.
    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar =
                Snackbar.make(
                        mView,
                        "An update has just been downloaded.",
                        Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("RESTART", view -> appUpdateManager.completeUpdate());
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
                ProgressBar prg = mView.findViewById(R.id.progressBar);
                TextView tvInstall = mView.findViewById(R.id.tvInstall);

                prg.setVisibility(View.VISIBLE);
                tvInstall.setVisibility(View.VISIBLE);
            }
        } else if (requestCode == RESULT_CODE_TERMS) {
            if (resultCode == Activity.RESULT_OK) {
                runnable.run();
            } else {
                if (getActivity() != null)
                    getActivity().finish();
            }
        }
    }
}
