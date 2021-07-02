package com.shorincity.vibin.music_sharing.youtube_files.floating.AsyncTask;

/**
 * Created by Nikki Gharde on 20-October-2020.
 * For CropData Technology Pvt. Ltd.
 */

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.shorincity.vibin.music_sharing.youtube_files.PlayYoutubeVideoActivity;
import com.shorincity.vibin.music_sharing.youtube_files.floating.PlayerService;

public class UserPresentBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TEST : ", "UserPresent Act-->" + intent.getAction());
        /*Sent when the user is present after
         * device wakes up (e.g when the keyguard is gone)
         * */
        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {

        }
        /*Device is shutting down. This is broadcast when the device
         * is being shut down (completely turned off, not sleeping)
         * */
        else if (intent.getAction().equals(Intent.ACTION_SHUTDOWN) ||intent.getAction().equals(Intent.ACTION_SCREEN_OFF) ) {
            Log.d("TEST : ", "Running-->" + isServiceRunning(context,PlayerService.class));
            if (isServiceRunning(context,PlayerService.class)) {
                Intent i = new Intent(context, PlayerService.class);
                context.stopService(i);
            }
        }
    }

    private boolean isServiceRunning(Context context, Class<PlayerService> playerServiceClass) {
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (playerServiceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return false;
    }
}