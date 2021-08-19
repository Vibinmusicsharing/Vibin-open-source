package com.shorincity.vibin.music_sharing.UI;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.activity.SignUpEmailPassActivity;
import com.shorincity.vibin.music_sharing.activity.TermsAndConditionsActivity;
import com.shorincity.vibin.music_sharing.activity.WebviewActivity;
import com.shorincity.vibin.music_sharing.fragment.ErrorDailogFragment;
import com.shorincity.vibin.music_sharing.model.AdditionalSignUpModel;
import com.shorincity.vibin.music_sharing.ripples.RippleButton;
import com.shorincity.vibin.music_sharing.ripples.listener.OnRippleCompleteListener;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;

import retrofit2.Call;
import retrofit2.Callback;

// login activity_youtube_2

public class LoginAct extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    Context mContext;
    Animation frombottom, fromtop;
    RippleButton btnLogin_login,btnjoin_login;
    TextView textView1;
    EditText password_login, email_login;
    ProgressBar loading;
    TextView text, btnForgotPass ;
    RippleButton google_sign_up_btn;
    private int RESULT_CODE_GOOGLE = 100;
    private static final int RC_GOOGLE_SIGN_IN = 007;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;
    private ProgressDialog mProgressDialog;
    String signUpMethod="";


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = LoginAct.this;
        frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        fromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        text = findViewById(R.id.text);

        btnForgotPass = findViewById(R.id.tv_forgot);

        google_sign_up_btn = findViewById(R.id.google_sign_up_btn);

        loading = (ProgressBar) findViewById(R.id.loading_login);
        btnjoin_login =  findViewById(R.id.btnjoin_login);
        btnLogin_login = (RippleButton) findViewById(R.id.btnlogin_login);
        //btnLogin_login.setOnRippleCompleteListener(onRippleCompleteListener);

        textView1 = (TextView) findViewById(R.id.textView1);

        //btnjoin_login.startAnimation(frombottom);
        //btnLogin_login.startAnimation(frombottom);

        email_login = (EditText) findViewById(R.id.email_login);
        password_login = (EditText) findViewById(R.id.password_login);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage
                (this, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        //email_login.startAnimation(fromtop);
        //password_login.startAnimation(fromtop);
        // text.startAnimation(frombottom);
        // btnForgotPass.startAnimation(frombottom);
//        btnjoin_login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(LoginAct.this, SignUpEmailPassActivity.class);
//                intent.putExtra(AppConstants.INTENT_SIGN_UP_METHOD, AppConstants.SIGNUP_BY_APP);
//                startActivity(intent);
//                finish();
//            }
//        });

        btnjoin_login.setOnRippleCompleteListener(new OnRippleCompleteListener() {
            @Override
            public void onComplete(View v) {
                Intent intent = new Intent(LoginAct.this, SignUpEmailPassActivity.class);
                intent.putExtra(AppConstants.INTENT_SIGN_UP_METHOD, AppConstants.SIGNUP_BY_APP);
                startActivity(intent);
                finish();
            }
        });

        //google_sign_up_btn.setOnRippleCompleteListener(onRippleCompleteListener);
        //btnLogin_login.setOnRippleCompleteListener(onRippleCompleteListener);
        btnLogin_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        btnForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginAct.this, WebviewActivity.class).putExtra(AppConstants.INTENT_WEBVIEW_URL, AppConstants.FORGOT_PASS_URL));
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
        Intent intent = new Intent(LoginAct.this, TermsAndConditionsActivity.class);
        startActivityForResult(intent,resultCode);
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

    /* private OnRippleCompleteListener onRippleCompleteListener = new OnRippleCompleteListener() {
        @Override
        public void onComplete(View view) {
            login();
        }
    };*/

    // login fucntion
    private void login() {
        String email = email_login.getText().toString().trim();
        String password = password_login.getText().toString().trim();

       /* InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) {

            hideSoftKeyboard(LoginAct.this);
        } else {

        }*/


        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter valid email id", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter the password", Toast.LENGTH_SHORT).show();

        } else if (password.length() < 6) {
            Toast.makeText(this, "Password must contain at least 6 characters!", Toast.LENGTH_SHORT).show();

        } else {
            loading.setVisibility(View.VISIBLE);
            signUpMethod = AppConstants.SIGNUP_BY_APP;
            postLogin(email, password);
        }

    }


    public void postLogin(final String email, final String password) {
        loading.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getData();

        Call<AdditionalSignUpModel> callback = dataAPI.login(AppConstants.LOGIN_SIGNUP_HEADER, email, password, signUpMethod);
        callback.enqueue(new Callback<AdditionalSignUpModel>() {
            @Override
            public void onResponse(Call<AdditionalSignUpModel> call, retrofit2.Response<AdditionalSignUpModel> response) {
                loading.setVisibility(View.INVISIBLE);

                Log.d("resp", String.valueOf(response));

                if (response != null && response.body() != null) {

                    if ((response.body().getStatus().equalsIgnoreCase("error") || response.body().getStatus().equalsIgnoreCase("failed"))
                            && !TextUtils.isEmpty(response.body().getMessage())) {
                        Toast.makeText(LoginAct.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        //Toast.makeText(LoginAct.this, "Password is Incorrect", Toast.LENGTH_SHORT).show();
                    } else {
                        if (response.body().getUserLoggedIn()) {

                            SharedPrefManager.getInstance(LoginAct.this).setSharedPrefBoolean(AppConstants.INTENT_IS_USER_LOGGEDIN, response.body().getUserLoggedIn());
                            SharedPrefManager.getInstance(LoginAct.this).setSharedPrefInt(AppConstants.INTENT_USER_ID, response.body().getUserId());
                            SharedPrefManager.getInstance(LoginAct.this).setSharedPrefString(AppConstants.INTENT_USER_TOKEN, response.body().getToken());
                            SharedPrefManager.getInstance(LoginAct.this).setSharedPrefString(AppConstants.INTENT_USER_API_TOKEN, response.body().getApiToken());
                            SharedPrefManager.getInstance(LoginAct.this).setSharedPrefString(AppConstants.INTENT_USER_PREFERRED_PLATFORM, response.body().getPreferredPlatform());
                            SharedPrefManager.getInstance(LoginAct.this).setSharedPrefString(AppConstants.INTENT_USER_NAME, response.body().getUsername());
                            SharedPrefManager.getInstance(LoginAct.this).setSharedPrefString(AppConstants.INTENT_FULL_NAME, response.body().getFullname());


                            Intent k = new Intent(LoginAct.this, youtube.class);
                            startActivity(k);
                            finishAffinity();

                        } else if (!response.body().getUserLoggedIn() && response.body().getStatus().equalsIgnoreCase("change_login_type")) {

                            showErrorDialog(response.body().getMessage());

                        } else {
                            Toast.makeText(LoginAct.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    Toast.makeText(LoginAct.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
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

    /*public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }*/

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
                String email = acct.getEmail();
                String [] pass = personName.split("\\s");
                String password = pass[0]+"123@";
                signUpMethod = AppConstants.SIGNUP_BY_Google;

                Logging.d("TEST", "Name: " + personName + ", email: " + email + ", Image: " + personPhotoUrl);

                if (!TextUtils.isEmpty(personName) && !TextUtils.isEmpty(email)) {
                    googleSignOut();

                    postLogin(email, password);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Logging.d("TEST", "onConnectionFailed:" + connectionResult);
    }
}
