package com.civicx.android.viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.civicx.android.fragments.QueriesFragment;
import com.civicx.android.fragments.QuestionFragment;
import com.civicx.android.fragments.SubmissionFragment;
import com.civicx.android.models.Post;

public class SurveyPagerAdapter extends FragmentStateAdapter  {

    private final Post post;

    public SurveyPagerAdapter(@NonNull FragmentActivity fragmentActivity, Post post) {
        super(fragmentActivity);
        this.post = post;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return QuestionFragment.getInstance(post);
            case 1:
                return QueriesFragment.getInstance(post);
            case 2:
                return SubmissionFragment.getInstance(post);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }


}
