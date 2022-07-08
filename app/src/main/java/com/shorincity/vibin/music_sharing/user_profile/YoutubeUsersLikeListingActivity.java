package com.shorincity.vibin.music_sharing.user_profile;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.adapters.ViewPagerAdapter;
import com.shorincity.vibin.music_sharing.model.UserLikeList;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YoutubeUsersLikeListingActivity extends AppCompatActivity {
    private ArrayList<UserLikeList.GotLikes> userLikeIGotLists;
    private ArrayList<UserLikeList.GotLikes> userLikeILikedLists;
    private ProgressBar progress_like;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_users_like_listing);

        userLikeIGotLists = new ArrayList<>();
        userLikeILikedLists = new ArrayList<>();
        initView();
        progress_like.setVisibility(View.VISIBLE);

        getLikeListingApi();
    }


    private void getLikeListingApi() {
        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        String user_token = SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
        Call<UserLikeList> callback = dataAPI.getLikes_DisLikesListing(token, user_token);
        callback.enqueue(new Callback<UserLikeList>() {
            @Override
            public void onResponse(Call<UserLikeList> call, Response<UserLikeList> response) {
                if (response != null && response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    if (response.body().getProfile_likes_i_got() != null) {
                        userLikeIGotLists.addAll(response.body().getProfile_likes_i_got());
                    }
                    if (response.body().getProfile__i_liked() != null) {
                        userLikeILikedLists.addAll(response.body().getProfile__i_liked());
                    }
                    progress_like.setVisibility(View.GONE);
                    setupViewPager(userLikeIGotLists, userLikeILikedLists);
                }
            }

            @Override
            public void onFailure(Call<UserLikeList> call, Throwable t) {
                setupViewPager(userLikeIGotLists, userLikeILikedLists);
            }
        });

    }

    private void setupViewPager(ArrayList<UserLikeList.GotLikes> userLikeIGotLists, ArrayList<UserLikeList.GotLikes> userLikeILikedLists) {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager_likes);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_likes);
        String[] titles = {"Likes Received", "Liked Profiles"};
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), titles, "1", userLikeIGotLists, userLikeILikedLists);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initView() {
        Toolbar mToolbar = findViewById(R.id.toolbar_likes);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progress_like = findViewById(R.id.progress_like);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}