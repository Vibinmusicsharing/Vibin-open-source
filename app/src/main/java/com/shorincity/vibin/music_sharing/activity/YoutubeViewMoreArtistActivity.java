package com.shorincity.vibin.music_sharing.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.adapters.YoutubeArtistPerticulerAdapter;
import com.shorincity.vibin.music_sharing.model.HomeYoutubeModel;

import java.util.ArrayList;

public class YoutubeViewMoreArtistActivity extends AppCompatActivity {
    RecyclerView artist_rv_perticular;
    TextView txt_artist_name;
    ProgressBar progressbar;
    String title;
    ArrayList<HomeYoutubeModel.YoutubeCustomModel> playlist;
    YoutubeArtistPerticulerAdapter youtubeArtistPerticulerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_more_artist);
        initView();
        playlist = new ArrayList<>();
        getIntentData();
        txt_artist_name.setText(title);
        setRecyclerView();
    }

    private void setRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);
        artist_rv_perticular.setLayoutManager(gridLayoutManager);
        artist_rv_perticular.setHasFixedSize(true);
        youtubeArtistPerticulerAdapter = new YoutubeArtistPerticulerAdapter(getApplicationContext(), playlist);
        artist_rv_perticular.setAdapter(youtubeArtistPerticulerAdapter);
    }

    private void getIntentData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            title = bundle.getString("title");
            playlist.addAll(bundle.getParcelableArrayList("playlist"));
        }


    }

    public void initView() {
        artist_rv_perticular = findViewById(R.id.artist_rv_perticular);
        txt_artist_name = findViewById(R.id.txt_artist_name);
        progressbar = findViewById(R.id.progressbar);
    }
}