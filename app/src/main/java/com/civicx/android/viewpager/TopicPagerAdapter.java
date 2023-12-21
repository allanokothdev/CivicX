package com.civicx.android.viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.civicx.android.fragments.BillFragment;
import com.civicx.android.fragments.HistoryFragment;
import com.civicx.android.models.Topic;

public class TopicPagerAdapter extends FragmentStateAdapter  {

    private final Topic topic;

    public TopicPagerAdapter(@NonNull FragmentActivity fragmentActivity, Topic topic) {
        super(fragmentActivity);
        this.topic = topic;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return BillFragment.getInstance(topic);
            case 1:
                return HistoryFragment.getInstance(topic);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }


}
