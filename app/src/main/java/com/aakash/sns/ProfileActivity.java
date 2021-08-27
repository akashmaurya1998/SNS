package com.aakash.sns;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    Toolbar toolbar;
    FirebaseUser user;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    EditText edtName, edtUserName, edtEmail;
    TextInputLayout tfEmail, tfUserName, tfName;
    Button btnChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = findViewById(R.id.pToolBar);

        edtEmail = findViewById(R.id.tfedtEmail);
        edtName = findViewById(R.id.tfedtName);
        edtUserName = findViewById(R.id.tfedtUserName);

        tfEmail = findViewById(R.id.tfpUEmail);
        tfName = findViewById(R.id.tfPName);
        tfUserName = findViewById(R.id.tfpUName);

        btnChange = findViewById(R.id.btnChange);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        tfName.setEnabled(false);
        tfUserName.setEnabled(false);
        tfEmail.setEnabled(false);

        //Load User Data
        getUserProfile();

        toolbar.setNavigationOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            finish();
        });

        btnChange.setOnClickListener(v->{
            if (TextUtils.equals(btnChange.getText(), getResources().getString(R.string.str_change))){
                enableComponents();
            } else if (TextUtils.equals(btnChange.getText(), getResources().getString(R.string.str_save))){
                saveChanges();
            }
        });
    }

//    Save User profile changes
    private void saveChanges() {

    }


    //    Enabling Components to make changes
    private void enableComponents() {
        tfName.setEnabled(true);
        tfUserName.setEnabled(true);
        tfEmail.setEnabled(true);
        btnChange.setText(getResources().getString(R.string.str_save));
    }


//    Fetch User Profile
    private void getUserProfile() {
        database.getReference().child("users")
                .orderByChild("userId")
                .equalTo(user.getUid())
                .limitToFirst(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()){
                            edtUserName.setText(ds.child("UserName").getValue(String.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        edtName.setText(user.getDisplayName());
        edtEmail.setText(user.getEmail());
    }
}