package com.shorincity.vibin.music_sharing.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.base.prefs.SharedPrefManager;
import com.shorincity.vibin.music_sharing.adapters.SelectLanguageAdapter;
import com.shorincity.vibin.music_sharing.databinding.ActivityEditProfileBinding;
import com.shorincity.vibin.music_sharing.databinding.BottomsheetEditProfileBinding;
import com.shorincity.vibin.music_sharing.databinding.BottomsheetSelectMusicGenreBinding;
import com.shorincity.vibin.music_sharing.model.APIResponse;
import com.shorincity.vibin.music_sharing.model.AdditionalSignUpModel;
import com.shorincity.vibin.music_sharing.model.CoverAvatarResponse;
import com.shorincity.vibin.music_sharing.model.MusicLanguageModel;
import com.shorincity.vibin.music_sharing.model.PreferredLangGenresModel;
import com.shorincity.vibin.music_sharing.model.Resource;
import com.shorincity.vibin.music_sharing.model.SignUpUserNameCheckModel;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.FileUtils;
import com.shorincity.vibin.music_sharing.utils.GlideApp;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.shorincity.vibin.music_sharing.utils.Utility;
import com.shorincity.vibin.music_sharing.viewmodel.EditProfileViewModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import kotlin.collections.CollectionsKt;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    private EditProfileViewModel viewModel;
    private final Calendar myCalendar = Calendar.getInstance();
    private boolean isUserNameVerified = false;
    private int imagePickerType = 0; // 0:Profile, 1:Cover
    private SharedPrefManager prefManager;
    private String baseUrl = "https://avatars.dicebear.com/api/";
    private String imageExtension = ".png";
    private String backgroundColor = "?background=%23FFFFFF";
    private String avatarType = "";
    private String avatarText = "";
    private BottomSheetDialog bottomSheet;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        if (Build.VERSION.SDK_INT > 29) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        } else {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
        viewModel = ViewModelProviders.of(this).get(EditProfileViewModel.class);
        binding.setLifecycleOwner(this);

        if (Build.VERSION.SDK_INT >= 30) {
            ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (view, windowInsets) -> {
                Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                params.leftMargin = insets.left;
                params.bottomMargin = insets.bottom;
                params.rightMargin = insets.right;

                view.setLayoutParams(params);

                return WindowInsetsCompat.CONSUMED;
            });
        }
        initControls();
    }

    private void initControls() {
        prefManager = SharedPrefManager.getInstance(this);

        GlideApp.with(this)
                .load(prefManager.getSharedPrefString(AppConstants.PREF_USER_AVATAR_PROFILE))
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(binding.userDpIv);

        GlideApp.with(this)
                .load(prefManager.getSharedPrefString(AppConstants.PREF_USER_COVER))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(binding.ivCoverImage);

        binding.etUsername.setText(prefManager.getSharedPrefString(AppConstants.INTENT_USER_NAME));
        binding.etFullName.setText(prefManager.getSharedPrefString(AppConstants.INTENT_FULL_NAME));
        binding.tvDob.setText(prefManager.getSharedPrefString(AppConstants.PREF_USER_DOB));
        binding.tvDob.setFocusable(false);
        binding.tvDob.setClickable(true);
        binding.scRecentSongs.setChecked(prefManager.getSharedPrefBoolean(AppConstants.PREF_SHOW_RECENTLY_SONGS));

        String userGender = prefManager.getSharedPrefString(AppConstants.PREF_USER_GENDER);
        if (userGender.equalsIgnoreCase(AppConstants.MALE)) {
            binding.rbMale.setChecked(true);
        } else if (userGender.equalsIgnoreCase(AppConstants.FEMALE)) {
            binding.rbFemale.setChecked(true);
        } else if (userGender.equalsIgnoreCase(AppConstants.OTHER)) {
            binding.rbOther.setChecked(true);
        }

        callPreferredLangGenres();
        binding.back.setOnClickListener(view -> {
            onBackPressed();
        });
        binding.flMusicGenre.setOnClickListener(view -> {
            openMusicGenre();
        });

        binding.flMusicLang.setOnClickListener(view -> {
            openMusicLanguage();
        });
        binding.tvDob.setOnClickListener(view -> {
            myCalendar.set(Calendar.YEAR, 2000);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view1, year, month, dayOfMonth) -> {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, month);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String myFormat = "dd/MM/yyyy"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                        binding.tvDob.setText(sdf.format(myCalendar.getTime()));
                    }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show();
        });
        binding.btnSave.setOnClickListener(view -> {
            String userName = binding.etUsername.getText().toString();
            String fullName = binding.etFullName.getText().toString();
            int selectedId = binding.rgGender.getCheckedRadioButtonId();
            String gender = "";
            if (selectedId == R.id.rbMale) {
                gender = AppConstants.MALE;
            } else if (selectedId == R.id.rbFemale) {
                gender = AppConstants.FEMALE;
            } else if (selectedId == R.id.rbOther) {
                gender = AppConstants.OTHER;
            }
            String dob = binding.tvDob.getText().toString();

            if (TextUtils.isEmpty(fullName)) {
                Toast.makeText(this, "Please enter Full name", Toast.LENGTH_LONG).show();
                return;
            } else if (TextUtils.isEmpty(userName)) {
                Toast.makeText(this, "Please enter username", Toast.LENGTH_LONG).show();
                return;
            } else if (TextUtils.isEmpty(gender)) {
                Toast.makeText(this, "Please enter gender", Toast.LENGTH_LONG).show();
                return;
            } else if (TextUtils.isEmpty(dob)) {
                Toast.makeText(this, "Please select Date of birth", Toast.LENGTH_LONG).show();
                return;
            } else {
                callEditProfileDetails(dob, gender, userName, fullName, binding.scRecentSongs.isChecked());
            }
        });
        binding.tvChangeProfile.setOnClickListener(view -> {
            openChangeProfile();
        });
        binding.tvChangeCover.setOnClickListener(view -> {
            imagePickerType = 1;
            ImagePicker.Companion.with(this)
                    .galleryOnly()    //User can only select image from Gallery
                    .start();    //Default Request Code is ImagePicker.REQUEST_CODE
        });

        binding.etUsername.addTextChangedListener(new EditTextWatch(this, binding.etUsername));

    }

    private void openMusicLanguage() {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(this);
        BottomsheetSelectMusicGenreBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.bottomsheet_select_music_genre, null, false);
        bottomSheet.setContentView(binding.getRoot());

        binding.rvLanguages.setLayoutManager(new GridLayoutManager(this, 3));
        binding.rvLanguages.setAdapter(new SelectLanguageAdapter(viewModel.getListLang(), position -> {
            if (position != RecyclerView.NO_POSITION) {
                MusicLanguageModel mBean = viewModel.getListLang().get(position);
                viewModel.setLangSelectedCounter(!mBean.isSelected());
                mBean.setSelected(!mBean.isSelected());
                if (binding.rvLanguages.getAdapter() != null)
                    binding.rvLanguages.getAdapter().notifyItemChanged(position);
                binding.setIsNextButtonEnable(viewModel.getLangSelectedCounter() >= 3);

            }
        }));
        binding.ivClose.setOnClickListener(v -> bottomSheet.dismiss());
        binding.btnSave.setOnClickListener(v -> {
            String selectedLangs = viewModel.getSelectedItem(viewModel.getListLang());
            String selectedGenre = viewModel.getSelectedGenres();
            if (!selectedLangs.contains("|")) {
                Toast.makeText(EditProfileActivity.this, "Please select at-least three languages", Toast.LENGTH_LONG).show();
            } else {
                callPostUpdateProfile(selectedGenre, selectedLangs, bottomSheet);
            }
        });
        binding.tvTitle.setText("I prefer");
        binding.tvSubTitle.setText("Select language(minimum 3)");

        binding.setIsNextButtonEnable(viewModel.getLangSelectedCounter() >= 3);

        bottomSheet.show();
    }

    private void openMusicGenre() {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(this);
        BottomsheetSelectMusicGenreBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.bottomsheet_select_music_genre, null, false);
        bottomSheet.setContentView(binding.getRoot());

        binding.rvLanguages.setLayoutManager(new GridLayoutManager(this, 3));
        binding.rvLanguages.setAdapter(new SelectLanguageAdapter(viewModel.getListGenre(), position -> {
            if (position != RecyclerView.NO_POSITION) {
                MusicLanguageModel mBean = viewModel.getListGenre().get(position);
                viewModel.setGenreSelectedCounter(!mBean.isSelected());
                mBean.setSelected(!mBean.isSelected());
                if (binding.rvLanguages.getAdapter() != null)
                    binding.rvLanguages.getAdapter().notifyItemChanged(position);
                binding.setIsNextButtonEnable(viewModel.getGenreSelectedCounter() >= 3);

            }
        }));
        binding.ivClose.setOnClickListener(v -> bottomSheet.dismiss());
        binding.btnSave.setOnClickListener(v -> {
            String selectedGenre = viewModel.getSelectedItem(viewModel.getListGenre());
            String selectedLang = viewModel.getSelectedLangs();
            if (!selectedGenre.contains("|")) {
                Toast.makeText(EditProfileActivity.this, "Please select at-least three genre", Toast.LENGTH_LONG).show();
            } else {
                callPostUpdateProfile(selectedGenre, selectedLang, bottomSheet);
            }
        });
        binding.tvTitle.setText("I like to hear");
        binding.tvSubTitle.setText("Select music genre(minimum 3)");
        binding.setIsNextButtonEnable(viewModel.getGenreSelectedCounter() >= 3);
        bottomSheet.show();

    }

    private void openChangeProfile() {

        bottomSheet = new BottomSheetDialog(this);
        BottomsheetEditProfileBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.bottomsheet_edit_profile, null, false);
        bottomSheet.setContentView(binding.getRoot());
        binding.ivClose.setOnClickListener(v -> bottomSheet.dismiss());
        binding.btnUploadImage.setOnClickListener(v -> {
            imagePickerType = 0;
            ImagePicker.Companion.with(this)
                    .galleryOnly()    //User can only select image from Gallery
                    .start();    //Default Request Code is ImagePicker.REQUEST_CODE
        });
        List<String> avatarTypes = viewModel.getAvatarTypes();
        List<String> namesList = viewModel.getNameList();
        ArrayAdapter<String> avatarAdapter = new ArrayAdapter<>(this, R.layout.item_avatar_spinner, avatarTypes);
        avatarAdapter.setDropDownViewResource(R.layout.item_avatar_spinner);
        binding.spinner.setAdapter(avatarAdapter);
        GlideApp.with(this)
                .load(prefManager.getSharedPrefString(AppConstants.PREF_USER_AVATAR_PROFILE))
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(binding.ivProfile);

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                avatarType = (String) adapterView.getItemAtPosition(i);
                GlideApp.with(EditProfileActivity.this)
                        .load(baseUrl + avatarType.toLowerCase() + "/" + avatarText + imageExtension + backgroundColor)
                        .circleCrop()
                        .into(binding.ivProfile);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.etSearchAvatar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                avatarText = editable.toString();
                GlideApp.with(EditProfileActivity.this)
                        .load(baseUrl + avatarType.toLowerCase() + "/" + avatarText + imageExtension + backgroundColor)
                        .circleCrop()
                        .into(binding.ivProfile);
            }
        });
        binding.ivRandom.setOnClickListener(view -> {
            int avatarTypeIndex = new Random().nextInt(avatarTypes.size() - 1);
            int nameIndex = new Random().nextInt(namesList.size() - 1);
            avatarType = avatarTypes.get(avatarTypeIndex);
            avatarText = namesList.get(nameIndex);

            GlideApp.with(EditProfileActivity.this)
                    .load(baseUrl + avatarType.toLowerCase() + "/" + avatarText + imageExtension + backgroundColor)
                    .circleCrop()
                    .into(binding.ivProfile);
        });

        binding.btnSave.setOnClickListener(view -> {
            final ProgressDialog showMe = new ProgressDialog(EditProfileActivity.this);
            showMe.setMessage("Please wait");
            showMe.setCancelable(true);
            showMe.setCanceledOnTouchOutside(false);
            showMe.show();

            String token = SharedPrefManager.getInstance(this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);

            viewModel.callUpdateProfileOrCover(token, "avatar_link",
                    baseUrl + avatarType.toLowerCase() + "/" + avatarText + imageExtension + backgroundColor, null).observe(this, apiResponseResource -> {
                if (apiResponseResource instanceof Resource.Loading) {
                    showMe.show();
                } else if (apiResponseResource instanceof Resource.Success) {
                    showMe.dismiss();
                    bottomSheet.dismiss();
                    CoverAvatarResponse response = ((Resource.Success<CoverAvatarResponse>) apiResponseResource).getData();
                    prefManager.setSharedPrefString(AppConstants.PREF_USER_AVATAR_PROFILE, response.getImageLink());
                    GlideApp.with(this)
                            .load(response.getImageLink())
                            .circleCrop()
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .into(EditProfileActivity.this.binding.userDpIv);
                } else if (apiResponseResource instanceof Resource.Error) {
                    showMe.dismiss();
                    String errorMsg = ((Resource.Error<CoverAvatarResponse>) apiResponseResource).getErrorMsg();
                    Toast.makeText(EditProfileActivity.this,
                            errorMsg != null ? errorMsg : "Something went wrong!",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });

        bottomSheet.show();
    }

    private void callPostUpdateProfile(String selectedGenre, String selectedLangs, BottomSheetDialog bottomSheet) {
        final ProgressDialog showMe = new ProgressDialog(EditProfileActivity.this);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();

        String token = SharedPrefManager.getInstance(this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);

        viewModel.postUpdateProfile(token, selectedLangs, selectedGenre).observe(this, apiResponseResource -> {
            if (apiResponseResource instanceof Resource.Loading) {
                showMe.show();
            } else if (apiResponseResource instanceof Resource.Success) {
                showMe.dismiss();
                bottomSheet.dismiss();
                viewModel.setSelectedGenres(selectedGenre);
                viewModel.setSelectedLangs(selectedLangs);
                APIResponse data = ((Resource.Success<APIResponse>) apiResponseResource).getData();

            } else if (apiResponseResource instanceof Resource.Error) {
                showMe.dismiss();
                String errorMsg = ((Resource.Error<APIResponse>) apiResponseResource).getErrorMsg();
                Toast.makeText(EditProfileActivity.this,
                        errorMsg != null ? errorMsg : "Something went wrong!",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void callPreferredLangGenres() {
        String token = SharedPrefManager.getInstance(this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
        viewModel.callPreferredLangGenres(token).observe(this, apiResponseResource -> {
            if (apiResponseResource instanceof Resource.Success) {
                PreferredLangGenresModel data = ((Resource.Success<PreferredLangGenresModel>) apiResponseResource).getData();
                for (String langs : data.getLanguages()) {
                    MusicLanguageModel mBean = CollectionsKt.firstOrNull(viewModel.getListLang(), musicLanguageModel -> musicLanguageModel.getName().equalsIgnoreCase(langs));
                    if (mBean != null) {
                        viewModel.setLangSelectedCounter(!mBean.isSelected());
                        mBean.setSelected(true);
                    }
                }
                for (String genres : data.getGenres()) {
                    MusicLanguageModel mBean = CollectionsKt.firstOrNull(viewModel.getListGenre(), musicLanguageModel -> musicLanguageModel.getName().equalsIgnoreCase(genres));
                    if (mBean != null) {
                        viewModel.setGenreSelectedCounter(!mBean.isSelected());
                        mBean.setSelected(true);
                    }
                }
                String selectedGenre = viewModel.getSelectedItem(viewModel.getListGenre());
                String selectedLang = viewModel.getSelectedItem(viewModel.getListLang());
                viewModel.setSelectedLangs(selectedLang);
                viewModel.setSelectedGenres(selectedGenre);

            } else if (apiResponseResource instanceof Resource.Error) {
                String errorMsg = ((Resource.Error<PreferredLangGenresModel>) apiResponseResource).getErrorMsg();
                Toast.makeText(EditProfileActivity.this,
                        errorMsg != null ? errorMsg : "Something went wrong!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class EditTextWatch implements TextWatcher {

        Context context;
        EditText editText;

        private EditTextWatch(Context context, EditText editText) {
            this.context = context;
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {


            switch (editText.getId()) {
                case R.id.etUsername:
                    Logging.d("Lengh--->" + binding.etUsername.getText().toString().trim().length());
                    if (!TextUtils.isEmpty(binding.etUsername.getText()) && binding.etUsername.getText().toString().trim().length() >= 6)
                        callUserNameCheckerAPI(binding.etUsername.getText().toString().trim());
                    else {
                        isUserNameVerified = false;
                        binding.etUsername.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    }
                    break;
                case R.id.cust_full_name:

                    break;
            }

            if (TextUtils.isEmpty(binding.etUsername.getText().toString())
                    || binding.etUsername.getText().toString().trim().length() < 6
                    || !isUserNameVerified) {
                Utility.setViewEnable(binding.btnSave, false);
                Logging.d(" 11-setViewEnable false");
                isUserNameVerified = false;
                binding.etUsername.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            } else {
                Logging.d(" 11-setViewEnable true");
                Utility.setViewEnable(binding.btnSave, true);
            }
        }
    }

    private void callUserNameCheckerAPI(String username) {
        //progressBar.setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getData();

        Call<SignUpUserNameCheckModel> callback = dataAPI.signupUsernameChecker(AppConstants.LOGIN_SIGNUP_HEADER, username);
        callback.enqueue(new Callback<SignUpUserNameCheckModel>() {
            @Override
            public void onResponse(Call<SignUpUserNameCheckModel> call, Response<SignUpUserNameCheckModel> response) {
                //progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    Logging.d(" callUserNameCheckerAPI response-->" + new Gson().toJson(response));
                    Log.i("YOUTUBE_TRENDING RESULT", response.toString());

                    if (response.body() != null) {

                        if (response.body().getStatus().equalsIgnoreCase("checked")) {

                            Logging.d("exist-->" + response.body().getExists());
                            if (response.body().getExists()) {
                                isUserNameVerified = false;
                                binding.etUsername.setError(getResources().getString(R.string.error_on_user_exist));
                            } else {
                                isUserNameVerified = true;

                                binding.etUsername.setError(null);
                                binding.etUsername.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check_mark_small, 0);
                            }


                            if (TextUtils.isEmpty(binding.etUsername.getText().toString())
                                    || !isUserNameVerified || binding.etUsername.getText().toString().trim().length() < 6) {
                                Utility.setViewEnable(binding.btnSave, false);
                                Logging.d(" -setViewEnable false 22");
                                binding.etUsername.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            } else {
                                Utility.setViewEnable(binding.btnSave, true);
                                binding.etUsername.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check_mark_small, 0);
                                Logging.d(" -setViewEnable true 22");
                            }

                        } else {
                            Logging.d(" callUserNameCheckerAPI unchecked res");
                        }

                    } else {
                        Logging.d(" callUserNameCheckerAPI null resp");
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SignUpUserNameCheckModel> call, Throwable t) {
                //progressBar.setVisibility(View.GONE);
                Logging.d(" callUserNameCheckerAPI onFailure");
                Toast.makeText(EditProfileActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void callEditProfileDetails(String dobUser, String gender,
                                        String username, String fullname, Boolean isRecentSongs) {
        final ProgressDialog showMe = new ProgressDialog(EditProfileActivity.this);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();

        String token = SharedPrefManager.getInstance(this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);

        viewModel.callEditProfileDetails(token, dobUser, gender, username, fullname, isRecentSongs).observe(this, apiResponseResource -> {
            if (apiResponseResource instanceof Resource.Loading) {
                showMe.show();
            } else if (apiResponseResource instanceof Resource.Success) {
                showMe.dismiss();
                AdditionalSignUpModel data = ((Resource.Success<AdditionalSignUpModel>) apiResponseResource).getData();
                viewModel.setPrefData(SharedPrefManager.getInstance(EditProfileActivity.this), data);
                finish();
            } else if (apiResponseResource instanceof Resource.Error) {
                showMe.dismiss();
                String errorMsg = ((Resource.Error<AdditionalSignUpModel>) apiResponseResource).getErrorMsg();
                Toast.makeText(EditProfileActivity.this,
                        errorMsg != null ? errorMsg : "Something went wrong!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                if (data != null) {
                    final ProgressDialog showMe = new ProgressDialog(EditProfileActivity.this);
                    showMe.setMessage("Please wait");
                    showMe.setCancelable(true);
                    showMe.setCanceledOnTouchOutside(false);
                    showMe.show();

                    String token = SharedPrefManager.getInstance(this).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);

                    Uri uri = data.getData();
                    File file = FileUtils.getFile(this, uri);
                    MultipartBody.Part body = null;
                    String paramName = "";
                    String imageType = "";
                    if (imagePickerType == 0) {
                        paramName = "avatar_image";
                        imageType = "avatar";
                    } else if (imagePickerType == 1) {
                        paramName = "cover_image";
                        imageType = "cover";
                    }

                    if (!TextUtils.isEmpty(paramName)) {
                        // create RequestBody instance from file
                        RequestBody requestFile =
                                RequestBody.create(
                                        MediaType.parse("multipart/form-data"),
                                        file
                                );

                        // MultipartBody.Part is used to send also the actual file name
                        body = MultipartBody.Part.createFormData(paramName,
                                file.getName(), requestFile);
                        viewModel.callUpdateProfileOrCover(token, imageType,
                                null, body).observe(this, apiResponseResource -> {
                            if (apiResponseResource instanceof Resource.Loading) {
                                showMe.show();
                            } else if (apiResponseResource instanceof Resource.Success) {
                                showMe.dismiss();
                                CoverAvatarResponse response = ((Resource.Success<CoverAvatarResponse>) apiResponseResource).getData();
                                if (imagePickerType == 1) {
                                    prefManager.setSharedPrefString(AppConstants.PREF_USER_COVER, response.getImageLink());
                                    GlideApp.with(this)
                                            .load(response.getImageLink())
                                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                            .into(binding.ivCoverImage);
//                                    finish();
                                } else {
                                    if (bottomSheet != null && bottomSheet.isShowing())
                                        bottomSheet.dismiss();
                                    prefManager.setSharedPrefString(AppConstants.PREF_USER_AVATAR_PROFILE, response.getImageLink());
                                    GlideApp.with(this)
                                            .load(response.getImageLink())
                                            .circleCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                            .into(binding.userDpIv);
                                }
                            } else if (apiResponseResource instanceof Resource.Error) {
                                showMe.dismiss();
                                String errorMsg = ((Resource.Error<CoverAvatarResponse>) apiResponseResource).getErrorMsg();
                                Toast.makeText(EditProfileActivity.this,
                                        errorMsg != null ? errorMsg : "Something went wrong!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
