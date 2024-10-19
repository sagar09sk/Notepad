package com.example.notepad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterForExpenditure extends RecyclerView.Adapter<AdapterForExpenditure.MyViewHolder>{

    private final Context context;
    private final ArrayList<String> expenditureMonthList,amountList;

    AdapterForExpenditure(Context context , ArrayList<String> expenditureMonthList,ArrayList<String> amountList){
        this.context = context;
        this.expenditureMonthList = expenditureMonthList;
        this.amountList = amountList;


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.expenditure_row_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String month = expenditureMonthList.get(position);
            holder.textViewMonth.setText(month);
            String totalAmount = amountList.get(position);
            holder.textViewAmount.setText("Rs " + totalAmount);

    }

    @Override
    public int getItemCount() {
        return expenditureMonthList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView  textViewMonth ,textViewAmount;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMonth = itemView.findViewById(R.id.textViewMonth);
            textViewAmount = itemView.findViewById(R.id.textViewAmount);
        }
    }
}