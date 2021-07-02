package com.shorincity.vibin.music_sharing.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;


import com.shorincity.vibin.music_sharing.BuildConfig;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Utility {

    public static void setViewEnable(View view, Boolean isEnable) {

        Float alphaValue = isEnable?1.0f:0.5f;

        view.setEnabled(isEnable);
        view.setAlpha(alphaValue);
    }

    public static  String getVersionName(Context context)
    {
        String versionName = "";
        try {
            versionName = BuildConfig.VERSION_NAME;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(versionName)) {
            try {
                PackageInfo pInfo =   context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                versionName = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return versionName;
    }

    public static void shareMyApp(Context context)
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    public static void emailUs(Activity context)
    {
                /*ShareCompat.IntentBuilder.from(context)
                        .setType("message/rfc822")
                        .addEmailTo("vibinmusicsharing@gmail.com")
                        .setSubject("test")
                        .setText("testing")
                        //.setHtmlText(body) //If you are using HTML in your body text
                        .setChooserTitle("Report Us")
                        .startChooser();*/

        Intent intent = new Intent (Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"vibinmusicsharing@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Any subject if you want");
        intent.setPackage("com.google.android.gm");
        if (intent.resolveActivity(context.getPackageManager())!=null)
            context.startActivity(intent);
        else
            Toast.makeText(context,"Gmail App is not installed",Toast.LENGTH_SHORT).show();
    }

    public static String prettyCount(Number number) {
        char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }

    public static boolean isWebUrl(String linkTxt)
    {
        return Patterns.WEB_URL.matcher(linkTxt).matches();

    }

    public static String millisIntoHHMMSS(long millis){
        String timeDutation = "00:00:00";
        try {
            timeDutation = String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return timeDutation;
        }
    }

    public static Date stringToDate(String dtStart) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = format.parse(dtStart);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String dateToString(Date date) {
        String dateTime = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        dateTime = dateFormat.format(date);
        System.out.println("Current Date Time : " + dateTime);
        return dateTime;
    }

    public static String convertDuration(long millis) {
        String timeDutation = "00:00";
        try {
            timeDutation = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return timeDutation;
        }
    }
    public static String convertSongDuration(long duration) {
        String out = null;
        long hours=0;
        try {
            hours = (duration / 3600000);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return out;
        }
        long remaining_minutes = (duration - (hours * 3600000)) / 60000;
        String minutes = String.valueOf(remaining_minutes);
        if (minutes.equals(0)) {
            minutes = "00";
        }
        long remaining_seconds = (duration - (hours * 3600000) - (remaining_minutes * 60000));
        String seconds = String.valueOf(remaining_seconds);
        if (seconds.length() < 2) {
            seconds = "00";
        } else {
            seconds = seconds.substring(0, 2);
        }

        if (hours > 0) {
            out = hours + ":" + minutes + ":" + seconds;
        } else {
            out = minutes + ":" + seconds;
        }

        return out;
    }

}
