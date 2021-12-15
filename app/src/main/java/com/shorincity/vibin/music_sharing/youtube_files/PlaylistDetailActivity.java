
package com.shorincity.vibin.music_sharing.youtube_files;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.like.LikeButton;
import com.shorincity.vibin.music_sharing.adapters.ViewCollabAdapter;
import com.shorincity.vibin.music_sharing.model.APIResponse;
import com.shorincity.vibin.music_sharing.model.MyPlaylistModel;
import com.shorincity.vibin.music_sharing.model.PlayListDeleteModel;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.model.PlaylistLikeModel;
import com.shorincity.vibin.music_sharing.model.PublicPlaylistItemAdapter;
import com.shorincity.vibin.music_sharing.model.firebase.RealTimeModel;
import com.shorincity.vibin.music_sharing.model.firebase.RealTimeSession;
import com.shorincity.vibin.music_sharing.model.firebase.RealTimeUser;
import com.shorincity.vibin.music_sharing.model.UserSearchModel;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.activity.OtherUserProfileActivity;
import com.shorincity.vibin.music_sharing.activity.RealTimePlayer;
import com.shorincity.vibin.music_sharing.adapters.UserSearchAdapter;
import com.shorincity.vibin.music_sharing.adapters.ViewCollab;
import com.shorincity.vibin.music_sharing.fragment.ErrorDailogFragment;
import com.shorincity.vibin.music_sharing.ripples.RippleButton;
import com.shorincity.vibin.music_sharing.ripples.listener.OnRippleCompleteListener;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;

import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.shorincity.vibin.music_sharing.utils.Utility;
import com.giphy.sdk.core.models.enums.RenditionType;
import com.giphy.sdk.ui.views.GifView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

// Playlist Detail Screen
public class PlaylistDetailActivity extends AppCompatActivity {

    private static String TAG = PlaylistDetailActivity.class.getName();

    String view_collabUrl = AppConstants.BASE_URL + "playlist/view_collab/";
    int id;
    PopupWindow popupWindow = null;
    ArrayList<ViewCollab> viewcollabList;
    com.shorincity.vibin.music_sharing.adapters.ViewCollabAdapter ViewCollabAdapter;
    RecyclerView viewCollabRecyclerView;
    ProgressBar progressBarViewCollab;
    NestedScrollView sliding_main;
    ImageView more_image;
    CardView cardview_option;
    MyPlaylistModel myPlaylistModel;
    RecyclerView playlistRv;
    ArrayList<PlaylistDetailModel> playlist;
    ArrayList<String> songNameList;
    private PublicPlaylistItemAdapter publicPlaylistItemAdapter;
    public GifView gifView;
    public ImageView pauseGifBtn;
    public TextView mTextViewTitle, descTxt, likeCountTxt, durationTv;
    private Button play_btn;
    private SimpleCursorAdapter mAdapter;
    int testNum = 0;
    RealTimeModel realTimeModel;
    DatabaseReference myRef;

    ProgressDialog mProgressDialog;
    RippleButton live_streaming_btn;
    boolean isPressed = false;
    int admin_id;
    TextView txt_edit_playlist;
    TextView txt_select_playlist;
    TextView txt_Leave_playlist;
    ArrayList<Integer> playlistId;
    ArrayList<Integer> collabsId;
    StringBuffer finalplaylistid;
    StringBuffer finalcollabslistid;
    PlaylistDetailActivity playlistDetailActivity;
    BottomSheetDialog dialog;
    LinearLayout ll_edit;
    TextView txt_edit, txt_cancel;
    LikeButton likeBtn;

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);

        finalcollabslistid = new StringBuffer();
        finalplaylistid = new StringBuffer();
        id = getIntent().getExtras().getInt("id");
        admin_id = getIntent().getExtras().getInt("admin_id");

        playlistId = new ArrayList<>();
        collabsId = new ArrayList<>();
        myPlaylistModel = (MyPlaylistModel) getIntent().getSerializableExtra(AppConstants.INTENT_PLAYLIST);
        inItViews();


        setViews();

        inItListeners();

        setAdapter();

        dialog = new BottomSheetDialog(PlaylistDetailActivity.this);
        dialog.setContentView(R.layout.layout_bottom_dialog);


        txt_edit_playlist = dialog.findViewById(R.id.txt_edit_playlist);
        txt_select_playlist = dialog.findViewById(R.id.txt_select_playlist);
        txt_Leave_playlist = dialog.findViewById(R.id.txt_Leave_playlist);
        txt_select_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finalplaylistid = new StringBuffer();
                finalcollabslistid = new StringBuffer();
                isPressed = false;
                if (txt_select_playlist.getText().toString().equalsIgnoreCase("Select")) {
                    txt_select_playlist.setText("Clear");
                    txt_edit_playlist.setVisibility(View.VISIBLE);
                    for (int i = 0; i < playlist.size(); i++) {
                        if (admin_id == SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefInt(AppConstants.INTENT_USER_ID)) {
                            playlist.get(i).setEditable(true);
                            playlist.get(i).setSelected(false);
                        } else if (playlist.get(i).getAdded_by().equals(SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefInt(AppConstants.INTENT_USER_ID))) {
                            playlist.get(i).setEditable(true);
                            playlist.get(i).setSelected(false);
                        }
                    }
                    publicPlaylistItemAdapter.notifyDataSetChanged();
                    if (admin_id == SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefInt(AppConstants.INTENT_USER_ID)) {
                        for (int i = 0; i < viewcollabList.size(); i++) {
                            viewcollabList.get(i).setEditable(true);
                            viewcollabList.get(i).setSelected(false);
                        }
                        ViewCollabAdapter.notifyDataSetChanged();
                    }
                } else {
                    txt_select_playlist.setText("Select");
                    txt_edit_playlist.setVisibility(View.GONE);
                    for (int i = 0; i < playlist.size(); i++) {
                        playlist.get(i).setEditable(false);
                    }
                    publicPlaylistItemAdapter.notifyDataSetChanged();
                    if (admin_id == SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefInt(AppConstants.INTENT_USER_ID)) {
                        for (int i = 0; i < viewcollabList.size(); i++) {
                            viewcollabList.get(i).setEditable(false);
                        }
                        ViewCollabAdapter.notifyDataSetChanged();
                    }
                }

            }
        });

        txt_edit_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    dialog.dismiss();
                    if (playlistId.size() > 0) {
                        for (Integer id : playlistId) {
                            finalplaylistid.append(id + ",");
                        }
                        finalplaylistid.deleteCharAt(finalplaylistid.length() - 1);
                    }
                    if (collabsId.size() > 0) {
                        for (Integer id : collabsId) {
                            finalcollabslistid.append(id + ",");
                        }
                        finalcollabslistid.deleteCharAt(finalcollabslistid.length() - 1);
                    }
                    if (finalplaylistid.length() > 0 || finalcollabslistid.length() > 0) {
                        editPlayList(finalplaylistid, finalcollabslistid);
                        dialog.dismiss();
                    } else {
                        if (admin_id == SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefInt(AppConstants.INTENT_USER_ID)) {
                            Toast.makeText(getApplicationContext(), "Please Select Playlist song or Collaborator which you want to delete", +2000).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Please Select Playlist song which you want to delete", +2000).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        txt_Leave_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                try {
                    Dialog builder = new Dialog(PlaylistDetailActivity.this);
                    builder.setContentView(R.layout.dialog_for_leave_collabs);
                    builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    builder.show();
                    Button btn_Yes = builder.findViewById(R.id.btn_Yes);
                    Button btn_No = builder.findViewById(R.id.btn_No);
                    RadioButton radio_1 = builder.findViewById(R.id.radio_1);
                    RadioButton radio_2 = builder.findViewById(R.id.radio_2);
                    btn_No.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            builder.dismiss();
                        }
                    });
                    btn_Yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (radio_1.isChecked()) {
                                playlistId = new ArrayList<>();
                                for (int i = 0; i < playlist.size(); i++) {
                                    if (playlist.get(i).getAdded_by().equals(SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefInt(AppConstants.INTENT_USER_ID))) {
                                        playlistId.add(playlist.get(i).getId());
                                    }
                                }
                                finalplaylistid = new StringBuffer();
                                finalcollabslistid = new StringBuffer();
                                if (playlistId.size() > 0) {
                                    for (Integer id : playlistId) {
                                        finalplaylistid.append(id + ",");
                                    }
                                    finalplaylistid.deleteCharAt(finalplaylistid.length() - 1);
                                }
                                finalcollabslistid.append(SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefInt(AppConstants.INTENT_USER_ID));
                                editPlayList(finalplaylistid, finalcollabslistid);
                            } else if (radio_2.isChecked()) {
                                finalcollabslistid = new StringBuffer();
                                finalplaylistid = new StringBuffer();
                                finalcollabslistid.append(SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefInt(AppConstants.INTENT_USER_ID));
                                editPlayList(finalplaylistid, finalcollabslistid);
                            } else {
                                Toast.makeText(getApplicationContext(), "Please Check One Option", +2000).show();
                            }
                            builder.dismiss();
                        }
                    });
                    builder.setCanceledOnTouchOutside(true);
                    builder.setCancelable(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        more_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                if (!isPressed) {
                    if (playlist.size() > 0) {
                        if (admin_id == SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefInt(AppConstants.INTENT_USER_ID)) {
                            txt_select_playlist.setVisibility(View.VISIBLE);
                        } else {
                            if (txt_select_playlist.getText().toString().equalsIgnoreCase("Clear")) {
                                txt_Leave_playlist.setVisibility(View.GONE);
                            } else {
                                txt_Leave_playlist.setVisibility(View.VISIBLE);
                            }
                            txt_select_playlist.setVisibility(View.GONE);
                            for (PlaylistDetailModel playListDeleteModel : playlist) {
                                if (playListDeleteModel.getAdded_by().equals(SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefInt(AppConstants.INTENT_USER_ID))) {
                                    txt_select_playlist.setVisibility(View.VISIBLE);
                                    break;
                                }
                            }
                        }
                    } else {
                        if (admin_id == SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefInt(AppConstants.INTENT_USER_ID)) {
                            if (viewcollabList.size() > 0) {
                                txt_select_playlist.setVisibility(View.VISIBLE);
                            } else {
                                txt_select_playlist.setVisibility(View.GONE);
                            }
                        } else {
                            txt_Leave_playlist.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    dialog.dismiss();
                }
            }
        });

        callPlaylistDetailAPI(String.valueOf(id));

        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int pos = 0;

                try {
                    if (playlist.get(pos).getType().equalsIgnoreCase(AppConstants.YOUTUBE)) {
                        Intent intent = new Intent(PlaylistDetailActivity.this, PlayYoutubeVideoActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("position", pos);
                        bundle.putString("title", playlist.get(pos).getName());
                        bundle.putString("description", "");
                        bundle.putString("thumbnail", playlist.get(pos).getImage());
                        bundle.putString("videoId", playlist.get(pos).getTrackId());
                        bundle.putString("from", "channel");
                        bundle.putParcelableArrayList("playlist", (ArrayList<? extends Parcelable>) playlist);
                        intent.putExtra("data", bundle);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                if (gifView.getTag() == null || (gifView.getTag() != null && gifView.getTag().equals("playing"))) {
//                    play_btn.setBackgroundResource(R.drawable.ic_baseline_play_arrow_white);
//                    pauseGifBtn.setColorFilter(ContextCompat.getColor(PlaylistDetailActivity.this, R.color.gph_white));
//                    gifView.setColorFilter(ContextCompat.getColor(PlaylistDetailActivity.this, R.color.gph_white), android.graphics.PorterDuff.Mode.MULTIPLY);
//                    gifView.pause();
//                    gifView.setTag("pause");
//                } else {
//                    play_btn.setBackgroundResource(R.drawable.ic_baseline_pause_white);
//                    pauseGifBtn.setColorFilter(ContextCompat.getColor(PlaylistDetailActivity.this, R.color.light_gray));
//                    gifView.setColorFilter(ContextCompat.getColor(PlaylistDetailActivity.this, R.color.light_gray), android.graphics.PorterDuff.Mode.MULTIPLY);
//                    gifView.play();
//                    gifView.setTag("playing");
//                }
            }
        });
    }

    private void editPlayList(StringBuffer finalplaylistid, StringBuffer finalcollabslistid) {
        DataAPI dataAPI = RetrofitAPI.getData();

        String userToken = SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
        String userTokenapi = AppConstants.TOKEN + SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        Log.d(TAG, "editPlayList: " + userTokenapi);
        Log.d("yash", "editPlayList: params" + userTokenapi + "\n" + userToken + "\n" + finalplaylistid + "\n" + finalcollabslistid + "\n" + id);
        Call<PlayListDeleteModel> callback = dataAPI.getEditPlaylist(userTokenapi, userToken, id, finalplaylistid.toString(), finalcollabslistid.toString());
//        J72EB5A5JM1K1QD6AIS6LG37
        callback.enqueue(new Callback<PlayListDeleteModel>() {
            @Override
            public void onResponse(Call<PlayListDeleteModel> call, retrofit2.Response<PlayListDeleteModel> response) {
                try {
                    System.out.println(response.body());
                    if (response.body().getStatus() != null) {
                        if (response.body().getStatus().equalsIgnoreCase("success")) {
                            Toast.makeText(getApplicationContext(), response.body().getMessage(), +2000).show();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), response.body().getMessage(), +2000).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<PlayListDeleteModel> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    public void selectedplaylistID(int ids, boolean selected) {
        try {
            if (selected) {
                playlistId.add(ids);
                Log.d("yash", "selectedplaylistID: " + selected + " Size" + playlistId.size());
            } else {
                for (int i = 0; i < playlistId.size(); i++) {
                    if (playlistId.get(i).equals(ids)) {
                        playlistId.remove(i);
                        Log.d("yash", "removeplaylistID: " + selected + " Size" + playlistId.size());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectedcollabsID(int ids, boolean selected) {
        try {
            if (selected) {
                collabsId.add(ids);
                Log.d("yash", "selectedcollabsID: " + selected + " Size" + collabsId.size());
            } else {
                for (int i = 0; i < collabsId.size(); i++) {
                    if (collabsId.get(i).equals(ids)) {
                        collabsId.remove(i);
                        Log.d("yash", "removecollabsID: " + selected + " Size" + collabsId.size());
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void inItListeners() {

        pauseGifBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalplaylistid = new StringBuffer();
                finalcollabslistid = new StringBuffer();
                if (admin_id == SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefInt(AppConstants.INTENT_USER_ID)) {
                    ll_edit.setVisibility(View.VISIBLE);
                    for (int i = 0; i < playlist.size(); i++) {
                        if (admin_id == SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefInt(AppConstants.INTENT_USER_ID)) {
                            playlist.get(i).setEditable(true);
                            playlist.get(i).setSelected(false);
                        } else if (playlist.get(i).getAdded_by().equals(SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefInt(AppConstants.INTENT_USER_ID))) {
                            playlist.get(i).setEditable(true);
                            playlist.get(i).setSelected(false);
                        }
                    }
                    publicPlaylistItemAdapter.notifyDataSetChanged();
                    if (admin_id == SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefInt(AppConstants.INTENT_USER_ID)) {
                        for (int i = 0; i < viewcollabList.size(); i++) {
                            viewcollabList.get(i).setEditable(true);
                            viewcollabList.get(i).setSelected(false);
                        }
                        ViewCollabAdapter.notifyDataSetChanged();
                    }
                } else {
                    try {
                        Dialog builder = new Dialog(PlaylistDetailActivity.this);
                        builder.setContentView(R.layout.dialog_for_leave_collabs);
                        builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        builder.show();
                        Button btn_Yes = builder.findViewById(R.id.btn_Yes);
                        Button btn_No = builder.findViewById(R.id.btn_No);
                        RadioButton radio_1 = builder.findViewById(R.id.radio_1);
                        RadioButton radio_2 = builder.findViewById(R.id.radio_2);
                        btn_No.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                builder.dismiss();
                            }
                        });
                        btn_Yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (radio_1.isChecked()) {
                                    playlistId = new ArrayList<>();
                                    for (int i = 0; i < playlist.size(); i++) {
                                        if (playlist.get(i).getAdded_by().equals(SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefInt(AppConstants.INTENT_USER_ID))) {
                                            playlistId.add(playlist.get(i).getId());
                                        }
                                    }
                                    finalplaylistid = new StringBuffer();
                                    finalcollabslistid = new StringBuffer();
                                    if (playlistId.size() > 0) {
                                        for (Integer id : playlistId) {
                                            finalplaylistid.append(id + ",");
                                        }
                                        finalplaylistid.deleteCharAt(finalplaylistid.length() - 1);
                                    }
                                    finalcollabslistid.append(SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefInt(AppConstants.INTENT_USER_ID));
                                    editPlayList(finalplaylistid, finalcollabslistid);
                                } else if (radio_2.isChecked()) {
                                    finalcollabslistid = new StringBuffer();
                                    finalplaylistid = new StringBuffer();
                                    finalcollabslistid.append(SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefInt(AppConstants.INTENT_USER_ID));
                                    editPlayList(finalplaylistid, finalcollabslistid);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Please Check One Option", +2000).show();
                                }
                                builder.dismiss();
                            }
                        });
                        builder.setCanceledOnTouchOutside(true);
                        builder.setCancelable(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        txt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (playlistId.size() > 0) {
                        for (Integer id : playlistId) {
                            finalplaylistid.append(id + ",");
                        }
                        finalplaylistid.deleteCharAt(finalplaylistid.length() - 1);
                    }
                    if (collabsId.size() > 0) {
                        for (Integer id : collabsId) {
                            finalcollabslistid.append(id + ",");
                        }
                        finalcollabslistid.deleteCharAt(finalcollabslistid.length() - 1);
                    }
                    if (finalplaylistid.length() > 0 || finalcollabslistid.length() > 0) {
                        editPlayList(finalplaylistid, finalcollabslistid);

                    } else {
                        if (admin_id == SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefInt(AppConstants.INTENT_USER_ID)) {
                            Toast.makeText(getApplicationContext(), "Please Select Playlist song or Collaborator which you want to delete", +2000).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Please Select Playlist song which you want to delete", +2000).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_edit.setVisibility(View.GONE);
                for (int i = 0; i < playlist.size(); i++) {
                    playlist.get(i).setEditable(false);
                }
                publicPlaylistItemAdapter.notifyDataSetChanged();
                if (admin_id == SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefInt(AppConstants.INTENT_USER_ID)) {
                    for (int i = 0; i < viewcollabList.size(); i++) {
                        viewcollabList.get(i).setEditable(false);
                    }
                    ViewCollabAdapter.notifyDataSetChanged();
                }
            }
        });

        findViewById(R.id.add_collab_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
//                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                } else {
//                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                }
                openDailogSearchCollab();
            }
        });

        findViewById(R.id.like_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                putPublicPLaylistLike(id);
            }
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

    private void inItViews() {


        live_streaming_btn = findViewById(R.id.live_streaming_btn);
        gifView = findViewById(R.id.gif_iv);
        likeBtn = findViewById(R.id.like_btn);
        pauseGifBtn = findViewById(R.id.pause_gif_btn);
        more_image = findViewById(R.id.more_image);
        mTextViewTitle = findViewById(R.id.item_title);
        durationTv = findViewById(R.id.item_duration);
        mTextViewTitle = findViewById(R.id.item_title);
        likeCountTxt = findViewById(R.id.like_count_txt);
        descTxt = findViewById(R.id.item_desc);
        play_btn = findViewById(R.id.play_btn);
        sliding_main = findViewById(R.id.sliding_main);
        ll_edit = findViewById(R.id.ll_edit);
        txt_edit = findViewById(R.id.txt_edit_playlist);
        txt_cancel = findViewById(R.id.txt_cancel_playlist);


        viewcollabList = new ArrayList<>();
        viewCollabRecyclerView = findViewById(R.id.view_collaborations);
        progressBarViewCollab = findViewById(R.id.progressbarViewCollab);
        progressBarViewCollab.setVisibility(View.VISIBLE);
        viewCollabRecyclerView.setHasFixedSize(true);
        viewCollabRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        ViewCollabAdapter = new ViewCollabAdapter(this, viewcollabList);
        ViewCollabAdapter.setCustomItemClickListener(new com.shorincity.vibin.music_sharing.adapters.ViewCollabAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                startActivity(new Intent(PlaylistDetailActivity.this, OtherUserProfileActivity.class).putExtra(AppConstants.INTENT_SEARCHED_USER_ID, viewcollabList.get(position).getId()).putExtra(AppConstants.INTENT_SEARCHED_USER_NAME, viewcollabList.get(position).getUsername()).putExtra(AppConstants.INTENT_SEARCHED_FULL_NAME, viewcollabList.get(position).getFullname()));
            }

            @Override
            public void onSelectedcollabsID(int ids, boolean selected) {
                selectedcollabsID(ids,selected);
            }
        });


        sliding_main.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

            }
        });
        viewCollabRecyclerView.setAdapter(ViewCollabAdapter);

        parseCollaborations();


        final String[] from = new String[]{"songName"};
        final int[] to = new int[]{android.R.id.text1};
        mAdapter = new SimpleCursorAdapter(PlaylistDetailActivity.this, android.R.layout.simple_list_item_1, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        live_streaming_btn.setOnRippleCompleteListener(new OnRippleCompleteListener() {
            @Override
            public void onComplete(View v) {
                processLiveSharing();
            }
        });

        /*final SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setSuggestionsAdapter(mAdapter);
        searchView.setIconifiedByDefault(false);*/

        // Getting selected (clicked) item suggestion
        /*searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) mAdapter.getItem(position);
                String txt = cursor.getString(cursor.getColumnIndex("songName"));
                //searchView.setQuery(txt, true);

                int actualPostion = songNameList.indexOf(txt);

                playlistRv.scrollToPosition(actualPostion);
                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                // Your code here
                return true;
            }
        });*/

        /*searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                populateAdapter(s);
                return false;
            }
        });*/
    }

    private void processLiveSharing() {
        if (playlist == null || playlist.size() == 0) {
            Toast.makeText(PlaylistDetailActivity.this, "Please create a playlist first!", Toast.LENGTH_LONG).show();
            return;
        } else if (viewcollabList == null || viewcollabList.size() == 0) {
            Toast.makeText(PlaylistDetailActivity.this, "Please create a collaborator first!", Toast.LENGTH_LONG).show();
            return;
        }

        RealTimeSession realTimeSession = new RealTimeSession();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(PlaylistDetailActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        int userId = SharedPrefManager.getInstance(PlaylistDetailActivity.this).getSharedPrefInt(AppConstants.INTENT_USER_ID);

        long sesionTimeStamp = System.currentTimeMillis();
        String sessionKey = AppConstants.SESSION_CHILD + userId + "_" + sesionTimeStamp;

        NumberFormat formatter = new DecimalFormat("00");
        String duration = "";

        duration = formatter.format(myPlaylistModel.getPlaylistDurationHours()) + ":" + formatter.format(myPlaylistModel.getPlaylistDurationMinutes()) + ":" + formatter.format(myPlaylistModel.getPlaylistDurationSeconds());

        realTimeSession.setSession_token(token);
        realTimeSession.setAdminId(userId);
        realTimeSession.setPlaylist_id(id);
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


        realTimeModel.getSessions().put(sessionKey, realTimeSession);

        String userIds = "", userKeys = "";

        for (int i = 0; i < viewcollabList.size(); i++) {
            String userKey = AppConstants.USER_CHILD + userId + "_" + viewcollabList.get(i).getId();//+"_"+sesionTimeStamp;

            userIds = TextUtils.isEmpty(userIds) ? "" + viewcollabList.get(i).getId() : userIds + ":" + viewcollabList.get(i).getId();
            userKeys = TextUtils.isEmpty(userKeys) ? userKey : userKeys + ":" + userKey;


            RealTimeUser realTimeUser = new RealTimeUser(sessionKey, token, viewcollabList.get(i).getId(), AppConstants.WAIT);
            realTimeModel.getUsers().put(userKey, realTimeUser);
        }

        callAddRealTimeInfo(sessionKey, token, userIds, userKeys, myRef, realTimeModel);
    }


    private void setViews() {

        if (myPlaylistModel != null) {
            MyPlaylistModel currentItem = myPlaylistModel;
            String name = currentItem.getName();

            mTextViewTitle.setText(name);
            NumberFormat formatter = new DecimalFormat("00");
            String duration = "";

            if (currentItem.getPlaylistDurationHours() > 0) {
                duration = formatter.format(currentItem.getPlaylistDurationHours() * 60 + currentItem.getPlaylistDurationMinutes()) + ":" + formatter.format(currentItem.getPlaylistDurationSeconds());
            } else if (currentItem.getPlaylistDurationMinutes() > 0) {
                duration = formatter.format(currentItem.getPlaylistDurationMinutes()) + ":" + formatter.format(currentItem.getPlaylistDurationSeconds());
            } else if (currentItem.getPlaylistDurationSeconds() > 0) {
                duration = formatter.format(currentItem.getPlaylistDurationSeconds());
            } else
                duration = "00:00";

            durationTv.setText(duration + "-");

            if (!TextUtils.isEmpty(currentItem.getDescription())) {
                descTxt.setVisibility(View.VISIBLE);
                descTxt.setText(currentItem.getDescription());
            } else {
                descTxt.setVisibility(View.GONE);
            }

            if (currentItem.getLikes() > 0) {
                likeCountTxt.setText(Utility.prettyCount(currentItem.getLikes()) + " Likes");
            } else {
                likeCountTxt.setText("");
            }

            String gifArraySplit[] = currentItem.getGifLink().split("/");
            String mediaId = gifArraySplit[gifArraySplit.length - 1];

            gifView.setMediaWithId(mediaId, RenditionType.preview, ContextCompat.getDrawable(this, R.color.light_gray));


            String fullName = SharedPrefManager.getInstance(PlaylistDetailActivity.this).getSharedPrefString(AppConstants.INTENT_FULL_NAME);

            if (!myPlaylistModel.getAdminName().equalsIgnoreCase(fullName)) {
                findViewById(R.id.collab_hldr).setVisibility(View.GONE);
                findViewById(R.id.realtime_btn_hldr).setVisibility(View.GONE);
            }
        }
    }

    private void setAdapter() {

        playlist = new ArrayList<>();

        playlistRv = findViewById(R.id.playlist_rv);
        playlistRv.setLayoutManager(new LinearLayoutManager(PlaylistDetailActivity.this, LinearLayoutManager.VERTICAL, false));
        playlistRv.setHasFixedSize(true);

        publicPlaylistItemAdapter = new PublicPlaylistItemAdapter(PlaylistDetailActivity.this, playlist);
        publicPlaylistItemAdapter.setCustomItemClickListener(new PublicPlaylistItemAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                try {
                    if (playlist.get(position).getType().equalsIgnoreCase(AppConstants.YOUTUBE)) {
                        Intent intent = new Intent(PlaylistDetailActivity.this, PlayYoutubeVideoActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("position", position);
                        bundle.putString("title", playlist.get(position).getName());
                        bundle.putString("description", "");
                        bundle.putString("thumbnail", playlist.get(position).getImage());
                        bundle.putString("videoId", playlist.get(position).getTrackId());
                        bundle.putString("from", "channel");
                        bundle.putParcelableArrayList("playlist", (ArrayList<? extends Parcelable>) playlist);
                        intent.putExtra("data", bundle);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSelectedplaylistID(int ids, boolean selected) {
                selectedplaylistID(ids, selected);
            }
        });
        playlistRv.setAdapter(publicPlaylistItemAdapter);
    }

    // add collaborations to recycler view
    private void parseCollaborations() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, view_collabUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // Logging.d("Avatar response-->"+response);
                    viewcollabList.clear();
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String email = jsonObject.getString("email");
                        String username = jsonObject.getString("username");
                        String fullname = jsonObject.getString("fullname");
                        String avatar_link = jsonObject.getString("avatar_link");

                        ViewCollab viewCollab = new ViewCollab();
                        viewCollab.setAvatarLink(avatar_link);
                        viewCollab.setEmail(email);
                        viewCollab.setFullname(fullname);
                        viewCollab.setUsername(username);
                        viewCollab.setId(id);

                        viewcollabList.add(viewCollab);
//                        if (viewcollabList.size() > 0) {
//                            more_image.setVisibility(View.VISIBLE);
//                        } else {
//                            if (admin_id != SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefInt(AppConstants.INTENT_USER_ID)) {
//                                more_image.setVisibility(View.VISIBLE);
//                            } else {
//                                more_image.setVisibility(View.GONE);
//                            }
//
//                        }
                        ViewCollabAdapter.notifyDataSetChanged();
                    }
                    progressBarViewCollab.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String token = SharedPrefManager.getInstance(PlaylistDetailActivity.this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);

                params.put("token", token);
                params.put("playlist_id", String.valueOf(id));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String token = AppConstants.TOKEN + SharedPrefManager.getInstance(PlaylistDetailActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
                params.put("Authorization", token);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void callPlaylistDetailAPI(String playlistId) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(PlaylistDetailActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        Call<ArrayList<PlaylistDetailModel>> callback = dataAPI.getPublicPlaylistDetail(token, SharedPrefManager.getInstance(PlaylistDetailActivity.this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN), playlistId);
        callback.enqueue(new Callback<ArrayList<PlaylistDetailModel>>() {
            @Override
            public void onResponse(Call<ArrayList<PlaylistDetailModel>> call, retrofit2.Response<ArrayList<PlaylistDetailModel>> response) {
                playlist.clear();
                if (response != null && response.body() != null && response.body().size() > 0) {

                    playlistRv.setVisibility(View.VISIBLE);
                    playlist.addAll(response.body());
//                    if (playlist.size() > 0) {
//                        more_image.setVisibility(View.VISIBLE);
//                    } else {
//                        if (admin_id != SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefInt(AppConstants.INTENT_USER_ID)) {
//                            more_image.setVisibility(View.VISIBLE);
//                        } else {
//                            more_image.setVisibility(View.GONE);
//                        }
//
//                    }
                    Collections.reverse(playlist);
                    publicPlaylistItemAdapter.notifyDataSetChanged();

                    songNameList = new ArrayList<>();
                    for (int i = 0; i < playlist.size(); i++)
                        songNameList.add(playlist.get(i).getName());

                } else {
//                    if (admin_id != SharedPrefManager.getInstance(getApplicationContext()).getSharedPrefInt(AppConstants.INTENT_USER_ID)) {
//                        more_image.setVisibility(View.VISIBLE);
//                    } else {
//                        more_image.setVisibility(View.GONE);
//                    }
                    playlistRv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<PlaylistDetailModel>> call, Throwable t) {
                more_image.setVisibility(View.GONE);
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    // dialog to search collabs
    RecyclerView userSearchRv;
    UserSearchAdapter userSearchAdapter;
    ArrayList<UserSearchModel> usersList;
    ImageView placeholerIv;
    EditText searchEdt;
    ProgressBar searchCallabProgess;


    private void openDailogSearchCollab() {

        BottomSheetDialog bottomSheet = new BottomSheetDialog(this);
        View bottom_sheet = getLayoutInflater().inflate(R.layout.bottomsheet_user_search, null);
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

        searchEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                callUserSearchAPI(searchEdt.getText().toString());

                return false;
            }
        });

        usersList = new ArrayList<>();
        userSearchRv.setLayoutManager(new LinearLayoutManager(PlaylistDetailActivity.this));

        userSearchAdapter = new UserSearchAdapter(PlaylistDetailActivity.this, usersList, id, true);
        userSearchAdapter.setCustomItemClickListener(new UserSearchAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                if (v.getId() == R.id.add_collab_btn) {
                    //sendCollabRequestNotification(id, usersList.get(position).getId(), AppConstants.COLLAB_REQUEST);
                } else {
                    startActivity(new Intent(PlaylistDetailActivity.this, OtherUserProfileActivity.class)
                            .putExtra(AppConstants.INTENT_SEARCHED_USER_ID, usersList.get(position).getId())
                            .putExtra(AppConstants.INTENT_SEARCHED_USER_NAME, usersList.get(position).getUsername())
                            .putExtra(AppConstants.INTENT_SEARCHED_FULL_NAME, usersList.get(position).getFullname())
                            .putExtra(AppConstants.INTENT_PLAYLIST_ID, id)
                            .putExtra(AppConstants.INTENT_COMING_FROM, PlaylistDetailActivity.class.getName()));
                }
            }
        });
        userSearchRv.setAdapter(userSearchAdapter);

    }

    private void callUserSearchAPI(String searchQuery) {

        searchCallabProgess.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

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

    private void callAvatar(String searchQuery) {

        searchCallabProgess.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

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

    // Unused : commented by swati
    /*private void callAddCollaborateAPI(int playlist_id, int searchedUserId) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(PlaylistDetailActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<APIResponse> callback = dataAPI.addCollab(token, playlist_id, searchedUserId);
        callback.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, retrofit2.Response<APIResponse> response) {
                if (response != null
                        && response.body() != null) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        Toast.makeText(PlaylistDetailActivity.this, "Collab created!", Toast.LENGTH_LONG).show();
                        sendCollabRequestNotification(playlist_id, searchedUserId,AppConstants.COLLAB_REQUEST);
                    }
                    else if(response.body().getStatus().equalsIgnoreCase("failed") && !TextUtils.isEmpty(response.body().getMessage()))
                        Toast.makeText(PlaylistDetailActivity.this,response.body().getMessage(), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(PlaylistDetailActivity.this,"Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                Toast.makeText(PlaylistDetailActivity.this,"Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });

    }*/

    private void sendCollabRequestNotification(int playlistId, int searchedUserId, String notifyType) {

        DataAPI dataAPI = RetrofitAPI.getData();
        String headerToken = AppConstants.TOKEN + SharedPrefManager.getInstance(PlaylistDetailActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        int userId = SharedPrefManager.getInstance(PlaylistDetailActivity.this).getSharedPrefInt(AppConstants.INTENT_USER_ID);

        Call<APIResponse> callback = dataAPI.sendNotification(headerToken, userId, searchedUserId, playlistId, notifyType);
        callback.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, retrofit2.Response<APIResponse> response) {
                if (response != null && response.body() != null) {

                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        Toast.makeText(PlaylistDetailActivity.this, "Notification sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PlaylistDetailActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(PlaylistDetailActivity.this, "Something went wrong 2", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                Log.i("Error: ", "ADD NOTIFICATION " + t.getMessage());
            }
        });
    }

    private void sendRealTimetNotification(int playlistId, String notifyType, DatabaseReference myRef, RealTimeModel realTimeModel, String sessionKey) {

        showProgressDialog();
        DataAPI dataAPI = RetrofitAPI.getData();
        String headerToken = AppConstants.TOKEN + SharedPrefManager.getInstance(PlaylistDetailActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        int userId = SharedPrefManager.getInstance(PlaylistDetailActivity.this).getSharedPrefInt(AppConstants.INTENT_USER_ID);

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
                                Toast.makeText(PlaylistDetailActivity.this, "Success", Toast.LENGTH_LONG).show();
                                //startActivity(new Intent(PlaylistDetailActivity.this, RealTimeYoutubePlayler.class)
                                startActivity(new Intent(PlaylistDetailActivity.this, RealTimePlayer.class).putExtra(AppConstants.INTENT_SESSION_KEY, sessionKey).putExtra(AppConstants.INTENT_USER_KEY, "").putExtra(AppConstants.INTENT_PLAYLIST_ID, id).putExtra("admin_id", admin_id));
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

    // Calling to do like/unlike a playlist
    private void putPublicPLaylistLike(int playlistId) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(PlaylistDetailActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<PlaylistLikeModel> callback = dataAPI.putPlaylistLike(token, SharedPrefManager.getInstance(PlaylistDetailActivity.this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN), playlistId);
        callback.enqueue(new Callback<PlaylistLikeModel>() {
            @Override
            public void onResponse(Call<PlaylistLikeModel> call, retrofit2.Response<PlaylistLikeModel> response) {
                if (response != null && response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    if (response.body().getLikeCount() > 0) {
                        likeBtn.setLiked(true);
                        //likeBtn.setColorFilter(ContextCompat.getColor(PlaylistDetailActivity.this, R.color.gph_white));
                        myPlaylistModel.setLikes(myPlaylistModel.getLikes() + 1);
                        likeCountTxt.setText(Utility.prettyCount(myPlaylistModel.getLikes()));
                    } else {
                        likeBtn.setLiked(false);
                        //likeBtn.setColorFilter(ContextCompat.getColor(PlaylistDetailActivity.this, R.color.light_gray));
                        myPlaylistModel.setLikes(myPlaylistModel.getLikes() - 1);

                        if (myPlaylistModel.getLikes() > 0)
                            likeCountTxt.setText(Utility.prettyCount(myPlaylistModel.getLikes()));
                        else
                            likeCountTxt.setText("");
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<PlaylistLikeModel> call, Throwable t) {

            }
        });
    }

    // Creating Session
    private void callAddRealTimeInfo(String sessionKey, String sessionToken, String userIds, String userKeys, DatabaseReference myRef, RealTimeModel realTimeModel) {
        showProgressDialog();

        DataAPI dataAPI = RetrofitAPI.getData();
        String headerToken = AppConstants.TOKEN + SharedPrefManager.getInstance(PlaylistDetailActivity.this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        int userId = SharedPrefManager.getInstance(PlaylistDetailActivity.this).getSharedPrefInt(AppConstants.INTENT_USER_ID);

        Call<APIResponse> callback = dataAPI.addRealTimeInfo(headerToken, id, userId, sessionKey, sessionToken, userIds, userKeys);
        callback.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, retrofit2.Response<APIResponse> response) {
                hideProgressDialog();

                if (response != null && response.body() != null) {
                    Logging.d("Add Info Res-->" + new Gson().toJson(response.body()));

                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        // Sending Notification to collaborators
                        sendRealTimetNotification(id, AppConstants.REAL_TIME_INVITE, myRef, realTimeModel, sessionKey);
                    } else if (response.body().getStatus().equalsIgnoreCase("already_exist") && !TextUtils.isEmpty(response.body().getMessage())) {
                        callToDeleteSession(response.body().getSessionKey());

                        // showErrorDialog(response.body().getMessage(), sessionKey);
                    } else
                        Toast.makeText(PlaylistDetailActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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


    // It will appear while user wants to recreate a session for realtime
    private void showErrorDialog(String message, final String sessionKey) {
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.INTENT_TITLE, "Error!");
        bundle.putString(AppConstants.INTENT_MESSAGE, message);
        bundle.putString(AppConstants.INTENT_BUTTON_NAME, getResources().getString(R.string.delete_session));
        bundle.putBoolean(AppConstants.INTENT_CANCELABLE, true);


        ErrorDailogFragment errorDailogFragment = new ErrorDailogFragment();
        errorDailogFragment.setButtonListener(new ErrorDailogFragment.ButtonListener() {
            @Override
            public void onErrorDialogButtonClick(View view) {
                callToDeleteSession(sessionKey);
            }
        });
        errorDailogFragment.setArguments(bundle);
        errorDailogFragment.setCancelable(false);
        errorDailogFragment.show(getSupportFragmentManager(), "RealTimeErrorDialog");

    }

    // API call to delete session
    private void callToDeleteSession(String sessionKey) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(this).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<APIResponse> callback = dataAPI.deleteRealTimeSession(token, sessionKey);
        callback.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, retrofit2.Response<APIResponse> response) {
                if (response != null && response.body() != null) {
                    Logging.d("DeleteSession Res-->" + new Gson().toJson(response.body()));
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        sendRealTimetNotification(id, AppConstants.REAL_TIME_INVITE, myRef, realTimeModel, sessionKey);
//                        try {
//                            realTimeModel.getSessions().remove(sessionKey);
//
//                            if (viewcollabList != null && viewcollabList.size() > 0) {
//
//                                int i = 0;
//
//                                while (i < viewcollabList.size()) {
//                                    String userKey = AppConstants.USER_CHILD + SharedPrefManager.getInstance(PlaylistDetailActivity.this).getSharedPrefInt(AppConstants.INTENT_USER_ID) + "_" + viewcollabList.get(i).getId();
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

                        // Toast.makeText(PlaylistDetailActivity.this, "Hurray! You con go with RealTime Player", Toast.LENGTH_LONG).show();
                    } else if (response.body().getStatus().equalsIgnoreCase("failed") && !TextUtils.isEmpty(response.body().getMessage()))
                        Log.i(TAG, "Session Ending Failed");
                    else
                        Log.i(TAG, "Session Ending Error");
                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                Toast.makeText(PlaylistDetailActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });

    }


}