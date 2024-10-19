package com.example.notepad;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serial;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddExpenditureDialong extends AppCompatDialogFragment {
    EditText edtExpenditure, edtAmount;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userID;
    int serial = 0;
    int totalAmount = 0;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();


        firebaseFirestore.collection("Expenditure "+userID).document("Expanses")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String no = document.getString("Serial");
                            if (no != null) serial = Integer.parseInt(no);
                            String am = document.getString("Total Amount");
                            if (no != null) totalAmount = Integer.parseInt(am);
                        }
                    }
                });


        AlertDialog.Builder createDialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_expenditure_dialog_layout,null);
        createDialog.setView(view)
                .setTitle(" Add Expenditure ")
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String expanse = edtExpenditure.getText().toString();
                        String amount = String.valueOf(edtAmount.getText());
                        int am = Integer.parseInt(amount);
                        totalAmount = am + totalAmount;
                        saveExpanse(expanse,amount,serial);
                    }
                });

        edtExpenditure = view.findViewById(R.id.expenditureTitle);
        edtAmount = view.findViewById(R.id.expenditureAmount);


        return createDialog.create();
    }

    private void saveExpanse(String expanse, String amount, int serial) {
        LocalDate todayDate = LocalDate.now();
        String currentDate = todayDate.toString();

        String month = getMonthFormat();
        String year = String.valueOf(getYear());
        serial += 1;
        String no = String.valueOf(serial);
        String totalAmountString  = String.valueOf(totalAmount);

        DocumentReference documentReference = firebaseFirestore
                .collection("Expenditure "+userID).document("Expanses");
        Map<String,Object> expanses = new HashMap<>();
        expanses.put("Month",month+year);
        expanses.put("Total Amount",totalAmountString);
        expanses.put("Serial",no);

        documentReference.set(expanses).addOnSuccessListener(unused ->{

        });

        String number = String.valueOf(serial);
        DocumentReference documentReference1 = firebaseFirestore.collection("Expenditure "+userID).document("Expanses")
                .collection(month+year).document(number);
        Map<String,Object> data = new HashMap<>();
        data.put("Expanse",expanse);
        data.put("Amount",amount);
        data.put("Date",currentDate);
        documentReference1.set(data).addOnSuccessListener(unused -> {

        });
    }

    private int getMonth() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        month = month + 1;
       return month;
    }
    private int getYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    private String getMonthFormat() {
         int month = getMonth();
        if (month == 1) return "JANUARY ";
        if (month == 2) return "FEBRUARY ";
        if (month == 3) return "MARCH ";
        if (month == 4) return "APRIL ";
        if (month == 5) return "MAY ";
        if (month == 6) return "JUNE ";
        if (month == 7) return "JULY ";
        if (month == 8) return "AUGUST ";
        if (month == 9) return "SEPTEMBER ";
        if (month == 10) return "OCTOBER ";
        if (month == 11) return "NOVEMBER ";
        if (month == 12) return "DECEMBER ";
        //default should never happen
        return "Null";

    }
}
