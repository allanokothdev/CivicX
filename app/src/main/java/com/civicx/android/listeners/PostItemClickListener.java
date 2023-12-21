package com.civicx.android.listeners;

import androidx.cardview.widget.CardView;

import com.civicx.android.models.Post;

public interface PostItemClickListener {

    void onPostItemClick(Post post, CardView cardView);
}
