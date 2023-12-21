package com.civicx.android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.civicx.android.constants.Constants;
import com.civicx.android.models.Brand;
import com.civicx.android.models.Notification;
import com.civicx.android.models.Post;
import com.civicx.android.models.User;
import com.civicx.android.utils.GetUser;
import com.civicx.android.utils.ScreenUtils;
import com.civicx.android.viewpager.CommentPagerAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkTextView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;

public class PostDetail extends AppCompatActivity implements View.OnClickListener {

    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final Context mContext = PostDetail.this;
    private Post post;
    private RelativeLayout container;
    private static String[] tabList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        post = (Post) bundle.getSerializable(Constants.OBJECT);
        tabList = getResources().getStringArray(R.array.topics);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Post Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_yellow_24dp);
        toolbar.setNavigationOnClickListener(v -> finishAfterTransition());
        container = findViewById(R.id.container);

        CardView profileCardView = findViewById(R.id.profileCardView);
        ImageView profileImageView = findViewById(R.id.profileImageView);
        TextView textView = findViewById(R.id.textView);
        TextView subTextView = findViewById(R.id.subTextView);
        TextView subItemTextView = findViewById(R.id.subItemTextView);

        ImageView imageView = findViewById(R.id.imageView);
        TextView locationTextView = findViewById(R.id.locationTextView);
        AutoLinkTextView summaryTextView = findViewById(R.id.summaryTextView);

        ImageView commentImageView = findViewById(R.id.commentImageView);
        TextView commentTextView = findViewById(R.id.commentTextView);
        ImageView upvoteImageView = findViewById(R.id.upvoteImageView);
        TextView upvoteTextView = findViewById(R.id.upvoteTextView);
        ImageView downVoteImageView = findViewById(R.id.downvoteImageView);
        TextView downVoteTextView = findViewById(R.id.downvoteTextView);
        ImageView shareImageView = findViewById(R.id.shareImageView);
        profileCardView.setTransitionName(post.getId());
        TabLayout tabLayout = findViewById(R.id.tabs);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        setUpViewPager(viewPager,tabLayout);

        Brand brand = post.getBrand();
        Glide.with(mContext.getApplicationContext()).load(brand.getPic()).placeholder(R.drawable.placeholder).into(profileImageView);
        textView.setText(brand.getTitle());
        subItemTextView.setText(brand.getCountry());

        imageView.setTransitionName(post.getId());
      //  Glide.with(mContext.getApplicationContext()).load(post.getPhotos().get(0)).placeholder(R.drawable.placeholder).into(imageView);
        locationTextView.setText(post.getBrand().getTitle());
        summaryTextView.setUrlModeColor(ContextCompat.getColor(mContext,R.color.colorSecondary));
        summaryTextView.addAutoLinkMode(AutoLinkMode.MODE_URL);
        summaryTextView.setAutoLinkText(post.getSummary());
        summaryTextView.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> {
            if (autoLinkMode.equals(AutoLinkMode.MODE_URL)){
                webViewSheetShow(post.getBrand().getTitle(),matchedText);
            }
        });

        fetchUpVotes(upvoteImageView,post);
        fetchDownVotes(downVoteImageView,post);

        upvoteTextView.setText(String.valueOf(0));
        downVoteTextView.setText(String.valueOf(0));
        commentTextView.setText(String.valueOf(0));

        subTextView.setText(post.getTimestamp());

        shareImageView.setOnClickListener(v -> shareImage(viewToBitmap(container),post.getId()));
        commentImageView.setOnClickListener(v -> {
            Bundle bundled = new Bundle();
            bundled.putSerializable("object", post);
            mContext.startActivity(new Intent(mContext, CreateComment.class).putExtras(bundled));
        });
    }


    private void setUpViewPager(ViewPager2 viewPager, TabLayout tabLayout) {
        CommentPagerAdapter adapter = new CommentPagerAdapter(this, post);
        viewPager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(tabList[position])).attach();
    }





    private void createNotification(String token, User user, String title, String message){
        String notificationID = firebaseFirestore.collection(Constants.TOKEN_NOTIFICATION).document().getId();
        Notification notification = new Notification(notificationID,user.getPic(),title,message,post.getId(),post.getObjectType(),token);
        firebaseFirestore.collection(Constants.TOKEN_NOTIFICATION).document(notificationID).set(notification).addOnSuccessListener(unused -> { });
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

    private void shareImage(Bitmap bitmap, String postID) {
        final  String appPackageName = mContext.getPackageName();
        String link = "https://play.google.com/store/apps/details?id=" + appPackageName;
        Uri uri = saveImageToShare(bitmap);
        Intent imageIntent = new Intent(Intent.ACTION_SEND);
        imageIntent.putExtra(Intent.EXTRA_STREAM, uri);
        imageIntent.putExtra(Intent.EXTRA_TEXT, link);
        imageIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
        imageIntent.setType("image/png");
        mContext.startActivity(Intent.createChooser(imageIntent, "Share Via"));
        addSharesCount(postID);
    }

    private void addSharesCount(final String objectID){
        DocumentReference reference = firebaseFirestore.collection(Constants.POSTS).document(objectID);
        firebaseFirestore.runTransaction(transaction -> {
            DocumentSnapshot documentSnapshot = transaction.get(reference);
            double newCount = documentSnapshot.getDouble("shares")+1;
            transaction.update(reference,"shares",newCount);
            return null;
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.floatingActionButton){
            Bundle bundled = new Bundle();
            bundled.putSerializable(Constants.OBJECT,post);
            startActivity(new Intent(mContext,CreateComment.class).putExtras(bundled));
        } else if (v.getId()==R.id.summaryTextView){

        }
    }

    private void fetchUpVotes(ImageView imageView, Post post){
        User user = GetUser.getUser(mContext, currentUserID);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constants.UPVOTES).child(post.getId()).child(currentUserID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    imageView.setImageResource(R.drawable.ic_twotone_arrow_circle_up_24);
                    imageView.setOnClickListener(view -> FirebaseDatabase.getInstance().getReference(Constants.UPVOTES).child(post.getId()).child(currentUserID).removeValue().addOnSuccessListener(unused -> reduceUpVoteCount(post.getId())));
                } else {
                    imageView.setImageResource(R.drawable.ic_baseline_arrow_circle_up_24);
                    imageView.setOnClickListener(v -> {
                        addUpVoteCount(post.getId());
                        FirebaseDatabase.getInstance().getReference(Constants.UPVOTES).child(post.getId()).child(currentUserID).setValue(true);
                        String title = "Post Up Vote";
                        String message = user.getName()+ " up voted your post";
                        createNotification(post.getBrand().getToken(),user, title, message);
                    });
                }
            }@Override public void onCancelled(@NonNull @NotNull DatabaseError error) { }
        });
    }

    private void fetchDownVotes(ImageView imageView, Post post){
        User user = GetUser.getUser(mContext, currentUserID);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DOWNVOTES).child(post.getId()).child(currentUserID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    imageView.setImageResource(R.drawable.ic_twotone_arrow_circle_down_24);
                    imageView.setOnClickListener(view -> FirebaseDatabase.getInstance().getReference(Constants.DOWNVOTES).child(post.getId()).child(currentUserID).removeValue().addOnSuccessListener(unused -> reduceDownVoteCount(post.getId())));
                } else {
                    imageView.setImageResource(R.drawable.ic_baseline_arrow_circle_down_24);
                    imageView.setOnClickListener(v -> {
                        addDownVoteCount(post.getId());
                        FirebaseDatabase.getInstance().getReference(Constants.DOWNVOTES).child(post.getId()).child(currentUserID).setValue(true);
                        String title = "Post DownVote";
                        String message = user.getName()+ " down voted your post";
                        createNotification(post.getBrand().getToken(),user, title, message);
                    });
                }
            }@Override public void onCancelled(@NonNull @NotNull DatabaseError error) { }
        });
    }

    private void addUpVoteCount(final String objectID){
        DocumentReference reference = firebaseFirestore.collection(Constants.POSTS).document(objectID);
        firebaseFirestore.runTransaction(transaction -> {

            DocumentSnapshot documentSnapshot = transaction.get(reference);
            double newCount = documentSnapshot.getDouble("upVotes")+1;
            transaction.update(reference,"upVotes",newCount);
            return null;
        });
    }


    private void reduceUpVoteCount(final String objectID){
        DocumentReference reference = firebaseFirestore.collection(Constants.POSTS).document(objectID);
        firebaseFirestore.runTransaction(transaction -> {
            DocumentSnapshot documentSnapshot = transaction.get(reference);
            double newCount = documentSnapshot.getDouble("upVotes")-1;
            transaction.update(reference,"upVotes",newCount);
            return null;
        });
    }

    private void addDownVoteCount(final String objectID){
        DocumentReference reference = firebaseFirestore.collection(Constants.POSTS).document(objectID);
        firebaseFirestore.runTransaction(transaction -> {

            DocumentSnapshot documentSnapshot = transaction.get(reference);
            double newCount = documentSnapshot.getDouble("downVotes")+1;
            transaction.update(reference,"downVotes",newCount);
            return null;
        });
    }


    private void reduceDownVoteCount(final String objectID){
        DocumentReference reference = firebaseFirestore.collection(Constants.POSTS).document(objectID);
        firebaseFirestore.runTransaction(transaction -> {
            DocumentSnapshot documentSnapshot = transaction.get(reference);
            double newCount = documentSnapshot.getDouble("downVotes")-1;
            transaction.update(reference,"downVotes",newCount);
            return null;
        });
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