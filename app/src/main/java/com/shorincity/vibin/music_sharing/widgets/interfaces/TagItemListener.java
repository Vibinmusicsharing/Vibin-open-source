package com.shorincity.vibin.music_sharing.widgets.interfaces;

import com.shorincity.vibin.music_sharing.widgets.TagModel;

public interface TagItemListener {
    void onGetAddedItem(String tagModel);

    void onGetRemovedItem(String model);
}
