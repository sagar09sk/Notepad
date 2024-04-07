package com.example.notepad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapterForDays extends RecyclerView.Adapter<RecyclerViewAdapterForDays.MyViewHolder>{

    Context context;
    ArrayList<String> datesList,dayDiffList;
    RecyclerViewAdapterForDays(Context context , ArrayList<String> datesList,ArrayList<String> dayDiffList) {
        this.context = context;
        this.datesList = datesList;
        this.dayDiffList = dayDiffList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view =  inflater.inflate(R.layout.days_row_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textViewDayDate.setText(datesList.get(position));
        holder.textViewDayDiff.setText(dayDiffList.get(position));
    }

    @Override
    public int getItemCount() {
        return datesList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textViewDayTittle;
        TextView textViewDayDate;
        TextView textViewDayDiff;
        TextView textViewDayDis;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDayTittle = itemView.findViewById(R.id.textViewDayTittle);
            textViewDayDate = itemView.findViewById(R.id.textViewDayDate);
            textViewDayDiff = itemView.findViewById(R.id.textViewDayDiff);
            textViewDayDis = itemView.findViewById(R.id.textViewDayDis);
        }
    }
}
