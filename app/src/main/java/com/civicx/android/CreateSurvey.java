package com.civicx.android;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.civicx.android.constants.Constants;
import com.civicx.android.models.Brand;
import com.civicx.android.models.Person;
import com.civicx.android.models.Post;
import com.civicx.android.utils.CustomDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class CreateSurvey extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private RelativeLayout container;
    private final Context mContext = CreateSurvey.this;
    private long selectedDate = System.currentTimeMillis();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private Button button;
    private Button dateButton;
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private TextInputEditText titleTextInputEditText;
    private TextInputEditText summaryInputEditText;
    private TextInputEditText linkInputEditText;
    private Spinner topicSpinner;
    private AVLoadingIndicatorView progressBar;
    private Brand brand;
    private ArrayList<String> sections = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_survey);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        brand = (Brand) bundle.getSerializable(Constants.OBJECT);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_yellow_24dp);
        toolbar.setNavigationOnClickListener(v -> finishAfterTransition());

        container = findViewById(R.id.container);
        topicSpinner = findViewById(R.id.topicSpinner);
        button = findViewById(R.id.button);
        dateButton = findViewById(R.id.dateButton);
        linkInputEditText = findViewById(R.id.linkInputEditText);
        titleTextInputEditText = findViewById(R.id.titleTextInputEditText);
        summaryInputEditText = findViewById(R.id.summaryInputEditText);
        progressBar = findViewById(R.id.progressBar);
        button.setOnClickListener(this);
        dateButton.setOnClickListener(this);
    }

    @TargetApi(23)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dateButton){
            CustomDatePicker customDatePicker = new CustomDatePicker();
            customDatePicker.show(getSupportFragmentManager(), "DATE PICK");
        } else if (v.getId() == R.id.button) {

            String title = titleTextInputEditText.getText().toString().trim();
            String summary = summaryInputEditText.getText().toString().trim();
            String link = linkInputEditText.getText().toString().trim();
            String topic = topicSpinner.getSelectedItem().toString();
            progressBar.setVisibility(View.VISIBLE);

            if (TextUtils.isEmpty(title)) {

                titleTextInputEditText.setError(getString(R.string.enter_bill_title));
                progressBar.setVisibility(View.GONE);

            } else if (TextUtils.isEmpty(summary)) {

                summaryInputEditText.setError(getString(R.string.enter_bill_summary));
                progressBar.setVisibility(View.GONE);

            } else if (TextUtils.isEmpty(link)) {

                summaryInputEditText.setError(getString(R.string.enter_bill_weblink));
                progressBar.setVisibility(View.GONE);

            } else if (TextUtils.isEmpty(topic)) {

                Snackbar.make(container, getString(R.string.select_topic), Snackbar.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);

            } else {

                button.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                String billID = firebaseFirestore.collection(Constants.POSTS).document().getId();

                sections.add("Section 1");

                ArrayList<String> comments = new ArrayList<>();
                ArrayList<String> photos = new ArrayList<>();
                ArrayList<String> upvotes = new ArrayList<>();
                ArrayList<String> downvotes = new ArrayList<>();
                ArrayList<String> bookmarks = new ArrayList<>();

                @SuppressLint("SimpleDateFormat") String today = new SimpleDateFormat("ddMMMM").format(selectedDate);
                ArrayList<String> tags = new ArrayList<>();
                tags.add(brand.getCountry());
                tags.add(topic);
                tags.add(brand.getId());
                tags.add(currentUserID);
                tags.add(brand.getCategory());
                tags.add(brand.getType());
                tags.add(today);
                tags.add(getTime(selectedDate));
                Person person = new Person(brand.getId(),brand.getPic(),brand.getTitle(), brand.getSummary(),brand.getToken());
                Post post = new Post(billID,billID,"Survey",title,summary,link,Constants.SURVEY,getTime(selectedDate),selectedDate,System.currentTimeMillis(),brand,person,brand.getType(), brand.getCountry(),brand.getCategory(),0,0,photos,sections,tags,bookmarks);

                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.OBJECT,post);
                //startActivity(new Intent(mContext, SelectRegions.class).putExtras(bundle));
                finishAfterTransition();
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    private String getTime(long time){
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time);
        return DateFormat.format("EEEE, dd MMMM yyyy", cal).toString();
    }

    private String getDate(long time){
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time);
        return DateFormat.format("dd MMMM yyyy", cal).toString();
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        view.setMaxDate(System.currentTimeMillis());
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        selectedDate = mCalendar.getTimeInMillis();
        dateButton.setText(getDate(selectedDate));
    }
}
