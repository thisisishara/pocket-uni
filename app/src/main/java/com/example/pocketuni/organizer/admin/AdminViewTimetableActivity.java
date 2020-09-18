package com.example.pocketuni.organizer.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pocketuni.R;
import com.example.pocketuni.model.Timetable;
import com.example.pocketuni.organizer.weekfragments.FridayFragment;
import com.example.pocketuni.organizer.weekfragments.MondayFragment;
import com.example.pocketuni.organizer.weekfragments.SaturdayFragment;
import com.example.pocketuni.organizer.weekfragments.SundayFragment;
import com.example.pocketuni.organizer.weekfragments.ThursdayFragment;
import com.example.pocketuni.organizer.weekfragments.TimetablePagerAdapter;
import com.example.pocketuni.organizer.weekfragments.TuesdayFragment;
import com.example.pocketuni.organizer.weekfragments.WednesdayFragment;
import com.example.pocketuni.security.SigninActivity;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminViewTimetableActivity extends AppCompatActivity implements AddTimetableSlotDialog.AddTimetableSlotDialogListener {
    private FloatingActionButton addTimetableSlot;
    private String tempResult; //temp
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView timetableInfo;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String timetableName, timetableInfoText;

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

        //collapsingToolbarLayout.setTitle(getIntent().getExtras().getString("timetableName"));
        this.timetableName = getIntent().getExtras().getString("timetableBatch");
        this.timetableInfoText = getIntent().getExtras().getString("timetableCourse");

        collapsingToolbarLayout.setTitle(this.timetableName);
        timetableInfo.setText(this.timetableInfoText);

        tabLayout = findViewById(R.id.timetableTabLayout);
        viewPager = findViewById(R.id.timetableViewPager);

        TimetablePagerAdapter timetablePagerAdapter = new TimetablePagerAdapter(getSupportFragmentManager());
        timetablePagerAdapter.addFragment(new MondayFragment(), "MON");
        timetablePagerAdapter.addFragment(new TuesdayFragment(), "TUE");
        timetablePagerAdapter.addFragment(new WednesdayFragment(), "WED");
        timetablePagerAdapter.addFragment(new ThursdayFragment(), "THU");
        timetablePagerAdapter.addFragment(new FridayFragment(), "FRI");
        timetablePagerAdapter.addFragment(new SaturdayFragment(), "SAT");
        timetablePagerAdapter.addFragment(new SundayFragment(), "SUN");

        viewPager.setAdapter(timetablePagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        addTimetableSlot = findViewById(R.id.addTimetableSlotsFloatingActionButton);

        addTimetableSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTimetableSlotDialog();
            }
        });

    }

    private void showAddTimetableSlotDialog () {
        AddTimetableSlotDialog addTimetableSlotDialog = new AddTimetableSlotDialog();
        addTimetableSlotDialog.show(getSupportFragmentManager(), "Add Timetable Slot Dialog");
    }

    private void showToast (String message) {
        Toast.makeText(AdminViewTimetableActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getNewTimetableSlotData(String courseCode, String courseName, String lecInCharge, String day, String sTime, String eTime) {
        tempResult = courseCode+" "+courseName+" "+lecInCharge+" "+day+" "+sTime+" "+eTime+" ";
        showToast(tempResult);
    }

    private void getTimetableData(){

    }
}