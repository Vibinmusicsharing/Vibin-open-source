package com.shorincity.vibin.music_sharing.UI;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.shorincity.vibin.music_sharing.R;
// first page
public class MainActivity extends AppCompatActivity {

    TextView app_title;
    Button login_btn,signup_btn;
    Animation smallToBig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(SharedPrefManager.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(this,Platform_choose_activity.class));
            finish();
        }else {
            app_title = findViewById(R.id.app_title);
            smallToBig = AnimationUtils.loadAnimation(this, R.anim.smalltobig);
            app_title.setAnimation(smallToBig);

            login_btn = findViewById(R.id.login_main);
            login_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, LoginAct.class));

                }
            });

            signup_btn = findViewById(R.id.signup_main);
            signup_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, SignUpAct.class));

                }
            });
        }

    }
}
