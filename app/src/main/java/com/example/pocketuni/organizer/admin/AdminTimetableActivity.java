package com.example.pocketuni.organizer.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pocketuni.R;
import com.example.pocketuni.security.SigninActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AdminTimetableActivity extends AppCompatActivity implements AddTimetableDialog.AddTimetableDialogListener {
    FloatingActionButton addTimetable;
    TextView t1; //temp

    String yearSemester;
    String course;
    int batch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_timetable);

        addTimetable = findViewById(R.id.addTimetablesFloatingActionButton);
        t1 = findViewById(R.id.textView);

        addTimetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show add timetable dialog
                showAddTimetableDialog();
            }
        });
    }

    public void showAddTimetableDialog () {
        AddTimetableDialog addTimetableDialog = new AddTimetableDialog();
        addTimetableDialog.show(getSupportFragmentManager(), "Add Timetable Dialog");
    }

    @Override
    public void getNewTimetableData(String yearSemesterSpinner, String courseSpinner, int batch) {
        this.yearSemester = yearSemesterSpinner;
        this.course = courseSpinner;
        this.batch = batch;
        t1.setText(yearSemester + course + batch);
        //if firestore success, show timetable
        //showToast(yearSemester + course + batch);

        Intent intent = new Intent(getApplicationContext(), AdminViewTimetableActivity.class);
        startActivity(intent);
    }

    private void showToast (String message) {
        Toast.makeText(AdminTimetableActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}