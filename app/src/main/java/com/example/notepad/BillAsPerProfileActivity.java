package com.example.notepad;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notepad.RecyclerViewAdapter.AdapterForBillHistory;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class BillAsPerProfileActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<String> dateList,currentList,amountList;
    AdapterForBillHistory recyclerViewAdapterForBillHistory;
    String profileName,date,userID;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bill_as_per_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView textViewAdd = findViewById(R.id.textViewAdd);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        profileName = intent.getStringExtra("name");
        userID = intent.getStringExtra("userID");
        date = intent.getStringExtra("date");
        recyclerView = findViewById(R.id.recyclerView);
        textViewAdd.setText("Bills of "+profileName.toUpperCase());
        firebaseFirestore = FirebaseFirestore.getInstance();
        dateList = new ArrayList<>();
        currentList = new ArrayList<>();
        amountList = new ArrayList<>();

        recyclerViewAdapterForBillHistory = new AdapterForBillHistory(this,dateList,currentList,amountList);
        firebaseFirestore.collection("Profiles of "+userID ).document(profileName)
                .collection("Bill Data")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            amountList.add(document.getString("Amount"));
                            currentList.add(document.getString("Current Reading"));
                            dateList.add(document.getString("Date"));
                            recyclerViewAdapterForBillHistory.notifyDataSetChanged();
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
        recyclerView.setAdapter(recyclerViewAdapterForBillHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.add_new_bill){
            Intent intentAdd = new Intent(this,GenerateBillActivity.class);
            intentAdd.putExtra("Name" ,profileName);
            intentAdd.putExtra("Date",date);
            startActivity(intentAdd);
        }

        if(id == R.id.delete_profile){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete ?");
            builder.setMessage("Are you sure you want delete ?");
            builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                //delete operation
                firebaseFirestore.collection("Profiles of "+userID ).document(profileName).delete()
                        .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "deletion is successful", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(this,MainActivity.class) );
                    finish();

                }).addOnFailureListener(e ->
                        Toast.makeText(this, " failed " +e , Toast.LENGTH_SHORT).show()
                );

            });
            builder.setNegativeButton("No", (dialogInterface, i) ->
                    Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show()
            );
            builder.create().show();

        }

        return super.onOptionsItemSelected(item);
    }


}