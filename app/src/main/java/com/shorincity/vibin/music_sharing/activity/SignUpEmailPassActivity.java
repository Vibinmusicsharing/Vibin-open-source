package com.shorincity.vibin.music_sharing.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.LoginAct;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.UI.youtube;
import com.shorincity.vibin.music_sharing.callbackclick.SignUpCallback;
import com.shorincity.vibin.music_sharing.databinding.ActivitySignupEmailPassBinding;
import com.shorincity.vibin.music_sharing.model.AdditionalSignUpModel;
import com.shorincity.vibin.music_sharing.model.Resource;
import com.shorincity.vibin.music_sharing.model.SignUpResponse;
import com.shorincity.vibin.music_sharing.model.SignUpUserNameCheckModel;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.OneTapLoginHelper;
import com.shorincity.vibin.music_sharing.viewmodel.SignupEmailPassViewModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// SignUp First Screen
public class SignUpEmailPassActivity extends AppCompatActivity {

    private Context mContext;

    private OneTapLoginHelper oneTapLoginHelper;
    private ActivitySignupEmailPassBinding binding;
    private SignupEmailPassViewModel mSignViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup_email_pass);
        mSignViewModel = ViewModelProviders.of(this).get(SignupEmailPassViewModel.class);
        binding.setLifecycleOwner(this);
        binding.setSignUpViewmodel(mSignViewModel);

        binding.setSignUpCallback(new SignUpCallback() {
            @Override
            public void onSignUpClick() {
                SignUpEmailPassActivity.this.onSignUpClick();
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

        mContext = SignUpEmailPassActivity.this;
        oneTapLoginHelper = new OneTapLoginHelper(this);
        oneTapLoginHelper.signInClick(this);

        statusBarColorChange();
        // Calling to find Activity's child view here
        inItViews();

        // get Intent Data
        //getIntentData();

        inItListener();
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

    private void inItViews() {

        SpannableString login = new SpannableString("Already have an account? Log in");

        ClickableSpan onClick = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(SignUpEmailPassActivity.this, LoginAct.class);
                intent.putExtra(AppConstants.PLAYLIST_UID, getIntent().getStringExtra(AppConstants.PLAYLIST_UID));
                startActivity(intent);
                finish();
            }
        };

        login.setSpan(onClick, 25, login.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        login.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.counterColor)),
                25, login.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        binding.textviewtext.setText(login);
        binding.textviewtext.setMovementMethod(LinkMovementMethod.getInstance());

        setNextButtonEnable();
    }

    private void inItListener() {
        mSignViewModel.email.observe(this, s -> {
            if (TextUtils.isEmpty(s)) {
                binding.edtEmailSignup.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_mail, 0);
            } else {
                binding.edtEmailSignup.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }

            if (!TextUtils.isEmpty(s) && android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                binding.validatorProgress.setVisibility(View.VISIBLE);
                binding.edtEmailSignup.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_mail, 0);
                callEmailCheckerAPI();
            } else {
                binding.edtEmailSignup.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_mail, 0);
                binding.validatorProgress.setVisibility(View.GONE);
            }
            setNextButtonEnable();
        });

        mSignViewModel.password.observe(this, s -> {
            if (mSignViewModel.isPasswordValid(s.trim()))
                binding.passwordSignup.setError(null);
            else
                binding.passwordSignup.setError(getString(R.string.error_pass_regex_not_match));
            setNextButtonEnable();
        });

        mSignViewModel.confirmPassword.observe(this, s -> {
            setNextButtonEnable();
        });
    }

    private void onSignUpClick() {
        String error = mSignViewModel.validate();
        if (TextUtils.isEmpty(error)) {
            gotoNextActivity();
        } else {
            Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
        }
    }

    private void setNextButtonEnable() {
        binding.btnSubmit.setEnabled(TextUtils.isEmpty(mSignViewModel.validate()));
    }

    private void navigateTnCActivity() {
        Intent intent = new Intent(SignUpEmailPassActivity.this, TermsAndConditionsActivity.class);
        startActivityForResult(intent, 100);
    }

    private void googleSignIn() {
        oneTapLoginHelper.signInClick(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OneTapLoginHelper.REQ_ONE_TAP) {
            ProgressDialog showMe = new ProgressDialog(SignUpEmailPassActivity.this);
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
                            SharedPrefManager.getInstance(SignUpEmailPassActivity.this).setSharedPrefBoolean(AppConstants.INTENT_IS_USER_LOGGEDIN, true); // response.body().getUserLoggedIn()
                            SharedPrefManager.getInstance(SignUpEmailPassActivity.this).setSharedPrefInt(AppConstants.INTENT_USER_ID, signUpResponse.getUserId());
                            SharedPrefManager.getInstance(SignUpEmailPassActivity.this).setSharedPrefString(AppConstants.INTENT_USER_TOKEN, signUpResponse.getToken());
                            SharedPrefManager.getInstance(SignUpEmailPassActivity.this).setSharedPrefString(AppConstants.INTENT_USER_API_TOKEN, signUpResponse.getApiToken());
                            SharedPrefManager.getInstance(SignUpEmailPassActivity.this).setSharedPrefString(AppConstants.INTENT_USER_PREFERRED_PLATFORM, signUpResponse.getPreferredPlatform());
                            SharedPrefManager.getInstance(SignUpEmailPassActivity.this).setSharedPrefString(AppConstants.INTENT_USER_NAME, signUpResponse.getUsername());
                            SharedPrefManager.getInstance(SignUpEmailPassActivity.this).setSharedPrefString(AppConstants.INTENT_FULL_NAME, signUpResponse.getFullname());

                            Intent k = new Intent(SignUpEmailPassActivity.this, SelectMusicLanguageActivity.class);
                            k.putExtra(AppConstants.PLAYLIST_UID, getIntent().getStringExtra(AppConstants.PLAYLIST_UID));
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(AppConstants.INTENT_IS_FROM_GOOGLE, true);
                            k.putExtra(AppConstants.INTENT_USER_DATA_BUNDLE, bundle);
                            k.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(k);

                        } else {
                            SharedPrefManager.getInstance(SignUpEmailPassActivity.this).setSharedPrefBoolean(AppConstants.INTENT_IS_USER_LOGGEDIN, signUpResponse.getUserLoggedIn());
                            if (signUpResponse.getUserId() != null)
                                SharedPrefManager.getInstance(SignUpEmailPassActivity.this).setSharedPrefInt(AppConstants.INTENT_USER_ID, signUpResponse.getUserId());
                            SharedPrefManager.getInstance(SignUpEmailPassActivity.this).setSharedPrefString(AppConstants.INTENT_USER_TOKEN, signUpResponse.getToken());
                            SharedPrefManager.getInstance(SignUpEmailPassActivity.this).setSharedPrefString(AppConstants.INTENT_USER_API_TOKEN, signUpResponse.getApiToken());
                            SharedPrefManager.getInstance(SignUpEmailPassActivity.this).setSharedPrefString(AppConstants.INTENT_USER_PREFERRED_PLATFORM, signUpResponse.getPreferredPlatform());
                            SharedPrefManager.getInstance(SignUpEmailPassActivity.this).setSharedPrefString(AppConstants.INTENT_USER_NAME, signUpResponse.getUsername());
                            SharedPrefManager.getInstance(SignUpEmailPassActivity.this).setSharedPrefString(AppConstants.INTENT_FULL_NAME, signUpResponse.getFullname());

                            Intent k;
                            if (signUpResponse.isAddedPreferences()) {
                                k = new Intent(SignUpEmailPassActivity.this, youtube.class);
                            } else {
                                k = new Intent(SignUpEmailPassActivity.this, SelectMusicLanguageActivity.class);
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
                        AlertDialog alertDialog = new AlertDialog.Builder(SignUpEmailPassActivity.this).create();
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
                    Toast.makeText(SignUpEmailPassActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void gotoNextActivity() {
        startActivity(new Intent(this, VerificationActivity.class)
                .putExtra(AppConstants.INTENT_FULL_NAME, "")
                .putExtra(AppConstants.INTENT_SIGN_UP_METHOD, AppConstants.SIGNUP_BY_APP)
                .putExtra(AppConstants.INTENT_EMAIL, mSignViewModel.email.getValue())
                .putExtra(AppConstants.INTENT_PASSWORD, mSignViewModel.password.getValue())
                .putExtra(AppConstants.PLAYLIST_UID, getIntent().getStringExtra(AppConstants.PLAYLIST_UID)));
    }

    private void callEmailCheckerAPI() {
        mSignViewModel.callEmailCheckerAPI().observe(this, signUpUserNameCheckModelResource -> {
            Log.i("ValidEmail", "valid");
            binding.validatorProgress.setVisibility(View.GONE);
            binding.edtEmailSignup.setEnabled(true);
            binding.edtEmailSignup.requestFocus();
            binding.edtEmailSignup.setFocusable(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.edtEmailSignup.setShowSoftInputOnFocus(true);
            }

            if (signUpUserNameCheckModelResource instanceof Resource.Success) {
                binding.validatorProgress.setVisibility(View.GONE);
                SignUpUserNameCheckModel data = ((Resource.Success<SignUpUserNameCheckModel>) signUpUserNameCheckModelResource).getData();
                if (data.getExists()) {
                    Log.i("ValidEmail", "getExists");
                    mSignViewModel.isEmailVerified = false;
                    binding.edtEmailSignup.setError(getResources().getString(R.string.error_on_email_exist));
                } else {
                    Log.i("ValidEmail", "true");
                    mSignViewModel.isEmailVerified = true;
                    binding.edtEmailSignup.setError(null);
                    binding.edtEmailSignup.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check_mark_small, 0);
                    binding.passwordSignup.setEnabled(true);
                    binding.confirmPassword.setEnabled(true);

                }
            } else if (signUpUserNameCheckModelResource instanceof Resource.Error) {
                String errorMsg = ((Resource.Error<SignUpUserNameCheckModel>) signUpUserNameCheckModelResource).getErrorMsg();
                Toast.makeText(SignUpEmailPassActivity.this,
                        errorMsg != null ? errorMsg : "Something went wrong!",
                        Toast.LENGTH_SHORT).show();
                binding.validatorProgress.setVisibility(View.GONE);
                binding.edtEmailSignup.setEnabled(true);
                binding.edtEmailSignup.requestFocus();
            }

        });
    }

}
