package com.aakash.sns;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {
    FirebaseUser user;
    FirebaseAuth mAuth;
    TextView tvUserName;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        tvUserName = findViewById(R.id.tvUserName);

//        assert user != null;

        tvUserName.setText(user.getDisplayName());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_Logout:
                mAuth.signOut();
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
                finish();
                Toast.makeText(HomeActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_manageProfile:
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }
}