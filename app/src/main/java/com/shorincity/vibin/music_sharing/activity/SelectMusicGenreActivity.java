package com.shorincity.vibin.music_sharing.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.UI.youtube;
import com.shorincity.vibin.music_sharing.adapters.SelectLanguageAdapter;
import com.shorincity.vibin.music_sharing.databinding.ActivitySelectMusicGenreBinding;
import com.shorincity.vibin.music_sharing.model.APIResponse;
import com.shorincity.vibin.music_sharing.model.MusicLanguageModel;
import com.shorincity.vibin.music_sharing.model.Resource;
import com.shorincity.vibin.music_sharing.model.SignUpResponse;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.shorincity.vibin.music_sharing.viewmodel.SelectMusicGenreViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectMusicGenreActivity extends AppCompatActivity {
    private ActivitySelectMusicGenreBinding binding;
    private SelectMusicGenreViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_music_genre);
        viewModel = ViewModelProviders.of(this).get(SelectMusicGenreViewModel.class);
        binding.setLifecycleOwner(this);
        binding.setViewmodel(viewModel);

        statusBarColorChange();
        init();
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

        binding.setIsNextButtonEnable(viewModel.getSelectedCounter() >= 3);

        binding.rvLanguages.setLayoutManager(new GridLayoutManager(this, 3));
        binding.rvLanguages.setAdapter(new SelectLanguageAdapter(viewModel.getList(), position -> {
            if (position != RecyclerView.NO_POSITION) {
                MusicLanguageModel mBean = viewModel.getList().get(position);
                viewModel.setSelectedCounter(!mBean.isSelected());

                mBean.setSelected(!mBean.isSelected());
                if (binding.rvLanguages.getAdapter() != null)
                    binding.rvLanguages.getAdapter().notifyItemChanged(position);

                binding.setIsNextButtonEnable(viewModel.getSelectedCounter() >= 3);
            }
        }));

        viewModel.search.observe(this, s -> {
            viewModel.onSearch();
            if (binding.rvLanguages.getAdapter() != null)
                binding.rvLanguages.getAdapter().notifyDataSetChanged();
        });

        binding.flNext.setOnClickListener(v -> {
            goToNextScreen();
        });
    }

    private void goToNextScreen() {
        String selectedGenre = viewModel.getSelectedItem();
        if (!selectedGenre.contains("|")) {
            Toast.makeText(SelectMusicGenreActivity.this, "Please select at-least three genre", Toast.LENGTH_LONG).show();
            return;
        }
        Bundle bundle = getIntent().getBundleExtra(AppConstants.INTENT_USER_DATA_BUNDLE);
        if (bundle != null && bundle.containsKey(AppConstants.INTENT_IS_FROM_GOOGLE)) {
            callPostUpdateProfile(selectedGenre);
        } else {
            registerUser(selectedGenre);
        }
    }


    private void registerUser(String selectedGenre) {
        // storing all the info in bundle travelled by previous screen
        Bundle bundle = getIntent().getBundleExtra(AppConstants.INTENT_USER_DATA_BUNDLE);
        String emailStr = bundle.getString(AppConstants.INTENT_EMAIL);
        String passStr = bundle.getString(AppConstants.INTENT_PASSWORD);
        String userNameStr = bundle.getString(AppConstants.INTENT_USER_NAME);
        String fullNameStr = bundle.getString(AppConstants.INTENT_FULL_NAME);
        String signUpMethodStr = bundle.getString(AppConstants.INTENT_SIGN_UP_METHOD);
        String genderStr = bundle.getString(AppConstants.INTENT_GENDER);
        String dob = bundle.getString(AppConstants.INTENT_DOB);
        String languages = bundle.getString(AppConstants.INTENT_LANGUAGE);

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
                languages,
                selectedGenre, genderStr, dob);
    }

    private void callSignUpAPI(String email,
                               String password,
                               String username,
                               String fullname,
                               String typeOfRegistration,
                               String timeOfRegistration,
                               String languages, String genres, String genderStr, String dobStr) {

        final ProgressDialog showMe = new ProgressDialog(SelectMusicGenreActivity.this);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);

        /*DataAPI dataAPI = RetrofitAPI.getData();

        // Logging.dLong("SUP mAvatarLink:"+mAvatarLink);
        signUpCallback = dataAPI.postSignUpFields(AppConstants.LOGIN_SIGNUP_HEADER, email, password,
                username, fullname, typeOfRegistration,
                timeOfRegistration, pushNotifications, "",
                genderStr, AppConstants.YOUTUBE, dobStr, languages, genres);


        signUpCallback.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                showMe.dismiss();
                if (response != null && response.body() != null
                        && response.body().getUserCreated().equalsIgnoreCase("true")) {
                    Logging.dLong("SignUp res:" + new Gson().toJson(response.body()));

                    SharedPrefManager.getInstance(SelectMusicGenreActivity.this).setSharedPrefBoolean(AppConstants.INTENT_IS_USER_LOGGEDIN, true); // response.body().getUserLoggedIn()
                    SharedPrefManager.getInstance(SelectMusicGenreActivity.this).setSharedPrefInt(AppConstants.INTENT_USER_ID, response.body().getUserId());
                    SharedPrefManager.getInstance(SelectMusicGenreActivity.this).setSharedPrefString(AppConstants.INTENT_USER_TOKEN, response.body().getToken());
                    SharedPrefManager.getInstance(SelectMusicGenreActivity.this).setSharedPrefString(AppConstants.INTENT_USER_API_TOKEN, response.body().getApiToken());
                    SharedPrefManager.getInstance(SelectMusicGenreActivity.this).setSharedPrefString(AppConstants.INTENT_USER_PREFERRED_PLATFORM, response.body().getPreferredPlatform());
                    SharedPrefManager.getInstance(SelectMusicGenreActivity.this).setSharedPrefString(AppConstants.INTENT_USER_NAME, response.body().getUsername());
                    SharedPrefManager.getInstance(SelectMusicGenreActivity.this).setSharedPrefString(AppConstants.INTENT_FULL_NAME, response.body().getFullname());

                    Intent k = new Intent(SelectMusicGenreActivity.this, youtube.class);
                    k.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(k);
                    finishAffinity();
//                    postAdditionalFields(userIdStr, genderStr, preferredPlatform, dobStr);
                } else {
                    //resetButtonState();
                    Toast.makeText(SelectMusicGenreActivity.this, response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                Logging.dLong("SignUp res:" + Log.getStackTraceString(t));
                showMe.dismiss();
                // resetButtonState();
                Toast.makeText(SelectMusicGenreActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });*/

        viewModel.postSignUpFields(email, password,
                username, fullname, typeOfRegistration,
                timeOfRegistration, "True",
                genderStr, dobStr, languages, genres).observe(this, signUpResponseResource -> {
            if (signUpResponseResource instanceof Resource.Loading) {
                showMe.show();
            } else if (signUpResponseResource instanceof Resource.Success) {
                showMe.dismiss();
                SignUpResponse data = ((Resource.Success<SignUpResponse>) signUpResponseResource).getData();
                viewModel.setPrefData(SharedPrefManager.getInstance(SelectMusicGenreActivity.this), data);
                Intent k = new Intent(SelectMusicGenreActivity.this, youtube.class);
                k.putExtra(AppConstants.PLAYLIST_UID, getIntent().getStringExtra(AppConstants.PLAYLIST_UID));
                k.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(k);
                finishAffinity();
            } else if (signUpResponseResource instanceof Resource.Error) {
                showMe.dismiss();
                String errorMsg = ((Resource.Error<SignUpResponse>) signUpResponseResource).getErrorMsg();
                Toast.makeText(SelectMusicGenreActivity.this,
                        errorMsg != null ? errorMsg : "Something went wrong!",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void callPostUpdateProfile(String selectedGenre) {
        final ProgressDialog showMe = new ProgressDialog(SelectMusicGenreActivity.this);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();

        DataAPI dataAPI = RetrofitAPI.getData();
        String token = SharedPrefManager.getInstance(this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
        Bundle bundle = getIntent().getBundleExtra(AppConstants.INTENT_USER_DATA_BUNDLE);
        String languages = bundle.getString(AppConstants.INTENT_LANGUAGE);
        /*dataAPI.postUpdateProfile(AppConstants.LOGIN_SIGNUP_HEADER, token,
                languages, selectedGenre).enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                if (response != null && response.body() != null &&
                        response.body().getStatus().equalsIgnoreCase("success")) {
                    Logging.dLong("SignUp res:" + new Gson().toJson(response.body()));

                    Intent k = new Intent(SelectMusicGenreActivity.this, youtube.class);
                    k.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(k);
                    finishAffinity();
//                    postAdditionalFields(userIdStr, genderStr, preferredPlatform, dobStr);
                } else {
                    showMe.dismiss();
                    //resetButtonState();
                    Toast.makeText(SelectMusicGenreActivity.this, response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                Logging.dLong("SignUp res:" + Log.getStackTraceString(t));
                showMe.dismiss();
                // resetButtonState();
                Toast.makeText(SelectMusicGenreActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });*/

        viewModel.postUpdateProfile(token, languages, selectedGenre).observe(this, apiResponseResource -> {
            if (apiResponseResource instanceof Resource.Loading) {
                showMe.show();
            } else if (apiResponseResource instanceof Resource.Success) {
                showMe.dismiss();
                APIResponse data = ((Resource.Success<APIResponse>) apiResponseResource).getData();
                Intent k = new Intent(SelectMusicGenreActivity.this, youtube.class);
                k.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(k);
                finishAffinity();
            } else if (apiResponseResource instanceof Resource.Error) {
                showMe.dismiss();
                String errorMsg = ((Resource.Error<APIResponse>) apiResponseResource).getErrorMsg();
                Toast.makeText(SelectMusicGenreActivity.this,
                        errorMsg != null ? errorMsg : "Something went wrong!",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }
}
