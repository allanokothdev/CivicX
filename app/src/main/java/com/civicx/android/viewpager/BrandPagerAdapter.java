package com.civicx.android.viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.civicx.android.fragments.EventFragment;
import com.civicx.android.fragments.LawFragment;
import com.civicx.android.fragments.NoticesFragment;
import com.civicx.android.fragments.SpotlightFragment;
import com.civicx.android.models.Brand;

public class BrandPagerAdapter extends FragmentStateAdapter  {

    private final Brand brand;

    public BrandPagerAdapter(@NonNull FragmentActivity fragmentActivity, Brand brand) {
        super(fragmentActivity);
        this.brand = brand;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return SpotlightFragment.getInstance(brand);
            case 1:
                return LawFragment.getInstance(brand);
            case 2:
                return NoticesFragment.getInstance(brand);
            case 3:
                return EventFragment.getInstance(brand);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 4;
    }


}
