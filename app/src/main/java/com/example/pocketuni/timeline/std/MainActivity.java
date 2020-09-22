package com.example.pocketuni.timeline.std;

import android.app.AlarmManager;
import android.app.PendingIntent;
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
import com.example.pocketuni.model.Reminder;
import com.example.pocketuni.model.Timetable;
import com.example.pocketuni.model.TimetableItem;
import com.example.pocketuni.organizer.std.OrganizerActivity;
import com.example.pocketuni.organizer.std.ReminderBroadcastReceiver;
import com.example.pocketuni.results.std.ResultsActivity;
import com.example.pocketuni.security.SigninActivity;
import com.example.pocketuni.timeline.AddPostActivity;
import com.example.pocketuni.timeline.DeletePostDialog;
import com.example.pocketuni.util.StdBottomNavigationHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity implements DeletePostDialog.DeletePostDialogListener {

    private BottomNavigationView bottomNavigationView;
    private Context context = MainActivity.this;
    private static final int ACTIVITY_NUMBER = 0;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String userID, timetableName;
    private List<TimetableItem> timetableItemList = new ArrayList<TimetableItem>();
    private List<Reminder> currentReminderList = new ArrayList<Reminder>();;
    private List<Reminder> deprecatedReminderList = new ArrayList<Reminder>();;
    private List<Reminder> newReminderList = new ArrayList<Reminder>();;
    private String TAG = "STD_TTS_TL";
    private int reminderCounter = 0, pendingIntents = 0;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        CurrentUser.setUserId(userID);

        //add listener to detect user changers
        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth == null){
                    if(firebaseAuth.getCurrentUser() == null) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            }
        });

        //set session info one time
        getUserStatus();

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

        //subscribe for notifications
        //FCM SUBSCRIPTION SERVICE
        //subscribeForNotifications();
    }

    //TIMELINE Methods
    @Override
    public void getConfirmation(boolean confirmation) {
        if (confirmation == true) {
            Toast.makeText(MainActivity.this,R.string.post_delete_confirmation_toast, Toast.LENGTH_SHORT).show();
        } else {
            showToast("Post Deleting failed");
        }
    }

    private void getUserStatus(){
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
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

                    //set timetable name
                    timetableName = CurrentUser.getCourse() + " " + CurrentUser.getBatch();
                }
                //set listeners to get realtime updates
                startListeningToUserChanges();
                getReminderUpdates();
                //startListeningToTimetableChanges(); // not necessary
            }
        });
    }

    private void getReminderUpdates(){
        CollectionReference collectionReference = firebaseFirestore.collection("timetables").document(CurrentUser.getCourse()+" "+CurrentUser.getBatch()).collection("slots");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(CurrentUser.isIsRemindersOn()==true){
                        if (task.getResult() != null && task.getResult().size()>0) {
                            updateAllRemindersOfCurrentUser(true, CurrentUser.getRemainderMinutes());
                        } else {
                            updateReminderSettingsOfCurrentUser(false, -1);
                            updateAllRemindersOfCurrentUser(false, -1);
                        }
                    }
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
                    timetableName = CurrentUser.getCourse() + " " + CurrentUser.getBatch();
                    Log.w(TAG, "User updated. (LN) : " + CurrentUser.getName());

                } else {
                    Log.w(TAG, "User not found. (LN)");
                }
            }
        });
    }

    private void addNewReminders(final int minutes){
        final CollectionReference collectionReference = firebaseFirestore.collection("timetables").document(timetableName).collection("slots");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());

                        TimetableItem timetableItem = document.toObject(TimetableItem.class);
                        timetableItemList.add(timetableItem);
                    }

                    for (TimetableItem timetableItem : timetableItemList) {
                        final String slotId = "REM " + timetableItem.getStartingTime() + " " + timetableItem.getEndingTime() + " " + timetableItem.getDay();

                        //calculate reminder time
                        Calendar slotStartingTime = Calendar.getInstance();
                        slotStartingTime.setTime((Date) timetableItem.getStartingDateTime());
                        System.out.println(slotStartingTime.getTime());

                        int startingHours = slotStartingTime.get(Calendar.HOUR_OF_DAY);
                        int startingMinutes = slotStartingTime.get(Calendar.MINUTE);
                        int totalMinutes = ((startingHours * 60) + startingMinutes) - minutes;
                        int newHours = totalMinutes / 60;
                        int newMinutes = totalMinutes % 60;

                        Calendar reminderTime = Calendar.getInstance();
                        reminderTime.set(Calendar.HOUR_OF_DAY, newHours);
                        reminderTime.set(Calendar.MINUTE, newMinutes);
                        reminderTime.set(Calendar.SECOND, 0);
                        reminderTime.set(Calendar.MILLISECOND, 0);

                        Date newReminderDateTime = reminderTime.getTime();

                        Reminder tempReminder = new Reminder(timetableItem.getDay(), timetableItem.getEndingTime(), newReminderDateTime, slotId, timetableItem.getStartingTime(), timetableItem.getSubjectCode(), timetableItem.getSubjectName(), CurrentUser.getUserId());

                        newReminderList.add(tempReminder);
                        Log.i(TAG, tempReminder.getReminderItemId() + tempReminder.getReminderDateTime());
                        System.out.println("NRLS"+newReminderList.size());
                    }

                    if (newReminderList.size() > 0) {
                        pendingIntents = newReminderList.size();
                        Log.i(TAG,pendingIntents +  " pending reminders found.");

                        //save new ones
                        for (final Reminder newReminder : newReminderList) {
                            DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).collection("reminders").document(newReminder.getReminderItemId());
                            documentReference.set(newReminder).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Reminder for \"" + timetableName + " " + "\" added successfully.");

                                        //create alarms (notification reminders)
                                        Calendar today = Calendar.getInstance();
                                        //SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
                                        // String todayTime = dateFormat.format(today.getTime());
                                        String dayLongNameOfToday = today.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

                                        Date reminderDate = newReminder.getReminderDateTime();
                                        Calendar reminderDateCalender = Calendar.getInstance();
                                        reminderDateCalender.setTime(reminderDate);
                                        reminderDateCalender.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH));
                                        reminderDateCalender.set(Calendar.YEAR, today.get(Calendar.YEAR));
                                        reminderDateCalender.set(Calendar.MONTH, today.get(Calendar.MONTH));

                                        String dayLongNameOfReminder;

                                        switch(newReminder.getDay()){
                                            case 1:
                                                dayLongNameOfReminder = "Monday";
                                                break;
                                            case 2:
                                                dayLongNameOfReminder = "Tuesday";
                                                break;
                                            case 3:
                                                dayLongNameOfReminder = "Wednesday";
                                                break;
                                            case 4:
                                                dayLongNameOfReminder = "Thursday";
                                                break;
                                            case 5:
                                                dayLongNameOfReminder = "Friday";
                                                break;
                                            case 6:
                                                dayLongNameOfReminder = "Saturday";
                                                break;
                                            case 7:
                                                dayLongNameOfReminder = "Sunday";
                                                break;
                                            default:
                                                dayLongNameOfReminder = "N/A";
                                                break;
                                        }

                                        if (dayLongNameOfToday.equalsIgnoreCase(dayLongNameOfReminder) == false){
                                            if (dayLongNameOfToday.equalsIgnoreCase("Monday")){
                                                switch(newReminder.getDay()){
                                                    //case 1:
                                                    //   reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+0);
                                                    //   break;
                                                    case 2:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+1);
                                                        break;
                                                    case 3:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+2);
                                                        break;
                                                    case 4:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+3);
                                                        break;
                                                    case 5:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+4);
                                                        break;
                                                    case 6:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+5);
                                                        break;
                                                    case 7:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+6);
                                                        break;
                                                    default:
                                                        //do nothing
                                                        //scenario is impossible
                                                        break;
                                                }
                                            } else if (dayLongNameOfToday.equalsIgnoreCase("Tuesday")){
                                                switch(newReminder.getDay()){
                                                    case 1:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+6);
                                                        break;
                                                    //case 2:
                                                    //    reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+0);
                                                    //    break;
                                                    case 3:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+1);
                                                        break;
                                                    case 4:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+2);
                                                        break;
                                                    case 5:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+3);
                                                        break;
                                                    case 6:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+4);
                                                        break;
                                                    case 7:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+5);
                                                        break;
                                                    default:
                                                        //do nothing
                                                        //scenario is impossible
                                                        break;
                                                }
                                            } else if (dayLongNameOfToday.equalsIgnoreCase("Wednesday")){
                                                switch(newReminder.getDay()){
                                                    case 1:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+5);
                                                        break;
                                                    case 2:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+6);
                                                        break;
                                                    //case 3:
                                                    //    reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+1);
                                                    //    break;
                                                    case 4:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+1);
                                                        break;
                                                    case 5:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+2);
                                                        break;
                                                    case 6:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+3);
                                                        break;
                                                    case 7:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+4);
                                                        break;
                                                    default:
                                                        //do nothing
                                                        //scenario is impossible
                                                        break;
                                                }
                                            } else if (dayLongNameOfToday.equalsIgnoreCase("Thursday")){
                                                switch(newReminder.getDay()){
                                                    case 1:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+4);
                                                        break;
                                                    case 2:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+5);
                                                        break;
                                                    case 3:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+6);
                                                        break;
                                                    //case 4:
                                                    //    reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+5);
                                                    //    break;
                                                    case 5:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+1);
                                                        break;
                                                    case 6:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+2);
                                                        break;
                                                    case 7:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+3);
                                                        break;
                                                    default:
                                                        //do nothing
                                                        //scenario is impossible
                                                        break;
                                                }
                                            } else if (dayLongNameOfToday.equalsIgnoreCase("Friday")){
                                                switch(newReminder.getDay()){
                                                    case 1:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+3);
                                                        break;
                                                    case 2:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+4);
                                                        break;
                                                    case 3:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+5);
                                                        break;
                                                    case 4:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+6);
                                                        break;
                                                    //case 5:
                                                    //    reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+3);
                                                    //    break;
                                                    case 6:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+1);
                                                        break;
                                                    case 7:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+2);
                                                        break;
                                                    default:
                                                        //do nothing
                                                        //scenario is impossible
                                                        break;
                                                }
                                            } else if (dayLongNameOfToday.equalsIgnoreCase("Saturday")){
                                                switch(newReminder.getDay()){
                                                    case 1:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+2);
                                                        break;
                                                    case 2:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+3);
                                                        break;
                                                    case 3:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+4);
                                                        break;
                                                    case 4:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+5);
                                                        break;
                                                    case 5:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+6);
                                                        break;
                                                    //case 6:
                                                    //    reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+4);
                                                    //    break;
                                                    case 7:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+1);
                                                        break;
                                                    default:
                                                        //do nothing
                                                        //scenario is impossible
                                                        break;
                                                }
                                            } else if (dayLongNameOfToday.equalsIgnoreCase("Sunday")){
                                                switch(newReminder.getDay()){
                                                    case 1:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+1);
                                                        break;
                                                    case 2:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+2);
                                                        break;
                                                    case 3:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+3);
                                                        break;
                                                    case 4:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+4);
                                                        break;
                                                    case 5:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+5);
                                                        break;
                                                    case 6:
                                                        reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+6);
                                                        break;
                                                    //case 7:
                                                    //    reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+5);
                                                    //    break;
                                                    default:
                                                        //do nothing
                                                        //scenario is impossible
                                                        break;
                                                }
                                            }
                                            System.out.println(today.getTime()+"\n"+reminderDateCalender.getTime() + newReminder.getReminderItemId() + " AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBNOTEQUAL");

                                        } else if (dayLongNameOfToday.equalsIgnoreCase(dayLongNameOfReminder) == true){
                                            if (reminderDateCalender.compareTo(today) <= 0){
                                                reminderDateCalender.set(Calendar.DATE, reminderDateCalender.get(Calendar.DATE)+7);
                                            } else {
                                                //no issue
                                            }
                                        }

                                        String body = "Hey, you have a lecture for " + newReminder.getSubjectName()+ " ("+newReminder.getSubjectCode()+") at "+newReminder.getStartingTime()+" Today.";

                                        Intent reminderIntent = new Intent(getApplicationContext(), ReminderBroadcastReceiver.class);
                                        reminderIntent.putExtra("requestCode", pendingIntents);
                                        reminderIntent.putExtra("title", "Ready for your next Lecture?");
                                        reminderIntent.putExtra("body", body);
                                        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), pendingIntents, reminderIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, reminderDateCalender.getTimeInMillis(),AlarmManager.INTERVAL_DAY * 7, pendingIntent);
                                        //For testing purposes
                                        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, reminderDateCalender.getTimeInMillis(),6000, pendingIntent); //1 min
                                        Log.i("REMINDER_CR", "Reminder Created for "+ newReminder.getReminderItemId()+" AT " + reminderDateCalender.getTime());
                                        pendingIntents--; //unique request code for each alarm in descending order of the number of reminders
                                    } else {
                                        Log.d(TAG, "unsuccessful.");
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    private void updateAllRemindersOfCurrentUser(final boolean enabledState, final int minutes){
        //delete all existing reminders
        DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        final CollectionReference collectionReferenceForDelete = documentReference.collection("reminders");
        collectionReferenceForDelete.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        reminderCounter = task.getResult().size();
                        Log.i(TAG, "Total number of reminders found: " + reminderCounter);

                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            Reminder rem = queryDocumentSnapshot.toObject(Reminder.class);
                            queryDocumentSnapshot.getReference().delete();
                            Log.w(TAG, "Reminder" + rem.getReminderItemId() +" flushed from FIREBASE for inserting new ones. (VTTL)");

                            //deleting reminders from device if exists
                            Intent reminderIntent = new Intent(getApplicationContext(), ReminderBroadcastReceiver.class);
                            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), reminderCounter, reminderIntent, PendingIntent.FLAG_NO_CREATE);
                            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                            if (pendingIntent!=null) {
                                alarmManager.cancel(pendingIntent);
                                Log.i(TAG,  "(PA) Local Reminder deleted for "+ rem.getReminderItemId());
                            }
                            reminderCounter--; //unique request code for each alarm in descending order of the number of reminders
                        }
                    }
                }
                if(enabledState == true){
                    addNewReminders(minutes);
                } else if (enabledState == false){
                }
            }
        });
    }

    private void updateReminderSettingsOfCurrentUser(boolean enabledState, int minutes){
        //update user details
        Map<String, Object> userReminderSettingsUpdate = new HashMap<>();
        userReminderSettingsUpdate.put("isRemindersOn", enabledState);
        userReminderSettingsUpdate.put("remainderMinutes", minutes);
        DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.update(userReminderSettingsUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "new reminder settings updated successfully. (VTTL)");
            }
        });
    }

    private void showToast (String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    //get updates
    private void subscribeForNotices(){
        FirebaseMessaging.getInstance().subscribeToTopic("notifications").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //showToast("Voila");
                } else {
                    //showToast("Failed");
                }
            }
        });
    }
}