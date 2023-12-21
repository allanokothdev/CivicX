package com.civicx.android;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.civicx.android.constants.Constants;
import com.civicx.android.models.Brand;
import com.civicx.android.models.Post;
import com.civicx.android.utils.ScreenUtils;
import com.civicx.android.viewpager.NoticePagerAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkTextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Locale;

public class EventDetail extends AppCompatActivity implements View.OnClickListener {

    private final Context mContext = EventDetail.this;
    private Post post;
    private boolean collapsed = false;
    private CoordinatorLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        post = (Post) bundle.getSerializable(Constants.OBJECT);

        container = findViewById(R.id.container);
        ImageView shareImageView = findViewById(R.id.shareImageView);
        RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);
        ImageView finishImageView = findViewById(R.id.finishImageView);
        ImageView toolbarImageView = findViewById(R.id.toolbarImageView);
        TextView toolbarTextView = findViewById(R.id.toolbarTextView);
        TextView toolbarSubTextView = findViewById(R.id.toolbarSubTextView);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        AppBarLayout app_bar = findViewById(R.id.app_bar);
        CardView cardView = findViewById(R.id.cardView);
        TextView dateTextView = findViewById(R.id.dateTextView);
        ImageView coverImageView = findViewById(R.id.coverImageView);
        AutoLinkTextView subTextView = findViewById(R.id.subTextView);
        TextView locationTextView = findViewById(R.id.locationTextView);
        TextView timeTextView = findViewById(R.id.timeTextView);
        Button button = findViewById(R.id.button);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        ExtendedFloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);

        //floatingActionButton.setBackgroundColor(Color.parseColor(GetColor.getColor(post.getHouse())));
        button.setOnClickListener(this);
        finishImageView.setOnClickListener(this);
        shareImageView.setOnClickListener(this);
        floatingActionButton.setOnClickListener(this);

        Brand brand = post.getBrand();

        app_bar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if ( verticalOffset < -26) {
                if (!collapsed) {
                    cardView.animate().scaleX(0f).scaleY(0f).setDuration(200).start();
                    collapsed = true;
                    relativeLayout.setVisibility(View.VISIBLE);
                }
            } else {
                if (collapsed) {
                    cardView.animate().scaleX(1).scaleY(1f).setDuration(200).start();
                    collapsed = false;
                    relativeLayout.setVisibility(View.GONE);
                }
            }
        });

        Glide.with(mContext.getApplicationContext()).load(brand.getPic()).placeholder(R.drawable.logo).into(toolbarImageView);
        toolbarTextView.setText(brand.getTitle());
        toolbarSubTextView.setText(brand.getSummary());

        coverImageView.setTransitionName(post.getId());
        Glide.with(mContext.getApplicationContext()).load(post.getPhotos().get(0)).centerCrop().dontAnimate().listener(new RequestListener<Drawable>() {@Override public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) { supportStartPostponedEnterTransition();return false; }@Override public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) { supportStartPostponedEnterTransition();return false; }}).into(coverImageView);
        subTextView.setUrlModeColor(ContextCompat.getColor(mContext,R.color.colorSecondary));
        subTextView.addAutoLinkMode(AutoLinkMode.MODE_URL);
        subTextView.setAutoLinkText(post.getSummary());
        locationTextView.setText(post.getHouse());
        timeTextView.setText(post.getTimestamp());
        dateTextView.setText(getDates(post.getDate()));
        subTextView.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> {
            if (autoLinkMode.equals(AutoLinkMode.MODE_URL)){
                //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(matchedText)));
                //webViewSheetShow(post.getBrand().getTitle(),matchedText);
            }
        });

        NoticePagerAdapter noticePagerAdapter = new NoticePagerAdapter(this, post);
        floatingActionButton.setVisibility(View.VISIBLE);
        viewPager.setAdapter(noticePagerAdapter);
        viewPager.setOffscreenPageLimit(1);

    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.floatingActionButton) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.OBJECT, post);
            startActivity(new Intent(mContext, CreateQuery.class).putExtras(bundle));
        } else if (v.getId() == R.id.button) {
            try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(post.getLink())));
            } catch (ActivityNotFoundException ignored) { }
        } else if (v.getId()==R.id.finishImageView){
            finishAfterTransition();
        } else if (v.getId()==R.id.shareImageView){
            shareImage(viewToBitmap(container));
        }
    }

    private String getDates(long time){
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time);
        return DateFormat.format("dd\nMMM", cal).toString().replace(".","");
    }

    private String getTime(long time){
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time);
        return DateFormat.format("HH:mm:aa", cal).toString();
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

    private void shareImage(Bitmap bitmap) {
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

    private void webViewSheetShow(final String websiteName, final String websiteUrl){

        View view = LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_webview, null);
        WebView websiteView = view.findViewById(R.id.websiteView);
        ImageView webViewFinishImageView = view.findViewById(R.id.webViewFinishImageView);
        TextView webViewSiteTitleTextView = view.findViewById(R.id.webViewSiteTitleTextView);
        TextView webViewSiteUrlTextView = view.findViewById(R.id.webViewSiteUrlTextView);

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        BottomSheetBehavior behavior = BottomSheetBehavior.from((View) view.getParent());
        ScreenUtils screenUtils = new ScreenUtils(mContext);
        behavior.setPeekHeight(screenUtils.getHeight());

        webViewSiteTitleTextView.setText(websiteName);
        webViewSiteUrlTextView.setText(websiteUrl);

        websiteView.setWebViewClient(new Browser());
        websiteView.getSettings().setLoadsImagesAutomatically(true);
        websiteView.getSettings().setJavaScriptEnabled(true);
        websiteView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        websiteView.loadUrl(websiteUrl);
        webViewFinishImageView.setOnClickListener(v -> bottomSheetDialog.dismiss());
    }

    private static class Browser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url){
            webView.loadUrl(url);
            return true;
        }
    }
}