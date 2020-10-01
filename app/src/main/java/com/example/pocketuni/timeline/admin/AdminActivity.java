package com.example.pocketuni.timeline.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketuni.R;
import com.example.pocketuni.model.CurrentUser;
import com.example.pocketuni.model.NoticeItem;
import com.example.pocketuni.security.SigninActivity;
import com.example.pocketuni.timeline.AddPostActivity;
import com.example.pocketuni.timeline.common.TimelinePostsViewerAdapter;
import com.example.pocketuni.timeline.std.MainActivity;
import com.example.pocketuni.util.AdminBottomNavigationHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

public class AdminActivity extends AppCompatActivity {
    private static final int ACTIVITY_NUMBER = 0;
    private static final int USER_TYPE = 0;
    private static final String TAG = "ADMIN__TLN";
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton addPostFloatingActionButton;
    private List<NoticeItem> generalNotices = new ArrayList<NoticeItem>(), firstYearNotices = new ArrayList<NoticeItem>(), secondYearNotices = new ArrayList<NoticeItem>(), thirdYearNotices = new ArrayList<NoticeItem>(), fourthYearNotices = new ArrayList<NoticeItem>();
    private RecyclerView recyclerViewGeneralNotices, recyclerViewFirstYearNotices, recyclerViewSecondYearNotices, recyclerViewThirdYearNotices, recyclerViewFourthYearNotices;
    private LinearLayoutManager linearLayoutManagerGeneralNotices, linearLayoutManagerFirstYear, linearLayoutManagerSecondYear, linearLayoutManagerThirdYear, linearLayoutManagerFourthYear;
    private TimelinePostsViewerAdapter timelinePostsViewerAdapterGeneral, timelinePostsViewerAdapterFirst, timelinePostsViewerAdapterSecond, timelinePostsViewerAdapterThird, timelinePostsViewerAdapterFourth;
    private Context context = AdminActivity.this;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.activity_admin);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        //redirect if an invalid user
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), SigninActivity.class));
            finish();
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        AdminBottomNavigationHelper.enableNavigation(context, bottomNavigationView, ACTIVITY_NUMBER);

        recyclerViewGeneralNotices = findViewById(R.id.recyclerViewGeneralNotices);
        recyclerViewFirstYearNotices = findViewById(R.id.recyclerViewFirstYearNotices);
        recyclerViewSecondYearNotices = findViewById(R.id.recyclerViewSecondYearNotices);
        recyclerViewThirdYearNotices = findViewById(R.id.recyclerViewThirdYearNotices);
        recyclerViewFourthYearNotices = findViewById(R.id.recyclerViewFourthYearNotices);

        linearLayoutManagerGeneralNotices = new LinearLayoutManager(AdminActivity.this);
        linearLayoutManagerFirstYear = new LinearLayoutManager(AdminActivity.this);
        linearLayoutManagerSecondYear = new LinearLayoutManager(AdminActivity.this);
        linearLayoutManagerThirdYear = new LinearLayoutManager(AdminActivity.this);
        linearLayoutManagerFourthYear = new LinearLayoutManager(AdminActivity.this);

        linearLayoutManagerGeneralNotices = new LinearLayoutManager(AdminActivity.this);
        linearLayoutManagerGeneralNotices.setStackFromEnd(true);
        linearLayoutManagerGeneralNotices.setReverseLayout(true);
        linearLayoutManagerGeneralNotices.setSmoothScrollbarEnabled(true);
        recyclerViewGeneralNotices.setLayoutManager(linearLayoutManagerGeneralNotices);

        linearLayoutManagerFirstYear = new LinearLayoutManager(AdminActivity.this);
        linearLayoutManagerFirstYear.setStackFromEnd(true);
        linearLayoutManagerFirstYear.setReverseLayout(true);
        linearLayoutManagerFirstYear.setSmoothScrollbarEnabled(true);
        recyclerViewFirstYearNotices.setLayoutManager(linearLayoutManagerFirstYear);

        linearLayoutManagerSecondYear = new LinearLayoutManager(AdminActivity.this);
        linearLayoutManagerSecondYear.setStackFromEnd(true);
        linearLayoutManagerSecondYear.setReverseLayout(true);
        linearLayoutManagerSecondYear.setSmoothScrollbarEnabled(true);
        recyclerViewSecondYearNotices.setLayoutManager(linearLayoutManagerSecondYear);

        linearLayoutManagerThirdYear = new LinearLayoutManager(AdminActivity.this);
        linearLayoutManagerThirdYear.setStackFromEnd(true);
        linearLayoutManagerThirdYear.setReverseLayout(true);
        linearLayoutManagerThirdYear.setSmoothScrollbarEnabled(true);
        recyclerViewThirdYearNotices.setLayoutManager(linearLayoutManagerThirdYear);

        linearLayoutManagerFourthYear = new LinearLayoutManager(AdminActivity.this);
        linearLayoutManagerFourthYear.setStackFromEnd(true);
        linearLayoutManagerFourthYear.setReverseLayout(true);
        linearLayoutManagerFourthYear.setSmoothScrollbarEnabled(true);
        recyclerViewFourthYearNotices.setLayoutManager(linearLayoutManagerFourthYear);

        addPostFloatingActionButton = findViewById(R.id.addPostFloatingActionButton);

        getUserStatus();

        addPostFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, AddPostActivity.class);
                startActivity(intent);
            }
        });

        getAllNotices();
    }

    private void getAllNotices(){
        CollectionReference collectionReference = firebaseFirestore.collection("timelineposts");
        collectionReference.orderBy("noticeDate").addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed to timeline posts.", e);
                    return;
                }

                generalNotices.clear();
                firstYearNotices.clear();
                secondYearNotices.clear();
                thirdYearNotices.clear();
                fourthYearNotices.clear();

                if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() > 0) {
                    Log.d(TAG, "Current data: " + queryDocumentSnapshots.getDocumentChanges());

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        NoticeItem notice = documentSnapshot.toObject(NoticeItem.class);
                        if (notice.getYear().equalsIgnoreCase("0")) {
                            generalNotices.add(notice);
                        } else if (notice.getYear().equalsIgnoreCase("1")){
                            firstYearNotices.add(notice);
                        } else if (notice.getYear().equalsIgnoreCase("2")){
                            secondYearNotices.add(notice);
                        } else if (notice.getYear().equalsIgnoreCase("3")){
                            thirdYearNotices.add(notice);
                        } else if (notice.getYear().equalsIgnoreCase("4")){
                            fourthYearNotices.add(notice);
                        }

                        timelinePostsViewerAdapterGeneral = new TimelinePostsViewerAdapter(AdminActivity.this, generalNotices, USER_TYPE);
                        recyclerViewGeneralNotices.setAdapter(timelinePostsViewerAdapterGeneral);

                        timelinePostsViewerAdapterFirst = new TimelinePostsViewerAdapter(AdminActivity.this, firstYearNotices, USER_TYPE);
                        recyclerViewFirstYearNotices.setAdapter(timelinePostsViewerAdapterFirst);

                        timelinePostsViewerAdapterSecond = new TimelinePostsViewerAdapter(AdminActivity.this, secondYearNotices, USER_TYPE);
                        recyclerViewSecondYearNotices.setAdapter(timelinePostsViewerAdapterSecond);

                        timelinePostsViewerAdapterThird = new TimelinePostsViewerAdapter(AdminActivity.this, thirdYearNotices, USER_TYPE);
                        recyclerViewThirdYearNotices.setAdapter(timelinePostsViewerAdapterThird);

                        timelinePostsViewerAdapterFourth = new TimelinePostsViewerAdapter(AdminActivity.this, fourthYearNotices, USER_TYPE);
                        recyclerViewFourthYearNotices.setAdapter(timelinePostsViewerAdapterFourth);
                    }
                    Log.i(TAG, timelinePostsViewerAdapterGeneral.getItemCount() + " general notices were found.");
                    Log.i(TAG, timelinePostsViewerAdapterFirst.getItemCount() + " first year notices were found.");
                    Log.i(TAG, timelinePostsViewerAdapterSecond.getItemCount() + " second year notices were found.");
                    Log.i(TAG, timelinePostsViewerAdapterThird.getItemCount() + " third year notices were found.");
                    Log.i(TAG, timelinePostsViewerAdapterFourth.getItemCount() + " fourth year notices were found.");

                } else {
                    Log.d(TAG, "Current data: null");
                    timelinePostsViewerAdapterGeneral = new TimelinePostsViewerAdapter(AdminActivity.this, generalNotices, USER_TYPE);
                    recyclerViewGeneralNotices.setAdapter(timelinePostsViewerAdapterGeneral);

                    timelinePostsViewerAdapterFirst = new TimelinePostsViewerAdapter(AdminActivity.this, firstYearNotices, USER_TYPE);
                    recyclerViewFirstYearNotices.setAdapter(timelinePostsViewerAdapterFirst);

                    timelinePostsViewerAdapterSecond = new TimelinePostsViewerAdapter(AdminActivity.this, secondYearNotices, USER_TYPE);
                    recyclerViewSecondYearNotices.setAdapter(timelinePostsViewerAdapterSecond);

                    timelinePostsViewerAdapterThird = new TimelinePostsViewerAdapter(AdminActivity.this, thirdYearNotices, USER_TYPE);
                    recyclerViewThirdYearNotices.setAdapter(timelinePostsViewerAdapterThird);

                    timelinePostsViewerAdapterFourth = new TimelinePostsViewerAdapter(AdminActivity.this, fourthYearNotices, USER_TYPE);
                    recyclerViewFourthYearNotices.setAdapter(timelinePostsViewerAdapterFourth);
                }
            }
        });
    }

    private void showToast (String message) {
        Toast.makeText(AdminActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void getUserStatus(){
        final DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {

            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.d(TAG, "Current data: " + documentSnapshot.getData());

                    CurrentUser.setEmail(firebaseAuth.getCurrentUser().getEmail());
                    CurrentUser.setName((String) documentSnapshot.get("name"));
                    CurrentUser.setDp((String) documentSnapshot.get("dp"));
                    CurrentUser.setUserType((String) documentSnapshot.get("userType"));
                    CurrentUser.setUserId((String) userID);

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

        startListeningToUserChanges();
    }

    private void startListeningToUserChanges(){
        DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "User: Listen failed. (LN)");
                    return;
                }
                if (documentSnapshot.exists()){
                    CurrentUser.setEmail(firebaseAuth.getCurrentUser().getEmail());
                    CurrentUser.setDp((String) documentSnapshot.get("dp"));
                    CurrentUser.setUserType((String) documentSnapshot.get("userType"));
                    CurrentUser.setUserId((String) firebaseAuth.getCurrentUser().getUid());
                    CurrentUser.setEmail((String) documentSnapshot.get("email"));

                    getAllNotices();

                } else {
                    Log.w(TAG, "User not found. (LN)");
                }
            }
        });
    }

    private void updateUserOnlineStatus(String status){
        //firebaseFirestore = FirebaseFirestore.getInstance();
        //firebaseAuth = FirebaseAuth.getInstance();

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