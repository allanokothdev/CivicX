package com.civicx.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.civicx.android.constants.Constants;
import com.civicx.android.models.Comments;
import com.civicx.android.models.Demographic;
import com.civicx.android.models.Person;
import com.civicx.android.models.Post;
import com.civicx.android.models.Response;
import com.civicx.android.models.User;
import com.civicx.android.utils.GetUser;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class CreateSummary extends AppCompatActivity implements View.OnClickListener {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final Context mContext = CreateSummary.this;
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private TextInputEditText sectionTextInputEditText;
    private TextInputEditText summaryTextInputText;
    private AVLoadingIndicatorView progressBar;
    private Post post;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_summary);

        user = GetUser.getUser(mContext, currentUserID);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        post = (Post) bundle.getSerializable(Constants.OBJECT);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_yellow_24dp);
        toolbar.setNavigationOnClickListener(v -> finishAfterTransition());

        ImageView imageView = findViewById(R.id.profileImageView);
        TextView textView = findViewById(R.id.textView);
        TextView subTextView = findViewById(R.id.subTextView);
        TextView subItemTextView = findViewById(R.id.subItemTextView);
        progressBar = findViewById(R.id.progressBar);
        sectionTextInputEditText = findViewById(R.id.sectionTextInputEditText);
        summaryTextInputText = findViewById(R.id.summaryTextInputText);
        subItemTextView.setOnClickListener(this);
        Glide.with(mContext.getApplicationContext()).load(user.getPic()).placeholder(R.drawable.placeholder).into(imageView);
        textView.setText(user.getName());
        subTextView.setText(getTime(System.currentTimeMillis()));

        Glide.with(mContext.getApplicationContext()).load(user.getPic()).placeholder(R.drawable.placeholder).into(imageView);
        textView.setText(user.getName());
        subTextView.setText(getTime(System.currentTimeMillis()));
    }


    private String getTime(long time) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time);
        return DateFormat.format("EEEE, dd MMMM yyyy", cal).toString();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.subItemTextView) {
            String text = summaryTextInputText.getText().toString().trim();
            String section = sectionTextInputEditText.getText().toString().trim();
            if (TextUtils.isEmpty(text)) {
                summaryTextInputText.setError("Paste Youtube Video link here");
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.VISIBLE);

                String commentID = firebaseFirestore.collection(Constants.COMMENTS).document().getId();
                Person person = new Person(currentUserID,user.getPic(),user.getName(),user.getCountry(),user.getToken());
                ArrayList<String> upvotes = new ArrayList<>();
                ArrayList<String> downvotes = new ArrayList<>();
                ArrayList<String> tags = new ArrayList<>();
                ArrayList<Response> responses = new ArrayList<>();
                tags.add(Constants.SUMMARY);
                tags.add(post.getId());
                Demographic demographic = new Demographic(currentUserID,user.getAge(),user.isDisability(),user.getWard(),user.getSubCounty(),user.getConstituency(),user.getCounty(),user.getGender(),user.getCountry());
                Comments comments = new Comments(commentID, post.getId(),person,currentUserID,false,section,section,section,text,"Published",System.currentTimeMillis(),1,false,demographic,tags);
                firebaseFirestore.collection(Constants.COMMENTS).document(commentID).set(comments).addOnSuccessListener(unused -> {
                    progressBar.setVisibility(View.GONE);
                    finishAfterTransition();
                });
            }
        }
    }

}