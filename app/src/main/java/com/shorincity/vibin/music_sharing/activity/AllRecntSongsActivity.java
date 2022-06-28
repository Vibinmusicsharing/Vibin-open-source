package com.shorincity.vibin.music_sharing.activity;

import static com.shorincity.vibin.music_sharing.utils.AppConstants.USER_ID;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.VibinApplication;
import com.shorincity.vibin.music_sharing.adapters.RecentPlayedAdapter;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.model.RecentSongModel;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.shorincity.vibin.music_sharing.youtube_files.PlayYoutubeVideoActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Screen to display all the songs played by user recently
public class AllRecntSongsActivity extends AppCompatActivity {

    private ArrayList<RecentSongModel> recentSongList;
    private RecentPlayedAdapter recentPlayedAdapter;
    private RecyclerView recentSongRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_recnt_songs);

        recentSongRv = (RecyclerView) findViewById(R.id.rv_recently_played);
        statusBarColorChange();
        setListAdapter();

        callGetRecentAllSongAPI(getIntent().getIntExtra(USER_ID, -1));
    }

    private void statusBarColorChange() {
        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
    }

    private void setListAdapter() {
        recentSongList = new ArrayList<>();

        recentSongRv.setLayoutManager(new LinearLayoutManager(AllRecntSongsActivity.this, LinearLayoutManager.VERTICAL, false));
        recentSongRv.setHasFixedSize(true);

        recentPlayedAdapter = new RecentPlayedAdapter(AllRecntSongsActivity.this, recentSongList);
        recentPlayedAdapter.setCustomItemClickListener(new RecentPlayedAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                try {
                    if (recentSongList.get(position).getSongType().equalsIgnoreCase(AppConstants.YOUTUBE)) {
                        ArrayList<PlaylistDetailModel> playlist;
                        playlist = new ArrayList<>();
                        for (int i = 0; i < recentSongList.size(); i++) {
                            playlist.add(new PlaylistDetailModel(
                                    recentSongList.get(i).getSongName(),
                                    recentSongList.get(i).getSongThumbnail(),
                                    recentSongList.get(i).getSongId()
                            ));
                        }
                        Bundle bundle = new Bundle();
                        bundle.putInt("position", position);
                        bundle.putString("title", recentSongList.get(position).getSongName());
                        bundle.putString("description", "");
                        bundle.putString("thumbnail", recentSongList.get(position).getSongThumbnail());
                        bundle.putString("videoId", recentSongList.get(position).getSongId());
                        bundle.putParcelableArrayList("playlist", (ArrayList<? extends Parcelable>) playlist);
                        bundle.putString("from", "channel");
                        Intent intent = new Intent(AllRecntSongsActivity.this, PlayYoutubeVideoActivity.class);
                        intent.putExtra("data", bundle);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    Log.d("yash", "onItemClick: " + e.getMessage());
                }
                // conditions to check song type before going to play in player screen

            }
        });
        recentSongRv.setAdapter(recentPlayedAdapter);

    }


    private void callGetRecentAllSongAPI(int userId) {

        ((ProgressBar) findViewById(R.id.progressbar)).setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(AllRecntSongsActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<ArrayList<RecentSongModel>> callback = dataAPI.getRecentAllSong(token, userId);
        callback.enqueue(new Callback<ArrayList<RecentSongModel>>() {
            @Override
            public void onResponse(Call<ArrayList<RecentSongModel>> call, Response<ArrayList<RecentSongModel>> response) {
                ((ProgressBar) findViewById(R.id.progressbar)).setVisibility(View.GONE);
                if (response != null && response.body() != null && response.body().size() > 0) {
                    recentSongList.addAll(response.body());
                    recentPlayedAdapter.notifyDataSetChanged();
                } else {

                }
            }

            @Override
            public void onFailure(Call<ArrayList<RecentSongModel>> call, Throwable t) {
                ((ProgressBar) findViewById(R.id.progressbar)).setVisibility(View.GONE);

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Top to Bottom animation whenever user  tap on back button
        overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logging.d("==>onStop youtube");
        if (((VibinApplication) getApplication()).isPipEnable) {
            Intent killPlayer = new Intent(this, PlayYoutubeVideoActivity.class);
            killPlayer.putExtra(PlayYoutubeVideoActivity.INTENT_KILL_PLAYER, true);
            startActivity(killPlayer);
        }
    }
}
