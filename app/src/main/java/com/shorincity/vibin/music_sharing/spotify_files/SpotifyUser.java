package com.shorincity.vibin.music_sharing.spotify_files;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.fragment.app.Fragment;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

//spotify user fragment
public class SpotifyUser extends Fragment {

    View view;
    Context mContext;

    public SpotifyUser() {
        // Required empty public constructor
    }
    private static final int REQUEST_CODE = 1337;
    private static final String CLIENT_ID = AppConstants.SPOTIFY_KEY;

    private static final String REDIRECT_URI = "http://vibin.in/callback/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /*final AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(getActivity(), REQUEST_CODE, request);*/

        view = inflater.inflate(R.layout.fragment_youtube_user_old, container, false);
        mContext = view.getContext();

        //
        SharedPrefManager sharedPrefManager = new SharedPrefManager(mContext);
        String email = sharedPrefManager.loadEmail();
        TextView textView = view.findViewById(R.id.textviewuser);
        textView.setText(email);
        SlidingUpPanelLayout slidingUpPanelLayout = view.findViewById(R.id.sliding_main);
        Button Log_out = (Button) view.findViewById(R.id.log_out);
        Log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(mContext).logout();

                getActivity().finishAffinity();

            }
        });

        return view;
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity_youtube_2
        // The next 19 lines of the code are what you need to copy & paste! :)
        if (requestCode == REQUEST_CODE) {
            final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(getActivity(), response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        spotifyPlayer.logout();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("this", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            } else {
                final AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
                builder.setScopes(new String[]{"user-read-private", "streaming"});
                AuthenticationRequest request = builder.build();

                AuthenticationClient.openLoginActivity(getActivity(), REQUEST_CODE, request);
            }
        }
    }*/

}
