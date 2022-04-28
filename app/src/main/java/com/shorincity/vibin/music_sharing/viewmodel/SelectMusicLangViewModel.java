package com.shorincity.vibin.music_sharing.viewmodel;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.model.MusicLanguageModel;

import java.util.ArrayList;

public class SelectMusicLangViewModel extends ViewModel {
    private final ArrayList<MusicLanguageModel> list = new ArrayList<>();
    private final ArrayList<MusicLanguageModel> copyList = new ArrayList<>();
    private int selectedCounter = 0;
    public MutableLiveData<String> search = new MutableLiveData<>();

    public SelectMusicLangViewModel() {
        setList();
    }

    private void setList() {
        list.add(new MusicLanguageModel(R.drawable.bengali, R.drawable.selected_bengali, "Bengali", false));
        list.add(new MusicLanguageModel(R.drawable.english, R.drawable.selected_english, "English", false));
        list.add(new MusicLanguageModel(R.drawable.gujrati, R.drawable.selected_gujrati, "Gujarati", false));
        list.add(new MusicLanguageModel(R.drawable.hindi, R.drawable.selected_hindi, "Hindi", false));
        list.add(new MusicLanguageModel(R.drawable.kannada, R.drawable.selected_kannada, "Kannada", false));
        list.add(new MusicLanguageModel(R.drawable.malayalam, R.drawable.selected_malayalam, "Malayalam", false));
        list.add(new MusicLanguageModel(R.drawable.marathi, R.drawable.selected_marathi, "Marathi", false));
        list.add(new MusicLanguageModel(R.drawable.punjabi, R.drawable.selected_punjabi, "Punjabi", false));
        list.add(new MusicLanguageModel(R.drawable.tamil, R.drawable.selected_tamil, "Tamil", false));
        list.add(new MusicLanguageModel(R.drawable.telugu, R.drawable.selected_telugu, "Telugu", false));
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
}
