package com.example.pocketuni.organizer.std;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.pocketuni.organizer.admin.AdminDeadlinesActivity;
import com.example.pocketuni.organizer.admin.AdminNoticesActivity;
import com.example.pocketuni.organizer.admin.AdminTimetableActivity;
import com.example.pocketuni.util.StdBottomNavigationHelper;
import com.example.pocketuni.R;
import com.example.pocketuni.security.SigninActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class OrganizerActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Context context = OrganizerActivity.this;
    private static final int ACTIVITY_NUMBER = 2;
    FirebaseAuth firebaseAuth;
    CardView timetables, deadlines, notices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.activity_organizer);

        timetables = findViewById(R.id.organizer_tile_timetable);
        deadlines = findViewById(R.id.organizer_tile_deadlines);
        notices = findViewById(R.id.organizer_tile_notices);

        firebaseAuth = FirebaseAuth.getInstance();

        //redirect if already logged in
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), SigninActivity.class));
            finish();
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        StdBottomNavigationHelper.enableNavigation(context, bottomNavigationView, ACTIVITY_NUMBER);

        //onClick listeners
        timetables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewTimetableActivity.class);
                startActivity(intent);
            }
        });

        deadlines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getApplicationContext(), AdminDeadlinesActivity.class);
                //startActivity(intent);
            }
        });

        notices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getApplicationContext(), AdminNoticesActivity.class);
                //startActivity(intent);
            }
        });
    }
}