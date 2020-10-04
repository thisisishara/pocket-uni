package com.example.pocketuni.results.std;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pocketuni.model.CurrentUser;
import com.example.pocketuni.model.Result;
import com.example.pocketuni.results.admin.AdminResultsDisplay;
import com.example.pocketuni.results.admin.CustomAdapter;
import com.example.pocketuni.results.admin.ListActivity;
import com.example.pocketuni.util.StdBottomNavigationHelper;
import com.example.pocketuni.R;
import com.example.pocketuni.security.SigninActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ResultsActivity extends AppCompatActivity {
    private List<Result> resultListSemesters = new ArrayList<>();
    private TextView textViewGPA, tv;
    private BottomNavigationView bottomNavigationView;
    private Context context = ResultsActivity.this;
    private static final int ACTIVITY_NUMBER = 3;
    private static  final  String TAG = "RSTACT";
    private List<Result> resultList = new ArrayList<Result>();
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.activity_results);

        textViewGPA = findViewById(R.id.textViewCumulativeGPA);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //redirect if already logged in
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), SigninActivity.class));
            finish();
        }

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new ResultsY1S1Fragment(), "Y1S1");
        viewPagerAdapter.addFragment(new ResultsY1S2Fragment(), "Y1S2");
        viewPagerAdapter.addFragment(new ResultsY2S1Fragment(), "Y2S1");
        viewPagerAdapter.addFragment(new ResultsY2S2Fragment(), "Y2S2");
        viewPagerAdapter.addFragment(new ResultsY3S1Fragment(), "Y3S1");
        viewPagerAdapter.addFragment(new ResultsY3S2Fragment(), "Y3S2");
        viewPagerAdapter.addFragment(new ResultsY4S1Fragment(), "Y4S1");
        viewPagerAdapter.addFragment(new ResultsY4S2Fragment(), "Y4S2");

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        getUserStatus();
        //getYearAndSemesters();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        StdBottomNavigationHelper.enableNavigation(context, bottomNavigationView, ACTIVITY_NUMBER);
    }

    private void getUserStatus(){
        DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    CurrentUser.setEmail((String) task.getResult().get("email"));
                    CurrentUser.setName((String) task.getResult().get("name"));
                    CurrentUser.setBatch((String) task.getResult().get("batch"));
                    CurrentUser.setCourse((String) task.getResult().get("course"));
                    CurrentUser.setDp((String) task.getResult().get("dp"));
                    CurrentUser.setSemester((String) task.getResult().get("semester"));
                    CurrentUser.setYear((String) task.getResult().get("academic_year"));
                    CurrentUser.setUserType((String) task.getResult().get("userType"));
                    CurrentUser.setUserId((String) task.getResult().get("userId"));
                    CurrentUser.setIsRemindersOn((Boolean) task.getResult().get("isRemindersOn"));
                    CurrentUser.setRemainderMinutes(((Long) task.getResult().get("remainderMinutes")).intValue());

                    calculateAndViewCumulativeGPA();
                }
                //set listeners to get realtime updates
                startListeningToUserChanges();
            }
        });
    }

    private void startListeningToUserChanges(){
        DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "User: Listen failed. (LN)");
                    return;
                }
                if (documentSnapshot.exists()){
                    CurrentUser.setEmail(firebaseAuth.getCurrentUser().getEmail());
                    CurrentUser.setName((String) documentSnapshot.get("name"));
                    CurrentUser.setBatch((String) documentSnapshot.get("batch"));
                    CurrentUser.setCourse((String) documentSnapshot.get("course"));
                    CurrentUser.setDp((String) documentSnapshot.get("dp"));
                    CurrentUser.setSemester((String) documentSnapshot.get("semester"));
                    CurrentUser.setYear((String) documentSnapshot.get("academic_year"));
                    CurrentUser.setUserType((String) documentSnapshot.get("userType"));
                    CurrentUser.setUserId((String) firebaseAuth.getCurrentUser().getUid());
                    CurrentUser.setIsRemindersOn((Boolean) documentSnapshot.get("isRemindersOn"));
                    CurrentUser.setEmail((String) documentSnapshot.get("email"));
                    CurrentUser.setRemainderMinutes(((Long) documentSnapshot.get("remainderMinutes")).intValue());
                    CurrentUser.setUserId((String) documentSnapshot.get("userId"));

                } else {
                    Log.w(TAG, "User not found. (LN)");
                }
            }
        });
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm){
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

    }

    private void calculateAndViewCumulativeGPA(){
        final String TAG = "CUM_GPA";

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        String email = CurrentUser.getEmail();

        resultList.clear();
        int count = 0;

        for(int mainCount=1; mainCount<9; mainCount++) {
            count++;
            final int stepCount = count;

            String yearAndSem = "Year One Semester One";

            switch(mainCount){
                case 1:
                    yearAndSem = "Year One Semester One";
                    break;
                case 2:
                    yearAndSem = "Year One Semester Two";
                    break;
                case 3:
                    yearAndSem = "Year Two Semester One";
                    break;
                case 4:
                    yearAndSem = "Year Two Semester Two";
                    break;
                case 5:
                    yearAndSem = "Year Three Semester One";
                    break;
                case 6:
                    yearAndSem = "Year Three Semester Two";
                    break;
                case 7:
                    yearAndSem = "Year Four Semester One";
                    break;
                case 8:
                    yearAndSem = "Year Four Semester Two";
                    break;
            }

            CollectionReference collectionReference = firebaseFirestore.collection("Students").document(email).collection("Results").document(yearAndSem).collection("Modules");
            collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        if (task.getResult() != null) {
                            Log.d(TAG, "Current module data: [" + stepCount +"] : " + "" + task.getResult().getDocumentChanges());

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Result newResult = documentSnapshot.toObject(Result.class);
                                resultList.add(newResult);
                            }

                            if(stepCount == 8){
                                processData();
                            }

                        } else {
                            Log.d(TAG, "Current data: null");
                            showToast("NO RESULTS");
                        }
                    }
                }
            });
        }
    }

    private void processData() {
        double gradePoint = 0.0;
        Log.i("AAA", resultList.size()+"");

        for (Result result : resultList) {
            gradePoint += getGradePoint(result.getGrades());
        }
        if (resultList.size() == 0) {
            // showToast("NO RESULTS");
        } else {
            textViewGPA.setText(String.format("%.2f",gradePoint/resultList.size()));
            Log.i("AAA", gradePoint + "/" + resultList.size()+" :" +gradePoint/resultList.size()+"");
        }
    }

    public double getGradePoint(String grade){
        double gradePoint = 0.0;

        if(grade.equals("A+")){
            gradePoint = 4.0;
        } else if(grade.equals("A")){
            gradePoint = 4.0;
        } else if(grade.equals("A-")){
            gradePoint = 3.7;
        } else if(grade.equals("B+")){
            gradePoint = 3.3;
        } else if(grade.equals("B")){
            gradePoint = 3.0;
        } else if(grade.equals("B-")){
            gradePoint = 2.7;
        } else if(grade.equals("C+")){
            gradePoint = 2.3;
        } else if(grade.equals("C")){
            gradePoint = 2.0;
        } else if(grade.equals("C-")){
            gradePoint = 1.7;
        } else if(grade.equals("D+")){
            gradePoint = 1.3;
        } else if(grade.equals("D")){
            gradePoint = 1.0;
        } else if(grade.equals("E")){
            gradePoint = 0.0;
        }
        return gradePoint;
    }

    private void showToast (String message) {
        Toast.makeText(ResultsActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private String getYearAndSem(int i){
        StringBuilder yearAndSem = new StringBuilder("");
        if (i == 0){
            yearAndSem.append("Year One Semester One");
        } else if (i == 1){
            yearAndSem.append("Year One Semester Two");
        } else if (i == 2){
            yearAndSem.append("Year Two Semester One");
        } else if (i == 3){
            yearAndSem.append("Year Two Semester Two");
        } else if (i == 4){
            yearAndSem.append("Year Three Semester One");
        } else if (i == 5){
            yearAndSem.append("Year Three Semester Two");
        } else if (i == 6){
            yearAndSem.append("Year Four Semester One");
        } else if (i == 7){
            yearAndSem.append("Year Four Semester Two");
        }
        return yearAndSem.toString();
    }
}