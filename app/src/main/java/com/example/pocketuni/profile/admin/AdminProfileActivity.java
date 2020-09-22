package com.example.pocketuni.profile.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.pocketuni.R;
import com.example.pocketuni.security.SigninActivity;
import com.example.pocketuni.util.AdminBottomNavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminProfileActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Context context = AdminProfileActivity.this;
    private static final int ACTIVITY_NUMBER = 4;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.activity_admin_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        //redirect if already logged in
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), SigninActivity.class));
            finish();
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        AdminBottomNavigationHelper.enableNavigation(context, bottomNavigationView, ACTIVITY_NUMBER);

    }

    public void signout(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}