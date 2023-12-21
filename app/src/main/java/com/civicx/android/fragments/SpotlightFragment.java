package com.civicx.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.civicx.android.PostDetail;
import com.civicx.android.R;
import com.civicx.android.adapters.HistoryAdapter;
import com.civicx.android.constants.Constants;
import com.civicx.android.listeners.BrandItemClickListener;
import com.civicx.android.listeners.PostItemClickListener;
import com.civicx.android.models.Brand;
import com.civicx.android.models.Post;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class SpotlightFragment extends Fragment implements PostItemClickListener, BrandItemClickListener {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ListenerRegistration registration;
    private HistoryAdapter adapter;
    private Brand brand;
    private final List<Post> objectList = new ArrayList<>();

    public SpotlightFragment() {
        // Required empty public constructor
    }

    public static SpotlightFragment getInstance(Brand brand){
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BRANDS, brand);
        SpotlightFragment fragment = new SpotlightFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        brand = (Brand) getArguments().getSerializable(Constants.BRANDS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL));
        adapter = new HistoryAdapter(getContext(), objectList, this, this);
        recyclerView.setAdapter(adapter);
        fetchObjects(brand.getId());
        return view;
    }

    private void fetchObjects(String objectID){
        Query query = firebaseFirestore.collection(Constants.POSTS).orderBy("type", Query.Direction.DESCENDING).whereEqualTo("type",Constants.POST_SPOTLIGHT).whereArrayContains("tags",objectID);
        registration = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null){
                for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        Post object = documentChange.getDocument().toObject(Post.class);
                        if (!objectList.contains(object)){
                            objectList.add(object);
                            adapter.notifyDataSetChanged();
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.MODIFIED){
                        Post object = documentChange.getDocument().toObject(Post.class);
                        if (objectList.contains(object)){
                            objectList.set(objectList.indexOf(object),object);
                            adapter.notifyDataSetChanged();
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.REMOVED){
                        Post object = documentChange.getDocument().toObject(Post.class);
                        if (objectList.contains(object)){
                            objectList.remove(object);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchObjects(brand.getId());
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
        Intent intent = new Intent(getContext(), PostDetail.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.OBJECT,post);
        intent.putExtras(bundle);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), Pair.create(cardView, post.getId()));
        startActivity(intent,activityOptionsCompat.toBundle());
    }

    @Override
    public void onBrandItemClick(Brand brand, ImageView imageView) {
    }
}