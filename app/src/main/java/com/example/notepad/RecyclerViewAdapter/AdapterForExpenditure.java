package com.example.notepad.RecyclerViewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notepad.Models.ExpenditureModel;
import com.example.notepad.Models.ExpenseDetailsModel;
import com.example.notepad.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdapterForExpenditure extends RecyclerView.Adapter<AdapterForExpenditure.MyViewHolder> {
    private final Context context;
    private final ArrayList<ExpenditureModel> expenditure;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String userID = firebaseAuth.getCurrentUser().getUid();

    public AdapterForExpenditure(Context context, ArrayList<ExpenditureModel> expenditure) {
        this.context = context;
        this.expenditure = expenditure;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.expenditure_row_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ExpenditureModel model = expenditure.get(position);
        holder.textViewMonth.setText(model.getMonth());
        holder.textViewAmount.setText("Rs " + model.getTotalAmount());

        ArrayList<ExpenseDetailsModel> expenseDetails = new ArrayList<>();
        firebaseFirestore.collection("Expenditure " + userID)
                .document("Expanses of " + model.getMonth())
                .collection(model.getMonth())
                .orderBy("Date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                expenseDetails.add(new ExpenseDetailsModel(
                                        document.getString("Serial"), document.getString("Date"), document.getString("Expanse"), document.getString("Amount")
                                ));
                            }

                            AdapterForExpenseDetails nestedAdapter = new AdapterForExpenseDetails(expenseDetails);
                            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                            holder.recyclerView.setAdapter(nestedAdapter);

                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return expenditure.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMonth, textViewAmount;
        RecyclerView recyclerView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMonth = itemView.findViewById(R.id.textViewMonth);
            textViewAmount = itemView.findViewById(R.id.textViewAmount);
            recyclerView = itemView.findViewById(R.id.recyclerViewDetail);
        }
    }
}