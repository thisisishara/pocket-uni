package com.example.pocketuni.organizer.std;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.pocketuni.R;
import com.example.pocketuni.organizer.admin.AddTimetableSlotDialog;
import com.example.pocketuni.organizer.admin.AdminViewTimetableActivity;
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

public class ViewTimetableActivity extends AppCompatActivity implements TimetableReminderDialog.TimetableReminderDialogListener {

    FloatingActionButton setTimetableReminderButton;
    String message; //temp

    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_timetable);

        tabLayout = findViewById(R.id.timetableTabLayout);
        viewPager = findViewById(R.id.timetableViewPager);

        TimetablePagerAdapter timetablePagerAdapter = new TimetablePagerAdapter(getSupportFragmentManager());
        timetablePagerAdapter.addFragment(new MondayFragment(null), "MON");
        timetablePagerAdapter.addFragment(new TuesdayFragment(null), "TUE");
        timetablePagerAdapter.addFragment(new WednesdayFragment(null), "WED");
        timetablePagerAdapter.addFragment(new ThursdayFragment(null), "THU");
        timetablePagerAdapter.addFragment(new FridayFragment(null), "FRI");
        timetablePagerAdapter.addFragment(new SaturdayFragment(null), "SAT");
        timetablePagerAdapter.addFragment(new SundayFragment(null), "SUN");

        viewPager.setAdapter(timetablePagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        setTimetableReminderButton = findViewById(R.id.addTimetableRemindersFloatingActionButton);

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

    private void showToast (String message) {
        Toast.makeText(ViewTimetableActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getNewTimetableReminderData(String enabledState, String time) {
        message = enabledState+" "+time+" ";
        showToast(message);
    }
}