package com.civicx.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.civicx.android.adapters.SectionAdapter;
import com.civicx.android.constants.Constants;
import com.civicx.android.listeners.StringItemClickListener;
import com.civicx.android.models.Post;
import com.civicx.android.models.Survey;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Objects;

public class CreateRadio extends AppCompatActivity implements View.OnClickListener, StringItemClickListener {

    private final Context mContext = CreateRadio.this;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private TextInputEditText titleTextInputEditText;
    private TextInputEditText summaryTextView;
    private AVLoadingIndicatorView progressBar;
    private Post post;
    private final ArrayList<String> questions = new ArrayList<>();
    private SectionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_radio);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        post = (Post) bundle.getSerializable(Constants.OBJECT);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_yellow_24dp);
        toolbar.setNavigationOnClickListener(v -> finishAfterTransition());

        progressBar = findViewById(R.id.progressBar);
        titleTextInputEditText = findViewById(R.id.titleTextInputEditText);
        summaryTextView = findViewById(R.id.summaryTextView);
        Button button = findViewById(R.id.button);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.HORIZONTAL));
        adapter = new SectionAdapter(mContext, questions, this);
        recyclerView.setAdapter(adapter);
        button.setOnClickListener(this);

        summaryTextView.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                String section = summaryTextView.getText().toString();
                if (!questions.contains(section)){
                    questions.add(section);
                    adapter.notifyDataSetChanged();
                } else {
                    summaryTextView.requestFocus();
                    summaryTextView.setError("Enter new Section");
                }
                summaryTextView.setText("");
                return true;
            }
            return false;
        });

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button) {
            String title = titleTextInputEditText.getText().toString();

            if (TextUtils.isEmpty(title)){
                titleTextInputEditText.setError("Enter Survey Title");
                progressBar.setVisibility(View.GONE);
            } else if (questions.size()<=1){
                Toast.makeText(mContext,"Add Survey Options",Toast.LENGTH_SHORT).show();
                progressBar.show();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                ArrayList<String> tags = new ArrayList<>();
                ArrayList<String> participants = new ArrayList<>();
                tags.add(Constants.SURVEYS);
                tags.add(post.getId());
                String surveyID = firebaseFirestore.collection(Constants.SURVEYS).document().getId();
                Survey survey = new Survey(surveyID, post.getId(), title,title,Constants.RADIO,System.currentTimeMillis(),questions,participants,tags);
                firebaseFirestore.collection(Constants.SURVEYS).document(surveyID).set(survey).addOnSuccessListener(unused -> {
                    progressBar.setVisibility(View.GONE);
                    finishAfterTransition();
                });

            }
        }
    }

    @Override
    public void onStringItemClick(String topic) {
        if (questions.contains(topic)){
            questions.remove(topic);
            adapter.notifyItemRemoved(questions.indexOf(topic));
        }
    }
}