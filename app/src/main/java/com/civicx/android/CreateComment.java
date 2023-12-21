package com.civicx.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.civicx.android.constants.Constants;
import com.civicx.android.models.Comments;
import com.civicx.android.models.Demographic;
import com.civicx.android.models.Notification;
import com.civicx.android.models.Person;
import com.civicx.android.models.Post;
import com.civicx.android.models.Reply;
import com.civicx.android.models.Response;
import com.civicx.android.models.User;
import com.civicx.android.utils.GetUser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class CreateComment extends AppCompatActivity implements View.OnClickListener {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final Context mContext = CreateComment.this;
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private Spinner topicSpinner;
    private Spinner optionSpinner;
    private TextInputEditText reasonTextInputEditText;
    private TextInputEditText recommendationTextInputText;
    private SwitchCompat switchCompat;
    private AVLoadingIndicatorView progressBar;
    private Post post;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_comment);

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
        switchCompat =findViewById(R.id.switchCompat);
        TextView textView = findViewById(R.id.textView);
        TextView subTextView = findViewById(R.id.subTextView);
        TextView subItemTextView = findViewById(R.id.subItemTextView);
        progressBar = findViewById(R.id.progressBar);
        optionSpinner = findViewById(R.id.optionSpinner);
        topicSpinner = findViewById(R.id.topicSpinner);
        reasonTextInputEditText = findViewById(R.id.reasonTextInputText);
        recommendationTextInputText = findViewById(R.id.recommendationTextInputText);
        subItemTextView.setOnClickListener(this);
        Glide.with(mContext.getApplicationContext()).load(user.getPic()).placeholder(R.drawable.placeholder).into(imageView);
        textView.setText(user.getName());
        subTextView.setText(getTime(System.currentTimeMillis()));

        reasonTextInputEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        reasonTextInputEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);

        recommendationTextInputText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        recommendationTextInputText.setRawInputType(InputType.TYPE_CLASS_TEXT);

        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                Glide.with(mContext.getApplicationContext()).load(Constants.LOGO_PICTURE).placeholder(R.drawable.placeholder).into(imageView);
                textView.setText(R.string.concerned_citizen);
            } else {
                Glide.with(mContext.getApplicationContext()).load(user.getPic()).placeholder(R.drawable.placeholder).into(imageView);
                textView.setText(user.getName());
            }
            subTextView.setText(getTime(System.currentTimeMillis()));
        });
    }



    private String getTime(long time){
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time);
        return DateFormat.format("EEEE, dd MMMM yyyy", cal).toString();
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.subItemTextView){
            String option = optionSpinner.getSelectedItem().toString();
            String topic = topicSpinner.getSelectedItem().toString();
            String reason = reasonTextInputEditText.getText().toString().trim();
            String recommendation = recommendationTextInputText.getText().toString().trim();
            if (TextUtils.isEmpty(option)) {
                Toast.makeText(mContext, "Specify option", Toast.LENGTH_SHORT).show();
                optionSpinner.requestFocus();
                progressBar.setVisibility(View.GONE);
            } else if (TextUtils.isEmpty(topic)){
                Toast.makeText(mContext, "Specify section", Toast.LENGTH_SHORT).show();
                topicSpinner.requestFocus();
                progressBar.setVisibility(View.GONE);
            } else if (TextUtils.isEmpty(reason)){
                reasonTextInputEditText.setError("Please enter reason why you agree/disagree");
                reasonTextInputEditText.requestFocus();
                progressBar.setVisibility(View.GONE);
            } else if (TextUtils.isEmpty(recommendation)){
                recommendationTextInputText.setError("Please enter your recommendation");
                recommendationTextInputText.requestFocus();
                progressBar.setVisibility(View.GONE);
            }else {
                progressBar.setVisibility(View.VISIBLE);

                String commentID = firebaseFirestore.collection(Constants.COMMENTS).document().getId();
                Person person = new Person(currentUserID,user.getPic(),user.getName(),user.getCountry(),user.getToken());
                ArrayList<String> upvotes = new ArrayList<>();
                ArrayList<String> downvotes = new ArrayList<>();
                ArrayList<String> tags = new ArrayList<>();
                ArrayList<Response> responses = new ArrayList<>();
                tags.add(Constants.COMMENTS);
                tags.add(post.getId());
                tags.add(currentUserID);
                Demographic demographic = new Demographic(currentUserID,user.getAge(),user.isDisability(),user.getWard(),user.getSubCounty(),user.getConstituency(),user.getCounty(),user.getGender(),user.getCountry());
                Comments commentObject = new Comments(commentID, post.getId(),person,currentUserID,switchCompat.isChecked(),option,topic,reason,recommendation,"Published",System.currentTimeMillis(),1,false,demographic,tags);

                firebaseFirestore.collection(Constants.COMMENTS).document(commentID).set(commentObject).addOnSuccessListener(unused -> {
                    if (!switchCompat.isChecked()){
                        Reply reply = new Reply(commentID, post.getId(),commentObject.getCd(),currentUserID,System.currentTimeMillis());
                        FirebaseFirestore.getInstance().collection(Constants.REPLY).document(commentID).set(reply).addOnSuccessListener(unused2 -> {
                            addParticipation(post);
                            //addUpCommentsCount(post.getId());
                            createNotification(post.getBrand().getToken(),user, post,commentObject);
                            progressBar.setVisibility(View.GONE);
                            finishAfterTransition();
                        });
                    } else {
                        addParticipation(post);
                        //addUpCommentsCount(post.getId());
                        createNotification(post.getBrand().getToken(),user, post,commentObject);
                        progressBar.setVisibility(View.GONE);
                        finishAfterTransition();
                    }
                });
            }
        }
    }


    private void createNotification(String token, User user, Post post, Comments comments){
        String title = user.getName()+ " added their voice to the "+post.getTitle();
        String notificationID = firebaseFirestore.collection(Constants.TOKEN_NOTIFICATION).document().getId();
        Notification notification = new Notification(notificationID,user.getPic(),title,comments.getRecommendation(),post.getId(),post.getObjectType(),token);
        firebaseFirestore.collection(Constants.TOKEN_NOTIFICATION).document(notificationID).set(notification).addOnSuccessListener(unused -> { });
    }

    private void addUpCommentsCount(final String objectID){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comments",FieldValue.arrayUnion(currentUserID));
        firebaseFirestore.collection(Constants.COMMENTS).document(objectID).update(hashMap).addOnSuccessListener((OnSuccessListener) o -> {

        });
    }

    private void addParticipation(Post post){
        DocumentReference postReference = firebaseFirestore.collection(Constants.POSTS).document(post.getId());
        postReference.update("participants", FieldValue.arrayUnion(currentUserID)).addOnSuccessListener(unused -> {

        });
    }

    private void addUserStars(final String objectID){
        DocumentReference reference = firebaseFirestore.collection(Constants.USERS).document(objectID);
        firebaseFirestore.runTransaction(transaction -> {
            DocumentSnapshot documentSnapshot = transaction.get(reference);
            double newCount = documentSnapshot.getDouble("stars")+5;
            transaction.update(reference,"stars",newCount);
            Toast.makeText(mContext, "You've been awarded 5 Stars", Toast.LENGTH_SHORT).show();
            return null;
        });
    }
}