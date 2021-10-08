package com.shorincity.vibin.music_sharing.viewmodel;

import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.shorincity.vibin.music_sharing.model.Resource;
import com.shorincity.vibin.music_sharing.model.SignUpUserNameCheckModel;
import com.shorincity.vibin.music_sharing.repository.LoginSignUpRepository;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupEmailPassViewModel extends ViewModel {
    private LoginSignUpRepository loginSignUpRepository = new LoginSignUpRepository();
    public MutableLiveData<String> email = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();
    public MutableLiveData<String> confirmPassword = new MutableLiveData<>();
    public boolean isEmailVerified;

    public String validate() {
        if (TextUtils.isEmpty(email.getValue()) || (email.getValue() != null && !android.util.Patterns.EMAIL_ADDRESS.matcher(email.getValue()).matches()) || !isEmailVerified) {
            return "Please enter valid Email Id!";
        } else if (TextUtils.isEmpty(password.getValue())) {
            return "Please enter the Password!";
        } else if (!isPasswordValid(password.getValue())) {
            return "Invalid Password!";
        } else if (TextUtils.isEmpty(confirmPassword.getValue())) {
            return "Please enter the Confirm Password!";
        } else if (!password.getValue().equals(confirmPassword.getValue())) {
            return "Password and Confirm Password should be same!";
        } else {
            return "";
        }
    }

    public boolean isPasswordValid(String s) {

        // 1 Cap 1 Lower 1 number 1 alphanumeric
        //Pattern p = Pattern.compile("(?-i)(?=^.{8,}$)((?!.*\\s)(?=.*[A-Z])(?=.*[a-z]))(?=(1)(?=.*\\d)|.*[^A-Za-z0-9])^.*$");

        // 1 Cap 1 lower 1 number 1 special char
        Pattern p = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d.*)(?=.*\\W.*)[a-zA-Z0-9\\S]{8,20}$");

        // 1 cap 1 lower 1 number. only some special char (if entered)
        //Pattern p = Pattern.compile("^.*(?=.{8,})(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(^[a-zA-Z0-9@\\$=!:.#%]+$)");
        Matcher m = p.matcher(s);
        return (m.find() && m.group().equals(s));
    }

    public LiveData<Resource<SignUpUserNameCheckModel>> callEmailCheckerAPI() {
        return loginSignUpRepository.callEmailCheckerAPI(email.getValue());
    }
}
