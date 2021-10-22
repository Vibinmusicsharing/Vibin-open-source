package com.shorincity.vibin.music_sharing.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.giphy.sdk.core.models.Media;
import com.giphy.sdk.core.models.enums.MediaType;
import com.giphy.sdk.core.models.enums.RatingType;
import com.giphy.sdk.core.models.enums.RenditionType;
import com.giphy.sdk.ui.GPHRequestType;
import com.giphy.sdk.ui.pagination.GPHContent;
import com.giphy.sdk.ui.views.GPHGridCallback;
import com.giphy.sdk.ui.views.GifView;
import com.giphy.sdk.ui.views.GiphyGridView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.activity.RealTimePlayer;
import com.shorincity.vibin.music_sharing.adapters.AutoCompleteAdapter;
import com.shorincity.vibin.music_sharing.adapters.PlaylistDetailsViewPagerAdapter;
import com.shorincity.vibin.music_sharing.adapters.ViewCollab;
import com.shorincity.vibin.music_sharing.databinding.BottomsheetPlaylistPassowrdBinding;
import com.shorincity.vibin.music_sharing.databinding.FragmentPlaylistDetailBinding;
import com.shorincity.vibin.music_sharing.databinding.PlaylistDetailsMenuBinding;
import com.shorincity.vibin.music_sharing.model.APIResponse;
import com.shorincity.vibin.music_sharing.model.MyPlaylistModel;
import com.shorincity.vibin.music_sharing.model.PlayListDeleteModel;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.model.firebase.RealTimeModel;
import com.shorincity.vibin.music_sharing.model.firebase.RealTimeSession;
import com.shorincity.vibin.music_sharing.model.firebase.RealTimeUser;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.CommonUtils;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.shorincity.vibin.music_sharing.utils.Utility;
import com.shorincity.vibin.music_sharing.viewmodel.LoginViewModel;
import com.shorincity.vibin.music_sharing.viewmodel.PlaylistDetailsViewModel;
import com.shorincity.vibin.music_sharing.widgets.TagView;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class PlaylistDetailFragmentNew extends MyBaseFragment {
    private static final String TAG = PlaylistDetailFragmentNew.class.getName();
    private static final String BUNDLE_ID = "id";
    private static final String BUNDLE_ADMIN_ID = "admin_id";
    private FragmentPlaylistDetailBinding binding;
    private MyPlaylistModel myPlaylistModel;
    private Context context;
    private ArrayList<String> genreList;
    private PlaylistDetailsViewModel viewModel;
    private RealTimeModel realTimeModel;
    private DatabaseReference myRef;
    private ProgressDialog mProgressDialog;

    public static PlaylistDetailFragmentNew getInstance(int id, int admin_id, MyPlaylistModel myPlaylistModel) {
        PlaylistDetailFragmentNew fragment = new PlaylistDetailFragmentNew();
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_ID, id);
        bundle.putInt(BUNDLE_ADMIN_ID, admin_id);
        bundle.putSerializable(AppConstants.INTENT_PLAYLIST, myPlaylistModel);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playlist_detail, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = binding.getRoot().getContext();
        myPlaylistModel = (MyPlaylistModel) getArguments().getSerializable(AppConstants.INTENT_PLAYLIST);
        genreList = CommonUtils.getGenre();
        viewModel = new PlaylistDetailsViewModel();
        initControls();
    }

    private void initControls() {
        if (myPlaylistModel != null) {
            setPlaylistDetails();
            ArrayList<String> titles = new ArrayList<>();
            titles.add("List");
            titles.add("Collaborators");
            PlaylistDetailsViewPagerAdapter adapter = new PlaylistDetailsViewPagerAdapter(getChildFragmentManager(),
                    titles, String.valueOf(myPlaylistModel.getId()), viewModel);
            binding.viewPager.setAdapter(adapter);
            binding.tabs.setupWithViewPager(binding.viewPager);

            binding.ivMenu.setOnClickListener(v -> {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                PlaylistDetailsMenuBinding popupView = DataBindingUtil.inflate(layoutInflater, R.layout.playlist_details_menu, null, false);

                PopupWindow popupWindow = new PopupWindow(
                        popupView.getRoot(),
                        CommonUtils.dpToPx(300, context),
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                popupView.llEditPlaylist.setOnClickListener(v1 -> {
                    popupWindow.dismiss();
                    openEditPlaylistBottomsheet();
                });
                popupView.llDeletePlaylist.setOnClickListener(v1 -> {
                    popupWindow.dismiss();
                    deletePlayList();
                });
                popupView.llShare.setOnClickListener(v1 -> {
                    popupWindow.dismiss();
                });
                popupView.llPrivate.setOnClickListener(v12 -> {
                    popupWindow.dismiss();
                    openSetPassword();
                });

                popupView.ivSwitch.setSelected(myPlaylistModel.getPrivate().equalsIgnoreCase("true"));
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                popupWindow.setOutsideTouchable(true);
                popupWindow.showAsDropDown(v);
            });
            binding.llPlay.setOnClickListener(v -> {
                int pos = 0;

                ArrayList<PlaylistDetailModel> playlist = viewModel.getPlaylist();
                try {
                    if (playlist.size() > 0 && playlist.get(pos).getType().equalsIgnoreCase(AppConstants.YOUTUBE)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("position", pos);
                        bundle.putString("title", playlist.get(pos).getName());
                        bundle.putString("description", "");
                        bundle.putString("thumbnail", playlist.get(pos).getImage());
                        bundle.putString("videoId", playlist.get(pos).getTrackId());
                        bundle.putString("from", "channel");
                        bundle.putParcelableArrayList("playlist", playlist);
                        onMusicPlay(bundle);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            binding.llLiveShare.setOnClickListener(v -> {
                processLiveSharing();
            });
            binding.ivBack.setOnClickListener(v -> {
                getActivity().onBackPressed();
            });

            // Initializing REAL TIME Firebase DB variable.............

            FirebaseDatabase database = FirebaseDatabase.getInstance();

            myRef = database.getReference(AppConstants.SESSION);

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    try {
                        dataSnapshot.child("sessions");
                        HashMap<String, RealTimeSession> sessionHashMap = new HashMap<>();
                        HashMap<String, RealTimeUser> userHashMap = new HashMap<>();

                        for (DataSnapshot ds : dataSnapshot.child("sessions").getChildren()) {
                            RealTimeSession realTimeSession = ds.getValue(RealTimeSession.class);
                            sessionHashMap.put(ds.getKey(), realTimeSession);
                        }

                        for (DataSnapshot ds : dataSnapshot.child("users").getChildren()) {
                            RealTimeUser realTimeUser = ds.getValue(RealTimeUser.class);
                            userHashMap.put(ds.getKey(), realTimeUser);
                        }

                        realTimeModel = new RealTimeModel();
                        realTimeModel.setSessions(sessionHashMap);
                        realTimeModel.setUsers(userHashMap);

                        Log.i(TAG, realTimeModel.toString());

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        }
    }

    private void setPlaylistDetails() {
        binding.tvName.setText(myPlaylistModel.getName());
        binding.tvAdminName.setText(myPlaylistModel.getAdminName());
        binding.tvDesc.setText(myPlaylistModel.getDescription());
        binding.tvTags.setText(myPlaylistModel.getPlayListTags());
        binding.tvSongTime.setText(String.format(Locale.US, "%d songs, %d hour %d min", myPlaylistModel.getSongs(),
                myPlaylistModel.getPlaylistDurationHours(),
                myPlaylistModel.getPlaylistDurationMinutes()));

        String[] gifArraySplit = myPlaylistModel.getGifLink().split("/");
        String mediaId = gifArraySplit[gifArraySplit.length - 1];

        binding.gifIv.setMediaWithId(mediaId, RenditionType.preview, ContextCompat.getDrawable(binding.gifIv.getContext(), R.color.light_gray));
        binding.ivLike.setSelected(myPlaylistModel.isLikedByUser());

    }

    private void openSetPassword() {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(context);
        BottomsheetPlaylistPassowrdBinding passwordBinding =
                DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottomsheet_playlist_passowrd, null, false);
        bottomSheet.setContentView(passwordBinding.getRoot());
        passwordBinding.ivSwitch.setSelected(myPlaylistModel.getPrivate().equalsIgnoreCase("true"));

        passwordBinding.etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                passwordBinding.btnSave.setEnabled(passwordBinding.ivSwitch.isSelected() && !TextUtils.isEmpty(s));
            }
        });
        passwordBinding.llPrivate.setOnClickListener(v -> {
            passwordBinding.ivSwitch.setSelected(!passwordBinding.ivSwitch.isSelected());
        });

        passwordBinding.btnSave.setOnClickListener(v -> {
            if (TextUtils.isEmpty(passwordBinding.etPassword.getText())) {
                Toast.makeText(context, "please enter playlist a password", Toast.LENGTH_SHORT).show();
                return;
            }
            ProgressDialog showMe = new ProgressDialog(context);
            showMe.setMessage("Please wait");
            showMe.setCancelable(true);
            showMe.setCanceledOnTouchOutside(false);
            showMe.show();

            DataAPI dataAPI = RetrofitAPI.getData();
            String token = AppConstants.TOKEN + SharedPrefManager.getInstance(context).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
            Call<MyPlaylistModel> callback = dataAPI.callSetPassword(token,
                    SharedPrefManager.getInstance(context).getSharedPrefString(AppConstants.INTENT_USER_TOKEN),
                    passwordBinding.etPassword.getText().toString().trim(),
                    passwordBinding.ivSwitch.isSelected() ? "true" : "false");
            callback.enqueue(new Callback<MyPlaylistModel>() {
                @Override
                public void onResponse(Call<MyPlaylistModel> call, retrofit2.Response<MyPlaylistModel> response) {
                    showMe.dismiss();
                    if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                        bottomSheet.dismiss();
                        myPlaylistModel = response.body();
                        setPlaylistDetails();
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

        });
        passwordBinding.ivClose.setOnClickListener(v -> bottomSheet.dismiss());
        bottomSheet.show();
    }

    private void openEditPlaylistBottomsheet() {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(context);
        View bottom_sheet = LayoutInflater.from(context).inflate(R.layout.bottomsheet_create_playlist, null);
        bottomSheet.setContentView(bottom_sheet);

        AppCompatTextView tvTitle = bottom_sheet.findViewById(R.id.tvTitle);
        EditText playlistName = bottom_sheet.findViewById(R.id.dialog_playlistname);
        SearchView playlistGif = bottom_sheet.findViewById(R.id.dialog_playlist_gif);
        GifView selectedGifIv = bottom_sheet.findViewById(R.id.selected_gif_iv);
        EditText playlistDesc = bottom_sheet.findViewById(R.id.dialog_playlist_desc);
        GiphyGridView giphyGridView = bottom_sheet.findViewById(R.id.gifsGridView);
        TagView tagView = bottom_sheet.findViewById(R.id.tag_view_test);
        AppCompatImageView ivClose = bottom_sheet.findViewById(R.id.ivClose);
        Button btnCreate = bottom_sheet.findViewById(R.id.btnCreate);

        tvTitle.setText("Edit Playlist");
        btnCreate.setText("Save");
        playlistName.setText(myPlaylistModel.getName());
        playlistDesc.setText(myPlaylistModel.getDescription());
        List<String> items = Arrays.asList(myPlaylistModel.getPlayListTags().split("\\s*,\\s*"));
        tagView.addTags(items);

        AppCompatAutoCompleteTextView autoCompleteTextView = bottom_sheet.findViewById(R.id.actTags);
        AutoCompleteAdapter adapter = new AutoCompleteAdapter(context, android.R.layout.simple_dropdown_item_1line, android.R.id.text1, genreList);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String tag = "# " + parent.getItemAtPosition(position).toString();
            tagView.addTag(tag);
            autoCompleteTextView.setText("");
        });

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
                Call<MyPlaylistModel> callback = dataAPI.callEditPlaylistBasics(token,
                        SharedPrefManager.getInstance(context).getSharedPrefString(AppConstants.INTENT_USER_TOKEN),
                        playlistName.getText().toString().trim(), myPlaylistModel.getId(),
                        playlistDesc.getText().toString().trim(), selectedGifLink[0],
                        TextUtils.join("|", tagView.getSelectedTags()), myPlaylistModel.getPlayListTags());
                callback.enqueue(new Callback<MyPlaylistModel>() {
                    @Override
                    public void onResponse(Call<MyPlaylistModel> call, retrofit2.Response<MyPlaylistModel> response) {
                        showMe.dismiss();
                        if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                            bottomSheet.dismiss();
                            myPlaylistModel = response.body();
                            setPlaylistDetails();
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

    private void deletePlayList() {
        ProgressDialog showMe = new ProgressDialog(context);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();

        DataAPI dataAPI = RetrofitAPI.getData();

        String userToken = SharedPrefManager.getInstance(context).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
        String userTokenapi = AppConstants.TOKEN + SharedPrefManager.getInstance(context).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        Log.d("yash", "deletePlayList: " + userTokenapi);
        Call<PlayListDeleteModel> callback = dataAPI.getDeletePlaylist(userTokenapi, userToken, myPlaylistModel.getId());
        callback.enqueue(new Callback<PlayListDeleteModel>() {
            @Override
            public void onResponse(Call<PlayListDeleteModel> call, retrofit2.Response<PlayListDeleteModel> response) {
                try {
                    showMe.dismiss();
                    if (response.body().getStatus() != null) {
                        if (response.body().getStatus().equalsIgnoreCase("success")) {
                            Intent intent = new Intent();
                            intent.setAction("DeletePlaylist");
                            context.sendBroadcast(intent);
                            Toast.makeText(context, response.body().getMessage(), +2000).show();
                            getActivity().onBackPressed();
                        } else {
                            Toast.makeText(context, response.body().getMessage(), +2000).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<PlayListDeleteModel> call, Throwable t) {
                showMe.dismiss();
                Toast.makeText(context,
                        t.getLocalizedMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void processLiveSharing() {
        ArrayList<PlaylistDetailModel> playlist = viewModel.getPlaylist();
        ArrayList<ViewCollab> viewcollabList = viewModel.getViewcollabList();

        if (playlist == null || playlist.size() == 0) {
            Toast.makeText(context, "Please create a playlist first!", Toast.LENGTH_LONG).show();
            return;
        } else if (viewcollabList == null || viewcollabList.size() == 0) {
            Toast.makeText(context, "Please create a collaborator first!", Toast.LENGTH_LONG).show();
            return;
        }

        RealTimeSession realTimeSession = new RealTimeSession();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(context).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        int userId = SharedPrefManager.getInstance(context).getSharedPrefInt(AppConstants.INTENT_USER_ID);

        long sesionTimeStamp = System.currentTimeMillis();
        String sessionKey = AppConstants.SESSION_CHILD + userId + "_" + sesionTimeStamp;

        NumberFormat formatter = new DecimalFormat("00");
        String duration = "";

        duration = formatter.format(myPlaylistModel.getPlaylistDurationHours()) + ":" + formatter.format(myPlaylistModel.getPlaylistDurationMinutes()) + ":" + formatter.format(myPlaylistModel.getPlaylistDurationSeconds());

        realTimeSession.setSession_token(token);
        realTimeSession.setAdminId(userId);
        realTimeSession.setPlaylist_id(myPlaylistModel.getId());
        realTimeSession.setPlaylist_time(duration);
        realTimeSession.setInvited(viewcollabList.size());
        realTimeSession.setJoined(0);
        realTimeSession.setStatus(AppConstants.WAIT);
        realTimeSession.setStart_in(0);
        realTimeSession.setElapsed_time(0);
        realTimeSession.setRead_elapsed(false);
        realTimeSession.setSongID(playlist.get(0).getTrackId());
        realTimeSession.setSongPosiionInList(0);
        realTimeSession.setSongType(playlist.get(0).getType());
        realTimeSession.setSong_changed(false);


        if (realTimeModel.getSessions() != null)
            realTimeModel.getSessions().put(sessionKey, realTimeSession);

        String userIds = "", userKeys = "";

        for (int i = 0; i < viewcollabList.size(); i++) {
            ViewCollab collab = viewcollabList.get(i);
            if (collab != null) {
                String userKey = AppConstants.USER_CHILD + userId + "_" + collab.getId();//+"_"+sesionTimeStamp;

                userIds = TextUtils.isEmpty(userIds) ? "" + collab.getId() : userIds + ":" + collab.getId();
                userKeys = TextUtils.isEmpty(userKeys) ? userKey : userKeys + ":" + userKey;


                RealTimeUser realTimeUser = new RealTimeUser(sessionKey, token, collab.getId(), AppConstants.WAIT);
                realTimeModel.getUsers().put(userKey, realTimeUser);
            }
        }

        callAddRealTimeInfo(sessionKey, token, userIds, userKeys, myRef, realTimeModel);
    }

    // Creating Session
    private void callAddRealTimeInfo(String sessionKey, String sessionToken, String userIds, String userKeys, DatabaseReference myRef, RealTimeModel realTimeModel) {
        showProgressDialog();

        DataAPI dataAPI = RetrofitAPI.getData();
        String headerToken = AppConstants.TOKEN + SharedPrefManager.getInstance(context).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        int userId = SharedPrefManager.getInstance(context).getSharedPrefInt(AppConstants.INTENT_USER_ID);

        Call<APIResponse> callback = dataAPI.addRealTimeInfo(headerToken, myPlaylistModel.getId(), userId, sessionKey, sessionToken, userIds, userKeys);
        callback.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, retrofit2.Response<APIResponse> response) {
                hideProgressDialog();

                if (response.body() != null) {
                    Logging.d("Add Info Res-->" + new Gson().toJson(response.body()));

                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        // Sending Notification to collaborators
                        sendRealTimetNotification(myPlaylistModel.getId(), AppConstants.REAL_TIME_INVITE, myRef, realTimeModel, sessionKey);
                    } else if (response.body().getStatus().equalsIgnoreCase("already_exist") &&
                            !TextUtils.isEmpty(response.body().getMessage()) &&
                            !TextUtils.isEmpty(response.body().getSessionKey())) {
                        callToDeleteSession(response.body().getSessionKey());
                        // showErrorDialog(response.body().getMessage(), sessionKey);
                    } else
                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
                } else {
                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                Log.i("Error: ", "ADD NOTIFICATION " + t.getMessage());
                hideProgressDialog();
            }
        });
    }

    private void sendRealTimetNotification(int playlistId, String notifyType, DatabaseReference myRef, RealTimeModel realTimeModel, String sessionKey) {

        showProgressDialog();
        DataAPI dataAPI = RetrofitAPI.getData();
        String headerToken = AppConstants.TOKEN + SharedPrefManager.getInstance(context).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        int userId = SharedPrefManager.getInstance(context).getSharedPrefInt(AppConstants.INTENT_USER_ID);

        Call<APIResponse> callback = dataAPI.sendNotificationRealTimeUpdate(headerToken, userId, playlistId, notifyType);
        callback.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, retrofit2.Response<APIResponse> response) {
                hideProgressDialog();
                if (response != null && response.body() != null) {

                    if (response.body().getStatus().equalsIgnoreCase("success")) {

                        myRef.setValue(realTimeModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Success", Toast.LENGTH_LONG).show();
                                //startActivity(new Intent(context, RealTimeYoutubePlayler.class)
                                startActivity(new Intent(context, RealTimePlayer.class).putExtra(AppConstants.INTENT_SESSION_KEY, sessionKey).putExtra(AppConstants.INTENT_USER_KEY, "")
                                        .putExtra(AppConstants.INTENT_PLAYLIST_ID, myPlaylistModel.getId())
                                        .putExtra("admin_id", myPlaylistModel.getAdmin_id()));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        });

                    } else {
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                Log.i("Error: ", "ADD NOTIFICATION " + t.getMessage());
                hideProgressDialog();
            }
        });
    }

    // API call to delete session
    private void callToDeleteSession(String sessionKey) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(context).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<APIResponse> callback = dataAPI.deleteRealTimeSession(token, sessionKey);
        callback.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, retrofit2.Response<APIResponse> response) {
                if (response != null && response.body() != null) {
                    Logging.d("DeleteSession Res-->" + new Gson().toJson(response.body()));
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        sendRealTimetNotification(myPlaylistModel.getId(), AppConstants.REAL_TIME_INVITE, myRef, realTimeModel, sessionKey);
//                        try {
//                            realTimeModel.getSessions().remove(sessionKey);
//
//                            if (viewcollabList != null && viewcollabList.size() > 0) {
//
//                                int i = 0;
//
//                                while (i < viewcollabList.size()) {
//                                    String userKey = AppConstants.USER_CHILD + SharedPrefManager.getInstance(context).getSharedPrefInt(AppConstants.INTENT_USER_ID) + "_" + viewcollabList.get(i).getId();
//                                    if (realTimeModel.getUsers().get(userKey).getSession_id().equalsIgnoreCase(sessionKey)) {
//                                        realTimeModel.getUsers().remove(userKey);
//                                    }
//                                    i++;
//                                }
//                            }
//                            myRef.setValue(realTimeModel);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
                        //processLiveSharing();

                        // Toast.makeText(context, "Hurray! You con go with RealTime Player", Toast.LENGTH_LONG).show();
                    } else if (response.body().getStatus().equalsIgnoreCase("failed") && !TextUtils.isEmpty(response.body().getMessage()))
                        Log.i(TAG, "Session Ending Failed");
                    else
                        Log.i(TAG, "Session Ending Error");
                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }

        if (mProgressDialog != null && !mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
