package com.example.pocketuni.organizer.common;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketuni.R;
import com.example.pocketuni.model.CurrentUser;
import com.example.pocketuni.model.TimetableItem;
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

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimetableSlotListAdapter  extends RecyclerView.Adapter<TimetableSlotListAdapter.TimetableSlotListViewHolder> {
    private String timetableName;
    private List<TimetableItem> timetableItems = new ArrayList<TimetableItem>();
    private Context context;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    public TimetableSlotListAdapter(Context context, String timetableName, List<TimetableItem> timetables){
        this.context = context;
        this.timetableName = timetableName;
        this.timetableItems = timetables;
    }

    @NonNull
    @Override
    public TimetableSlotListAdapter.TimetableSlotListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.layout_timetableslot_listitem, parent, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        return new TimetableSlotListAdapter.TimetableSlotListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimetableSlotListAdapter.TimetableSlotListViewHolder holder, final int position) {
        SimpleDateFormat formatDate = new SimpleDateFormat("hh:mm a");
        String start = formatDate.format(timetableItems.get(position).getStartingDateTime());
        String end = formatDate.format(timetableItems.get(position).getEndingDateTime());

        holder.timetableSlotName.setText(timetableItems.get(position).getSubjectCode() +"\n" + start + " - " + end);
        holder.timetableSlotDetails.setText(timetableItems.get(position).getLocation()+"\n"+timetableItems.get(position).getSubjectName()+"\n"+timetableItems.get(position).getLecturerInCharge());
        holder.timetableSlotIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_timetableslot_listitem));

        holder.timetableSlotListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(context, AdminViewTimetableActivity.class);
                //intent.putExtra("timetableName", timetables.get(position).getTimetable_name());
                intent.putExtra("timetableSlotId", timetableItems.get(position).getSubjectCode()+" "+timetableItems.get(position).getDay()+" "+timetableItems.get(position).getStartingTime());
                intent.putExtra("timetableSlotName", timetableItems.get(position).getSubjectCode() +"\n" + timetableItems.get(position).getStartingTime() + " - " + timetableItems.get(position).getEndingTime());
                intent.putExtra("timetableSlotSubjectCode", timetableItems.get(position).getSubjectCode());
                intent.putExtra("timetableSlotSubjectName", timetableItems.get(position).getSubjectName());
                intent.putExtra("timetableSlotStartTime", timetableItems.get(position).getStartingTime());
                intent.putExtra("timetableSlotEndTime", timetableItems.get(position).getEndingTime());
                intent.putExtra("timetableSlotDay", timetableItems.get(position).getDay());
                intent.putExtra("timetableSlotLocation", timetableItems.get(position).getLocation());
                intent.putExtra("timetableSlotLecturer", timetableItems.get(position).getLecturerInCharge());
                context.startActivity(intent);*/
            }
        });

        if(CurrentUser.getUserType().equalsIgnoreCase("ADMIN")){
            holder.timetableDeleteButton.setVisibility(View.VISIBLE);

            holder.timetableDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final DocumentReference documentReference = firebaseFirestore.collection("timetables").document(timetableName).collection("slots").document(timetableItems.get(position).getStartingTime()+ " " + timetableItems.get(position).getDay());
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                task.getResult().getReference().delete();
                                showToast("TIMETABLE SLOT HAS BEEN DELETED.");
                                Log.i("TTLADAPT", "Timetable slot has been deleted.");
                            }
                        }
                    });
                }
            });

        } else {
            holder.timetableDeleteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return timetableItems.size();
    }

    public class TimetableSlotListViewHolder extends RecyclerView.ViewHolder{

        TextView timetableSlotName, timetableSlotDetails;
        ImageView timetableSlotIcon;
        CardView timetableSlotListItem, timetableDeleteButton;

        public TimetableSlotListViewHolder(@NonNull View itemView) {
            super(itemView);

            timetableSlotName = itemView.findViewById(R.id.timetableslotname);
            timetableSlotDetails = itemView.findViewById(R.id.timetableslot_details);
            timetableSlotIcon = itemView.findViewById(R.id.timetablesloticon);
            timetableSlotListItem = itemView.findViewById(R.id.timetableslot_listitem);
            timetableDeleteButton = itemView.findViewById(R.id.delete_button);
        }
    }

    private void showToast (String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
