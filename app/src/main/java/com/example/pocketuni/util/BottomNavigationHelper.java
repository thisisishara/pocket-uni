package com.example.pocketuni.util;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.pocketuni.R;
import com.example.pocketuni.messenger.ChatActivity;
import com.example.pocketuni.profile.ProfileActivity;
import com.example.pocketuni.reminder.ReminderActivity;
import com.example.pocketuni.results.ResultsActivity;
import com.example.pocketuni.timeline.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationHelper {

    private static final String TAG = "BottomNavigationViewHelper";

    public static void enableNavigation (final Context context, BottomNavigationView bottomNavigationView, int ACTIVITY_NUMBER) {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                switch (item.getItemId()){
                    case R.id.navigation_timeline:
                        intent = new Intent(context, MainActivity.class);
                        break;
                    case R.id.navigation_messenger:
                        intent = new Intent(context, ChatActivity.class);
                        break;
                    case R.id.navigation_reminder:
                        intent = new Intent(context, ReminderActivity.class);
                        break;
                    case R.id.navigation_results:
                        intent = new Intent(context, ResultsActivity.class);
                        break;
                    case R.id.navigation_profile:
                        intent = new Intent(context, ProfileActivity.class);
                        break;
                    default:
                        intent = null;
                        break;
                }

                if (intent != null) {
                    context.startActivity(intent);
                }

                return false;
            }
        });

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);
    }
}
