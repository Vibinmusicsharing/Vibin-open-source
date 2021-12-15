package com.shorincity.vibin.music_sharing.fragment;

import static com.shorincity.vibin.music_sharing.utils.AppConstants.KEY_CODE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.gson.Gson;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.UI.youtube;
import com.shorincity.vibin.music_sharing.activity.LoginSignUpActivity;
import com.shorincity.vibin.music_sharing.activity.SelectMusicGenreActivity;
import com.shorincity.vibin.music_sharing.activity.SelectMusicLanguageActivity;
import com.shorincity.vibin.music_sharing.activity.SignUpEmailPassActivity;
import com.shorincity.vibin.music_sharing.activity.SignUpPreferPlatformActivity;
import com.shorincity.vibin.music_sharing.activity.SplashActivity;
import com.shorincity.vibin.music_sharing.model.VersionResponse;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.activity.TermsAndConditionsActivity;
import com.shorincity.vibin.music_sharing.model.SignUpResponse;
import com.shorincity.vibin.music_sharing.model.YoutubeTrendingModel;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private boolean isTimerOver = false, isApiCallDone = false;
    private int RESULT_CODE_TERMS = 100;
    private VersionResponse versionResponse;
    private boolean isTimerOver = false, isApiCallDone = false;
    private static int APP_UPDATE_REQUEST_CODE = 100;
    private AppUpdateManager appUpdateManager;

    public SplashFragment() {
    }

    public static SplashFragment newInstance() {
        return new SplashFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (SplashActivity) context;
        }
    }

    private Runnable runnable = () -> {
        isTimerOver = true;
        if (isApiCallDone)
            redirectScreen();
    };

    private InstallStateUpdatedListener listener = state -> {
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            popupSnackbarForCompleteUpdate();
        } else if (state.installStatus() == InstallStatus.FAILED) {
            if (isApiCallDone && isTimerOver) {
                runnable.run();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_splash, container, false);
        ImageView imageView = mView.findViewById(R.id.imageView1);
        Glide.with(this).load(R.drawable.vibin_gif).into(imageView);

        init();
        return mView;
    }


    private void init() {
        mContext = getActivity();
        //For Toolbar
        appUpdateManager = AppUpdateManagerFactory.create(mContext);
        mActivity.toolbarInitialization(false, "", "", true);
        appUpdateManager.registerListener(listener);
        callVersionCheckApi();
        onNextCheck();
    }

    private void onNextCheck() {
        runnable = () -> {
            isTimerOver = true;
            if (SharedPrefManager.getInstance(mContext).getSharedPrefBoolean
                    (AppConstants.INTENT_SESSION_KEY)) {

                if (isApiCallDone) {
                    if (signUpResponse != null && signUpResponse.isAddedPreferences())
                        intent = new Intent(mContext, youtube.class);
                    else {
                        intent = new Intent(mContext, SelectMusicLanguageActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(AppConstants.INTENT_IS_FROM_GOOGLE, true);
                        intent.putExtra(AppConstants.INTENT_USER_DATA_BUNDLE, bundle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
        };
        new Handler().postDelayed(runnable, AppConstants.SPLASH_DELAY);

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

                    if (isApiCallDone && isTimerOver) {
                        runnable.run();
                    }
                }

                @Override
                public void onFailure(Call<SignUpResponse> call, Throwable t) {
                    Logging.dLong("SignUp res:" + Log.getStackTraceString(t));
                    Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    private void navigateTnCActivity(int resultCode) {
        Intent intent = new Intent(mContext, TermsAndConditionsActivity.class);
        startActivityForResult(intent, resultCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CODE_TERMS) {
            if (resultCode == Activity.RESULT_OK) {
                runnable.run();
            } else {
                if (getActivity() != null)
                    getActivity().finish();
            }
        }
        new Handler().postDelayed(runnable, AppConstants.SPLASH_DELAY);
    }

    private void redirectScreen() {

        if (SharedPrefManager.getInstance(mContext).getSharedPrefBoolean
                (AppConstants.INTENT_SESSION_KEY)) {
            intent = new Intent(mContext, youtube.class);
        } else {
            intent = new Intent(mContext, LoginSignUpActivity.class);
        }
        if (isVisible() == true) {
            startActivity(intent);
            if (getActivity() != null)
                getActivity().finish();
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
                    isApiCallDone = true;
                    versionResponse = response.body();

                    AppConstants.YOUTUBE_KEY = versionResponse.getYoutube();
                    AppConstants.GIPHY_API_KEY = versionResponse.getGiphy();

                    if (versionResponse.isUpdateRequired()) {
                        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
                        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
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
                                    e.printStackTrace();
                                    if (isApiCallDone && isTimerOver) {
                                        runnable.run();
                                    }
                                }
                            }
                        });
                    } else {
                        if (isApiCallDone && isTimerOver) {
                            runnable.run();
                        }
                    }
                } else {
                    Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_LONG).show();
                    if (isApiCallDone && isTimerOver) {
                        runnable.run();
                    }
                }
            }

            @Override
            public void onFailure(Call<VersionResponse> call, Throwable t) {
                Toast.makeText(mContext, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                if (isApiCallDone && isTimerOver) {
                    runnable.run();
                }
            }
        });
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == APP_UPDATE_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                Log.d("LOG_TAG", "Update flow failed! Result code: " + resultCode);
                // If the update is cancelled or fails,
                // you can request to start the update again.
                if (isApiCallDone && isTimerOver) {
                    runnable.run();
                }
            } else {
                ProgressBar prg = mView.findViewById(R.id.progressBar);
                TextView tvInstall = mView.findViewById(R.id.tvInstall);

                prg.setVisibility(View.VISIBLE);
                tvInstall.setVisibility(View.VISIBLE);
            }
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

    /*SharedPreferences sharedpreferences = mContext.getSharedPreferences(AppConstants.TERMS_COND,
                Context.MODE_PRIVATE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(SharedPrefManager.getInstance(mContext).getSharedPrefBoolean
                        (AppConstants.INTENT_SESSION_KEY)) {
                    intent = new Intent(mContext, spotify.class);
                    startActivity(intent);
                    getActivity().finish();

                } else if (sharedpreferences.contains(AppConstants.TERMS_COND_ACCEPTED)){
                    intent = new Intent(mContext, LoginSignUpActivity.class);
                    startActivity(intent);
                    getActivity().finish();

                }else {
                    mActivity.navigateToFragment(TermsAndConditionsFragment.newInstance(), TermsAndConditionsFragment.class.getName(), null,
                            false);
                }
            }
        },AppConstants.SPLASH_DELAY);*/
}
