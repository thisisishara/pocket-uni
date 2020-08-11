package com.example.pocketuni;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SigninActivity extends AppCompatActivity {

    EditText txtEmail, txtPassword;
    Button btnSignin;
    TextView btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        txtEmail = this.findViewById(R.id.editTextEmailAddress);
        txtPassword =this.findViewById(R.id.editTextPassword);
        btnSignin = this.findViewById(R.id.buttonSignin);
        btnSignup = this.findViewById(R.id.textViewSignup);

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtEmail.getText().toString().trim();
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


            }
        });
    }

    private void showToast (String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}