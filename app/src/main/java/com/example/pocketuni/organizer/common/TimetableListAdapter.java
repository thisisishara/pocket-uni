package com.example.pocketuni.organizer.common;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketuni.R;
import com.example.pocketuni.model.Timetable;
import com.example.pocketuni.organizer.admin.AdminViewTimetableActivity;
import com.example.pocketuni.organizer.std.OrganizerActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TimetableListAdapter extends RecyclerView.Adapter<TimetableListAdapter.TimetableListViewHolder> {
    private List<Timetable> timetables = new ArrayList<Timetable>();
    private Context context;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    public TimetableListAdapter(Context context,  List<Timetable> timetables){
        this.context = context;
        this.timetables = timetables;
    }

    @NonNull
    @Override
    public TimetableListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.layout_timetable_listitem, parent, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        return new TimetableListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimetableListViewHolder holder, final int position) {
        holder.timetableName.setText(timetables.get(position).getTimetable_batch());
        holder.timetableCourse.setText(timetables.get(position).getTimetable_course());
        holder.timetableIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_timetable_listitem));

        holder.timetableListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AdminViewTimetableActivity.class);
                //intent.putExtra("timetableName", timetables.get(position).getTimetable_name());
                intent.putExtra("timetableName", timetables.get(position).getTimetable_name());
                intent.putExtra("timetableBatch", timetables.get(position).getTimetable_batch());
                intent.putExtra("timetableYear", timetables.get(position).getTimetable_year());
                intent.putExtra("timetableSemester", timetables.get(position).getTimetable_semester());
                intent.putExtra("timetableCourse", timetables.get(position).getTimetable_course());
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                context.startActivity(intent);
            }
        });

        holder.timetableDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DocumentReference documentReference = firebaseFirestore.collection("timetables").document(timetables.get(position).getTimetable_name());
                CollectionReference collectionReference = documentReference.collection("slots");
                collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().size()>0){
                                for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                                    queryDocumentSnapshot.getReference().delete();
                                }
                            }
                            documentReference.delete();
                        }
                    }
                });

                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            task.getResult().getReference().delete();
                            Log.i("TTADAPT", "Timetable has been deleted.");
                            showToast("TIMETABLE HAS BEEN DELETED.");
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return timetables.size();
    }

    public class TimetableListViewHolder extends RecyclerView.ViewHolder{

        TextView timetableName, timetableCourse;
        ImageView timetableIcon, timetableNotification;
        CardView timetableListItem, timetableDeleteButton;

        public TimetableListViewHolder(@NonNull View itemView) {
            super(itemView);

            timetableName = itemView.findViewById(R.id.timetablename);
            timetableCourse = itemView.findViewById(R.id.timetablecourse);
            timetableIcon = itemView.findViewById(R.id.timetableicon);
            timetableListItem = itemView.findViewById(R.id.timetable_listitem);
            timetableDeleteButton = itemView.findViewById(R.id.delete_button);
        }
    }

    public void setTimetables(ArrayList<Timetable> timetableSearchResult){
        this.timetables = timetableSearchResult;
        notifyDataSetChanged();
    }

    private void showToast (String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
