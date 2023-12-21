package com.civicx.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.civicx.android.R;
import com.civicx.android.constants.Constants;
import com.civicx.android.models.Brand;
import com.civicx.android.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.ViewHolder>{

    private final Context mContext;
    private final List<Brand> stringList;
    private final User user;

    public BrandAdapter(Context mContext, List<Brand> stringList, User user) {
        this.stringList = stringList;
        this.mContext = mContext;
        this.user = user;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
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

            Brand brand = stringList.get(position);
            imageView.setTransitionName(brand.getId());
            Glide.with(mContext.getApplicationContext()).load(brand.getPic()).placeholder(R.drawable.placeholder).into(imageView);
            textView.setText(brand.getTitle());
            subTextView.setText(brand.getSummary());
            fetchSubscribers(brand,user,subItemTextView);
        }
    }

    private void fetchSubscribers(Brand brand, User user, TextView textView){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constants.SUBSCRIBERS).child(brand.getId()).child(user.getId());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    textView.setText(R.string.subscribed);
                    textView.setBackgroundResource(R.drawable.blue_background_button);
                    textView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                    textView.setOnClickListener(v -> FirebaseDatabase.getInstance().getReference(Constants.SUBSCRIBERS).child(brand.getId()).child(user.getId()).removeValue());
                } else {
                    textView.setText(R.string.subscribe);
                    textView.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                    textView.setBackgroundResource(R.drawable.white_background_button);
                    textView.setOnClickListener(v -> FirebaseDatabase.getInstance().getReference(Constants.SUBSCRIBERS).child(brand.getId()).child(user.getId()).setValue(user));
                }
            }@Override public void onCancelled(@NonNull @NotNull DatabaseError error) { }
        });
    }


}
