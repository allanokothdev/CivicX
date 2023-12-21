package com.civicx.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.civicx.android.R;
import com.civicx.android.adapters.CommentAdapter;
import com.civicx.android.constants.Constants;
import com.civicx.android.models.Comments;
import com.civicx.android.models.Post;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AssentedFragment extends Fragment {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ListenerRegistration registration;
    private CommentAdapter adapter;
    private Post post;
    private final List<Comments> objectList = new ArrayList<>();

    public AssentedFragment() {
        // Required empty public constructor
    }

    public static AssentedFragment getInstance(Post post){
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.POSTS, post);
        AssentedFragment fragment = new AssentedFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        post = (Post) getArguments().getSerializable(Constants.POSTS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);

        try {
            RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

            DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
            divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(requireContext(), R.drawable.posts_divider)));
            recyclerView.addItemDecoration(divider);
            adapter = new CommentAdapter(requireContext(),objectList, post);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
            fetchObjects(post.getId());
        } catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }

    private void fetchObjects(String objectID){
        Query query = firebaseFirestore.collection(Constants.COMMENTS).orderBy("timestamp", Query.Direction.DESCENDING).whereEqualTo("parentID",objectID).whereEqualTo("stage",Constants.APPROVED);
        registration = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null){
                for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        Comments object = documentChange.getDocument().toObject(Comments.class);
                        if (!objectList.contains(object)){
                            objectList.add(object);
                            adapter.notifyDataSetChanged();
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.MODIFIED){
                        Comments object = documentChange.getDocument().toObject(Comments.class);
                        if (objectList.contains(object)){
                            objectList.set(objectList.indexOf(object),object);
                            adapter.notifyItemChanged(objectList.indexOf(object));
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.REMOVED){
                        Comments object = documentChange.getDocument().toObject(Comments.class);
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
    public void onStop() {
        super.onStop();
        if (registration != null){
            registration.remove();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchObjects(post.getId());
    }

}