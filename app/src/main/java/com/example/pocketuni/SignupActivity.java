package com.example.pocketuni;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    EditText txtEmail, txtPassword, txtConfirmPassword, txtName, txtBbatch;
    Spinner spinSemester;
    Button btnSignup;
    TextView btnSignin;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        txtEmail = this.findViewById(R.id.editTextEmailAddress);
        txtName =this.findViewById(R.id.editTextName);
        txtPassword = this.findViewById(R.id.editTextPassword);
        txtConfirmPassword = this.findViewById(R.id.editTextConfirmPassword);
        spinSemester = this.findViewById(R.id.spinnerSemester);
        //batch = this.findViewById(R.id.editTextBatch);
        btnSignup = this.findViewById(R.id.buttonSignup);
        btnSignin = this.findViewById(R.id.textViewSignin);

        firebaseAuth = FirebaseAuth.getInstance();

        //redirect if already logged in
        if(firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtEmail.getText().toString().trim();
                String password = txtPassword.getText().toString();
                String confirmPassword = txtConfirmPassword.getText().toString();
                String name = txtName.getText().toString().trim();
                String semester = spinSemester.getSelectedItem().toString().trim();


                //validation
                if(email.matches(getResources().getString(R.string.email_regex)) == false){
                    //txtEmail.setError(getResources().getString(R.string.wrong_email_domain_error));
                    txtEmail.setText("");
                    txtPassword.setText("");
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

                //registration
                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            showToast("ACCOUNT CREATED SUCCESSFULLY");
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            showToast("ERROR OCCURRED! " + task.getException().getMessage());
                        }
                    }
                });

            }
        });

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void showToast (String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}