package com.example.pocketuni.organizer.std;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pocketuni.R;
import com.example.pocketuni.model.CurrentUser;
import com.example.pocketuni.model.Reminder;
import com.example.pocketuni.model.TimetableItem;
import com.example.pocketuni.organizer.admin.AddTimetableSlotDialog;
import com.example.pocketuni.organizer.admin.AdminViewTimetableActivity;
import com.example.pocketuni.organizer.common.TimetableSlotListAdapter;
import com.example.pocketuni.organizer.weekfragments.FridayFragment;
import com.example.pocketuni.organizer.weekfragments.MondayFragment;
import com.example.pocketuni.organizer.weekfragments.SaturdayFragment;
import com.example.pocketuni.organizer.weekfragments.SundayFragment;
import com.example.pocketuni.organizer.weekfragments.ThursdayFragment;
import com.example.pocketuni.organizer.weekfragments.TimetablePagerAdapter;
import com.example.pocketuni.organizer.weekfragments.TuesdayFragment;
import com.example.pocketuni.organizer.weekfragments.WednesdayFragment;
import com.example.pocketuni.timeline.std.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

public class ViewTimetableActivity extends AppCompatActivity implements TimetableReminderDialog.TimetableReminderDialogListener {
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton setTimetableReminderButton;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView timetableInfo, textViewTimetableInfoError;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private String timetableName, timetableBatch, timetableInfoText;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private int pendingIntents = 0;
    private List<TimetableItem> timetableItemList = new ArrayList<TimetableItem>();
    private List<Reminder> newReminderList = new ArrayList<Reminder>();
    private int reminderCounter = 0;
    private String TAG = "STD_TTL_VTTL";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_timetable);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser= firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        collapsingToolbarLayout = findViewById(R.id.std_timetable_toolbar_layout);
        setTimetableReminderButton = findViewById(R.id.addTimetableRemindersFloatingActionButton);
        timetableInfo = findViewById(R.id.textViewStdTimetableInfo);
        textViewTimetableInfoError = findViewById(R.id.textViewStdTimetableInfoError);
        tabLayout = findViewById(R.id.stdTimetableTabLayout);
        viewPager = findViewById(R.id.stdTimetableViewPager);

        //collapsingToolbarLayout.setTitle(getIntent().getExtras().getString("timetableName"));
        this.timetableName = CurrentUser.getCourse()+" "+CurrentUser.getBatch();
        this.timetableBatch = CurrentUser.getBatch();
        this.timetableInfoText = CurrentUser.getCourse();

        collapsingToolbarLayout.setTitle(this.timetableBatch);
        timetableInfo.setText(this.timetableInfoText);
        textViewTimetableInfoError.setText("");

        TimetablePagerAdapter timetablePagerAdapter = new TimetablePagerAdapter(getSupportFragmentManager());
        timetablePagerAdapter.addFragment(new MondayFragment(timetableName), "MON");
        timetablePagerAdapter.addFragment(new TuesdayFragment(timetableName), "TUE");
        timetablePagerAdapter.addFragment(new WednesdayFragment(timetableName), "WED");
        timetablePagerAdapter.addFragment(new ThursdayFragment(timetableName), "THU");
        timetablePagerAdapter.addFragment(new FridayFragment(timetableName), "FRI");
        timetablePagerAdapter.addFragment(new SaturdayFragment(timetableName), "SAT");
        timetablePagerAdapter.addFragment(new SundayFragment(timetableName), "SUN");

        viewPager.setAdapter(timetablePagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        setTimetableReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimetableReminderDialog();
            }
        });
    }

    public void showTimetableReminderDialog () {
        TimetableReminderDialog timetableReminderDialog = new TimetableReminderDialog();
        timetableReminderDialog.show(getSupportFragmentManager(), "Add Timetable Reminder Dialog");
    }

    @Override
    public void getNewTimetableReminderData(boolean enabledState, int minutes) {
        updateReminderSettingsOfCurrentUser(enabledState, minutes);
        updateAllRemindersOfCurrentUser(enabledState, minutes);
    }

    private void showToast (String message) {
        Toast.makeText(ViewTimetableActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void addNewReminders(final int minutes){
        CollectionReference collectionReference = firebaseFirestore.collection("timetables").document(timetableName).collection("slots");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, "NEW RMD: " + document.getId() + " => " + document.getData());

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

                    if (isGreaterThanZero(newReminderList.size()) == true) {
                        pendingIntents = newReminderList.size();
                        Log.i(TAG,pendingIntents +  " pending reminders found.");

                        for (final Reminder newReminder : newReminderList) {
                            DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).collection("reminders").document(newReminder.getReminderItemId());
                            documentReference.set(newReminder).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
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

                                        String dayLongNameOfReminder = getDayLongNameOfDate(newReminder.getDay());

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
                                        //For Testing Purposes, set repeat time to 1min
                                        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, reminderDateCalender.getTimeInMillis(),6000, pendingIntent); //1 min
                                        Log.i("REMINDER_CR", "Reminder Created for "+ newReminder.getReminderItemId()+" AT " + reminderDateCalender.getTime());
                                        pendingIntents--; //unique request code for each alarm in descending order of the number of reminders
                                    }else {
                                        Log.d(TAG, "unsuccessful.");
                                    }
                                }
                            });
                        }
                        showToast("REMINDERS ARE ALL SET.");
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
                        Log.i(TAG,reminderCounter +  " existing reminders found.");

                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            queryDocumentSnapshot.getReference().delete();
                            Log.w(TAG, "all reminders flushed for inserting new ones. (VTTL)");

                            Intent reminderIntent = new Intent(getApplicationContext(), ReminderBroadcastReceiver.class);
                            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), reminderCounter, reminderIntent, PendingIntent.FLAG_NO_CREATE);
                            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                            if (pendingIntent!=null) {
                                alarmManager.cancel(pendingIntent);
                                Reminder rem = queryDocumentSnapshot.toObject(Reminder.class);
                                Log.i("REMINDER_DL", "Reminder deleted for "+ rem.getReminderItemId());
                            }
                            reminderCounter--; //unique request code for each alarm in descending order of the number of reminders
                        }
                    }
                }
                if(enabledState == true){
                    addNewReminders(minutes);
                } else if (enabledState == false){
                    showToast("REMINDERS REMOVED.");
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

    private void updateUserOnlineStatus(String status){
        HashMap<String,Object> userStatus = new HashMap<String, Object>();
        userStatus.put("status", status);

        DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.update(userStatus);
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

    public boolean isGreaterThanZero(int size){
        if(size > 0){
            return true;
        } else {
            return false;
        }
    }

    public String getDayLongNameOfDate(int dateNumber){
        String dayLongNameOfDate = null;

        switch(dateNumber){
            case 1:
                dayLongNameOfDate = "Monday";
                break;
            case 2:
                dayLongNameOfDate = "Tuesday";
                break;
            case 3:
                dayLongNameOfDate = "Wednesday";
                break;
            case 4:
                dayLongNameOfDate = "Thursday";
                break;
            case 5:
                dayLongNameOfDate = "Friday";
                break;
            case 6:
                dayLongNameOfDate = "Saturday";
                break;
            case 7:
                dayLongNameOfDate = "Sunday";
                break;
            default:
                dayLongNameOfDate = "N/A";
                break;
        }

        return dayLongNameOfDate;
    }
}