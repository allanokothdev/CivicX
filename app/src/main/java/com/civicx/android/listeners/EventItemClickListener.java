package com.civicx.android.listeners;

import android.widget.ImageView;

import com.civicx.android.models.Event;

public interface EventItemClickListener {

    void onEventItemClick(Event event, ImageView imageView);
}
