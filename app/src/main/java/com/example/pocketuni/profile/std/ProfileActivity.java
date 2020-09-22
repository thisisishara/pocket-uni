package com.example.pocketuni.profile.std;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketuni.R;
import com.example.pocketuni.model.Reminder;
import com.example.pocketuni.organizer.std.ReminderBroadcastReceiver;
import com.example.pocketuni.security.SigninActivity;
import com.example.pocketuni.util.StdBottomNavigationHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Context context = ProfileActivity.this;
    private static final int ACTIVITY_NUMBER = 4;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private int reminderCounter = 0;
    private String TAG = "SPA_REMDEL_COUNT";
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //redirect if already logged in
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), SigninActivity.class));
            finish();
        }


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        StdBottomNavigationHelper.enableNavigation(context, bottomNavigationView, ACTIVITY_NUMBER);
    }

    public void signout() {
        //siging out takes place after the reminders are deleted (OnComplete)
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(),SigninActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void disableRemindersAndSignOut(View view) {
        CollectionReference collectionReferenceForDelete = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).collection("reminders");
        collectionReferenceForDelete.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        reminderCounter = task.getResult().size();
                        Log.i(TAG, "Total number of reminders found: " + reminderCounter);

                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            Reminder remdel = queryDocumentSnapshot.toObject(Reminder.class);
                            Log.i(TAG, "local reminder for " + remdel.getReminderItemId() + " (ID: " + reminderCounter + ") has been deleted");

                            Intent reminderIntent = new Intent(getApplicationContext(), ReminderBroadcastReceiver.class);
                            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), reminderCounter, reminderIntent, PendingIntent.FLAG_NO_CREATE);
                            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                            if (pendingIntent!=null) {
                                alarmManager.cancel(pendingIntent);
                                Reminder rem = queryDocumentSnapshot.toObject(Reminder.class);
                                Log.i(TAG,  "(PA) Reminder deleted for "+ rem.getReminderItemId());
                            }
                            reminderCounter--; //unique request code for each alarm in descending order of the number of reminders
                        }

                        signout();
                    }
                    else{
                        signout();
                    }
                }
            }
        });
    }
}