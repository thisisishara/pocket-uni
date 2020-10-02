package com.example.pocketuni.results.admin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorSpace;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketuni.R;
import com.example.pocketuni.model.Result;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<ViewHolder> {
    Class<ListActivity> adminResultsDisplay;
    ListActivity listActivity;
    List<Result> resultList;
    Context context;

    public CustomAdapter(ListActivity listActivity, List<Result> resultList) {
        this.listActivity = listActivity;
        this.resultList = resultList;
    }

    public CustomAdapter(Class<ListActivity> adminResultsDisplay, List<Result> resultList) {
        this.adminResultsDisplay = adminResultsDisplay;
        this.resultList = resultList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_layout, parent, false);

        final ViewHolder viewHolder = new ViewHolder(itemView);

        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String mode = "1";

                Intent intent = new Intent(view.getContext(), AdminResultsDisplay.class);
                intent.putExtra("regNum1", resultList.get(position).getRegNum());
                intent.putExtra("module1", resultList.get(position).getModule());
                intent.putExtra("caMarks1", resultList.get(position).getCaMarks());
                intent.putExtra("grades1", resultList.get(position).getGrades());
                intent.putExtra("period1", resultList.get(position).getPeriod());
                intent.putExtra("year1", resultList.get(position).getYear());
                intent.putExtra("mode", mode);
                view.getContext().startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(listActivity, "Item long clicked", Toast.LENGTH_SHORT).show();
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mRegNumTv.setText(resultList.get(position).getModule());
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }
}
