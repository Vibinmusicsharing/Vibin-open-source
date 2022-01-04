package com.shorincity.vibin.music_sharing.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

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
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.activity.RealTimePlayer;
import com.shorincity.vibin.music_sharing.activity.RealTimePlayerActivity;
import com.shorincity.vibin.music_sharing.adapters.AutoCompleteAdapter;
import com.shorincity.vibin.music_sharing.adapters.PlaylistDetailsViewPagerAdapter;
import com.shorincity.vibin.music_sharing.adapters.ViewCollab;
import com.shorincity.vibin.music_sharing.databinding.BottomsheetPlaylistPassowrdBinding;
import com.shorincity.vibin.music_sharing.databinding.FragmentPlaylistDetailBinding;
import com.shorincity.vibin.music_sharing.databinding.LayoutPlaylistListnerMenuBinding;
import com.shorincity.vibin.music_sharing.databinding.PlaylistDetailsMenuBinding;
import com.shorincity.vibin.music_sharing.model.APIResponse;
import com.shorincity.vibin.music_sharing.model.CreateSessionModel;
import com.shorincity.vibin.music_sharing.model.MyPlaylistModel;
import com.shorincity.vibin.music_sharing.model.PlayListDeleteModel;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.model.PlaylistSongCollabDeleteModel;
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
            int userId = SharedPrefManager.getInstance(context).getSharedPrefInt(AppConstants.INTENT_USER_ID);
            viewModel.setAdmin(myPlaylistModel.getAdmin_id() == userId);
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
                PopupWindow popupWindow;
                if (viewModel.isAdmin()) {
                    PlaylistDetailsMenuBinding popupView = DataBindingUtil.inflate(layoutInflater, R.layout.playlist_details_menu, null, false);

                    popupWindow = new PopupWindow(
                            popupView.getRoot(),
                            CommonUtils.dpToPx(300, context),
                            ViewGroup.LayoutParams.WRAP_CONTENT);

                    popupView.llEditPlaylist.setOnClickListener(v1 -> {
                        popupWindow.dismiss();
                        openEditPlaylistBottomsheet();
                    });
                    popupView.llDeletePlaylist.setOnClickListener(v1 -> {
                        popupWindow.dismiss();
                        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                        View view1 = LayoutInflater.from(context).inflate(R.layout.dialog_delete_playlist, null, false);
                        alertDialog.setView(view1);
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.setCancelable(false);

                        TextView tvTitle = view1.findViewById(R.id.tvTitle);
                        MaterialButton btnCancel = view1.findViewById(R.id.btnCancel);
                        MaterialButton btnDelete = view1.findViewById(R.id.btnDelete);

                        tvTitle.setText(String.format("Are you sure you want to delete \"%s\" playlist?", myPlaylistModel.getName()));
                        btnCancel.setOnClickListener(v2 -> alertDialog.dismiss());
                        btnDelete.setOnClickListener(v2 -> {
                            deletePlayList();
                            alertDialog.dismiss();
                        });
                        alertDialog.show();
                    });
                    popupView.llShare.setOnClickListener(v1 -> {
                        popupWindow.dismiss();
                    });
                    popupView.llPrivate.setOnClickListener(v12 -> {
                        popupWindow.dismiss();
                        if (myPlaylistModel.getPrivate().equalsIgnoreCase("true")) {
                            callUpdateDetails("", "false", null);
                        } else
                            openSetPassword();
                    });

                    popupView.ivSwitch.setSelected(myPlaylistModel.getPrivate().equalsIgnoreCase("true"));
                } else {
                    LayoutPlaylistListnerMenuBinding popupView = DataBindingUtil.inflate(layoutInflater, R.layout.layout_playlist_listner_menu, null, false);
                    popupWindow = new PopupWindow(
                            popupView.getRoot(),
                            CommonUtils.dpToPx(300, context),
                            CommonUtils.dpToPx(110, context));
                    popupView.llLeave.setOnClickListener(v13 -> {
                        popupWindow.dismiss();
                        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                        View view1 = LayoutInflater.from(context).inflate(R.layout.dialog_leave_playlist, null, false);
                        alertDialog.setView(view1);
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.setCancelable(false);

                        TextView tvTitle = view1.findViewById(R.id.tvTitle);
                        MaterialButton btnCancel = view1.findViewById(R.id.btnCancel);
                        MaterialButton btnDelete = view1.findViewById(R.id.btnDelete);
                        AppCompatCheckBox cbDeleteSongs = view1.findViewById(R.id.cbRemoveSongs);

                        tvTitle.setText("Are you sure you want to Leave playlist?");
                        btnCancel.setOnClickListener(v1 -> alertDialog.dismiss());
                        btnDelete.setOnClickListener(v2 -> {
                            alertDialog.dismiss();
                            int userId1 = SharedPrefManager.getInstance(context).getSharedPrefInt(AppConstants.INTENT_USER_ID);
                            callDeleteCollab(String.valueOf(userId1), cbDeleteSongs.isChecked());
                        });
                        alertDialog.show();
                    });
                }
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
            binding.ivLike.setOnClickListener(v -> putPublicPLaylistLike(myPlaylistModel.getId()));
        }
    }

    private void setPlaylistDetails() {
        if (myPlaylistModel != null) {
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
            callUpdateDetails(passwordBinding.etPassword.getText().toString().trim(),
                    passwordBinding.ivSwitch.isSelected() ? "true" : "false", bottomSheet);
        });
        passwordBinding.ivClose.setOnClickListener(v -> bottomSheet.dismiss());
        bottomSheet.show();
    }

    private void callUpdateDetails(String password, String isPrivate, BottomSheetDialog bottomSheet) {
        ProgressDialog showMe = new ProgressDialog(context);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();

        DataAPI dataAPI = RetrofitAPI.getData();
        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(context).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        Call<MyPlaylistModel> callback = dataAPI.callEditPlaylistBasics(token,
                SharedPrefManager.getInstance(context).getSharedPrefString(AppConstants.INTENT_USER_TOKEN),
                "", myPlaylistModel.getId(), "", "", "",
                password,
                isPrivate);
        callback.enqueue(new Callback<MyPlaylistModel>() {
            @Override
            public void onResponse(Call<MyPlaylistModel> call, retrofit2.Response<MyPlaylistModel> response) {
                showMe.dismiss();
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    if (bottomSheet != null)
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

    private void callDeleteCollab(String collabId, boolean isDeleteSongs) {
        ProgressDialog showMe = new ProgressDialog(context);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();

        DataAPI dataAPI = RetrofitAPI.getData();
        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(context).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        Call<PlaylistSongCollabDeleteModel> callback = dataAPI.callDeleteSongsOrCollabApi(token,
                SharedPrefManager.getInstance(context).getSharedPrefString(AppConstants.INTENT_USER_TOKEN),
                myPlaylistModel.getId(),
                isDeleteSongs ? "all" : "", collabId);
        callback.enqueue(new Callback<PlaylistSongCollabDeleteModel>() {
            @Override
            public void onResponse(Call<PlaylistSongCollabDeleteModel> call, retrofit2.Response<PlaylistSongCollabDeleteModel> response) {
                showMe.dismiss();
                if (response.body() != null &&
                        response.body().getStatus().equalsIgnoreCase("success")) {
                    binding.ivBack.callOnClick();
                } else {
                    Toast.makeText(context,
                            (response.body() != null && response.body().getMessage() != null) ? response.body().getMessage() : "Something went wrong!",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PlaylistSongCollabDeleteModel> call, Throwable t) {
                showMe.dismiss();
                Toast.makeText(context,
                        t.getLocalizedMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
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
        String tags = myPlaylistModel.getPlayListTags().replaceAll("#", "");
        List<String> items = Arrays.asList(tags.split("\\s*,\\s*"));
        tagView.addTags(items);

        AppCompatAutoCompleteTextView autoCompleteTextView = bottom_sheet.findViewById(R.id.actTags);
        AutoCompleteAdapter adapter = new AutoCompleteAdapter(context, android.R.layout.simple_dropdown_item_1line, android.R.id.text1, genreList);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String tag = parent.getItemAtPosition(position).toString();
            tagView.addTag(tag);
            autoCompleteTextView.setText("");
        });

        final String[] selectedGifLink = {""};
        selectedGifLink[0] = myPlaylistModel.getGifLink();
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

        String[] gifArraySplit = myPlaylistModel.getGifLink().split("/");
        String mediaId = gifArraySplit[gifArraySplit.length - 1];
        selectedGifIv.setMediaWithId(mediaId, RenditionType.preview, ContextCompat.getDrawable(selectedGifIv.getContext(), R.color.light_gray));
        selectedGifIv.setVisibility(View.VISIBLE);

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
                        tagView.getSelectedTags().size() > 0 ? TextUtils.join("|", tagView.getSelectedTags()) : "All",
                        "", "");
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
        int userId = SharedPrefManager.getInstance(context).getSharedPrefInt(AppConstants.INTENT_USER_ID);

        if (playlist == null || playlist.size() == 0) {
            Toast.makeText(context, "Please add a song in playlist to start Live Sharing", Toast.LENGTH_LONG).show();
            return;
        } else if (viewcollabList == null || viewcollabList.size() == 0) {
            Toast.makeText(context, "Please create a collaborator first!", Toast.LENGTH_LONG).show();
            return;
        } else if (userId != myPlaylistModel.getAdmin_id()) {
            Toast.makeText(context, "You are not an admin of this playlist.", Toast.LENGTH_LONG).show();
            return;
        }
        callCreateSessionApi();
    }

    private void callCreateSessionApi() {
        showProgressDialog();
        DataAPI dataAPI = RetrofitAPI.getData();
        String headerToken = AppConstants.TOKEN + SharedPrefManager.getInstance(context).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        String userToken = SharedPrefManager.getInstance(context).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
        Call<CreateSessionModel> callback = dataAPI.callCreateRTSession(headerToken, userToken, String.valueOf(myPlaylistModel.getId()));
        callback.enqueue(new Callback<CreateSessionModel>() {
            @Override
            public void onResponse(Call<CreateSessionModel> call, retrofit2.Response<CreateSessionModel> response) {
                hideProgressDialog();

                if (response.body() != null) {
                    Logging.d("Add Info Res-->" + new Gson().toJson(response.body()));
                    CreateSessionModel model = response.body();
                    if (model != null && model.getStatus().equalsIgnoreCase("success")) {
                        // Sending Notification to collaborators
                        startActivity(new Intent(context, RealTimePlayerActivity.class).putExtra(AppConstants.INTENT_SESSION_KEY, model.getSessionKey()).putExtra(AppConstants.INTENT_USER_KEY, "")
                                .putExtra(AppConstants.INTENT_PLAYLIST_ID, myPlaylistModel.getId())
                                .putExtra("admin_id", myPlaylistModel.getAdmin_id())
                                .putExtra(AppConstants.INTENT_IS_ADMIN, true));
                    } else
                        Toast.makeText(context,
                                (response.body() != null && response.body().getMessage() != null) ? response.body().getMessage() : "Something went wrong!",
                                Toast.LENGTH_LONG).show();
                } else {
                }
            }

            @Override
            public void onFailure(Call<CreateSessionModel> call, Throwable t) {
                Log.i("Error: ", "ADD NOTIFICATION " + t.getMessage());
                hideProgressDialog();
            }
        });
    }

    private void putPublicPLaylistLike(int playlistId) {
        binding.ivLike.setSelected(!myPlaylistModel.isLikedByUser());

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<MyPlaylistModel> callback = dataAPI.putPlaylistLikeNew(token,
                SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_TOKEN), playlistId);
        callback.enqueue(new Callback<MyPlaylistModel>() {
            @Override
            public void onResponse(Call<MyPlaylistModel> call, retrofit2.Response<MyPlaylistModel> response) {
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
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
                Toast.makeText(context,
                        "Something went wrong!",
                        Toast.LENGTH_LONG).show();
                Logging.d("TEST", "putPublicPLaylistLike onFailure Called");
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
