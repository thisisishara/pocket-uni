package com.example.pocketuni.messenger.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pocketuni.R;
import com.example.pocketuni.messenger.common.ChatsFragment;
import com.example.pocketuni.messenger.common.MessengerPagerAdapter;
import com.example.pocketuni.messenger.common.UsersFragment;
import com.example.pocketuni.model.CurrentUser;
import com.example.pocketuni.security.SigninActivity;
import com.example.pocketuni.util.AdminBottomNavigationHelper;
import com.example.pocketuni.util.StdBottomNavigationHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AdminChatActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Context context = AdminChatActivity.this;
    private static final int ACTIVITY_NUMBER = 1;
    FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private String TAG = "STD_CHAT_MAIN";

    private ImageView closeInfoBox;
    private CardView infoBox;
    private LinearLayout infoBoxContainer;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView txv;
    private String str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.activity_admin_chat);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //redirect if already logged in
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), SigninActivity.class));
            finish();
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        AdminBottomNavigationHelper.enableNavigation(context, bottomNavigationView, ACTIVITY_NUMBER);

        closeInfoBox = findViewById(R.id.infoBoxClose);
        infoBox = findViewById(R.id.infoBox);
        infoBoxContainer = findViewById(R.id.infoBoxContainer);
        tabLayout = findViewById(R.id.messengerTabs);
        viewPager = findViewById(R.id.messengerViewPager);

        MessengerPagerAdapter messengerPagerAdapter = new MessengerPagerAdapter((getSupportFragmentManager()));
        messengerPagerAdapter.addFragment(new UsersFragment(), "Users");
        messengerPagerAdapter.addFragment(new ChatsFragment(), "Chats");

        viewPager.setAdapter(messengerPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        final int position = 1;
        tabLayout.selectTab(tabLayout.getTabAt(position));

        //set session info one time
        getUserStatus();
    }

    public void CloseInfoBox(View view) {
        infoBoxContainer.setVisibility(View.GONE);
    }

    private void getUserStatus(){
        DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    CurrentUser.setEmail((String) task.getResult().get("email"));
                    CurrentUser.setName((String) task.getResult().get("name"));
                    CurrentUser.setProfilePicture((Image) task.getResult().get("profile_pic"));
                    CurrentUser.setDp((String) task.getResult().get("dp"));
                    CurrentUser.setUserType((String) task.getResult().get("userType"));
                    CurrentUser.setUserId((String) task.getResult().get("userId"));
                }
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
}