package com.shorincity.vibin.music_sharing.base.network;

import com.bumptech.glide.RequestBuilder;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkInterceptor implements Interceptor {

    @Inject
    public String authToken;
    @Inject
    public String apiToken;
    private static final String AUTHORIZATION = "Authorization";
    private static final String API_TOKEN_KEY = "x-api-login-token";

    @Inject
    public NetworkInterceptor(String authToken, String apiToken) {
        this.authToken = authToken;
        this.apiToken = apiToken;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader(AUTHORIZATION, authToken);
        }

        if (apiToken != null && !apiToken.isEmpty()) {
            builder.addHeader(API_TOKEN_KEY, apiToken);
        }

        return chain.proceed(builder.method(chain.request().method(), chain.request().body()).build());
    }
}
