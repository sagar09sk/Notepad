package com.example.notepad;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.notepad.Models.FireBaseFireStoreHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;

public class GenerateBillActivity extends AppCompatActivity {

    private TextView previousinfo;
    private TextView textViewHeading;
    private EditText currentinfo;
    private TextView unitinfo;
    private TextView amountinfo;
    private Button saveButton;
    Button updateButton;
    String currentDate,previousReading,currentReading;


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



        updateButton = findViewById(R.id.updateButton);
        TextView nameinfo = findViewById(R.id.nameinfo);
        previousinfo = findViewById(R.id.previousinfo);
        textViewHeading = findViewById(R.id.textViewHeading);


        //for get current date
        LocalDate todayDate;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            todayDate = LocalDate.now();
            currentDate = todayDate.toString();
            TextView dateinfo = findViewById(R.id.dateinfo);
            dateinfo.setText(String.valueOf(todayDate));
        }

        FireBaseFireStoreHelper fireBaseFireStoreHelper = new FireBaseFireStoreHelper();
        String name = getIntent().getStringExtra("Name");
        String date = getIntent().getStringExtra("Date");
        nameinfo.setText(name.toUpperCase());

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userID = firebaseAuth.getCurrentUser().getUid();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        // Edit bill
        if(date.equals(currentDate)){
            textViewHeading.setText(" Edit Today Bill ");
            firebaseFirestore.collection("Profiles of "+userID ).document(name).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        previousinfo.setText(document.getString("Previous Reading"));
                        previousReading = document.getString("Previous Reading");
                    }
                }
            });
            //new bill
        }else{
            textViewHeading.setText(" Generate New Bill ");
            firebaseFirestore.collection("Profiles of "+userID ).document(name).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        previousinfo.setText(document.getString("Current Reading"));
                        previousReading = document.getString("Current Reading");
                    }
                }
            });
        }

        TextView rateinfo = findViewById(R.id.rateinfo);
        rateinfo.setText("Rs 5 ");


        currentinfo = findViewById(R.id.currentinfo);
        currentinfo.addTextChangedListener(currentTextWatcher);


        //calculate Amount
        updateButton.setOnClickListener(view -> {
            closeKeyword();
            LinearLayout layoutUnit = findViewById(R.id.layoutUnit);
            LinearLayout layoutAmount = findViewById(R.id.layoutAmount);
            LinearLayout layoutButton = findViewById(R.id.layoutButton);
            layoutUnit.setVisibility(View.VISIBLE);
            layoutAmount.setVisibility(View.VISIBLE);
            layoutButton.setVisibility(View.VISIBLE);
            currentReading = currentinfo.getText().toString();
            int pre = Integer.parseInt(previousReading);
            int cur = Integer.parseInt(currentReading);
            int unit = cur-pre;
            unitinfo = findViewById(R.id.unitinfo);
            unitinfo.setText(String.valueOf(unit));

            int amount = unit * 5 ;
            amountinfo = findViewById(R.id.amountinfo);
            amountinfo.setText("Rs " + amount);

            saveButton = findViewById(R.id.saveButton);
            saveButton.setOnClickListener(view1 -> {
                fireBaseFireStoreHelper.addBillInFirebase(this,name,currentDate,previousReading,currentReading,String.valueOf(amount));
                fireBaseFireStoreHelper.updateBillInProfiles(this,name,currentDate, String.valueOf(amount),currentReading,previousReading);
                startActivity(new Intent(this,MainActivity.class));
                finish();
            });
        });


    }

    private final TextWatcher currentTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String current = currentinfo.getText().toString().trim();
            updateButton.setEnabled(!current.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void closeKeyword(){
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

}