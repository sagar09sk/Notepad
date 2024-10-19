package com.example.notepad;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ExpenditureFragment extends Fragment {

    ArrayList<String> expenditureMonthList,amountList;
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expenditure, container, false);

        expenditureMonthList = new ArrayList<>();
        amountList = new ArrayList<>();

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();


        expenditureRecycleView = view.findViewById(R.id.recyclerViewExpenditure);
        expenditureRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        AdapterForExpenditure recyclerViewAdapterForExpenditure = new AdapterForExpenditure(getActivity(),expenditureMonthList,amountList);
        expenditureRecycleView.setAdapter(recyclerViewAdapterForExpenditure);

        buttonAddExpenditure = view.findViewById(R.id.buttonAddExpenditure);
        buttonAddExpenditure.setVisibility(View.VISIBLE);
        buttonAddExpenditure.setOnClickListener(v -> {
            AddExpenditureDialong expenditureDialong = new AddExpenditureDialong();
            expenditureDialong.show(requireActivity().getSupportFragmentManager()," Add Expenditure " );
        });

        firebaseFirestore.collection("Expenditure "+userID).get()
                .addOnCompleteListener(task -> {
                    if (task.isComplete()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            expenditureMonthList.add(document.getString("Month"));
                            amountList.add(document.getString("Total Amount"));
                            recyclerViewAdapterForExpenditure.notifyDataSetChanged();
                        }
                    }
                });
        return view;
    }
}