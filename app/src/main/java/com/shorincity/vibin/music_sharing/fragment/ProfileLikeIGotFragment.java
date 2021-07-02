package com.shorincity.vibin.music_sharing.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.adapters.UserLikesAdapter;
import com.shorincity.vibin.music_sharing.model.UserLikeList;

import java.util.ArrayList;

public class ProfileLikeIGotFragment extends Fragment {

    public ArrayList<UserLikeList.GotLikes> userLikeIGotLists;
    TextView txt_nolikes;
    RecyclerView rv_likeslisting;
    UserLikesAdapter userLikesAdapter;

    public ProfileLikeIGotFragment(ArrayList<UserLikeList.GotLikes> userLikeIGotLists) {
        this.userLikeIGotLists = userLikeIGotLists;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_like_i_got, container, false);
        initView(view);
        if (userLikeIGotLists.size() > 0) {
            setAdapter();
            txt_nolikes.setVisibility(View.GONE);
            rv_likeslisting.setVisibility(View.VISIBLE);
        } else {
            txt_nolikes.setVisibility(View.VISIBLE);
            rv_likeslisting.setVisibility(View.GONE);
        }


        return view;
    }

    public void setAdapter() {
        rv_likeslisting.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rv_likeslisting.setHasFixedSize(true);
        userLikesAdapter = new UserLikesAdapter(getActivity(), userLikeIGotLists);
        rv_likeslisting.setAdapter(userLikesAdapter);
    }


    public void initView(View view) {
        txt_nolikes = view.findViewById(R.id.txt_nolikes);
        rv_likeslisting = view.findViewById(R.id.rv_likeslisting);
    }
}