package com.civicx.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.civicx.android.adapters.PostAdapter;
import com.civicx.android.constants.Constants;
import com.civicx.android.listeners.BrandItemClickListener;
import com.civicx.android.listeners.PostItemClickListener;
import com.civicx.android.models.Brand;
import com.civicx.android.models.Category;
import com.civicx.android.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Categories extends AppCompatActivity implements PostItemClickListener, BrandItemClickListener,View.OnClickListener {

    private final Context mContext = Categories.this;
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ListenerRegistration registration;
    private AVLoadingIndicatorView progressBar;
    private final List<Post> objectList = new ArrayList<>();
    private PostAdapter adapter;
    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        category = (Category) bundle.getSerializable(Constants.OBJECT);

        progressBar = findViewById(R.id.progressBar);
        ImageView imageView = findViewById(R.id.imageView);
        ImageView finishImageView = findViewById(R.id.finishImageView);
        TextView textView = findViewById(R.id.textView);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(mContext, R.drawable.posts_divider)));
        recyclerView.addItemDecoration(divider);
        adapter = new PostAdapter(mContext, objectList, this, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        fetchObjects(category.getCt());

        imageView.setTransitionName(category.getCd());
        textView.setText(category.getCt());
        finishImageView.setOnClickListener(this);

        new Handler().postDelayed(this::emptyList, 5000);

    }

    private void emptyList(){
        progressBar.setVisibility(View.GONE);
        if (objectList.size()<1){
            Toast.makeText(mContext, "There are no Posts under "+category.getCt(), Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchObjects(String objectName){
        Query query = firebaseFirestore.collection(Constants.POSTS).orderBy("id", Query.Direction.DESCENDING).whereEqualTo("category",objectName).whereArrayContains("tags","Kenya");
        registration = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null){
                for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        Post object = documentChange.getDocument().toObject(Post.class);
                        if (!objectList.contains(object)){
                            objectList.add(object);
                            progressBar.setVisibility(View.GONE);
                            adapter.notifyItemInserted(objectList.size()-1);
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.MODIFIED){
                        Post object = documentChange.getDocument().toObject(Post.class);
                        if (objectList.contains(object)){
                            objectList.set(objectList.indexOf(object),object);
                            adapter.notifyItemChanged(objectList.indexOf(object));
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.REMOVED){
                        Post object = documentChange.getDocument().toObject(Post.class);
                        if (objectList.contains(object)){
                            objectList.remove(object);
                            adapter.notifyItemRemoved(objectList.indexOf(object));
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchObjects(category.getCt());

    }

    @Override
    public void onStop() {
        super.onStop();
        if (registration != null){
            registration.remove();
        }
    }

    @Override
    public void onPostItemClick(Post post, CardView cardView) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.OBJECT,post);
        if (post.getObjectType().equals(Constants.BILL)){
            Intent intent = new Intent(mContext, BillDetail.class);
            intent.putExtras(bundle);
            ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, Pair.create(cardView, post.getId()));
            startActivity(intent,activityOptionsCompat.toBundle());
        } else if(post.getObjectType().equals(Constants.NOTICE)){
            Intent intent = new Intent(mContext, NoticeDetail.class);
            intent.putExtras(bundle);
            ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, Pair.create(cardView, post.getId()));
            startActivity(intent,activityOptionsCompat.toBundle());
        } else if(post.getObjectType().equals(Constants.EVENT)){
            Intent intent = new Intent(mContext, EventDetail.class);
            intent.putExtras(bundle);
            ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, Pair.create(cardView, post.getId()));
            startActivity(intent,activityOptionsCompat.toBundle());
        } else if(post.getObjectType().equals(Constants.ACTIVITY)){
            Intent intent = new Intent(mContext, PostDetail.class);
            intent.putExtras(bundle);
            ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, Pair.create(cardView, post.getId()));
            startActivity(intent,activityOptionsCompat.toBundle());
        } else if(post.getObjectType().equals(Constants.SURVEY)){
            Intent intent = new Intent(mContext, SurveyDetail.class);
            intent.putExtras(bundle);
            ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, Pair.create(cardView, post.getId()));
            startActivity(intent,activityOptionsCompat.toBundle());
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.finishImageView){
            finishAfterTransition();
        }
    }

    @Override
    public void onBrandItemClick(Brand brand, ImageView imageView) {
        Intent intent = new Intent(mContext, BrandProfile.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object",brand);
        intent.putExtras(bundle);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, Pair.create(imageView, brand.getId()));
        startActivity(intent,activityOptionsCompat.toBundle());
    }
}