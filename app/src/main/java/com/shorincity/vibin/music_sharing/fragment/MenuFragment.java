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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.LoginAct;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.UI.youtube;
import com.shorincity.vibin.music_sharing.activity.AboutUsActivity;
import com.shorincity.vibin.music_sharing.activity.EditProfileActivity;
import com.shorincity.vibin.music_sharing.activity.LoginSignUpActivity;
import com.shorincity.vibin.music_sharing.activity.PrivacyPolicyActivity;
import com.shorincity.vibin.music_sharing.adapters.MenuListAdapter;
import com.shorincity.vibin.music_sharing.adapters.MenuSettingAdapter;
import com.shorincity.vibin.music_sharing.model.LogoutModel;
import com.shorincity.vibin.music_sharing.model.MenuActionItem;
import com.shorincity.vibin.music_sharing.model.MenuSettingModel;
import com.shorincity.vibin.music_sharing.model.UpdatePreferPlatformModel;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Utility;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuFragment extends MyBaseFragment implements View.OnClickListener {


    private View view;
    private ArrayList<MenuSettingModel> list;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu, container);

        setMenuList();
        RecyclerView rvMenu = view.findViewById(R.id.rvMenu);
        rvMenu.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rvMenu.setAdapter(new MenuSettingAdapter(list, position -> {
            if (position != RecyclerView.NO_POSITION) {
                MenuSettingModel mBean = list.get(position);

                switch (mBean.getType()) {
                    case 0: {
                        startActivity(new Intent(view.getContext(), EditProfileActivity.class));
                        break;
                    }
                    case 1: {
                        startActivity(new Intent(getActivity(), AboutUsActivity.class));
                        break;
                    }
                    case 2: {
                        break;
                    }
                    case 3: {
                        startActivity(new Intent(getActivity(), PrivacyPolicyActivity.class));
                        break;
                    }
                    case 4: {
                        Utility.shareMyApp(getActivity());
                        break;
                    }
                    case 5: {
                        Utility.emailUs(getActivity());
                        break;
                    }
                }
            }
        }));


        // Report Us
        ((Button) view.findViewById(R.id.btn_flag)).setOnClickListener(this);

        // Logout
        LinearLayout logoutBtn = view.findViewById(R.id.btn_logout);
        logoutBtn.setOnClickListener(this);
        AppCompatImageView ivClose = view.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(this);

        return view;
    }

    private void setMenuList() {
        list = new ArrayList<>();
        list.add(new MenuSettingModel(getString(R.string.lbl_edit_profile),
                getString(R.string.lbl_edit_profile_sub),
                R.drawable.ic_edit_profile, 0));
        list.add(new MenuSettingModel(getString(R.string.lbl_about_us),
                getString(R.string.lbl_about_us_sub),
                R.drawable.ic_about_us, 1));
        list.add(new MenuSettingModel(getString(R.string.lbl_block_user),
                getString(R.string.lbl_block_user_sub),
                R.drawable.ic_blocked_user, 2));
        list.add(new MenuSettingModel(getString(R.string.lbl_privacy),
                getString(R.string.lbl_privacy_sub),
                R.drawable.ic_privacy_policy, 3));
        list.add(new MenuSettingModel(getString(R.string.lbl_app_version),
                Utility.getVersionName(getActivity()),
                R.drawable.ic_app_version, 4));
        list.add(new MenuSettingModel(getString(R.string.lbl_report_us),
                getString(R.string.lbl_report_us_sub),
                R.drawable.ic_report_us, 5));
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
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
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
            case R.id.ivClose:
                if (getActivity() instanceof youtube) {
                    ((youtube) getActivity()).mSlidingLayout.closePane();
                }
                break;
        }
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
                    getActivity().startActivity(new Intent(getActivity(), LoginAct.class));
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