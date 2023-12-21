package com.civicx.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.civicx.android.R;
import com.civicx.android.listeners.CategoryItemClickListener;
import com.civicx.android.models.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{

    private final String TAG = this.getClass().getSimpleName();
    private final Context mContext;
    private final List<Category> stringList;
    private final CategoryItemClickListener categoryItemClickListener;

    public CategoryAdapter(Context mContext, List<Category> stringList, CategoryItemClickListener categoryItemClickListener){
        this.mContext = mContext;
        this.stringList = stringList;
        this.categoryItemClickListener = categoryItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.categories_item, parent, false);
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

        private final CardView cardView;
        private final TextView textView;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            textView = itemView.findViewById(R.id.textView);
        }

        void bind(int position) {

            final Category category = stringList.get(position);

            textView.setText(category.getCt());

            itemView.setOnClickListener(v -> categoryItemClickListener.onCategoryItemClick(category, cardView));

        }
    }
}
