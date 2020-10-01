package com.example.pocketuni.timeline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pocketuni.R;
import com.example.pocketuni.model.CurrentUser;
import com.example.pocketuni.security.SigninActivity;
import com.example.pocketuni.timeline.admin.AdminActivity;
import com.example.pocketuni.timeline.std.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class AddPostActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private EditText titleEditText, contentsEditText;
    private Spinner audienceSpinner;
    private Button addPostButton, cancelAddPostButton;
    private String userID, userName, userDP, userEail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //redirect if an invalid user
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), SigninActivity.class));
            finish();
        }

        userID = firebaseAuth.getCurrentUser().getUid();
        userName = CurrentUser.getName();
        userEail = firebaseAuth.getCurrentUser().getEmail();
        userDP = CurrentUser.getDp();

        addPostButton = findViewById(R.id.addPostButton);
        cancelAddPostButton = findViewById(R.id.canceladdPostButton);
        titleEditText = findViewById(R.id.postTitle);
        contentsEditText = findViewById(R.id.postBody);
        audienceSpinner = findViewById(R.id.postAudienceSpinner);

        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //validations
                if(contentsEditText.getText().toString().equalsIgnoreCase("")){
                    showToast("POST CONTENTS CANNOT BE EMPTY.");
                    return;
                }

                //save data to firebase firestore
                Calendar postedDateTimeCalender = Calendar.getInstance();
                Date postedDateTime = postedDateTimeCalender.getTime();
                String audience = "";

                if (audienceSpinner.getSelectedItem().toString().equalsIgnoreCase("General")){
                    audience = "0";
                } else if (audienceSpinner.getSelectedItem().toString().equalsIgnoreCase("1st Year")){
                    audience = "1";
                } else if (audienceSpinner.getSelectedItem().toString().equalsIgnoreCase("2nd Year")){
                    audience = "2";
                } else if (audienceSpinner.getSelectedItem().toString().equalsIgnoreCase("3rd Year")){
                    audience = "3";
                } else if (audienceSpinner.getSelectedItem().toString().equalsIgnoreCase("4th Year")){
                    audience = "4";
                } else {
                    //do nothing
                }

                HashMap<String, Object> newPost = new HashMap<String, Object>();
                newPost.put ("noticeId", userID+""+postedDateTime.toString());
                newPost.put ("noticeDate", postedDateTime);
                newPost.put ("noticeTitle", titleEditText.getText().toString());
                newPost.put ("noticeContent", contentsEditText.getText().toString());
                newPost.put ("year", audience);
                newPost.put ("semester", "default");
                newPost.put ("adminEmail", userEail);
                newPost.put ("adminName", userName);
                newPost.put ("adminId", userID);
                newPost.put ("adminDp", userDP);

                DocumentReference documentReference = firebaseFirestore.collection("timelineposts").document(userID+""+postedDateTime.toString());
                documentReference.set(newPost).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            showToast(getResources().getString(R.string.add_post_confirmation_toast));
                        } else {
                            showToast("PROBLEM OCCURRED WHILE ADDING THE POST.");
                        }

                        finishActivity();
                    }
                });
            }
        });

        cancelAddPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishActivity();
            }
        });
    }

    private void finishActivity(){
        Intent intent = new Intent(AddPostActivity.this, AdminActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }

    private void showToast (String message) {
        Toast.makeText(AddPostActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateUserOnlineStatus(String status){
        if(firebaseAuth.getCurrentUser() != null) {
            HashMap<String, Object> userStatus = new HashMap<String, Object>();
            userStatus.put("status", status);

            DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
            documentReference.update(userStatus);
        }
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