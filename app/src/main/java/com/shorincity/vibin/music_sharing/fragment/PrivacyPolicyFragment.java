package com.shorincity.vibin.music_sharing.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.activity.PrivacyPolicyActivity;
import com.shorincity.vibin.music_sharing.activity.SplashActivity;
import com.shorincity.vibin.music_sharing.model.TermsAndConditionsDetailsModel;
import com.shorincity.vibin.music_sharing.model.TermsAndConditionsModel;
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
public class PrivacyPolicyFragment extends Fragment {

    private Activity mActivity;
    private Context mContext;
    private View mView;
    private TextView tvPrivacyPolicy;
    private ProgressBar progressBar_cyclic;
    TextView txt_google, txt_youtube;
    String Google = "https://policies.google.com/privacy";
    String Youtube = "https://www.youtube.com/t/terms";
    Button btn_google, btn_youtube;
    LinearLayout btn_links;

    public PrivacyPolicyFragment() {
    }

    public static PrivacyPolicyFragment newInstance() {
        PrivacyPolicyFragment termsAndConditionsFragment = new PrivacyPolicyFragment();
        return termsAndConditionsFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SplashActivity) {
            mActivity = (SplashActivity) context;
        } else if (context instanceof PrivacyPolicyActivity) {
            mActivity = (PrivacyPolicyActivity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_privacy_policy, container, false);

        init();
        progressBar_cyclic.setVisibility(View.VISIBLE);
        sendType(AppConstants.PRIVACY_POLICY_TYPE);

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
        tvPrivacyPolicy = mView.findViewById(R.id.tvPrivacyPolicy);
        btn_google = mView.findViewById(R.id.btn_google);
        btn_youtube = mView.findViewById(R.id.btn_yooutube);
        btn_links = mView.findViewById(R.id.ll_btnLinks);

        //For Toolbar
        if (mActivity instanceof SplashActivity) {
            ((SplashActivity) mActivity).toolbarInitialization(true, getString(R.string.privacy_policy),
                    "", true);
        } else if (mActivity instanceof PrivacyPolicyActivity) {
        }

    }

    public void sendType(String type) {
        progressBar_cyclic.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getData();
        Call<TermsAndConditionsModel> callback = dataAPI.getTermsAndConditions(AppConstants.LOGIN_SIGNUP_HEADER, type);
        callback.enqueue(new Callback<TermsAndConditionsModel>() {
            @Override
            public void onResponse(Call<TermsAndConditionsModel> call, retrofit2.Response<TermsAndConditionsModel> response) {
                if (response != null && response.body() != null) {

                    Log.d("body", response.body().toString());
                    if ((response.body().getStatus().equalsIgnoreCase("success"))
                            && !TextUtils.isEmpty(response.body().getStatus())) {

                        StringBuilder displayResponse = new StringBuilder();
                        TermsAndConditionsModel resource = response.body();
                        List<TermsAndConditionsDetailsModel> termsAndConditionsDetailsModelList =
                                resource.getDetails();

                        for (TermsAndConditionsDetailsModel list : termsAndConditionsDetailsModelList) {
                            displayResponse.append(list.getData());
                        }
                        btn_links.setVisibility(View.VISIBLE);
                        progressBar_cyclic.setVisibility(View.GONE);
                        tvPrivacyPolicy.setText(displayResponse.toString());

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
