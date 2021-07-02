package com.shorincity.vibin.music_sharing.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.shorincity.vibin.music_sharing.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public class AdditionalSignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private static EditText edtDob;
    private RelativeLayout dobHldr, genderMaleHldr, genderFemaleHldr;
    private Button spotifyBtn, youtubeBtn, submitBtn;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_sign_up);
        
        inItViews();
        
        inItListeners();

    }

    private void inItViews() {

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        dobHldr = findViewById(R.id.rl_dob);
        genderMaleHldr = findViewById(R.id.rl_user_male);
        genderFemaleHldr = findViewById(R.id.rl_user_female);
        spotifyBtn = findViewById(R.id.spotify_btn);
        youtubeBtn = findViewById(R.id.youtube_btn);
        submitBtn = findViewById(R.id.btn_submit);


        edtDob = findViewById(R.id.edt_dob);
        edtDob.setFocusable(false);
    }

    private void inItListeners() {
        edtDob.setOnClickListener(this);
        dobHldr.setOnClickListener(this);
        genderMaleHldr.setOnClickListener(this);
        genderFemaleHldr.setOnClickListener(this);
        spotifyBtn.setOnClickListener(this);
        youtubeBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_dob:
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
                break;

            case R.id.rl_user_male:
                if(!genderMaleHldr.isSelected() && !genderFemaleHldr.isSelected()) {
                    genderMaleHldr.setSelected(true);
                    genderFemaleHldr.setSelected(false);
                }
                else if(genderFemaleHldr.isSelected()) {
                    genderMaleHldr.setSelected(true);
                    genderFemaleHldr.setSelected(false);
                }
                break;

            case R.id.rl_user_female:
                if(!genderMaleHldr.isSelected() && !genderFemaleHldr.isSelected()) {
                    genderFemaleHldr.setSelected(true);
                    genderMaleHldr.setSelected(false);
                }
                else if(genderMaleHldr.isSelected()) {
                    genderFemaleHldr.setSelected(true);
                    genderMaleHldr.setSelected(false);
                }
                break;
            case R.id.youtube_btn:
                if(!youtubeBtn.isSelected() && !spotifyBtn.isSelected()) {
                    youtubeBtn.setSelected(true);
                    spotifyBtn.setSelected(false);
                }
                else if(spotifyBtn.isSelected()) {
                    youtubeBtn.setSelected(true);
                    spotifyBtn.setSelected(false);
                }
                break;

            case R.id.spotify_btn:
                if(!youtubeBtn.isSelected() && !spotifyBtn.isSelected()) {
                    spotifyBtn.setSelected(true);
                    youtubeBtn.setSelected(false);
                }
                else if(youtubeBtn.isSelected()) {
                    spotifyBtn.setSelected(true);
                    youtubeBtn.setSelected(false);
                }
                break;
            case R.id.btn_submit:

                if(isValidate()) {
                    int userId = 4;//getIntent().getIntExtra(AppConstants.INTENT_USER_ID,-1);
                    String dob = edtDob.getText().toString().trim();
                    String sex = genderMaleHldr.isSelected()?"male":"female";
                    String platform = youtubeBtn.isSelected()?"youtube":"spotify";


                    //if (userId > -1)
                        //postAdditionalFields(String.valueOf(userId),dob, sex, platform);
                }

                break;
        }
    }

    private boolean isValidate() {
        Boolean isValidate = false;
        if(TextUtils.isEmpty(edtDob.getText().toString().trim())) {
            showError(getResources().getString(R.string.error_age_choose));

        } else if(!genderMaleHldr.isSelected() && !genderFemaleHldr.isSelected()) {
            showError(getResources().getString(R.string.error_gender_choose));

        } else if(!youtubeBtn.isSelected() && !spotifyBtn.isSelected()) {
            showError(getResources().getString(R.string.error_platform_choose));

        } else
            isValidate = true;

        return isValidate;
    }

    //show calender
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
//            int year = c.get(Calendar.YEAR);
//            int month = c.get(Calendar.MONTH);
//            int day = c.get(Calendar.DAY_OF_MONTH);
            int year = 2000;
            int month = 0;
            int day = 1;

            DatePickerDialog datePickerDialog=new DatePickerDialog(getActivity(), this, year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            month = month+1;

            if (String.valueOf(day).length()==1 && String.valueOf(month).length()==1) {
                edtDob.setText("0"+day+"/0"+month+"/"+year);
            } else if (String.valueOf(day).length()==1) {
                edtDob.setText("0"+day+"/"+month+"/"+year);
            } else if (String.valueOf(month).length()==1) {
                edtDob.setText(day+"/0"+month+"/"+year);
            } else {
                edtDob.setText(day+"/"+month+"/"+year);
            }
        }
    }

/*
    public void postAdditionalFields(final String userId, String sex, String platfrom, String dob) {
        //progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getData();

        Call<SignUpModel> callback = dataAPI.postAdditionalFields(Integer.valueOf(userId),sex,platfrom,dob);
        callback.enqueue(new Callback<SignUpModel>() {
            @Override
            public void onResponse(Call<SignUpModel> call, Response<SignUpModel> response) {
                //progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    Log.i("YOUTUBE_TRENDING RESULT", response.toString());

                    if(response.body() != null) {

                        if (response.body().getAddedAdditionalFieldsUser()) {

                            if (spotifyBtn.isSelected()) {
                                Intent intent = new Intent(AdditionalSignUpActivity.this, spotify.class);
                                startActivity(intent);
                            } else {
                                Intent k = new Intent(AdditionalSignUpActivity.this, youtube.class);
                                startActivity(k);
                            }

                        }
                        Toast.makeText(AdditionalSignUpActivity.this, "Result Received ", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(AdditionalSignUpActivity.this, "Result Not Found: ", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(AdditionalSignUpActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<SignUpModel> call, Throwable t) {
                //progressBar.setVisibility(View.GONE);
                Toast.makeText(AdditionalSignUpActivity.this,"Error: " +t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }
*/

    private void showError(String errorMsg) {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, errorMsg , Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }


}
