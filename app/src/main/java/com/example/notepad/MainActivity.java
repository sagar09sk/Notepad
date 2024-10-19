package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        TextView textViewAdd = findViewById(R.id.textViewAdd);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        // If user is not logged in
        if(user == null){
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            // if user is not verified
            if(!user.isEmailVerified()){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Your email is not verified");
                builder.setMessage("Please verify your Email then login");

                builder.setPositiveButton(" Resend ", (dialogInterface, i) ->
                        user.sendEmailVerification().addOnSuccessListener(unused -> {
                            Toast.makeText(MainActivity.this, "Verification Email has been sent. ", Toast.LENGTH_SHORT).show();
                            firebaseAuth.signOut();
                            finish();
                        })
                );
                builder.setNegativeButton(" OK ", (dialogInterface, i) -> {
                    //close alert dialog
                    firebaseAuth.signOut();
                    finish();
                });

                builder.create().show();
            }
        }

        replaceFragment(new NotesFragment());
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(menuItem -> {
           int id = menuItem.getItemId();
           if (id == R.id.notes){
                replaceFragment(new NotesFragment());
               textViewAdd.setText("Notes");
            }else if(id == R.id.bills){
               replaceFragment(new BillsFragment());
               textViewAdd.setText("Electricity Bills");
           }else if(id == R.id.dateRange){
               replaceFragment(new DateRangeFragment());
               textViewAdd.setText("Important Days");
           }else if(id == R.id.expenditure){
               replaceFragment( new ExpenditureFragment());
               textViewAdd.setText("Expenditure");
           }
           return true;
        });



    }


    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_for_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id  = item.getItemId();
        if(id == R.id.logout_app){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(" Logout for App ");
            builder.setMessage(" Are you sure you want to logout of the Notepad app? ");
            builder.setNeutralButton(" Cancel " ,(dialogInterface, i) -> {
            });
            builder.setPositiveButton(" YES Sure " ,(dialogInterface, i) -> {
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            });
            builder.create().show();
        }
        return super.onOptionsItemSelected(item);
    }
}