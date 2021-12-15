package com.shorincity.vibin.music_sharing.activity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.gson.Gson;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.callbackclick.PlaylistDetailCallback;
import com.shorincity.vibin.music_sharing.databinding.ActivityRealTimePlayerBinding;
import com.shorincity.vibin.music_sharing.fragment.RealTimeBaseFragment;
import com.shorincity.vibin.music_sharing.fragment.RealTimeListnerFragment;
import com.shorincity.vibin.music_sharing.fragment.RealTimeListnerSongsFragment;
import com.shorincity.vibin.music_sharing.fragment.RealTimePlayerChatFragment;
import com.shorincity.vibin.music_sharing.fragment.RealTimePlayerFragment;
import com.shorincity.vibin.music_sharing.model.realtime.ChatResponse;
import com.shorincity.vibin.music_sharing.model.realtime.RTConnect;
import com.shorincity.vibin.music_sharing.model.realtime.RTJoinUpdate;
import com.shorincity.vibin.music_sharing.model.realtime.RTListner;
import com.shorincity.vibin.music_sharing.model.realtime.RTListnerMain;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.shorincity.vibin.music_sharing.viewmodel.RTPlayerViewModel;
import com.shorincity.vibin.music_sharing.websocket.NaiveSSLContext;
import com.shorincity.vibin.music_sharing.youtube_files.floating.AsyncTask.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.net.ssl.SSLContext;

public class RealTimePlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private WebSocket webSocketClient;
    private ActivityRealTimePlayerBinding binding;
    private String sessionKey;
    private int playlistId;
    private final RTPlayerViewModel viewModel = new RTPlayerViewModel();
    private RTConnect rtConnect;
    private YouTubePlayer player;
    private MyPlayerStateChangeListener playerStateChangeListener;
    private MyPlaybackEventListener playbackEventListener;
    private final int REQUEST_VIDEO = 123;
    private Handler handler, pollUpdateHandler;
    private Runnable my, pollUpdateRunnable;
    private boolean isAdmin = false;
    private int suffleNo;
    private boolean isConnected = true;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_real_time_player);
        statusBarColorChange();
        getIntentData();
        initWebSocket();
        initControls();
    }

    private void getIntentData() {
        playlistId = getIntent().getExtras().getInt(AppConstants.INTENT_PLAYLIST_ID);
        sessionKey = getIntent().getExtras().getString(AppConstants.INTENT_SESSION_KEY);
        isAdmin = getIntent().getBooleanExtra(AppConstants.INTENT_IS_ADMIN, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null)
            webSocketClient.disconnect();

        if (handler != null && my != null)
            handler.removeCallbacks(my);

        if (pollUpdateHandler != null && pollUpdateRunnable != null)
            pollUpdateHandler.removeCallbacks(pollUpdateRunnable);
    }

    private void initWebSocket() {
        try {
            int userId = SharedPrefManager.getInstance(this).getSharedPrefInt(AppConstants.INTENT_USER_ID);
            WebSocketFactory factory = new WebSocketFactory();
            SSLContext context = NaiveSSLContext.getInstance("TLS");
            factory.setSSLContext(context);
            factory.setVerifyHostname(false);
            factory.setServerName("staging.vibin.in");
            String uri = Constants.REAL_TIME_WEB_SOCKET_URL + sessionKey + "/" + userId + "/";
            webSocketClient = factory.createSocket(uri);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        webSocketClient.addListener(new WebSocketAdapter() {
            @Override
            public void onTextMessage(WebSocket websocket, String message) {
                // Received a text message.
                Log.d("LOGTAG", "==>" + message);
                try {

                    JSONObject object = new JSONObject(message);
                    if (object.has("status")) {
                        rtConnect = new Gson().fromJson(message, RTConnect.class);
                    } else if (object.has("type")) {
                        String type = object.getString("type");
                        switch (type) {
                            case "joining_update":
                            case "leaving_update": {
                                RTJoinUpdate rtJoinUpdate = new Gson().fromJson(message, RTJoinUpdate.class);
                                showBadge(binding.navView, R.id.navigation_listener, rtJoinUpdate.getTotalJoined().intValue(),
                                        rtJoinUpdate.getTotalJoined() > 0L);
                                viewModel.addChatResponse(rtJoinUpdate);
                                if (rtJoinUpdate.getUserIdLeft() != null) {
                                    for (int i = 0; i < viewModel.getRtListnersList().size(); i++) {
                                        RTListner mBean = viewModel.getRtListnersList().get(i);
                                        if (mBean.getUserId().equals(rtJoinUpdate.getUserIdLeft())) {
                                            viewModel.getRtListnersList().remove(i);
                                            break;
                                        }
                                    }
                                }

                                int userId = SharedPrefManager
                                        .getInstance(RealTimePlayerActivity.this)
                                        .getSharedPrefInt(AppConstants.INTENT_USER_ID);
                                if (rtJoinUpdate.getUserIdLeft() == userId) {
                                    runOnUiThread(() -> {
                                        alertDialogShow("You are removed by admin.");
                                    });
                                } else {
                                    runOnUiThread(() -> {
                                        Fragment fragment = getFragmentManager().findFragmentById(R.id.flMain);
                                        if (fragment instanceof RealTimeListnerFragment) {
                                            if (type.equalsIgnoreCase("joining_update")) {
                                                callGetListner();
                                            } else
                                                ((RealTimeListnerFragment) fragment).setListnerList(viewModel.getRtListnersList());
                                        }
                                    });
                                }
                                return;
                            }
                            case "broadcast_update_playback": {
                                rtConnect = new Gson().fromJson(message, RTConnect.class);
                                break;
                            }
                            case "send_chat": {
                                ChatResponse chatResponse = new Gson().fromJson(message, ChatResponse.class);
                                viewModel.addChatResponse(chatResponse);
                                runOnUiThread(() -> {
                                    Fragment fragment = getFragmentManager().findFragmentById(R.id.flMain);
                                    if (fragment instanceof RealTimePlayerChatFragment) {
                                        ((RealTimePlayerChatFragment) fragment).notifyItemAdded();
                                    }
                                });
                                return;
                            }
                            case "REPEAT":
                            case "SUFFLE":
                                return;

                            case "ending_session": {
                                runOnUiThread(() -> {
                                    alertDialogShow("Live session has been ended by admin");
                                });
                                return;
                            }
                        }
                    } else if (object.has("update_type") &&
                            object.getString("update_type").equalsIgnoreCase("get_joined_users")) {
                        RTListnerMain rtJoinUpdate = new Gson().fromJson(message, RTListnerMain.class);
                        viewModel.setRtListnersList(rtJoinUpdate.getRtListnerList());

                        runOnUiThread(() -> {
                            Fragment fragment = getFragmentManager().findFragmentById(R.id.flMain);
                            if (fragment instanceof RealTimeListnerFragment) {
                                ((RealTimeListnerFragment) fragment).setListnerList(viewModel.getRtListnersList());
                            }
                        });
                        return;
                    }

                    if (rtConnect != null && rtConnect.getData() != null) {
                        viewModel.setElapsedSongTime(Integer.parseInt(rtConnect.getData().getElapsedSongTime()));
                        if ((rtConnect.getData().getSessionPlayback().equalsIgnoreCase(AppConstants.CHANGED)
                                || rtConnect.getData().getSessionPlayback().equalsIgnoreCase(AppConstants.START))) {
                            setPlayerView(rtConnect.getData().getSongPlaying().intValue());
                        } else if (rtConnect.getData().getSessionPlayback().equalsIgnoreCase(AppConstants.PAUSE)) {
                            player.pause();
                            runOnUiThread(() -> {
                                Fragment fragment = getFragmentManager().findFragmentById(R.id.flMain);
                                if (fragment instanceof RealTimePlayerFragment) {
                                    ((RealTimePlayerFragment) fragment).setPlayPause(false);
                                }
                            });
                        } else if (rtConnect.getData().getSessionPlayback().equalsIgnoreCase(AppConstants.SEEKSONG)) {
                            player.seekToMillis(viewModel.getElapsedSongTime());
                        } else {
                            player.play();
                            runOnUiThread(() -> {
                                Fragment fragment = getFragmentManager().findFragmentById(R.id.flMain);
                                if (fragment instanceof RealTimePlayerFragment) {
                                    ((RealTimePlayerFragment) fragment).setPlayPause(true);
                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        class WebSocketTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    // Connect to the server and perform an opening handshake.
                    // This method blocks until the opening handshake is finished.
                    webSocketClient.connect();

                }  // The certificate of the peer does not match the expected hostname.
                catch (WebSocketException e) {
                    // A violation against the WebSocket protocol was detected
                    // during the opening handshake.
                    isConnected = false;
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                Log.d("Log tag", "onPostExecute");
                if (!isConnected)
                    alertDialogShow("Something went wrong!!");
                else if (isAdmin) {
                    pollUpdateHandler = new Handler();
                    pollUpdateHandler.postDelayed(pollUpdateRunnable, 500);
                }
            }
        }

        new WebSocketTask().execute();

        pollUpdateRunnable = () -> {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("update_type", "poll_update");
                jsonObject.put("elapsed_song_time", String.valueOf(viewModel.getElapsedSongTime()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Logging.d("==>" + jsonObject.toString());
            webSocketClient.sendText(jsonObject.toString());
//            pollUpdateHandler.postDelayed(pollUpdateRunnable, 500);
        };

    }

    private void initControls() {
        playerStateChangeListener = new MyPlayerStateChangeListener();
        playbackEventListener = new MyPlaybackEventListener();
        binding.myYoutube.initialize(AppConstants.YOUTUBE_KEY, this);

        binding.tvEnd.setOnClickListener(v -> {
            if (isAdmin) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("update_type", "end_session");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Logging.d("==>" + jsonObject.toString());
                webSocketClient.sendText(jsonObject.toString());
            }
            webSocketClient.disconnect();
            finish();
        });

        binding.navView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_listener:
                    binding.llMain.setBackgroundColor(ContextCompat.getColor(RealTimePlayerActivity.this, R.color.colorF3));
                    RealTimeListnerFragment listnerFragment = RealTimeListnerFragment.getInstance(isAdmin);
                    setFragment(listnerFragment);
                    listnerFragment.setListnerList(viewModel.getRtListnersList());
                    listnerFragment.setSongName(viewModel.getSongName());
                    return true;

                case R.id.navigation_playlist: {
                    binding.llMain.setBackgroundColor(ContextCompat.getColor(RealTimePlayerActivity.this, R.color.white));
                    if (isAdmin) {
                        RealTimePlayerFragment fragment = RealTimePlayerFragment.getInstance(playlistId);
                        setFragment(fragment);
                        fragment.setSongName(viewModel.getSongName());
                        fragment.setPlaylistList(viewModel.getPlaylist());
                        fragment.setPlayPause(player != null && player.isPlaying());
                    } else {
                        RealTimeListnerSongsFragment fragment = RealTimeListnerSongsFragment.getInstance();
                        setFragment(fragment);
                        fragment.setSongName(viewModel.getSongName());
                        fragment.setPlaylistList(viewModel.getPlaylist());
                    }
                    return true;
                }
                case R.id.navigation_chat:
                    binding.llMain.setBackgroundColor(ContextCompat.getColor(RealTimePlayerActivity.this, R.color.colorF3));
                    RealTimePlayerChatFragment chatFragment = RealTimePlayerChatFragment.getInstance();
                    chatFragment.setSongName(viewModel.getSongName());
                    chatFragment.setChatList(viewModel.getChatList());
                    setFragment(chatFragment);
                    return true;

                default:
                    return false;
            }
        });
        setFragment(isAdmin ? RealTimePlayerFragment.getInstance(playlistId) : RealTimeListnerSongsFragment.getInstance());
        binding.navView.getMenu().getItem(1).setChecked(true);
        if (isAdmin) {
            binding.tvEnd.setText("End");
            binding.navView.getMenu().getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_admin_player_unselected));
        } else {
            binding.tvEnd.setText("Leave");
            binding.navView.getMenu().getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_player_unselected));
        }
        viewModel.getPublicPlaylistDetail(this, String.valueOf(playlistId), new PlaylistDetailCallback() {
            @Override
            public void onResponse() {
                Fragment fragment = getFragmentManager().findFragmentById(R.id.flMain);
                if (fragment instanceof RealTimePlayerFragment) {
                    ((RealTimePlayerFragment) fragment).setPlaylistList(viewModel.getPlaylist());
                } else if (fragment instanceof RealTimeListnerSongsFragment) {
                    ((RealTimeListnerSongsFragment) fragment).setPlaylistList(viewModel.getPlaylist());
                }
            }

            @Override
            public void onError() {

            }
        });

        processSeekBar();
    }

    private void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flMain, fragment, fragment.getClass().getName());
        fragmentTransaction.commit();
    }

    private void statusBarColorChange() {
        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }
    }

    private void showBadge(BottomNavigationView
                                   bottomNavigationView, @IdRes int itemId, int value, boolean isShow) {

        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(itemId);
        badgeDrawable.setNumber(value);
        badgeDrawable.setVisible(isShow);
    }

    private void alertDialogShow(String message) {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getResources().getString(R.string.app_name));
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                });
        alertDialog.show();
    }

    public void callGetListner() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("update_type", "get_joined_users");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logging.d("==>" + jsonObject.toString());
        webSocketClient.sendText(jsonObject.toString());
    }

    public void removeListner(int id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("update_type", "remove_user_session");
            jsonObject.put("remove_user", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logging.d("==>" + jsonObject.toString());
        webSocketClient.sendText(jsonObject.toString());
    }

    public void setCurrentIndex(int index) {
        viewModel.setCurrentIndex(viewModel.getCurrentIndex() + index);
    }

    public void setCurrentPosition(int position) {
        viewModel.setCurrentIndex(position);
    }

    public void callBroadcastUpdate(String status) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("update_type", "broadcast_update_playback");
            jsonObject.put("session_playback", status);
            jsonObject.put("song_playing", viewModel.getCurrentIndex());
            jsonObject.put("elapsed_song_time", String.valueOf(viewModel.getElapsedSongTime()));
            if (rtConnect != null && rtConnect.getData() != null) {
                jsonObject.put("is_repeat", rtConnect.getData().isRepeat());
                jsonObject.put("is_shuffle", rtConnect.getData().isShuffle());
            } else {
                jsonObject.put("is_repeat", false);
                jsonObject.put("is_shuffle", false);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logging.d("==>" + jsonObject.toString());
        webSocketClient.sendText(jsonObject.toString());
    }

    public void callBroadcastUpdate() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("update_type", "broadcast_update_playback");
            jsonObject.put("session_playback", !viewModel.isStart() ? AppConstants.START : (player.isPlaying() ? AppConstants.PAUSE : AppConstants.PLAY));
            jsonObject.put("song_playing", viewModel.getCurrentIndex());
            jsonObject.put("elapsed_song_time", String.valueOf(viewModel.getElapsedSongTime()));
            if (rtConnect != null && rtConnect.getData() != null) {
                jsonObject.put("is_repeat", rtConnect.getData().isRepeat());
                jsonObject.put("is_shuffle", rtConnect.getData().isShuffle());
            } else {
                jsonObject.put("is_repeat", false);
                jsonObject.put("is_shuffle", false);
            }

            if (!viewModel.isStart())
                viewModel.setStart(true);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logging.d("==>" + jsonObject.toString());
        webSocketClient.sendText(jsonObject.toString());
    }

    public void callBroadcastUpdate(String status, boolean isRepeat, boolean isShuffle) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("update_type", "broadcast_update_playback");
            jsonObject.put("session_playback", status);
            jsonObject.put("song_playing", viewModel.getCurrentIndex());
            jsonObject.put("elapsed_song_time", String.valueOf(viewModel.getElapsedSongTime()));

            if (rtConnect != null && rtConnect.getData() != null) {
                if (isRepeat) {
                    jsonObject.put("is_repeat", !rtConnect.getData().isRepeat());
                    jsonObject.put("is_shuffle", false);
                }
                if (isShuffle) {
                    jsonObject.put("is_repeat", false);
                    jsonObject.put("is_shuffle", !rtConnect.getData().isShuffle());
                }
            } else {
                jsonObject.put("is_repeat", false);
                jsonObject.put("is_shuffle", false);
            }
            if (!viewModel.isStart())
                viewModel.setStart(true);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logging.d("==>" + jsonObject.toString());
        webSocketClient.sendText(jsonObject.toString());
    }

    public void sendChatMessage(String message) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("update_type", "send_chat_message");
            jsonObject.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logging.d("==>" + jsonObject.toString());
        webSocketClient.sendText(jsonObject.toString());
    }

    private void processSeekBar() {
        if (handler != null) {
            handler.removeCallbacks(my);
            handler = null;
        }
        handler = new Handler();
        my = new Runnable() {
            @Override
            public void run() {

                handler.postDelayed(this, 1000);

                if (player != null && player.isPlaying()) {
                    viewModel.setLengthms(player.getDurationMillis());
                    float current = player.getCurrentTimeMillis();
                    float wowInt = ((current / viewModel.getLengthms()) * 100);

                    runOnUiThread(() -> {
                        Fragment fragment = getFragmentManager().findFragmentById(R.id.flMain);
                        if (fragment instanceof RealTimePlayerFragment) {
                            ((RealTimePlayerFragment) fragment).setProgress((int) wowInt, (int) current, player.getDurationMillis());
                        }
                    });
                }
            }
        };
        handler.postDelayed(my, 1000);
    }

    public void setElapsedTime(int progress, boolean isSeek) {
        int to;
        if (player != null) {
            viewModel.setLengthms(player.getDurationMillis());
            to = viewModel.getLengthms() * progress / 100;
            viewModel.setElapsedSongTime(to);

            if (isSeek) {
                callBroadcastUpdate(AppConstants.SEEKSONG);
            }
        }
    }

    private void setPlayerView(int songPosition) {
        if (viewModel.getPlaylist() == null || viewModel.getPlaylist().size() == 0)
            return;

        viewModel.setSongName(viewModel.getPlaylist().get(songPosition).getName());
        player.loadVideo(viewModel.getPlaylist().get(songPosition).getTrackId());
        player.seekToMillis(viewModel.getElapsedSongTime());

        runOnUiThread(() -> {
            Fragment fragment = getFragmentManager().findFragmentById(R.id.flMain);
            if (fragment instanceof RealTimeBaseFragment) {
                ((RealTimeBaseFragment) fragment).setSongName(viewModel.getSongName());
            }

            if (fragment instanceof RealTimePlayerFragment) {
                ((RealTimePlayerFragment) fragment).setPlayPause(player.isPlaying());
            }
        });

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer
            youTubePlayer, boolean b) {

        this.player = youTubePlayer;
        youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
        youTubePlayer.setPlaybackEventListener(playbackEventListener);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider
                                                provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, REQUEST_VIDEO);
        } else {
            Toast.makeText(this, "ERROR!", Toast.LENGTH_SHORT).show();
        }
    }

    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {

        @Override
        public void onPlaying() {
            Fragment fragment = getFragmentManager().findFragmentById(R.id.flMain);
            if (fragment instanceof RealTimePlayerFragment) {
                ((RealTimePlayerFragment) fragment).setPlayPause(true);
            }
        }

        @Override
        public void onPaused() {
            Fragment fragment = getFragmentManager().findFragmentById(R.id.flMain);
            if (fragment instanceof RealTimePlayerFragment) {
                ((RealTimePlayerFragment) fragment).setPlayPause(false);
            }
        }

        @Override
        public void onStopped() {
            // Called when playback stops for a reason other than being paused.
//            showMessage("Stopped");
        }

        @Override
        public void onBuffering(boolean b) {
            // Called when buffering starts or ends.
        }

        @Override
        public void onSeekTo(int i) {
            // Called when a jump in playback position occurs, either
            // due to user scrubbing or call to seekRelativeMillis() or seekToMillis()
        }
    }

    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        @Override
        public void onLoading() {
            // Called when the player is loading a video
            // At this point, it's not ready to accept commands affecting playback such as play() or pause()
        }

        @Override
        public void onLoaded(String s) {
            // Called when a video is done loading.
            player.play();
            Fragment fragment = getFragmentManager().findFragmentById(R.id.flMain);
            if (fragment instanceof RealTimePlayerFragment) {
                ((RealTimePlayerFragment) fragment).setPlayPause(true);
            }
            // Playback methods such as play(), pause() or seekToMillis(int) may be called after this callback.
        }

        @Override
        public void onAdStarted() {
            // Called when playback of an advertisement starts.
        }

        @Override
        public void onVideoStarted() {
            player.seekToMillis(viewModel.getElapsedSongTime());
        }

        @Override
        public void onVideoEnded() {

            if (rtConnect != null && rtConnect.getData() != null) {
                viewModel.setElapsedSongTime(0);
                int songPosition;
                if (rtConnect.getData().isRepeat()) {
                    songPosition = rtConnect.getData().getSongPlaying().intValue();
                } else if (rtConnect.getData().isShuffle()) {
                    songPosition = suffleNo(rtConnect.getData().getSongPlaying().intValue());
                } else {
                    songPosition = rtConnect.getData().getSongPlaying().intValue() + 1;
                }
                if (songPosition < viewModel.getPlaylist().size()) {
                    viewModel.setCurrentIndex(songPosition);
                    callBroadcastUpdate(AppConstants.CHANGED);
                } else {
                    binding.tvEnd.callOnClick();
                }
            }
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            // Called when an error occurs.
        }

    }

    public int suffleNo(int currentPos) {
        Random random = new Random();
        suffleNo = random.nextInt(viewModel.getPlaylist().size() - 1);
        if (suffleNo == currentPos)
            suffleNo(currentPos);
        return suffleNo;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_VIDEO) {
            binding.myYoutube.initialize(AppConstants.YOUTUBE_KEY, this);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.flMain);
        if (fragment instanceof RealTimePlayerFragment ||
                fragment instanceof RealTimeListnerSongsFragment) {
            alertDialogShow("Are you sure you want to leave?");
//            super.onBackPressed();
        } else if (isAdmin) {
            binding.navView.getMenu().getItem(1).setChecked(true);
            RealTimePlayerFragment fragmentPlayer = RealTimePlayerFragment.getInstance(playlistId);
            setFragment(fragmentPlayer);
            fragmentPlayer.setPlaylistList(viewModel.getPlaylist());
        } else {
            binding.navView.getMenu().getItem(1).setChecked(true);
            RealTimeListnerSongsFragment fragmentSong = RealTimeListnerSongsFragment.getInstance();
            setFragment(fragmentSong);
            fragmentSong.setPlaylistList(viewModel.getPlaylist());
        }
    }
}
