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
    ListActivity listActivity;
    List<Result> resultList;
    Context context;

    public CustomAdapter(ListActivity listActivity, List<Result> resultList) {
        this.listActivity = listActivity;
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
                /*String ca = resultList.get(position).getCaMarks();
                String grade = resultList.get(position).getGrades();
                String year = resultList.get(position).getYear();
                Toast.makeText(listActivity, "Item clicked "+ ca + " " + grade + " " + year , Toast.LENGTH_SHORT).show();*/

                Intent intent = new Intent(view.getContext(), AdminResultsDisplay.class);
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
