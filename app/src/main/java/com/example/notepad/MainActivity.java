package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import static android.content.ContentValues.TAG;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ImageButton buttonAddNote;
    RecyclerView recyclerView;
    String userID;
    ArrayList<String> noteList;
    ArrayList<String> titleList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        noteList = new ArrayList<>();
        titleList = new ArrayList<>();

        // If user is not logged in
        if(firebaseAuth.getCurrentUser() == null){
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);

        }

        // If user is logged in
        if(firebaseAuth.getCurrentUser() != null){
            // if user is not verified
            FirebaseUser user = firebaseAuth.getCurrentUser();
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

            // view notes
            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            CustomAdapterForAllNote customAdapterForAllNote = new CustomAdapterForAllNote(this,titleList,noteList);

            // get notes for firebaseStore
            firebaseFirestore = FirebaseFirestore.getInstance();
            userID = firebaseAuth.getCurrentUser().getUid();
            firebaseFirestore.collection(userID).get().addOnCompleteListener(task -> {
                if(task.isComplete()){
                    recyclerView.setVisibility(View.VISIBLE);
                    for(QueryDocumentSnapshot document: task.getResult()){
                        noteList.add(document.getString("Note"));
                        titleList.add(document.getString("Title"));
                        customAdapterForAllNote.notifyDataSetChanged();

                    }
                }else{
                    Log.d(TAG, "failed to get Notes "+task.getException());
                    Toast.makeText(MainActivity.this, "failed to get Notes "+task.getException(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            });


            recyclerView.setAdapter(customAdapterForAllNote);

            // add note function
            buttonAddNote = findViewById(R.id.buttonAddNote);
            buttonAddNote.setVisibility(View.VISIBLE);
            buttonAddNote.setOnClickListener(view -> {
                startActivity(new Intent(getApplicationContext(), AddNoteActivity.class));
                finish();
            });

        }

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