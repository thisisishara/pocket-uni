package com.example.pocketuni.organizer.std;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.work.impl.background.systemalarm.ConstraintProxyUpdateReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.CorrectionInfo;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pocketuni.model.CurrentUser;
import com.example.pocketuni.model.TimetableItem;
import com.example.pocketuni.organizer.admin.AdminDeadlinesActivity;
import com.example.pocketuni.organizer.admin.AdminNoticesActivity;
import com.example.pocketuni.organizer.admin.AdminTimetableActivity;
import com.example.pocketuni.organizer.admin.AdminViewTimetableActivity;
import com.example.pocketuni.organizer.common.TimetableSlotListAdapter;
import com.example.pocketuni.util.StdBottomNavigationHelper;
import com.example.pocketuni.R;
import com.example.pocketuni.security.SigninActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class OrganizerActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Context context = OrganizerActivity.this;
    private static final int ACTIVITY_NUMBER = 2;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CardView timetables, deadlines, notices;
    private ImageView timetableNotificationIcon;
    private  String userID, timetableName;
    private boolean isTimetableAvailable = false;
    private String TAG = "STD_TTS_ORG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.activity_organizer);

        timetables = findViewById(R.id.organizer_tile_timetable);
        timetableNotificationIcon = findViewById(R.id.timetablenotification);
        deadlines = findViewById(R.id.organizer_tile_deadlines);
        notices = findViewById(R.id.organizer_tile_notices);

        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        timetableName = CurrentUser.getCourse() + " " + CurrentUser.getBatch();

        //redirect if already logged in
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), SigninActivity.class));
            finish();
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        StdBottomNavigationHelper.enableNavigation(context, bottomNavigationView, ACTIVITY_NUMBER);

        //onClick listeners
        timetables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTimetableAvailable == true) {
                    Intent intent = new Intent(getApplicationContext(), ViewTimetableActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                } else {
                    showToast("TIMETABLE IS NOT AVAILABLE YET.");
                }
            }
        });

        deadlines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getApplicationContext(), AdminDeadlinesActivity.class);
                //startActivity(intent);
            }
        });

        notices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getApplicationContext(), AdminNoticesActivity.class);
                //startActivity(intent);
            }
        });

        getUserStatus();
    }

    private void getUserStatus() {
        DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    CurrentUser.setEmail((String) task.getResult().get("email"));
                    CurrentUser.setName((String) task.getResult().get("name"));
                    CurrentUser.setBatch((String) task.getResult().get("batch"));
                    CurrentUser.setCourse((String) task.getResult().get("course"));
                    CurrentUser.setProfilePicture((Image) task.getResult().get("profile_pic"));
                    CurrentUser.setSemester((String) task.getResult().get("semester"));
                    CurrentUser.setYear((String) task.getResult().get("academic_year"));
                    CurrentUser.setUserType((String) task.getResult().get("userType"));
                    CurrentUser.setUserId((String) task.getResult().get("userId"));
                    CurrentUser.setIsRemindersOn((Boolean) task.getResult().get("isRemindersOn"));
                    CurrentUser.setRemainderMinutes(((Long) task.getResult().get("remainderMinutes")).intValue());
                    timetableName = CurrentUser.getCourse() + " " + CurrentUser.getBatch();
                }
                getTimetableStatus();
                startListeningToUserChanges();
            }
        });
    }

    private void getTimetableStatus() {
        //listener to check if timetable is available
        //ICON: red_n/a, green_available, blue_available+reminder

        CollectionReference collectionReference = firebaseFirestore.collection("timetables").document((CurrentUser.getCourse() + " " + CurrentUser.getBatch())).collection("slots");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.i(TAG, "timetable slots fetched successfully.");

                    if (task.getResult().size() > 0) {
                        isTimetableAvailable = true;
                        if (CurrentUser.isIsRemindersOn() == true) {
                            timetableNotificationIcon.setImageDrawable(getResources().getDrawable(R.drawable.notifblue));
                        } else {
                            timetableNotificationIcon.setImageDrawable(getResources().getDrawable(R.drawable.notifgreen));
                        }
                    } else {
                        isTimetableAvailable = false;
                        timetableNotificationIcon.setImageDrawable(getResources().getDrawable(R.drawable.notifred));
                    }
                } else {
                    Log.w(TAG, "timetable slot data: failed to fetch");
                }
            }
        });
        startListeningToTimetableChanges();
    }

    private void startListeningToTimetableChanges(){
        CollectionReference collectionReference = firebaseFirestore.collection("timetables").document((CurrentUser.getCourse() + " " + CurrentUser.getBatch())).collection("slots");;
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "User: Listen failed. (LN)");
                    return;
                }

                if(queryDocumentSnapshots.size() > 0) {
                    Log.w(TAG, queryDocumentSnapshots.size() + " timetable slots found. (LN)");
                    isTimetableAvailable = true;
                    if(CurrentUser.isIsRemindersOn() == true){
                        timetableNotificationIcon.setImageDrawable(getResources().getDrawable(R.drawable.notifblue));
                    } else {
                        timetableNotificationIcon.setImageDrawable(getResources().getDrawable(R.drawable.notifgreen));
                    }
                } else {
                    Log.w(TAG, queryDocumentSnapshots.size() + " is less than or equal to zero. (LN)");
                    isTimetableAvailable = false;
                    timetableNotificationIcon.setImageDrawable(getResources().getDrawable(R.drawable.notifred));
                }
            }
        });
    }

    private void startListeningToUserChanges(){
        DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "User: Listen failed. (LN)");
                    return;
                }
                if (documentSnapshot.exists()){
                    CurrentUser.setEmail(firebaseAuth.getCurrentUser().getEmail());
                    CurrentUser.setName((String) documentSnapshot.get("name"));
                    CurrentUser.setBatch((String) documentSnapshot.get("batch"));
                    CurrentUser.setCourse((String) documentSnapshot.get("course"));
                    CurrentUser.setProfilePicture((Image) documentSnapshot.get("profile_pic"));
                    CurrentUser.setSemester((String) documentSnapshot.get("semester"));
                    CurrentUser.setYear((String) documentSnapshot.get("academic_year"));
                    CurrentUser.setUserType((String) documentSnapshot.get("userType"));
                    CurrentUser.setUserId((String) firebaseAuth.getCurrentUser().getUid());
                    CurrentUser.setIsRemindersOn((Boolean) documentSnapshot.get("isRemindersOn"));
                    CurrentUser.setEmail((String) documentSnapshot.get("email"));
                    CurrentUser.setRemainderMinutes(((Long) documentSnapshot.get("remainderMinutes")).intValue());
                    CurrentUser.setUserId((String) documentSnapshot.get("userId"));
                    Log.w(TAG, "User updated. (LN) : " + CurrentUser.getName());

                    getTimetableStatus();
                } else {
                    Log.w(TAG, "User not found. (LN)");
                }
            }
        });
    }

    private void showToast (String message) {
        Toast.makeText(OrganizerActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}