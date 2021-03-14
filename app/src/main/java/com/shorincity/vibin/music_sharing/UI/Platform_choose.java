package com.shorincity.vibin.music_sharing.UI;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.shorincity.vibin.music_sharing.R;
// Unused : Commented by Swati
// platform choose page
public class Platform_choose extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platform_choose);

        final Button youtube = findViewById(R.id.youtube);
        final Button spotify = findViewById(R.id.spotify);
        final Button offline = findViewById(R.id.buttonlogout);

        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Platform_choose.this, com.shorincity.vibin.music_sharing.UI.youtube.class));
            }
        });


        offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Platform_choose.this,account.class);
                startActivity(intent);
            }
        });
    }
}
