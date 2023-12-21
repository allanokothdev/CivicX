package com.civicx.android.viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.civicx.android.fragments.AssentedFragment;
import com.civicx.android.fragments.MemorandaFragment;
import com.civicx.android.fragments.QueriesFragment;
import com.civicx.android.fragments.SummaryFragment;
import com.civicx.android.models.Post;

public class BillPagerAdapter extends FragmentStateAdapter  {

    private final Post post;

    public BillPagerAdapter(@NonNull FragmentActivity fragmentActivity, Post post) {
        super(fragmentActivity);
        this.post = post;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return MemorandaFragment.getInstance(post);
            case 1:
                return SummaryFragment.getInstance(post);
            case 2:
                return QueriesFragment.getInstance(post);
            case 3:
                return AssentedFragment.getInstance(post);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 4;
    }


}
