package com.civicx.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.civicx.android.constants.Constants;
import com.civicx.android.models.Post;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Objects;

public class Splash extends AppCompatActivity implements View.OnClickListener{

    private final Context mContext = Splash.this;
    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private GoogleSignInClient googleSignInClient;
    private final String TAG = this.getClass().getSimpleName();
    private LinearLayout linearLayout;
    private AVLoadingIndicatorView progressBar;
    private static final int RC_SIGN_IN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView loginTextView = findViewById(R.id.loginTextView);
        progressBar = findViewById(R.id.progressBar);
        linearLayout = findViewById(R.id.linearLayout);
        Button googleButton = findViewById(R.id.googleButton);
        Button emailButton = findViewById(R.id.emailButton);
        googleButton.setOnClickListener(this);
        emailButton.setOnClickListener(this);
        loginTextView.setOnClickListener(this);

        getState();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(mContext, googleSignInOptions);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "Google sign in success");
                firebaseAuthWithGoogle(account);
            }catch (ApiException e){
                progressBar.setVisibility(View.GONE);
                Log.w(TAG, "Google sign in failed",e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        boolean isNewUser = Objects.requireNonNull(task.getResult().getAdditionalUserInfo()).isNewUser();
                        sendUserToCreateActivity(isNewUser);
                    }else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(mContext,Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()),Toast.LENGTH_SHORT).show();
                    }
                });
    }



    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.googleButton) {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
            progressBar.setVisibility(View.VISIBLE);
        } else if (v.getId()==R.id.emailButton){
            startActivity(new Intent(mContext, SignUp.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        } else if (v.getId()==R.id.loginTextView){
            startActivity(new Intent(mContext, Login.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
    }

    private void hideSystemUi(){
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            hideSystemUi();
        }
    }

    private void sendUserToCreateActivity(boolean status){
        if (status){
            startActivity(new Intent(mContext, CreateUserProfile.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }else {
            startActivity(new Intent(mContext, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
        progressBar.setVisibility(View.GONE);
        overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
    }

    private void getState(){
        if (firebaseUser != null){
            linearLayout.setVisibility(View.INVISIBLE);
            if (getIntent().hasExtra(Constants.OBJECT_ID)) {
                String type = getIntent().getStringExtra(Constants.TYPE);
                String objectID = getIntent().getStringExtra(Constants.OBJECT_ID);
                fetchPost(objectID,type);
            } else {
                new Handler().postDelayed(() -> sendUserToCreateActivity(false), 100);
            }
        }else {
            linearLayout.setVisibility(View.VISIBLE);
        }
    }

    private void fetchPost(String objectID, String type){
        firebaseFirestore.collection(Constants.POSTS).document(objectID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()){
                Post post = documentSnapshot.toObject(Post.class);
                assert post != null;
                specificActivity(post,type);
            } else {
                new Handler().postDelayed(() -> sendUserToCreateActivity(false), 100);
            }
        });
    }

    private void specificActivity(Post post, String objectID){
        if (Objects.equals(objectID, Constants.BILL)){
            Intent intent = new Intent(mContext, BillDetail.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.OBJECT,post);
            intent.putExtras(bundle);
            startActivity(intent,bundle);

        } else if (Objects.equals(objectID, Constants.EVENT)){
            Intent intent = new Intent(mContext, EventDetail.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.OBJECT,post);
            intent.putExtras(bundle);
            startActivity(intent,bundle);

        } else if (Objects.equals(objectID, Constants.TRACK_RECORD)){
            Intent intent = new Intent(mContext, PostDetail.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.OBJECT,post);
            intent.putExtras(bundle);
            startActivity(intent,bundle);

        } else if (Objects.equals(objectID, Constants.NOTICE)){
            Intent intent = new Intent(mContext, PostDetail.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.OBJECT,post);
            intent.putExtras(bundle);
            startActivity(intent,bundle);
        }
    }
}

