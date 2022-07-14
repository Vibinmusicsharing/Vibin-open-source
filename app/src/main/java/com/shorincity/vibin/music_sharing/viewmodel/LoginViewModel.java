package com.shorincity.vibin.music_sharing.viewmodel;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.shorincity.vibin.music_sharing.base.prefs.SharedPrefManager;
import com.shorincity.vibin.music_sharing.model.AdditionalSignUpModel;
import com.shorincity.vibin.music_sharing.model.Resource;
import com.shorincity.vibin.music_sharing.repository.LoginSignUpRepository;
import com.shorincity.vibin.music_sharing.utils.AppConstants;

public class LoginViewModel extends ViewModel {

    private LoginSignUpRepository loginSignUpRepository = new LoginSignUpRepository();
    public MutableLiveData<String> email = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();

    public String isValid() {
        if (TextUtils.isEmpty(email.getValue()) || (email.getValue() != null && !android.util.Patterns.EMAIL_ADDRESS.matcher(email.getValue()).matches())) {
            return "Please enter valid email id";
        } else if (TextUtils.isEmpty(password.getValue())) {
            return "Please enter the password";
        } else if (password.getValue().length() < 6) {
            return "Password must contain at least 6 characters!";
        } else {
            return "";
        }
    }

    public LiveData<Resource<AdditionalSignUpModel>> callLoginApi() {
        return loginSignUpRepository.callLoginApi(email.getValue(), password.getValue());
    }

    public void setPrefData(SharedPrefManager prefData, AdditionalSignUpModel data) {
        prefData.setSharedPrefBoolean(AppConstants.INTENT_IS_USER_LOGGEDIN, data.getUserLoggedIn());
        prefData.setSharedPrefInt(AppConstants.INTENT_USER_ID, data.getUserId());
        prefData.setSharedPrefString(AppConstants.INTENT_USER_TOKEN, data.getToken());
        prefData.setSharedPrefString(AppConstants.INTENT_USER_API_TOKEN, data.getApiToken());
        prefData.setSharedPrefString(AppConstants.INTENT_USER_PREFERRED_PLATFORM, data.getPreferredPlatform());
        prefData.setSharedPrefString(AppConstants.INTENT_USER_NAME, data.getUsername());
        prefData.setSharedPrefString(AppConstants.INTENT_FULL_NAME, data.getFullname());
        prefData.setSharedPrefString(AppConstants.PREF_USER_COVER, data.getCoverImage());
        prefData.setSharedPrefString(AppConstants.PREF_USER_DOB, data.getDob());
        prefData.setSharedPrefString(AppConstants.PREF_USER_GENDER, data.getGender());
        prefData.setSharedPrefString(AppConstants.PREF_USER_AVATAR_PROFILE, data.getAvatar_link());
        prefData.setSharedPrefBoolean(AppConstants.PREF_SHOW_RECENTLY_SONGS, data.getShowRecentlySongs());

    }
}
