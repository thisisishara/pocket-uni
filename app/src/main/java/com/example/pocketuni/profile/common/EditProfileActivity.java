package com.example.pocketuni.profile.common;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketuni.R;
import com.example.pocketuni.model.CurrentUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {
    private EditText fullName, batch;
    private Spinner courseSpinner, semesterSpinner;
    private Button cancelButton, updateButton;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String year, semester, batchString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        cancelButton = findViewById(R.id.cancelButton);
        updateButton = findViewById(R.id.updateButton);
        fullName = findViewById(R.id.editTextName);
        batch = findViewById(R.id.editTextBatch);
        semesterSpinner = findViewById(R.id.spinnerSemester);
        courseSpinner = findViewById(R.id.spinnerCourse);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        getUserInfo();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CurrentUser.getUserType().equalsIgnoreCase("ADMIN")){
                    updateAdmin();
                } else {
                    updateUser();
                }
            }
        });
    }

    private void updateAdmin(){
        if(fullName.getText().toString().trim().equalsIgnoreCase("") || fullName.getText().toString() ==null){
            showToast("NAME CANNOT BE EMPTY.");
            return;
        }

        HashMap<String, Object> newAdminDetails = new HashMap<String, Object>();
        newAdminDetails.put("name", fullName.getText().toString());

        DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.update(newAdminDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    showToast("PROFILE UPDATED.");
                    finish();
                } else {
                    showToast("FAILED TO UPLOAD PROFILE.");
                }
            }
        });
    }

    private void updateUser(){
        if(fullName.getText().toString().trim().equalsIgnoreCase("") || fullName.getText().toString() ==null){
            showToast("NAME CANNOT BE EMPTY.");
            return;
        }

        if (semesterSpinner.getSelectedItem().toString() != null){
            getYearAndSemester(semesterSpinner.getSelectedItem().toString());
        } else {
            showToast("PLEASE SELECT THE ACADEMIC YEAR AND SEMESTER");
        }

        if (batch.getText().toString().trim().isEmpty()) {
            batch.setText("");
            showToast("BATCH CANNOT BE EMPTY");
            return;
        } else {
            batchString = "Y"+year+"S"+semester+"G"+batch.getText().toString().trim();
            System.out.println(batchString);
        }

        if (courseSpinner.getSelectedItem().toString().trim() == null){
            showToast("PLEASE SELECT YOUR COURSE");
        }

        HashMap<String, Object> newUserDetails = new HashMap<String, Object>();
        newUserDetails.put("name", fullName.getText().toString());
        newUserDetails.put("batch", batchString);
        newUserDetails.put("academic_year", year);
        newUserDetails.put("semester", semester);
        newUserDetails.put("course", courseSpinner.getSelectedItem().toString().trim());

        DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.update(newUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    showToast("PROFILE UPDATED.");
                    finish();
                } else {
                    showToast("FAILED TO UPLOAD PROFILE.");
                }
            }
        });
    }

    private void getYearAndSemester(String year_semester) {
        switch (year_semester) {
            case "Year 1 Semester 1":
                year = "1";
                semester = "1";
                break;
            case "Year 1 Semester 2":
                year = "1";
                semester = "2";
                break;
            case "Year 2 Semester 1":
                year = "2";
                semester = "1";
                break;
            case "Year 2 Semester 2":
                year = "2";
                semester = "2";
                break;
            case "Year 3 Semester 1":
                year = "3";
                semester = "1";
                break;
            case "Year 3 Semester 2":
                year = "3";
                semester = "2";
                break;
            case "Year 4 Semester 1":
                year = "4";
                semester = "1";
                break;
            case "Year 4 Semester 2":
                year = "4";
                semester = "2";
                break;
            default:
                year = "N/A";
                semester = "N/A";
                break;
        }
    }

    private void getUserInfo() {
        DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    CurrentUser.setUserType((String) task.getResult().get("userType"));
                    CurrentUser.setName((String) task.getResult().get("name"));

                    if(CurrentUser.getUserType().equalsIgnoreCase("STDNT")) {
                        CurrentUser.setBatch((String) task.getResult().get("batch"));
                        CurrentUser.setCourse((String) task.getResult().get("course"));
                        CurrentUser.setSemester((String) task.getResult().get("semester"));
                        CurrentUser.setYear((String) task.getResult().get("academic_year"));
                    }

                    fullName.setText(CurrentUser.getName());

                    if (CurrentUser.getUserType().equalsIgnoreCase("ADMIN")) {
                        batch.setVisibility(View.GONE);
                        semesterSpinner.setVisibility(View.GONE);
                        courseSpinner.setVisibility(View.GONE);
                    } else {
                        if (CurrentUser.getYear().equalsIgnoreCase("1") && CurrentUser.getSemester().equalsIgnoreCase("1")) {
                            semesterSpinner.setSelection(0);
                        } else if (CurrentUser.getYear().equalsIgnoreCase("1") && CurrentUser.getSemester().equalsIgnoreCase("2")) {
                            semesterSpinner.setSelection(1);
                        } else if (CurrentUser.getYear().equalsIgnoreCase("2") && CurrentUser.getSemester().equalsIgnoreCase("1")) {
                            semesterSpinner.setSelection(2);
                        } else if (CurrentUser.getYear().equalsIgnoreCase("2") && CurrentUser.getSemester().equalsIgnoreCase("2")) {
                            semesterSpinner.setSelection(3);
                        } else if (CurrentUser.getYear().equalsIgnoreCase("3") && CurrentUser.getSemester().equalsIgnoreCase("1")) {
                            semesterSpinner.setSelection(4);
                        } else if (CurrentUser.getYear().equalsIgnoreCase("3") && CurrentUser.getSemester().equalsIgnoreCase("2")) {
                            semesterSpinner.setSelection(5);
                        } else if (CurrentUser.getYear().equalsIgnoreCase("4") && CurrentUser.getSemester().equalsIgnoreCase("1")) {
                            semesterSpinner.setSelection(6);
                        } else if (CurrentUser.getYear().equalsIgnoreCase("4") && CurrentUser.getSemester().equalsIgnoreCase("2")) {
                            semesterSpinner.setSelection(7);
                        } else {
                            //do nothing
                        }

                        if (CurrentUser.getCourse().equalsIgnoreCase("Software Engineering")) {
                            courseSpinner.setSelection(0);
                        } else if (CurrentUser.getCourse().equalsIgnoreCase("Information Technology")) {
                            courseSpinner.setSelection(1);
                        } else if (CurrentUser.getCourse().equalsIgnoreCase("Information Systems Engineering")) {
                            courseSpinner.setSelection(2);
                        } else if (CurrentUser.getCourse().equalsIgnoreCase("Computer Systems &amp; Network")) {
                            courseSpinner.setSelection(3);
                        } else if (CurrentUser.getCourse().equalsIgnoreCase("Cyber Security")) {
                            courseSpinner.setSelection(4);
                        } else if (CurrentUser.getCourse().equalsIgnoreCase("Interactive Media")) {
                            courseSpinner.setSelection(5);
                        } else if (CurrentUser.getCourse().equalsIgnoreCase("Data Science")) {
                            courseSpinner.setSelection(6);
                        }

                        batch.setText(CurrentUser.getBatch().substring(CurrentUser.getBatch().indexOf("G") + 1));
                    }
                }
            }
        });
    }

    private void showToast (String message) {
        Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateUserOnlineStatus(String status){
        HashMap<String,Object> userStatus = new HashMap<String, Object>();
        userStatus.put("status", status);

        DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.update(userStatus);
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateUserOnlineStatus("offline");
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUserOnlineStatus("online");
    }
}