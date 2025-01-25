package com.example.notepad;

import static android.content.ContentValues.TAG;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
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
import android.widget.Toast;
import com.example.notepad.RecyclerViewAdapter.AdapterForLastBill;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BillsFragment extends Fragment {

    TextView textViewTotalAmount;
    FirebaseFirestore firebaseFirestore;
    RecyclerView recyclerView;
    ArrayList<String> profileNameList, dateList, amountList;
    ImageView createProfileButton;
    String currentDate;
    String userID;


    public BillsFragment() {
        // Required empty public constructor
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bills, container, false);

        //declare variables
        LocalDate todayDate = LocalDate.now();
        currentDate = todayDate.toString();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        profileNameList = new ArrayList<>();
        dateList = new ArrayList<>();
        amountList = new ArrayList<>();
        textViewTotalAmount = view.findViewById(R.id.textViewTotalAmount);


        // create profile
        createProfileButton = view.findViewById(R.id.createProfileButton);
        createProfileButton.setOnClickListener(v -> {
            addNewProfile();
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
            float total = 0.0F;
            for(String i : amountList){
                float a = Float.parseFloat(i);
                total = total + a;
            }
            textViewTotalAmount.setText(String.format("Total Amount = %sRs", total));
        });
        recyclerView.setAdapter(recyclerViewAdapterForLastBill);

        return view;
    }

    private void addNewProfile(){
        android.app.AlertDialog.Builder createDialog= new android.app.AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.add_new_profile_dialog_layout, null);
        EditText profileEditText = view.findViewById(R.id.profileEditText);
        createDialog.setView(view).setTitle(" Enter Profile Name ");
        createDialog.setNeutralButton("Cancel", (dialog, which) -> {

        });
        createDialog.setPositiveButton("Save",(dialog, which) -> {
            String createName = profileEditText.getText().toString();
            createProfileInFirebaseNew(getContext(),createName,currentDate);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout,new BillsFragment()).commit();
        });
        createDialog.create().show();
    }



    public void createProfileInFirebaseNew(Context context, String profileName , String date ){
        //save profile in database
        DocumentReference ProfilesCollection = firebaseFirestore.collection("Profiles of " + userID).document(profileName);
        Map<String, Object> profile = new HashMap<>();
        profile.put("Profile Name", profileName);
        profile.put("Amount", "0");
        profile.put("Date", date);
        profile.put("Current Reading", "0");
        profile.put("Previous Reading", "0");
        ProfilesCollection.set(profile).addOnSuccessListener(unused -> {
            // save first bill in database
            DocumentReference billsCollection = firebaseFirestore.collection("Profiles of " + userID).document(profileName).collection("Bill Data").document("date " + date);
            Map<String, Object> bill = new HashMap<>();
            bill.put("Previous Reading", "0");
            bill.put("Current Reading", "0");
            bill.put("Amount", "0");
            bill.put("Date", date);
            billsCollection.set(bill).addOnSuccessListener(unused1 -> Toast.makeText(context, "saved", Toast.LENGTH_SHORT).show())
                    //if profile did not save
                    .addOnFailureListener(e -> Toast.makeText(context, "failed " + e, Toast.LENGTH_SHORT).show());
        }).addOnFailureListener(e -> Toast.makeText(context, "failed " + e, Toast.LENGTH_SHORT).show());

    }




}