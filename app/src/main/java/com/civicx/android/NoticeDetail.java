package com.civicx.android;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.civicx.android.constants.Constants;
import com.civicx.android.models.Post;
import com.civicx.android.utils.GetColor;
import com.civicx.android.viewpager.NoticePagerAdapter;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.Objects;

public class NoticeDetail extends AppCompatActivity implements View.OnClickListener {
    private final Context mContext = NoticeDetail.this;
    private Post post;

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        post = (Post) bundle.getSerializable(Constants.OBJECT);
        String color = GetColor.getColor(post.getHouse());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(post.getHouse());
        Objects.requireNonNull(getSupportActionBar()).setSubtitle(post.getTimestamp());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_yellow_24dp);
        toolbar.setNavigationOnClickListener(v -> finishAfterTransition());

        ImageView imageView = findViewById(R.id.imageView);
        button = findViewById(R.id.button);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        CardView cardView = findViewById(R.id.cardView);
        TextView textView = findViewById(R.id.textView);
        TextView subTextView = findViewById(R.id.subTextView);
        ExtendedFloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        cardView.setTransitionName(post.getId());

        floatingActionButton.setBackgroundColor(Color.parseColor(color));
        linearLayout.setBackgroundColor(Color.parseColor(color));

        Glide.with(mContext.getApplicationContext()).load(post.getPhotos().get(0)).placeholder(R.drawable.placeholder).into(imageView);
        textView.setText(post.getTitle());
        subTextView.setText(post.getSummary());
        floatingActionButton.setOnClickListener(this);
        button.setOnClickListener(this);
        floatingActionButton.setTag(Constants.QUERIES);

        NoticePagerAdapter noticePagerAdapter = new NoticePagerAdapter(this, post);
        floatingActionButton.setVisibility(View.VISIBLE);
        viewPager.setAdapter(noticePagerAdapter);
        viewPager.setOffscreenPageLimit(1);
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.floatingActionButton){
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.OBJECT, post);
            startActivity(new Intent(mContext, CreateQuery.class).putExtras(bundle));
        }else if (v.getId()==R.id.button){
            try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(post.getLink())));
            } catch (ActivityNotFoundException ignored) { }
        }
    }


}