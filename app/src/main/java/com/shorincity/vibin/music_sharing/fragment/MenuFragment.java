package com.shorincity.vibin.music_sharing.fragment;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatRadioButton;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.UI.youtube;
import com.shorincity.vibin.music_sharing.activity.AboutUsActivity;
import com.shorincity.vibin.music_sharing.activity.LoginSignUpActivity;
import com.shorincity.vibin.music_sharing.activity.PrivacyPolicyActivity;
import com.shorincity.vibin.music_sharing.adapters.MenuListAdapter;
import com.shorincity.vibin.music_sharing.model.LogoutModel;
import com.shorincity.vibin.music_sharing.model.MenuActionItem;
import com.shorincity.vibin.music_sharing.model.UpdatePreferPlatformModel;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Utility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuFragment extends ListFragment implements MenuListAdapter.SwitchChangeListener, View.OnClickListener {

    AppCompatRadioButton youtubeAppCompatRadioButton;
    AppCompatRadioButton spotifyAppCompatRadioButton;

    MenuListAdapter menuListAdapter;
    View view;
    Call<UpdatePreferPlatformModel> preferPlatformCallBack;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu, container);

        menuListAdapter = new MenuListAdapter(R.layout.row_menu_action_item, getActivity(), MenuActionItem.values());
        menuListAdapter.setSwitchChangeListener(this);
        setListAdapter(menuListAdapter);
        String preferPlatform;
        // Switching Platform from here
        RadioGroup platformRg = (RadioGroup) view.findViewById(R.id.rg_platform);


        preferPlatform = SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_PREFERRED_PLATFORM);

        if (!TextUtils.isEmpty(preferPlatform) && preferPlatform.equalsIgnoreCase(AppConstants.SPOTIFY)) {
            platformRg.check(R.id.rb_spotify);
        } else {
            platformRg.check(R.id.rb_youtube);
        }

        platformRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {

                    case R.id.rb_youtube:

                        if (!getActivity().getClass().getName().equalsIgnoreCase(youtube.class.getName())) {
                            // setting YOUTUBE as preferred platform
                            callUpdatePlatformAPI(AppConstants.YOUTUBE);
                            ((AppCompatRadioButton) view.findViewById(R.id.rb_spotify)).setEnabled(false);
                        }
                        break;
                }
            }

        });

        // Report Us
        ((Button) view.findViewById(R.id.btn_flag)).setOnClickListener(this);

          youtubeAppCompatRadioButton=view.findViewById(R.id.rb_youtube);
          spotifyAppCompatRadioButton=view.findViewById(R.id.rb_spotify);

        // Logout
        Button logoutBtn = (Button) view.findViewById(R.id.btn_logout);
        logoutBtn.setOnClickListener(this);

        // version
        TextView versionTv = (TextView) view.findViewById(R.id.tv_version);
        String versionName = "";

        try {
            versionName = getActivity().getString(R.string.version) + Utility.getVersionName(getActivity());

        } catch (Exception e) {

        } finally {
            versionTv.setText(versionName);
        }

        return view;
    }

    private void inItViews() {
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (position == 0) {// About Us
            startActivity(new Intent(getActivity(), AboutUsActivity.class));
        } else if (position == 1) { // Share APP

            Utility.shareMyApp(getActivity());

        } else if (position == 2) { // Privacy Policy
            startActivity(new Intent(getActivity(), PrivacyPolicyActivity.class));
        }
        //Toast.makeText(getActivity(), "Checked : " + position, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSwitchChanged(CompoundButton compoundButton, boolean b) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_flag:
                // going to gmail
                Utility.emailUs(getActivity());

                break;
            case R.id.btn_logout:
                // logging out from app
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                postLogout();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();

                break;
        }
    }


    public void callUpdatePlatformAPI(String preferPlatform) {
        DataAPI dataAPI = RetrofitAPI.getData();
        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        int userId = SharedPrefManager.getInstance(getActivity()).getSharedPrefInt(AppConstants.INTENT_USER_ID);

        preferPlatformCallBack = dataAPI.callUpdatePreferredPlatform(token, userId, preferPlatform);
        preferPlatformCallBack.enqueue(new Callback<UpdatePreferPlatformModel>() {
            @Override
            public void onResponse(Call<UpdatePreferPlatformModel> call, Response<UpdatePreferPlatformModel> response) {
                ((AppCompatRadioButton) view.findViewById(R.id.rb_youtube)).setEnabled(true);
                ((AppCompatRadioButton) view.findViewById(R.id.rb_spotify)).setEnabled(true);


                if (response != null && response.body() != null) {

                    if (response.body().getUpdateStatus() == true || response.body().getMessage().equalsIgnoreCase(AppConstants.STATUS_UPDATED)) {

                        /*if(preferPlatform.equalsIgnoreCase(AppConstants.YOUTUBE) *//*&& !getActivity().getClass().getName().equalsIgnoreCase(youtube.class.getName())*//*) {
                            SharedPrefManager.getInstance(getActivity()).setSharedPrefString(AppConstants.INTENT_USER_PREFERRED_PLATFORM,AppConstants.YOUTUBE);

                            gotoYoutubeHome();
                        }
                        else if(preferPlatform.equalsIgnoreCase(AppConstants.SPOTIFY) *//*&& !getActivity().getClass().getName().equalsIgnoreCase(spotify.class.getName())*//*) {
                            //Toast.makeText(getActivity(),"Checked : Spotify",Toast.LENGTH_LONG).show();
                            SharedPrefManager.getInstance(getActivity()).setSharedPrefString(AppConstants.INTENT_USER_PREFERRED_PLATFORM,AppConstants.SPOTIFY);
                            gotoSpotifyHome();
                        }*/

                    }
                } else {
                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UpdatePreferPlatformModel> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                ((AppCompatRadioButton) view.findViewById(R.id.rb_youtube)).setEnabled(true);
                ((AppCompatRadioButton) view.findViewById(R.id.rb_spotify)).setEnabled(true);

            }
        });

        if (preferPlatform.equalsIgnoreCase(AppConstants.YOUTUBE) /*&& !getActivity().getClass().getName().equalsIgnoreCase(youtube.class.getName())*/) {
            SharedPrefManager.getInstance(getActivity()).setSharedPrefString(AppConstants.INTENT_USER_PREFERRED_PLATFORM, AppConstants.YOUTUBE);

            gotoYoutubeHome();
        }
    }

    private void gotoYoutubeHome() {
        Intent k = new Intent(getActivity(), youtube.class);
        startActivity(k.putExtra(AppConstants.INTENT_UPDATE_PLATFORM, AppConstants.YOUTUBE));
        getActivity().finish();
    }



    public void postLogout() {
        DataAPI dataAPI = RetrofitAPI.getData();

        String userToken = SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
        String userApiToken = AppConstants.TOKEN + SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<LogoutModel> callback = dataAPI.logout(userApiToken, userToken);
        callback.enqueue(new Callback<LogoutModel>() {
            @Override
            public void onResponse(Call<LogoutModel> call, Response<LogoutModel> response) {
                if (response != null && response.body() != null && response.body().getLoggedOut()) {
                    SharedPrefManager.getInstance(getActivity()).clearAllSharedPref();
                    getActivity().startActivity(new Intent(getActivity(), LoginSignUpActivity.class));
                    getActivity().finishAffinity();
                } else {
                }
            }

            @Override
            public void onFailure(Call<LogoutModel> call, Throwable t) {
            }
        });

    }

}