package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import android.os.Bundle;

public class AddNoteActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    EditText editTextTitle,editTextMultiLine;
    ImageView buttonSaveNote;
    String userID;
    TextView textViewAdd;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        editTextTitle = findViewById(R.id.editTextTittle);
        editTextMultiLine = findViewById(R.id.editTextMultiLine);
        textViewAdd = findViewById(R.id.textViewAdd);


        String intentTitle = getIntent().getStringExtra("title");
        //in case of new note
        if(intentTitle == null){
            textViewAdd.setText("Add New Note");

            buttonSaveNote = findViewById(R.id.buttonSaveNote);
            buttonSaveNote.setOnClickListener(view -> {

                String title = editTextTitle.getText().toString();
                String noteBody = editTextMultiLine.getText().toString();

                try {
                    title = CryptoUtils.encrypt(title);
                    noteBody = CryptoUtils.encrypt(noteBody);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                firebaseAuth = FirebaseAuth.getInstance();
                userID = firebaseAuth.getCurrentUser().getUid();
                firebaseFirestore = FirebaseFirestore.getInstance();
                DocumentReference documentReference = firebaseFirestore.collection(userID).document(title);
                Map<String,Object> note = new HashMap<>();
                note.put("Title",title);
                note.put("Note",noteBody);

                documentReference.set(note).addOnFailureListener(e -> {
                    Toast.makeText(AddNoteActivity.this, "Failed to Save"+ e, Toast.LENGTH_SHORT).show();
                });

                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();

            });

        }
        //in case of edit note
        else{
            textViewAdd.setText("Edit Note");
            String intentNote = getIntent().getStringExtra("note");

            String decryptTitle;
            String decryptNote;
            try {
                decryptTitle = CryptoUtils.decrypt(intentTitle);
                decryptNote = CryptoUtils.decrypt(intentNote);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            editTextTitle.setText(decryptTitle);
            editTextMultiLine.setText(decryptNote);


            buttonSaveNote = findViewById(R.id.buttonSaveNote);
            buttonSaveNote.setOnClickListener(view -> {

                String title = editTextTitle.getText().toString();
                String noteBody = editTextMultiLine.getText().toString();

                try {
                    title = CryptoUtils.encrypt(title);
                    noteBody = CryptoUtils.encrypt(noteBody);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                firebaseAuth = FirebaseAuth.getInstance();
                userID = firebaseAuth.getCurrentUser().getUid();
                firebaseFirestore = FirebaseFirestore.getInstance();
                DocumentReference documentReference = firebaseFirestore.collection(userID).document(title);
                Map<String,Object> note = new HashMap<>();
                note.put("Title",title);
                note.put("Note",noteBody);

                documentReference.set(note).addOnSuccessListener(unused->{
                            FirebaseFirestore.getInstance().collection(userID).document(intentTitle).delete().addOnSuccessListener(unused2 -> {
                            }).addOnFailureListener(e ->
                                    Toast.makeText(this, " failed " +e , Toast.LENGTH_SHORT).show()
                            );
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(AddNoteActivity.this, "Failed to Save"+ e, Toast.LENGTH_SHORT).show();
                        });



                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();

            });

        }

    }
}