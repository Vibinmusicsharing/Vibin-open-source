package com.shorincity.vibin.music_sharing.activity;

import static com.shorincity.vibin.music_sharing.utils.AppConstants.LOGIN_SIGNUP_HEADER;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.UI.youtube;
import com.shorincity.vibin.music_sharing.model.APIResponse;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;

import retrofit2.Call;
import retrofit2.Callback;

public class VerificationActivity extends AppCompatActivity {

    private OtpView otpView;
    private AppCompatTextView tvTimer, tvResend;
    private FrameLayout flNext;
    private LinearLayout llResend;
    private CountDownTimer timer;
    private String code;
    private String fullName = "";
    private String emailStr, passStr, signUpMethodStr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_account);

        getIntentData();
        init();
    }

    private void getIntentData() {
        fullName = getIntent().getStringExtra(AppConstants.INTENT_FULL_NAME);
        emailStr = getIntent().getStringExtra(AppConstants.INTENT_EMAIL);
        passStr = getIntent().getStringExtra(AppConstants.INTENT_PASSWORD);
        signUpMethodStr = getIntent().getStringExtra(AppConstants.INTENT_SIGN_UP_METHOD);

        signUpMethodStr = TextUtils.isEmpty(signUpMethodStr) ? AppConstants.SIGNUP_BY_APP : signUpMethodStr;
    }

    private void init() {
        otpView = findViewById(R.id.otp_view);
        tvTimer = findViewById(R.id.tvTimer);
        tvResend = findViewById(R.id.tvResend);
        flNext = findViewById(R.id.flNext);
        llResend = findViewById(R.id.llResend);

        startTimer();
        sendOtpToEmail(false);
        flNext.setOnClickListener(v -> callVerifyApi());

        otpView.setOtpCompletionListener(otp -> code = otp);
        tvResend.setOnClickListener(v -> {
            sendOtpToEmail(true);
        });

    }

    private void startTimer() {
        timer = new CountDownTimer(120000, 1000) {

            public void onTick(long millisUntilFinished) {
                long minute = millisUntilFinished / (60 * 1000) % 60;
                long seconds = millisUntilFinished / 1000 % 60;

                tvTimer.setText(String.format("%02d : %02d", minute, seconds));
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                setResendButton(true);
            }
        };
        timer.start();
    }

    private void setResendButton(boolean isEnable) {
        if (isEnable) {
            llResend.setVisibility(View.VISIBLE);
            tvTimer.setVisibility(View.GONE);
            tvResend.setAlpha(1.0f);
        } else {
            llResend.setVisibility(View.GONE);
            tvTimer.setVisibility(View.VISIBLE);
            tvResend.setAlpha(0.5f);
        }
        tvResend.setEnabled(isEnable);
        tvResend.setClickable(isEnable);
    }

    private void sendOtpToEmail(boolean isResend) {
        setResendButton(false);
        DataAPI dataAPI = RetrofitAPI.getData();

        Call<APIResponse> callback = dataAPI.sendVerificationOtp(LOGIN_SIGNUP_HEADER, emailStr);
        callback.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, retrofit2.Response<APIResponse> response) {
                //progressBar.setVisibility(View.GONE);
                Logging.d("Send Noti response:" + response);
                if (response.body() != null) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        Toast.makeText(VerificationActivity.this,
                                "Verification code send to your email", Toast.LENGTH_LONG).show();
                        if (isResend) {
                            startTimer();
                        }
                    } else {
                        setResendButton(true);
                        Toast.makeText(VerificationActivity.this,
                                response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    setResendButton(true);
                    Logging.d("Verification", "Something went wrong ");
                    Toast.makeText(VerificationActivity.this,
                            response.body().getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                setResendButton(true);
                Logging.d("Verification", "Error : " + t.getMessage());
                Toast.makeText(VerificationActivity.this,
                        t.getMessage(), Toast.LENGTH_LONG).show();
                //Log.i("Error: " , "ADD NOTIFICATION "+t.getMessage());
            }
        });
    }

    private void callVerifyApi() {
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "Please Enter code!!!", Toast.LENGTH_LONG).show();
            return;
        } else if (code.length() < 6) {
            Toast.makeText(this, "Please Enter valid code!!!", Toast.LENGTH_LONG).show();
            return;
        }

        ProgressDialog showMe = new ProgressDialog(VerificationActivity.this);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();

        DataAPI dataAPI = RetrofitAPI.getData();

        Call<APIResponse> callback = dataAPI.sendVerifyEmail(LOGIN_SIGNUP_HEADER, emailStr, code);
        callback.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, retrofit2.Response<APIResponse> response) {
                //progressBar.setVisibility(View.GONE);
                Logging.d("Send Noti response:" + response);
                showMe.dismiss();
                if (response.body() != null) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        gotoNextActivity();
                    } else {
                        Toast.makeText(VerificationActivity.this,
                                response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Logging.d("Verification", "Something went wrong ");
                    Toast.makeText(VerificationActivity.this,
                            response.body().getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                showMe.dismiss();
                Logging.d("Verification", "Error : " + t.getMessage());
                //Log.i("Error: " , "ADD NOTIFICATION "+t.getMessage());
            }
        });
    }

    private void gotoNextActivity() {
        startActivity(new Intent(this, SignUpUserNameActivity.class)
                .putExtra(AppConstants.INTENT_FULL_NAME, fullName)
                .putExtra(AppConstants.INTENT_SIGN_UP_METHOD, signUpMethodStr)
                .putExtra(AppConstants.INTENT_EMAIL, emailStr)
                .putExtra(AppConstants.INTENT_PASSWORD, passStr));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}
