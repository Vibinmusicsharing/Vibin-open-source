package com.shorincity.vibin.music_sharing.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.utils.Utility;

public class AboutUsActivity extends AppCompatActivity {

    ImageView img_facebook, img_instagram, img_twitter, img_linkedin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        img_facebook = findViewById(R.id.img_facebook);
        img_instagram = findViewById(R.id.img_instagram);
        img_twitter = findViewById(R.id.img_twitter);
        img_linkedin = findViewById(R.id.img_linkedin);



        TextView versionTv = (TextView) findViewById(R.id.tv_version);
        String versionName = "";

        try {
            versionName = getApplicationContext().getString(R.string.version) + Utility.getVersionName(getApplicationContext());

        } catch (Exception e) {

        } finally {
            versionTv.setText(versionName);
        }

        img_instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("https://instagram.com/vibin_sharing?igshid=npmvbss2mj86");
                Intent i= new Intent(Intent.ACTION_VIEW,uri);

                i.setPackage("com.instagram.android");

                try {
                    startActivity(i);
                } catch (ActivityNotFoundException e) {

                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://instagram.com/vibin_sharing?igshid=npmvbss2mj86")));
                }
            }
        });

        img_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("https://www.facebook.com/vibinmusicsharing/");
                Intent i= new Intent(Intent.ACTION_VIEW,uri);

                i.setPackage("com.facebook.katana");

                try {
                    startActivity(i);
                } catch (ActivityNotFoundException e) {

                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.facebook.com/vibinmusicsharing/")));
                }
            }
        });

        img_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("https://twitter.com/vibin_music");
                Intent i= new Intent(Intent.ACTION_VIEW,uri);

                i.setPackage("com.twitter.android");

                try {
                    startActivity(i);
                } catch (ActivityNotFoundException e) {

                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://twitter.com/vibin_music")));
                }
            }
        });

        img_linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("https://www.linkedin.com/company/35884186/admin/");
                Intent i= new Intent(Intent.ACTION_VIEW,uri);

                i.setPackage("com.linkedin.android");

                try {
                    startActivity(i);
                } catch (ActivityNotFoundException e) {

                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.linkedin.com/company/35884186/admin/")));
                }
            }
        });


    }
}