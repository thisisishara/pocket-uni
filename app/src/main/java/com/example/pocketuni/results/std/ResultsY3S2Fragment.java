package com.example.pocketuni.results.std;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pocketuni.R;
import com.example.pocketuni.model.CurrentUser;
import com.example.pocketuni.model.Result;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.List;

public class ResultsY3S2Fragment extends Fragment {
    private View view;
    private TextView module, grade, caMarks, caPeriod, textViewSemGpa, textViewSemPassStatus;
    private TableLayout tableLayout;
    private TableRow tableRow;
    private List<Result> resultList = new ArrayList<>();
    private static  final  String TAG = "RST FRAG SIX";
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    private String email;

    public ResultsY3S2Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_results_y3_s2, container, false);
        textViewSemGpa = view.findViewById(R.id.textViewSemesterGPA);
        textViewSemPassStatus = view.findViewById(R.id.textViewSemesterStatus);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        tableLayout = view.findViewById(R.id.tableLayout);
        tableLayout.setColumnStretchable(0, true);
        tableLayout.setColumnStretchable(1, true);
        tableLayout.setColumnStretchable(2, true);
        tableLayout.setColumnStretchable(3, true);

        getUserStatus();
        return view;
    }

    private void showData() {
        final String TAG = "GET RSLTS FRAG SIX";

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        email = CurrentUser.getEmail();
        String yearAndSem = "Year Three Semester Two";

        CollectionReference collectionReference = firebaseFirestore.collection("Students").document(email).collection("Results").document(yearAndSem).collection("Modules");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                resultList.clear();
                tableLayout.removeAllViewsInLayout();
                tableLayout.removeAllViews();

                if (queryDocumentSnapshots != null) {
                    Log.d(TAG, "Current data: " + queryDocumentSnapshots.getDocumentChanges());

                    for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){

                        Result newResult = documentSnapshot.toObject(Result.class);
                        resultList.add(newResult);

                    }
                    assignData();

                } else {
                    Log.d(TAG, "Current data: null");
                    showToast("NO RESULTS");
                }
            }
        });
    }

    private void showToast (String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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

                    email = CurrentUser.getEmail();

                    showData();
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

    private void assignData(){
        int count = 0, passCounter = 0;
        double gradePoint = 0.0;

        tableRow = new TableRow(getContext());
        module = new TextView(getContext());
        grade = new TextView(getContext());
        caMarks = new TextView(getContext());
        caPeriod = new TextView(getContext());
        module.setText("Subject");
        module.setTextSize(15);
        module.setGravity(Gravity.CENTER);
        module.setPadding(0,0,0,10);
        grade.setText("Grade");
        grade.setTextSize(15);
        grade.setGravity(Gravity.CENTER);
        module.setPadding(0,0,0,10);
        caMarks.setText("CA Mark");
        caMarks.setTextSize(15);
        caMarks.setGravity(Gravity.CENTER);
        module.setPadding(0,0,0,10);
        caPeriod.setText("CA Period");
        caPeriod.setTextSize(15);
        caPeriod.setGravity(Gravity.CENTER);
        module.setPadding(0,0,0,10);
        tableRow.addView(module);
        tableRow.addView(grade);
        tableRow.addView(caMarks);
        tableRow.addView(caPeriod);
        tableLayout.addView(tableRow);

        for(Result result : resultList){
            tableRow = new TableRow(getContext());
            module = new TextView(getContext());
            grade = new TextView(getContext());
            caMarks = new TextView(getContext());
            caPeriod = new TextView(getContext());
            module.setText((String)resultList.get(count).getModule()); //variable taken from db
            module.setTextSize(13);
            module.setGravity(Gravity.CENTER);
            module.setPadding(0,0,0,5);
            grade.setText(resultList.get(count).getGrades()); //variable taken from db
            grade.setTextSize(13);
            grade.setGravity(Gravity.CENTER);
            module.setPadding(0,0,0,5);
            caMarks.setText(resultList.get(count).getCaMarks()); //variable taken from db
            caMarks.setTextSize(13);
            caMarks.setGravity(Gravity.CENTER);
            module.setPadding(0,0,0,5);
            caPeriod.setText(resultList.get(count).getYear()+" "+resultList.get(count).getPeriod()); //variable taken from db
            caPeriod.setTextSize(13);
            caPeriod.setGravity(Gravity.CENTER);
            module.setPadding(0,0,0,5);
            tableRow.addView(module);
            tableRow.addView(grade);
            tableRow.addView(caMarks);
            tableRow.addView(caPeriod);
            tableLayout.addView(tableRow);

            gradePoint += getGradePoint(grade.getText().toString().trim());

            passCounter += getPassStatus(grade.getText().toString().trim(), caMarks.getText().toString().trim());
            count++;
        }
        if (count == 0) {
            showToast("NO RESULTS");
        } else {
            textViewSemGpa.setText(String.format("%.2f",gradePoint/(count)));

            if (passCounter > 0){
                textViewSemPassStatus.setText("Fail");
            } else {
                textViewSemPassStatus.setText("Pass");
            }
        }
    }

    private int getPassStatus(String grade, String caMarks){
        int passCounter = 0;
        if (grade.equals("C-") || grade.equals("D+") || grade.equals("D") || grade.equals("E") || Double.parseDouble(caMarks) < 40.0){
            passCounter++;
        }
        return passCounter;
    }

    private double getGradePoint(String grade){
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
}