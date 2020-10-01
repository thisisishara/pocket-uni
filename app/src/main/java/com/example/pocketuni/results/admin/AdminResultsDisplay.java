package com.example.pocketuni.results.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pocketuni.R;
import com.example.pocketuni.security.SigninActivity;
import com.example.pocketuni.util.AdminBottomNavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class AdminResultsDisplay extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Context context = AdminResultsDisplay.this;
    private static final int ACTIVITY_NUMBER = 3;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    EditText regNumberD, caMarksD;
    private Spinner spinnerPeriod, spinnerYear, spinnerGrade, spinnerModule;
    private String sRegNumber, sCAMarks, sPeriod, sGetPeriod, sYear, sGetYear, sGetGrade, sGradePoint, sLetterGrade, sGetModule, sModule;
    Button addNewEntry, update, delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.activity_admin_results_display);

        regNumberD = findViewById(R.id.studentRegistrationNumberDisplay);
        caMarksD = findViewById(R.id.caMarksDisplay);
        spinnerGrade = findViewById(R.id.spinnerGradeDisplay);
        spinnerPeriod = findViewById(R.id.spinnerPeriodDisplay);
        spinnerYear = findViewById(R.id.spinnerYearDisplay);
        spinnerModule = findViewById(R.id.spinnerModuleDisplay);

        addNewEntry = (Button) findViewById(R.id.btnAddNewEntry);
        update = findViewById(R.id.btnUpdate);
        delete = findViewById(R.id.btnDelete);

        firebaseAuth = FirebaseAuth.getInstance();

        //redirect if already logged in
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), SigninActivity.class));
            finish();
        }

        //Firestore
        db = FirebaseFirestore.getInstance();

        //get extras from add activity
        Intent extras = getIntent();

        /*String extraGrade, extraPeriod, extraYear, extraModule;

        extraGrade = extras.getStringExtra("grade");
        extraPeriod = extras.getStringExtra("period");
        extraYear = extras.getStringExtra("grade");
        extraModule = extras.getStringExtra("grade");*/

        regNumberD.setText(extras.getStringExtra("regNum"));
        caMarksD.setText(extras.getStringExtra("caMarks"));
        spinnerGrade.setSelection(getSpinnerGrade(Objects.requireNonNull(extras.getStringExtra("grades"))));
        spinnerPeriod.setSelection(getSpinnerPeriod(Objects.requireNonNull(extras.getStringExtra("period"))));
        spinnerYear.setSelection(getSpinnerYear(Objects.requireNonNull(extras.getStringExtra("year"))));
        spinnerModule.setSelection(getSpinnerModule(Objects.requireNonNull(extras.getStringExtra("module"))));

        //Generate years from 1999 to date for spinner
        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1999; i <= thisYear; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years);

        spinnerYear = (Spinner)findViewById(R.id.spinnerYearDisplay);
        spinnerYear.setAdapter(adapter);
        //End generating years

        addNewEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminResultsDisplay.this, AdminResultsAdd.class));
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        AdminBottomNavigationHelper.enableNavigation(context, bottomNavigationView, ACTIVITY_NUMBER);
    }

    private void showToast (String message) {
        Toast.makeText(AdminResultsDisplay.this, message, Toast.LENGTH_SHORT).show();
    }

    private int getSpinnerModule(String module){
        int var = 0;
        if (module.equals("Communication Skills")){
            var = 0;
        } else if (module.equals("Computer Networks")){
            var = 1;
        } else if (module.equals("Database Management Systems")){
            var = 2;
        } else if (module.equals("English for Academic Purposes")){
            var = 3;
        } else if (module.equals("Information Systems and Data Modeling")){
            var = 4;
        } else if (module.equals("Internet and Web Technologies")){
            var = 5;
        } else if (module.equals("Introduction to Computer Systems")){
            var = 6;
        } else if (module.equals("Introduction to Programming")){
            var = 7;
        } else if (module.equals("Mathematics for Computing")){
            var = 8;
        } else if (module.equals("Object Oriented Concepts")){
            var = 9;
        } else if (module.equals("Object Oriented Programming")){
            var = 10;
        } else if (module.equals("Operating Systems and System Administration")){
            var = 11;
        } else if (module.equals("Software Engineering")){
            var = 12;
        } else if (module.equals("Software Process Modeling")){
            var = 13;
        }
        return var;
    }

    private int getSpinnerPeriod(String period){
        int var = 0;
        if (period.equals("Jan-Jun")){
            var = 0;
        } else if (period.equals("Jun-Dec")){
            var = 1;
        }
        return var;
    }

    private int getSpinnerYear(String year){
        int yearCounter = 1999, var = 0;
        for (int i=0; i <= Calendar.getInstance().get(Calendar.YEAR)-1999;){
            String yearS = Integer.toString(yearCounter);
            if (year.equals(yearS)){
                var = i;
            }
            i++;
        }
        return var;
    }

    private int getSpinnerGrade(String grade){
        int var = 0;
        if (grade.equals("A+")){
            var = 0;
        } else if (grade.equals("A")){
            var = 1;
        } else if (grade.equals("A-")){
            var = 2;
        } else if (grade.equals("B+")){
            var = 3;
        } else if (grade.equals("B")){
            var = 4;
        } else if (grade.equals("B-")){
            var = 5;
        } else if (grade.equals("C+")){
            var = 6;
        } else if (grade.equals("C")){
            var = 7;
        } else if (grade.equals("C-")){
            var = 8;
        } else if (grade.equals("D+")){
            var = 9;
        } else if (grade.equals("D")){
            var = 10;
        } else if (grade.equals("E")){
            var = 11;
        }
        return var;
    }
}