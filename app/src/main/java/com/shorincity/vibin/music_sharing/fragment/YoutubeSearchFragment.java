package com.shorincity.vibin.music_sharing.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shorincity.vibin.music_sharing.model.Item;
import com.shorincity.vibin.music_sharing.model.ModelData;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.adapters.YoutubeSearchAdapter;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.youtube_files.PlayYoutubeVideoActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// youtube search fragment
public class YoutubeSearchFragment extends Fragment {

    View view;
    Context mContext;
    EditText editTextsearch;
    Button searchEditText;
    ProgressBar progressBar;
    RecyclerView youtubeSearchRv;

    ArrayList<Item> youtubeSearchList;
    YoutubeSearchAdapter youtubeSearchAdapter;


    EditText edtsearch;
    Button btnsearch;
    ListView listView;
    ArrayList<Item> mangitem;
    public static String idvideo;
    String title;
    String thumbnail;
    TextView noresults;

    TextView searchsomething;



    public YoutubeSearchFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_youtube_search1, container, false);
        mContext = view.getContext();

        inItviews();

        String text = getArguments().getString("search");
        if(!text.equals("")) {

            edtsearch.setText(text);
            listView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            String tukhoa = text;
            //tukhoa =tukhoa.replace(" ","%20");
            Docdulieu(tukhoa);
        }

        //edtsearch.setText("laung lanchi");
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
                Intent intent = new Intent(getActivity(), PlayYoutubeVideoActivity.class);
                idvideo = mangitem.get(position).getId().getVideoId();
                Item currentItem = mangitem.get(position);
                title = currentItem.getSnippet().getTitle();
                thumbnail = currentItem.getSnippet().getThumbnails().getMedium().getUrl();
                intent.putExtra("title",title);
                intent.putExtra("description","");
                intent.putExtra("thumbnail",thumbnail);
                intent.putExtra("videoId",idvideo);
                startActivity(intent);
            }
        });


        setTrendingAdapter();

        return view;
    }

    private void setTrendingAdapter() {

        youtubeSearchList = new ArrayList<>();
        youtubeSearchRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        youtubeSearchRv.setHasFixedSize(true);

        youtubeSearchAdapter = new YoutubeSearchAdapter(getActivity(), youtubeSearchList);
        youtubeSearchAdapter.setCustomItemClickListener(new YoutubeSearchAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(getActivity(), PlayYoutubeVideoActivity.class);
                idvideo = youtubeSearchList.get(position).getId().getVideoId();
                Item currentItem = mangitem.get(position);
                title = currentItem.getSnippet().getTitle();
                thumbnail = currentItem.getSnippet().getThumbnails().getMedium().getUrl();
                intent.putExtra("title",title);
                intent.putExtra("description","");
                intent.putExtra("thumbnail",thumbnail);
                intent.putExtra("videoId",idvideo);
                startActivity(intent);
            }
        });
        youtubeSearchRv.setAdapter(youtubeSearchAdapter);

    }

    private void inItviews() {
        noresults = view.findViewById(R.id.noResultsy);
        progressBar = view.findViewById(R.id.progressbar);
        searchsomething = view.findViewById(R.id.searchidy);
        edtsearch = view.findViewById(R.id.edittextSearch);
        btnsearch = view.findViewById(R.id.buttonSearch);
        listView = view.findViewById(R.id.listview);
        youtubeSearchRv = view.findViewById(R.id.youtube_search_rv);
    }


    public void callgetYoutubeSearchListAPI(String query) {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getYoutubeData();
        // videos?part=contentDetails&chart=mostPopular&regionCode=IN&maxResults=25&key=AIzaSyDn7GZfot4NowEcGPzRYv7h80s7LUT_vcs

        Call<ModelData> callback = dataAPI.getYoutubeSearchList("snippet","mostPopular",query,"25","track%20Cartist","AIzaSyDn7GZfot4NowEcGPzRYv7h80s7LUT_vcs");
        callback.enqueue(new Callback<ModelData>() {
            @Override
            public void onResponse(Call<ModelData> call, Response<ModelData> response) {
                progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    Log.i("YOUTUBE_TRENDING RESULT", response.toString());

                    if(response.body().getItems() != null
                            && response.body().getItems().size() > 0 ) {
                        //youtubeSearchList.addAll(response.body().getItems());
                        //youtubeSearchAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "Result Found: "+response.body().getItems().size(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "Result Not Found: ", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ModelData> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(),"Error: " +t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }


    // parse data from youtube api to list view
    public void Docdulieu(String tukhoa) {
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getYoutubeData();
        Call<ModelData> callback = dataAPI.getResurt("snippet", tukhoa, "50", "track%20Cartist", AppConstants.YOUTUBE_KEY);
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
                    progressBar.setVisibility(View.GONE);
                    listView.setVisibility(View.GONE);
                    youtubeSearchList.clear();
                    youtubeSearchList.addAll(mangitem);
                    youtubeSearchAdapter.notifyDataSetChanged();


                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            youtubeSearchRv.scrollTo(0,0);
                            youtubeSearchRv.smoothScrollToPosition(0);


                            try {
                                InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(edtsearch.getWindowToken(), 0);
                            }catch (Exception e) {
                                
                            }
                        }
                    });

                    /* youtubeSearchAdapter = new YoutubeSearchAdapter(getContext(), android.R.layout.simple_list_item_1, mangitem);
                    progressBar.setVisibility(View.GONE);
                    listView.setAdapter(youtubeSearchAdapter);
                    listView.setVisibility(View.VISIBLE);
                    */

                }
            }
            @Override
            public void onFailure(Call<ModelData> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                noresults.setVisibility(View.VISIBLE);
            }
        });

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
