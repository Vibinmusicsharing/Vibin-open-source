package com.shorincity.vibin.music_sharing.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.activity.PrivacyPolicyActivity;
import com.shorincity.vibin.music_sharing.activity.SplashActivity;
import com.shorincity.vibin.music_sharing.activity.TermsAndConditionsActivity;
import com.shorincity.vibin.music_sharing.model.TermsAndConditionsDetailsModel;
import com.shorincity.vibin.music_sharing.model.TermsAndConditionsModel;
import com.shorincity.vibin.music_sharing.ripples.RippleButton;
import com.shorincity.vibin.music_sharing.ripples.listener.OnRippleCompleteListener;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;

import java.util.List;

import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Aditya S.Gangasagar
 * On 08-August-2020
 **/
public class TermsAndConditionsFragment extends Fragment  {

    private Activity mActivity;
    private Context mContext;
    private View mView;
    private RippleButton bDeny, bAccept;
    private TextView tvTermsCondition;
    private ProgressBar progressBar_cyclic;
    private boolean isFromSettings;

    public TermsAndConditionsFragment(boolean isFromSettings) {
        this.isFromSettings=isFromSettings;
    }

    public static TermsAndConditionsFragment newInstance() {
        TermsAndConditionsFragment termsAndConditionsFragment = new TermsAndConditionsFragment(false);
        return termsAndConditionsFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SplashActivity) {
            mActivity = (SplashActivity) context;
        }else  if (context instanceof PrivacyPolicyActivity) {
            mActivity = (PrivacyPolicyActivity) context;
        }
        else  if (context instanceof TermsAndConditionsActivity) {
            mActivity = (TermsAndConditionsActivity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_terms_and_conditions, container, false);

        init();
        sendType(AppConstants.TERMS_COND_TYPE);
        return mView;
    }

    private void init() {
        mContext = getActivity();
        progressBar_cyclic = mView.findViewById(R.id.progressBar_cyclic);
        tvTermsCondition = mView.findViewById(R.id.tvTermsAndConditions);
        bDeny = mView.findViewById(R.id.bDeny);
        bAccept = mView.findViewById(R.id.bAccept);
        bDeny.setOnRippleCompleteListener(onRippleCompleteListener);
        bAccept.setOnRippleCompleteListener(onRippleCompleteListener);
        //For Toolbar



        if (mActivity instanceof SplashActivity) {
            ((SplashActivity)mActivity ).toolbarInitialization(true,getString(R.string.terms_and_conditions),
                    "", false);
        }
        if (mActivity instanceof TermsAndConditionsActivity) {
            ((TermsAndConditionsActivity)mActivity ).toolbarInitialization(true,getString(R.string.terms_and_conditions),
                    "", false);
        }
    }

    private void rippleClicked(){
        bDeny.setOnRippleCompleteListener(new OnRippleCompleteListener() {
            @Override
            public void onComplete(View v) {
            }
        });
    }


    private OnRippleCompleteListener onRippleCompleteListener=new OnRippleCompleteListener() {
        @Override
        public void onComplete(View view) {
            onClick(view);
        }
    };


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bAccept:
              onAccept();
                break;
            case R.id.bDeny:
              onDeny();
                break;
        }
    }

    private void onDeny() {
        getActivity().finish();
    }

    private void onAccept() {

        saveSharedPrefTermsAndCond();
         getActivity().setResult(Activity.RESULT_OK);
         getActivity().finish();

    }

    private void saveSharedPrefTermsAndCond() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefs.edit().putBoolean(AppConstants.TERMS_COND_KEY, true).apply();
    }

    public void sendType(String type) {
        progressBar_cyclic.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getData();
        Call<TermsAndConditionsModel> callback = dataAPI.getTermsAndConditions(AppConstants.LOGIN_SIGNUP_HEADER,type);
        callback.enqueue(new Callback<TermsAndConditionsModel>() {
            @Override
            public void onResponse(Call<TermsAndConditionsModel> call, retrofit2.Response<TermsAndConditionsModel> response) {
                if(response != null && response.body() != null) {

                    if((response.body().getStatus().equalsIgnoreCase("success"))
                            && !TextUtils.isEmpty(response.body().getStatus())) {

                        StringBuilder displayResponse = new StringBuilder();
                        TermsAndConditionsModel resource = response.body();
                        List<TermsAndConditionsDetailsModel> termsAndConditionsDetailsModelList =
                                resource.getDetails();

                        for (TermsAndConditionsDetailsModel list : termsAndConditionsDetailsModelList) {
                            displayResponse.append(list.getData());
                        }
                        progressBar_cyclic.setVisibility(View.GONE);

                        if(isFromSettings){
                            bAccept.setVisibility(View.GONE);
                            bDeny.setVisibility(View.GONE);
                        }else{
                            bAccept.setVisibility(View.VISIBLE);
                            bDeny.setVisibility(View.VISIBLE);
                        }

                        tvTermsCondition.setText(displayResponse.toString());
                    }
                } else {
                    progressBar_cyclic.setVisibility(View.GONE);
                    Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    //Log.d("TEST","Something went wrong!");
                }
            }
            @Override
            public void onFailure(Call<TermsAndConditionsModel> call, Throwable t) {
                progressBar_cyclic.setVisibility(View.GONE);
                Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_SHORT).show();
                //Log.d("TEST","Something went wrong!" + "Call : " + call + "t : " + t);
            }
        });
    }
}
