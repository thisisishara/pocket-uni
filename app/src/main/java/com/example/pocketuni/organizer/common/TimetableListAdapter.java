package com.example.pocketuni.organizer.common;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketuni.R;
import com.example.pocketuni.model.Timetable;
import com.example.pocketuni.organizer.admin.AdminViewTimetableActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TimetableListAdapter extends RecyclerView.Adapter<TimetableListAdapter.TimetableListViewHolder> {

    List<Timetable> timetables = new ArrayList<Timetable>();
    Context context;

    public TimetableListAdapter(Context context, List<Timetable> timetables){
        this.context = context;
        this.timetables = timetables;
    }

    @NonNull
    @Override
    public TimetableListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.layout_timetable_listitem, parent, false);
        return new TimetableListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimetableListViewHolder holder, final int position) {
        holder.timetableName.setText(timetables.get(position).getTimetable_batch());
        holder.timetableCourse.setText(timetables.get(position).getTimetable_course());
        holder.timetableNotification.setImageDrawable(null);
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
                context.startActivity(intent);
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
        CardView timetableListItem;

        public TimetableListViewHolder(@NonNull View itemView) {
            super(itemView);

            timetableName = itemView.findViewById(R.id.timetablename);
            timetableCourse = itemView.findViewById(R.id.timetablecourse);
            timetableIcon = itemView.findViewById(R.id.timetableicon);
            timetableNotification = itemView.findViewById(R.id.timetablenotification);
            timetableListItem = itemView.findViewById(R.id.timetable_listitem);
        }
    }
}
