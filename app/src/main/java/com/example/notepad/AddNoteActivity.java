package com.example.notepad;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
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
    ImageView buttonSaveNote,buttonDeleteNote,buttonShareNote;
    String userID;
    TextView textViewAdd;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        editTextTitle = findViewById(R.id.editTextTittle);
        editTextMultiLine = findViewById(R.id.editTextMultiLine);
        buttonDeleteNote = findViewById(R.id.buttonDeleteNote);
        buttonSaveNote = findViewById(R.id.buttonSaveNote);
        buttonShareNote = findViewById(R.id.buttonShareNote);
        textViewAdd = findViewById(R.id.textViewAdd);
        firebaseAuth = FirebaseAuth.getInstance();

        String intentTitle = getIntent().getStringExtra("title");
        String intentNote = getIntent().getStringExtra("note");
        if(intentTitle == null){
            textViewAdd.setText("Add New Note");
            buttonSaveNote.setVisibility(View.VISIBLE);
        }
        else{
            textViewAdd.setText("Edit Note");
            editTextTitle.setText(intentTitle);
            editTextMultiLine.setText(intentNote);
            buttonDeleteNote.setVisibility(View.VISIBLE);
            buttonShareNote.setVisibility(View.VISIBLE);
            editTextTitle.setFocusable(false);
        }

        userID = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        buttonSaveNote.setOnClickListener(view -> {
            String title = editTextTitle.getText().toString().trim();
            String noteBody = editTextMultiLine.getText().toString();
            if(TextUtils.isEmpty(title)){
                editTextTitle.setError("Title is Required");
                return;
            }

            try {
                noteBody = CryptoUtils.encrypt(noteBody);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            DocumentReference documentReference = firebaseFirestore.collection(userID).document(title);
            Map<String,Object> note = new HashMap<>();
            note.put("Title",title);
            note.put("Note",noteBody);
            documentReference.set(note).addOnSuccessListener(unused ->{
                Toast.makeText(this, " saved ", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            });

        });

        buttonDeleteNote.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to delete this note ");
            builder.setTitle(" Delete Note ");
            builder.setNeutralButton(" Cancel ", (dialogInterface, i) -> {
            });
            builder.setPositiveButton(" Delete ", (dialogInterface, i) -> {
                FirebaseFirestore.getInstance().collection(userID).document(intentTitle).delete().addOnSuccessListener(unused -> {
                    startActivity(new Intent(this ,MainActivity.class));
                    finish();
                });
            });
            builder.create().show();
        });

        buttonShareNote.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT ,"Note");
            intent.putExtra(Intent.EXTRA_TEXT ,intentNote);
            startActivity(Intent.createChooser(intent,"Share"));

        });

    }


}