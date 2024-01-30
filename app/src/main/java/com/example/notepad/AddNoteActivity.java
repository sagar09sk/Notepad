package com.example.notepad;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    LinearLayout layoutEdit;
    String userID;
    TextView textViewAdd;
    String encryptNoteIntent;
    String encryptTitleIntent;
    String title;
    String noteBody;



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
        layoutEdit = findViewById(R.id.layoutEdit);
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();


        editTextMultiLine.setOnClickListener(v -> {
            layoutEdit.setVisibility(View.INVISIBLE);
        });


        encryptTitleIntent = getIntent().getStringExtra("encryptTitle");
        encryptNoteIntent = getIntent().getStringExtra("encryptNote");
        if(encryptTitleIntent == null){
            textViewAdd.setText("Add New Note");
        }
        else{
            try {
                editTextTitle.setText(CryptoUtils.decrypt(encryptTitleIntent));
                editTextMultiLine.setText(CryptoUtils.decrypt(encryptNoteIntent));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            layoutEdit.setVisibility(View.VISIBLE);
            textViewAdd.setText("Edit Note");
            editTextTitle.setFocusable(false);
        }

        buttonSaveNote.setOnClickListener(view -> {
            title = editTextTitle.getText().toString().trim();
            noteBody = editTextMultiLine.getText().toString();
            if(TextUtils.isEmpty(title)){
                editTextTitle.setError("Title is Required");
                return;
            }

            try {
                if(encryptTitleIntent == null){
                    title = CryptoUtils.encrypt(title);
                }else{
                    title = encryptTitleIntent;
                }
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
                Intent intent = new Intent(AddNoteActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

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
                FirebaseFirestore.getInstance().collection(userID).document(encryptTitleIntent).delete().addOnSuccessListener(unused -> {
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
            intent.putExtra(Intent.EXTRA_TEXT ,noteBody);
            startActivity(Intent.createChooser(intent,"Share"));

        });

    }


}