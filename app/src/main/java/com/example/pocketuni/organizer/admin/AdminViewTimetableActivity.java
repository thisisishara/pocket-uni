package com.example.pocketuni.organizer.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pocketuni.R;
import com.example.pocketuni.organizer.weekfragments.FridayFragment;
import com.example.pocketuni.organizer.weekfragments.MondayFragment;
import com.example.pocketuni.organizer.weekfragments.SaturdayFragment;
import com.example.pocketuni.organizer.weekfragments.SundayFragment;
import com.example.pocketuni.organizer.weekfragments.ThursdayFragment;
import com.example.pocketuni.organizer.weekfragments.TimetablePagerAdapter;
import com.example.pocketuni.organizer.weekfragments.TuesdayFragment;
import com.example.pocketuni.organizer.weekfragments.WednesdayFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class AdminViewTimetableActivity extends AppCompatActivity implements AddTimetableSlotDialog.AddTimetableSlotDialogListener {
    FloatingActionButton addTimetableSlot;
    String tempResult; //temp

    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_timetable);

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

    public void showAddTimetableSlotDialog () {
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
}