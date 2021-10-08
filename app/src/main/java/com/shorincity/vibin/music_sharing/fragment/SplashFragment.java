package com.shorincity.vibin.music_sharing.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.UI.youtube;
import com.shorincity.vibin.music_sharing.activity.LoginSignUpActivity;
import com.shorincity.vibin.music_sharing.activity.SelectMusicGenreActivity;
import com.shorincity.vibin.music_sharing.activity.SelectMusicLanguageActivity;
import com.shorincity.vibin.music_sharing.activity.SignUpEmailPassActivity;
import com.shorincity.vibin.music_sharing.activity.SplashActivity;
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
        Glide.with(this).load(R.drawable.vibin_gif).into(imageView);

        init();
        return mView;
    }

    private void init() {
        mContext = getActivity();
        //For Toolbar
        mActivity.toolbarInitialization(false, "", "", true);
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
