package com.example.pocketuni.results.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.pocketuni.R;
import com.example.pocketuni.model.Result;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    List<Result> resultList = new ArrayList<>();
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseFirestore db;

    CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        db = FirebaseFirestore.getInstance();

        mRecyclerView = findViewById(R.id.recycler_view);

        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        showData();
    }

    private void showData() {
        String DialogRegNum = getIntent().getStringExtra("DialogRegNum");
        String DialogYearSem = getIntent().getStringExtra("DialogYearSem");

        db.collection("Students").document(DialogRegNum).collection("Results").document(DialogYearSem).collection("Modules")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot doc: task.getResult()){
                    Result result = new Result(doc.getString("regNum"),
                            doc.getString("module"),
                            doc.getString("caMarks"),
                            doc.getString("grades"),
                            doc.getString("period"),
                            doc.getString("year"));
                    resultList.add(result);
                }
                adapter = new CustomAdapter(ListActivity.this, resultList);
                mRecyclerView.setAdapter(adapter);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(e.getMessage());
                    }
                });
    }

    private void showToast (String message) {
        Toast.makeText(ListActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}