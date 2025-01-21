package com.example.notepad;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.notepad.Models.DateFormatModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddExpenditureDialog extends AppCompatDialogFragment {
    EditText edtExpenditure, edtAmount;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userID;
    int currentSerial = 0;
    int totalAmount = 0;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        DateFormatModel dateFormat = new DateFormatModel();
        String monthYear = dateFormat.getMonthFormat() + dateFormat.getYear();

        String timeForOrder = String.valueOf(dateFormat.getYear() + dateFormat.getMonth());


        firebaseFirestore.collection("Expenditure " + userID).document("Expanses of " + monthYear).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String s = document.getString("Serial");
                    if (s != null) currentSerial = Integer.parseInt(s);
                    String am = document.getString("Total Amount");
                    if (s != null) if (am != null) {
                        totalAmount = Integer.parseInt(am);
                    }
                }
            }
        });

        AlertDialog.Builder createDialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_expenditure_dialog_layout, null);
        edtExpenditure = view.findViewById(R.id.expenditureTitle);
        edtAmount = view.findViewById(R.id.expenditureAmount);
        createDialog.setView(view).setTitle(" Add Expenditure ").setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String expanse = edtExpenditure.getText().toString();
                String amount = String.valueOf(edtAmount.getText());
                if (TextUtils.isEmpty(expanse)) {
                    return;
                }
                if (TextUtils.isEmpty(amount)) {
                    return;
                }
                int am = Integer.parseInt(amount);
                totalAmount = am + totalAmount;
                saveExpanse(expanse, amount, currentSerial, monthYear, timeForOrder);

                replaceFragment(new ExpenditureFragment());


            }
        });
        return createDialog.create();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }


    private void saveExpanse(String expanse, String amount, int serial, String month,String time) {
        LocalDate todayDate = LocalDate.now();
        String currentDate = todayDate.toString();

        serial += 1;
        String no = String.valueOf(serial);
        String totalAmountString = String.valueOf(totalAmount);


        DocumentReference documentReference = firebaseFirestore.collection("Expenditure " + userID).document("Expanses of " + month);
        Map<String, Object> expanses = new HashMap<>();
        expanses.put("Month", month);
        expanses.put("Total Amount", totalAmountString);
        expanses.put("Serial", no);
        expanses.put("Time",time);

        documentReference.set(expanses).addOnSuccessListener(unused -> {

        });

        String number = String.valueOf(serial);
        DocumentReference documentReference1 = firebaseFirestore.collection("Expenditure " + userID).document("Expanses of " + month).collection(month).document(number);
        Map<String, Object> data = new HashMap<>();
        data.put("Expanse", expanse);
        data.put("Amount", amount);
        data.put("Date", currentDate);
        data.put("Serial", number);
        documentReference1.set(data).addOnSuccessListener(unused -> {

        });
    }

}
