package com.example.pocketuni.timeline.std;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketuni.R;
import com.example.pocketuni.security.SigninActivity;
import com.example.pocketuni.timeline.AddPostActivity;
import com.example.pocketuni.timeline.DeletePostDialog;
import com.example.pocketuni.util.StdBottomNavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements DeletePostDialog.DeletePostDialogListener {

    private BottomNavigationView bottomNavigationView;
    private Context context = MainActivity.this;
    private static final int ACTIVITY_NUMBER = 0;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        //redirect if already logged in
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), SigninActivity.class));
            finish();
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        StdBottomNavigationHelper.enableNavigation(context, bottomNavigationView, ACTIVITY_NUMBER);



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
            showToast("Post Ddeleting failed");
        }
    }

    //easy toast method for strings
    private void showToast (String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}