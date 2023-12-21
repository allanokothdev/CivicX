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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.civicx.android.CreateResponse;
import com.civicx.android.R;
import com.civicx.android.constants.Constants;
import com.civicx.android.models.Comments;
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

public class CommentAdapter extends RecyclerView.Adapter{

    private final Context mContext;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final List<Comments> stringList;
    private final Post post;

    public static final int COMMENT = 0;
    public static final int RESPONSES = 1;

    public CommentAdapter(Context mContext, List<Comments> stringList, Post post) {
        this.stringList = stringList;
        this.mContext = mContext;
        this.post = post;
    }

    @Override
    public int getItemViewType(int position) {
        return COMMENT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentViewHolder(LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        ((CommentViewHolder) holder).bind(position);
    }


    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public class ResponseViewHolder extends RecyclerView.ViewHolder{

        private final ImageView imageView;
        private final TextView textView;
        private final TextView subTextView;
        private final TextView subItemTextView;
        private final LinearLayout container;
        private final TextView topicTextView;
        private final ImageView optionImageView;
        private final ImageView commentImageView;
        private final TextView commentTextView;

        private final ImageView upVoteImageView;
        private final TextView upVoteTextView;
        private final ImageView downVoteImageView;
        private final TextView downVoteTextView;
        private final ImageView shareImageView;
        private final RecyclerView recyclerView;

        private ResponseAdapter responseAdapter;

        public ResponseViewHolder(@NonNull View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.container);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            subTextView = itemView.findViewById(R.id.subTextView);
            subItemTextView = itemView.findViewById(R.id.subItemTextView);
            topicTextView = itemView.findViewById(R.id.topicTextView);
            optionImageView = itemView.findViewById(R.id.optionImageView);
            commentImageView = itemView.findViewById(R.id.commentImageView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            upVoteImageView = itemView.findViewById(R.id.upvoteImageView);
            upVoteTextView = itemView.findViewById(R.id.upvoteTextView);
            downVoteImageView = itemView.findViewById(R.id.downvoteImageView);
            downVoteTextView = itemView.findViewById(R.id.downvoteTextView);
            shareImageView = itemView.findViewById(R.id.shareImageView);
            recyclerView = itemView.findViewById(R.id.recyclerView);
        }

        void bind(int position) {

            try {
                Comments comments = stringList.get(position);

                if (comments.isAnonymous()){
                    Glide.with(mContext.getApplicationContext()).load(Constants.LOGO_PICTURE).placeholder(R.drawable.placeholder).into(imageView);
                    textView.setText(R.string.concerned_citizen);
                } else {
                    fetchUserDetails(comments.getPublisherID(),imageView, textView);
                }

                if (comments.isReported()){
                    subTextView.setText(R.string.flagged_comment);
                } else {
                    subTextView.setText(comments.getTopic());
                }

                topicTextView.setText(comments.getStatus());
                subItemTextView.setText(comments.getRecommendation());




                shareImageView.setOnClickListener(v -> shareImage(viewToBitmap(container),comments.getCd()));


                optionImageView.setOnClickListener(view -> {
                    if (comments.getPublisherID().equals(currentUserID)){
                        commentAdminOptions(comments, position);
                    } else {
                        commentNormalOptions(comments);
                    }
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{

        private final ImageView imageView;
        private final TextView textView;
        private final TextView subTextView;
        private final TextView subItemTextView;
        private final LinearLayout container;
        private final TextView topicTextView;
        private final ImageView optionImageView;
        private final ImageView commentImageView;
        private final TextView commentTextView;
        private final ImageView upVoteImageView;
        private final TextView upVoteTextView;
        private final ImageView downVoteImageView;
        private final TextView downVoteTextView;
        private final ImageView shareImageView;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.container);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            subTextView = itemView.findViewById(R.id.subTextView);
            subItemTextView = itemView.findViewById(R.id.subItemTextView);
            topicTextView = itemView.findViewById(R.id.topicTextView);
            optionImageView = itemView.findViewById(R.id.optionImageView);

            commentImageView = itemView.findViewById(R.id.commentImageView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            upVoteImageView = itemView.findViewById(R.id.upvoteImageView);
            upVoteTextView = itemView.findViewById(R.id.upvoteTextView);
            downVoteImageView = itemView.findViewById(R.id.downvoteImageView);
            downVoteTextView = itemView.findViewById(R.id.downvoteTextView);
            shareImageView = itemView.findViewById(R.id.shareImageView);
        }

        void bind(int position) {

            try {
                Comments comments = stringList.get(position);

                if (comments.isAnonymous()){
                    Glide.with(mContext.getApplicationContext()).load(Constants.LOGO_PICTURE).placeholder(R.drawable.placeholder).into(imageView);
                    textView.setText(R.string.concerned_citizen);
                } else {
                    fetchUserDetails(comments.getPublisherID(),imageView, textView);
                }

                if (comments.isReported()){
                    subTextView.setText(R.string.flagged_comment);
                } else {
                    subTextView.setText(comments.getTopic());
                }

                topicTextView.setText(comments.getStatus());
                subItemTextView.setText(comments.getRecommendation());

                shareImageView.setOnClickListener(v -> shareImage(viewToBitmap(container),comments.getCd()));


                optionImageView.setOnClickListener(view -> {
                    if (comments.getPublisherID().equals(currentUserID)){
                        commentAdminOptions(comments, position);
                    } else {
                        commentNormalOptions(comments);
                    }
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void fetchUserDetails(String userID, ImageView imageView, TextView textView){
        firebaseFirestore.collection(Constants.USERS).document(userID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()){
                User user = documentSnapshot.toObject(User.class);
                assert user != null;
                Glide.with(mContext.getApplicationContext()).load(user.getPic()).placeholder(R.drawable.placeholder).into(imageView);
                textView.setText(user.getName());
            }
        });
    }

    private void fetchUpVotes(ImageView imageView, Comments comments){
        User user = GetUser.getUser(mContext, currentUserID);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constants.UPVOTES).child(comments.getCd()).child(currentUserID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    imageView.setImageResource(R.drawable.baseline_thumb_up_24);
                    imageView.setOnClickListener(view -> FirebaseDatabase.getInstance().getReference(Constants.UPVOTES).child(comments.getCd()).child(currentUserID).removeValue().addOnSuccessListener(unused -> reduceUpVoteCount(comments.getCd())));
                } else {
                    imageView.setImageResource(R.drawable.outline_thumb_up_24);
                    imageView.setOnClickListener(v -> {
                        addUpVoteCount(comments.getCd());
                        FirebaseDatabase.getInstance().getReference(Constants.UPVOTES).child(comments.getCd()).child(currentUserID).setValue(true);
                        String title = "Comment Up Vote";
                        String message = user.getName()+ " up voted your comment";
                        //createNotification(comments.getTokenID(),user, title, message, post);
                    });
                }
            }@Override public void onCancelled(@NonNull @NotNull DatabaseError error) { }
        });
    }

    private void fetchDownVotes(ImageView imageView, Comments comments){
        User user = GetUser.getUser(mContext, currentUserID);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DOWNVOTES).child(comments.getCd()).child(currentUserID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    imageView.setImageResource(R.drawable.baseline_thumb_down_24);
                    imageView.setOnClickListener(view -> FirebaseDatabase.getInstance().getReference(Constants.DOWNVOTES).child(comments.getCd()).child(currentUserID).removeValue().addOnSuccessListener(unused -> reduceDownVoteCount(comments.getCd())));
                } else {
                    imageView.setImageResource(R.drawable.outline_thumb_down_24);
                    imageView.setOnClickListener(v -> {
                        addDownVoteCount(comments.getCd());
                        FirebaseDatabase.getInstance().getReference(Constants.DOWNVOTES).child(comments.getCd()).child(currentUserID).setValue(true);
                        String title = "Comment DownVote";
                        String message = user.getName()+ " down voted your comment";
                        //createNotification(comments.getTokenID(),user, title, message,post);
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
        DocumentReference reference = firebaseFirestore.collection(Constants.COMMENTS).document(objectID);
        firebaseFirestore.runTransaction(transaction -> {
            DocumentSnapshot documentSnapshot = transaction.get(reference);
            double newCount = documentSnapshot.getDouble("shares")+1;
            transaction.update(reference,"shares",newCount);
            return null;
        });
    }

    private void addUpVoteCount(final String objectID){
        DocumentReference reference = firebaseFirestore.collection(Constants.COMMENTS).document(objectID);
        firebaseFirestore.runTransaction(transaction -> {

            DocumentSnapshot documentSnapshot = transaction.get(reference);
            double newCount = documentSnapshot.getDouble("upVotes")+1;
            transaction.update(reference,"upVotes",newCount);
            return null;
        });
    }


    private void reduceUpVoteCount(final String objectID){
        DocumentReference reference = firebaseFirestore.collection(Constants.COMMENTS).document(objectID);
        firebaseFirestore.runTransaction(transaction -> {
            DocumentSnapshot documentSnapshot = transaction.get(reference);
            double newCount = documentSnapshot.getDouble("upVotes")-1;
            transaction.update(reference,"upVotes",newCount);
            return null;
        });
    }

    private void addDownVoteCount(final String objectID){
        DocumentReference reference = firebaseFirestore.collection(Constants.COMMENTS).document(objectID);
        firebaseFirestore.runTransaction(transaction -> {

            DocumentSnapshot documentSnapshot = transaction.get(reference);
            double newCount = documentSnapshot.getDouble("downVotes")+1;
            transaction.update(reference,"downVotes",newCount);
            return null;
        });
    }


    private void reduceDownVoteCount(final String objectID){
        DocumentReference reference = firebaseFirestore.collection(Constants.COMMENTS).document(objectID);
        firebaseFirestore.runTransaction(transaction -> {
            DocumentSnapshot documentSnapshot = transaction.get(reference);
            double newCount = documentSnapshot.getDouble("downVotes")-1;
            transaction.update(reference,"downVotes",newCount);
            return null;
        });
    }

    private void commentAdminOptions(Comments comments, int position){
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

        editLayout.setOnClickListener(view1 -> {
            deleteComment(comments,position);
            bottomSheetDialog.dismiss();
        });
    }

    private void editBill(Comments comments){
        @SuppressLint("InflateParams") View view = LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_bill, null);
        ImageView imageView = view.findViewById(R.id.imageView);
        TextView textView = view.findViewById(R.id.textView);
        Button button = view.findViewById(R.id.button);
        TextInputEditText titleTextInputEditText = view.findViewById(R.id.titleTextInputEditText);
        TextInputEditText summaryTextInputEditText = view.findViewById(R.id.summaryTextInputEditText);

        Glide.with(mContext.getApplicationContext()).load(R.drawable.logo).placeholder(R.drawable.placeholder).into(imageView);
        textView.setText(comments.getTopic());

        titleTextInputEditText.setText(comments.getReason());
        summaryTextInputEditText.setText(comments.getRecommendation());

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) view.getParent());
        ScreenUtils screenUtils = new ScreenUtils(mContext);
        behavior.setPeekHeight(screenUtils.getHeight());

        button.setOnClickListener(view1 -> {
            String title = titleTextInputEditText.getText().toString();
            String summary = summaryTextInputEditText.getText().toString();

            if (TextUtils.isEmpty(title)){
                Toast.makeText(mContext, "Enter Comment Reason", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(summary)){
                Toast.makeText(mContext, "Enter Comment Recommendation", Toast.LENGTH_SHORT).show();
            } else {
                textView.setText(title);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("reason", title);
                hashMap.put("recommendation", summary);
                firebaseFirestore.collection(Constants.COMMENTS).document(comments.getCd()).update(hashMap).addOnSuccessListener(unused -> {
                    Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                });
            }
        });
    }


    private void commentNormalOptions(Comments comments){
        @SuppressLint("InflateParams") View view = LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_comment_report, null);
        LinearLayout reportLayout = view.findViewById(R.id.reportLayout);

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) view.getParent());
        ScreenUtils screenUtils = new ScreenUtils(mContext);
        behavior.setPeekHeight(screenUtils.getHeight());
        reportLayout.setOnClickListener(view1 -> {
            reportComment(comments);
            bottomSheetDialog.dismiss();
        });

    }



    private void deleteComment(Comments comments, int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Delete Comment");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", (dialog, which) -> FirebaseFirestore.getInstance().collection(Constants.COMMENTS).document(comments.getCd()).delete().addOnSuccessListener(unused -> {
            if (stringList.contains(comments)){
                stringList.remove(comments);
                notifyItemRemoved(position);
            }
        }));
        builder.setNegativeButton("No", (dialog, which) -> { });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void reportComment(Comments comments){
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Report Comment");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("reported", true);
            firebaseFirestore.collection(Constants.COMMENTS).document(comments.getCd()).update(hashMap).addOnSuccessListener(unused -> {
                Toast.makeText(mContext, "Reported Successfully", Toast.LENGTH_SHORT).show();
            });
        });
        builder.setNegativeButton("No", (dialog, which) -> { });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
