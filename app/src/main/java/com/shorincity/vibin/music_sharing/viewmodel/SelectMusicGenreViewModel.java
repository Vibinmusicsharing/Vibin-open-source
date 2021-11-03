package com.shorincity.vibin.music_sharing.viewmodel;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.activity.SelectMusicGenreActivity;
import com.shorincity.vibin.music_sharing.model.APIResponse;
import com.shorincity.vibin.music_sharing.model.AdditionalSignUpModel;
import com.shorincity.vibin.music_sharing.model.MusicLanguageModel;
import com.shorincity.vibin.music_sharing.model.Resource;
import com.shorincity.vibin.music_sharing.model.SignUpResponse;
import com.shorincity.vibin.music_sharing.repository.LoginSignUpRepository;
import com.shorincity.vibin.music_sharing.utils.AppConstants;

import java.util.ArrayList;

public class SelectMusicGenreViewModel extends ViewModel {
    private LoginSignUpRepository loginSignUpRepository = new LoginSignUpRepository();
    private ArrayList<MusicLanguageModel> list = new ArrayList<>();
    private ArrayList<MusicLanguageModel> copyList = new ArrayList<>();
    private int selectedCounter = 0;
    public MutableLiveData<String> search = new MutableLiveData<>();

    public SelectMusicGenreViewModel() {
        setList();
    }

    private void setList() {
        list.add(new MusicLanguageModel(R.drawable.blues, R.drawable.selected_blues, "Blues", false));
        list.add(new MusicLanguageModel(R.drawable.classical, R.drawable.selected_classical, "Classical", false));
        list.add(new MusicLanguageModel(R.drawable.country_side, R.drawable.selected_country_side, "Country side", false));
        list.add(new MusicLanguageModel(R.drawable.disco, R.drawable.selected_disco, "Disco", false));
        list.add(new MusicLanguageModel(R.drawable.hiphop, R.drawable.selected_hiphop, "Hiphop", false));
        list.add(new MusicLanguageModel(R.drawable.jazz, R.drawable.selected_jazz, "Jazz", false));
        list.add(new MusicLanguageModel(R.drawable.k_pop, R.drawable.selected_k_pop, "K pop", false));
        list.add(new MusicLanguageModel(R.drawable.metal, R.drawable.selected_metal, "Metal", false));
        list.add(new MusicLanguageModel(R.drawable.rock, R.drawable.selected_rock, "Rock", false));
        list.add(new MusicLanguageModel(R.drawable.romantic, R.drawable.selected_romantic, "Romantic", false));
        list.add(new MusicLanguageModel(R.drawable.urban, R.drawable.selected_urban, "Urban", false));
        list.add(new MusicLanguageModel(R.drawable.workout, R.drawable.selected_workout, "Workout", false));
        copyList.addAll(list);
    }

    public ArrayList<MusicLanguageModel> getList() {
        return list;
    }

    public void setSelectedCounter(boolean isIncrease) {
        if (isIncrease)
            selectedCounter++;
        else
            selectedCounter--;
    }

    public int getSelectedCounter() {
        return selectedCounter;
    }

    public void onSearch() {
        list.clear();
        if (!TextUtils.isEmpty(search.getValue())) {
            for (MusicLanguageModel mBean : copyList) {
                if (mBean.getName().toLowerCase().contains(search.getValue().toLowerCase())) {
                    list.add(mBean);
                }
            }
        } else {
            list.addAll(copyList);
        }
    }

    public String getSelectedItem() {
        ArrayList<String> selectedLanguages = new ArrayList<>();
        for (MusicLanguageModel mBean : list) {
            if (mBean.isSelected())
                selectedLanguages.add(mBean.getName());
        }
        if (selectedLanguages.size() < 3) {
            return "Please select at-least three languages";
        } else
            return TextUtils.join("|", selectedLanguages);
    }

    public void setPrefData(SharedPrefManager prefData, SignUpResponse data) {

        prefData.setSharedPrefBoolean(AppConstants.INTENT_IS_USER_LOGGEDIN, true); // response.body().getUserLoggedIn()
        prefData.setSharedPrefInt(AppConstants.INTENT_USER_ID, data.getUserId());
        prefData.setSharedPrefString(AppConstants.INTENT_USER_TOKEN, data.getToken());
        prefData.setSharedPrefString(AppConstants.INTENT_USER_API_TOKEN, data.getApiToken());
        prefData.setSharedPrefString(AppConstants.INTENT_USER_PREFERRED_PLATFORM, data.getPreferredPlatform());
        prefData.setSharedPrefString(AppConstants.INTENT_USER_NAME, data.getUsername());
        prefData.setSharedPrefString(AppConstants.INTENT_FULL_NAME, data.getFullname());


    }

    public LiveData<Resource<SignUpResponse>> postSignUpFields(String email, String password,
                                                               String username, String fullname, String typeOfRegistration,
                                                               String timeOfRegistration, String pushNotifications,
                                                               String genderStr, String dobStr, String languages, String genres) {
        return loginSignUpRepository.postSignUpFields(email, password,
                username, fullname, typeOfRegistration,
                timeOfRegistration, pushNotifications,
                genderStr, dobStr, languages, genres);
    }

    public LiveData<Resource<APIResponse>> postUpdateProfile(String token, String languages, String selectedGenre) {
        return loginSignUpRepository.postUpdateProfile(token, languages, selectedGenre);
    }
}
