package com.civicx.android.viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MainPagerAdapter extends FragmentStateAdapter  {

    private final String topic;

    public MainPagerAdapter(@NonNull FragmentActivity fragmentActivity, String topic) {
        super(fragmentActivity);
        this.topic = topic;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        return null;
    }

    @Override
    public int getItemCount() {
        return 4;
    }


}
