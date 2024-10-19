package com.example.notepad;

import static android.content.ContentValues.TAG;
import static android.text.InputType.TYPE_CLASS_TEXT;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.LocalDate;
import java.util.ArrayList;

public class BillsFragment extends Fragment {

    TextView textViewTotalAmount;
    FirebaseFirestore firebaseFirestore;
    FireBaseFireStoreHelper fireBaseFireStoreHelper;
    RecyclerView recyclerView;
    ArrayList<String> profileNameList, dateList, amountList;
    ImageView createProfileButton;
    String currentDate;


    public BillsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bills, container, false);

        //declare variables
        LocalDate todayDate = LocalDate.now();
        currentDate = todayDate.toString();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        fireBaseFireStoreHelper = new FireBaseFireStoreHelper();
        profileNameList = new ArrayList<>();
        dateList = new ArrayList<>();
        amountList = new ArrayList<>();
        textViewTotalAmount = view.findViewById(R.id.textViewTotalAmount);


        // create profile
        createProfileButton = view.findViewById(R.id.createProfileButton);
        createProfileButton.setOnClickListener(v -> {
            EditText editTextCreate = new EditText(view.getContext());
            editTextCreate.setInputType(TYPE_CLASS_TEXT);
            AlertDialog.Builder createDialog = new AlertDialog.Builder(view.getContext());
            createDialog.setTitle(" Enter Profile Name ");
            createDialog.setMessage(" Profile Name must be Unique ");
            createDialog.setView(editTextCreate);
            createDialog.setPositiveButton(" Create ", (dialogInterface, i) -> {
                String createName = editTextCreate.getText().toString();
                fireBaseFireStoreHelper.createProfileInFirebaseNew(getContext(),createName,currentDate);
                startActivity(new Intent(getContext(),MainActivity.class));
            });
            createDialog.setNeutralButton("Cancel",(dialogInterface, i) -> {
                //cancel
            });
            createDialog.create().show();
        });


        //get and set all last bills to RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AdapterForLastBill recyclerViewAdapterForLastBill = new AdapterForLastBill(getContext(),userID,profileNameList,dateList,amountList);
        firebaseFirestore.collection("Profiles of "+userID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    amountList.add(document.getString("Amount"));
                    profileNameList.add(document.getString("Profile Name"));
                    dateList.add(document.getString("Date"));
                    recyclerViewAdapterForLastBill.notifyDataSetChanged();
                }
            } else {
                Log.w(TAG, "Error getting documents.", task.getException());
            }
            //get total
            int total = 0;
            for(String i : amountList){
                int a = Integer.parseInt(i);
                total = total + a;
            }
            textViewTotalAmount.setText("Total Amount = " + total  +"Rs" );
        });

        recyclerView.setAdapter(recyclerViewAdapterForLastBill);






        return view;
    }
}