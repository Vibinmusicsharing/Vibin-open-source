package com.shorincity.vibin.music_sharing.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.UI.youtube;
import com.shorincity.vibin.music_sharing.model.AdditionalSignUpModel;
import com.shorincity.vibin.music_sharing.model.SignUpResponse;
import com.shorincity.vibin.music_sharing.ripples.RippleButton;
import com.shorincity.vibin.music_sharing.ripples.listener.OnRippleCompleteListener;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.shorincity.vibin.music_sharing.utils.Utility;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpDobActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean isLeepYearSelected = false;
    private String genderStr, dobStr;
    private String mAvatarLink;
    private Spinner monthSpinner, daySpinner, yearSpinner;
    private ArrayList<String> yearList, monthList, dayList;
    private ArrayAdapter<String> daySpinnerAdapter;
    private String selectedYear, selectedMonth, selectedDay;
    private RippleButton nextBtn;
    private String selectedMonthInTwoDigit, selectedDayInTwoDigit;

    private Call<SignUpResponse> signUpCallback;
    private Call<AdditionalSignUpModel> registerCallback;
    private String preferredPlatform;
    private int userIdStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_dob);

        statusBarColorChange();
        getIntentData();

        inItViews();

        // Firstly, setting list of 90 years back from the current year
        setYearSpinner();

        inItListeners();

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

    private void getIntentData() {
        Bundle bundle = getIntent().getBundleExtra(AppConstants.INTENT_USER_DATA_BUNDLE);
        if (bundle != null) {
            if (bundle.containsKey(AppConstants.INTENT_GENDER))
                genderStr = bundle.getString(AppConstants.INTENT_GENDER);
            if (bundle.containsKey(AppConstants.INTENT_GENDER_AVATAR_LINK))
                mAvatarLink = bundle.getString(AppConstants.INTENT_GENDER_AVATAR_LINK);
        }
        Logging.d("SUDOB bundle:" + bundle.toString());
    }

    private void setMonthsSpinner() {
        // Initializing a String Array
        String[] months = new String[]{"Month", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        monthList = new ArrayList<>(Arrays.asList(months));

        // Initializing an ArrayAdapter
        // Disable the first item from Spinner
        // First item will be use for hint
        // Set the hint text color gray
        ArrayAdapter<String> monthSpinnerAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, monthList) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.LTGRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        monthSpinnerAdapter.setDropDownViewResource(R.layout.item_spinner);
        monthSpinner.setAdapter(monthSpinnerAdapter);

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);

                // If user change the default selection
                // First item is disable and it is used for hint
                if (position > 0) {

                    selectedMonth = selectedItemText;

                    NumberFormat formatter = new DecimalFormat("00");
                    selectedMonthInTwoDigit = formatter.format(position);


                    validate();

                    String thirtyMonthStr = "Jun Nov Apr Sep";

                    String thirtyOneMonthStr = "Jan Mar May Jul Aug Oct Dec";

                    dayList.clear();
                    dayList.add("Day");

                    if (thirtyMonthStr.contains(selectedItemText)) {
                        for (int i = 0; i < 30; i++) {
                            //dayList.add(i, String.valueOf(i + 1));
                            dayList.add(String.valueOf(i + 1));
                        }
                    } else if (thirtyOneMonthStr.contains(selectedItemText)) {
                        for (int i = 0; i < 31; i++) {
                            //dayList.add(i, String.valueOf(i + 1));
                            dayList.add(String.valueOf(i + 1));
                        }
                    } else if (selectedItemText.equalsIgnoreCase("Feb")) {

                        int febDays = isLeepYearSelected ? 29 : 28;

                        for (int i = 0; i < febDays; i++) {
                            //dayList.add(i, String.valueOf(i + 1));
                            dayList.add(String.valueOf(i + 1));
                        }
                    }

                    daySpinnerAdapter.notifyDataSetChanged();
                    daySpinner.setEnabled(true);
                    daySpinner.setAlpha(1f);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setDaySpinner() {

        String[] days = new String[]{"Day"};

        dayList = new ArrayList<>(Arrays.asList(days));

        // Initializing an ArrayAdapter
        daySpinnerAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, dayList) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.LTGRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        daySpinnerAdapter.setDropDownViewResource(R.layout.item_spinner);
        daySpinner.setAdapter(daySpinnerAdapter);

        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);

                // If user change the default selection
                // First item is disable and it is used for hint
                if (position > 0) {

                    selectedDay = selectedItemText;

                    NumberFormat formatter = new DecimalFormat("00");
                    selectedDayInTwoDigit = formatter.format(position);
                    validate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setYearSpinner() {

        String[] year = new String[]{"Year"};

        yearList = new ArrayList<>(Arrays.asList(year));

        final int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        int MIN_YEAR_LIMIT = 90;
        int pastYear = currentYear - MIN_YEAR_LIMIT;


        for (int i = 0; i <= MIN_YEAR_LIMIT; i++) {
            yearList.add(String.valueOf(i + pastYear));
        }

        // Initializing an ArrayAdapter
        // Disable the first item from Spinner
        // First item will be use for hint
        // Set the hint text color gray
        ArrayAdapter<String> yearSpinnerAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, yearList) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.LTGRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        yearSpinnerAdapter.setDropDownViewResource(R.layout.item_spinner);
        yearSpinner.setAdapter(yearSpinnerAdapter);

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if (position > 0) {

                    isLeepYearSelected = (Integer.valueOf(selectedItemText) % 4 == 0) ? true : false;

                    selectedYear = selectedItemText;
                    validate();
                    monthSpinner.setEnabled(true);
                    monthSpinner.setAlpha(1f);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void inItViews() {
        monthSpinner = (Spinner) findViewById(R.id.spinner_month);
        daySpinner = (Spinner) findViewById(R.id.spinner_day);
        yearSpinner = (Spinner) findViewById(R.id.spinner_year);
        nextBtn = findViewById(R.id.btn_next);

        monthSpinner.setEnabled(false);
        monthSpinner.setAlpha(0.5f);

        daySpinner.setEnabled(false);
        daySpinner.setAlpha(0.5f);

        yearList = new ArrayList<>();
        monthList = new ArrayList<>();
        dayList = new ArrayList<>();

        setYearSpinner();
        setMonthsSpinner();
        setDaySpinner();
    }

    private void inItListeners() {

        nextBtn.setOnRippleCompleteListener(new OnRippleCompleteListener() {
            @Override
            public void onComplete(View v) {
                onClick(v);
            }
        });
        // nextBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                // gotoNextActivity();
                if (!((signUpCallback != null && signUpCallback.isExecuted())
                        || (registerCallback != null && registerCallback.isExecuted()))) {
                    preferredPlatform = AppConstants.YOUTUBE;
                    registerUser();
                }
                break;
        }
    }

    private void validate() {

        if (TextUtils.isEmpty(selectedYear) || TextUtils.isEmpty(selectedMonth) || TextUtils.isEmpty(selectedDay))
            Utility.setViewEnable(nextBtn, false);
        else Utility.setViewEnable(nextBtn, true);

    }

    public void registerUser() {
        String dob = selectedDayInTwoDigit + "/" + selectedMonthInTwoDigit + "/" + selectedYear;
        // storing all the info in bundle travelled by previous screen
        Bundle bundle = getIntent().getBundleExtra(AppConstants.INTENT_USER_DATA_BUNDLE);
        String emailStr = bundle.getString(AppConstants.INTENT_EMAIL);
        String passStr = bundle.getString(AppConstants.INTENT_PASSWORD);
        String userNameStr = bundle.getString(AppConstants.INTENT_USER_NAME);
        String fullNameStr = bundle.getString(AppConstants.INTENT_FULL_NAME);
        String signUpMethodStr = bundle.getString(AppConstants.INTENT_SIGN_UP_METHOD);
        genderStr = bundle.getString(AppConstants.INTENT_GENDER);
        dobStr = dob;
        mAvatarLink = bundle.getString(AppConstants.INTENT_GENDER_AVATAR_LINK);
        String languages = bundle.getString(AppConstants.INTENT_LANGUAGE);
        String genres = bundle.getString(AppConstants.INTENT_GENRES);

        Logging.d("SUDP bundle 11:" + bundle.toString());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String timeOfRegistration = dateFormat.format(date);

        callSignUpAPI(emailStr,
                passStr,
                userNameStr,
                fullNameStr,
                signUpMethodStr,
                timeOfRegistration,
                "True",
                languages,
                genres);
    }

    public void callSignUpAPI(String email,
                              String password,
                              String username,
                              String fullname,
                              String typeOfRegistration,
                              String timeOfRegistration,
                              String pushNotifications,
                              String languages,
                              String genres) {

//        if (preferredPlatform.equalsIgnoreCase(AppConstants.SPOTIFY)) {
//            spotifyBtn.startAnimation();
//        } else {
//            youtubeBtn.startAnimation();
//        }
        final ProgressDialog showMe = new ProgressDialog(SignUpDobActivity.this);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();

        DataAPI dataAPI = RetrofitAPI.getData();

        // Logging.dLong("SUP mAvatarLink:"+mAvatarLink);
        signUpCallback = dataAPI.postSignUpFields(AppConstants.LOGIN_SIGNUP_HEADER, email, password,
                username, fullname, typeOfRegistration,
                timeOfRegistration, pushNotifications, mAvatarLink,
                genderStr, preferredPlatform, dobStr, languages, genres);


        signUpCallback.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response != null && response.body() != null
                        && response.body().getUserCreated().equalsIgnoreCase("true")) {
                    Logging.dLong("SignUp res:" + new Gson().toJson(response.body()));
                    userIdStr = response.body().getUserId();

                    SharedPrefManager.getInstance(SignUpDobActivity.this).setSharedPrefString(AppConstants.INTENT_AVATAR_LINK, mAvatarLink);
//                    postAdditionalFields(userIdStr, genderStr, preferredPlatform, dobStr);
                } else {
                    showMe.dismiss();
                    //resetButtonState();
                    Toast.makeText(SignUpDobActivity.this, response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                Logging.dLong("SignUp res:" + Log.getStackTraceString(t));
                showMe.dismiss();
                // resetButtonState();
                Toast.makeText(SignUpDobActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void postAdditionalFields(final int userId, String sex, final String platfrom, String dob) {
        DataAPI dataAPI = RetrofitAPI.getData();

        registerCallback = dataAPI.postAdditionalFields(AppConstants.LOGIN_SIGNUP_HEADER, userId, sex, platfrom, dob);
        registerCallback.enqueue(new Callback<AdditionalSignUpModel>() {
            @Override
            public void onResponse(Call<AdditionalSignUpModel> call, Response<AdditionalSignUpModel> response) {
                if (response != null && response.body() != null
                        && !TextUtils.isEmpty(response.body().getStatus())
                        && response.body().getStatus().equalsIgnoreCase("success")) {


                    // Storing Users' info in preferences for further use
                    SharedPrefManager.getInstance(SignUpDobActivity.this).setSharedPrefBoolean(AppConstants.INTENT_IS_USER_LOGGEDIN, true); // response.body().getUserLoggedIn()
                    SharedPrefManager.getInstance(SignUpDobActivity.this).setSharedPrefInt(AppConstants.INTENT_USER_ID, response.body().getUserId());
                    SharedPrefManager.getInstance(SignUpDobActivity.this).setSharedPrefString(AppConstants.INTENT_USER_TOKEN, response.body().getToken());
                    SharedPrefManager.getInstance(SignUpDobActivity.this).setSharedPrefString(AppConstants.INTENT_USER_API_TOKEN, response.body().getApiToken());
                    SharedPrefManager.getInstance(SignUpDobActivity.this).setSharedPrefString(AppConstants.INTENT_USER_PREFERRED_PLATFORM, response.body().getPreferredPlatform());
                    SharedPrefManager.getInstance(SignUpDobActivity.this).setSharedPrefString(AppConstants.INTENT_USER_NAME, response.body().getUsername());
                    SharedPrefManager.getInstance(SignUpDobActivity.this).setSharedPrefString(AppConstants.INTENT_FULL_NAME, response.body().getFullname());

                    Intent k = new Intent(SignUpDobActivity.this, youtube.class);
                    k.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(k);
                    finishAffinity();

//                    if (platfrom.equalsIgnoreCase(AppConstants.SPOTIFY)) {
//                        Intent intent = new Intent(SignUpDobActivity.this, spotify.class);
//                        startActivity(intent);
//                        finishAffinity();
//                    } else {
//                        Intent k = new Intent(SignUpDobActivity.this, youtube.class);
//                        startActivity(k);
//                        finishAffinity();
//                    }
                } else {

                    //resetButtonState();
                    Toast.makeText(SignUpDobActivity.this, response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AdditionalSignUpModel> call, Throwable t) {
                Toast.makeText(SignUpDobActivity.this,  getString(R.string.msg_network_failed), Toast.LENGTH_LONG).show();
                // resetButtonState();
            }
        });

    }
}
