package com.shorincity.vibin.music_sharing.activity;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.adapters.SharePlaylistViewPagerAdapter;
import com.shorincity.vibin.music_sharing.databinding.ActivitySharePlaylistBinding;
import com.shorincity.vibin.music_sharing.fragment.SimpleScannerFragment;
import com.shorincity.vibin.music_sharing.model.MyPlaylistModel;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;

import java.util.ArrayList;

public class SharePlaylistActivity extends AppCompatActivity {
    private ActivitySharePlaylistBinding binding;
    private MyPlaylistModel viewModel;
    private SharePlaylistViewPagerAdapter adapter;
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_share_playlist);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
        getIntentExtra();
        statusBarColorChange();
        initControls();

    }

    private void getIntentExtra() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(AppConstants.INTENT_PLAYLIST))
                viewModel = (MyPlaylistModel) intent.getSerializableExtra(AppConstants.INTENT_PLAYLIST);
        }
    }

    private void statusBarColorChange() {
        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }
    }

    private void initControls() {
        ArrayList<String> titleList = new ArrayList<>();
        titleList.add("QR code");
        titleList.add("Scanner");
        adapter = new SharePlaylistViewPagerAdapter(getFragmentManager(), titleList, viewModel);
        binding.viewPager.setAdapter(adapter);
        binding.dotsIndicator.setViewPager(binding.viewPager);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    binding.tvTitle.setTextColor(ContextCompat.getColor(SharePlaylistActivity.this, R.color.white));
                    binding.tvTitle.setText("Scanner");
                    binding.ivBack.setImageDrawable(ContextCompat.getDrawable(SharePlaylistActivity.this, R.drawable.ic_back_white));
                } else {
                    binding.tvTitle.setTextColor(ContextCompat.getColor(SharePlaylistActivity.this, R.color.textColor));
                    binding.tvTitle.setText("QR code");
                    binding.ivBack.setImageDrawable(ContextCompat.getDrawable(SharePlaylistActivity.this, R.drawable.ic_back_grey));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        binding.ivBack.setOnClickListener(v -> finish());
    }


    public void openScanner() {
        binding.viewPager.setCurrentItem(1, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logging.d("==> onPause called");
        if (adapter != null) {
            Fragment scannerFrg = adapter.getItem(1);
            if (scannerFrg instanceof SimpleScannerFragment) {
                ((SimpleScannerFragment) scannerFrg).stopCamera();
            }
        }
    }
}
