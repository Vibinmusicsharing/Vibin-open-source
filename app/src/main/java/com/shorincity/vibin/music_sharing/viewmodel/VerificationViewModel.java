package com.shorincity.vibin.music_sharing.viewmodel;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.shorincity.vibin.music_sharing.model.APIResponse;
import com.shorincity.vibin.music_sharing.model.Resource;
import com.shorincity.vibin.music_sharing.repository.LoginSignUpRepository;
import com.shorincity.vibin.music_sharing.utils.AppConstants;

public class VerificationViewModel extends ViewModel {
    private LoginSignUpRepository loginSignUpRepository = new LoginSignUpRepository();
    private String code = "";
    private String fullName = "";
    private String emailStr, passStr, signUpMethodStr;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmailStr() {
        return emailStr;
    }

    public void setEmailStr(String emailStr) {
        this.emailStr = emailStr;
    }

    public String getPassStr() {
        return passStr;
    }

    public void setPassStr(String passStr) {
        this.passStr = passStr;
    }

    public String getSignUpMethodStr() {
        return signUpMethodStr;
    }

    public void setSignUpMethodStr(String signUpMethodStr) {
        this.signUpMethodStr = TextUtils.isEmpty(signUpMethodStr) ? AppConstants.SIGNUP_BY_APP : signUpMethodStr;
    }

    public String isValid() {
        if (TextUtils.isEmpty(code)) {
            return "Please Enter code!!!";
        } else if (code.length() < 6) {
            return "Please Enter valid code!!!";
        } else
            return "";
    }

    public LiveData<Resource<APIResponse>> sendVerificationOtp() {
        return loginSignUpRepository.sendVerificationOtp(emailStr);
    }

    public LiveData<Resource<APIResponse>> sendVerifyEmail() {
        return loginSignUpRepository.sendVerifyEmail(emailStr, code);
    }
}
