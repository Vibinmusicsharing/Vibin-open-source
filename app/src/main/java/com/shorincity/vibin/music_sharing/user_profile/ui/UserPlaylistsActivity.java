package com.shorincity.vibin.music_sharing.user_profile.ui;

import static com.shorincity.vibin.music_sharing.utils.AppConstants.USER_ID;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.databinding.ActivityUserPlaylistsBinding;

public class UserPlaylistsActivity extends AppCompatActivity {

    private ActivityUserPlaylistsBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_playlists);

        UserPlaylistFragment fragment = UserPlaylistFragment.getNewInstance(getIntent().getIntExtra(USER_ID, -1));
        openFragment(fragment, false);
    }

    public void openFragment(Fragment fragment, Boolean addToBackStack) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if(addToBackStack) {
            transaction.addToBackStack(fragment.getClass().getName());
        }
        transaction.replace(R.id.fcv_container, fragment, fragment.getClass().getName());
        transaction.commitAllowingStateLoss();
    }
}
