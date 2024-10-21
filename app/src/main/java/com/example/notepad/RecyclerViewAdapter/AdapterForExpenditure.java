package com.example.notepad.RecyclerViewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notepad.Models.ExpenditureModel;
import com.example.notepad.Models.ExpenseDetailsModel;
import com.example.notepad.R;

import java.util.ArrayList;

public class AdapterForExpenditure extends RecyclerView.Adapter<AdapterForExpenditure.MyViewHolder>{
    private final Context context;
    private final ArrayList<ExpenditureModel> expenditure;
    private final ArrayList<ExpenseDetailsModel> expenseDetails;

    public AdapterForExpenditure(Context context, ArrayList<ExpenditureModel> expenditure, ArrayList<ExpenseDetailsModel> expenseDetails){
        this.context = context;
        this.expenditure = expenditure;
        this.expenseDetails = expenseDetails;
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
        ExpenditureModel model = expenditure.get(position);
        holder.textViewMonth.setText(model.getMonth());
        holder.textViewAmount.setText("Rs "+ model.getTotalAmount());

        AdapterForExpenseDetails nestedAdapter = new AdapterForExpenseDetails(expenseDetails);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        holder.recyclerView.setAdapter(nestedAdapter);
        nestedAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return expenditure.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView  textViewMonth ,textViewAmount;
        RecyclerView recyclerView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMonth = itemView.findViewById(R.id.textViewMonth);
            textViewAmount = itemView.findViewById(R.id.textViewAmount);
            recyclerView = itemView.findViewById(R.id.recyclerViewDetail);
        }
    }
}