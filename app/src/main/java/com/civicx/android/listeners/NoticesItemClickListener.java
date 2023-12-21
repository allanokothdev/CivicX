package com.civicx.android.listeners;

import androidx.cardview.widget.CardView;

import com.civicx.android.models.Notices;

public interface NoticesItemClickListener {

    void onNoticesItemClick(Notices notices, CardView cardView);
}
