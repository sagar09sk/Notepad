package com.example.notepad.RecyclerViewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notepad.Models.ExpenseDetailsModel;
import com.example.notepad.R;

import java.util.ArrayList;

public class AdapterForExpenseDetails extends RecyclerView.Adapter<AdapterForExpenseDetails.MyViewHolder> {
    private final ArrayList<ExpenseDetailsModel> expenseDetails;
    public AdapterForExpenseDetails(ArrayList<ExpenseDetailsModel> expenseDetails){
        this.expenseDetails = expenseDetails;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.expense_detail_row_layout , viewGroup , false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int position) {
        ExpenseDetailsModel model = expenseDetails.get(position);
        viewHolder.textViewSerial.setText(model.getSerial());
        viewHolder.textViewDate.setText(model.getDate());
        viewHolder.textViewTitle.setText(model.getExpenseTitle());
        viewHolder.textViewAmount.setText("Rs "+model.getExpenseAmount());
    }

    @Override
    public int getItemCount() {
        return expenseDetails.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewSerial;
        private final TextView textViewDate;
        private final TextView textViewTitle;
        private final TextView textViewAmount;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSerial = itemView.findViewById(R.id.textViewSerial);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewAmount = itemView.findViewById(R.id.textViewAmount);
        }
    }
}
