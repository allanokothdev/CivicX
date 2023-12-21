package com.civicx.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.civicx.android.R;
import com.civicx.android.constants.Constants;
import com.civicx.android.models.Response;
import com.civicx.android.models.User;
import com.civicx.android.utils.PostGetTimeAgo;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ResponseAdapter extends RecyclerView.Adapter<ResponseAdapter.ViewHolder>{

    private final String TAG = this.getClass().getSimpleName();
    private final Context mContext;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final List<Response> stringList;

    public ResponseAdapter(Context mContext, List<Response> stringList){
        this.mContext = mContext;
        this.stringList = stringList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.response_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ((ViewHolder) holder).bind(position);
    }


    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private final ImageView imageView;
        private final TextView textView;
        private final TextView subTextView;
        private final TextView subItemTextView;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            subTextView = itemView.findViewById(R.id.subTextView);
            subItemTextView = itemView.findViewById(R.id.subItemTextView);
        }

        void bind(int position) {
            final Response response = stringList.get(position);
            fetchUserDetails(response.getPublisherID(),imageView, textView);
            subTextView.setText(PostGetTimeAgo.postGetTimeAgo(response.getTimestamp(),mContext));
            subItemTextView.setText(response.getSummary());
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
}
