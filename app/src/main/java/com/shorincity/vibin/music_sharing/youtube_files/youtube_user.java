package com.shorincity.vibin.music_sharing.youtube_files;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;


// yotube user

public class youtube_user extends Fragment {
    View view;
    Context context;

    public youtube_user() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_youtube_user_old, container, false);
        context = view.getContext();
        SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
        String email = sharedPrefManager.loadEmail();
        TextView textView = view.findViewById(R.id.textviewuser);
        SlidingUpPanelLayout slidingUpPanelLayout = view.findViewById(R.id.sliding_main);
        textView.setText(email);


        Button Log_out = (Button) view.findViewById(R.id.log_out);
        Log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(context).logout();

                getActivity().finishAffinity();

            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
       /* ((youtube)getActivity()).mSlidingLayout.setSlidingEnable(false);
        ((youtube)getActivity()).mSlidingLayout.setSliderFadeColor(getActivity().getResources().getColor(android.R.color.transparent));
*/
    }
}
