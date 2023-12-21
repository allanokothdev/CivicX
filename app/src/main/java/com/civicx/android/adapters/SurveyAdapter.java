package com.civicx.android.adapters;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.civicx.android.R;
import com.civicx.android.constants.Constants;
import com.civicx.android.models.Demographic;
import com.civicx.android.models.Person;
import com.civicx.android.models.Post;
import com.civicx.android.models.Submission;
import com.civicx.android.models.Survey;
import com.civicx.android.models.User;
import com.civicx.android.utils.GetUser;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SurveyAdapter extends RecyclerView.Adapter {
    private final Context mContext;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final List<Survey> stringList;
    private final Post post;
    public static final int RADIO = 0;
    public static final int FORM = 1;

    public SurveyAdapter(Context mContext, List<Survey> stringList, Post post){
        this.mContext = mContext;
        this.stringList = stringList;
        this.post = post;
    }

    @Override
    public int getItemViewType(int position) {
        Survey survey = stringList.get(position);
        if (survey.getType().equals(Constants.FORM)){
            return FORM;
        } else {
            return RADIO;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == FORM){
            return new FormViewHolder(LayoutInflater.from(mContext).inflate(R.layout.form_item, parent, false));
        } else {
            return new RadioViewHolder(LayoutInflater.from(mContext).inflate(R.layout.survey_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        Survey survey = stringList.get(position);
        if (survey.getType().equals(Constants.FORM)){
            ((FormViewHolder) holder).bind(position);
        } else if (survey.getType().equals(Constants.RADIO)){
            ((RadioViewHolder) holder).bind(position);
        }
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public class RadioViewHolder extends RecyclerView.ViewHolder{

        private final ImageView imageView;
        private final TextView textView;
        private final Spinner spinner;
        private boolean isSpinnerTouched = false;

        private final Button button;

        private RadioViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            spinner = itemView.findViewById(R.id.spinner);
            button = itemView.findViewById(R.id.button);
        }

        void bind(int position) {

            final Survey survey = stringList.get(position);
            Glide.with(mContext.getApplicationContext()).load(post.getPerson().getPic()).placeholder(R.drawable.placeholder).into(imageView);
            textView.setText(survey.getTitle());

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, survey.getQuestions());
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(dataAdapter);

            button.setOnClickListener(view -> {
                String topic = spinner.getSelectedItem().toString();
                if (!TextUtils.isEmpty(topic)){
                    postListResponse(topic, survey);
                } else {
                    Toast.makeText(mContext,"Please select a response",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public class FormViewHolder extends RecyclerView.ViewHolder{

        private final ImageView imageView;
        private final TextView textView;
        private final TextInputEditText summaryTextInputText;

        private final Button button;

        private FormViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            summaryTextInputText = itemView.findViewById(R.id.summaryTextInputText);
            button = itemView.findViewById(R.id.button);
        }

        void bind(int position) {

            final Survey survey = stringList.get(position);
            Glide.with(mContext.getApplicationContext()).load(post.getPerson().getPic()).placeholder(R.drawable.placeholder).into(imageView);
            textView.setText(survey.getTitle());
            summaryTextInputText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            summaryTextInputText.setRawInputType(InputType.TYPE_CLASS_TEXT);

            button.setOnClickListener(view -> {
                String topic = summaryTextInputText.getText().toString();
                if (!TextUtils.isEmpty(topic)){
                    postSurveyResponse(survey,topic,summaryTextInputText);
                } else {
                    Toast.makeText(mContext,"Please select a response",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void postSurveyResponse(Survey survey, String response, TextInputEditText textInputEditText){
        ArrayList<String> tags = new ArrayList<>();
        tags.add(post.getId());
        tags.add(response);
        tags.add(survey.getId());
        String questionID = firebaseFirestore.collection(Constants.SUBMISSIONS).document().getId();
        User user = GetUser.getUser(mContext, currentUserID);
        Person person = new Person(currentUserID,user.getPic(),user.getName(),user.getCountry(),user.getToken());
        Demographic demographic = new Demographic(currentUserID,user.getAge(),user.isDisability(),user.getWard(),user.getSubCounty(),user.getConstituency(),user.getCounty(),user.getGender(),user.getCountry());
        Submission submission = new Submission(questionID,false,person, survey.getTitle(),response,System.currentTimeMillis(),demographic,tags);
        firebaseFirestore.collection(Constants.SUBMISSIONS).document(questionID).set(submission).addOnSuccessListener(unused -> {
            textInputEditText.setText("");
            Toast.makeText(mContext,"Successful Submission",Toast.LENGTH_SHORT).show();

        });
    }

    private void postListResponse(String topic, Survey survey){
        ArrayList<String> tags = new ArrayList<>();
        tags.add(post.getId());
        tags.add(topic);
        tags.add(survey.getId());
        User user = GetUser.getUser(mContext, currentUserID);
        Person person = new Person(currentUserID,user.getPic(),user.getName(),user.getCountry(),user.getToken());
        Demographic demographic = new Demographic(currentUserID,user.getAge(),user.isDisability(),user.getWard(),user.getSubCounty(),user.getConstituency(),user.getCounty(),user.getGender(),user.getCountry());
        String questionID = firebaseFirestore.collection(Constants.SUBMISSIONS).document().getId();
        Submission submission = new Submission(questionID,false,person, survey.getTitle(),topic,System.currentTimeMillis(),demographic,tags);
        firebaseFirestore.collection(Constants.SUBMISSIONS).document(questionID).set(submission).addOnSuccessListener(unused -> {  Toast.makeText(mContext,"Successful Submission",Toast.LENGTH_SHORT).show(); });
    }

    private void addSurveyParticipation(Survey survey){
        if (!survey.getParticipants().contains(currentUserID)){
            DocumentReference postReference = firebaseFirestore.collection(Constants.SURVEYS).document(survey.getId());
            postReference.update("participants", FieldValue.arrayUnion(currentUserID)).addOnSuccessListener(unused -> {});
        }
    }

    private void addPostParticipation(Post post){
        if (post.getBookmarks().contains(currentUserID)){
            DocumentReference postReference = firebaseFirestore.collection(Constants.POSTS).document(post.getId());
            postReference.update("comment", FieldValue.arrayUnion(currentUserID)).addOnSuccessListener(unused -> {});
        }
    }



}
