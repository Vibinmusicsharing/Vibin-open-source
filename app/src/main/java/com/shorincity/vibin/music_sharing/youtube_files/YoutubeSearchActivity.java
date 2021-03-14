package com.shorincity.vibin.music_sharing.youtube_files;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shorincity.vibin.music_sharing.model.ModelData;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.model.Item;
import com.shorincity.vibin.music_sharing.utils.AppConstants;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YoutubeSearchActivity extends AppCompatActivity {
    public static String API_KEY = "AIzaSyDn7GZfot4NowEcGPzRYv7h80s7LUT_vcs";
    EditText edtsearch;
    Button btnsearch;
    ListView listView;
    YoutubeSearchAdapter youtubeSearchAdapter;
    ArrayList<Item> mangitem;
    public static String idvideo;
    String title;
    String thumbnail;
    TextView noresults;
    ProgressBar progressBar;

    TextView searchsomething;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        statusBarColorChange();
        noresults = findViewById(R.id.noResultsy);
        progressBar = findViewById(R.id.progressbar);
        Anhxa();
        searchsomething = findViewById(R.id.searchidy);
        String text = getIntent().getExtras().getString("search");
        if(!text.equals("")) {

            edtsearch.setText(text);
            listView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            String tukhoa = text;
            Docdulieu(tukhoa);
        }

        if(edtsearch.getText().toString().isEmpty()){
            searchsomething.setVisibility(View.VISIBLE);
        }

        edtsearch.setOnEditorActionListener(editorActionListener);
        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                String tukhoa = edtsearch.getText().toString();
                //tukhoa =tukhoa.replace(" ","%20");
                Docdulieu(tukhoa);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(YoutubeSearchActivity.this, PlayYoutubeVideoActivity.class);
                 idvideo = mangitem.get(position).getId().getVideoId();
                 Item currentItem = mangitem.get(position);
                 title = currentItem.getSnippet().getTitle();
                 thumbnail = currentItem.getSnippet().getThumbnails().getMedium().getUrl();
                 intent.putExtra("title",title);
                 intent.putExtra("thumbnail",thumbnail);
                 intent.putExtra("videoId",idvideo);
                 startActivity(intent);
            }
        });

    }

    private void statusBarColorChange(){
        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));
        }
    }

    // parse data from youtube api to list view
    public void Docdulieu(String tukhoa) {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getYoutubeData();
        Call<ModelData> callback = dataAPI.getResurt("snippet", tukhoa, "50", "video", AppConstants.YOUTUBE_KEY);
        callback.enqueue(new Callback<ModelData>() {
            @Override
            public void onResponse(Call<ModelData> call, Response<ModelData> response) {
                ModelData modelData = response.body();
                mangitem = (ArrayList<Item>) response.body().getItems();
                if(mangitem.size() == 0){
                    progressBar.setVisibility(View.GONE);
                    noresults.setVisibility(View.VISIBLE);
                }
                else {
                    noresults.setVisibility(View.GONE);
                    youtubeSearchAdapter = new YoutubeSearchAdapter(YoutubeSearchActivity.this, android.R.layout.simple_list_item_1, mangitem);
                    progressBar.setVisibility(View.GONE);
                    listView.setAdapter(youtubeSearchAdapter);
                    listView.setVisibility(View.VISIBLE);
                    listView.scrollTo(0,0);
                    listView.smoothScrollToPosition(0);
                }
            }
            @Override
            public void onFailure(Call<ModelData> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                noresults.setVisibility(View.VISIBLE);
            }
        });

    }

    private void Anhxa() {
        edtsearch = findViewById(R.id.edittextSearch);
        btnsearch = findViewById(R.id.buttonSearch);
        listView = findViewById(R.id.listview);
    }

    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (actionId){
                case EditorInfo.IME_ACTION_SEARCH:

                    if(edtsearch.getText().toString().isEmpty()){
                        listView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        searchsomething.setVisibility(View.VISIBLE);
                    }else {
                        noresults.setVisibility(View.GONE);
                        searchsomething.setVisibility(View.GONE);
                        listView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        String tukhoa = edtsearch.getText().toString();
                        //tukhoa = tukhoa.replace(" ", "%20");
                        Docdulieu(tukhoa);
                    }
                    break;
            }
            return false;
        }

    };
}
