package com.civicx.android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.civicx.android.constants.Constants;
import com.civicx.android.models.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class CreateUserProfile extends AppCompatActivity implements View.OnClickListener {

    public final static int PICK_IMAGE = 1;
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final Context mContext = CreateUserProfile.this;
    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private Uri mainImageUri = null;
    private String downloadUrlString = null;

    private SwitchCompat switchCompat;
    private RelativeLayout container;
    private ImageView imageView;
    private TextInputEditText nameTextInputEditText;
    private Spinner ageSpinner;
    private Spinner genderSpinner;
    private AVLoadingIndicatorView progressBar;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_profile);

        TextView privacyTextView = findViewById(R.id.privacyTextView);
        ageSpinner = findViewById(R.id.ageSpinner);
        switchCompat =findViewById(R.id.switchCompat);
        genderSpinner = findViewById(R.id.genderSpinner);
        container = findViewById(R.id.container);
        button = findViewById(R.id.button);
        Button uploadButton = findViewById(R.id.uploadButton);
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);
        nameTextInputEditText = findViewById(R.id.nameTextInputEditText);
        //Glide.with(mContext.getApplicationContext()).load(Objects.requireNonNull(firebaseUser.getPhotoUrl()).toString()).into(imageView);
        nameTextInputEditText.setText(firebaseUser.getDisplayName());
        button.setOnClickListener(this);
        privacyTextView.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:

                String title = Objects.requireNonNull(nameTextInputEditText.getText()).toString().trim();
                String age = ageSpinner.getSelectedItem().toString();
                String gender = genderSpinner.getSelectedItem().toString();

                if (TextUtils.isEmpty(title)) {
                    nameTextInputEditText.setError("Enter your Name");
                    progressBar.setVisibility(View.GONE);
                    break;
                }else if (TextUtils.isEmpty(age)){
                    Snackbar snackbar = Snackbar.make(container,R.string.select_your_age_group, Snackbar.LENGTH_SHORT);snackbar.show();
                    progressBar.setVisibility(View.GONE);
                    break;
                }else if (TextUtils.isEmpty(gender)){
                    Snackbar snackbar = Snackbar.make(container,R.string.select_gender, Snackbar.LENGTH_SHORT);snackbar.show();
                    progressBar.setVisibility(View.GONE);
                    break;

                }else {

                    button.setEnabled(false);
                    createUser(title, firebaseUser.getEmail() ,age,gender, "Kenya");
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                }
            case R.id.privacyTextView:
                String url = "https://civicvoices.wordpress.com/civic-voices-privacy-policy/";
                Intent indie = new Intent(Intent.ACTION_VIEW);
                indie.setData(Uri.parse(url));
                startActivity(indie);
                break;
            case R.id.uploadButton:
                try {
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(CreateUserProfile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else { BringImagePicker();break; }
                }catch (Exception e){ break; }


        }
    }

    private void createUser(String name, String email, String age, String gender, String country){
        try {
            if (downloadUrlString != null) {
                User user = new User(currentUserID,downloadUrlString,name,email,age,switchCompat.isChecked(),null,null,null,null,gender, country,"",1,false, false);
                Bundle updateBundle = new Bundle();
                updateBundle.putSerializable(Constants.OBJECT, user);
                startActivity(new Intent(mContext, MainActivity.class).putExtras(updateBundle));
                finishAfterTransition();
            } else if (mainImageUri != null){
                final StorageReference ref = FirebaseStorage.getInstance().getReference().child(Constants.USERS).child(System.currentTimeMillis() + ".png");
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),mainImageUri);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
                byte[] data = byteArrayOutputStream.toByteArray();
                UploadTask uploadTask = ref.putBytes(data);
                uploadTask.addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();

                        User user = new User(currentUserID,downloadUri.toString(),name,email,age,switchCompat.isChecked(),null,null,null,null,gender, country,"",1,false, false);
                        Bundle updateBundle = new Bundle();
                        updateBundle.putSerializable(Constants.OBJECT, user);
                        startActivity(new Intent(mContext, MainActivity.class).putExtras(updateBundle));
                        finishAfterTransition();
                    }
                })).addOnFailureListener(e -> {
                    Snackbar snackbar = Snackbar.make(container, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT);snackbar.show();
                    progressBar.setVisibility(View.INVISIBLE);
                });
            } else {
                User user = new User(currentUserID,Constants.LOGO_PICTURE,name,email,age, switchCompat.isChecked(),null,null, null,null,gender, country,"",1,false, false);
                Bundle updateBundle = new Bundle();
                updateBundle.putSerializable(Constants.OBJECT, user);
                startActivity(new Intent(mContext, MainActivity.class).putExtras(updateBundle));
                finishAfterTransition();
            }
        } catch (IOException e){e.printStackTrace(); }
    }

    private void BringImagePicker() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Profile Pic"),PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode ==PICK_IMAGE && resultCode == RESULT_OK && data != null){
                imageView.setImageURI(data.getData());
                mainImageUri = data.getData();
                postImage(mainImageUri);
            }
        }catch (Exception e){e.printStackTrace(); }
    }

    private void postImage(Uri mainImageUri){
        try {
            final StorageReference ref = FirebaseStorage.getInstance().getReference().child(Constants.USERS).child(System.currentTimeMillis() + ".png");
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),mainImageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();
            UploadTask uploadTask = ref.putBytes(data);
            uploadTask.addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    downloadUrlString = downloadUri.toString();
                }
            })).addOnFailureListener(e -> {
                Snackbar snackbar = Snackbar.make(container, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT);snackbar.show();
                progressBar.setVisibility(View.INVISIBLE);
                button.setEnabled(true);
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
