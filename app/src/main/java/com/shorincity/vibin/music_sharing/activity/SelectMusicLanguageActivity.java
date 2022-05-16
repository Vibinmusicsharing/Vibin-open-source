package com.shorincity.vibin.music_sharing.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.adapters.SelectLanguageAdapter;
import com.shorincity.vibin.music_sharing.databinding.ActivitySelectMusicLanguageBinding;
import com.shorincity.vibin.music_sharing.model.MusicLanguageModel;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.shorincity.vibin.music_sharing.viewmodel.SelectMusicLangViewModel;

public class SelectMusicLanguageActivity extends AppCompatActivity {
    private ActivitySelectMusicLanguageBinding binding;
    private SelectMusicLangViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_music_language);
        viewModel = ViewModelProviders.of(this).get(SelectMusicLangViewModel.class);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);

        statusBarColorChange();
        init();
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

    private void init() {

        binding.setIsNextButtonEnable(viewModel.getSelectedCounter() >= 3);

        binding.rvLanguages.setLayoutManager(new GridLayoutManager(this, 3));
        binding.rvLanguages.setAdapter(new SelectLanguageAdapter(viewModel.getList(), position -> {
            if (position != RecyclerView.NO_POSITION) {
                MusicLanguageModel mBean = viewModel.getList().get(position);
                viewModel.setSelectedCounter(!mBean.isSelected());

                mBean.setSelected(!mBean.isSelected());
                if (binding.rvLanguages.getAdapter() != null)
                    binding.rvLanguages.getAdapter().notifyItemChanged(position);
                binding.setIsNextButtonEnable(viewModel.getSelectedCounter() >= 3);

            }
        }));

        viewModel.search.observe(this, s -> {
            viewModel.onSearch();
            if (binding.rvLanguages.getAdapter() != null)
                binding.rvLanguages.getAdapter().notifyDataSetChanged();
        });

        binding.flNext.setOnClickListener(v -> {
            goToNextScreen();
        });
    }

    private void goToNextScreen() {
        String selectedLangs = viewModel.getSelectedItem();
        if (!selectedLangs.contains("|")) {
            Toast.makeText(SelectMusicLanguageActivity.this, "Please select at-least three languages", Toast.LENGTH_LONG).show();
            return;
        }
        Logging.d("==>" + selectedLangs);
        Bundle bundle = getIntent().getBundleExtra(AppConstants.INTENT_USER_DATA_BUNDLE);
        bundle.putString(AppConstants.INTENT_LANGUAGE, selectedLangs);

        startActivity(new Intent(this, SelectMusicGenreActivity.class)
                .putExtra(AppConstants.INTENT_USER_DATA_BUNDLE, bundle)
                .putExtra(AppConstants.PLAYLIST_UID, getIntent().getStringExtra(AppConstants.PLAYLIST_UID)));
    }
}
