package com.shorincity.vibin.music_sharing.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.adapters.AvatarListAdapter;
import com.shorincity.vibin.music_sharing.model.avatar.Avatar;
import com.shorincity.vibin.music_sharing.model.avatar.AvatarDetails;
import com.shorincity.vibin.music_sharing.ripples.RippleButton;
import com.shorincity.vibin.music_sharing.ripples.listener.OnRippleCompleteListener;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// SignUp Third Screen : Choose Gender
public class SignUpGenderActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private RelativeLayout genderMaleHldr, genderFemaleHldr, genderMaleFemaleHldr;
    private RippleButton nextBtn;
    private int userId;
    private final int MALE = 1;
    private final int FEMALE = 2;
    private final int MALE_FEMALE = 3;
    private int mCurrentGenderSelected = -1;

    private RecyclerView mRecyclerView;
    private AvatarListAdapter mAvatarListAdapter;

    private String mAvatarLink;
    private Call<AvatarDetails> avatarCallBack;

    private List<Avatar> mMaleList;
    private List<Avatar> mFeMaleList;
    private List<Avatar> mOtherList;
    private RelativeLayout progress_view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_gender);
        mContext = SignUpGenderActivity.this;

        statusBarColorChange();
        getIntentData();

        inItViews();
        initAvatar();
        inItListeners();
        callApi();
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

    private void callApi() {
        mMaleList = new ArrayList<>();
        mFeMaleList = new ArrayList<>();
        mOtherList = new ArrayList<>();

//        callGenderAPI(AppConstants.MALE);
//        callGenderAPI(AppConstants.FEMALE);
//        callGenderAPI(AppConstants.OTHER);
    }


    private void initAvatar() {
        mRecyclerView = (RecyclerView) findViewById(R.id.avatar_recyclerview);
        //Vertical RecyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Horizontal RecyclerView
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

    }

    private void populateUI(int gender) {

        if (mAvatarListAdapter == null) {
            mAvatarListAdapter = new AvatarListAdapter(mContext);
            mAvatarListAdapter.setCustomItemClickListener(new AvatarListAdapter.CustomItemClickListener() {
                @Override
                public void onItemClick(Avatar item, int position) {
                    mAvatarLink = item.getLink();
                    Logging.dLong("AvatarLink SL:"+mAvatarLink);
                }
            });
        }

        mRecyclerView.setAdapter(mAvatarListAdapter);
        if (gender == MALE) {
            if (mMaleList == null || mMaleList.size() == 0) {
                callGenderAPI(AppConstants.MALE);
            }
            mAvatarListAdapter.setList(mMaleList);
        } else if (gender == FEMALE) {
            if (mFeMaleList == null || mFeMaleList.size() == 0) {
                callGenderAPI(AppConstants.FEMALE);
            }
            mAvatarListAdapter.setList(mFeMaleList);
        } else if (gender == MALE_FEMALE) {
            if (mOtherList == null || mOtherList.size() == 0) {
                callGenderAPI(AppConstants.OTHER);
            }
            mAvatarListAdapter.setList(mOtherList);
        }
    }

    private void getIntentData() {
        userId = getIntent().getIntExtra(AppConstants.INTENT_USER_ID, 0);
    }

    private void inItViews() {
        progress_view = findViewById(R.id.progress_view);

        genderMaleFemaleHldr = findViewById(R.id.rl_user_male_female);
        genderMaleHldr = findViewById(R.id.rl_user_male);
        genderFemaleHldr = findViewById(R.id.rl_user_female);
        nextBtn = findViewById(R.id.btn_next);
        nextBtn.setOnRippleCompleteListener(new OnRippleCompleteListener() {
            @Override
            public void onComplete(View v) {
                onClick(v);
            }
        });

        setViewEnable(nextBtn, false);
        progress_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //for not click anyother while api call
            }
        });
    }

    private void inItListeners() {
        genderMaleFemaleHldr.setOnClickListener(this);
        genderMaleHldr.setOnClickListener(this);
        genderFemaleHldr.setOnClickListener(this);
        //nextBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_user_male:

                mCurrentGenderSelected = MALE;
                setViewEnable(nextBtn, true);

                genderMaleHldr.setBackgroundResource(R.drawable.gender_selector);
                genderMaleFemaleHldr.setBackgroundResource(0);
                genderFemaleHldr.setBackgroundResource(0);
                populateUI(mCurrentGenderSelected);
                findViewById(R.id.avtar).setVisibility(View.VISIBLE);
                break;
            case R.id.rl_user_male_female:
                mCurrentGenderSelected = MALE_FEMALE;
                setViewEnable(nextBtn, true);

                genderMaleFemaleHldr.setBackgroundResource(R.drawable.gender_selector);
                genderMaleHldr.setBackgroundResource(0);
                genderFemaleHldr.setBackgroundResource(0);
                populateUI(mCurrentGenderSelected);
                findViewById(R.id.avtar).setVisibility(View.VISIBLE);
                break;
            case R.id.rl_user_female:
                mCurrentGenderSelected = FEMALE;
                setViewEnable(nextBtn, true);

                genderFemaleHldr.setBackgroundResource(R.drawable.gender_selector);
                genderMaleHldr.setBackgroundResource(0);
                genderMaleFemaleHldr.setBackgroundResource(0);
                populateUI(mCurrentGenderSelected);
                findViewById(R.id.avtar).setVisibility(View.VISIBLE);
                break;
            case R.id.btn_next:

                gotoNextActivity();

                break;
        }
    }

    private void gotoNextActivity() {

        String gender = "";
        if (mCurrentGenderSelected == MALE) {
            gender = AppConstants.MALE;
        } else if (mCurrentGenderSelected == MALE_FEMALE) {
            gender = AppConstants.FEMALE;
        } else if (mCurrentGenderSelected == FEMALE) {
            gender = AppConstants.OTHER;
        }

        if (TextUtils.isEmpty(gender)) {
            Toast.makeText(mContext, getString(R.string.gender_empty), Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mAvatarLink)) {
            Toast.makeText(mContext, getString(R.string.avatar_empty), Toast.LENGTH_SHORT).show();
        } else {
            Bundle bundle = getIntent().getBundleExtra(AppConstants.INTENT_USER_DATA_BUNDLE);
            bundle.putString(AppConstants.INTENT_GENDER, gender);
            bundle.putString(AppConstants.INTENT_GENDER_AVATAR_LINK, mAvatarLink);

            Logging.dLong("mAvatarLink Gen:"+mAvatarLink);
            Logging.dLong("mAvatarLink bundle:"+bundle.toString());

            startActivity(new Intent(this, SignUpDobActivity.class).putExtra(AppConstants.INTENT_USER_DATA_BUNDLE, bundle));
        }

    }

    private void setViewEnable(View view, Boolean isEnable) {

        Float alphaValue = isEnable ? 1.0f : 0.5f;

        view.setEnabled(isEnable);
        view.setAlpha(alphaValue);

    }


    public void callGenderAPI(String gender) {

        progress_view.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getData();

        avatarCallBack = dataAPI.getAvatars(AppConstants.LOGIN_SIGNUP_HEADER, gender);
        avatarCallBack.enqueue(new Callback<AvatarDetails>() {
            @Override
            public void onResponse(Call<AvatarDetails> call, Response<AvatarDetails> response) {
                if (response != null && response.body() != null) {
                    progress_view.setVisibility(View.GONE);
                   // Logging.dLong("Avatar:" + new Gson().toJson(response.body()));
                    if (gender.equalsIgnoreCase(AppConstants.MALE)) {
                        mMaleList.addAll(response.body().getAvatars());
                    } else if (gender.equalsIgnoreCase(AppConstants.FEMALE)) {
                        mFeMaleList.addAll(response.body().getAvatars());
                    } else if (gender.equalsIgnoreCase(AppConstants.OTHER)) {
                        mOtherList.addAll(response.body().getAvatars());
                    }
                } else {
                    Toast.makeText(SignUpGenderActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AvatarDetails> call, Throwable t) {
                progress_view.setVisibility(View.GONE);
                Toast.makeText(SignUpGenderActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });

    }

}
