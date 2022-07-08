package com.shorincity.vibin.music_sharing.activity;

import static com.shorincity.vibin.music_sharing.utils.AppConstants.FOR_TNC;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.databinding.ActivityAboutUsBinding;
import com.shorincity.vibin.music_sharing.utils.Utility;

public class AboutUsActivity extends AppCompatActivity {

    ActivityAboutUsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about_us);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white));

        initListeners();
        setVersionNumber();

    }

    private void setVersionNumber() {
        String versionName = "";
        try {
            versionName = getApplicationContext().getString(R.string.version) + Utility.getVersionName(getApplicationContext());
        } catch (Exception e) {
            binding.tvVersion.setVisibility(View.GONE);
        } finally {
            binding.tvVersion.setText(versionName);
        }
    }

    private void initListeners() {
        binding.tvTerms.setOnClickListener(view -> {
            Intent intent = new Intent(this, PrivacyPolicyActivity.class);
            intent.putExtra(FOR_TNC, true);
            startActivity(intent);
        });

        binding.tvPrivacy.setOnClickListener(view -> {
            Intent intent = new Intent(this, PrivacyPolicyActivity.class);
            intent.putExtra(FOR_TNC, false);
            startActivity(intent);
        });

        binding.tvReport.setOnClickListener(view -> {
            Utility.emailUs(this);
        });

        binding.back.setOnClickListener(view -> {
            onBackPressed();
        });
    }
}