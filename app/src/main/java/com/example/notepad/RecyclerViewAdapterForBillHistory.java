package com.example.notepad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapterForBillHistory extends RecyclerView.Adapter<RecyclerViewAdapterForBillHistory.MyViewHolder>{

    private final Context context;
    private final ArrayList<String> bill_date , bill_current, bill_amount;

    RecyclerViewAdapterForBillHistory(Context context , ArrayList<String> bill_date , ArrayList<String> bill_current , ArrayList<String> bill_amount){
        this.context = context;
        this.bill_date = bill_date;

        this.bill_current = bill_current;
        this.bill_amount = bill_amount;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.billhistory_row_layout, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.dateinfoTextView.setText(String.valueOf(bill_date.get(position)));
        holder.currentinfoTextView.setText(String.valueOf(bill_current.get(position)));
        holder.amountinfoTextView.setText(String.valueOf(bill_amount.get(position)));
    }

    @Override
    public int getItemCount() {
        return bill_date.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView dateinfoTextView, currentinfoTextView , amountinfoTextView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            dateinfoTextView = itemView.findViewById(R.id.dateinfoTextView);
            currentinfoTextView = itemView.findViewById(R.id.currentinfoTextView);
            amountinfoTextView = itemView.findViewById(R.id.amountinfoTextView);
        }
    }
}
