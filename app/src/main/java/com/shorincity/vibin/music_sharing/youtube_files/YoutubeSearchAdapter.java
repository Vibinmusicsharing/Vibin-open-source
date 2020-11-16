package com.shorincity.vibin.music_sharing.youtube_files;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.shorincity.vibin.music_sharing.model.Item;

import com.shorincity.vibin.music_sharing.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

// Unused yourube list view adapter
public class YoutubeSearchAdapter extends ArrayAdapter<Item> {
    public YoutubeSearchAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Item> objects) {
        super(context, resource, objects);

    }

    class ViewHolder{
        TextView txtTen;
        ImageView imghinh;

    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.dong_video,null);
            viewHolder = new ViewHolder();
            viewHolder.imghinh = convertView.findViewById(R.id.imageviewThumbnail);
            viewHolder.txtTen = convertView.findViewById(R.id.textviewTitle);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Item item = getItem(position);

        viewHolder.txtTen.setText(item.getSnippet().getTitle());
        Picasso.get().load(item.getSnippet().getThumbnails().getMedium().getUrl()).into(viewHolder.imghinh);


        return convertView;
    }

}
