package com.shorincity.vibin.music_sharing.utils;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.shorincity.vibin.music_sharing.R;

public class MiniPlayer {
    public static void addMiniPlayer(Context context, RelativeLayout MainLayout) {
        try {
            RelativeLayout relativeLayout = new RelativeLayout(context);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT); // or wrap_content
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

            LinearLayout parent = new LinearLayout(context);
            parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100));
            parent.setOrientation(LinearLayout.HORIZONTAL);

            ImageView iv = new ImageView(context);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(50, 40);
            iv.setLayoutParams(layoutParams1);
            iv.setImageDrawable(context.getResources().getDrawable(R.drawable.app_logo));
            parent.addView(iv);
            parent.setBackgroundColor(context.getResources().getColor(R.color.gph_dark_red));
            relativeLayout.setLayoutParams(layoutParams);
            MainLayout.addView(relativeLayout);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
