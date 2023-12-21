package com.civicx.android.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.civicx.android.R;
import com.civicx.android.constants.Constants;
import com.civicx.android.listeners.PostItemClickListener;
import com.civicx.android.models.Comments;
import com.civicx.android.models.Post;
import com.civicx.android.models.Reply;
import com.civicx.android.models.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder>{

    private final Context mContext;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final PostItemClickListener postItemClickListener;
    private final List<Reply> stringList;

    public ReplyAdapter(Context mContext, List<Reply> stringList, PostItemClickListener postItemClickListener) {
        this.stringList = stringList;
        this.mContext = mContext;
        this.postItemClickListener = postItemClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.reply_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }


    @Override
    public int getItemCount() {
        return stringList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private final ImageView brandImageView;
        private final TextView brandTitleTextView;
        private final TextView brandCategoryTextView;

        private final CardView cardView;
        private final LinearLayout linearLayout;
        private final TextView textView;
        private final TextView subTextView;
        private final TextView subItemTextView;
        private final TextView houseTextView;

        private final ImageView imageView;
        private final TextView replyTextView;
        private final TextView replySubTextView;
        private final TextView contentTextView;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            brandImageView = itemView.findViewById(R.id.brandImageView);
            brandTitleTextView = itemView.findViewById(R.id.brandTitleTextView);
            brandCategoryTextView = itemView.findViewById(R.id.brandCategoryTextView);
            textView = itemView.findViewById(R.id.textView);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            subTextView = itemView.findViewById(R.id.subTextView);
            subItemTextView = itemView.findViewById(R.id.subItemTextView);
            houseTextView = itemView.findViewById(R.id.houseTextView);

            imageView = itemView.findViewById(R.id.imageView);
            replyTextView = itemView.findViewById(R.id.replyTextView);
            replySubTextView = itemView.findViewById(R.id.replySubTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
        }

        void bind(int position) {

            Reply reply = stringList.get(position);
            fetchBill(reply,brandImageView,brandTitleTextView,brandCategoryTextView,cardView,linearLayout,textView,subTextView,houseTextView,subItemTextView,itemView);
            fetchComment(reply,imageView,replyTextView,replySubTextView,contentTextView);
        }
    }

    private void fetchBill(Reply reply,ImageView brandImageView, TextView brandTitleTextView, TextView brandCategoryTextView,CardView cardView, LinearLayout linearLayout ,TextView textView ,TextView subTextView ,TextView houseTextView,TextView subItemTextView,View itemView){
        firebaseFirestore.collection(Constants.POSTS).document(reply.getPostID()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()){
                Post post = documentSnapshot.toObject(Post.class);
                assert post != null;
                cardView.setTransitionName(post.getId());

                Glide.with(mContext.getApplicationContext()).load(post.getBrand().getPic()).placeholder(R.drawable.logo).into(brandImageView);
                brandTitleTextView.setText(post.getBrand().getTitle());
                brandCategoryTextView.setText(post.getBrand().getSummary());

                //linearLayout.setBackgroundColor();
                textView.setText(post.getTitle().trim());
                subTextView.setText(post.getSummary().trim());
                houseTextView.setText(post.getHouse());
                subItemTextView.setText(post.getTimestamp());
                itemView.setOnClickListener(v -> postItemClickListener.onPostItemClick(post,cardView));

            }
        });
    }

    private void fetchComment(Reply reply, ImageView imageView,TextView replyTextView ,TextView replySubTextView,TextView contentTextView){
        firebaseFirestore.collection(Constants.COMMENTS).document(reply.getCommentID()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()){
                Comments comments = documentSnapshot.toObject(Comments.class);
                assert comments != null;
                replySubTextView.setText(getTime(comments.getTimestamp()));
                contentTextView.setText(comments.getRecommendation());

                if (comments.isAnonymous()){
                    Glide.with(mContext.getApplicationContext()).load(Constants.LOGO_PICTURE).placeholder(R.drawable.placeholder).into(imageView);
                    replyTextView.setText(R.string.concerned_citizen);
                } else {
                    fetchUserDetails(comments.getPublisherID(),imageView, replyTextView);
                }

            }
        });
    }


    private String getTime(long time){
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time);
        return DateFormat.format("EEEE, dd MMMM yyyy", cal).toString();
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
}
