package com.civicx.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
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
import androidx.cardview.widget.CardView;
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
import com.civicx.android.utils.GetColor;
import com.civicx.android.utils.GetUser;
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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter{

    private final Context mContext;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final PostItemClickListener postItemClickListener;
    private final BrandItemClickListener brandItemClickListener;
    private final List<Post> stringList;

    public PostAdapter(Context mContext, List<Post> stringList, PostItemClickListener postItemClickListener, BrandItemClickListener brandItemClickListener) {
        this.stringList = stringList;
        this.mContext = mContext;
        this.brandItemClickListener = brandItemClickListener;
        this.postItemClickListener = postItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        Post post = stringList.get(position);
        switch (post.getObjectType()) {
            case Constants.ACTIVITY:
                return Constants.POST_SPOTLIGHT;
            case Constants.EVENT:
                return Constants.POST_EVENT;
            case Constants.SURVEY:
                return Constants.POST_SURVEY;
            case Constants.NOTICE:
                return Constants.POST_NOTICE;
            default:
                return Constants.POST_BILL;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Constants.POST_SPOTLIGHT){
            return new SpotlightViewHolder(LayoutInflater.from(mContext).inflate(R.layout.history_item, parent, false));
        } else if (viewType == Constants.POST_BILL){
            return new BillViewHolder(LayoutInflater.from(mContext).inflate(R.layout.bill_item, parent, false));
        } else if (viewType == Constants.POST_EVENT){
            return new EventViewHolder(LayoutInflater.from(mContext).inflate(R.layout.event_item, parent, false));
        } else if (viewType == Constants.POST_SURVEY){
            return new SurveyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.bill_item, parent, false));
        } else if (viewType == Constants.POST_NOTICE){
            return new NoticeViewHolder(LayoutInflater.from(mContext).inflate(R.layout.event_item, parent, false));
        } else {
            return new BillViewHolder(LayoutInflater.from(mContext).inflate(R.layout.bill_item, parent, false));
        }
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        try {
            Post post = stringList.get(position);
            if (post.getObjectType().equals(Constants.ACTIVITY)){
                ((SpotlightViewHolder) holder).bind(position);
            } else if (post.getObjectType().equals(Constants.BILL)){
                ((BillViewHolder) holder).bind(position);
            } else if (post.getObjectType().equals(Constants.EVENT)){
                ((EventViewHolder) holder).bind(position);
            } else if (post.getObjectType().equals(Constants.NOTICE)){
                ((NoticeViewHolder) holder).bind(position);
            } else if (post.getObjectType().equals(Constants.SURVEY)){
                ((SurveyViewHolder) holder).bind(position);
            }
        }catch (Exception e){e.printStackTrace();}
    }


    @Override
    public int getItemCount() {
        return stringList.size();
    }


    public class BillViewHolder extends RecyclerView.ViewHolder{

        private final LinearLayout linearLayout;
        private final TextView topicTextView;
        private final CircleImageView profileImageView;
        private final TextView textView;
        private final TextView subTextView;
        private final TextView titleTextView;
        private final TextView summaryTextView;
        private final TextView locationTextView;

        private final Button commentButton;
        private final TextView commentTextView;
        private final ImageView upVoteImageView;
        private final TextView upVoteTextView;
        private final ImageView downVoteImageView;
        private final TextView downVoteTextView;
        private final Button shareButton;

        private final LinearLayout container;
        private final CardView cardView;

        private BillViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.linearLayout);
            topicTextView = itemView.findViewById(R.id.topicTextView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            summaryTextView = itemView.findViewById(R.id.summaryTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            cardView = itemView.findViewById(R.id.cardView);
            container = itemView.findViewById(R.id.container);
            textView = itemView.findViewById(R.id.textView);
            subTextView = itemView.findViewById(R.id.subTextView);
            commentButton = itemView.findViewById(R.id.commentButton);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            upVoteImageView = itemView.findViewById(R.id.upvoteImageView);
            upVoteTextView = itemView.findViewById(R.id.upvoteTextView);
            downVoteImageView = itemView.findViewById(R.id.downvoteImageView);
            downVoteTextView = itemView.findViewById(R.id.downvoteTextView);
            shareButton = itemView.findViewById(R.id.shareButton);
        }

        void bind(int position) {

            Post post = stringList.get(position);
            cardView.setTransitionName(post.getId());

            Brand brand = post.getBrand();
            Glide.with(mContext.getApplicationContext()).load(brand.getPic()).placeholder(R.drawable.placeholder).into(profileImageView);
            topicTextView.setText("Bill");
            textView.setText(brand.getTitle());
            subTextView.setText(post.getTimestamp());
            summaryTextView.setText(post.getSummary());
            titleTextView.setText(post.getTitle());
            locationTextView.setText(post.getHouse());
            shareButton.setOnClickListener(v -> shareImage(viewToBitmap(container),post.getId()));
            commentButton.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.OBJECT, post);
                mContext.startActivity(new Intent(mContext, CreateComment.class).putExtras(bundle));
            });

            if (post.getDate()<System.currentTimeMillis()){
                commentButton.setText("Deadline passed");
                commentButton.setEnabled(false);
            }

            cardView.setOnClickListener(v -> postItemClickListener.onPostItemClick(post,cardView));
            profileImageView.setOnClickListener(v -> brandItemClickListener.onBrandItemClick(post.getBrand(),profileImageView));
            linearLayout.setBackgroundColor(Color.parseColor(GetColor.getColor(post.getCategory())));
            shareButton.setBackgroundColor(Color.parseColor(GetColor.getColor(post.getCategory())));
            commentButton.setBackgroundColor(Color.parseColor(GetColor.getColor(post.getCategory())));

        }
    }

    public class SurveyViewHolder extends RecyclerView.ViewHolder{

        private final LinearLayout linearLayout;
        private final TextView topicTextView;
        private final CircleImageView profileImageView;
        private final TextView textView;
        private final TextView subTextView;
        private final TextView titleTextView;
        private final TextView summaryTextView;
        private final TextView locationTextView;

        private final Button commentImageView;
        private final TextView commentTextView;
        private final ImageView upVoteImageView;
        private final TextView upVoteTextView;
        private final ImageView downVoteImageView;
        private final TextView downVoteTextView;
        private final Button shareImageView;

        private final LinearLayout container;
        private final CardView cardView;

        private SurveyViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.linearLayout);
            topicTextView = itemView.findViewById(R.id.topicTextView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            summaryTextView = itemView.findViewById(R.id.summaryTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            cardView = itemView.findViewById(R.id.cardView);
            container = itemView.findViewById(R.id.container);
            textView = itemView.findViewById(R.id.textView);
            subTextView = itemView.findViewById(R.id.subTextView);
            commentImageView = itemView.findViewById(R.id.commentButton);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            upVoteImageView = itemView.findViewById(R.id.upvoteImageView);
            upVoteTextView = itemView.findViewById(R.id.upvoteTextView);
            downVoteImageView = itemView.findViewById(R.id.downvoteImageView);
            downVoteTextView = itemView.findViewById(R.id.downvoteTextView);
            shareImageView = itemView.findViewById(R.id.shareButton);
        }

        void bind(int position) {

            Post post = stringList.get(position);
            cardView.setTransitionName(post.getId());

            Brand brand = post.getBrand();
            Glide.with(mContext.getApplicationContext()).load(brand.getPic()).placeholder(R.drawable.placeholder).into(profileImageView);
            topicTextView.setText(post.getTopic());
            textView.setText(brand.getTitle());
            subTextView.setText(post.getTimestamp());
            summaryTextView.setText(post.getSummary());
            titleTextView.setText(post.getTitle());
            locationTextView.setText(post.getHouse());
            commentImageView.setText("Participate");
            shareImageView.setOnClickListener(v -> shareImage(viewToBitmap(container),post.getId()));
            commentImageView.setOnClickListener(v -> postItemClickListener.onPostItemClick(post,cardView));
            cardView.setOnClickListener(v -> postItemClickListener.onPostItemClick(post,cardView));
            profileImageView.setOnClickListener(v -> brandItemClickListener.onBrandItemClick(post.getBrand(),profileImageView));
            linearLayout.setBackgroundColor(Color.parseColor(GetColor.getColor(post.getCategory())));
            shareImageView.setBackgroundColor(Color.parseColor(GetColor.getColor(post.getCategory())));
            commentImageView.setBackgroundColor(Color.parseColor(GetColor.getColor(post.getCategory())));

        }
    }

    public class SpotlightViewHolder extends RecyclerView.ViewHolder{

        private final CircleImageView profileImageView;
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
        private final CardView cardView;
        private final RelativeLayout container;
        private final ImageView optionImageView;

        public SpotlightViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImageView = itemView.findViewById(R.id.profileImageView);
            summaryTextView = itemView.findViewById(R.id.summaryTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            container = itemView.findViewById(R.id.container);
            cardView = itemView.findViewById(R.id.cardView);
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
                locationTextView.setText(post.getBrand().getSummary());
                imageView.setOnClickListener(v -> postItemClickListener.onPostItemClick(post,cardView));
                shareImageView.setOnClickListener(v -> shareImage(viewToBitmap(container),post.getId()));
                commentImageView.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.OBJECT, post);
                    mContext.startActivity(new Intent(mContext, CreateComment.class).putExtras(bundle));
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public class NoticeViewHolder extends RecyclerView.ViewHolder{

        private final TextView topicTextView;
        private final CircleImageView profileImageView;
        private final ImageView coverImageView;
        private final TextView textView;
        private final TextView subTextView;
        private final TextView locationTextView;

        private final LinearLayout container;

        private final CardView cardView;

        private final Button commentButton;
        private final Button shareButton;


        private NoticeViewHolder(@NonNull View itemView) {
            super(itemView);

            topicTextView = itemView.findViewById(R.id.topicTextView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            coverImageView = itemView.findViewById(R.id.coverImageView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            textView = itemView.findViewById(R.id.textView);
            subTextView = itemView.findViewById(R.id.subTextView);
            cardView = itemView.findViewById(R.id.cardView);
            commentButton = itemView.findViewById(R.id.commentButton);
            shareButton = itemView.findViewById(R.id.shareButton);
            container = itemView.findViewById(R.id.container);
        }

        void bind(int position) {

            Post post = stringList.get(position);
            commentButton.setText("View Notice");
            cardView.setTransitionName(post.getId());
            Brand brand = post.getBrand();
            topicTextView.setText(post.getTopic());
            textView.setText(brand.getTitle());
            subTextView.setText(post.getTimestamp());
            locationTextView.setText(post.getHouse());
            Glide.with(mContext.getApplicationContext()).load(brand.getPic()).placeholder(R.drawable.placeholder).into(profileImageView);
            Glide.with(mContext.getApplicationContext()).load(post.getPhotos().get(0)).placeholder(R.drawable.placeholder).into(coverImageView);

            shareButton.setOnClickListener(v -> shareImage(viewToBitmap(container),post.getId()));
            commentButton.setOnClickListener(v -> postItemClickListener.onPostItemClick(post,cardView));



            cardView.setOnClickListener(v -> postItemClickListener.onPostItemClick(post,cardView));
            profileImageView.setOnClickListener(v -> brandItemClickListener.onBrandItemClick(post.getBrand(),profileImageView));

        }
    }

    public class EventViewHolder extends RecyclerView.ViewHolder{
        private final TextView topicTextView;
        private final CircleImageView profileImageView;
        private final ImageView coverImageView;
        private final TextView textView;
        private final TextView subTextView;
        private final TextView locationTextView;

        private final LinearLayout container;
        private final CardView cardView;
        private final Button commentButton;
        private final Button shareButton;

        private EventViewHolder(@NonNull View itemView) {
            super(itemView);

            topicTextView = itemView.findViewById(R.id.topicTextView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            coverImageView = itemView.findViewById(R.id.coverImageView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            textView = itemView.findViewById(R.id.textView);
            subTextView = itemView.findViewById(R.id.subTextView);
            cardView = itemView.findViewById(R.id.cardView);
            container = itemView.findViewById(R.id.container);

            commentButton = itemView.findViewById(R.id.commentButton);
            shareButton = itemView.findViewById(R.id.shareButton);
        }

        void bind(int position) {

            Post post = stringList.get(position);
            cardView.setTransitionName(post.getId());
            commentButton.setText("View Event");

            Brand brand = post.getBrand();
            topicTextView.setText("Public Event");
            textView.setText(brand.getTitle());
            subTextView.setText(post.getTimestamp());
            locationTextView.setText(post.getHouse());
            Glide.with(mContext.getApplicationContext()).load(brand.getPic()).placeholder(R.drawable.placeholder).into(profileImageView);
            Glide.with(mContext.getApplicationContext()).load(post.getPhotos().get(0)).placeholder(R.drawable.placeholder).into(coverImageView);

            shareButton.setOnClickListener(v -> shareImage(viewToBitmap(container),post.getId()));
            commentButton.setOnClickListener(v -> postItemClickListener.onPostItemClick(post,cardView));

            cardView.setOnClickListener(v -> postItemClickListener.onPostItemClick(post,cardView));
            profileImageView.setOnClickListener(v -> brandItemClickListener.onBrandItemClick(post.getBrand(),profileImageView));

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


    private void fetchUpVotes(ImageView imageView, Post post){
        User user = GetUser.getUser(mContext, currentUserID);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constants.UPVOTES).child(post.getId()).child(currentUserID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    imageView.setImageResource(R.drawable.baseline_thumb_up_24);
                    imageView.setOnClickListener(view -> FirebaseDatabase.getInstance().getReference(Constants.UPVOTES).child(post.getId()).child(currentUserID).removeValue().addOnSuccessListener(unused -> reduceUpVoteCount(post.getId())));
                } else {
                    imageView.setImageResource(R.drawable.outline_thumb_up_24);
                    imageView.setOnClickListener(v -> {
                        addUpVoteCount(post.getId());
                        FirebaseDatabase.getInstance().getReference(Constants.UPVOTES).child(post.getId()).child(currentUserID).setValue(true);
                        String title = "Post Up Vote";
                        String message = user.getName()+ " up voted your post";
                        createNotification(post.getBrand().getToken(),user, title, message,post);
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
                    imageView.setImageResource(R.drawable.baseline_thumb_down_24);
                    imageView.setOnClickListener(view -> FirebaseDatabase.getInstance().getReference(Constants.DOWNVOTES).child(post.getId()).child(currentUserID).removeValue().addOnSuccessListener(unused -> reduceDownVoteCount(post.getId())));
                } else {
                    imageView.setImageResource(R.drawable.outline_thumb_down_24);
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

}
