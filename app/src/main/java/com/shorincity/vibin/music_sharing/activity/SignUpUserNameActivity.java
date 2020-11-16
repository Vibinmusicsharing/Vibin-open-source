package com.shorincity.vibin.music_sharing.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.model.SignUpUserNameCheckModel;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.shorincity.vibin.music_sharing.utils.Utility;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


// SignUp Second Screen : UserName, FullName
public class SignUpUserNameActivity  extends AppCompatActivity implements View.OnClickListener {

    String fullName = "";
    String emailStr, passStr, signUpMethodStr;
    EditText cust_full_name, usernameEdt;
    private Button nextBtn;
    boolean isUserNameVerified = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_username);

        statusBarColorChange();
        // function to get all the data passed by previous activity through intent
        getIntentData();

        // Calling to find Activity's child view here
        inItViews();

        // setting all the required listeners in this method
        inItListeners();
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

    private void getIntentData() {
        fullName = getIntent().getStringExtra(AppConstants.INTENT_FULL_NAME);
        emailStr = getIntent().getStringExtra(AppConstants.INTENT_EMAIL);
        passStr = getIntent().getStringExtra(AppConstants.INTENT_PASSWORD);
        signUpMethodStr = getIntent().getStringExtra(AppConstants.INTENT_SIGN_UP_METHOD);

        signUpMethodStr = TextUtils.isEmpty(signUpMethodStr)? AppConstants.SIGNUP_BY_APP : signUpMethodStr;
    }

    private void inItViews() {
        cust_full_name = (EditText) findViewById(R.id.cust_full_name);
        usernameEdt = (EditText) findViewById(R.id.tv_username);
        nextBtn = findViewById(R.id.btn_next);
       /* nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick(view);
            }
        });*/



        if(!TextUtils.isEmpty(fullName)) {
            cust_full_name.setText(fullName);
            cust_full_name.setEnabled(false);
            cust_full_name.setAlpha(0.5f);
        }
        usernameEdt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        Utility.setViewEnable(nextBtn,false);
    }

    private void inItListeners() {

        cust_full_name.addTextChangedListener(new EditTextWatch(this,cust_full_name));
        usernameEdt.addTextChangedListener(new EditTextWatch(this, usernameEdt));

      nextBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:

                // Going to next Screen which is Gender Screen
                gotoNextActivity();
            break;
        }
    }

    private void gotoNextActivity() {

        String userName = usernameEdt.getText().toString();
        String fullName = cust_full_name.getText().toString();

        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.INTENT_EMAIL,emailStr);
        bundle.putString(AppConstants.INTENT_PASSWORD,passStr);
        bundle.putString(AppConstants.INTENT_USER_NAME,userName);
        bundle.putString(AppConstants.INTENT_FULL_NAME,fullName);
        bundle.putString(AppConstants.INTENT_SIGN_UP_METHOD,signUpMethodStr);

        startActivity(new Intent(this, SignUpGenderActivity.class)
                .putExtra(AppConstants.INTENT_USER_DATA_BUNDLE, bundle));

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

        }

        @Override
        public void afterTextChanged(Editable editable) {


            switch (editText.getId()) {
                case R.id.tv_username:
                    Logging.d("Lengh--->"+usernameEdt.getText().toString().trim().length());
                    if(!TextUtils.isEmpty(usernameEdt.getText()) && usernameEdt.getText().toString().trim().length() >= 6)
                        callUserNameCheckerAPI(usernameEdt.getText().toString().trim());
                    else {
                        isUserNameVerified=false;
                        usernameEdt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    }
                    break;
                case R.id.cust_full_name:

                    break;
            }
            try {
                Logging.d("22 usernameEdt-->"+usernameEdt.getText().toString());
                Logging.d("22 usernameEdt-->"+cust_full_name.getText().toString());
                Logging.d("22 isUserNameVerified-->"+isUserNameVerified);
                Logging.d("22  length-->"+usernameEdt.getText().toString().trim().length());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (TextUtils.isEmpty(usernameEdt.getText().toString())
                    || usernameEdt.getText().toString().trim().length() < 6
                    || TextUtils.isEmpty(cust_full_name.getText().toString())
                    || !isUserNameVerified) {
                Utility.setViewEnable(nextBtn,false);
               Logging.d(" 11-setViewEnable false");
                isUserNameVerified=false;
                usernameEdt.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            } else {
               Logging.d(" 11-setViewEnable true");
                Utility.setViewEnable(nextBtn, true);
            }
        }
    }

    public void callUserNameCheckerAPI(String username) {
        //progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getData();

        Call<SignUpUserNameCheckModel> callback = dataAPI.signupUsernameChecker(AppConstants.LOGIN_SIGNUP_HEADER, username);
        callback.enqueue(new Callback<SignUpUserNameCheckModel>() {
            @Override
            public void onResponse(Call<SignUpUserNameCheckModel> call, Response<SignUpUserNameCheckModel> response) {
                //progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    Logging.d(" callUserNameCheckerAPI response-->"+new Gson().toJson(response));
                    Log.i("YOUTUBE_TRENDING RESULT", response.toString());

                    if(response.body() != null) {

                        if (response.body().getStatus().equalsIgnoreCase("checked")) {

                            Logging.d("exist-->"+response.body().getExists());
                            if (response.body().getExists()) {
                                isUserNameVerified = false;
                                usernameEdt.setError(getResources().getString(R.string.error_on_user_exist));
                            } else {
                                isUserNameVerified = true;

                                usernameEdt.setError(null);
                                usernameEdt.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.check_mark_small,0);
                            }


                            try {
                                Logging.d("11 usernameEdt-->"+usernameEdt.getText().toString());
                                Logging.d("11 usernameEdt-->"+cust_full_name.getText().toString());
                                Logging.d("11 isUserNameVerified-->"+isUserNameVerified);
                                Logging.d("11  length-->"+usernameEdt.getText().toString().trim().length());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (TextUtils.isEmpty(usernameEdt.getText().toString()) || TextUtils.isEmpty(cust_full_name.getText().toString()) || !isUserNameVerified ||   usernameEdt.getText().toString().trim().length() < 6) {
                                Utility.setViewEnable(nextBtn,false);
                               Logging.d(" -setViewEnable false 22");
                                usernameEdt.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                            } else {
                                Utility.setViewEnable(nextBtn, true);
                                usernameEdt.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.check_mark_small,0);
                              Logging.d(" -setViewEnable true 22");
                            }

                        }else{
                            Logging.d(" callUserNameCheckerAPI unchecked res");
                        }

                    } else {
                        Logging.d(" callUserNameCheckerAPI null resp");
                    }
                } else {
                    Toast.makeText(SignUpUserNameActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<SignUpUserNameCheckModel> call, Throwable t) {
                //progressBar.setVisibility(View.GONE);
                Logging.d(" callUserNameCheckerAPI onFailure");
                Toast.makeText(SignUpUserNameActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });

    }
}
