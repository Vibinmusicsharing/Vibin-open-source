package com.shorincity.vibin.music_sharing.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.UI.youtube;
import com.shorincity.vibin.music_sharing.model.AdditionalSignUpModel;
import com.shorincity.vibin.music_sharing.model.SignUpResponse;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// SignUp Fourth Screen : Preferred Platform Screen
public class SignUpPreferPlatformActivity extends AppCompatActivity implements View.OnClickListener {
    private String mAvatarLink;
    private String genderStr, dobStr;

    private int userIdStr;
    private String preferredPlatform;
    private CircularProgressButton youtubeBtn, spotifyBtn;
    private Call<SignUpResponse> signUpCallback;
    private Call<AdditionalSignUpModel> registerCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_preferred_platform);

        statusBarColorChange();
        getIntentData();

        inItViews();

        inItListener();
    }

    private void statusBarColorChange(){
        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));
        }
    }

    private void inItViews() {
        youtubeBtn = findViewById(R.id.youtube_btn);
        spotifyBtn = findViewById(R.id.spotify_btn);

    }

    private void inItListener() {

        youtubeBtn.setOnClickListener(this);
        spotifyBtn.setOnClickListener(this);
    }

    private void getIntentData() {
        Bundle bundle = getIntent().getBundleExtra(AppConstants.INTENT_USER_DATA_BUNDLE);
        userIdStr = bundle.getInt(AppConstants.INTENT_USER_ID,0);
        genderStr = bundle.getString(AppConstants.INTENT_GENDER);
        dobStr = bundle.getString(AppConstants.INTENT_DOB);
        mAvatarLink= bundle.getString(AppConstants.INTENT_GENDER_AVATAR_LINK);

        Logging.d("SUDP bundle:"+mAvatarLink);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.youtube_btn:
                if(!((signUpCallback != null && signUpCallback.isExecuted())
                        || (registerCallback != null && registerCallback.isExecuted()))) {
                    preferredPlatform = AppConstants.YOUTUBE;
                    registerUser();
                }
                break;
            case R.id.spotify_btn:
                if(!((signUpCallback != null && signUpCallback.isExecuted())
                        || (registerCallback != null && registerCallback.isExecuted()))) {
                    preferredPlatform = AppConstants.SPOTIFY;
                    registerUser();
                }
                break;
        }
    }

    public void registerUser() {
        // storing all the info in bundle travelled by previous screen
        Bundle bundle = getIntent().getBundleExtra(AppConstants.INTENT_USER_DATA_BUNDLE);
        String emailStr = bundle.getString(AppConstants.INTENT_EMAIL);
        String passStr = bundle.getString(AppConstants.INTENT_PASSWORD);
        String userNameStr = bundle.getString(AppConstants.INTENT_USER_NAME);
        String fullNameStr = bundle.getString(AppConstants.INTENT_FULL_NAME);
        String signUpMethodStr = bundle.getString(AppConstants.INTENT_SIGN_UP_METHOD);
        genderStr = bundle.getString(AppConstants.INTENT_GENDER);
        dobStr = bundle.getString(AppConstants.INTENT_DOB);
        mAvatarLink= bundle.getString(AppConstants.INTENT_GENDER_AVATAR_LINK);

        Logging.d("SUDP bundle 11:"+bundle.toString());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String timeOfRegistration = dateFormat.format(date);

        callSignUpAPI(emailStr,
                passStr,
                userNameStr,
                fullNameStr,
                signUpMethodStr,
                timeOfRegistration,
                "True");
    }

    public void callSignUpAPI(String email,
                              String password,
                              String username,
                              String fullname,
                              String typeOfRegistration,
                              String timeOfRegistration,
                              String  pushNotifications) {

        if (preferredPlatform.equalsIgnoreCase(AppConstants.SPOTIFY)) {
            spotifyBtn.startAnimation();
        } else {
            youtubeBtn.startAnimation();
        }

        DataAPI dataAPI = RetrofitAPI.getData();

       // Logging.dLong("SUP mAvatarLink:"+mAvatarLink);
        signUpCallback = dataAPI.postSignUpFields(AppConstants.LOGIN_SIGNUP_HEADER,email,password,username,fullname,typeOfRegistration,timeOfRegistration,pushNotifications,mAvatarLink);


        signUpCallback.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response != null && response.body() != null
                 && response.body().getUserCreated().equalsIgnoreCase("true")) {
                    Logging.dLong("SignUp res:"+new Gson().toJson(response.body()));
                            userIdStr = response.body().getUserId();

                    SharedPrefManager.getInstance(SignUpPreferPlatformActivity.this).setSharedPrefString(AppConstants.INTENT_AVATAR_LINK,mAvatarLink);
                            postAdditionalFields(userIdStr,genderStr,preferredPlatform,dobStr);
                } else {
                    resetButtonState();
                    Toast.makeText(SignUpPreferPlatformActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                Logging.dLong("SignUp res:"+ Log.getStackTraceString(t));
                resetButtonState();
                Toast.makeText(SignUpPreferPlatformActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });

    }



    public void postAdditionalFields(final int userId, String sex, final String platfrom, String dob) {
        DataAPI dataAPI = RetrofitAPI.getData();

        registerCallback = dataAPI.postAdditionalFields(AppConstants.LOGIN_SIGNUP_HEADER,userId,sex,platfrom,dob);
        registerCallback.enqueue(new Callback<AdditionalSignUpModel>() {
            @Override
            public void onResponse(Call<AdditionalSignUpModel> call, Response<AdditionalSignUpModel> response) {
                if (response != null && response.body() != null
                        && !TextUtils.isEmpty(response.body().getStatus())
                        && response.body().getStatus().equalsIgnoreCase("success")) {


                    // Storing Users' info in preferences for further use
                    SharedPrefManager.getInstance(SignUpPreferPlatformActivity.this).setSharedPrefBoolean(AppConstants.INTENT_IS_USER_LOGGEDIN,true); // response.body().getUserLoggedIn()
                    SharedPrefManager.getInstance(SignUpPreferPlatformActivity.this).setSharedPrefInt(AppConstants.INTENT_USER_ID,response.body().getUserId());
                    SharedPrefManager.getInstance(SignUpPreferPlatformActivity.this).setSharedPrefString(AppConstants.INTENT_USER_TOKEN,response.body().getToken());
                    SharedPrefManager.getInstance(SignUpPreferPlatformActivity.this).setSharedPrefString(AppConstants.INTENT_USER_API_TOKEN,response.body().getApiToken());
                    SharedPrefManager.getInstance(SignUpPreferPlatformActivity.this).setSharedPrefString(AppConstants.INTENT_USER_PREFERRED_PLATFORM,response.body().getPreferredPlatform());
                    SharedPrefManager.getInstance(SignUpPreferPlatformActivity.this).setSharedPrefString(AppConstants.INTENT_USER_NAME,response.body().getUsername());
                    SharedPrefManager.getInstance(SignUpPreferPlatformActivity.this).setSharedPrefString(AppConstants.INTENT_FULL_NAME,response.body().getFullname());

                    Intent k = new Intent(SignUpPreferPlatformActivity.this, youtube.class);
                    startActivity(k);
                    finishAffinity();

                } else {
                    resetButtonState();
                    Toast.makeText(SignUpPreferPlatformActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<AdditionalSignUpModel> call, Throwable t) {
                Toast.makeText(SignUpPreferPlatformActivity.this,"Error: " +t.getMessage(),Toast.LENGTH_LONG).show();
                resetButtonState();
            }
        });

    }

    private void resetButtonState() {
        if (preferredPlatform.equalsIgnoreCase(AppConstants.SPOTIFY)) {
            spotifyBtn.revertAnimation();
            spotifyBtn.setBackgroundResource(R.drawable.spotify_button);

        } else {
            youtubeBtn.revertAnimation();
            youtubeBtn.setBackgroundResource(R.drawable.spotify_button);
        }
    }

}
