package com.aakash.sns;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class ProfileActivity extends AppCompatActivity {

    Toolbar toolbar;
    FirebaseUser user;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    EditText edtName, edtUserName, edtEmail;
    TextInputLayout tfEmail, tfUserName, tfName;
    Button btnChange;
    Bitmap bitmap;
    ImageView ivProfilePic;
    private String imageIdentifier;

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

        ivProfilePic = findViewById(R.id.upic);

        //Load User Data
        getUserProfile();

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        btnChange.setOnClickListener(v->{
            if (TextUtils.equals(btnChange.getText(), getResources().getString(R.string.str_change))){
                enableComponents();
            } else if (TextUtils.equals(btnChange.getText(), getResources().getString(R.string.str_save))){
                saveChanges();
            }
        });

        ivProfilePic.setOnClickListener(v -> selectImage());
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


//    Selecting Image
    private void selectImage(){
        if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1000 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectImage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == RESULT_OK && data != null){
            Uri chosenImageData = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), chosenImageData);
                ivProfilePic.setImageBitmap(bitmap);
                uploadImage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    Uploading Image
    private void uploadImage(){
        // Get the data from an ImageView as bytes
        if (bitmap != null){
            ivProfilePic.setDrawingCacheEnabled(true);
            ivProfilePic.buildDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            imageIdentifier = user.getUid() + ".png";
            UploadTask uploadTask = FirebaseStorage.getInstance().getReference()
                    .child("ProfilePics")
                    .child(imageIdentifier)
                    .putBytes(data);
            uploadTask
                    .addOnFailureListener(exception -> Toast.makeText(ProfileActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show())
                    .addOnSuccessListener(taskSnapshot -> Toast.makeText(ProfileActivity.this, "Profile pic changed!!", Toast.LENGTH_SHORT).show());
        }
    }
}