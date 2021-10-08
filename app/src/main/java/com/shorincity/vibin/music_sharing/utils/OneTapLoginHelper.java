package com.shorincity.vibin.music_sharing.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.activity.SignUpDobActivity;
import com.shorincity.vibin.music_sharing.model.SignUpResponse;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OneTapLoginHelper {
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    public static final int REQ_ONE_TAP = 999;

    public OneTapLoginHelper(Context context) {
        initOneTapClient(context);
    }

    private void initOneTapClient(Context context) {
        oneTapClient = Identity.getSignInClient(context);
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(context.getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();
    }

    public void signInClick(Activity activity) {
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(activity, result -> {
                    try {
                        activity.startIntentSenderForResult(
                                result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        Logging.d("Couldn't start One Tap UI: " + e.getLocalizedMessage());
                    }
                })
                .addOnFailureListener(activity, e -> {
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    Logging.d(e.getLocalizedMessage());
                });
    }

    public void handleResponse(Intent data, GoogleSignupCallback googleSignupCallback) {
        try {
            SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
            DataAPI dataAPI = RetrofitAPI.getData();
            dataAPI.postGoogleSignup(AppConstants.LOGIN_SIGNUP_HEADER,
                    credential.getId(),
                    credential.getDisplayName(),
                    credential.getProfilePictureUri() != null ? credential.getProfilePictureUri().toString() : "")
                    .enqueue(new Callback<SignUpResponse>() {
                        @Override
                        public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                            if (response != null && response.body() != null) {
                                googleSignupCallback.onResponse(response.body());
                            } else {
                                googleSignupCallback.onFailure(response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<SignUpResponse> call, Throwable t) {
                            Logging.dLong("SignUp res:" + Log.getStackTraceString(t));
                            googleSignupCallback.onFailure("Something went wrong!");
                        }
                    });
        } catch (ApiException e) {
            switch (e.getStatusCode()) {
                case CommonStatusCodes.CANCELED:
                    Logging.d("One-tap dialog was closed.");
                    // Don't re-prompt the user.
//                    showOneTapUI = false;
                    googleSignupCallback.onFailure("One-tap dialog was closed.");
                    break;
                case CommonStatusCodes.NETWORK_ERROR:
                    Logging.d("One-tap encountered a network error.");
                    // Try again or just ignore.
                    googleSignupCallback.onFailure("One-tap encountered a network error.");
                    break;
                default:
                    googleSignupCallback.onFailure("Something went wrong.");
                    Logging.d("Couldn't get credential from result."
                            + e.getLocalizedMessage());
                    break;
            }
        }
    }

    public interface GoogleSignupCallback {
        void onResponse(SignUpResponse signUpResponse);

        void onFailure(String msg);
    }
}
