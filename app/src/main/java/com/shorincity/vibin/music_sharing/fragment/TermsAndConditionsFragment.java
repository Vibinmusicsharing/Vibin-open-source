package com.shorincity.vibin.music_sharing.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Aditya S.Gangasagar
 * On 08-August-2020
 **/
public class TermsAndConditionsFragment extends Fragment {

    private Activity mActivity;
    private Context mContext;
    private View mView;
    private RippleButton bDeny, bAccept;
    private TextView tvTermsCondition;
    private ProgressBar progressBar_cyclic;
    private boolean isFromSettings;
    private TextView txt_links;
    TextView txt_google, txt_youtube;
    String text = "I agree to Accept above mentioned policies and Google Privacy Policy and YouTube’s Terms of Services";
    String Google = "https://policies.google.com/privacy";
    String Youtube = "https://www.youtube.com/t/terms";
    LinearLayout ll_links;
    String text1 = "Google Privacy Policy";
    String text2 = "YouTube’s Terms of Services";
    Button btn_google, btn_youtube;
    private static String BUNDLE_SETTING = "setting";

    public TermsAndConditionsFragment() {
//        this.isFromSettings=isFromSettings;
    }

    public static TermsAndConditionsFragment newInstance(boolean isFromSettings) {
        TermsAndConditionsFragment termsAndConditionsFragment = new TermsAndConditionsFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(BUNDLE_SETTING, isFromSettings);
        termsAndConditionsFragment.setArguments(bundle);
        return termsAndConditionsFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SplashActivity) {
            mActivity = (SplashActivity) context;
        } else if (context instanceof PrivacyPolicyActivity) {
            mActivity = (PrivacyPolicyActivity) context;
        } else if (context instanceof TermsAndConditionsActivity) {
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
        Bundle args = getArguments();
        isFromSettings = args.getBoolean(BUNDLE_SETTING, false);
        init();
        sendType(AppConstants.TERMS_COND_TYPE);
        SpannableString ss = new SpannableString(text);
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                //Toast.makeText(mActivity, "Google", Toast.LENGTH_SHORT).show();

                // initializing object for custom chrome tabs.
                CustomTabsIntent.Builder customIntent = new CustomTabsIntent.Builder();

                // below line is setting toolbar color
                // for our custom chrome tab.
                customIntent.setToolbarColor(ContextCompat.getColor(mActivity, R.color.colorAccent));

                // we are calling below method after
                // setting our toolbar color.
                openCustomTab(mActivity, customIntent.build(), Uri.parse(Google));


            }
        };
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                //Toast.makeText(mActivity, "youtube", Toast.LENGTH_SHORT).show();
                // initializing object for custom chrome tabs.
                CustomTabsIntent.Builder customIntent = new CustomTabsIntent.Builder();

                // below line is setting toolbar color
                // for our custom chrome tab.
                customIntent.setToolbarColor(ContextCompat.getColor(mActivity, R.color.colorAccent));

                // we are calling below method after
                // setting our toolbar color.
                openCustomTab(mActivity, customIntent.build(), Uri.parse(Youtube));
            }
        };

        ss.setSpan(clickableSpan1, 47, 68, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan2, 73, 100, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        txt_links.setText(ss);
        txt_links.setMovementMethod(LinkMovementMethod.getInstance());

        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // initializing object for custom chrome tabs.
                CustomTabsIntent.Builder customIntent = new CustomTabsIntent.Builder();

                // below line is setting toolbar color
                // for our custom chrome tab.
                customIntent.setToolbarColor(ContextCompat.getColor(mActivity, R.color.colorAccent));

                // we are calling below method after
                // setting our toolbar color.
                openCustomTab(mActivity, customIntent.build(), Uri.parse(Google));
            }
        });

        btn_youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // initializing object for custom chrome tabs.
                CustomTabsIntent.Builder customIntent = new CustomTabsIntent.Builder();

                // below line is setting toolbar color
                // for our custom chrome tab.
                customIntent.setToolbarColor(ContextCompat.getColor(mActivity, R.color.colorAccent));

                // we are calling below method after
                // setting our toolbar color.
                openCustomTab(mActivity, customIntent.build(), Uri.parse(Youtube));
            }
        });
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
        txt_links = mView.findViewById(R.id.txt_links);
        btn_google = mView.findViewById(R.id.btn_google);
        btn_youtube = mView.findViewById(R.id.btn_yooutube);
        ll_links = mView.findViewById(R.id.ll_btnLinks);
        //For Toolbar


        if (mActivity instanceof SplashActivity) {
            ((SplashActivity) mActivity).toolbarInitialization(true, getString(R.string.terms_and_conditions),
                    "", false);
        }
        if (mActivity instanceof TermsAndConditionsActivity) {
            ((TermsAndConditionsActivity) mActivity).toolbarInitialization(true, getString(R.string.terms_and_conditions),
                    "", false);
        }
    }

    private void rippleClicked() {
        bDeny.setOnRippleCompleteListener(new OnRippleCompleteListener() {
            @Override
            public void onComplete(View v) {
            }
        });
    }


    private OnRippleCompleteListener onRippleCompleteListener = new OnRippleCompleteListener() {
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
        if (getActivity() != null)
            getActivity().finish();
    }

    private void onAccept() {
        saveSharedPrefTermsAndCond();
        if (getActivity() != null) {
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
        }
    }

    private void saveSharedPrefTermsAndCond() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefs.edit().putBoolean(AppConstants.TERMS_COND_KEY, true).apply();
    }

    public void sendType(String type) {
        progressBar_cyclic.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getData();
        Call<TermsAndConditionsModel> callback = dataAPI.getTermsAndConditions(AppConstants.LOGIN_SIGNUP_HEADER, type);
        callback.enqueue(new Callback<TermsAndConditionsModel>() {
            @Override
            public void onResponse(Call<TermsAndConditionsModel> call, retrofit2.Response<TermsAndConditionsModel> response) {
                if (response != null && response.body() != null) {

                    if ((response.body().getStatus().equalsIgnoreCase("success"))
                            && !TextUtils.isEmpty(response.body().getStatus())) {

                        StringBuilder displayResponse = new StringBuilder();
                        TermsAndConditionsModel resource = response.body();
                        List<TermsAndConditionsDetailsModel> termsAndConditionsDetailsModelList =
                                resource.getDetails();

                        for (TermsAndConditionsDetailsModel list : termsAndConditionsDetailsModelList) {
                            displayResponse.append(list.getData());
                        }
                        progressBar_cyclic.setVisibility(View.GONE);

                        if (isFromSettings) {
                            bAccept.setVisibility(View.GONE);
                            bDeny.setVisibility(View.GONE);
                            txt_links.setVisibility(View.GONE);
                            ll_links.setVisibility(View.VISIBLE);
                        } else {
                            bAccept.setVisibility(View.VISIBLE);
                            bDeny.setVisibility(View.VISIBLE);
                            txt_links.setVisibility(View.VISIBLE);
                            ll_links.setVisibility(View.GONE);
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

    public static void openCustomTab(Activity activity, CustomTabsIntent customTabsIntent, Uri uri) {
        // package name is the default package
        // for our custom chrome tab
        String packageName = "com.android.chrome";
        if (packageName != null) {

            // we are checking if the package name is not null
            // if package name is not null then we are calling
            // that custom chrome tab with intent by passing its
            // package name.
            customTabsIntent.intent.setPackage(packageName);

            // in that custom tab intent we are passing
            // our url which we have to browse.
            customTabsIntent.launchUrl(activity, uri);
        } else {
            // if the custom tabs fails to load then we are simply
            // redirecting our user to users device default browser.
            activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }
}
