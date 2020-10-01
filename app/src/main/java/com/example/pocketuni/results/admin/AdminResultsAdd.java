package com.example.pocketuni.results.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketuni.R;
import com.example.pocketuni.security.SigninActivity;
import com.example.pocketuni.util.AdminBottomNavigationHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AdminResultsAdd extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Context context = AdminResultsAdd.this;
    private static final int ACTIVITY_NUMBER = 3;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    private Spinner spinnerPeriod, spinnerYear, spinnerGrade, spinnerModule;
    Button btnSubmit, btnClear;
    EditText regNumber, caMarks;
    private String sRegNumber, sCAMarks, sPeriod, sGetPeriod, sYear, sGetYear, sGetGrade, sGradePoint, sLetterGrade, sGetModule, sModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.activity_admin_results_add);

        regNumber = findViewById(R.id.studentRegistrationNumberAdd);
        caMarks = findViewById(R.id.caMarksAdd);
        spinnerGrade = findViewById(R.id.spinnerGradeAdd);
        spinnerPeriod = findViewById(R.id.spinnerPeriodAdd);
        spinnerYear = findViewById(R.id.spinnerYearAdd);
        spinnerModule = findViewById(R.id.spinnerModuleAdd);

        btnClear = findViewById(R.id.btnReset);
        btnSubmit = findViewById(R.id.btnSubmit);

        firebaseAuth = FirebaseAuth.getInstance();

        //redirect if already logged in
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), SigninActivity.class));
            finish();
        }

        //Firestore
        db = FirebaseFirestore.getInstance();

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regNumber.setText(" ");
                caMarks.setText(" ");

                showToast("Fields Cleared");
            }
        });

        //CA marks validation
        caMarks.setFilters(new InputFilter[]{new InputFilterMinMax("0.0", "100.0")});

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sRegNumber = regNumber.getText().toString().trim();
                sGetModule = spinnerModule.getSelectedItem().toString().trim();
                sCAMarks = caMarks.getText().toString().trim();
                sGetGrade = spinnerGrade.getSelectedItem().toString().trim();
                sGetPeriod = spinnerPeriod.getSelectedItem().toString().trim();
                sGetYear = spinnerYear.getSelectedItem().toString().trim();

                String yearAndSem = getYearAndSemester(sGetModule);
                uploadData(sRegNumber, sGetModule, sCAMarks, sGetGrade, sGetPeriod, sGetYear, yearAndSem);

                Intent intent = new Intent(AdminResultsAdd.this, AdminResultsDisplay.class);
                intent.putExtra("regNum", sRegNumber);
                intent.putExtra("module", sGetModule);
                intent.putExtra("caMarks", sCAMarks);
                intent.putExtra("grades", sGetGrade);
                intent.putExtra("period", sGetPeriod);
                intent.putExtra("year", sGetYear);
                startActivity(intent);

                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(AdminResultsAdd.this, AdminResultsDisplay.class);
                        intent.putExtra("regNum", sRegNumber);
                        intent.putExtra("module", sGetModule);
                        intent.putExtra("caMarks", sCAMarks);
                        intent.putExtra("grades", sGetGrade);
                        intent.putExtra("period", sGetPeriod);
                        intent.putExtra("year", sGetYear);
                        startActivity(intent);
                    }
                }, 1000);*/
            }
        });

        //Generate years from 1999 to date for spinner
        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1999; i <= thisYear; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years);

        spinnerYear = (Spinner)findViewById(R.id.spinnerYearAdd);
        spinnerYear.setAdapter(adapter);
        //End generating years

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        AdminBottomNavigationHelper.enableNavigation(context, bottomNavigationView, ACTIVITY_NUMBER);

    }

    private void uploadData(String sRegNumber, String sGetModule, String sCAMarks, String sGetGrade, String sGetPeriod, String sGetYear, String yearAndSem) {
        Map<String, Object> doc = new HashMap<>();
        doc.put("regNum", sRegNumber);
        doc.put("module", sGetModule);
        doc.put("caMarks", sCAMarks);
        doc.put("grades", sGetGrade);
        doc.put("period", sGetPeriod);
        doc.put("year", sGetYear);

        db.collection("Students").document(sRegNumber).collection("Results").document(yearAndSem).collection("Modules").document(sGetModule).set(doc).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                showToast("Data Added Successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast(e.getMessage());
            }
        });
    }

    private void showToast (String message) {
        Toast.makeText(AdminResultsAdd.this, message, Toast.LENGTH_SHORT).show();
    }

    private void getGrade() {
        switch (sGetGrade) {
            case "A+":
                sLetterGrade = "A+";
                sGradePoint = "4.0";
                break;
            case "A":
                sLetterGrade = "A";
                sGradePoint = "4.0";
                break;
            case "A-":
                sLetterGrade = "A-";
                sGradePoint = "3.7";
                break;
            case "B+":
                sLetterGrade = "B+";
                sGradePoint = "3.3";
                break;
            case "B":
                sLetterGrade = "B";
                sGradePoint = "3.0";
                break;
            case "B-":
                sLetterGrade = "B-";
                sGradePoint = "2.7";
                break;
            case "C+":
                sLetterGrade = "C+";
                sGradePoint = "2.3";
                break;
            case "C":
                sLetterGrade = "C";
                sGradePoint = "2.0";
                break;
            case "C-":
                sLetterGrade = "C-";
                sGradePoint = "1.7";
                break;
            case "D+":
                sLetterGrade = "D+";
                sGradePoint = "1.3";
                break;
            case "D":
                sLetterGrade = "D";
                sGradePoint = "1.0";
                break;
            case "E":
                sLetterGrade = "E";
                sGradePoint = "0.0";
                break;
            default:
                sLetterGrade = "N/A";
                sGradePoint = "N/A";
                break;
        }
    }

    private void getPeriod() {
        switch (sGetPeriod) {
            case "Jan-Jun":
                sPeriod = "Jan-Jun";
                break;
            case "Jun-Jan":
                sPeriod = "Jun-Dec";
                break;
            default:
                sLetterGrade = "N/A";
                sGradePoint = "N/A";
                break;
        }
    }

    private void getModule() {
        switch (sGetModule) {
            case "Introduction to Programming":
                sModule = "Introduction to Programming";
                break;
            case "Introduction to Computer Systems":
                sModule = "Introduction to Computer Systems";
                break;
            case "Mathematics for Computing":
                sModule = "Mathematics for Computing";
                break;
            case "Communication Skills":
                sModule = "Communication Skills";
                break;
            case "Object Oriented Concepts":
                sModule = "Object Oriented Concepts";
                break;
            case "Software Process Modeling":
                sModule = "Software Process Modeling";
                break;
            case "English for Academic Purposes":
                sModule = "English for Academic Purposes";
                break;
            case "Information Systems & Data Modeling":
                sModule = "Information Systems & Data Modeling";
                break;
            case "Internet & Web Technologies":
                sModule = "Internet & Web Technologies";
                break;
            case "Software Engineering":
                sModule = "Software Engineering";
                break;
            case "Object Oriented Programming":
                sModule = "Object Oriented Programming";
                break;
            case "Database Management Systems":
                sModule = "Database Management Systems";
                break;
            case "Computer Networks":
                sModule = "Computer Networks";
                break;
            case "Operating Systems and System Administration":
                sModule = "Operating Systems and System Administration";
                break;
            default:
                sLetterGrade = "N/A";
                sGradePoint = "N/A";
                break;
        }
    }

    private String getYearAndSemester(String module){
        StringBuilder yearAndSemester = new StringBuilder("");
        if (module.equals("Introduction to Programming") || module.equals("Introduction to Computer Systems") || module.equals("Mathematics for Computing") || module.equals("Communication Skills")){
            yearAndSemester.append("Year One Semester One");
        } else if(module.equals("Object Oriented Concepts") || module.equals("Software Process Modeling") || module.equals("English for Academic Purposes") || module.equals("Information Systems & Data Modeling") || module.equals("Internet and Web Technologies")) {
            yearAndSemester.append("Year One Semester Two");
        } else if(module.equals("Software Engineering") || module.equals("Object Oriented Programming") || module.equals("Database Management Systems") || module.equals("Computer Networks") || module.equals("Operating Systems and System Administration")) {
            yearAndSemester.append("Year Two Semester One");
        }
        return yearAndSemester.toString();
    }

    /*private void addListenerOnSpinnerItemSelection() {
        spinnerDate = (Spinner) findViewById(R.id.spinnerDate);
        //spinnerDate.setOnItemSelectedListener(new CustomeOnItemSelectedListener());
    }

    private void addListenerOnButton() {
        spinnerDate = (Spinner) findViewById(R.id.spinnerDate);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminResultsAdd.this, AdminResultsDisplay.class));
            }
        });
    }*/
}
