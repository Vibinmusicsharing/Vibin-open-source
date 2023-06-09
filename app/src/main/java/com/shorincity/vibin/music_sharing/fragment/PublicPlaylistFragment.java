package com.shorincity.vibin.music_sharing.fragment;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.giphy.sdk.core.models.Media;
import com.giphy.sdk.core.models.enums.MediaType;
import com.giphy.sdk.core.models.enums.RatingType;
import com.giphy.sdk.core.models.enums.RenditionType;
import com.giphy.sdk.ui.GPHRequestType;
import com.giphy.sdk.ui.pagination.GPHContent;
import com.giphy.sdk.ui.views.GPHGridCallback;
import com.giphy.sdk.ui.views.GifView;
import com.giphy.sdk.ui.views.GiphyGridView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.UI.youtube;
import com.shorincity.vibin.music_sharing.adapters.AutoCompleteAdapter;
import com.shorincity.vibin.music_sharing.adapters.UserPlaylistAdapter;
import com.shorincity.vibin.music_sharing.model.MyPlaylistModel;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.model.shareplaylist.PlaylistDetailResponse;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.CommonUtils;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.shorincity.vibin.music_sharing.utils.Utility;
import com.shorincity.vibin.music_sharing.widgets.TagView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;

public class PublicPlaylistFragment extends MyBaseFragment {

    private View view;
    private Context context;
    private ProgressBar progressBar;
    private TextView textView;
    private UserPlaylistAdapter myPlaylistAdapter;
    private ArrayList<MyPlaylistModel> myPlaylists;
    private RecyclerView playlistRv;
    private EditText edittext;
    private ArrayList<String> genreList;

    private ArrayList<PlaylistDetailModel> Songplaylist = new ArrayList<>();

    public PublicPlaylistFragment() {
    }

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            callMyPlaylistAPI(SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_TOKEN));
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_youtube_playlist, container, false);
        context = view.getContext();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        genreList = CommonUtils.getGenre();
        Utility.configGiphy(getActivity());
        edittext = view.findViewById(R.id.edittextSearch);
        edittext.setOnEditorActionListener(editorActionListener);
        progressBar = view.findViewById(R.id.progressbar);
        textView = view.findViewById(R.id.textviewplaylistfragment);

        FloatingActionButton floatingActionButton = view.findViewById(R.id.fab);
        FloatingActionButton search = view.findViewById(R.id.search);
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter("DeletePlaylist"));
        // set Playlist Adapter
        myPlaylists = new ArrayList<>();
        playlistRv = view.findViewById(R.id.rv_playlist);
        /*playlistRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false));*/
        playlistRv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        playlistRv.setHasFixedSize(true);
        myPlaylistAdapter = new UserPlaylistAdapter(getActivity(), myPlaylists);
        myPlaylistAdapter.setCustomItemClickListener(new UserPlaylistAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                PlaylistDetailFragmentNew fragment = PlaylistDetailFragmentNew.getInstance(myPlaylists.get(position).getId(),
                        myPlaylists.get(position).getAdmin_id(), myPlaylists.get(position));
                ((youtube) getActivity()).onLoadFragment(fragment);
                    /*Intent intent = new Intent(context, PlaylistDetailActivity.class);
                    int id = myPlaylists.get(position).getId();
                    intent.putExtra("id", id);
                    intent.putExtra("admin_id", myPlaylists.get(position).getAdmin_id());
                    intent.putExtra(AppConstants.INTENT_PLAYLIST, myPlaylists.get(position));
                    startActivity(intent);*/
            }

            @Override
            public void onLikeClick(View view, int position) {
                putPublicPLaylistLike(myPlaylists.get(position).getId(), position);
            }

            @Override
            public void onPlayClicked(View view, int position) {
                //Toast.makeText(getContext(), "Play", Toast.LENGTH_SHORT).show();
                int id = myPlaylists.get(position).getId();
                callPlaylistDetailAPI(String.valueOf(id));


            }
        });
        playlistRv.setAdapter(myPlaylistAdapter);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreatePlaylistDialog();
            }
        });


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchFragment fragment = SearchFragment.getInstance("");
                ((youtube) getActivity()).onLoadFragment(fragment);
                   /* Intent intent = new Intent(context, Search.class);
                    startActivity(intent);*/
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // dialog to add playlist
    private void openCreatePlaylistDialog() {
        /*final AlertDialog.Builder mb = new AlertDialog.Builder(getActivity());

        final View dialog = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog, null, false);
        final EditText playlistName = dialog.findViewById(R.id.dialog_playlistname);
        final SearchView playlistGif = dialog.findViewById(R.id.dialog_playlist_gif);
        final GifView selectedGifIv = dialog.findViewById(R.id.selected_gif_iv);
        final EditText playlistDesc = dialog.findViewById(R.id.dialog_playlist_desc);
        final EditText PlaylistPassword = dialog.findViewById(R.id.dialog_password);
        final GiphyGridView giphyGridView = dialog.findViewById(R.id.gifsGridView);
        final String[] selectedGifLink = {""};
        // setting Giphy GridView
        giphyGridView.setDirection(GiphyGridView.HORIZONTAL);
        giphyGridView.setSpanCount(2);
        giphyGridView.setCellPadding(0);
        giphyGridView.setCallback(new GPHGridCallback() {
            @Override
            public void contentDidUpdate(int i) {
                Log.i("GifURL", "Position " + i);
            }

            @Override
            public void didSelectMedia(@NotNull Media media) {
                Log.i("GifURL", "BitlyGifURL " + media.getBitlyGifUrl());
                Log.i("GifURL", "BitlyURL " + media.getBitlyUrl());
                Log.i("GifURL", "Content " + media.getContentUrl());
                Log.i("GifURL", "EmbededUrl " + media.getEmbedUrl());
                Log.i("GifURL", "SourceUrl " + media.getSourcePostUrl());

                selectedGifLink[0] = media.getEmbedUrl();
                selectedGifIv.setVisibility(View.VISIBLE);
                selectedGifIv.setMedia(media, RenditionType.preview, ContextCompat.getDrawable(getActivity(), R.color.light_gray));
            }
        });
        playlistGif.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                GPHContent gphContent = new GPHContent();
                gphContent.setMediaType(MediaType.gif);
                gphContent.setRating(RatingType.pg13);
                gphContent.setRequestType(GPHRequestType.search);
                gphContent.setSearchQuery(query);
                giphyGridView.setContent(gphContent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //PlaylistPassword.setVisibility(View.GONE);
        PlaylistPassword.setAlpha(0.5f);
        final Switch toggle = dialog.findViewById(R.id.toggle);
        final TextView publicprivate = dialog.findViewById(R.id.privatepublic);
        final Boolean[] checking = new Boolean[1];
        checking[0] = false;
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Boolean b;
                if (isChecked) {
                    publicprivate.setText("Private");
                    PlaylistPassword.setEnabled(true);
                    PlaylistPassword.setAlpha(1.0f);
                    //PlaylistPassword.setVisibility(View.VISIBLE);
                    b = true;
                } else {
                    b = false;
                    PlaylistPassword.setText("");
                    PlaylistPassword.setEnabled(false);
                    PlaylistPassword.setAlpha(0.5f);
                    //PlaylistPassword.setVisibility(View.GONE);
                    publicprivate.setText("Public");
                }
                checking[0] = b;
            }
        });

        mb.setView(dialog)
                .setTitle("Create New Playlist")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String playlistname = playlistName.getText().toString();
                        String password = PlaylistPassword.getText().toString();
                        if (checking[0]) {
                            if (TextUtils.isEmpty(playlistname)) {
                                Toast.makeText(context, "please give playlist some name", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(selectedGifLink[0])) {
                                Toast.makeText(context, "please choose a GIF", Toast.LENGTH_SHORT).show();
                            } else if (!Utility.isWebUrl(selectedGifLink[0])) {
                                Toast.makeText(context, "please choose a valid GIF", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(playlistDesc.getText())) {
                                Toast.makeText(context, "please enter playlist's description", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(password)) {
                                Toast.makeText(context, "please enter playlist a password", Toast.LENGTH_SHORT).show();
                            } else {
                                addTexts(playlistname, selectedGifLink[0], playlistDesc.getText().toString(), password, checking);
                            }
                        } else {
                            if (TextUtils.isEmpty(playlistname)) {
                                Toast.makeText(context, "please give playlist some name", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(selectedGifLink[0])) {
                                Toast.makeText(context, "please choose a GIF", Toast.LENGTH_SHORT).show();
                            } else if (!Utility.isWebUrl(selectedGifLink[0])) {
                                Toast.makeText(context, "please choose a valid GIF", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(playlistDesc.getText())) {
                                Toast.makeText(context, "please enter playlist's description", Toast.LENGTH_SHORT).show();
                            } else {
                                addTexts(playlistname, selectedGifLink[0], playlistDesc.getText().toString(), "", checking);
                            }
                        }
                    }
                });
        mb.setView(dialog);
        final AlertDialog ass = mb.create();
        ass.show();*/
        openCreatePlaylistBottomsheet();
    }

    private final TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (actionId) {
                case EditorInfo.IME_ACTION_SEARCH: {
                    String text = edittext.getText().toString();
                    SearchFragment fragment = SearchFragment.getInstance(text);
                    ((youtube) getActivity()).onLoadFragment(fragment);
                    /*Intent intent = new Intent(context, Search.class);
                    intent.putExtra("search", text);
                    startActivity(intent);*/
                    break;
                }
            }
            return false;
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onStart() {
        super.onStart();

        if (getActivity() instanceof youtube) {
            ((youtube) getActivity()).mSlidingLayout.setSlidingEnable(false);
            ((youtube) getActivity()).mSlidingLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        callMyPlaylistAPI(SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_TOKEN));

        if (getActivity() instanceof youtube) {
            ((youtube) getActivity()).mSlidingLayout.closePane();
        }
    }

    private void callMyPlaylistAPI(String userToken) {
        textView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getData();
        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        Call<ArrayList<MyPlaylistModel>> callback = dataAPI.getMyPlaylist(token, userToken);
        callback.enqueue(new Callback<ArrayList<MyPlaylistModel>>() {
            @Override
            public void onResponse(Call<ArrayList<MyPlaylistModel>> call, retrofit2.Response<ArrayList<MyPlaylistModel>> response) {
                progressBar.setVisibility(View.GONE);
                myPlaylists.clear();
                if (response.body() != null && response.body().size() > 0) {
                    playlistRv.setVisibility(View.VISIBLE);
                    myPlaylists.addAll(response.body());
                    myPlaylistAdapter.notifyDataSetChanged();
                    Logging.d("TEST", "callMyPlaylistAPI Called");
                } else {
                    playlistRv.setVisibility(View.GONE);
                    Logging.d("TEST", "callMyPlaylistAPI Else Called");
                }

                if (myPlaylists.size() == 0) {
                    textView.setVisibility(View.VISIBLE);
                    Logging.d("TEST", "callMyPlaylistAPI Size 0");
                } else {
                    textView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<MyPlaylistModel>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Logging.d("TEST", "callMyPlaylistAPI onFailure Called");
            }
        });
    }

    private void putPublicPLaylistLike(int playlistId, int position) {
        MyPlaylistModel playlistModel = myPlaylists.get(position);
        playlistModel.setLikedByUser(!playlistModel.isLikedByUser());
        myPlaylists.set(position, playlistModel);
        myPlaylistAdapter.notifyItemChanged(position);

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<MyPlaylistModel> callback = dataAPI.putPlaylistLikeNew(token,
                SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_TOKEN), playlistId);
        callback.enqueue(new Callback<MyPlaylistModel>() {
            @Override
            public void onResponse(Call<MyPlaylistModel> call, retrofit2.Response<MyPlaylistModel> response) {
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    myPlaylists.set(position, response.body());
                    myPlaylistAdapter.notifyItemChanged(position);
                } else {
                    Toast.makeText(context,
                            (response.body() != null && response.body().getMessage() != null) ? response.body().getMessage() : "Something went wrong!",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<MyPlaylistModel> call, Throwable t) {
                Toast.makeText(context,
                        "Something went wrong!",
                        Toast.LENGTH_LONG).show();
                Logging.d("TEST", "putPublicPLaylistLike onFailure Called");
            }
        });
    }

    private void callPlaylistDetailAPI(String playlistId) {
        ProgressDialog showMe = new ProgressDialog(context);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(context).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        Call<PlaylistDetailResponse> callback = dataAPI.getPublicPlaylistDetail(token,
                SharedPrefManager.getInstance(context).getSharedPrefString(AppConstants.INTENT_USER_TOKEN),
                playlistId, AppConstants.SOURCE_TYPE_IN_APP);
        callback.enqueue(new Callback<PlaylistDetailResponse>() {
            @Override
            public void onResponse(Call<PlaylistDetailResponse> call, retrofit2.Response<PlaylistDetailResponse> response) {
                showMe.dismiss();
                Songplaylist.clear();
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {

                    Songplaylist.addAll(response.body().getTracks());
                    Collections.reverse(Songplaylist);
                    int pos = 0;

                    try {
                        if (Songplaylist.get(pos).getType().equalsIgnoreCase(AppConstants.YOUTUBE)) {
                            Bundle bundle = new Bundle();
                            bundle.putInt("position", pos);
                            bundle.putString("title", Songplaylist.get(pos).getName());
                            bundle.putString("description", "");
                            bundle.putString("thumbnail", Songplaylist.get(pos).getImage());
                            bundle.putString("videoId", Songplaylist.get(pos).getTrackId());
                            bundle.putString("from", "channel");
                            bundle.putParcelableArrayList("playlist", Songplaylist);
                            onMusicPlay(bundle);
                            /*Intent intent = new Intent(context, PlayYoutubeVideoActivity.class);
                            intent.putExtra("data", bundle);
                            startActivity(intent);*/
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<PlaylistDetailResponse> call, Throwable t) {
                showMe.dismiss();
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void openCreatePlaylistBottomsheet() {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(context);
        View bottom_sheet = LayoutInflater.from(context).inflate(R.layout.bottomsheet_create_playlist, null);
        bottomSheet.setContentView(bottom_sheet);

        EditText playlistName = bottom_sheet.findViewById(R.id.dialog_playlistname);
        SearchView playlistGif = bottom_sheet.findViewById(R.id.dialog_playlist_gif);
        GifView selectedGifIv = bottom_sheet.findViewById(R.id.selected_gif_iv);
        EditText playlistDesc = bottom_sheet.findViewById(R.id.dialog_playlist_desc);
        GiphyGridView giphyGridView = bottom_sheet.findViewById(R.id.gifsGridView);
        TagView tagView = bottom_sheet.findViewById(R.id.tag_view_test);
        AppCompatImageView ivClose = bottom_sheet.findViewById(R.id.ivClose);
        Button btnCreate = bottom_sheet.findViewById(R.id.btnCreate);

        AppCompatAutoCompleteTextView autoCompleteTextView = bottom_sheet.findViewById(R.id.actTags);
        AutoCompleteAdapter adapter = new AutoCompleteAdapter(context, android.R.layout.simple_dropdown_item_1line, android.R.id.text1, genreList);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String tag = parent.getItemAtPosition(position).toString();
            tagView.addTag(tag);
            autoCompleteTextView.setText("");
        });

        final String[] selectedGifLink = {""};
        // setting Giphy GridView
        giphyGridView.setDirection(GiphyGridView.HORIZONTAL);
        giphyGridView.setSpanCount(2);
        giphyGridView.setCellPadding(0);
        giphyGridView.setContent(GPHContent.Companion.getTrendingGifs());
        giphyGridView.setCallback(new GPHGridCallback() {
            @Override
            public void contentDidUpdate(int i) {
                Log.i("GifURL", "Position " + i);
            }

            @Override
            public void didSelectMedia(@NotNull Media media) {
                Log.i("GifURL", "BitlyGifURL " + media.getBitlyGifUrl());
                Log.i("GifURL", "BitlyURL " + media.getBitlyUrl());
                Log.i("GifURL", "Content " + media.getContentUrl());
                Log.i("GifURL", "EmbededUrl " + media.getEmbedUrl());
                Log.i("GifURL", "SourceUrl " + media.getSourcePostUrl());

                selectedGifLink[0] = media.getEmbedUrl();
                selectedGifIv.setVisibility(View.VISIBLE);
                selectedGifIv.setMedia(media, RenditionType.preview, ContextCompat.getDrawable(getActivity(), R.color.light_gray));
            }
        });
        playlistGif.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                GPHContent gphContent = new GPHContent();
                gphContent.setMediaType(MediaType.gif);
                gphContent.setRating(RatingType.pg13);
                gphContent.setRequestType(GPHRequestType.search);
                gphContent.setSearchQuery(query);
                giphyGridView.setContent(gphContent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        ivClose.setOnClickListener(v -> bottomSheet.dismiss());

        btnCreate.setOnClickListener(v -> {
            if (TextUtils.isEmpty(playlistName.getText().toString().trim())) {
                Toast.makeText(context, "please give playlist some name", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(selectedGifLink[0])) {
                Toast.makeText(context, "please choose a GIF", Toast.LENGTH_SHORT).show();
            } else if (!Utility.isWebUrl(selectedGifLink[0])) {
                Toast.makeText(context, "please choose a valid GIF", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(playlistDesc.getText().toString().trim())) {
                Toast.makeText(context, "please enter playlist's description", Toast.LENGTH_SHORT).show();
            } else {

                ProgressDialog showMe = new ProgressDialog(context);
                showMe.setMessage("Please wait");
                showMe.setCancelable(true);
                showMe.setCanceledOnTouchOutside(false);
                showMe.show();

                DataAPI dataAPI = RetrofitAPI.getData();
                String token = AppConstants.TOKEN + SharedPrefManager.getInstance(context).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
                Call<MyPlaylistModel> callback = dataAPI.callCreatePlayList(token,
                        SharedPrefManager.getInstance(context).getSharedPrefString(AppConstants.INTENT_USER_TOKEN),
                        playlistName.getText().toString().trim(), playlistDesc.getText().toString().trim(),
                        selectedGifLink[0], "false", "",
                        tagView.getSelectedTags().size() > 0 ? TextUtils.join("|", tagView.getSelectedTags()) : "All");
                callback.enqueue(new Callback<MyPlaylistModel>() {
                    @Override
                    public void onResponse(Call<MyPlaylistModel> call, retrofit2.Response<MyPlaylistModel> response) {
                        showMe.dismiss();
                        if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                            bottomSheet.dismiss();
                            myPlaylists.add(response.body());
                            myPlaylistAdapter.notifyItemInserted(myPlaylists.size());
                        } else {
                            Toast.makeText(context,
                                    (response.body() != null && response.body().getMessage() != null) ? response.body().getMessage() : "Something went wrong!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MyPlaylistModel> call, Throwable t) {
                        showMe.dismiss();
                        Toast.makeText(context,
                                t.getLocalizedMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        bottomSheet.show();

    }

}
