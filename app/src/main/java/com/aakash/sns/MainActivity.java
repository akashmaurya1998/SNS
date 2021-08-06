package com.aakash.sns;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;
    CircularProgressButton signUpBtn, signInBtn;
    Bitmap bitmap;
    FirebaseAuth mAuth;
    EditText edtUserName, edtEmail, edtPassword;
    String email, password, username;
    FirebaseUser user;
    private GoogleSignInClient mGoogleSignInClient;
    SignInButton googleSignInBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signUpBtn =  findViewById(R.id.btn_signUp);
        signInBtn = findViewById(R.id.btn_signIn);

        mAuth = FirebaseAuth.getInstance();
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.check_mark);

        edtUserName = findViewById(R.id.edtUserName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);

        googleSignInBtn =  findViewById(R.id.google_button);


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

//        SIGNING UP
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               signUp();
            }
        });

//        SIGNING IN
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

//    SignUp Function
    private void signUp(){
        email = edtEmail.getText().toString();
        password = edtPassword.getText().toString();
        username = edtUserName.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(username)){
            Toast.makeText(MainActivity.this, "Please fill out each field.", Toast.LENGTH_SHORT).show();
            if (TextUtils.isEmpty(email)){
                edtEmail.setError("Cannot be empty!");
            }

            if (TextUtils.isEmpty(password)){
                edtPassword.setError("Cannot be empty!");
            }

            if (TextUtils.isEmpty(username)){
                edtUserName.setError("Cannot be empty!");
            }
        } else {
            signUpBtn.startAnimation();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                user = mAuth.getCurrentUser();
                                UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(username)
                                        .build();

                                user.updateProfile(changeRequest)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(MainActivity.this, "Welcome" + username, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            signUpBtn.doneLoadingAnimation(android.R.color.white, bitmap);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    signUpBtn.revertAnimation();
                                }
                            }, 1000);

                        }
                    });
        }
    }

//    SignIn Function
    private void signIn() {
        password = edtPassword.getText().toString();
        email = edtEmail.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(MainActivity.this, "Please fill out each field.", Toast.LENGTH_SHORT).show();
            if (TextUtils.isEmpty(email)) {
                edtEmail.setError("Cannot be empty!");
            }

            if (TextUtils.isEmpty(password)) {
                edtPassword.setError("Cannot be empty!");
            }
        } else {
            signInBtn.startAnimation();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                user = mAuth.getCurrentUser();
                                Toast.makeText(MainActivity.this, "Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                                signInBtn.doneLoadingAnimation(android.R.color.white, bitmap);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        signInBtn.revertAnimation();
                                    }
                                }, 1000);
                            } else {
                                Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                signInBtn.doneLoadingAnimation(android.R.color.white, bitmap);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        signInBtn.revertAnimation();
                                    }
                                }, 1000);
                            }
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
//                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
                             user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, user.getDisplayName(), Toast.LENGTH_SHORT).show();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            updateUI(null);
                            Toast.makeText(MainActivity.this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}