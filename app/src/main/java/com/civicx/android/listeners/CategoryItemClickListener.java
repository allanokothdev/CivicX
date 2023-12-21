package com.civicx.android.listeners;


import androidx.cardview.widget.CardView;

import com.civicx.android.models.Category;

public interface CategoryItemClickListener {

    void onCategoryItemClick(Category category, CardView cardView);
}
