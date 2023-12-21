package com.civicx.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.civicx.android.R;
import com.civicx.android.listeners.StringItemClickListener;

import java.util.List;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.ViewHolder>{
    private final Context mContext;
    private final List<String> objectList;
    private final StringItemClickListener itemClickListener;
    public SectionAdapter(Context mContext, List<String> objectList, StringItemClickListener itemClickListener){
        this.mContext = mContext;
        this.objectList = objectList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.topics_item, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }
    @Override
    public int getItemCount() {
        return objectList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView textView;
        private final ImageView imageView;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);
        }
        void bind(int position) {

            String section = objectList.get(position);
            textView.setText(section);
            imageView.setOnClickListener(view -> itemClickListener.onStringItemClick(section));

        }
    }

}
