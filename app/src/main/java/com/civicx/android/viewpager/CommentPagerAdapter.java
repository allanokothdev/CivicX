package com.civicx.android.viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.civicx.android.fragments.CommentFragment;
import com.civicx.android.models.Post;

public class CommentPagerAdapter extends FragmentStateAdapter  {

    private final Post post;

    public CommentPagerAdapter(@NonNull FragmentActivity fragmentActivity, Post post) {
        super(fragmentActivity);
        this.post = post;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return CommentFragment.getInstance(post);
    }

    @Override
    public int getItemCount() {
        return 1;
    }


}
