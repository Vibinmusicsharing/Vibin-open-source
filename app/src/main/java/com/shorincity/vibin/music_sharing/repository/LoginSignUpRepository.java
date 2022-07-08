package com.shorincity.vibin.music_sharing.repository;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.shorincity.vibin.music_sharing.model.APIResponse;
import com.shorincity.vibin.music_sharing.model.AdditionalSignUpModel;
import com.shorincity.vibin.music_sharing.model.CoverAvatarResponse;
import com.shorincity.vibin.music_sharing.model.PreferredLangGenresModel;
import com.shorincity.vibin.music_sharing.model.Resource;
import com.shorincity.vibin.music_sharing.model.SignUpResponse;
import com.shorincity.vibin.music_sharing.model.SignUpUserNameCheckModel;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginSignUpRepository {
    private static final String TAG = LoginSignUpRepository.class.getSimpleName();
    private DataAPI dataAPI;

    public LoginSignUpRepository() {
        dataAPI = RetrofitAPI.getData();
    }

    public LiveData<Resource<AdditionalSignUpModel>> callLoginApi(String email, String password) {
        final MutableLiveData<Resource<AdditionalSignUpModel>> data = new MutableLiveData<>();
        data.setValue(new Resource.Loading<>());
        Call<AdditionalSignUpModel> call = dataAPI.login(AppConstants.LOGIN_SIGNUP_HEADER, email, password, AppConstants.SIGNUP_BY_APP);
        call.enqueue(new Callback<AdditionalSignUpModel>() {
            @Override
            public void onResponse(Call<AdditionalSignUpModel> call, retrofit2.Response<AdditionalSignUpModel> response) {
                if (response.body() != null) {
                    if ((response.body().getStatus().equalsIgnoreCase("error") || response.body().getStatus().equalsIgnoreCase("failed"))
                            && !TextUtils.isEmpty(response.body().getMessage())) {
                        data.setValue(new Resource.Error<>(response.body().getMessage()));
                    } else {
                        data.setValue(new Resource.Success<>(response.body()));
                    }
                } else {
                    data.setValue(new Resource.Error<>("Something went wrong!"));
                }
            }

            @Override
            public void onFailure(Call<AdditionalSignUpModel> call, Throwable t) {
                data.setValue(new Resource.Error<>("Something went wrong!"));
            }
        });
        return data;
    }

    public LiveData<Resource<SignUpUserNameCheckModel>> callEmailCheckerAPI(String email) {
        final MutableLiveData<Resource<SignUpUserNameCheckModel>> data = new MutableLiveData<>();
        dataAPI.signupEmailChecker(AppConstants.LOGIN_SIGNUP_HEADER, email)
                .enqueue(new Callback<SignUpUserNameCheckModel>() {
                    @Override
                    public void onResponse(Call<SignUpUserNameCheckModel> call, retrofit2.Response<SignUpUserNameCheckModel> response) {
                        if (response.body() != null) {
                            if (response.body().getStatus().equalsIgnoreCase("checked")) {
                                data.setValue(new Resource.Success<>(response.body()));
                            } else {
                                data.setValue(new Resource.Error<>(response.body().getMessage()));
                            }
                        } else {
                            data.setValue(new Resource.Error<>("Something went wrong!"));
                        }
                    }

                    @Override
                    public void onFailure(Call<SignUpUserNameCheckModel> call, Throwable t) {
                        data.setValue(new Resource.Error<>("Something went wrong!"));
                    }
                });
        return data;
    }

    public LiveData<Resource<APIResponse>> sendVerificationOtp(String email) {
        final MutableLiveData<Resource<APIResponse>> data = new MutableLiveData<>();
        dataAPI.sendVerificationOtp(AppConstants.LOGIN_SIGNUP_HEADER, email)
                .enqueue(new Callback<APIResponse>() {
                    @Override
                    public void onResponse(Call<APIResponse> call, retrofit2.Response<APIResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getStatus().equalsIgnoreCase("success")) {
                                data.setValue(new Resource.Success<>(response.body()));
                            } else {
                                data.setValue(new Resource.Error<>(response.body().getMessage()));
                            }
                        } else {
                            Logging.d("Verification", "Something went wrong ");
                            data.setValue(new Resource.Error<>("Something went wrong!"));
                        }
                    }

                    @Override
                    public void onFailure(Call<APIResponse> call, Throwable t) {
                        data.setValue(new Resource.Error<>("Something went wrong!"));
                    }
                });
        return data;
    }

    public LiveData<Resource<APIResponse>> sendVerifyEmail(String email, String code) {
        final MutableLiveData<Resource<APIResponse>> data = new MutableLiveData<>();
        data.setValue(new Resource.Loading<>());
        dataAPI.sendVerifyEmail(AppConstants.LOGIN_SIGNUP_HEADER, email, code)
                .enqueue(new Callback<APIResponse>() {
                    @Override
                    public void onResponse(Call<APIResponse> call, retrofit2.Response<APIResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getStatus().equalsIgnoreCase("success")) {
                                data.setValue(new Resource.Success<>(response.body()));
                            } else {
                                data.setValue(new Resource.Error<>(response.body().getMessage()));
                            }
                        } else {
                            Logging.d("Verification", "Something went wrong ");
                            data.setValue(new Resource.Error<>("Something went wrong!"));
                        }
                    }

                    @Override
                    public void onFailure(Call<APIResponse> call, Throwable t) {
                        data.setValue(new Resource.Error<>("Something went wrong!"));
                    }
                });
        return data;
    }

    public LiveData<Resource<SignUpResponse>> postSignUpFields(String email, String password,
                                                               String username, String fullname, String typeOfRegistration,
                                                               String timeOfRegistration, String pushNotifications,
                                                               String genderStr, String dobStr, String languages, String genres) {
        final MutableLiveData<Resource<SignUpResponse>> data = new MutableLiveData<>();
        data.setValue(new Resource.Loading<>());
        dataAPI.postSignUpFields(AppConstants.LOGIN_SIGNUP_HEADER, email, password,
                username, fullname, typeOfRegistration,
                timeOfRegistration, pushNotifications, "",
                genderStr, dobStr, AppConstants.YOUTUBE, languages, genres)
                .enqueue(new Callback<SignUpResponse>() {
                    @Override
                    public void onResponse(Call<SignUpResponse> call, retrofit2.Response<SignUpResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getUserCreated().equalsIgnoreCase("true")) {
                                data.setValue(new Resource.Success<>(response.body()));
                            } else {
                                data.setValue(new Resource.Error<>(response.body().getMessage()));
                            }
                        } else {
                            data.setValue(new Resource.Error<>("Something went wrong!"));
                        }
                    }

                    @Override
                    public void onFailure(Call<SignUpResponse> call, Throwable t) {
                        data.setValue(new Resource.Error<>("Something went wrong!"));
                    }
                });
        return data;
    }

    public LiveData<Resource<APIResponse>> postUpdateProfile(String token, String languages, String selectedGenre) {
        final MutableLiveData<Resource<APIResponse>> data = new MutableLiveData<>();
        data.setValue(new Resource.Loading<>());
        dataAPI.postUpdateProfile(AppConstants.LOGIN_SIGNUP_HEADER, token,
                languages, selectedGenre)
                .enqueue(new Callback<APIResponse>() {
                    @Override
                    public void onResponse(Call<APIResponse> call, retrofit2.Response<APIResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getStatus().equalsIgnoreCase("success")) {
                                data.setValue(new Resource.Success<>(response.body()));
                            } else {
                                data.setValue(new Resource.Error<>(response.body().getMessage()));
                            }
                        } else {
                            data.setValue(new Resource.Error<>("Something went wrong!"));
                        }
                    }

                    @Override
                    public void onFailure(Call<APIResponse> call, Throwable t) {
                        data.setValue(new Resource.Error<>("Something went wrong!"));
                    }
                });
        return data;
    }

    public LiveData<Resource<PreferredLangGenresModel>> callPreferredLangGenres(String token) {
        final MutableLiveData<Resource<PreferredLangGenresModel>> data = new MutableLiveData<>();
        dataAPI.callPreferredLangGenres(AppConstants.LOGIN_SIGNUP_HEADER, token)
                .enqueue(new Callback<PreferredLangGenresModel>() {
                    @Override
                    public void onResponse(Call<PreferredLangGenresModel> call, Response<PreferredLangGenresModel> response) {
                        if (response.body() != null) {
                            if (response.body().getStatus().equalsIgnoreCase("success")) {
                                data.setValue(new Resource.Success<>(response.body()));
                            } else {
                                data.setValue(new Resource.Error<>(response.body().getMessage()));
                            }
                        } else {
                            data.setValue(new Resource.Error<>("Something went wrong!"));
                        }
                    }

                    @Override
                    public void onFailure(Call<PreferredLangGenresModel> call, Throwable t) {
                        data.setValue(new Resource.Error<>("Something went wrong!"));
                    }
                });
        return data;
    }

    public LiveData<Resource<AdditionalSignUpModel>> callEditProfileDetails(String token, String dobUser,
                                                                            String gender, String username,
                                                                            String fullname, Boolean isRecentSongs) {
        final MutableLiveData<Resource<AdditionalSignUpModel>> data = new MutableLiveData<>();

        dataAPI.callEditProfileDetails(AppConstants.LOGIN_SIGNUP_HEADER, token, dobUser, gender, username, fullname, isRecentSongs)
                .enqueue(new Callback<AdditionalSignUpModel>() {
                    @Override
                    public void onResponse(Call<AdditionalSignUpModel> call, Response<AdditionalSignUpModel> response) {
                        if (response.body() != null) {
                            if (response.body().getStatus().equalsIgnoreCase("success")) {
                                data.setValue(new Resource.Success<>(response.body()));
                            } else {
                                data.setValue(new Resource.Error<>(response.body().getMessage()));
                            }
                        } else {
                            data.setValue(new Resource.Error<>("Something went wrong!"));
                        }
                    }

                    @Override
                    public void onFailure(Call<AdditionalSignUpModel> call, Throwable t) {
                        data.setValue(new Resource.Error<>("Something went wrong!"));
                    }
                });
        return data;
    }

    public LiveData<Resource<CoverAvatarResponse>> callUpdateProfileOrCover(String token, String imageType,
                                                                            String avatarLink, MultipartBody.Part file) {
        RequestBody requestBodyToken =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, token);
        RequestBody requestBodyImageType =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, imageType);
        RequestBody requestBodyAvatarLink = null;
        if (avatarLink != null)
            requestBodyAvatarLink =
                    RequestBody.create(
                            okhttp3.MultipartBody.FORM, avatarLink);


        final MutableLiveData<Resource<CoverAvatarResponse>> data = new MutableLiveData<>();
        dataAPI.callUpdateProfileOrCover(AppConstants.LOGIN_SIGNUP_HEADER,
                requestBodyToken, requestBodyImageType, file, requestBodyAvatarLink).enqueue(new Callback<CoverAvatarResponse>() {
            @Override
            public void onResponse(Call<CoverAvatarResponse> call, Response<CoverAvatarResponse> response) {
                if (response.body() != null) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        data.setValue(new Resource.Success<>(response.body()));
                    } else {
                        data.setValue(new Resource.Error<>(response.body().getMessage()));
                    }
                } else {
                    data.setValue(new Resource.Error<>("Something went wrong!"));
                }
            }

            @Override
            public void onFailure(Call<CoverAvatarResponse> call, Throwable t) {
                data.setValue(new Resource.Error<>("Something went wrong!"));
            }
        });

        return data;
    }
}
