package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
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
    Button buttonSaveNote;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        editTextTitle = findViewById(R.id.editTextTittle);
        editTextMultiLine = findViewById(R.id.editTextMultiLine);

        editTextTitle.setText(getIntent().getStringExtra("title"));
        editTextMultiLine.setText(getIntent().getStringExtra("note"));

        buttonSaveNote = findViewById(R.id.buttonSaveNote);
        buttonSaveNote.setOnClickListener(view -> {

            String title = editTextTitle.getText().toString();
            String noteBody = editTextMultiLine.getText().toString();

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
}