package com.shorincity.vibin.music_sharing.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.UI.youtube;
import com.shorincity.vibin.music_sharing.adapters.PlaylistCollaboratosAdapter;
import com.shorincity.vibin.music_sharing.adapters.PlaylistSongsAdapter;
import com.shorincity.vibin.music_sharing.adapters.UserSearchAdapter;
import com.shorincity.vibin.music_sharing.adapters.ViewCollab;
import com.shorincity.vibin.music_sharing.callbackclick.PlaylistDetailCallback;
import com.shorincity.vibin.music_sharing.databinding.FragmentCollaboratorsListBinding;
import com.shorincity.vibin.music_sharing.databinding.FragmentPlaylistSongListBinding;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.model.PlaylistSongCollabDeleteModel;
import com.shorincity.vibin.music_sharing.model.UserSearchModel;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.shorincity.vibin.music_sharing.viewmodel.PlaylistDetailsViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class PlaylistCollaboratosFragment extends MyBaseFragment {
    private FragmentCollaboratorsListBinding binding;
    private Context mContext;
    private static final String TAG = PlaylistDetailFragment.class.getName();
    private static final String PLAYLIST_ID = "playlist_id";
    private static final String VIEW_MODEL = "view_mode";
    private PlaylistDetailsViewModel viewModel;
    private String playlistId;

    public static PlaylistCollaboratosFragment getInstance(String playlistId, PlaylistDetailsViewModel viewModel) {
        PlaylistCollaboratosFragment fragment = new PlaylistCollaboratosFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PLAYLIST_ID, playlistId);
        fragment.setArguments(bundle);
        bundle.putParcelable(VIEW_MODEL, viewModel);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_collaborators_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = binding.getRoot().getContext();
        viewModel = getArguments().getParcelable(VIEW_MODEL);
        initControls();
    }

    private void initControls() {
        playlistId = getArguments().getString(PLAYLIST_ID);
        binding.rvSongs.setLayoutManager(new GridLayoutManager(binding.rvSongs.getContext(), 4));
        binding.rvSongs.setAdapter(new PlaylistCollaboratosAdapter(mContext, viewModel.getViewcollabList(), new PlaylistCollaboratosAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(int type, int position) {
                if (type == 0) {
                    ViewCollab collab = viewModel.getViewcollabList().get(position);
                    if (collab == null) {
                        openDailogSearchCollab(Integer.parseInt(playlistId));
                    }
                } else if (type == 1) {
                    ViewCollab collab = viewModel.getViewcollabList().get(position);
                    if (collab != null) {
                        callDeleteCollab(String.valueOf(collab.getId()));
                    }
                }
            }
        }));
        viewModel.getCollaboratorsList(mContext, playlistId, new PlaylistDetailCallback() {
            @Override
            public void onResponse() {
                if (binding.rvSongs.getAdapter() != null)
                    binding.rvSongs.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onError() {

            }
        });
    }

    // dialog to search collabs
    private RecyclerView userSearchRv;
    private UserSearchAdapter userSearchAdapter;
    private ArrayList<UserSearchModel> usersList;
    private ImageView placeholerIv;
    private EditText searchEdt;
    private ProgressBar searchCallabProgess;

    private void openDailogSearchCollab(int playlistId) {

        BottomSheetDialog bottomSheet = new BottomSheetDialog(mContext);
        View bottom_sheet = getActivity().getLayoutInflater().inflate(R.layout.bottomsheet_user_search, null);
        bottomSheet.setContentView(bottom_sheet);
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) bottom_sheet.getParent());
        mBehavior.setPeekHeight((int) getResources().getDimension(R.dimen._250sdp));
        bottomSheet.show();
//        bottom_sheet = findViewById(R.id.bottom_sheet);
//        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
//        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


        // final AlertDialog.Builder mb = new AlertDialog.Builder(this);
        // final View view = LayoutInflater.from(this).inflate(R.layout.fragment_user_search, null, false);
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        userSearchRv = bottom_sheet.findViewById(R.id.user_search_rv);
        searchEdt = bottom_sheet.findViewById(R.id.edittextSearch);
        placeholerIv = bottom_sheet.findViewById(R.id.placeholder);
        placeholerIv.setVisibility(View.VISIBLE);
        searchCallabProgess = bottom_sheet.findViewById(R.id.progressbar);

        searchEdt.setOnEditorActionListener((v, actionId, event) -> {
            callUserSearchAPI(searchEdt.getText().toString());

            return false;
        });

        usersList = new ArrayList<>();
        userSearchRv.setLayoutManager(new LinearLayoutManager(mContext));

        userSearchAdapter = new UserSearchAdapter(mContext, usersList, playlistId, true);
        userSearchAdapter.setCustomItemClickListener((v, position) -> {

            if (v.getId() == R.id.add_collab_btn) {
//                    sendCollabRequestNotification(id, usersList.get(position).getId(), AppConstants.COLLAB_REQUEST);
            } else {
                bottomSheet.dismiss();
                OtherUserProfileFragment fragment = OtherUserProfileFragment.getInstance(
                        usersList.get(position).getId(),
                        playlistId,
                        usersList.get(position).getUsername(),
                        usersList.get(position).getFullname());
                ((youtube) getActivity()).onLoadFragment(fragment);

                /*startActivity(new Intent(mContext, OtherUserProfileActivity.class)
                        .putExtra(AppConstants.INTENT_SEARCHED_USER_ID, usersList.get(position).getId())
                        .putExtra(AppConstants.INTENT_SEARCHED_USER_NAME, usersList.get(position).getUsername())
                        .putExtra(AppConstants.INTENT_SEARCHED_FULL_NAME, usersList.get(position).getFullname())
                        .putExtra(AppConstants.INTENT_PLAYLIST_ID, id)
                        .putExtra(AppConstants.INTENT_COMING_FROM, PlaylistDetailActivity.class.getName()));*/
            }
        });
        userSearchRv.setAdapter(userSearchAdapter);

    }

    private void callUserSearchAPI(String searchQuery) {

        searchCallabProgess.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<ArrayList<UserSearchModel>> callback = dataAPI.searchUsers(token, searchQuery);
        callback.enqueue(new Callback<ArrayList<UserSearchModel>>() {
            @Override
            public void onResponse(Call<ArrayList<UserSearchModel>> call, retrofit2.Response<ArrayList<UserSearchModel>> response) {
                searchCallabProgess.setVisibility(View.GONE);
                usersList.clear();
                if (response != null && response.body() != null && response.body().size() > 0) {
                    placeholerIv.setVisibility(View.GONE);
                    usersList.addAll(response.body());
                    userSearchAdapter.notifyDataSetChanged();
                } else {
                    placeholerIv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<UserSearchModel>> call, Throwable t) {
                searchCallabProgess.setVisibility(View.GONE);
            }
        });
    }

    private void callDeleteCollab(String collabId) {
        ProgressDialog showMe = new ProgressDialog(mContext);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();

        DataAPI dataAPI = RetrofitAPI.getData();
        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        Call<PlaylistSongCollabDeleteModel> callback = dataAPI.callDeleteSongsOrCollabApi(token,
                SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_TOKEN),
                Integer.parseInt(playlistId),
                "", collabId);
        callback.enqueue(new Callback<PlaylistSongCollabDeleteModel>() {
            @Override
            public void onResponse(Call<PlaylistSongCollabDeleteModel> call, retrofit2.Response<PlaylistSongCollabDeleteModel> response) {
                showMe.dismiss();
                if (response.body() != null &&
                        response.body().getStatus().equalsIgnoreCase("success")) {
                    PlaylistSongCollabDeleteModel mResponse = response.body();
                    ArrayList<ViewCollab> songList = viewModel.getViewcollabList();
                    List<Integer> deletedSongs = mResponse.getDeletedCollaborator();
                    for (int i = 0; i < songList.size(); i++) {
                        ViewCollab mBean = songList.get(i);
                        if (deletedSongs.size() > 1) {
                            int id = deletedSongs.get(0);
                            if (mBean != null && mBean.getId() == id) {
                                songList.remove(i);
                                if (binding.rvSongs.getAdapter() != null)
                                    binding.rvSongs.getAdapter().notifyItemRemoved(i);
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                } else {
                    Toast.makeText(mContext,
                            (response.body() != null && response.body().getMessage() != null) ? response.body().getMessage() : "Something went wrong!",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PlaylistSongCollabDeleteModel> call, Throwable t) {
                showMe.dismiss();
                Toast.makeText(mContext,
                        t.getLocalizedMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

}
