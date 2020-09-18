package com.example.pocketuni.timeline.std;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketuni.R;
import com.example.pocketuni.model.CurrentUser;
import com.example.pocketuni.security.SigninActivity;
import com.example.pocketuni.timeline.AddPostActivity;
import com.example.pocketuni.timeline.DeletePostDialog;
import com.example.pocketuni.util.StdBottomNavigationHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements DeletePostDialog.DeletePostDialogListener {

    private BottomNavigationView bottomNavigationView;
    private Context context = MainActivity.this;
    private static final int ACTIVITY_NUMBER = 0;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        final String userID = firebaseAuth.getCurrentUser().getUid();

        //redirect if an invalid user
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), SigninActivity.class));
            finish();
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        StdBottomNavigationHelper.enableNavigation(context, bottomNavigationView, ACTIVITY_NUMBER);


        //saving current user info
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            private static final String TAG = "STD TIMELINE";

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot user = task.getResult();
                    if (user.exists()) {
                        CurrentUser.setEmail(firebaseAuth.getCurrentUser().getEmail());
                        CurrentUser.setName((String)user.get("name"));
                        CurrentUser.setBatch((String)user.get("batch"));
                        CurrentUser.setCourse((String)user.get("course"));
                        CurrentUser.setProfilePicture((Image)user.get("profile_pic"));
                        CurrentUser.setSemester((String)user.get("semester"));
                        CurrentUser.setUserId((String)userID);
                        CurrentUser.setYear((String)user.get("academic_year"));

                        Log.d(TAG, "DocumentSnapshot data: " + user.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        //delete post dialog
        TextView deletePostButton = findViewById(R.id.deletePostButton2);
        deletePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeletePostDialog deletePostDialog = new DeletePostDialog();
                deletePostDialog.show(getSupportFragmentManager(), "Delete Post");
            }
        });

        FloatingActionButton addPostButton = findViewById(R.id.addPostFloatingActionButton);
        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddPostActivity.class));
            }
        });

    }

    @Override
    public void getConfirmation(boolean confirmation) {
        if (confirmation == true) {
            Toast.makeText(MainActivity.this,R.string.post_delete_confirmation_toast, Toast.LENGTH_SHORT).show();
        } else {
            showToast("Post Deleting failed");
        }
    }

    //easy toast method for strings
    private void showToast (String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}