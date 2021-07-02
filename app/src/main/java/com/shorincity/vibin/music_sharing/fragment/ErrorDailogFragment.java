package com.shorincity.vibin.music_sharing.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.utils.AppConstants;

public class ErrorDailogFragment extends DialogFragment {
    String errorTitleStr, errorMsgStr, errorBtnNameStr;
    boolean isCancelable;
    TextView titleTv, messageTv;
    Button continueBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_spotify_premium_error,container,false);

        getIntentData();

        titleTv = view.findViewById(R.id.title);
        messageTv = view.findViewById(R.id.message);
        continueBtn = view.findViewById(R.id.continue_btn);

        if(TextUtils.isEmpty(errorTitleStr))
            titleTv.setVisibility(View.GONE);
        else {
            titleTv.setText(errorTitleStr);
        }

        if(TextUtils.isEmpty(errorMsgStr))
            messageTv.setVisibility(View.GONE);
        else {
            messageTv.setText(errorMsgStr);
        }

        if(TextUtils.isEmpty(errorBtnNameStr))
            continueBtn.setText(getResources().getString(R.string.continu));
        else {
            continueBtn.setText(errorBtnNameStr);
        }

        if(isCancelable)
            view.findViewById(R.id.cancel_iv).setVisibility(View.VISIBLE);
        else
            view.findViewById(R.id.cancel_iv).setVisibility(View.GONE);

        view.findViewById(R.id.cancel_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        view.findViewById(R.id.continue_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getButtonListener().onErrorDialogButtonClick(view);
                getDialog().dismiss();
            }
        });

        return view;
    }

    private void getIntentData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            errorTitleStr = bundle.getString(AppConstants.INTENT_TITLE);
            errorMsgStr = bundle.getString(AppConstants.INTENT_MESSAGE);
            errorBtnNameStr = bundle.getString(AppConstants.INTENT_BUTTON_NAME);
            isCancelable = bundle.getBoolean(AppConstants.INTENT_CANCELABLE);
        }
    }

    public ButtonListener getButtonListener() {
        return buttonListener;
    }

    public void setButtonListener(ButtonListener buttonListener) {
        this.buttonListener = buttonListener;
    }

    ButtonListener buttonListener;

    public interface ButtonListener{
        public void onErrorDialogButtonClick(View view);
    }


    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
