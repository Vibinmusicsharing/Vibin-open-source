package com.shorincity.vibin.music_sharing.youtube_files.floating.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.youtube_files.floating.PlayerService;

public class FullscreenWebPlayer extends Activity {

    public static boolean active = false;
    public static Activity fullScreenAct;

    ViewGroup parent;
    WebView player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.active = true;
        fullScreenAct = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_web_player);

        LinearLayout ll = (LinearLayout) findViewById(R.id.layout_fullscreen);
        player = WebPlayer.getPlayer();

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
        );

        parent = (ViewGroup) player.getParent();
        parent.removeView(player);

        ll.addView(player, params);

        WebPlayer.loadScript(JavaScript.playVideoScript());

    }

    @Override
    public void onBackPressed() {
        if (active) {
            ((ViewGroup) player.getParent()).removeView(player);
            parent.addView(player);
            PlayerService.startAgain();
        }
        active = false;
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        if (active) {
            fullScreenAct.onBackPressed();
        }
        active = false;
        super.onPause();
    }
}
