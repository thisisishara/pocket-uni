package com.example.pocketuni.organizer.weekfragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketuni.R;
import com.example.pocketuni.model.TimetableItem;
import com.example.pocketuni.organizer.common.TimetableSlotListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


public class SundayFragment extends Fragment {

    private List<TimetableItem> timetableItems = new ArrayList<TimetableItem>();
    private String timetableName;
    private RecyclerView recyclerViewTimetableSlots;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private View view;
    private TimetableSlotListAdapter timetableSlotListAdapter;

    public SundayFragment(String timetableName) {
        this.timetableName = timetableName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sunday, container, false);

        recyclerViewTimetableSlots = view.findViewById(R.id.sunday_slot_list);
        recyclerViewTimetableSlots.hasFixedSize();
        recyclerViewTimetableSlots.setLayoutManager(new LinearLayoutManager(getContext()));
        getAvailableSlots();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getAvailableSlots();
    }

    private void getAvailableSlots(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //getSlots
        //show currently available timetables
        CollectionReference collectionReference = firebaseFirestore.collection("timetables").document(timetableName).collection("slots");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            private static final String TAG = "GET_SUN_TIMETABLE_SLOTS";

            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                timetableItems.clear();
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    Log.d(TAG, "Current data: " + queryDocumentSnapshots.getDocumentChanges());

                    for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        //putting into a list of slots
                        TimetableItem timetableItem = documentSnapshot.toObject(TimetableItem.class);

                        if(timetableItem.getDay()==7){
                            timetableItems.add(timetableItem);
                        }

                        timetableSlotListAdapter = new TimetableSlotListAdapter(getContext(), timetableName, timetableItems);
                        recyclerViewTimetableSlots.setAdapter(timetableSlotListAdapter);
                    }

                } else {
                    Log.d(TAG, "Current data: null");
                    timetableSlotListAdapter = new TimetableSlotListAdapter(getContext(), timetableName, timetableItems);
                    recyclerViewTimetableSlots.setAdapter(timetableSlotListAdapter);
                    //showToast("NO TIMETABLE SLOTS.");
                }
            }
        });
    }

    private void showToast (String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}