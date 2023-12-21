package com.civicx.android;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.civicx.android.constants.Constants;
import com.civicx.android.models.Post;
import com.civicx.android.utils.GetColor;
import com.civicx.android.viewpager.BillPagerAdapter;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public class BillDetail extends AppCompatActivity implements View.OnClickListener {
    private final Context mContext = BillDetail.this;
    private Post post;

    private CardView cardView;
    private static String[] tabList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_detail);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        post = (Post) bundle.getSerializable(Constants.OBJECT);
        String color = GetColor.getColor(post.getHouse());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(post.getHouse());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_yellow_24dp);
        toolbar.setNavigationOnClickListener(v -> finishAfterTransition());

        if (post.getDate()<System.currentTimeMillis()){
            Objects.requireNonNull(getSupportActionBar()).setSubtitle("Submission Deadline passed");
        } else {
            Objects.requireNonNull(getSupportActionBar()).setSubtitle(post.getTimestamp());
        }

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        cardView = findViewById(R.id.cardView);
        TextView textView = findViewById(R.id.textView);
        Button button = findViewById(R.id.button);
        Button commentButton = findViewById(R.id.commentButton);
        Button shareButton = findViewById(R.id.shareButton);
        TextView subTextView = findViewById(R.id.subTextView);
        ExtendedFloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        cardView.setTransitionName(post.getId());
        button.setOnClickListener(this);
        commentButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);

        button.setBackgroundColor(Color.parseColor(color));
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(color));
        floatingActionButton.setBackgroundColor(Color.parseColor(color));
        linearLayout.setBackgroundColor(Color.parseColor(color));

        textView.setText(post.getTitle());
        subTextView.setText(post.getSummary());
        floatingActionButton.setOnClickListener(this);
        floatingActionButton.setTag(Constants.COMMENTS);

        BillPagerAdapter billPagerAdapter = new BillPagerAdapter(this, post);
        floatingActionButton.setVisibility(View.VISIBLE);
        tabList = getResources().getStringArray(R.array.billtabs);
        viewPager.setAdapter(billPagerAdapter);
        viewPager.setOffscreenPageLimit(4);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(tabList[position])).attach();
        for (int i = 0; i < tabLayout.getTabCount(); i++){
            TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.custom_tab, null);
            //tv.setBackgroundColor(Color.parseColor(color));
            tabLayout.getTabAt(i).setCustomView(tv);
        }
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        if (post.getDate()>System.currentTimeMillis()){
                            floatingActionButton.setVisibility(View.VISIBLE);
                            floatingActionButton.setTag(Constants.COMMENTS);
                            floatingActionButton.setText(R.string.add_your_voice);
                        }
                        break;
                    case 1:
                    case 3:
                        floatingActionButton.setVisibility(View.GONE);
                        floatingActionButton.setTag(Constants.COMMENTS);
                        floatingActionButton.setText(R.string.post_query);
                        break;
                    case 2:
                        floatingActionButton.setVisibility(View.VISIBLE);
                        floatingActionButton.setTag(Constants.QUERIES);
                        floatingActionButton.setText(R.string.post_query);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.floatingActionButton){
            if (v.getTag().equals(Constants.COMMENTS)){
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.OBJECT, post);
                startActivity(new Intent(mContext, CreateComment.class).putExtras(bundle));
            } else if (v.getTag().equals(Constants.QUERIES)){
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.OBJECT, post);
                startActivity(new Intent(mContext, CreateQuery.class).putExtras(bundle));
            }
        }else if (v.getId()==R.id.commentButton){
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.OBJECT, post);
            startActivity(new Intent(mContext, CreateComment.class).putExtras(bundle));
        }else if (v.getId()==R.id.shareButton){
            shareImage(viewToBitmap(cardView),post.getId());
        }else if (v.getId()==R.id.button){
            try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(post.getLink())));
            } catch (ActivityNotFoundException ignored) { }
        }
    }

    private Uri saveImageToShare(Bitmap bitmap) {
        File imageFolder = new File(mContext.getCacheDir(), "images");
        Uri uri = null;
        try {
            imageFolder.mkdirs();
            File file = new File(imageFolder, "shared_image.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(mContext, "com.civicke.android.fileprovider", file);
        }catch (Exception e){
            Toast.makeText(mContext, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

        }
        return uri;
    }

    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void shareImage(Bitmap bitmap, String commentID) {
        final  String appPackageName = mContext.getPackageName();
        String link = "https://play.google.com/store/apps/details?id=" + appPackageName;
        Uri uri = saveImageToShare(bitmap);
        Intent imageIntent = new Intent(Intent.ACTION_SEND);
        imageIntent.putExtra(Intent.EXTRA_STREAM, uri);
        imageIntent.putExtra(Intent.EXTRA_TEXT, link);
        imageIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
        imageIntent.setType("image/png");
        mContext.startActivity(Intent.createChooser(imageIntent, "Share Via"));
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getAge(int year, int month, int dayOfMonth) {
        return Period.between(LocalDate.of(year, month, dayOfMonth), LocalDate.now()).getYears();
    }

}