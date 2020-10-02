package com.example.pocketuni.results.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
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
import com.example.pocketuni.model.Result;
import com.example.pocketuni.security.SigninActivity;
import com.example.pocketuni.util.AdminBottomNavigationHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AdminResultsDisplay extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Context context = AdminResultsDisplay.this;
    private static final int ACTIVITY_NUMBER = 3;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    private String mode;

    private List<Result> resultList = new ArrayList<>();
    EditText regNumberD, caMarksD;
    private Spinner spinnerPeriod, spinnerYear, spinnerGrade, spinnerModule;
    private String sRegNumber, sCAMarks, sGetPeriod, sGetYear, sGetGrade, sGetModule;
    Button addNewEntry, update, delete;
    CustomAdapter adapter;
    RecyclerView mRecyclerView;

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

        db = FirebaseFirestore.getInstance();

        //get extras from add activity
        Intent extras = getIntent();

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

        mode = extras.getStringExtra("mode");

        // Check whether coming to this activity from Add activity or directly. 0 if from Add, 1 if direct.
        if (mode.equals("0")){
            regNumberD.setText(extras.getStringExtra("regNum"));
            caMarksD.setText(extras.getStringExtra("caMarks"));
            spinnerGrade.setSelection(getSpinnerGrade(Objects.requireNonNull(extras.getStringExtra("grades"))));
            spinnerPeriod.setSelection(getSpinnerPeriod(Objects.requireNonNull(extras.getStringExtra("period"))));
            spinnerYear.setSelection(getSpinnerYear(Objects.requireNonNull(extras.getStringExtra("year"))));
            spinnerModule.setSelection(getSpinnerModule(Objects.requireNonNull(extras.getStringExtra("module"))));
        } else if (mode.equals("1")){
            regNumberD.setText(extras.getStringExtra("regNum1"));
            caMarksD.setText(extras.getStringExtra("caMarks1"));
            spinnerGrade.setSelection(getSpinnerGrade(Objects.requireNonNull(extras.getStringExtra("grades1"))));
            spinnerPeriod.setSelection(getSpinnerPeriod(Objects.requireNonNull(extras.getStringExtra("period1"))));
            spinnerYear.setSelection(getSpinnerYear(Objects.requireNonNull(extras.getStringExtra("year1"))));
            spinnerModule.setSelection(getSpinnerModule(Objects.requireNonNull(extras.getStringExtra("module1"))));
        }

        spinnerModule.setEnabled(false);
        regNumberD.setEnabled(false);

        addNewEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminResultsDisplay.this, AdminResultsAdd.class));
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Warning");
                builder.setMessage("Do you want to delete ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        deleteData();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        AdminBottomNavigationHelper.enableNavigation(context, bottomNavigationView, ACTIVITY_NUMBER);
    }

    public void deleteData(){
        String regNum = regNumberD.getText().toString().trim();
        String email = regNum+"@my.sliit.lk";
        String yearSem = getYearAndSemester(spinnerModule.getSelectedItem().toString().trim());
        String module = spinnerModule.getSelectedItem().toString().trim();

        db.collection("Students").document(email).collection("Results").document(yearSem).collection("Modules").document(module).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        showToast("RECORD DELETED SUCCESSFULLY");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast(e.getMessage());
            }
        });

        if(mode.equals("0")){
            Intent intent = new Intent(AdminResultsDisplay.this, AdminResultsActivity.class);
            startActivity(intent);
        } else if(mode.equals("1")){
            Intent intent = new Intent(AdminResultsDisplay.this, ListActivity.class);
            intent.putExtra("DialogRegNum", regNum);
            intent.putExtra("DialogYearSem", yearSem);
            startActivity(intent);
        }
    }

    private void showToast (String message) {
        Toast.makeText(AdminResultsDisplay.this, message, Toast.LENGTH_SHORT).show();
    }

    public int getSpinnerModule(String module){
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

    public int getSpinnerPeriod(String period){
        int var = 0;
        if (period.equals("Jan-Jun")){
            var = 0;
        } else if (period.equals("Jun-Dec")){
            var = 1;
        }
        return var;
    }

    public int getSpinnerYear(String year){
        int yearCounter = 1999;
        int var = 0;
        for (int i=0; i <= Calendar.getInstance().get(Calendar.YEAR)-1999;){
            String yearS = String.valueOf(yearCounter);
            if (year.equals(yearS)){
                var = i;
            }
            i++;
            yearCounter++;
        }
        return var;
    }

    public int getSpinnerGrade(String grade){
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

    private void showData() {
        String email = regNumberD.getText().toString().trim()+"@my.sliit.lk";
        String DialogYearSem = getYearAndSemester(spinnerModule.getSelectedItem().toString().trim());

        db.collection("Students").document(email).collection("Results").document(DialogYearSem).collection("Modules")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot doc: task.getResult()){
                    Result result = new Result(doc.getString("regNum"),
                            doc.getString("module"),
                            doc.getString("caMarks"),
                            doc.getString("grades"),
                            doc.getString("period"),
                            doc.getString("year"));
                    resultList.add(result);
                }
                if(!resultList.isEmpty()){
                    adapter = new CustomAdapter(ListActivity.class, resultList);
                    mRecyclerView.setAdapter(adapter);
                } else {
                    showToast("NO MODULES TO DISPLAY");
                    Intent intent = new Intent(AdminResultsDisplay.this, AdminResultsActivity.class);
                    startActivity(intent);
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(e.getMessage());
                    }
                });
    }

    private void updateData(){
        sRegNumber = regNumberD.getText().toString().trim();
        sGetModule = spinnerModule.getSelectedItem().toString().trim();
        sCAMarks = caMarksD.getText().toString().trim();
        sGetGrade = spinnerGrade.getSelectedItem().toString().trim();
        sGetPeriod = spinnerPeriod.getSelectedItem().toString().trim();
        sGetYear = spinnerYear.getSelectedItem().toString().trim();

        final Map<String, Object> doc = new HashMap<>();
        doc.put("regNum", sRegNumber);
        doc.put("module", sGetModule);
        doc.put("caMarks", sCAMarks);
        doc.put("grades", sGetGrade);
        doc.put("period", sGetPeriod);
        doc.put("year", sGetYear);

        String email = sRegNumber+"@my.sliit.lk";
        String yearSem = getYearAndSemester(sGetModule);

        db.collection("Students").document(email).collection("Results").document(yearSem).collection("Modules").document(sGetModule).set(doc)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        showToast("UPDATED SUCCESSFULLY");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast(e.getMessage());
            }
        });
    }
}