package com.shorincity.vibin.music_sharing.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.ripples.RippleButton;
import com.shorincity.vibin.music_sharing.ripples.listener.OnRippleCompleteListener;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.shorincity.vibin.music_sharing.utils.Utility;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class SignUpDobActivity extends AppCompatActivity implements View.OnClickListener {

    boolean isLeepYearSelected = false;
    private String genderStr;
    private String mAvatarLink;
    private int userId;
    private Spinner monthSpinner, daySpinner, yearSpinner;
    private ArrayList<String> yearList, monthList, dayList;
    private ArrayAdapter<String> daySpinnerAdapter, monthSpinnerAdapter, yearSpinnerAdapter;
    private String selectedYear, selectedMonth, selectedDay;
    private RippleButton nextBtn;
    private String selectedMonthInTwoDigit, selectedDayInTwoDigit;
    private int MIN_YEAR_LIMIT = 90;


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
        Bundle bundle = getIntent().getBundleExtra(AppConstants.INTENT_USER_DATA_BUNDLE);
        userId = bundle.getInt(AppConstants.INTENT_USER_ID, 0);
        genderStr = bundle.getString(AppConstants.INTENT_GENDER);
        mAvatarLink = bundle.getString(AppConstants.INTENT_GENDER_AVATAR_LINK);
        Logging.d("SUDOB bundle:"+bundle.toString());
    }

    private void setMonthsSpinner() {
        // Initializing a String Array
        String[] months = new String[]{"Month", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        monthList = new ArrayList<>(Arrays.asList(months));

        // Initializing an ArrayAdapter
        monthSpinnerAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, monthList) {
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

        int pastYear = currentYear - MIN_YEAR_LIMIT;


        for (int i = 0; i <= MIN_YEAR_LIMIT; i++) {
            yearList.add(String.valueOf(i + pastYear));
        }

        // Initializing an ArrayAdapter
        yearSpinnerAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, yearList) {
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
                gotoNextActivity();
                break;
        }
    }

    private void gotoNextActivity() {
        String dob = selectedDayInTwoDigit + "/" + selectedMonthInTwoDigit + "/" + selectedYear;

        Bundle bundle = getIntent().getBundleExtra(AppConstants.INTENT_USER_DATA_BUNDLE);
        bundle.putString(AppConstants.INTENT_DOB, dob);
        bundle.putString(AppConstants.INTENT_GENDER_AVATAR_LINK, mAvatarLink);

        Logging.d("SUDOB Avatar:"+mAvatarLink);
        Logging.d("SUDOB bundle:"+bundle.toString());

        startActivity(new Intent(this, SignUpPreferPlatformActivity.class).putExtra(AppConstants.INTENT_USER_DATA_BUNDLE, bundle));
    }

    private void validate() {

        if (TextUtils.isEmpty(selectedYear) || TextUtils.isEmpty(selectedMonth) || TextUtils.isEmpty(selectedDay))
            Utility.setViewEnable(nextBtn, false);
        else Utility.setViewEnable(nextBtn, true);

    }
}
