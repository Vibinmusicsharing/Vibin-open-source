package com.shorincity.vibin.music_sharing.viewmodel;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.model.APIResponse;
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
        list.add(new MusicLanguageModel(R.drawable.acoustic, R.drawable.acoustic_selected, "Acoustic", false));
        list.add(new MusicLanguageModel(R.drawable.alternative, R.drawable.alternative_selected, "Alternative", false));
        list.add(new MusicLanguageModel(R.drawable.blues, R.drawable.blues_selected, "Blues", false));
        list.add(new MusicLanguageModel(R.drawable.chillout, R.drawable.chillout_selected, "Chillout", false));
        list.add(new MusicLanguageModel(R.drawable.dance, R.drawable.dance_selected, "Dance", false));
        list.add(new MusicLanguageModel(R.drawable.electronic, R.drawable.electronic_selected, "Electronic", false));
        list.add(new MusicLanguageModel(R.drawable.electronica, R.drawable.electronica_selected, "Electronica", false));
        list.add(new MusicLanguageModel(R.drawable.folk, R.drawable.folk_selected, "Folk", false));
        list.add(new MusicLanguageModel(R.drawable.funk, R.drawable.funk_selected, "Funk", false));
        list.add(new MusicLanguageModel(R.drawable.heavy_metal, R.drawable.heavy_metal_selected, "Heavy Metal", false));
        list.add(new MusicLanguageModel(R.drawable.hiphop, R.drawable.hiphop_selected, "Hiphop", false));
        list.add(new MusicLanguageModel(R.drawable.house, R.drawable.house_selected, "House", false));
        list.add(new MusicLanguageModel(R.drawable.indie, R.drawable.indie_selected, "Indie", false));
        list.add(new MusicLanguageModel(R.drawable.jazz, R.drawable.jazz_selected, "Jazz", false));
        list.add(new MusicLanguageModel(R.drawable.metal, R.drawable.metal_selected, "Metal", false));
        list.add(new MusicLanguageModel(R.drawable.pop, R.drawable.pop_selected, "Pop", false));
        list.add(new MusicLanguageModel(R.drawable.rock, R.drawable.rock_selected, "Rock", false));

//        list.add(new MusicLanguageModel(R.drawable.classical, R.drawable.selected_classical, "Classical", false));
//        list.add(new MusicLanguageModel(R.drawable.country_side, R.drawable.selected_country_side, "Country side", false));
//        list.add(new MusicLanguageModel(R.drawable.disco, R.drawable.selected_disco, "Disco", false));
//        list.add(new MusicLanguageModel(R.drawable.k_pop, R.drawable.selected_k_pop, "K pop", false));
//        list.add(new MusicLanguageModel(R.drawable.romantic, R.drawable.selected_romantic, "Romantic", false));
//        list.add(new MusicLanguageModel(R.drawable.urban, R.drawable.selected_urban, "Urban", false));
//        list.add(new MusicLanguageModel(R.drawable.workout, R.drawable.selected_workout, "Workout", false));
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
        prefData.setSharedPrefString(AppConstants.PREF_USER_COVER, data.getCoverImage());
        prefData.setSharedPrefString(AppConstants.PREF_USER_DOB, data.getDob());
        prefData.setSharedPrefString(AppConstants.PREF_USER_GENDER, data.getGender());
        prefData.setSharedPrefString(AppConstants.PREF_USER_AVATAR_PROFILE, data.getAvatar_link());
        prefData.setSharedPrefBoolean(AppConstants.PREF_SHOW_RECENTLY_SONGS, data.getShowRecentlySongs());

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
