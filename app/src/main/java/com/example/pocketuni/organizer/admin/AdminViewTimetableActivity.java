package com.example.pocketuni.organizer.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pocketuni.R;
import com.example.pocketuni.model.Timetable;
import com.example.pocketuni.model.TimetableItem;
import com.example.pocketuni.organizer.common.TimetableListAdapter;
import com.example.pocketuni.organizer.weekfragments.FridayFragment;
import com.example.pocketuni.organizer.weekfragments.MondayFragment;
import com.example.pocketuni.organizer.weekfragments.SaturdayFragment;
import com.example.pocketuni.organizer.weekfragments.SundayFragment;
import com.example.pocketuni.organizer.weekfragments.ThursdayFragment;
import com.example.pocketuni.organizer.weekfragments.TimetablePagerAdapter;
import com.example.pocketuni.organizer.weekfragments.TuesdayFragment;
import com.example.pocketuni.organizer.weekfragments.WednesdayFragment;
import com.example.pocketuni.security.SigninActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminViewTimetableActivity extends AppCompatActivity implements AddTimetableSlotDialog.AddTimetableSlotDialogListener {
    private FloatingActionButton addTimetableSlot;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView timetableInfo, textViewTimetableInfoError;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String timetableName, timetableBatch, timetableInfoText;
    private TimetableItem timetableSlot;
    private List<TimetableItem> timetableItems = new ArrayList<TimetableItem>();
    private List<TimetableItem> timetableItemsMonday = new ArrayList<TimetableItem>();
    private List<TimetableItem> timetableItemsTuesday = new ArrayList<TimetableItem>();
    private List<TimetableItem> timetableItemsWednesday = new ArrayList<TimetableItem>();
    private List<TimetableItem> timetableItemsThursday = new ArrayList<TimetableItem>();
    private List<TimetableItem> timetableItemsFriday = new ArrayList<TimetableItem>();
    private List<TimetableItem> timetableItemsSaturday = new ArrayList<TimetableItem>();
    private List<TimetableItem> timetableItemsSunday = new ArrayList<TimetableItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_timetable);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //redirect if an invalid user
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), SigninActivity.class));
            finish();
        }

        collapsingToolbarLayout = findViewById(R.id.timetables_toolbar_layout);
        timetableInfo = findViewById(R.id.textViewTimetableInfo);
        textViewTimetableInfoError = findViewById(R.id.textViewTimetableInfoError);

        //collapsingToolbarLayout.setTitle(getIntent().getExtras().getString("timetableName"));
        this.timetableName = getIntent().getExtras().getString("timetableName");
        this.timetableBatch = getIntent().getExtras().getString("timetableBatch");
        this.timetableInfoText = getIntent().getExtras().getString("timetableCourse");

        collapsingToolbarLayout.setTitle(this.timetableBatch);
        timetableInfo.setText(this.timetableInfoText);
        textViewTimetableInfoError.setText("");

        tabLayout = findViewById(R.id.timetableTabLayout);
        viewPager = findViewById(R.id.timetableViewPager);

        addTimetableSlot = findViewById(R.id.addTimetableSlotsFloatingActionButton);

        addTimetableSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTimetableSlotDialog();
            }
        });

        //show tablayout
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

        getAvailableTimetableSlots();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getAvailableTimetableSlots();
    }

    private void showAddTimetableSlotDialog () {
        AddTimetableSlotDialog addTimetableSlotDialog = new AddTimetableSlotDialog();
        addTimetableSlotDialog.show(getSupportFragmentManager(), "Add Timetable Slot Dialog");
    }

    private void showToast (String message) {
        Toast.makeText(AdminViewTimetableActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getNewTimetableSlotData(String courseCode, String courseName, String lecInCharge, String day, String location, String sTime, String eTime, Date startTime, Date endTime) {
        final Map<String, Object> newTimetableSlot = new HashMap<>();
        final String slotId = day + " " + sTime;
        newTimetableSlot.put("itemId", slotId);
        newTimetableSlot.put("subjectCode", courseCode);
        newTimetableSlot.put("subjectName", courseName);
        newTimetableSlot.put("startingTime", sTime);
        newTimetableSlot.put("endingTime", eTime);
        newTimetableSlot.put("day", day);
        newTimetableSlot.put("lecturerInCharge", lecInCharge);
        newTimetableSlot.put("location", location);
        newTimetableSlot.put("startingDateTime", startTime);
        newTimetableSlot.put("endingDateTime", endTime);

        final DocumentReference documentReference = firebaseFirestore.collection("timetables").document(timetableName).collection("slots").document(slotId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if(document.exists()){
                    showToast("TIMETABLE SLOT ALREADY EXISTS.");
                }
                else{
                    documentReference.set(newTimetableSlot).addOnSuccessListener(new OnSuccessListener<Void>() {
                        private static final String TAG = "ADD_TIMETABLE_SLOT";

                        @Override
                        public void onSuccess(Void aVoid) {

                            Log.d(TAG, "Timetable slot \"" + timetableName + " " + slotId + "\" created successfully.");

                            showToast("TIMETABLE SLOT ADDED SUCCESSFULLY.");
                            //load new data
                            getAvailableTimetableSlots();
                        }
                    });
                }
            }
        });
    }

    private void getAvailableTimetableSlots(){
        /*//show currently available timetables
        CollectionReference collectionReference = firebaseFirestore.collection("timetables").document(timetableName).collection("slots");
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()) {
                    textViewTimetableInfoError.setText("");
                    for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        //putting into a list of slots
                        TimetableItem timetableItem = documentSnapshot.toObject(TimetableItem.class);
                        timetableItems.add(timetableItem);

                        switch(timetableItem.getDay()){
                            case "Monday":
                                timetableItemsMonday.add(timetableItem);
                                break;
                            case "Tuesday":
                                timetableItemsTuesday.add(timetableItem);
                                break;
                            case "Wednesday":
                                timetableItemsWednesday.add(timetableItem);
                                break;
                            case "Thursday":
                                timetableItemsThursday.add(timetableItem);
                                break;
                            case "Friday":
                                timetableItemsFriday.add(timetableItem);
                                break;
                            case "Saturday":
                                timetableItemsSaturday.add(timetableItem);
                                break;
                            case "Sunday":
                                timetableItemsSunday.add(timetableItem);
                                break;
                            default:
                                //do nothing
                                break;
                        }
                    }

                } else {
                    textViewTimetableInfoError.setText(getResources().getString(R.string.admin_timetableslot_description_error));
                    showToast("NO TIMETABLE SLOTS.");
                }
            }
        });*/


    }
}