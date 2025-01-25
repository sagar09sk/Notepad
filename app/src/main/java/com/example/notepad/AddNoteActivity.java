package com.example.notepad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
    EditText editTextTitle, editTextMultiLine;
    String userID;
    String noteIntent;
    String titleIntent;
    String title;
    String noteBody;
    Boolean editNote;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView textViewAdd = findViewById(R.id.textViewAdd);
        setSupportActionBar(toolbar);

        editTextTitle = findViewById(R.id.editTextTittle);
        editTextMultiLine = findViewById(R.id.editTextMultiLine);
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        titleIntent = getIntent().getStringExtra("Title");
        noteIntent = getIntent().getStringExtra("Note");
        if (titleIntent == null) {
            textViewAdd.setText("Add New Note");
            editTextTitle.requestFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
            editNote = false;

        } else {
            editTextTitle.setText(titleIntent);
            editTextMultiLine.setText(noteIntent);
            textViewAdd.setText("Edit Note");
            editTextTitle.setFocusable(false);
            editNote = true;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_for_add_notepad, menu);
        if (editNote) {
            menu.findItem(R.id.delete_note).setVisible(true);
            menu.findItem(R.id.share_note).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.save_note) {
            title = editTextTitle.getText().toString().trim();
            noteBody = editTextMultiLine.getText().toString();
            if (TextUtils.isEmpty(title)) {
                editTextTitle.setError("Title is Required");
            } else {
                DocumentReference documentReference = firebaseFirestore.collection(userID).document(title);
                Map<String, Object> note = new HashMap<>();
                note.put("Title", title);
                note.put("Note", noteBody);
                documentReference.set(note).addOnSuccessListener(unused -> {
                    Toast.makeText(this, " saved ", Toast.LENGTH_SHORT).show();


                    Intent intent = new Intent(AddNoteActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                });
            }
        }

        if (id == R.id.delete_note) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to delete this note ");
            builder.setTitle(" Delete Note ");
            builder.setNeutralButton(" Cancel ", (dialogInterface, i) -> {
            });
            builder.setPositiveButton(" Delete ", (dialogInterface, i) -> {
                FirebaseFirestore.getInstance().collection(userID).document(titleIntent).delete().addOnSuccessListener(unused -> {
                    Intent intent = new Intent(AddNoteActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                });
            });
            builder.create().show();
        }

        if (id == R.id.share_note) {

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Note");
            intent.putExtra(Intent.EXTRA_TEXT, noteIntent);
            startActivity(Intent.createChooser(intent, "Share"));
        }

        return super.onOptionsItemSelected(item);
    }


}