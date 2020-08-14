package com.example.pocketuni.results.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.icu.util.EthiopicCalendar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pocketuni.R;
import com.example.pocketuni.security.SigninActivity;
import com.example.pocketuni.util.AdminBottomNavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AdminResultsActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Context context = AdminResultsActivity.this;
    private static final int ACTIVITY_NUMBER = 3;
    FirebaseAuth firebaseAuth;

    private Spinner spinnerDate;
    private Button btnSubmit;
    private Button btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.activity_admin_results);

        firebaseAuth = FirebaseAuth.getInstance();

        //redirect if already logged in
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), SigninActivity.class));
            finish();
        }

        addListenerOnButton();
        addListenerOnSpinnerItemSelection();

        final EditText regNumber = findViewById(R.id.studentRegistrationNumber);
        final EditText moduleName = findViewById(R.id.moduleName);
        final EditText caMarks = findViewById(R.id.caMarks);
        final EditText grade = findViewById(R.id.grade);
        final EditText caYear = findViewById(R.id.caYear);

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regNumber.setText(" ");
                moduleName.setText(" ");
                caMarks.setText(" ");
                grade.setText(" ");
                caYear.setText(" ");

                Toast clearToast = Toast.makeText(getApplicationContext(), "Text Cleared", Toast.LENGTH_SHORT);
                clearToast.show();
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        AdminBottomNavigationHelper.enableNavigation(context, bottomNavigationView, ACTIVITY_NUMBER);

    }

    private void addListenerOnSpinnerItemSelection() {
        spinnerDate = (Spinner) findViewById(R.id.spinnerDate);
        //spinnerDate.setOnItemSelectedListener(new CustomeOnItemSelectedListener());
    }

    private void addListenerOnButton() {
        spinnerDate = (Spinner) findViewById(R.id.spinnerDate);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminResultsActivity.this, AdminResultsDisplay.class));
            }
        });
    }
}