package com.shorincity.vibin.music_sharing.fragment;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.activity.SharePlaylistActivity;
import com.shorincity.vibin.music_sharing.databinding.FragmentSharePlaylistQrcodeBinding;
import com.shorincity.vibin.music_sharing.model.MyPlaylistModel;
import com.shorincity.vibin.music_sharing.model.shareplaylist.SharePlaylistResponse;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;

import retrofit2.Call;
import retrofit2.Callback;

public class SharePlaylistQrCodeFragment extends MyBaseFragment {
    private FragmentSharePlaylistQrcodeBinding binding;
    private MyPlaylistModel myPlaylistModel;
    private SharePlaylistResponse sharePlaylistResponse;

    public static SharePlaylistQrCodeFragment getInstance(MyPlaylistModel myPlaylistModel) {
        SharePlaylistQrCodeFragment fragment = new SharePlaylistQrCodeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConstants.INTENT_PLAYLIST, myPlaylistModel);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_share_playlist_qrcode, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myPlaylistModel = (MyPlaylistModel) getArguments().getSerializable(AppConstants.INTENT_PLAYLIST);

        int userId = SharedPrefManager.getInstance(binding.getRoot().getContext()).getSharedPrefInt(AppConstants.INTENT_USER_ID);
        binding.setIsAdmin(myPlaylistModel.getAdmin_id() == userId);
        initControls();
    }

    private void initControls() {
        if (myPlaylistModel != null) {
            binding.tvPlaylistName.setText(myPlaylistModel.getName());
            if (TextUtils.isEmpty(myPlaylistModel.getAdminName())) {
                binding.llArtistName.setVisibility(View.INVISIBLE);
            } else {
                binding.llArtistName.setVisibility(View.VISIBLE);
                binding.tvArtist.setText(myPlaylistModel.getAdminName());
            }
        }
        binding.ivScan.setOnClickListener(v -> ((SharePlaylistActivity) getActivity()).openScanner());

        binding.ivShare.setOnClickListener(v -> {
            if (sharePlaylistResponse != null) {
                String msg = sharePlaylistResponse.getData().getFullUrl() + "\n\nHi, Tap on above link and join my playlist \"" + myPlaylistModel.getName() + "\" to share your favorite track.";
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, msg);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch (Exception e) {
                    //e.toString();
                }
            }
        });

        binding.llLink.setOnClickListener(v -> {
            if (sharePlaylistResponse != null) {
                ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Playlist Link", sharePlaylistResponse.getData().getFullUrl() + "");
                clipboard.setPrimaryClip(clip);
                Toast.makeText(v.getContext(),
                        "Link copied",
                        Toast.LENGTH_LONG).show();
            }
        });

        binding.tvReset.setOnClickListener(v -> {
            callSharePlaylistApi(true);
        });
        callSharePlaylistApi(false);
    }

    private void setData(SharePlaylistResponse response) {
        if (binding != null) {
            sharePlaylistResponse = response;
            String encodedImage = sharePlaylistResponse.getData().getQrCode();
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            binding.ivQrCode.setImageBitmap(decodedByte);
            binding.tvLink.setText(sharePlaylistResponse.getData().getFullUrl());
        }
    }

    private void callSharePlaylistApi(boolean regenerate) {
        ProgressDialog showMe = new ProgressDialog(binding.getRoot().getContext());
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();

        DataAPI dataAPI = RetrofitAPI.getData();
        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(binding.getRoot().getContext()).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        Call<SharePlaylistResponse> callback = dataAPI.callSharePlaylistApi(token,
                SharedPrefManager.getInstance(binding.getRoot().getContext()).getSharedPrefString(AppConstants.INTENT_USER_TOKEN),
                myPlaylistModel.getId(), regenerate);
        callback.enqueue(new Callback<SharePlaylistResponse>() {
            @Override
            public void onResponse(Call<SharePlaylistResponse> call, retrofit2.Response<SharePlaylistResponse> response) {
                showMe.dismiss();
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    setData(response.body());

                } else {
                    Toast.makeText(binding.getRoot().getContext(),
                            (response.body() != null && response.body().getMessage() != null) ? response.body().getMessage() : "Something went wrong!",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SharePlaylistResponse> call, Throwable t) {
                showMe.dismiss();
                Toast.makeText(binding.getRoot().getContext(),
                        t.getLocalizedMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

}