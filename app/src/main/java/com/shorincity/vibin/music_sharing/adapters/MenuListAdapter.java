package com.shorincity.vibin.music_sharing.adapters;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import com.shorincity.vibin.music_sharing.model.MenuActionItem;
import com.shorincity.vibin.music_sharing.R;

public class MenuListAdapter extends ArrayAdapter<MenuActionItem> {

    int resource;
    Activity activity;

    public MenuListAdapter(int resource, Activity activity, MenuActionItem[] items) {
        super(activity, resource, items);

        this.resource = resource;
        this.activity = activity;
    }

    public View getView (int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if(rowView == null) {
            rowView = activity.getLayoutInflater().inflate(resource, null);

            MenuItemViewHolder viewHolder = new MenuItemViewHolder();

            viewHolder.menuItemImageView = rowView.findViewById(R.id.menu_item_image_view);
            viewHolder.menuItemTextView = rowView.findViewById(R.id.menu_item_text_view);
            viewHolder.menuItemSwitch = rowView.findViewById(R.id.switch_notifications);

            rowView.setTag(viewHolder);
        }

        MenuItemViewHolder holder = (MenuItemViewHolder)rowView.getTag();

//        if(position == MenuActionItem.ITEM1.ordinal()) {
//            //holder.menuItemImageView.setImageDrawable(activity_youtube_2.getDrawable(R.drawable.ic_payment_white_24dp));
//            holder.menuItemTextView.setText(activity.getResources().getString(R.string.push_notification));
//            holder.menuItemSwitch.setVisibility(View.GONE);
//
//        }
//        else
        if(position == MenuActionItem.ITEM1.ordinal()) {
            //holder.menuItemImageView.setImageDrawable(activity_youtube_2.getDrawable(R.drawable.ic_pets_white_24dp));
            holder.menuItemTextView.setText(activity.getResources().getString(R.string.about_us));
            holder.menuItemSwitch.setVisibility(View.GONE);
        }
        else if(position == MenuActionItem.ITEM2.ordinal()) {
            //holder.menuItemImageView.setImageDrawable(activity_youtube_2.getDrawable(R.drawable.ic_receipt_white_24dp));
            holder.menuItemTextView.setText(activity.getResources().getString(R.string.share_app));
            holder.menuItemSwitch.setVisibility(View.GONE);
        }
        else if(position == MenuActionItem.ITEM3.ordinal()) {
            //holder.menuItemImageView.setImageDrawable(activity_youtube_2.getDrawable(R.drawable.ic_shopping_cart_white_24dp));
            holder.menuItemTextView.setText(activity.getResources().getString(R.string.privacy_policy));
            holder.menuItemSwitch.setVisibility(View.GONE);
        }
        /*else if(position == MenuActionItem.ITEM5.ordinal()) {
            //holder.menuItemImageView.setImageDrawable(activity_youtube_2.getDrawable(R.drawable.ic_work_white_24dp));
            holder.menuItemTextView.setText(activity_youtube_2.getResources().getString(R.string.privacy_policy));
        }*/

//        holder.menuItemSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked){
//
//
//                }else {
//
//                }
//            }
//        });

//        holder.menuItemSwitch.setOnCheckedChangeListener(new SwitchCompat.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                getSwitchChangeListener().onSwitchChanged(compoundButton,b);
//                Toast.makeText(getContext(), "notification", Toast.LENGTH_LONG).show();
//            }
//        });

        return rowView;
    }

    private static class MenuItemViewHolder {
        public ImageView menuItemImageView;
        public TextView menuItemTextView;
        public Switch menuItemSwitch;
    }

    public SwitchChangeListener getSwitchChangeListener() {
        return switchChangeListener;
    }

    public void setSwitchChangeListener(SwitchChangeListener switchChangeListener) {
        this.switchChangeListener = switchChangeListener;
    }

    SwitchChangeListener switchChangeListener;

    public interface SwitchChangeListener
    {
        void onSwitchChanged(CompoundButton compoundButton, boolean b);
    }
}