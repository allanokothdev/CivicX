package com.civicx.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.civicx.android.adapters.CategoryAdapter;
import com.civicx.android.adapters.PostAdapter;
import com.civicx.android.constants.Constants;
import com.civicx.android.listeners.BrandItemClickListener;
import com.civicx.android.listeners.CategoryItemClickListener;
import com.civicx.android.listeners.PostItemClickListener;
import com.civicx.android.models.Brand;
import com.civicx.android.models.Category;
import com.civicx.android.models.Post;
import com.civicx.android.models.Token;
import com.civicx.android.models.User;
import com.civicx.android.utils.GetUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, PostItemClickListener, CategoryItemClickListener, BrandItemClickListener {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final String TAG = this.getClass().getSimpleName();
    private long pressedTime;
    private final Context mContext = MainActivity.this;
    private ImageView moreImageView;
    private User user;

    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ListenerRegistration registration;
    private ListenerRegistration listenerRegistration;
    private final List<Post> objectList = new ArrayList<>();
    private PostAdapter adapter;
    private AVLoadingIndicatorView progressBar;

    private CategoryAdapter categoryAdapter;
    private final List<Category> categoryArrayList = new ArrayList<>();

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = GetUser.getUser(mContext, currentUserID);

        RecyclerView categoryRecyclerView = findViewById(R.id.categoriesRecycleView);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(mContext,RecyclerView.HORIZONTAL,false));
        categoryAdapter = new CategoryAdapter(mContext, categoryArrayList, this);
        categoryRecyclerView.setHasFixedSize(true);
        categoryRecyclerView.setAdapter(categoryAdapter);
        getCategories(user.getCountry());

        progressBar = findViewById(R.id.progressBar);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        adapter = new PostAdapter(mContext, objectList, this, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.VISIBLE);
        fetchObjects(user.getCounty());
        moreImageView = findViewById(R.id.moreImageView);
        moreImageView.setOnClickListener(this);
        fetchUserInfo(currentUserID);
    }

    private void getCategories(String country){
        Query query = firebaseFirestore.collection(Constants.CATEGORIES).orderBy("rank", Query.Direction.DESCENDING).whereArrayContains("tags","Kenya");
        listenerRegistration = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null){
                for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        Category object = documentChange.getDocument().toObject(Category.class);
                        if (!categoryArrayList.contains(object)){
                            categoryArrayList.add(object);
                            progressBar.setVisibility(View.GONE);
                            categoryAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });

    }


    private void fetchObjects(String objectID){
        Query query = firebaseFirestore.collection(Constants.POSTS).orderBy("publishedDate", Query.Direction.DESCENDING).whereArrayContains("tags",objectID);
        registration = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null){
                for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        Post object = documentChange.getDocument().toObject(Post.class);
                        if (!objectList.contains(object)){
                            objectList.add(object);
                            progressBar.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
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
        fetchObjects(user.getCounty());
        getCategories(user.getCountry());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (registration != null){
            registration.remove();
        } else if (listenerRegistration != null){
            listenerRegistration.remove();
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
    public void onCategoryItemClick(Category category, CardView cardView) {
        Intent intent = new Intent(mContext, Categories.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.OBJECT,category);
        intent.putExtras(bundle);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, Pair.create(cardView, category.getCt()));
        startActivity(intent,activityOptionsCompat.toBundle());
    }

    @Override
    public void onBrandItemClick(Brand brand, ImageView imageView) {
        Intent intent = new Intent(mContext, BrandProfile.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.OBJECT,brand);
        intent.putExtras(bundle);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, Pair.create(imageView, brand.getId()));
        startActivity(intent,activityOptionsCompat.toBundle());
    }



    @Override public void onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
            return;
        }else {
            Toast.makeText(mContext, "Press Back Again to Exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();

    }

    private void fetchUserInfo(String currentUserID){
        FirebaseFirestore.getInstance().collection(Constants.USERS).document(currentUserID).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                user = documentSnapshot.toObject(User.class);
                assert user != null;
                GetUser.saveUser(mContext,user);

                subscribeTopic(currentUserID);
                subscribeTopic(Constants.NATIONAL_ASSEMBLY);
                subscribeTopic(Constants.SENATE);
                subscribeTopic(Constants.COUNTY_ASSEMBLY);
                subscribeTopic(user.getCounty());
                subscribeTopic(Constants.NATIONAL);
                subscribeTopic(Constants.NGO);
                subscribeTopic(Constants.MINISTRY);
                subscribeTopic(user.getGender());

                SharedPreferences.Editor localeEditor = getSharedPreferences(Constants.COUNTY,Context.MODE_PRIVATE).edit();
                localeEditor.putString(Constants.COUNTY, user.getCounty());
                localeEditor.apply();


            } else {
                startActivity(new Intent(mContext, UpdateUserProfile.class));
            }
        });
    }


    private void fetchToken(User user){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                String token = task.getResult();
                String msg = getString(R.string.msg_token_fmt, token);
                Log.d(TAG, msg);

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("token",token);
                FirebaseDatabase.getInstance().getReference(Constants.TOKEN).child(currentUserID).updateChildren(hashMap);

                Token groupToken = new Token(token);
                //FirebaseDatabase.getInstance().getReference(Constants.GROUPS_TOKENS).child(user.getBorough()).child(user.getId()).setValue(groupToken);
                FirebaseDatabase.getInstance().getReference(Constants.GROUPS_TOKENS).child(user.getConstituency()).child(user.getId()).setValue(groupToken);
                FirebaseDatabase.getInstance().getReference(Constants.GROUPS_TOKENS).child(user.getSubCounty()).child(user.getId()).setValue(groupToken);
                FirebaseDatabase.getInstance().getReference(Constants.GROUPS_TOKENS).child(user.getWard()).child(user.getId()).setValue(groupToken);
                FirebaseDatabase.getInstance().getReference(Constants.GROUPS_TOKENS).child(user.getCounty()).child(user.getId()).setValue(groupToken);
                FirebaseDatabase.getInstance().getReference(Constants.GROUPS_TOKENS).child(user.getCountry()).child(user.getId()).setValue(groupToken);
                FirebaseDatabase.getInstance().getReference(Constants.GROUPS_TOKENS).child(user.getGender()).child(user.getId()).setValue(groupToken);

                SharedPreferences.Editor editor = getSharedPreferences(Constants.USERS,Context.MODE_PRIVATE).edit();
                editor.putString(Constants.TOKEN, token);
                editor.apply();

                GetUser.saveObject(mContext,Constants.TOKEN,token);

                HashMap<String, Object> userMap = new HashMap<>();
                userMap.put("token",token);
                FirebaseFirestore.getInstance().collection(Constants.USERS).document(currentUserID).update(userMap);

            }else {
                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
            }
        });
    }

    private void subscribeTopic(String topic){
        FirebaseMessaging.getInstance().subscribeToTopic(topic).addOnCompleteListener(task -> { });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.moreImageView) {
            PopupMenu popupMen = new PopupMenu(mContext, moreImageView);
            popupMen.getMenuInflater().inflate(R.menu.menu_main, popupMen.getMenu());
            popupMen.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.action_edit:
                        if (user != null){
                            Bundle updateBundle = new Bundle();
                            updateBundle.putSerializable(Constants.OBJECT, user);
                            //startActivity(new Intent(mContext, EditUserProfile.class).putExtras(updateBundle));
                        }
                        break;
                    case R.id.action_governance:
                        if (user != null){
                            Bundle updateBundle = new Bundle();
                            updateBundle.putSerializable(Constants.OBJECT, user);
                            //startActivity(new Intent(mContext, UpdateGovernance.class).putExtras(updateBundle));
                        }
                        break;
                    case R.id.action_search:
                        //startActivity(new Intent(mContext, Search.class));
                        break;
                    case R.id.action_profile:
                        if (user != null){
                            Bundle updateBundle = new Bundle();
                            updateBundle.putSerializable(Constants.OBJECT, user);
                            //startActivity(new Intent(mContext, UserProfile.class).putExtras(updateBundle));
                        }
                        break;
                    case R.id.action_sign_out:
                        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Sign Out");
                        builder.setCancelable(true);
                        builder.setPositiveButton("Yes", (dialog, which) -> {
                            firebaseAuth.signOut();
                            finishAfterTransition();
                        });
                        builder.setNegativeButton("Hell No", (dialog, which) -> {
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        break;
                }
                return true;
            });
            popupMen.show();
        }
    }
}