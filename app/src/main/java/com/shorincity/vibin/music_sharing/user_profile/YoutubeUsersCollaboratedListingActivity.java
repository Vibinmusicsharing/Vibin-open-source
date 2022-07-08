package com.shorincity.vibin.music_sharing.user_profile;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.adapters.CollaboratorListing;
import com.shorincity.vibin.music_sharing.model.CollabsList;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YoutubeUsersCollaboratedListingActivity extends AppCompatActivity {
    private RecyclerView rv_collabs;
    private ArrayList<CollabsList.UserData> maincollabslist;
    private ProgressBar progress_collabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_users_collaborated_listing);
        initView();
        progress_collabs.setVisibility(View.VISIBLE);
        getCollabListing();


    }

    private void getCollabListing() {
        DataAPI dataAPI = RetrofitAPI.getData();
        maincollabslist = new ArrayList<>();
        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        String user_token = SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
        Call<CollabsList> callback = dataAPI.getCollabsListing(
                token, user_token);
        callback.enqueue(new Callback<CollabsList>() {
            @Override
            public void onResponse(Call<CollabsList> call, Response<CollabsList> response) {
//                Log.d(TAG, "onResponse: "+response.headers().);
                Log.d("yash", "url : " + response.raw().request().url());
                Log.d("yash", "Header : " + response.raw().request().headers().toString());
                if (response != null && response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    progress_collabs.setVisibility(View.GONE);
                    ArrayList<CollabsList.UserData> privateplaylist = new ArrayList<>();
                    ArrayList<CollabsList.UserData> publicplaylist = new ArrayList<>();
                    if (response.body().getPrivate_playlist_colaborators() != null) {
                        privateplaylist.addAll(response.body().getPrivate_playlist_colaborators());
                        for (int i = 0; i < privateplaylist.size(); i++) {
                            privateplaylist.get(i).setType("Private Playlist");
                        }

                    }
                    if (response.body().getPublic_playlist_colaborators() != null) {
                        publicplaylist.addAll(response.body().getPublic_playlist_colaborators());
                        for (int i = 0; i < publicplaylist.size(); i++) {
                            publicplaylist.get(i).setType("Public Playlist");
                        }
                    }

                    maincollabslist.addAll(privateplaylist);
                    maincollabslist.addAll(publicplaylist);
                    if (maincollabslist.size() > 0) {
                        setAdapter();
                    }
                }
            }


            @Override
            public void onFailure(Call<CollabsList> call, Throwable t) {
                progress_collabs.setVisibility(View.GONE);
            }
        });

    }

    public void setAdapter() {
        rv_collabs.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        rv_collabs.setHasFixedSize(true);
        CollaboratorListing collaboratorListing = new CollaboratorListing(getApplicationContext(), maincollabslist);
        rv_collabs.setAdapter(collaboratorListing);
    }

    private void initView() {
        Toolbar mToolbar = findViewById(R.id.toolbar_likes);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rv_collabs = findViewById(R.id.rv_collabs);
        progress_collabs = findViewById(R.id.progress_collabs);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}