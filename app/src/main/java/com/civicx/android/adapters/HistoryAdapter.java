package com.civicx.android.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.civicx.android.CreateComment;
import com.civicx.android.R;
import com.civicx.android.constants.Constants;
import com.civicx.android.listeners.BrandItemClickListener;
import com.civicx.android.listeners.PostItemClickListener;
import com.civicx.android.models.Brand;
import com.civicx.android.models.Notification;
import com.civicx.android.models.Post;
import com.civicx.android.models.User;
import com.civicx.android.utils.GetUser;
import com.civicx.android.utils.ScreenUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class HistoryAdapter extends RecyclerView.Adapter{

    private final Context mContext;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final PostItemClickListener postItemClickListener;
    private final BrandItemClickListener brandItemClickListener;
    private final List<Post> stringList;

    public HistoryAdapter(Context mContext, List<Post> stringList, PostItemClickListener postItemClickListener, BrandItemClickListener brandItemClickListener) {
        this.stringList = stringList;
        this.mContext = mContext;
        this.brandItemClickListener = brandItemClickListener;
        this.postItemClickListener = postItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return Constants.POST_SPOTLIGHT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LegacyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.history_item, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        try {
            ((LegacyViewHolder) holder).bind(position);
        }catch (Exception e){e.printStackTrace();}
    }


    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public class LegacyViewHolder extends RecyclerView.ViewHolder{

        private final ImageView profileImageView;
        private final TextView textView;
        private final TextView subTextView;

        private final ImageView imageView;
        private final TextView summaryTextView;
        private final TextView locationTextView;

        private final ImageView commentImageView;
        private final TextView commentTextView;
        private final ImageView upVoteImageView;
        private final TextView upVoteTextView;
        private final ImageView downVoteImageView;
        private final TextView downVoteTextView;
        private final ImageView shareImageView;

        private final RelativeLayout container;
        private final ImageView optionImageView;

        public LegacyViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImageView = itemView.findViewById(R.id.profileImageView);
            summaryTextView = itemView.findViewById(R.id.summaryTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);

            container = itemView.findViewById(R.id.container);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            subTextView = itemView.findViewById(R.id.subTextView);
            commentImageView = itemView.findViewById(R.id.commentImageView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            upVoteImageView = itemView.findViewById(R.id.upvoteImageView);
            upVoteTextView = itemView.findViewById(R.id.upvoteTextView);
            downVoteImageView = itemView.findViewById(R.id.downvoteImageView);
            downVoteTextView = itemView.findViewById(R.id.downvoteTextView);
            shareImageView = itemView.findViewById(R.id.shareImageView);
            optionImageView = itemView.findViewById(R.id.optionImageView);
        }

        void bind(int position) {

            try {
                Post post = stringList.get(position);

                Brand brand = post.getBrand();
                Glide.with(mContext.getApplicationContext()).load(brand.getPic()).placeholder(R.drawable.placeholder).into(profileImageView);
                textView.setText(brand.getTitle());
                subTextView.setText(post.getTimestamp());

                Glide.with(mContext.getApplicationContext()).load(post.getPhotos().get(0)).placeholder(R.drawable.placeholder).into(imageView);
                summaryTextView.setText(post.getSummary());

                fetchUpVotes(upVoteImageView,post);
                fetchDownVotes(downVoteImageView,post);

                upVoteTextView.setText(String.valueOf(0));
                downVoteTextView.setText(String.valueOf(0));
                commentTextView.setText(String.valueOf(0));

                shareImageView.setOnClickListener(v -> shareImage(viewToBitmap(container),post.getId()));
                commentImageView.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("object", post);
                    mContext.startActivity(new Intent(mContext, CreateComment.class).putExtras(bundle));
                });

                optionImageView.setOnClickListener(view -> {
                    if (post.getBrand().getPublisher().equals(currentUserID)){
                        commentAdminOptions(post, position);
                    } else {
                        commentNormalOptions(post);
                    }
                });
            } catch (Exception e){
                e.printStackTrace();
            }
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
                        createNotification(post.getBrand().getToken(),user, title, message, post);
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
                        createNotification(post.getBrand().getToken(),user, title, message, post);
                    });
                }
            }@Override public void onCancelled(@NonNull @NotNull DatabaseError error) { }
        });
    }


    private void createNotification(String token, User user, String title, String message, Post post){
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
        addSharesCount(commentID);
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



    private void commentAdminOptions(Post comments, int position){
        @SuppressLint("InflateParams") View view = LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_comment_options, null);
        LinearLayout deleteLayout = view.findViewById(R.id.deleteLayout);
        LinearLayout editLayout = view.findViewById(R.id.editLayout);

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) view.getParent());
        ScreenUtils screenUtils = new ScreenUtils(mContext);
        behavior.setPeekHeight(screenUtils.getHeight());
        deleteLayout.setOnClickListener(view1 -> {
            deleteComment(comments,position);
            bottomSheetDialog.dismiss();
        });

        editLayout.setOnClickListener(view12 -> {
            editComment(comments);
            bottomSheetDialog.dismiss();
        });
    }

    private void commentNormalOptions(Post post){
        @SuppressLint("InflateParams") View view = LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_comment_report, null);
        LinearLayout reportLayout = view.findViewById(R.id.reportLayout);

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) view.getParent());
        ScreenUtils screenUtils = new ScreenUtils(mContext);
        behavior.setPeekHeight(screenUtils.getHeight());
        reportLayout.setOnClickListener(view1 -> {
            reportComment(post);
            bottomSheetDialog.dismiss();
        });

    }

    private void editComment(Post post){
        @SuppressLint("InflateParams") View view = LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_comment_edit, null);
        ImageView imageView = view.findViewById(R.id.imageView);
        TextView textView = view.findViewById(R.id.textView);
        Button button = view.findViewById(R.id.button);
        TextInputEditText summaryTextInputEditText = view.findViewById(R.id.summaryTextInputEditText);
        summaryTextInputEditText.setText(post.getSummary());

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) view.getParent());
        ScreenUtils screenUtils = new ScreenUtils(mContext);
        behavior.setPeekHeight(screenUtils.getHeight());

        button.setOnClickListener(view1 -> {
            String memoranda = summaryTextInputEditText.getText().toString();

            if (TextUtils.isEmpty(memoranda)){
                Toast.makeText(mContext, "Enter Post", Toast.LENGTH_SHORT).show();
            } else {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("summary", memoranda);
                firebaseFirestore.collection(Constants.POSTS).document(post.getId()).update(hashMap).addOnSuccessListener(unused -> {
                    Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                });
            }
        });
    }


    private void deleteComment(Post post, int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Delete Post");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", (dialog, which) -> FirebaseFirestore.getInstance().collection(Constants.COMMENTS).document(post.getId()).delete().addOnSuccessListener(unused -> {
            if (stringList.contains(post)){
                stringList.remove(post);
                notifyItemRemoved(position);
            }
        }));
        builder.setNegativeButton("No", (dialog, which) -> { });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void reportComment(Post post){
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Report Post");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("reported", true);
            firebaseFirestore.collection(Constants.POSTS).document(post.getId()).update(hashMap).addOnSuccessListener(unused -> {
                Toast.makeText(mContext, "Reported Successfully", Toast.LENGTH_SHORT).show();
            });
        });
        builder.setNegativeButton("No", (dialog, which) -> { });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
