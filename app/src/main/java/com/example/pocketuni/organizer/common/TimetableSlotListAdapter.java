package com.example.pocketuni.organizer.common;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketuni.R;
import com.example.pocketuni.model.TimetableItem;
import com.example.pocketuni.organizer.admin.AdminViewTimetableActivity;

import java.util.ArrayList;
import java.util.List;

public class TimetableSlotListAdapter  extends RecyclerView.Adapter<TimetableSlotListAdapter.TimetableSlotListViewHolder> {
    List<TimetableItem> timetableItems = new ArrayList<TimetableItem>();
    Context context;

    public TimetableSlotListAdapter(Context context, List<TimetableItem> timetables){
        this.context = context;
        this.timetableItems = timetables;
    }

    @NonNull
    @Override
    public TimetableSlotListAdapter.TimetableSlotListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.layout_timetableslot_listitem, parent, false);
        return new TimetableSlotListAdapter.TimetableSlotListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimetableSlotListAdapter.TimetableSlotListViewHolder holder, final int position) {
        holder.timetableSlotName.setText(timetableItems.get(position).getSubjectCode() +"\n" + timetableItems.get(position).getStartingTime() + " - " + timetableItems.get(position).getEndingTime());
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
    }

    @Override
    public int getItemCount() {
        return timetableItems.size();
    }

    public class TimetableSlotListViewHolder extends RecyclerView.ViewHolder{

        TextView timetableSlotName, timetableSlotDetails;
        ImageView timetableSlotIcon;
        CardView timetableSlotListItem;

        public TimetableSlotListViewHolder(@NonNull View itemView) {
            super(itemView);

            timetableSlotName = itemView.findViewById(R.id.timetableslotname);
            timetableSlotDetails = itemView.findViewById(R.id.timetableslot_details);
            timetableSlotIcon = itemView.findViewById(R.id.timetablesloticon);
            timetableSlotListItem = itemView.findViewById(R.id.timetableslot_listitem);
        }
    }
}
