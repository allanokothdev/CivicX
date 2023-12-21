package com.civicx.android.listeners;

import android.widget.ImageView;

import com.civicx.android.models.User;

public interface UserItemClickListener {

    void onUserItemClick(User user, ImageView imageView);
}
