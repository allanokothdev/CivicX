package com.civicx.android.listeners;

import android.widget.TextView;

import com.civicx.android.models.Topic;

public interface TopicItemClickListener {

    void onTopicItemClick(Topic topic, TextView textView);
}
