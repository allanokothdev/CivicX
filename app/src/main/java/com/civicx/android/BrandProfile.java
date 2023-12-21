package com.civicx.android;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.civicx.android.constants.Constants;
import com.civicx.android.models.Brand;
import com.civicx.android.models.User;
import com.civicx.android.utils.GetUser;
import com.civicx.android.viewpager.BrandPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class BrandProfile extends AppCompatActivity {

    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private static String[] tabList;
    private final Context mContext = BrandProfile.this;
    private Brand brand;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_profile);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        brand = (Brand) bundle.getSerializable(Constants.OBJECT);
        tabList = getResources().getStringArray(R.array.tabs);

        user = GetUser.getUser(mContext, currentUserID);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(brand.getSummary());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_yellow_24dp);
        toolbar.setNavigationOnClickListener(v -> finishAfterTransition());

        ImageView imageView = findViewById(R.id.imageView);
        TextView textView = findViewById(R.id.textView);
        TextView subTextView = findViewById(R.id.subTextView);
        TextView messageTextView = findViewById(R.id.messageTextView);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        imageView.setTransitionName(brand.getId());
        Glide.with(mContext.getApplicationContext()).load(brand.getPic()).centerCrop().dontAnimate().listener(new RequestListener<Drawable>() {@Override public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) { supportStartPostponedEnterTransition();return false; }@Override public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) { supportStartPostponedEnterTransition();return false; }}).into(imageView);
        textView.setText(brand.getTitle());
        subTextView.setText(brand.getSummary());
        messageTextView.setOnClickListener(v -> Toast.makeText(mContext,"DM not Open",Toast.LENGTH_SHORT).show());
        setUpViewPager(viewPager,tabLayout);

    }

    private void setUpViewPager(ViewPager2 viewPager, TabLayout tabLayout) {
        BrandPagerAdapter adapter = new BrandPagerAdapter(this, brand);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(tabList[position])).attach();
        for (int i = 0; i < tabLayout.getTabCount(); i++){
            TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.custom_tab, null);
            tabLayout.getTabAt(i).setCustomView(tv);
        }
    }


    private void getSubscription(String objectID, final TextView textView) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constants.SUBSCRIBERS).child(objectID).child(currentUserID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    textView.setText(R.string.subscribed);
                    textView.setBackgroundResource(R.drawable.blue_background_button);
                    textView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                    textView.setOnClickListener(v -> {
                    });
                }else {
                    textView.setText(R.string.subscribe);
                    textView.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                    textView.setBackgroundResource(R.drawable.white_background_button);
                    textView.setOnClickListener(v -> FirebaseDatabase.getInstance().getReference(Constants.SUBSCRIBERS).child(objectID).child(currentUserID).setValue(user));
                }
            }@Override public void onCancelled(@NonNull DatabaseError error) { }
        });
    }



}