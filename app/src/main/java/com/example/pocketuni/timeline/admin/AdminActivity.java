package com.example.pocketuni.timeline.admin;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketuni.R;
import com.example.pocketuni.model.CurrentUser;
import com.example.pocketuni.security.SigninActivity;
import com.example.pocketuni.timeline.std.MainActivity;
import com.example.pocketuni.util.AdminBottomNavigationHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class AdminActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Context context = AdminActivity.this;
    private static final int ACTIVITY_NUMBER = 0;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.activity_admin);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        //redirect if an invalid user
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), SigninActivity.class));
            finish();
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        AdminBottomNavigationHelper.enableNavigation(context, bottomNavigationView, ACTIVITY_NUMBER);

        //getting current user info
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            private static final String TAG = "ADMIN_SESSION_TLN";

            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.d(TAG, "Current data: " + documentSnapshot.getData());

                    CurrentUser.setEmail(firebaseAuth.getCurrentUser().getEmail());
                    CurrentUser.setName(("name"));
                    CurrentUser.setProfilePicture((Image) documentSnapshot.get("profile_pic"));
                    CurrentUser.setUserType((String) documentSnapshot.get("userType"));
                    CurrentUser.setUserId((String) userID);

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    //easy toast method for strings
    private void showToast (String message) {
        Toast.makeText(AdminActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}