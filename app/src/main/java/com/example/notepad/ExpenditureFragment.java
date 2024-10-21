package com.example.notepad;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.notepad.Models.ExpenditureModel;
import com.example.notepad.Models.ExpenseDetailsModel;
import com.example.notepad.RecyclerViewAdapter.AdapterForExpenditure;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ExpenditureFragment extends Fragment {
    ArrayList<String> expenditureMonthList,amountList;
    ArrayList<ExpenseDetailsModel> expenseDetails;
    ArrayList<ExpenditureModel> expenditure;
    RecyclerView expenditureRecycleView;
    FloatingActionButton buttonAddExpenditure;
    FirebaseAuth firebaseAuth;
    String userID;
    public ExpenditureFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenditure, container, false);
        expenditureMonthList = new ArrayList<>();
        amountList = new ArrayList<>();
        expenseDetails = new ArrayList<>();
        expenditure = new ArrayList<>();

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        AdapterForExpenditure adapterForExpenditure = new AdapterForExpenditure(getActivity(),expenditure,expenseDetails);
        expenditureRecycleView = view.findViewById(R.id.recyclerViewExpenditure);
        expenditureRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        expenditureRecycleView.setAdapter(adapterForExpenditure);


        buttonAddExpenditure = view.findViewById(R.id.buttonAddExpenditure);
        buttonAddExpenditure.setVisibility(View.VISIBLE);
        buttonAddExpenditure.setOnClickListener(v -> {
            AddExpenditureDialong expenditureDialog = new AddExpenditureDialong();
            expenditureDialog.show(requireActivity().getSupportFragmentManager()," Add Expenditure " );
        });

        firebaseFirestore.collection("Expenditure "+userID).get()
                .addOnCompleteListener(task -> {
                    if (task.isComplete()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            expenditure.add(new ExpenditureModel(
                                    document.getString("Serial"),document.getString("Month"),document.getString("Total Amount")
                            ));
                            String month = document.getString("Month");
                            expenditureMonthList.add(month);
                            amountList.add(document.getString("Total Amount"));

                            firebaseFirestore.collection("Expenditure "+userID).document("Expanses of "+month)
                                    .collection(month)
                                    .get().addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            for (QueryDocumentSnapshot data : task1.getResult()) {
                                                expenseDetails.add(new ExpenseDetailsModel(
                                                    data.getString("Serial"),data.getString("Date"),data.getString("Expanse"),data.getString("Amount")
                                                ));
                                                adapterForExpenditure.notifyDataSetChanged();
                                            }
                                        }
                                    });
                        }
                    }
                });
        return view;
    }
}