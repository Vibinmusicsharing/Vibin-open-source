package com.shorincity.vibin.music_sharing.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shorincity.vibin.music_sharing.model.UserSearchModel;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.UI.youtube;
import com.shorincity.vibin.music_sharing.activity.OtherUserProfileActivity;
import com.shorincity.vibin.music_sharing.adapters.UserSearchAdapter;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSearchFragment extends Fragment {

    View view;
    ImageView placeholerIv;
    RecyclerView userSearchRv;
    UserSearchAdapter userSearchAdapter;
    ArrayList<UserSearchModel> usersList;
    EditText searchEdt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if (view == null) {

            view = inflater.inflate(R.layout.fragment_user_search, container, false);
            userSearchRv = view.findViewById(R.id.user_search_rv);
            placeholerIv = view.findViewById(R.id.placeholder);
            placeholerIv.setVisibility(View.VISIBLE);
            searchEdt = view.findViewById(R.id.edittextSearch);

            //searchEdt.setOnEditorActionListener(editorActionListener);
//            setAdapter();
            textChangeLister();
        }

        return view;
    }

    private void setAdapter() {

        userSearchRv.setLayoutManager(new LinearLayoutManager(getActivity()));

        userSearchAdapter = new UserSearchAdapter(getActivity(), usersList);
        userSearchAdapter.setCustomItemClickListener(new UserSearchAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                startActivity(new Intent(getActivity(), OtherUserProfileActivity.class).putExtra(AppConstants.INTENT_SEARCHED_USER_ID, usersList.get(position).getId()).putExtra(AppConstants.INTENT_SEARCHED_USER_NAME, usersList.get(position).getUsername()).putExtra(AppConstants.INTENT_SEARCHED_FULL_NAME, usersList.get(position).getFullname()));
            }
        });
        userSearchRv.setAdapter(userSearchAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();

        if (getActivity() instanceof youtube) {
            ((youtube) getActivity()).mSlidingLayout.setSlidingEnable(false);
            ((youtube) getActivity()).mSlidingLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        searchEdt.setText("");
        if (getActivity() instanceof youtube) {
            ((youtube) getActivity()).mSlidingLayout.closePane();
        }

    }

    private void textChangeLister() {
        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if(usersList!=null){
//                    usersList.clear();
//                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = searchEdt.getText().toString();
                if (!TextUtils.isEmpty(searchText) && searchText.length() >= 2) {
                    callUserSearchAPI(searchText);
                }
            }
        });
    }

    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (actionId) {
                case EditorInfo.IME_ACTION_SEARCH:
                    String searchText = searchEdt.getText().toString();
                    if (!TextUtils.isEmpty(searchText)) {
                        callUserSearchAPI(searchText);
                    } else {
                        Toast.makeText(getContext(), "Field can't be empty", Toast.LENGTH_LONG).show();
                    }

                    break;
            }
            return false;
        }

    };

    private void callUserSearchAPI(String searchQuery) {
        ((ProgressBar) view.findViewById(R.id.progressbar)).setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<ArrayList<UserSearchModel>> callback = dataAPI.searchUsers(token, searchQuery);
        callback.enqueue(new Callback<ArrayList<UserSearchModel>>() {
            @Override
            public void onResponse(Call<ArrayList<UserSearchModel>> call, Response<ArrayList<UserSearchModel>> response) {
                ((ProgressBar) view.findViewById(R.id.progressbar)).setVisibility(View.GONE);
                usersList = new ArrayList<>();
                if (response != null && response.body() != null && response.body().size() > 0) {
                    Logging.d("TEST", "callUserSearchAPI Called-->" + new Gson().toJson(response.body()));
                    usersList.addAll(0, response.body());
                    if (usersList.size() > 0) {
                        placeholerIv.setVisibility(View.GONE);
                        setAdapter();
                    } else {
                        placeholerIv.setVisibility(View.GONE);
                    }
                } else {
                    Logging.d("TEST", "callUserSearchAPI else Called");
                    placeholerIv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<UserSearchModel>> call, Throwable t) {
                ((ProgressBar) view.findViewById(R.id.progressbar)).setVisibility(View.GONE);
                Logging.d("TEST", "callUserSearchAPI onFailure Called");
            }
        });

    }
}
