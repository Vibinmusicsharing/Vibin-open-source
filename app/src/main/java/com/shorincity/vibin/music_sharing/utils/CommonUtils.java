package com.shorincity.vibin.music_sharing.utils;

import android.content.Context;

import java.util.ArrayList;

public class CommonUtils {

    public static ArrayList<String> getGenre() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Blues");
        list.add("Classical");
        list.add("Country side");
        list.add("Disco");
        list.add("Hiphop");
        list.add("Jazz");
        list.add("K pop");
        list.add("Metal");
        list.add("Rock");
        list.add("Romantic");
        list.add("Urban");
        list.add("Workout");
        return list;
    }

    public static int dpToPx(int dps, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }

}
