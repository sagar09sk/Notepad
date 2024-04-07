package com.example.notepad;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapterForLastBill extends RecyclerView.Adapter<RecyclerViewAdapterForLastBill.MyViewHolder> {

    private final Context context;
    private final String userID;
    private final ArrayList<String> nameList;
    private final ArrayList<String> dateList;
    private final ArrayList<String> amountList ;

    RecyclerViewAdapterForLastBill(Context context , String userID, ArrayList<String> nameList , ArrayList<String> dateList , ArrayList<String> amountList){
        this.context = context;
        this.userID = userID;
        this.nameList = nameList;
        this.dateList = dateList;
        this.amountList = amountList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.last_all_bills_row_layout,parent,false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textViewName.setText(nameList.get(position).toUpperCase());
        holder.textViewDate.setText(dateList.get(position));
        holder.textViewAmount.setText("Rs : "+amountList.get(position));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            final String name = (String) nameList.get(position);
            final String date = (String) dateList.get(position);
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context , BillAsPerProfileActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("userID" , userID);
                intent.putExtra("date" , date);
                context.startActivity(intent);


            }
        });

    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textViewName, textViewDate, textViewAmount;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewAmount = itemView.findViewById(R.id.textViewAmount);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
