package com.shorincity.vibin.music_sharing.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.databinding.ActivityVerificationAccountBinding;
import com.shorincity.vibin.music_sharing.model.APIResponse;
import com.shorincity.vibin.music_sharing.model.Resource;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.viewmodel.VerificationViewModel;

public class VerificationActivity extends AppCompatActivity {

    private CountDownTimer timer;
    private ActivityVerificationAccountBinding binding;
    private VerificationViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verification_account);
        viewModel = ViewModelProviders.of(this).get(VerificationViewModel.class);
        binding.setLifecycleOwner(this);

        statusBarColorChange();
        getIntentData();
        init();
    }

    private void getIntentData() {
        viewModel.setFullName(getIntent().getStringExtra(AppConstants.INTENT_FULL_NAME));
        viewModel.setEmailStr(getIntent().getStringExtra(AppConstants.INTENT_EMAIL));
        viewModel.setPassStr(getIntent().getStringExtra(AppConstants.INTENT_PASSWORD));
        viewModel.setSignUpMethodStr(getIntent().getStringExtra(AppConstants.INTENT_SIGN_UP_METHOD));
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

    private void init() {
        binding.flNext.setEnabled(viewModel.getCode().length() >= 6);

        startTimer();
        sendOtpToEmail(false);
        binding.flNext.setOnClickListener(v -> callVerifyApi());

        binding.otpView.setOtpCompletionListener(otp -> {
            viewModel.setCode(otp);
            binding.flNext.setEnabled(otp.length() >= 6);
        });
        binding.tvResend.setOnClickListener(v -> {
            sendOtpToEmail(true);
        });

    }

    private void startTimer() {
        timer = new CountDownTimer(120000, 1000) {

            public void onTick(long millisUntilFinished) {
                long minute = millisUntilFinished / (60 * 1000) % 60;
                long seconds = millisUntilFinished / 1000 % 60;

                binding.tvTimer.setText(String.format("%02d : %02d", minute, seconds));
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                setResendButton(true);
            }
        };
        timer.start();
    }

    private void setResendButton(boolean isEnable) {
        binding.setIsResendEnable(isEnable);
    }

    private void sendOtpToEmail(boolean isResend) {
        setResendButton(false);

        viewModel.sendVerificationOtp().observe(this, new Observer<Resource<APIResponse>>() {
            @Override
            public void onChanged(Resource<APIResponse> apiResponseResource) {
                if (apiResponseResource instanceof Resource.Success) {
                    Toast.makeText(VerificationActivity.this,
                            "Verification code send to your email", Toast.LENGTH_LONG).show();
                    if (isResend) {
                        startTimer();
                    }
                } else if (apiResponseResource instanceof Resource.Error) {
                    setResendButton(true);
                    String errorMsg = ((Resource.Error<APIResponse>) apiResponseResource).getErrorMsg();
                    Toast.makeText(VerificationActivity.this,
                            errorMsg != null ? errorMsg : "Something went wrong!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        /*DataAPI dataAPI = RetrofitAPI.getData();

        Call<APIResponse> callback = dataAPI.sendVerificationOtp(LOGIN_SIGNUP_HEADER, "");
        callback.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, retrofit2.Response<APIResponse> response) {
                //progressBar.setVisibility(View.GONE);
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
        });*/
    }

    private void callVerifyApi() {
        String errorMsg = viewModel.isValid();
        if (!TextUtils.isEmpty(errorMsg)) {
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            return;
        }
        ProgressDialog showMe = new ProgressDialog(VerificationActivity.this);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);

        viewModel.sendVerifyEmail().observe(this, apiResponseResource -> {
            if (apiResponseResource instanceof Resource.Loading) {
                showMe.show();
            } else if (apiResponseResource instanceof Resource.Success) {
                showMe.dismiss();
                gotoNextActivity();
            } else if (apiResponseResource instanceof Resource.Error) {
                showMe.dismiss();
                String errorMsg1 = ((Resource.Error<APIResponse>) apiResponseResource).getErrorMsg();
                Toast.makeText(VerificationActivity.this,
                        errorMsg1 != null ? errorMsg1 : "Something went wrong!",
                        Toast.LENGTH_LONG).show();
            }
        });

       /* DataAPI dataAPI = RetrofitAPI.getData();

        Call<APIResponse> callback = dataAPI.sendVerifyEmail(LOGIN_SIGNUP_HEADER, "", "");
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
        });*/
    }

    private void gotoNextActivity() {
        startActivity(new Intent(this, SignUpUserNameActivity.class)
                .putExtra(AppConstants.INTENT_FULL_NAME, viewModel.getFullName())
                .putExtra(AppConstants.INTENT_SIGN_UP_METHOD, viewModel.getSignUpMethodStr())
                .putExtra(AppConstants.INTENT_EMAIL, viewModel.getEmailStr())
                .putExtra(AppConstants.INTENT_PASSWORD, viewModel.getPassStr()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}
