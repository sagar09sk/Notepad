package com.example.notepad;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class GenerateBillActivity extends AppCompatActivity {

    private TextView previousInfo;
    private EditText currentInfo;
    private TextView rateInfo;
    private Button saveButton;
    Button calculateButton;
    String currentDate,previousReading,currentReading,unitRate;
    String userID;
    FirebaseFirestore firebaseFirestore;
    String name;
    String date;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_generate_bill);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        calculateButton = findViewById(R.id.updateButton);
        TextView nameInfo = findViewById(R.id.nameinfo);
        previousInfo = findViewById(R.id.previousinfo);
        TextView textViewHeading = findViewById(R.id.textViewHeading);
        rateInfo = findViewById(R.id.rateinfo);
        TextView dateInfo = findViewById(R.id.dateinfo);
        LinearLayout layoutUnit = findViewById(R.id.layoutUnit);
        LinearLayout layoutAmount = findViewById(R.id.layoutAmount);
        LinearLayout layoutButton = findViewById(R.id.layoutButton);
        saveButton = findViewById(R.id.saveButton);
        currentInfo = findViewById(R.id.currentinfo);
        currentInfo.requestFocus();

        InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
        }

        //get name and date for intent
        name = getIntent().getStringExtra("Name");
        date = getIntent().getStringExtra("Date");
        if (name != null) {
            nameInfo.setText(name.toUpperCase());
        }

        //for get current date
        LocalDate todayDate;
        todayDate = LocalDate.now();
        currentDate = todayDate.toString();
        dateInfo.setText(String.valueOf(todayDate));


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        getUnitRate();


        //for Edit Unit Rate
        TextView editRateTextView = findViewById(R.id.editRateTextView);
        editRateTextView.setOnClickListener(v -> {
            editUnitRate();
        });

        //get previous reading
        if (date != null) {
            // when we try to Edit bill
            if (date.equals(currentDate)) {
                textViewHeading.setText(" Edit Today Bill ");
                getReadingFormDataBase("Previous Reading");

            } //when we try to add new bill
            else {
                textViewHeading.setText(" Generate New Bill ");
                getReadingFormDataBase("Current Reading");
            }
        }
        //calculate Amount
        calculateButton.setOnClickListener(view -> {
            currentReading = currentInfo.getText().toString();
            // if current reading is not entered
            if (currentReading.isEmpty()) {
                currentInfo.setError("Enter current reading");
            } else {
                int pre = Integer.parseInt(previousReading);
                int cur = Integer.parseInt(currentReading);

                //if current reading is not valid
                if (pre > cur) {
                    currentInfo.setError("current reading can't be less then previous reading");

                } else {
                    closeKeyword();
                    layoutUnit.setVisibility(View.VISIBLE);
                    layoutAmount.setVisibility(View.VISIBLE);
                    layoutButton.setVisibility(View.VISIBLE);


                    int unit = cur - pre;
                    TextView unitInfo = findViewById(R.id.unitinfo);
                    unitInfo.setText(String.valueOf(unit));

                    float rate = Float.parseFloat(unitRate);
                    float amount = unit * rate;
                    TextView amountInfo = findViewById(R.id.amountinfo);
                    amountInfo.setText("Rs " + amount);

                    saveButton.setOnClickListener(view1 -> {
                        addBillInFirebase(this, name, currentDate, previousReading, currentReading, String.valueOf(amount));
                        updateBillInProfiles(this, name, currentDate, String.valueOf(amount), currentReading, previousReading);
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    });

                }
            }

        });


    }

    private void closeKeyword(){
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }


    private void addBillInFirebase(Context context,String profileName , String date ,String previousReading ,String currentReading,String amount){
        DocumentReference billsCollection = firebaseFirestore
                .collection("Profiles of "+ userID ).document(profileName)
                .collection("Bill Data").document("date " +date);
        Map<String,Object> bill = new HashMap<>();
        bill.put("Previous Reading",previousReading);
        bill.put("Current Reading",currentReading);
        bill.put("Amount",amount);
        bill.put("Date" , date);
        billsCollection.set(bill).addOnSuccessListener(unused ->
                Toast.makeText(context, "bill saved", Toast.LENGTH_SHORT).show()
        ).addOnFailureListener(e ->
                Toast.makeText(context, "failed "+ e ,Toast.LENGTH_SHORT).show()
        );
    }


    private void updateBillInProfiles(Context context,String profileName , String date ,String amount ,String current , String previous){
        DocumentReference ProfilesCollection = firebaseFirestore.collection("Profiles of "+ userID ).document(profileName);
        Map<String,Object> profile = new HashMap<>();
        profile.put("Profile Name",profileName);
        profile.put("Amount", amount);
        profile.put("Date" , date);
        profile.put("Current Reading",current);
        profile.put("Previous Reading",previous);

        ProfilesCollection.set(profile).addOnSuccessListener(unused ->
                Toast.makeText(context, "bill updated", Toast.LENGTH_SHORT).show()
        ).addOnFailureListener(e ->
                Toast.makeText(context, "failed "+ e , Toast.LENGTH_SHORT).show()
        );
    }

    private void getUnitRate(){
        firebaseFirestore.collection("Unit Rate of "+userID).document("Unit Rate").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String s = document.getString("Unit Rate");
                    if (s != null) rateInfo.setText(String.format("Rs %s", s));
                    unitRate = s;
                }
            }
        });
    }

    private void getReadingFormDataBase(String path){
        if (name != null) {
            firebaseFirestore.collection("Profiles of " + userID).document(name).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        previousInfo.setText(document.getString(path));
                        previousReading = document.getString(path);
                    }
                }
            });
        }
    }


    private void editUnitRate(){
        AlertDialog.Builder createDialog= new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_unit_rate_dialog_layout, null);
        EditText unitRateEditText = view.findViewById(R.id.unitRateEditText);
        unitRateEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(unitRateEditText, InputMethodManager.SHOW_IMPLICIT);
        }
        createDialog.setView(view).setTitle(" Enter Per Unit Rate ");
        createDialog.setNeutralButton("Cancel", (dialog, which) -> {

        });
        createDialog.setPositiveButton("Save", (dialog, which) -> {
            String inputRate = unitRateEditText.getText().toString();
            if (TextUtils.isEmpty(inputRate)) {
                Toast.makeText(GenerateBillActivity.this, "failed to Update! Try Again by Enter valid rate ", Toast.LENGTH_SHORT).show();
            } else {
                float rate = Float.parseFloat(inputRate);
                if (rate > 20 || rate < 2) {
                    Toast.makeText(GenerateBillActivity.this, "failed to Update! Try Again by Enter rate between 2 to 20 ", Toast.LENGTH_SHORT).show();
                } else {
                    String stringRate = String.valueOf(rate);
                    DocumentReference documentReference1 = firebaseFirestore.collection("Unit Rate of " + userID).document("Unit Rate");
                    Map<String, Object> data = new HashMap<>();
                    data.put("Unit Rate", stringRate);
                    documentReference1.set(data).addOnSuccessListener(unused -> {
                        getUnitRate();
                    });

                }
            }
        });
        createDialog.create().show();

    }


}