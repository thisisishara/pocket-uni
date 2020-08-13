package com.example.pocketuni.util;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.pocketuni.R;
import com.example.pocketuni.std.messenger.ChatActivity;
import com.example.pocketuni.std.profile.ProfileActivity;
import com.example.pocketuni.std.organizer.OrganizerActivity;
import com.example.pocketuni.std.results.ResultsActivity;
import com.example.pocketuni.std.timeline.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StdBottomNavigationHelper {

    private static final String TAG = "StdBottomNavigationViewHelper";


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
                        intent = new Intent(context, OrganizerActivity.class);
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
