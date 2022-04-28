package com.shorincity.vibin.music_sharing.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import com.shorincity.vibin.music_sharing.UI.LoginAct;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.UI.youtube;
import com.shorincity.vibin.music_sharing.model.AdditionalSignUpModel;
import com.shorincity.vibin.music_sharing.ripples.RippleButton;
import com.shorincity.vibin.music_sharing.ripples.listener.OnRippleCompleteListener;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;

import retrofit2.Call;
import retrofit2.Callback;

// First Screen
public class LoginSignUpActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LoginSignUpActivity.class.getSimpleName();
    private static final int RC_GOOGLE_SIGN_IN = 007;
    private static final int RC_APP_SIGN_IN = 006;
    private Button btbGoogleSignIn;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private ProgressBar loading;
    private RippleButton emailSignInBtn, createAccBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);

        statusBarColorChange();
        // Checking for user is Logged In or nor
        if (SharedPrefManager.getInstance(this).getSharedPrefBoolean(AppConstants.INTENT_IS_USER_LOGGEDIN)) {

            // If user is already login then going to their selected platform directly.
            String platform = SharedPrefManager.getInstance(this).getSharedPrefString(AppConstants.INTENT_USER_PREFERRED_PLATFORM);
            Intent intent = new Intent(LoginSignUpActivity.this, youtube.class);
            startActivity(intent);
            finishAffinity();
        }

        // If user is not login then Screen view will appear.
        inItViews();

        // Initializing Google Variable inside the method
        initializeGoogleSignIn();

        // setting buttons' listeners
        inItListener();

    }

    private void initializeGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
    }

    private void inItListener() {
        btbGoogleSignIn.setOnClickListener(this);
        /*emailSignInBtn.setOnClickListener(this);
        createAccBtn.setOnClickListener(this);*/

        emailSignInBtn.setOnRippleCompleteListener(onRippleCompleteListener);
        createAccBtn.setOnRippleCompleteListener(onRippleCompleteListener);
    }

    private OnRippleCompleteListener onRippleCompleteListener = new OnRippleCompleteListener() {
        @Override
        public void onComplete(View view) {
            onClick(view);
        }
    };

    private void inItViews() {

        btbGoogleSignIn = (Button) findViewById(R.id.google_sign_up_btn);
        emailSignInBtn = findViewById(R.id.email_signin_btn);
        createAccBtn = findViewById(R.id.create_account_btn);
        loading = (ProgressBar) findViewById(R.id.loading_login);

    }

    @Override
    public void onClick(View view) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isAccepted = prefs.getBoolean(AppConstants.TERMS_COND_KEY, false);
        switch (view.getId()) {

            case R.id.google_sign_up_btn:
                if (isAccepted) {
                    googleSignIn();
                } else {
                    navigateTnCActivity(RESULT_CODE_GOOGLE);
                }

                break;

            case R.id.email_signin_btn:
                if (isAccepted) {
                    gotoLoginActivity();
                } else {
                    navigateTnCActivity(RESULT_CODE_EMAIL);
                }
                break;

            case R.id.create_account_btn:
                if (isAccepted) {
                    gotoSignUpActivity();
                } else {
                    navigateTnCActivity(RESULT_CODE_SIGN_UP);
                }
                break;
        }
    }

    private int RESULT_CODE_GOOGLE = 100;
    private int RESULT_CODE_EMAIL = 101;
    private int RESULT_CODE_SIGN_UP = 102;


    private void navigateTnCActivity(int resultCode) {
        Intent intent = new Intent(LoginSignUpActivity.this, TermsAndConditionsActivity.class);
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

    private void googleRevokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                //updateUI(false);
            }
        });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        hideProgressDialog();
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.e(TAG, "display name: " + acct.getDisplayName());

            try {
                String personName = acct.getDisplayName();
                String personPhotoUrl = acct.getPhotoUrl() != null ? acct.getPhotoUrl().toString() : "";
                String email = acct.getEmail();

                Log.e(TAG, "Name: " + personName + ", email: " + email + ", Image: " + personPhotoUrl);

                if (!TextUtils.isEmpty(personName) && !TextUtils.isEmpty(email)) {
                    googleSignOut();

                    postLogin(personName, email, "");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
        }
    }

    private void gotoLoginActivity() {
        startActivityForResult(new Intent(LoginSignUpActivity.this, LoginAct.class), RC_APP_SIGN_IN);
    }

    private void gotoSignUpActivity() {
        startActivity(new Intent(LoginSignUpActivity.this, SignUpEmailPassActivity.class));
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
        } else if (requestCode == RESULT_CODE_EMAIL && resultCode == RESULT_OK) {
            gotoLoginActivity();
        } else if (requestCode == RESULT_CODE_SIGN_UP && resultCode == RESULT_OK) {
            gotoSignUpActivity();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            Log.d(TAG, "Got cached sign-in");
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
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

    public void postLogin(final String fullname, final String email, final String password) {
        loading.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getData();

        Call<AdditionalSignUpModel> callback = dataAPI.login(AppConstants.LOGIN_SIGNUP_HEADER, email, password, AppConstants.SIGNUP_BY_Google);
        callback.enqueue(new Callback<AdditionalSignUpModel>() {
            @Override
            public void onResponse(Call<AdditionalSignUpModel> call, retrofit2.Response<AdditionalSignUpModel> response) {
                //progressBar.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);

                if (response != null && response.code() == 200) {


                    if (response.body().getUserLoggedIn()) {

                        Toast.makeText(LoginSignUpActivity.this, "Authentication Success!", Toast.LENGTH_SHORT).show();

                        // Storing Users' Info in preference
                        SharedPrefManager.getInstance(LoginSignUpActivity.this).setSharedPrefBoolean(AppConstants.INTENT_IS_USER_LOGGEDIN, response.body().getUserLoggedIn());
                        SharedPrefManager.getInstance(LoginSignUpActivity.this).setSharedPrefInt(AppConstants.INTENT_USER_ID, response.body().getUserId());
                        SharedPrefManager.getInstance(LoginSignUpActivity.this).setSharedPrefString(AppConstants.INTENT_USER_TOKEN, response.body().getToken());
                        SharedPrefManager.getInstance(LoginSignUpActivity.this).setSharedPrefString(AppConstants.INTENT_USER_API_TOKEN, response.body().getApiToken());
                        SharedPrefManager.getInstance(LoginSignUpActivity.this).setSharedPrefString(AppConstants.INTENT_USER_PREFERRED_PLATFORM, response.body().getPreferredPlatform());
                        SharedPrefManager.getInstance(LoginSignUpActivity.this).setSharedPrefString(AppConstants.INTENT_USER_NAME, response.body().getUsername());
                        SharedPrefManager.getInstance(LoginSignUpActivity.this).setSharedPrefString(AppConstants.INTENT_FULL_NAME, response.body().getFullname());

                        Intent k = new Intent(LoginSignUpActivity.this, youtube.class);
                        startActivity(k);
                        finish();

                    } else {
                        startActivity(new Intent(LoginSignUpActivity.this, SignUpEmailPassActivity.class).putExtra(AppConstants.INTENT_FULL_NAME, fullname).putExtra(AppConstants.INTENT_EMAIL, email).putExtra(AppConstants.INTENT_PASSWORD, password).putExtra(AppConstants.INTENT_SIGN_UP_METHOD, AppConstants.SIGNUP_BY_Google));

                    }

                }
            }

            @Override
            public void onFailure(Call<AdditionalSignUpModel> call, Throwable t) {

                loading.setVisibility(View.GONE);
            }
        });

    }

    public void navigateToFragment(Fragment fragment, String navigatingToFragmentName, String navigationFromFragmentName, boolean isAddToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.fragment_slide_left_enter, R.animator.fragment_slide_left_exit, R.animator.fragment_slide_right_enter, R.animator.fragment_slide_right_exit);
        ft.replace(R.id.frame_container, fragment, navigatingToFragmentName);
        if (isAddToBackStack) {
            ft.addToBackStack(navigationFromFragmentName);
        }
        ft.commitAllowingStateLoss();
    }

    private void statusBarColorChange() {
        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
    }

}
