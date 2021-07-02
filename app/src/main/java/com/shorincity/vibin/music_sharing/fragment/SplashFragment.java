package com.shorincity.vibin.music_sharing.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.UI.youtube;
import com.shorincity.vibin.music_sharing.activity.LoginSignUpActivity;
import com.shorincity.vibin.music_sharing.activity.SplashActivity;
import com.shorincity.vibin.music_sharing.utils.AppConstants;

/**
 * Created by Aditya S.Gangasagar
 * On 08-August-2020
 **/
public class SplashFragment extends Fragment {

    private SplashActivity mActivity;
    private Context mContext;
    private View mView;
    private Intent intent;

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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
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
        }, AppConstants.SPLASH_DELAY);
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
