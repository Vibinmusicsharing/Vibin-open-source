package com.shorincity.vibin.music_sharing.UI;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.activity.SelectMusicLanguageActivity;
import com.shorincity.vibin.music_sharing.activity.SignUpEmailPassActivity;
import com.shorincity.vibin.music_sharing.activity.TermsAndConditionsActivity;
import com.shorincity.vibin.music_sharing.activity.WebviewActivity;
import com.shorincity.vibin.music_sharing.callbackclick.LoginCallback;
import com.shorincity.vibin.music_sharing.databinding.ActivityLoginBinding;
import com.shorincity.vibin.music_sharing.fragment.ErrorDailogFragment;
import com.shorincity.vibin.music_sharing.model.AdditionalSignUpModel;
import com.shorincity.vibin.music_sharing.model.Resource;
import com.shorincity.vibin.music_sharing.model.SignUpResponse;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.shorincity.vibin.music_sharing.utils.OneTapLoginHelper;
import com.shorincity.vibin.music_sharing.viewmodel.LoginViewModel;

// login activity_youtube_2

public class LoginAct extends AppCompatActivity {

    private Context mContext;
    private OneTapLoginHelper oneTapLoginHelper;
    private ActivityLoginBinding binding;
    private LoginViewModel mLoginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mLoginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        binding.setLoginViewmodel(mLoginViewModel);
        binding.setLifecycleOwner(this);
        binding.setLoginCallback(new LoginCallback() {
            @Override
            public void onLoginClick() {
                login();
            }

            @Override
            public void onForgotClick() {
//                startActivity(new Intent(LoginAct.this, WebviewActivity.class).putExtra(AppConstants.INTENT_WEBVIEW_URL, AppConstants.FORGOT_PASS_URL));
                String packageName = "com.android.chrome";
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.intent.setPackage(packageName);
                customTabsIntent.launchUrl(LoginAct.this, Uri.parse(AppConstants.FORGOT_PASS_URL));
            }

            @Override
            public void onGoogleClick() {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                boolean isAccepted = prefs.getBoolean(AppConstants.TERMS_COND_KEY, false);
                if (isAccepted) {
                    googleSignIn();
                } else {
                    navigateTnCActivity();
                }
            }
        });

        mContext = LoginAct.this;

        statusBarColorChange();

        oneTapLoginHelper = new OneTapLoginHelper(this);
        oneTapLoginHelper.signInClick(this);

        SpannableString login = new SpannableString("Don't have an account? Sign up");
        ClickableSpan onClick = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(LoginAct.this, SignUpEmailPassActivity.class);
                intent.putExtra(AppConstants.INTENT_SIGN_UP_METHOD, AppConstants.SIGNUP_BY_APP);
                intent.putExtra(AppConstants.PLAYLIST_UID, getIntent().getStringExtra(AppConstants.PLAYLIST_UID));
                startActivity(intent);
                finish();
            }
        };

        login.setSpan(onClick, 23, login.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        login.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.counterColor)),
                23, login.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        binding.textviewtext.setMovementMethod(LinkMovementMethod.getInstance());
        binding.textviewtext.setText(login);

        setObserver();
        setNextButtonEnable();
    }

    private final Observer<String> emailPassObserver = s -> setNextButtonEnable();

    private void setObserver() {
        mLoginViewModel.email.observe(this, emailPassObserver);
        mLoginViewModel.password.observe(this, emailPassObserver);
    }

    private void setNextButtonEnable() {
        String error = mLoginViewModel.isValid();
        binding.btnloginLogin.setEnabled(TextUtils.isEmpty(error));
    }

    private void statusBarColorChange() {
        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }
    }

    private void navigateTnCActivity() {
        Intent intent = new Intent(LoginAct.this, TermsAndConditionsActivity.class);
        startActivityForResult(intent, 100);
    }

    private void googleSignIn() {
        oneTapLoginHelper.signInClick(this);
    }

    // login fucntion
    private void login() {
        String error = mLoginViewModel.isValid();
        if (TextUtils.isEmpty(error)) {
            binding.loadingLogin.setVisibility(View.VISIBLE);
            postLogin();
        } else {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        }
    }

    private void postLogin() {
        mLoginViewModel.callLoginApi().observe(this, additionalSignUpModelResource -> {
            if (additionalSignUpModelResource instanceof Resource.Loading) {
                binding.loadingLogin.setVisibility(View.VISIBLE);
            } else if (additionalSignUpModelResource instanceof Resource.Success) {
                binding.loadingLogin.setVisibility(View.INVISIBLE);
                AdditionalSignUpModel data = ((Resource.Success<AdditionalSignUpModel>) additionalSignUpModelResource).getData();
                if (data != null) {
                    if (data.getUserLoggedIn()) {
                        mLoginViewModel.setPrefData(SharedPrefManager.getInstance(LoginAct.this), data);

                        Intent k;
                        if (data.isAddedPreferences()) {
                            k = new Intent(LoginAct.this, youtube.class);
                        } else {
                            k = new Intent(LoginAct.this, SelectMusicLanguageActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(AppConstants.INTENT_IS_FROM_GOOGLE, true);
                            k.putExtra(AppConstants.INTENT_USER_DATA_BUNDLE, bundle);
                            k.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        }
                        k.putExtra(AppConstants.PLAYLIST_UID, getIntent().getStringExtra(AppConstants.PLAYLIST_UID));
                        startActivity(k);
                        finishAffinity();

                    } else if (!data.getUserLoggedIn() && data.getStatus().equalsIgnoreCase("change_login_type")) {

                        showErrorDialog(data.getMessage());

                    } else {
                        Toast.makeText(LoginAct.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginAct.this,
                            "Something went wrong!",
                            Toast.LENGTH_SHORT).show();
                }
            } else if (additionalSignUpModelResource instanceof Resource.Error) {
                binding.loadingLogin.setVisibility(View.INVISIBLE);
                String errorMsg = ((Resource.Error<AdditionalSignUpModel>) additionalSignUpModelResource).getErrorMsg();
                Toast.makeText(LoginAct.this,
                        errorMsg != null ? errorMsg : "Something went wrong!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showErrorDialog(String message) {
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.INTENT_TITLE, "Error!");
        bundle.putString(AppConstants.INTENT_MESSAGE, message);
        bundle.putString(AppConstants.INTENT_BUTTON_NAME, getResources().getString(R.string.signin_with_google));
        bundle.putBoolean(AppConstants.INTENT_CANCELABLE, true);


        ErrorDailogFragment errorDailogFragment = new ErrorDailogFragment();
        errorDailogFragment.setButtonListener(new ErrorDailogFragment.ButtonListener() {
            @Override
            public void onErrorDialogButtonClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        errorDailogFragment.setArguments(bundle);
        errorDailogFragment.setCancelable(false);
        errorDailogFragment.show(getSupportFragmentManager(), "LoginErrorDialog");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OneTapLoginHelper.REQ_ONE_TAP) {
            ProgressDialog showMe = new ProgressDialog(LoginAct.this);
            showMe.setMessage("Please wait");
            showMe.setCancelable(true);
            showMe.setCanceledOnTouchOutside(false);
            showMe.show();

            oneTapLoginHelper.handleResponse(data, new OneTapLoginHelper.GoogleSignupCallback() {
                @Override
                public void onResponse(SignUpResponse signUpResponse) {
                    showMe.dismiss();
                    if (signUpResponse.getStatus().equalsIgnoreCase("success")) {
                        if (signUpResponse.getUserCreated().equalsIgnoreCase("true")) {
                            SharedPrefManager.getInstance(LoginAct.this).setSharedPrefBoolean(AppConstants.INTENT_IS_USER_LOGGEDIN, true); // response.body().getUserLoggedIn()
                            SharedPrefManager.getInstance(LoginAct.this).setSharedPrefInt(AppConstants.INTENT_USER_ID, signUpResponse.getUserId());
                            SharedPrefManager.getInstance(LoginAct.this).setSharedPrefString(AppConstants.INTENT_USER_TOKEN, signUpResponse.getToken());
                            SharedPrefManager.getInstance(LoginAct.this).setSharedPrefString(AppConstants.INTENT_USER_API_TOKEN, signUpResponse.getApiToken());
                            SharedPrefManager.getInstance(LoginAct.this).setSharedPrefString(AppConstants.INTENT_USER_PREFERRED_PLATFORM, signUpResponse.getPreferredPlatform());
                            SharedPrefManager.getInstance(LoginAct.this).setSharedPrefString(AppConstants.INTENT_USER_NAME, signUpResponse.getUsername());
                            SharedPrefManager.getInstance(LoginAct.this).setSharedPrefString(AppConstants.INTENT_FULL_NAME, signUpResponse.getFullname());

                            Intent k = new Intent(LoginAct.this, SelectMusicLanguageActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(AppConstants.INTENT_IS_FROM_GOOGLE, true);
                            k.putExtra(AppConstants.INTENT_USER_DATA_BUNDLE, bundle);
                            k.putExtra(AppConstants.PLAYLIST_UID, getIntent().getStringExtra(AppConstants.PLAYLIST_UID));
                            k.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(k);

                        } else {
                            SharedPrefManager.getInstance(LoginAct.this).setSharedPrefBoolean(AppConstants.INTENT_IS_USER_LOGGEDIN, signUpResponse.getUserLoggedIn());
                            if (signUpResponse.getUserId() != null)
                                SharedPrefManager.getInstance(LoginAct.this).setSharedPrefInt(AppConstants.INTENT_USER_ID, signUpResponse.getUserId());
                            SharedPrefManager.getInstance(LoginAct.this).setSharedPrefString(AppConstants.INTENT_USER_TOKEN, signUpResponse.getToken());
                            SharedPrefManager.getInstance(LoginAct.this).setSharedPrefString(AppConstants.INTENT_USER_API_TOKEN, signUpResponse.getApiToken());
                            SharedPrefManager.getInstance(LoginAct.this).setSharedPrefString(AppConstants.INTENT_USER_PREFERRED_PLATFORM, signUpResponse.getPreferredPlatform());
                            SharedPrefManager.getInstance(LoginAct.this).setSharedPrefString(AppConstants.INTENT_USER_NAME, signUpResponse.getUsername());
                            SharedPrefManager.getInstance(LoginAct.this).setSharedPrefString(AppConstants.INTENT_FULL_NAME, signUpResponse.getFullname());

                            Intent k;
                            if (signUpResponse.isAddedPreferences()) {
                                k = new Intent(LoginAct.this, youtube.class);
                            } else {
                                k = new Intent(LoginAct.this, SelectMusicLanguageActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(AppConstants.INTENT_IS_FROM_GOOGLE, true);
                                k.putExtra(AppConstants.INTENT_USER_DATA_BUNDLE, bundle);
                                k.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            }
                            k.putExtra(AppConstants.PLAYLIST_UID, getIntent().getStringExtra(AppConstants.PLAYLIST_UID));
                            startActivity(k);
                        }
                        finishAffinity();
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(LoginAct.this).create();
                        alertDialog.setTitle(getResources().getString(R.string.app_name));
                        alertDialog.setMessage(signUpResponse.getMessage());
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                                (dialog, which) -> {
                                    alertDialog.dismiss();
                                });
                        alertDialog.show();
                    }
                }

                @Override
                public void onFailure(String msg) {
                    showMe.dismiss();
                    Toast.makeText(LoginAct.this, msg, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
