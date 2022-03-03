package com.shorincity.vibin.music_sharing.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.zxing.Result;
import com.shorincity.vibin.music_sharing.activity.SplashActivity;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class SimpleScannerFragment extends Fragment implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    public static SimpleScannerFragment getInstance() {
        return new SimpleScannerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mScannerView = new ZXingScannerView(getActivity());
        return mScannerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        /*Toast.makeText(getActivity(), "Contents = " + rawResult.getText() +
                ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();*/
        if (!TextUtils.isEmpty(rawResult.getText())) {

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(rawResult.getText()));
            FirebaseDynamicLinks.getInstance().getDynamicLink(intent).addOnSuccessListener(getActivity(), pendingDynamicLinkData -> {
                Uri deepLink = null;
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.getLink();
                }
                try {
                    if (deepLink != null) {
                        String uID = deepLink.getQueryParameter("id");
                        Logging.d("==> link id" + uID);
                        Intent intent1 = new Intent();
                        intent1.putExtra(AppConstants.PLAYLIST_UID, uID);
                        getActivity().setResult(Activity.RESULT_OK, intent1);
                        getActivity().finish();
                    }
                } catch (Exception e) {

                }
            }).addOnFailureListener(getActivity(), e -> Log.w("TAG", "getDynamicLink:onFailure", e));
        } else {
            startCameraPreview();
        }
    }

    private void startCameraPreview() {
        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(SimpleScannerFragment.this);
            }
        }, 2000);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopCamera();
    }

    public void stopCamera() {
        if (mScannerView != null)
            mScannerView.stopCamera();
    }
}