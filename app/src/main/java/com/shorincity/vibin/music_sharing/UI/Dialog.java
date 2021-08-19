package com.shorincity.vibin.music_sharing.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.shorincity.vibin.music_sharing.R;

// Unused : Commented By Swati
// dialog  activity_youtube_2
public class Dialog extends AppCompatDialogFragment {

    private EditText playlistName,PlaylistPassword;
    private ExampleDialogListener listener;
    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog,null);

        playlistName = view.findViewById(R.id.dialog_playlistname);
        PlaylistPassword = view.findViewById(R.id.dialog_password);

        builder.setView(view)
                .setTitle("login")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    String playlistname = playlistName.getText().toString();
                    String password = PlaylistPassword.getText().toString();
                    listener.addTexts(playlistname,password);
                    }
                });



          return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ExampleDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement the method");
        }
    }

    public interface ExampleDialogListener{
      void addTexts(String playlistname, String password);
    }
}
