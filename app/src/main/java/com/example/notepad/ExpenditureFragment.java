package com.example.notepad;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notepad.Models.ExpenditureModel;
import com.example.notepad.Models.ExpenseDetailsModel;
import com.example.notepad.RecyclerViewAdapter.AdapterForExpenditure;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ExpenditureFragment extends Fragment {
    ArrayList<String> expenditureMonthList,amountList;
    ArrayList<ExpenseDetailsModel> expenseDetails;
    ArrayList<ExpenditureModel> expenditure;
    RecyclerView expenditureRecycleView;
    FloatingActionButton buttonAddExpenditure;
    FirebaseAuth firebaseAuth;
    String userID;
    TextView monthTextView;
    ProgressBar progressBarExpanse;
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

        AdapterForExpenditure adapterForExpenditure = new AdapterForExpenditure(getActivity(),expenditure);
        expenditureRecycleView = view.findViewById(R.id.recyclerViewExpenditure);
        expenditureRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        expenditureRecycleView.setAdapter(adapterForExpenditure);

        progressBarExpanse = view.findViewById(R.id.progressBarExpanse);
        buttonAddExpenditure = view.findViewById(R.id.buttonAddExpenditure);
        buttonAddExpenditure.setVisibility(View.VISIBLE);
        buttonAddExpenditure.setOnClickListener(v -> {
            AddExpenditureDialong expenditureDialog = new AddExpenditureDialong();
            expenditureDialog.show(requireActivity().getSupportFragmentManager()," Add Expenditure " );
        });


        firebaseFirestore.collection("Expenditure " + userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            progressBarExpanse.setVisibility(View.INVISIBLE);
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                expenditure.add(new ExpenditureModel(
                                        document.getString("Serial"), document.getString("Month"), document.getString("Total Amount")
                                ));
                                String month = document.getString("Month");
                                expenditureMonthList.add(month);
                                amountList.add(document.getString("Total Amount"));

                                adapterForExpenditure.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error getting documents", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return view;
    }
}