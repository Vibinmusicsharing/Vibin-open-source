package com.shorincity.vibin.music_sharing.UI;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.base.prefs.SharedPrefManager;

// Unused : Commented By Swati
public class account extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        SharedPrefManager sharedPrefManager = new SharedPrefManager(account.this);
        String email = sharedPrefManager.loadEmail();
        TextView textView = findViewById(R.id.textviewuser);
        textView.setText(email);

        Button Log_out = (Button) findViewById(R.id.log_out);
        Log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(account.this).logout();
                finishAffinity();
            }
        });

    }
}
