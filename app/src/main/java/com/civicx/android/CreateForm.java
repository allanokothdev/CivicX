package com.civicx.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.civicx.android.constants.Constants;
import com.civicx.android.models.Post;
import com.civicx.android.models.Survey;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Objects;

public class CreateForm extends AppCompatActivity implements View.OnClickListener {

    private final Context mContext = CreateForm.this;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private TextInputEditText titleTextInputEditText;
    private AVLoadingIndicatorView progressBar;
    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_form);

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
        Button button = findViewById(R.id.button);
        button.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button) {
            String title = titleTextInputEditText.getText().toString();

            if (TextUtils.isEmpty(title)){
                titleTextInputEditText.setError("Enter Survey Title");
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.VISIBLE);
                ArrayList<String> tags = new ArrayList<>();
                ArrayList<String> participants = new ArrayList<>();
                tags.add(Constants.SURVEYS);
                tags.add(post.getId());
                String surveyID = firebaseFirestore.collection(Constants.SURVEYS).document().getId();
                Survey survey = new Survey(surveyID, post.getId(), title,title,Constants.FORM,System.currentTimeMillis(),null,participants,tags);
                firebaseFirestore.collection(Constants.SURVEYS).document(surveyID).set(survey).addOnSuccessListener(unused -> {
                    progressBar.setVisibility(View.GONE);
                    finishAfterTransition();
                });
            }
        }
    }

}