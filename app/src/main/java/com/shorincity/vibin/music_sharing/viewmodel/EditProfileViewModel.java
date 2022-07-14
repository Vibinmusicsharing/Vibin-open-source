package com.shorincity.vibin.music_sharing.viewmodel;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.base.prefs.SharedPrefManager;
import com.shorincity.vibin.music_sharing.model.APIResponse;
import com.shorincity.vibin.music_sharing.model.AdditionalSignUpModel;
import com.shorincity.vibin.music_sharing.model.CoverAvatarResponse;
import com.shorincity.vibin.music_sharing.model.MusicLanguageModel;
import com.shorincity.vibin.music_sharing.model.PreferredLangGenresModel;
import com.shorincity.vibin.music_sharing.model.Resource;
import com.shorincity.vibin.music_sharing.repository.LoginSignUpRepository;
import com.shorincity.vibin.music_sharing.utils.AppConstants;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;

public class EditProfileViewModel extends ViewModel {
    private final ArrayList<MusicLanguageModel> listLang = new ArrayList<>();
    private final ArrayList<MusicLanguageModel> copyListLang = new ArrayList<>();
    public MutableLiveData<String> search = new MutableLiveData<>();
    private int langSelectedCounter = 0;
    private ArrayList<MusicLanguageModel> listGenre = new ArrayList<>();
    private ArrayList<MusicLanguageModel> copyGenreList = new ArrayList<>();
    private int genreSelectedCounter = 0;
    private LoginSignUpRepository loginSignUpRepository = new LoginSignUpRepository();
    private String selectedLangs, selectedGenres;

    public EditProfileViewModel() {
        setLangsList();
        setGenreList();
    }

    private void setLangsList() {
        listLang.add(new MusicLanguageModel(R.drawable.bengali, R.drawable.selected_bengali, "Bengali", false));
        listLang.add(new MusicLanguageModel(R.drawable.english, R.drawable.selected_english, "English", false));
        listLang.add(new MusicLanguageModel(R.drawable.gujrati, R.drawable.selected_gujrati, "Gujarati", false));
        listLang.add(new MusicLanguageModel(R.drawable.hindi, R.drawable.selected_hindi, "Hindi", false));
        listLang.add(new MusicLanguageModel(R.drawable.kannada, R.drawable.selected_kannada, "Kannada", false));
        listLang.add(new MusicLanguageModel(R.drawable.malayalam, R.drawable.selected_malayalam, "Malayalam", false));
        listLang.add(new MusicLanguageModel(R.drawable.marathi, R.drawable.selected_marathi, "Marathi", false));
        listLang.add(new MusicLanguageModel(R.drawable.punjabi, R.drawable.selected_punjabi, "Punjabi", false));
        listLang.add(new MusicLanguageModel(R.drawable.tamil, R.drawable.selected_tamil, "Tamil", false));
        listLang.add(new MusicLanguageModel(R.drawable.telugu, R.drawable.selected_telugu, "Telugu", false));
        copyListLang.addAll(listLang);
    }

    private void setGenreList() {
        listGenre.add(new MusicLanguageModel(R.drawable.acoustic, R.drawable.acoustic_selected, "Acoustic", false));
        listGenre.add(new MusicLanguageModel(R.drawable.alternative, R.drawable.alternative_selected, "Alternative", false));
        listGenre.add(new MusicLanguageModel(R.drawable.blues, R.drawable.blues_selected, "Blues", false));
        listGenre.add(new MusicLanguageModel(R.drawable.chillout, R.drawable.chillout_selected, "Chillout", false));
        listGenre.add(new MusicLanguageModel(R.drawable.dance, R.drawable.dance_selected, "Dance", false));
        listGenre.add(new MusicLanguageModel(R.drawable.electronic, R.drawable.electronic_selected, "Electronic", false));
        listGenre.add(new MusicLanguageModel(R.drawable.electronica, R.drawable.electronica_selected, "Electronica", false));
        listGenre.add(new MusicLanguageModel(R.drawable.folk, R.drawable.folk_selected, "Folk", false));
        listGenre.add(new MusicLanguageModel(R.drawable.funk, R.drawable.funk_selected, "Funk", false));
        listGenre.add(new MusicLanguageModel(R.drawable.heavy_metal, R.drawable.heavy_metal_selected, "Heavy Metal", false));
        listGenre.add(new MusicLanguageModel(R.drawable.hiphop, R.drawable.hiphop_selected, "Hiphop", false));
        listGenre.add(new MusicLanguageModel(R.drawable.house, R.drawable.house_selected, "House", false));
        listGenre.add(new MusicLanguageModel(R.drawable.indie, R.drawable.indie_selected, "Indie", false));
        listGenre.add(new MusicLanguageModel(R.drawable.jazz, R.drawable.jazz_selected, "Jazz", false));
        listGenre.add(new MusicLanguageModel(R.drawable.metal, R.drawable.metal_selected, "Metal", false));
        listGenre.add(new MusicLanguageModel(R.drawable.pop, R.drawable.pop_selected, "Pop", false));
        listGenre.add(new MusicLanguageModel(R.drawable.rock, R.drawable.rock_selected, "Rock", false));
        copyGenreList.addAll(listGenre);
    }

    public ArrayList<String> getAvatarTypes() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Adventurer");
        list.add("Adventurer-neutral");
        list.add("Avataaars");
        list.add("Big-ears");
        list.add("Bottts");
        list.add("Croodles");
        list.add("Micah");
        list.add("Pixel-art");
        return list;
    }

    public ArrayList<String> getNameList() {
        ArrayList<String> names = new ArrayList<>();
        names.add("Guest");
        names.add("Test");
        names.add("Temp");
        names.add("Hello");
        names.add("Pinky");
        names.add("Minky");
        return names;
    }

    public ArrayList<MusicLanguageModel> getListLang() {
        return listLang;
    }

    public ArrayList<MusicLanguageModel> getListGenre() {
        return listGenre;
    }

    public void setLangSelectedCounter(boolean isIncrease) {
        if (isIncrease)
            langSelectedCounter++;
        else
            langSelectedCounter--;
    }

    public int getLangSelectedCounter() {
        return langSelectedCounter;
    }

    public void setGenreSelectedCounter(boolean isIncrease) {
        if (isIncrease)
            genreSelectedCounter++;
        else
            genreSelectedCounter--;
    }

    public int getGenreSelectedCounter() {
        return genreSelectedCounter;
    }

    public String getSelectedItem(List<MusicLanguageModel> list) {
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

    public LiveData<Resource<APIResponse>> postUpdateProfile(String token, String languages, String selectedGenre) {
        return loginSignUpRepository.postUpdateProfile(token, languages, selectedGenre);
    }

    public LiveData<Resource<PreferredLangGenresModel>> callPreferredLangGenres(String token) {
        return loginSignUpRepository.callPreferredLangGenres(token);
    }

    public LiveData<Resource<AdditionalSignUpModel>> callEditProfileDetails(String token, String dobUser,
                                                                            String gender, String username,
                                                                            String fullname, Boolean isRecentSongs) {
        return loginSignUpRepository.callEditProfileDetails(token, dobUser, gender, username, fullname, isRecentSongs);
    }

    public LiveData<Resource<CoverAvatarResponse>> callUpdateProfileOrCover(String token, String imageType,
                                                                            String avatarLink, MultipartBody.Part file) {
        return loginSignUpRepository.callUpdateProfileOrCover(token, imageType, avatarLink, file);
    }

    public void setPrefData(SharedPrefManager prefData, AdditionalSignUpModel data) {
        prefData.setSharedPrefString(AppConstants.INTENT_USER_NAME, data.getUsername());
        prefData.setSharedPrefString(AppConstants.INTENT_FULL_NAME, data.getFullname());
        if (!TextUtils.isEmpty(data.getCoverImage()))
            prefData.setSharedPrefString(AppConstants.PREF_USER_COVER, data.getCoverImage());
        prefData.setSharedPrefString(AppConstants.PREF_USER_DOB, data.getDob());
        prefData.setSharedPrefString(AppConstants.PREF_USER_GENDER, data.getGender());
        if (!TextUtils.isEmpty(data.getAvatar_link()))
            prefData.setSharedPrefString(AppConstants.PREF_USER_AVATAR_PROFILE, data.getAvatar_link());
        prefData.setSharedPrefBoolean(AppConstants.PREF_SHOW_RECENTLY_SONGS, data.getShowRecentlySongs());
    }

    public void setSelectedLangs(String selectedLangs) {
        this.selectedLangs = selectedLangs;
    }

    public void setSelectedGenres(String selectedGenres) {
        this.selectedGenres = selectedGenres;
    }

    public String getSelectedLangs() {
        return selectedLangs;
    }

    public String getSelectedGenres() {
        return selectedGenres;
    }
}
