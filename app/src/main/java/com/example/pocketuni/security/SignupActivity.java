package com.example.pocketuni.security;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pocketuni.R;
import com.example.pocketuni.timeline.std.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    public static final String TAG = "SIGNUP";
    EditText txtEmail, txtPassword, txtConfirmPassword, txtName, txtBatch;
    Spinner spinSemester, spinCourse;
    Button btnSignup;
    TextView btnSignin;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userID;

    String email, name, password, confirmPassword, year_semester, year, semester, batch, course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        txtEmail = this.findViewById(R.id.editTextEmailAddress);
        txtName =this.findViewById(R.id.editTextName);
        txtPassword = this.findViewById(R.id.editTextPassword);
        txtConfirmPassword = this.findViewById(R.id.editTextConfirmPassword);
        spinSemester = this.findViewById(R.id.spinnerSemester);
        spinCourse = this.findViewById(R.id.spinnerCourse);
        txtBatch = this.findViewById(R.id.editTextBatch);
        btnSignup = this.findViewById(R.id.buttonSignup);
        btnSignin = this.findViewById(R.id.textViewSignin);
        progressBar = this.findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //redirect if already logged in
        if(firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = txtEmail.getText().toString().trim();
                password = txtPassword.getText().toString();
                confirmPassword = txtConfirmPassword.getText().toString();
                name = txtName.getText().toString().trim();
                year_semester = spinSemester.getSelectedItem().toString().trim();
                batch = txtBatch.getText().toString().trim();
                course = spinCourse.getSelectedItem().toString().trim();

                //validation
                if(email.matches(getResources().getString(R.string.email_regex)) == false){
                    //txtEmail.setError(getResources().getString(R.string.wrong_email_domain_error));
                    txtEmail.setText("");
                    txtPassword.setText("");
                    txtConfirmPassword.setText("");
                    showToast("INVALID EMAIL ADDRESS");
                    return;
                }

                if (name.trim().isEmpty()) {
                    txtName.setText("");
                    showToast("NAME CANNOT BE EMPTY");
                    return;
                }

                if (password.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
                    txtPassword.setText("");
                    txtConfirmPassword.setText("");
                    showToast("PASSWORD CANNOT BE EMPTY");
                    return;
                }

                if (password.equals(confirmPassword) == false) {
                    txtPassword.setText("");
                    txtConfirmPassword.setText("");
                    showToast("PASSWORDS DO NOT MATCH");
                    return;
                }

                if (password.length() < 6) {
                    txtPassword.setText("");
                    txtConfirmPassword.setText("");
                    showToast("PASSWORD SHOULD CONTAIN AT LEAST 6 CHARACTERS");
                    return;
                }

                //get year and semester
                if (year_semester != null){
                    getYearAndSemester();
                } else {
                    showToast("PLEASE SELECT THE ACADEMIC YEAR AND SEMESTER");
                }

                if (batch.trim().isEmpty()) {
                    txtBatch.setText("");
                    showToast("BATCH CANNOT BE EMPTY");
                    return;
                } else {
                    batch = "Y"+year+"S"+semester+"G"+batch;
                }

                if (course == null){
                    showToast("PLEASE SELECT YOUR COURSE");
                }

                //progressbar
                progressBar.setVisibility(View.VISIBLE);

                //registration
                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            userID = firebaseAuth.getCurrentUser().getUid();

                            Map<String, Object> user = new HashMap<>();
                            user.put("name", name);
                            user.put("email", email);
                            user.put("academic_year", year);
                            user.put("semester", semester);
                            user.put("batch", batch);
                            user.put("course", course);
                            user.put("userId", userID);
                            user.put("dp", null);
                            user.put("isRemindersOn", false);
                            user.put("remainderMinutes", -1);
                            user.put("userType", "STDNT"); //types: ADMIN/STDNT, by default STDNT since evey user who registers is a Student.

                            DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "User Account details successfully added for " + userID);

                                    showToast("ACCOUNT CREATED SUCCESSFULLY");

                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    progressBar.setVisibility(View.INVISIBLE);

                                }
                            });
                        } else {
                            showToast("ERROR OCCURRED. " + task.getException().getMessage());
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SigninActivity.class));
            }
        });
    }

    private void showToast (String message) {
        Toast.makeText(SignupActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void getYearAndSemester() {
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
}