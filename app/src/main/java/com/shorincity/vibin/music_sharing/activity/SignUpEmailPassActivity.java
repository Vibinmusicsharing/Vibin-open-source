package com.shorincity.vibin.music_sharing.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.LoginAct;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.UI.youtube;
import com.shorincity.vibin.music_sharing.fragment.ErrorDailogFragment;
import com.shorincity.vibin.music_sharing.model.AdditionalSignUpModel;
import com.shorincity.vibin.music_sharing.model.SignUpUserNameCheckModel;
import com.shorincity.vibin.music_sharing.ripples.RippleButton;
import com.shorincity.vibin.music_sharing.ripples.listener.OnRippleCompleteListener;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// SignUp First Screen
public class SignUpEmailPassActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private Context mContext;
    Animation frombottom, fromtop;
    RippleButton btnLogin_signup;
    private RippleButton joinBtn;
    TextView textView2;
    EditText emailSignUpEdt, password_signup, password_confirm;
    ProgressBar loading;

    private String fullName = "", signUpMethod = "", email, googlePass;
    private String password = "";
    private boolean isEmailVerified;
    private ProgressBar validatorProgress;
    private RippleButton google_sign_up_btn;
    private int RESULT_CODE_GOOGLE = 100;
    private static final int RC_GOOGLE_SIGN_IN = 007;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_email_pass);

        mContext = SignUpEmailPassActivity.this;


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

        TextView text = findViewById(R.id.textviewtext);

        frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        fromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop);

        loading = (ProgressBar) findViewById(R.id.loading_signup);
        joinBtn = findViewById(R.id.btn_submit);
        btnLogin_signup =  findViewById(R.id.btnLogin_signup);

        textView2 = (TextView) findViewById(R.id.textView2);

        emailSignUpEdt = (EditText) findViewById(R.id.edt_email_signup);

        password_signup = (EditText) findViewById(R.id.password_signup);
        password_confirm = (EditText) findViewById(R.id.confirm_password);

        validatorProgress = (ProgressBar) findViewById(R.id.validator_progress);
        google_sign_up_btn = findViewById(R.id.google_sign_up_btn);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage
                (this, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        //joinBtn.startAnimation(frombottom);
        // btnLogin_signup.startAnimation(frombottom);

        // text.startAnimation(frombottom);
        //textView2.startAnimation(fromtop);

        //emailSignUpEdt.startAnimation(fromtop);
        //((RelativeLayout)findViewById(R.id.email_holder)).startAnimation(fromtop);
        //password_signup.startAnimation(fromtop);
        //password_confirm.startAnimation(fromtop);

    }

    private void inItListener() {
        /*emailSignUpEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                emailSignUpEdt.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            }
        });*/


        emailSignUpEdt.addTextChangedListener(new EditTextWatch(this, emailSignUpEdt));
        password_signup.addTextChangedListener(new EditTextWatch(this, password_signup));

//        btnLogin_signup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(SignUpEmailPassActivity.this, LoginAct.class);
//                startActivity(intent);
//                finish();
//            }
//        });

        btnLogin_signup.setOnRippleCompleteListener(new OnRippleCompleteListener() {
            @Override
            public void onComplete(View v) {
                Intent intent = new Intent(SignUpEmailPassActivity.this, LoginAct.class);
                startActivity(intent);
                finish();
            }
        });

        joinBtn.setOnRippleCompleteListener(new OnRippleCompleteListener() {
            @Override
            public void onComplete(View v) {
                if (validate()) {
                    gotoNextActivity();
                }
            }
        });

        google_sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                boolean isAccepted = prefs.getBoolean(AppConstants.TERMS_COND_KEY, false);
                if (isAccepted) {
                    googleSignIn();
                } else {
                    navigateTnCActivity(RESULT_CODE_GOOGLE);
                }
            }
        });

    }

    private void navigateTnCActivity(int resultCode) {
        Intent intent = new Intent(SignUpEmailPassActivity.this, TermsAndConditionsActivity.class);
        startActivityForResult(intent, resultCode);
    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    private void googleSignOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                hideProgressDialog();
            }
        });
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else if (requestCode == RESULT_CODE_GOOGLE && resultCode == RESULT_OK) {
            googleSignIn();
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Logging.d("TEST", "handleSignInResult:" + result.isSuccess());
        hideProgressDialog();
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Logging.d("TEST", "display name: " + acct.getDisplayName());

            try {
                String personName = acct.getDisplayName();
                String personPhotoUrl = acct.getPhotoUrl() != null ? acct.getPhotoUrl().toString() : "";
                String Email = acct.getEmail();
                String [] pass = personName.split("\\s");


                Logging.d("TEST", "Name: " + personName + ", email: " + Email + ", Image: " + personPhotoUrl);

                if (!TextUtils.isEmpty(personName) && !TextUtils.isEmpty(Email)) {
                    googleSignOut();

                    fullName = personName;
                    email = Email;
                    signUpMethod = AppConstants.SIGNUP_BY_Google;
                    password = pass[0]+"123@";

                    gotoNextActivity();
                    //postLogin(email, personName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
        }
    }


    public void postLogin(final String email, final String password) {
        loading.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getData();

        Call<AdditionalSignUpModel> callback = dataAPI.login(AppConstants.LOGIN_SIGNUP_HEADER, email, password, AppConstants.SIGNUP_BY_APP);
        callback.enqueue(new Callback<AdditionalSignUpModel>() {
            @Override
            public void onResponse(Call<AdditionalSignUpModel> call, Response<AdditionalSignUpModel> response) {
                loading.setVisibility(View.INVISIBLE);

                if (response != null && response.body() != null) {

                    if ((response.body().getStatus().equalsIgnoreCase("error") || response.body().getStatus().equalsIgnoreCase("failed"))
                            && !TextUtils.isEmpty(response.body().getMessage())) {
                        Toast.makeText(SignUpEmailPassActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        if (response.body().getUserLoggedIn()) {

                            SharedPrefManager.getInstance(SignUpEmailPassActivity.this).setSharedPrefBoolean(AppConstants.INTENT_IS_USER_LOGGEDIN, response.body().getUserLoggedIn());
                            SharedPrefManager.getInstance(SignUpEmailPassActivity.this).setSharedPrefInt(AppConstants.INTENT_USER_ID, response.body().getUserId());
                            SharedPrefManager.getInstance(SignUpEmailPassActivity.this).setSharedPrefString(AppConstants.INTENT_USER_TOKEN, response.body().getToken());
                            SharedPrefManager.getInstance(SignUpEmailPassActivity.this).setSharedPrefString(AppConstants.INTENT_USER_API_TOKEN, response.body().getApiToken());
                            SharedPrefManager.getInstance(SignUpEmailPassActivity.this).setSharedPrefString(AppConstants.INTENT_USER_PREFERRED_PLATFORM, response.body().getPreferredPlatform());
                            SharedPrefManager.getInstance(SignUpEmailPassActivity.this).setSharedPrefString(AppConstants.INTENT_USER_NAME, response.body().getUsername());
                            SharedPrefManager.getInstance(SignUpEmailPassActivity.this).setSharedPrefString(AppConstants.INTENT_FULL_NAME, response.body().getFullname());

                            Intent k = new Intent(SignUpEmailPassActivity.this, youtube.class);
                            startActivity(k);
                            finishAffinity();

                        } else if (!response.body().getUserLoggedIn() && response.body().getStatus().equalsIgnoreCase("change_login_type")) {

                            showErrorDialog(response.body().getMessage());

                        } else {
                            Toast.makeText(SignUpEmailPassActivity.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    Toast.makeText(SignUpEmailPassActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdditionalSignUpModel> call, Throwable t) {

                loading.setVisibility(View.INVISIBLE);
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
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            Logging.d("TEST", "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    private void getIntentData() {

        signUpMethod = "";

        try {
            fullName = getIntent().getStringExtra(AppConstants.INTENT_FULL_NAME);
            signUpMethod = getIntent().getStringExtra(AppConstants.INTENT_SIGN_UP_METHOD);
            email = getIntent().getStringExtra(AppConstants.INTENT_EMAIL);
            googlePass = getIntent().getStringExtra(AppConstants.INTENT_PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            // checking that user is coming by tapping on G+ or by filling details manually

            if (!TextUtils.isEmpty(signUpMethod) && signUpMethod.equalsIgnoreCase(AppConstants.SIGNUP_BY_Google)) {

                // Going to user name (SignUp Second Page directly), if user is coming by G+ and already have email
                startActivity(new Intent(this, SignUpUserNameActivity.class)
                        .putExtra(AppConstants.INTENT_FULL_NAME, fullName)
                        .putExtra(AppConstants.INTENT_SIGN_UP_METHOD, signUpMethod)
                        .putExtra(AppConstants.INTENT_EMAIL, email)
                        .putExtra(AppConstants.INTENT_PASSWORD, googlePass));
                finish();
            } else {
                // need to fill email and pass manually
                signUpMethod = AppConstants.SIGNUP_BY_APP;
            }

            /*if(TextUtils.isEmpty(signUpMethod))
                signUpMethod = AppConstants.SIGNUP_BY_APP;

            if(!TextUtils.isEmpty(email)) {
                emailSignUpEdt.setText(email);
                emailSignUpEdt.setEnabled(false);
                emailSignUpEdt.setAlpha(0.5f);
            }


            if(!TextUtils.isEmpty(googlePass)) {
                password = googlePass;
                password_confirm.setVisibility(View.GONE);
                password_signup.setVisibility(View.GONE);
            }*/

        }
    }

    public boolean validate() {
        boolean isValidate = false;

        email = emailSignUpEdt.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() || !isEmailVerified) {
            Toast.makeText(this, "Please enter valid Email Id!", Toast.LENGTH_SHORT).show();
        } else if (password_signup.getVisibility() == View.VISIBLE) {

            String password = password_signup.getText().toString().trim();
            String conf_pass = password_confirm.getText().toString().trim();

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please enter the Password!", Toast.LENGTH_SHORT).show();
            } else if (!isPasswordValid(password)) {
                Toast.makeText(this, "Invalid Password!", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(conf_pass)) {
                Toast.makeText(this, "Please enter the Confirm Password!", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(conf_pass)) {
                Toast.makeText(this, "Password and Confirm Password should be same!", Toast.LENGTH_SHORT).show();
            } else {
                isValidate = true;
            }

        } else {
            isValidate = true;
        }

        return isValidate;
    }

    public static boolean isPasswordValid(String s) {

        // 1 Cap 1 Lower 1 number 1 alphanumeric
        //Pattern p = Pattern.compile("(?-i)(?=^.{8,}$)((?!.*\\s)(?=.*[A-Z])(?=.*[a-z]))(?=(1)(?=.*\\d)|.*[^A-Za-z0-9])^.*$");

        // 1 Cap 1 lower 1 number 1 special char
        Pattern p = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d.*)(?=.*\\W.*)[a-zA-Z0-9\\S]{8,20}$");

        // 1 cap 1 lower 1 number. only some special char (if entered)
        //Pattern p = Pattern.compile("^.*(?=.{8,})(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(^[a-zA-Z0-9@\\$=!:.#%]+$)");
        Matcher m = p.matcher(s);
        return (m.find() && m.group().equals(s));
    }

    private void gotoNextActivity() {

        if (password_signup.getVisibility() == View.VISIBLE){
            if (password.equals("")){
                password = password_signup.getText().toString().trim();
                signUpMethod = AppConstants.SIGNUP_BY_APP;
            }
        }


        startActivity(new Intent(this, SignUpUserNameActivity.class)
                .putExtra(AppConstants.INTENT_FULL_NAME, fullName)
                .putExtra(AppConstants.INTENT_SIGN_UP_METHOD, signUpMethod)
                .putExtra(AppConstants.INTENT_EMAIL, email)
                .putExtra(AppConstants.INTENT_PASSWORD, password));

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public class EditTextWatch implements TextWatcher {

        Context context;
        EditText editText;

        private EditTextWatch(Context context, EditText editText) {
            this.context = context;
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            switch (editText.getId()) {
                case R.id.edt_email_signup:
                    email = emailSignUpEdt.getText().toString().trim();
                   // Log.d("TEST", "onTextChanged email-->" + email);
                    if (TextUtils.isEmpty(email)) {
                        emailSignUpEdt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_mail, 0);
                    } else {
                        emailSignUpEdt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    }
                    break;
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            switch (editText.getId()) {
                case R.id.edt_email_signup:

                    email = emailSignUpEdt.getText().toString().trim();

                   // Log.d("TEST", "afterTextChanged email-->" + email);

                    if (!TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        validatorProgress.setVisibility(View.VISIBLE);
                        //emailSignUpEdt.setEnabled(false);
                        emailSignUpEdt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_mail, 0);
                        callEmailCheckerAPI(email);
                    } else {
                        emailSignUpEdt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_mail, 0);
                        validatorProgress.setVisibility(View.GONE);
                        /*emailSignUpEdt.setEnabled(true);
                        emailSignUpEdt.requestFocus();
                        emailSignUpEdt.setFocusable(true);*/
                    }

                    break;
                case R.id.password_signup:

                    if (isPasswordValid(password_signup.getText().toString().trim()))
                        password_signup.setError(null);
                    else
                        password_signup.setError(getResources().getString(R.string.error_pass_regex_not_match));
                    break;
            }

            /*if (!isEmailVerified) {
                Utility.setViewEnable(joinBtn,false);
            } else {
                Utility.setViewEnable(joinBtn, true);
            }*/
        }
    }

    Call<SignUpUserNameCheckModel> emailCheckerCallback;

    public void callEmailCheckerAPI(String email) {
        //progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getData();

        emailCheckerCallback = dataAPI.signupEmailChecker(AppConstants.LOGIN_SIGNUP_HEADER, email);
        emailCheckerCallback.enqueue(new Callback<SignUpUserNameCheckModel>() {
            @Override
            public void onResponse(Call<SignUpUserNameCheckModel> call, Response<SignUpUserNameCheckModel> response) {
                //progressBar.setVisibility(View.GONE);
                Log.i("ValidEmail", "valid");
                validatorProgress.setVisibility(View.GONE);
                emailSignUpEdt.setEnabled(true);
                emailSignUpEdt.requestFocus();
                emailSignUpEdt.setFocusable(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    emailSignUpEdt.setShowSoftInputOnFocus(true);
                }

                if (response != null && response.body() != null) {

                    if (response.body() != null) {

                        if (response.body().getStatus().equalsIgnoreCase("checked")) {

                            if (response.body().getExists()) {
                                isEmailVerified = false;
                                emailSignUpEdt.setError(getResources().getString(R.string.error_on_email_exist));
                            } else {
                                isEmailVerified = true;
                                emailSignUpEdt.setError(null);
                                emailSignUpEdt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check_mark_small, 0);
                                password_signup.setEnabled(true);
                                password_confirm.setEnabled(true);

                            }
                        }

                    } else {
                    }
                } else {
                    Toast.makeText(SignUpEmailPassActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SignUpUserNameCheckModel> call, Throwable t) {
                //progressBar.setVisibility(View.GONE);
                Toast.makeText(SignUpEmailPassActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                validatorProgress.setVisibility(View.GONE);
                emailSignUpEdt.setEnabled(true);
                emailSignUpEdt.requestFocus();
            }
        });

    }

}
