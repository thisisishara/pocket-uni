package com.example.pocketuni.security;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pocketuni.R;
import com.example.pocketuni.timeline.admin.AdminActivity;
import com.example.pocketuni.timeline.std.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.EventListener;

public class SigninActivity extends AppCompatActivity {

    EditText txtEmail, txtPassword;
    Button btnSignin;
    TextView btnSignup;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        txtEmail = this.findViewById(R.id.editTextEmailAddress);
        txtPassword =this.findViewById(R.id.editTextPassword);
        btnSignin = this.findViewById(R.id.buttonSignin);
        btnSignup = this.findViewById(R.id.textViewSignup);
        progressBar = this.findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();

        //redirect if already logged in
        if(firebaseAuth.getCurrentUser() != null){
            if (firebaseAuth.getCurrentUser().getEmail().equals(getResources().getString(R.string.admin_email)) == true) {
                startActivity(new Intent(getApplicationContext(), AdminActivity.class));
            } else {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
            finish();
        }

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = txtEmail.getText().toString().trim();
                String password = txtPassword.getText().toString();

                if(email.matches(getResources().getString(R.string.email_regex)) == false){
                    //txtEmail.setError(getResources().getString(R.string.wrong_email_domain_error));
                    txtEmail.setText("");
                    txtPassword.setText("");
                    showToast("INVALID EMAIL ADDRESS");
                    return;
                }

                if (password.trim().isEmpty()) {
                    txtPassword.setText("");
                    showToast("PASSWORD CANNOT BE EMPTY");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //authentication
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            showToast("SIGNED IN SUCCESSFULLY");

                            Intent intent = null;

                            if (email.equals(getResources().getString(R.string.admin_email))) {
                                intent = new Intent(getApplicationContext(), AdminActivity.class);
                            } else {
                                intent = new Intent(getApplicationContext(), MainActivity.class);
                            }

                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            progressBar.setVisibility(View.INVISIBLE);

                        } else {
                            showToast("SIGN IN FAILED! " + task.getException().getMessage());
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
            }
        });

    }

    private void showToast (String message) {
        Toast.makeText(SigninActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}