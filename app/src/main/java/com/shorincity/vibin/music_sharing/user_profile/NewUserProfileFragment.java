package com.shorincity.vibin.music_sharing.user_profile;

import static com.shorincity.vibin.music_sharing.utils.AppConstants.FAILED_RESPONSE;
import static com.shorincity.vibin.music_sharing.utils.AppConstants.PIN_PLAYLIST;
import static com.shorincity.vibin.music_sharing.utils.AppConstants.SUCCESS_RESPONSE;
import static com.shorincity.vibin.music_sharing.utils.AppConstants.UNPIN_PLAYLIST;
import static com.shorincity.vibin.music_sharing.utils.AppConstants.USER_ID;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.giphy.sdk.core.models.Media;
import com.giphy.sdk.core.models.enums.MediaType;
import com.giphy.sdk.core.models.enums.RatingType;
import com.giphy.sdk.core.models.enums.RenditionType;
import com.giphy.sdk.ui.GPHRequestType;
import com.giphy.sdk.ui.pagination.GPHContent;
import com.giphy.sdk.ui.views.GPHGridCallback;
import com.giphy.sdk.ui.views.GifView;
import com.giphy.sdk.ui.views.GiphyGridView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.ovenbits.quickactionview.Action;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.base.prefs.SharedPrefManager;
import com.shorincity.vibin.music_sharing.UI.youtube;
import com.shorincity.vibin.music_sharing.activity.AllRecntSongsActivity;
import com.shorincity.vibin.music_sharing.adapters.AddToPlaylistAdapter;
import com.shorincity.vibin.music_sharing.adapters.AutoCompleteAdapter;
import com.shorincity.vibin.music_sharing.adapters.RecentPlayedSongsAdapter;
import com.shorincity.vibin.music_sharing.adapters.UserProfilePlaylistAdapter;
import com.shorincity.vibin.music_sharing.databinding.FragmentUserProfileNewBinding;
import com.shorincity.vibin.music_sharing.fragment.MyBaseFragment;
import com.shorincity.vibin.music_sharing.fragment.PlaylistDetailFragmentNew;
import com.shorincity.vibin.music_sharing.model.APIResponse;
import com.shorincity.vibin.music_sharing.model.MyPlaylistModel;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.model.RecentSongModel;
import com.shorincity.vibin.music_sharing.model.UpdateLikeStatusModel;
import com.shorincity.vibin.music_sharing.model.profile.CustomerBasicDetails;
import com.shorincity.vibin.music_sharing.model.profile.UserProfileResponse;
import com.shorincity.vibin.music_sharing.model.shareplaylist.SharePlaylistResponse;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.user_profile.ui.UserPlaylistsActivity;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.CommonUtils;
import com.shorincity.vibin.music_sharing.utils.CustomSlidePanLayout;
import com.shorincity.vibin.music_sharing.utils.GlideApp;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.shorincity.vibin.music_sharing.utils.Utility;
import com.shorincity.vibin.music_sharing.widgets.TagView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class NewUserProfileFragment extends Fragment implements UserProfilePlaylistAdapter.MenuItemCallback {

    private FragmentUserProfileNewBinding binding;
    private ArrayList<MyPlaylistModel> playlists;
    private ArrayList<RecentSongModel> recentSongsList;
    private CustomSlidePanLayout slidePanLayout;
    private int customerId = -1, playlistId;
    private String userName, fullName;
    private boolean isOtherProfile = false;
    private ArrayList<MyPlaylistModel> collabPlaylist;
    private AddToPlaylistAdapter addToPlaylistAdapter;
    private AlertDialog ass;
    private ArrayList<String> genreList;

    private static final String BUNDLE_CUSTOMER_ID = "customer_id";
    private static final String BUNDLE_PLAYLIST_ID = "playlist_id";
    private static final String BUNDLE_USERNAME = "user_name";
    private static final String BUNDLE_FULLNAME = "full_name";

    @Inject
    UserProfileViewModel viewModel;


    public static NewUserProfileFragment getInstance() {
        return new NewUserProfileFragment();
    }

    public static NewUserProfileFragment getInstance(int customerId, int playlistId, String userName, String fullName) {
        NewUserProfileFragment fragment = new NewUserProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_CUSTOMER_ID, customerId);
        bundle.putInt(BUNDLE_PLAYLIST_ID, playlistId);
        bundle.putString(BUNDLE_USERNAME, userName);
        bundle.putString(BUNDLE_FULLNAME, fullName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_profile_new, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Utility.configGiphy(getActivity());

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(BUNDLE_CUSTOMER_ID)) {
            customerId = bundle.getInt(BUNDLE_CUSTOMER_ID, 0);
            playlistId = bundle.getInt(BUNDLE_PLAYLIST_ID, 0);
            fullName = bundle.getString(BUNDLE_FULLNAME, "");
            userName = bundle.getString(BUNDLE_USERNAME, "");
            isOtherProfile = true;
        } else {
            SharedPrefManager prefManager = SharedPrefManager.getInstance(binding.getRoot().getContext());
            customerId = prefManager.getSharedPrefInt(AppConstants.INTENT_USER_ID);
            fullName = prefManager.getSharedPrefString(AppConstants.INTENT_FULL_NAME);
            userName = prefManager.getSharedPrefString(AppConstants.INTENT_USER_NAME);
            isOtherProfile = false;
        }
        genreList = CommonUtils.getGenre();

        initControls();
        viewModel.checkVM();
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
//            if (slidePanLayout == null && getActivity() instanceof youtube) {
////                slidePanLayout = ((youtube) getActivity()).mSlidingLayout;
//            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                slidePanLayout.setSlidingEnable(false);
                slidePanLayout.setSliderFadeColor(getActivity().getResources().getColor(android.R.color.transparent));
                slidePanLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        binding.setIsUserVerified(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (customerId != -1)
            callUserProfile(customerId);
    }

    private void initControls() {
        SharedPrefManager prefManager = SharedPrefManager.getInstance(binding.getRoot().getContext());
        playlists = new ArrayList<>();
        recentSongsList = new ArrayList<>();
        initRecyclerViews();

        binding.ablToolbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    binding.tbProfile.setVisibility(View.VISIBLE);
                    binding.llToolbar.setVisibility(View.VISIBLE);
                } else if (isShow) {
                    isShow = false;
                    binding.tbProfile.setVisibility(View.GONE);
                    binding.llToolbar.setVisibility(View.GONE);
                }
            }
        });

        binding.ivSettingToolbar.setOnClickListener(view -> {
            if (slidePanLayout.isOpen()) {
                slidePanLayout.closePane();
            } else {
                slidePanLayout.openPane();
            }
        });

        binding.llLike.setOnClickListener(view -> {
            if (!isOtherProfile) {
                Intent intent = new Intent(binding.llLike.getContext(), YoutubeUsersLikeListingActivity.class);
                startActivity(intent);
            }
        });

        binding.llCollab.setOnClickListener(view -> {
            if (!isOtherProfile) {
                Intent intent = new Intent(binding.llCollab.getContext(), YoutubeUsersCollaboratedListingActivity.class);
                startActivity(intent);
            }
        });

        binding.tvSeeAllRecently.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), AllRecntSongsActivity.class);
            intent.putExtra(USER_ID, customerId);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        });

        binding.tvSeeAll.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), UserPlaylistsActivity.class);
            intent.putExtra(USER_ID, customerId);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        });

        binding.ivSetting.setOnClickListener(view -> {
            if (slidePanLayout.isOpen()) {
                slidePanLayout.closePane();
            } else {
                slidePanLayout.openPane();
            }
        });

        binding.ivCollabPlaylists.setOnClickListener(view -> {
            if (playlistId > 0) sendCollabRequestNotification(playlistId, customerId);
            else collabDialog();
        });

        binding.ivLikeUser.setOnClickListener(view -> {
            String likeStatus = !binding.ivLikeUser.isSelected() ? "True" : "False";
            callUpdateLikeStatusAPI(prefManager.getSharedPrefInt(AppConstants.INTENT_USER_ID), customerId, likeStatus);
            sendLikeNotification(prefManager.getSharedPrefInt(AppConstants.INTENT_USER_ID), customerId, likeStatus);
        });
    }

    private void initRecyclerViews() {
        binding.rvPlaylist.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvPlaylist.setAdapter(new UserProfilePlaylistAdapter(binding.getRoot().getContext(), playlists, this));

        binding.rvRecentlyPlayed.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext(),
                LinearLayoutManager.VERTICAL, false));
        binding.rvRecentlyPlayed.setAdapter(new RecentPlayedSongsAdapter(binding.getRoot().getContext(),
                recentSongsList, position -> {
            try {
                if (recentSongsList.get(position).getSongType().equalsIgnoreCase(AppConstants.YOUTUBE)) {
                    ArrayList<PlaylistDetailModel> playlist;
                    playlist = new ArrayList<>();
                    for (int i = 0; i < recentSongsList.size(); i++) {
                        playlist.add(new PlaylistDetailModel(
                                recentSongsList.get(i).getSongName(),
                                recentSongsList.get(i).getSongThumbnail(),
                                recentSongsList.get(i).getSongId()
                        ));
                    }
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", position);
                    bundle.putString("title", recentSongsList.get(position).getSongName());
                    bundle.putString("description", "");
                    bundle.putString("thumbnail", recentSongsList.get(position).getSongThumbnail());
                    bundle.putString("videoId", recentSongsList.get(position).getSongId());
                    bundle.putParcelableArrayList("playlist", playlist);
//                    onMusicPlay(bundle);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    private void setUserData(CustomerBasicDetails userData) {
        setUserCover(userData);
        Glide.with(this)
                .load(userData.getUserAvatarLink())
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(binding.userDpIv);

        Glide.with(this)
                .load(userData.getUserAvatarLink())
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(binding.ivUserPic);

        binding.tvUserName.setText("@" + userData.getUserUsername());
        binding.tvUserFullName.setText(userData.getUserFullname());
        binding.tvUserNameToolbar.setText(userData.getUserFullname());
        binding.ivLikeUser.setSelected(userData.getLikeButtonStatus().equalsIgnoreCase("liked"));

        binding.tvLikeCount.setText(Utility.prettyCount(userData.getLikesReceived()));
        binding.tvCollabCount.setText(Utility.prettyCount(userData.getCollaboratorCount()));

        binding.setIsOtherProfile(isOtherProfile);
        binding.setIsUserVerified(userData.getProfileVerified());
        binding.setIsRecentSongShow(userData.getShowRecentSong());
    }

    private void setUserCover(CustomerBasicDetails userData) {
        if (userData.getIsCoverImageAvailable()) {
            binding.groupCover.setVisibility(View.GONE);
            GlideApp.with(this)
                    .load(userData.getUserCoverLink())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(binding.ivCoverImage);
        } else {
            binding.groupCover.setVisibility(View.VISIBLE);
            binding.cvBackground1.setBackgroundColor(Color.parseColor(userData.getUserCoverLink()));
            binding.vBackground3.setBackgroundColor(Color.parseColor(userData.getUserCoverLink()));
        }
    }

    private void callUserProfile(int customerId) {
        DataAPI dataAPI = RetrofitAPI.getData();
        String token = SharedPrefManager.getInstance(binding.getRoot().getContext()).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);

        dataAPI.callUserProfile(AppConstants.LOGIN_SIGNUP_HEADER, token, customerId).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.body() != null) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        setUserData(response.body().getCustomerBasicDetails());
                        playlists.clear();
                        if (response.body().getPlaylists().isEmpty()) {
                            binding.groupNoPlaylist.setVisibility(View.VISIBLE);
                            binding.rvPlaylist.setVisibility(View.GONE);
                            binding.tvSeeAll.setVisibility(View.GONE);
                        } else {
                            binding.groupNoPlaylist.setVisibility(View.GONE);
                            binding.rvPlaylist.setVisibility(View.VISIBLE);
                            binding.tvSeeAll.setVisibility(View.VISIBLE);
                            playlists.addAll(response.body().getPlaylists());
                            if (isVisible() && binding.rvPlaylist.getAdapter() != null)
                                binding.rvPlaylist.getAdapter().notifyDataSetChanged();
                        }

                        recentSongsList.clear();
                        if (response.body().getRecentlyPlayedSongs().isEmpty()) {
                            binding.groupNoRecentSongsFound.setVisibility(View.VISIBLE);
                            binding.rvRecentlyPlayed.setVisibility(View.GONE);
                            binding.tvSeeAllRecently.setVisibility(View.GONE);
                        } else {
                            binding.groupNoRecentSongsFound.setVisibility(View.GONE);
                            binding.rvRecentlyPlayed.setVisibility(View.VISIBLE);
                            binding.tvSeeAllRecently.setVisibility(View.VISIBLE);
                            recentSongsList.addAll(response.body().getRecentlyPlayedSongs());
                            if (isVisible() && binding.rvRecentlyPlayed.getAdapter() != null)
                                binding.rvRecentlyPlayed.getAdapter().notifyDataSetChanged();
                        }
                    } else if (response.body().getStatus().equalsIgnoreCase("failed")) {
                        Toast.makeText(getActivity(), response.body().getMessage(),
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callUpdateLikeStatusAPI(int userId, int searchedUserId, String likeStatus) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        //likeBtn.startAnimation();
        Call<UpdateLikeStatusModel> addLogCallback = dataAPI.updateLikeUserProfile(token, userId, searchedUserId, likeStatus);
        addLogCallback.enqueue(new Callback<UpdateLikeStatusModel>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(Call<UpdateLikeStatusModel> call, Response<UpdateLikeStatusModel> response) {

                if (response.body().getButtonStatus().equalsIgnoreCase("liked")) {
                    Logging.d("liked --->");
                    binding.ivLikeUser.setSelected(true);

                } else {
                    binding.ivLikeUser.setSelected(false);
                }
                binding.tvLikeCount.setText(Utility.prettyCount(response.body().getHowManyLikesUser()));
            }

            @Override
            public void onFailure(Call<UpdateLikeStatusModel> call, Throwable t) {
                binding.ivLikeUser.setSelected(false);
            }
        });
    }

    private void sendLikeNotification(int userId, int searchedUserId, String likeStatus) {

        if (likeStatus.equalsIgnoreCase("True")) {

            DataAPI dataAPI = RetrofitAPI.getData();
            String headerToken = AppConstants.TOKEN + SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

            Call<APIResponse> callback = dataAPI.sendNotification(headerToken, userId, searchedUserId, 0, AppConstants.PROFILE_LIKE);
            callback.enqueue(new Callback<APIResponse>() {

                @SuppressLint("NewApi")
                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                    //progressBar.setVisibility(View.GONE);
                    if (response != null && response.body() != null) {
                        Logging.d("like success--->" + new Gson().toJson(response.body()));
                        if (response.body().getStatus().equalsIgnoreCase("success")) {
                            binding.ivLikeUser.setSelected(true);
                        } else {
                            binding.ivLikeUser.setSelected(false);
                        }
                    } else {
                        binding.ivLikeUser.setSelected(false);
                    }
                }

                @SuppressLint("NewApi")
                @Override
                public void onFailure(Call<APIResponse> call, Throwable t) {
                    Log.i("Error: ", "ADD NOTIFICATION " + t.getMessage());
                    Logging.d("unlike onFailure--->");
                    binding.ivLikeUser.setSelected(false);
                }
            });
        }

    }

    private void sendCollabRequestNotification(int playlistId, int searchedUserId) {
        DataAPI dataAPI = RetrofitAPI.getData();
        String headerToken = AppConstants.TOKEN + SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        int userId = SharedPrefManager.getInstance(getActivity()).getSharedPrefInt(AppConstants.INTENT_USER_ID);

        Call<APIResponse> callback = dataAPI.sendNotification(headerToken, userId, searchedUserId, playlistId, AppConstants.COLLAB_REQUEST);
        callback.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, retrofit2.Response<APIResponse> response) {
                Logging.d("User Profile res-->" + new Gson().toJson(response.body()));
                if (response.body() != null) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                        binding.ivCollabPlaylists.setSelected(true);
                    } else if (response.body().getStatus().equalsIgnoreCase("failed")) {
                        Toast.makeText(getActivity(), response.body().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                Logging.i("Error: ", "ADD NOTIFICATION " + t.getMessage());
            }
        });
    }

    // add video to playlist
    private void collabDialog() {
        final AlertDialog.Builder mb = new AlertDialog.Builder(binding.getRoot().getContext());
        final View dialog = LayoutInflater.from(binding.getRoot().getContext()).inflate(R.layout.dialog_add_to_playlist, null, false);

        collabPlaylist = new ArrayList<>();
        RecyclerView userPlaylistsView = dialog.findViewById(R.id.playlists);
        TextView collabTextView = dialog.findViewById(R.id.textviewplaylistplayer);
        ProgressBar collabProgressBar = dialog.findViewById(R.id.progressbarPlayList);
        collabProgressBar.setVisibility(View.VISIBLE);
        userPlaylistsView.setHasFixedSize(true);
        userPlaylistsView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext(), LinearLayoutManager.VERTICAL, false));

        // parse public playlist to collaborate with
        parsePublicPlaylist(collabProgressBar, collabTextView);
        addToPlaylistAdapter = new AddToPlaylistAdapter(binding.getRoot().getContext(), collabPlaylist);
        addToPlaylistAdapter.setCustomItemClickListener(new AddToPlaylistAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                ass.dismiss();
                sendCollabRequestNotification(collabPlaylist.get(position).getId(), customerId);
            }
        });
        userPlaylistsView.setAdapter(addToPlaylistAdapter);

        mb.setView(dialog).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).setPositiveButton("Create New Playlist", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openCreatePLaylistDialog();
            }
        });

        mb.setView(dialog);
        ass = mb.create();
        ass.show();
    }

    private void parsePublicPlaylist(ProgressBar collabProgressBar, TextView collabTextView) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(binding.getRoot().getContext()).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<ArrayList<MyPlaylistModel>> callback = dataAPI.getPublicPlaylist(token, customerId);
        callback.enqueue(new Callback<ArrayList<MyPlaylistModel>>() {
            @Override
            public void onResponse(Call<ArrayList<MyPlaylistModel>> call, Response<ArrayList<MyPlaylistModel>> response) {
                collabProgressBar.setVisibility(View.GONE);
                collabPlaylist.clear();
                if (response != null && response.body() != null && response.body().size() > 0) {
                    collabTextView.setVisibility(View.GONE);
                    collabPlaylist.addAll(response.body());
                    addToPlaylistAdapter.notifyDataSetChanged();
                } else {
                    collabTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<MyPlaylistModel>> call, Throwable t) {
                collabProgressBar.setVisibility(View.GONE);
            }
        });
    }

    // dialog to add playlist
    private void openCreatePLaylistDialog() {
        openCreatePlaylistBottomsheet();
    }

    private void openCreatePlaylistBottomsheet() {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(binding.getRoot().getContext());
        View bottom_sheet = LayoutInflater.from(binding.getRoot().getContext()).inflate(R.layout.bottomsheet_create_playlist, null);
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
        AutoCompleteAdapter adapter = new AutoCompleteAdapter(binding.getRoot().getContext(),
                android.R.layout.simple_dropdown_item_1line, android.R.id.text1, genreList);
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
                Toast.makeText(getActivity(), "please give playlist some name", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(selectedGifLink[0])) {
                Toast.makeText(getActivity(), "please choose a GIF", Toast.LENGTH_SHORT).show();
            } else if (!Utility.isWebUrl(selectedGifLink[0])) {
                Toast.makeText(getActivity(), "please choose a valid GIF", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(playlistDesc.getText().toString().trim())) {
                Toast.makeText(getActivity(), "please enter playlist's description", Toast.LENGTH_SHORT).show();
            } else {

                ProgressDialog showMe = new ProgressDialog(binding.getRoot().getContext());
                showMe.setMessage("Please wait");
                showMe.setCancelable(true);
                showMe.setCanceledOnTouchOutside(false);
                showMe.show();

                DataAPI dataAPI = RetrofitAPI.getData();
                String token = AppConstants.TOKEN + SharedPrefManager.getInstance(binding.getRoot().getContext()).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
                Call<MyPlaylistModel> callback = dataAPI.callCreatePlayList(token,
                        SharedPrefManager.getInstance(binding.getRoot().getContext()).getSharedPrefString(AppConstants.INTENT_USER_TOKEN),
                        playlistName.getText().toString().trim(), playlistDesc.getText().toString().trim(),
                        selectedGifLink[0], "false", "",
                        tagView.getSelectedTags().size() > 0 ? TextUtils.join("|", tagView.getSelectedTags()) : "All");
                callback.enqueue(new Callback<MyPlaylistModel>() {
                    @Override
                    public void onResponse(Call<MyPlaylistModel> call, retrofit2.Response<MyPlaylistModel> response) {
                        showMe.dismiss();
                        if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                            bottomSheet.dismiss();
                            collabPlaylist.add(response.body());
                            addToPlaylistAdapter.notifyItemInserted(collabPlaylist.size());
                        } else {
                            Toast.makeText(binding.getRoot().getContext(),
                                    (response.body() != null && response.body().getMessage() != null) ? response.body().getMessage() : "Something went wrong!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MyPlaylistModel> call, Throwable t) {
                        showMe.dismiss();
                        Toast.makeText(binding.getRoot().getContext(),
                                t.getLocalizedMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        bottomSheet.show();

    }

    private void pinPlaylist(int playlistId, String pinType) {
        DataAPI dataAPI = RetrofitAPI.getData();
        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        dataAPI.callPinPlayList(token, SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_TOKEN),
                playlistId, pinType).enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                if (response.body() != null) {
                    if (response.body().getStatus().equalsIgnoreCase(SUCCESS_RESPONSE)) {
                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        callUserProfile(customerId);
                    } else if (response.body().getStatus().equalsIgnoreCase(FAILED_RESPONSE)) {
                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                Toast.makeText(binding.getRoot().getContext(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void likePlaylist(int playlistId) {
        DataAPI dataAPI = RetrofitAPI.getData();
        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        dataAPI.putPlaylistLikeNew(token, SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_TOKEN), playlistId).enqueue(new Callback<MyPlaylistModel>() {
            @Override
            public void onResponse(Call<MyPlaylistModel> call, Response<MyPlaylistModel> response) {
                if (response.body().getStatus().equalsIgnoreCase(SUCCESS_RESPONSE)) {
                    Toast.makeText(getActivity(), "Playlist liked", Toast.LENGTH_SHORT).show();
                } else if (response.body().getStatus().equalsIgnoreCase(FAILED_RESPONSE)) {
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MyPlaylistModel> call, Throwable t) {
                Toast.makeText(binding.getRoot().getContext(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showShareDialog(String url) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
        startActivity(Intent.createChooser(shareIntent, "Choose application to share"));
    }

    private void sharePlaylist(int playlistId) {
        DataAPI dataAPI = RetrofitAPI.getData();
        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(binding.getRoot().getContext()).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        dataAPI.callSharePlaylistApi(token, SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_TOKEN), playlistId, false).enqueue(new Callback<SharePlaylistResponse>() {
            @Override
            public void onResponse(Call<SharePlaylistResponse> call, Response<SharePlaylistResponse> response) {
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase(SUCCESS_RESPONSE)) {
                    showShareDialog(response.body().getData().getFullUrl());
                } else {
                    Toast.makeText(getActivity(),
                            (response.body() != null && response.body().getMessage() != null) ? response.body().getMessage() : "Something went wrong!",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SharePlaylistResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onItemClick(int position) {
        PlaylistDetailFragmentNew fragment = PlaylistDetailFragmentNew.getInstance(playlists.get(position).getId(),
                playlists.get(position).getAdmin_id(), playlists.get(position));
//        ((youtube) getActivity()).onLoadFragment(fragment);
    }

    @Override
    public void onHoverActionSelected(Action action, MyPlaylistModel playlist) {
        if (action.getId() == R.id.action_pin) {
            pinPlaylist(playlist.getId(), playlist.isPinnedPlaylist() ? UNPIN_PLAYLIST : PIN_PLAYLIST);
        } else if (action.getId() == R.id.action_like) {
            likePlaylist(playlist.getId());
        } else if (action.getId() == R.id.action_share) {
            sharePlaylist(playlist.getId());
        }

    }
}
