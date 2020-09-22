package com.example.pocketuni.util;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.pocketuni.R;
import com.example.pocketuni.messenger.admin.AdminChatActivity;
import com.example.pocketuni.profile.admin.AdminProfileActivity;
import com.example.pocketuni.organizer.admin.AdminOrganizerActivity;
import com.example.pocketuni.results.admin.AdminResultsActivity;
import com.example.pocketuni.timeline.admin.AdminActivity;
import com.example.pocketuni.organizer.std.OrganizerActivity;
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
                        intent = new Intent(context, AdminOrganizerActivity.class);
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
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
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
