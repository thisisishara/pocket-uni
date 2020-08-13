package com.example.pocketuni.util;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.pocketuni.R;
import com.example.pocketuni.admin.messenger.AdminChatActivity;
import com.example.pocketuni.admin.profile.AdminProfileActivity;
import com.example.pocketuni.admin.organizer.AdminOrganizerActivity;
import com.example.pocketuni.admin.results.AdminResultsActivity;
import com.example.pocketuni.admin.timeline.AdminActivity;
import com.example.pocketuni.std.messenger.ChatActivity;
import com.example.pocketuni.std.profile.ProfileActivity;
import com.example.pocketuni.std.organizer.OrganizerActivity;
import com.example.pocketuni.std.results.ResultsActivity;
import com.example.pocketuni.std.timeline.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminBottomNavigationHelper {
    private static final String TAG = "StdBottomNavigationViewHelper";


    public static void enableNavigation (final Context context, BottomNavigationView bottomNavigationView, int ACTIVITY_NUMBER) {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                switch (item.getItemId()) {
                    case R.id.navigation_timeline:
                        intent = new Intent(context, AdminActivity.class);
                        break;
                    case R.id.navigation_messenger:
                        intent = new Intent(context, AdminChatActivity.class);
                        break;
                    case R.id.navigation_reminder:
                        intent = new Intent(context, com.example.pocketuni.admin.organizer.AdminOrganizerActivity.class);
                        break;
                    case R.id.navigation_results:
                        intent = new Intent(context, AdminResultsActivity.class);
                        break;
                    case R.id.navigation_profile:
                        intent = new Intent(context, AdminProfileActivity.class);
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
