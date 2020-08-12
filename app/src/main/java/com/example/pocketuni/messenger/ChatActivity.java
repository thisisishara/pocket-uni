package com.example.pocketuni.messenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.pocketuni.util.BottomNavigationHelper;
import com.example.pocketuni.R;
import com.example.pocketuni.security.SigninActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ChatActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Context context = ChatActivity.this;
    private static final int ACTIVITY_NUMBER = 1;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.activity_chat);

        firebaseAuth = FirebaseAuth.getInstance();

        //redirect if already logged in
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), SigninActivity.class));
            finish();
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavigationHelper.enableNavigation(context, bottomNavigationView, ACTIVITY_NUMBER);

    }
}