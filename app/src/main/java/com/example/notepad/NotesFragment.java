package com.example.notepad;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.notepad.RecyclerViewAdapter.AdapterForAllNote;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NotesFragment extends Fragment {

    RecyclerView recyclerView;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ImageButton buttonAddNote;
    String userID;
    ArrayList<String> noteList;
    ArrayList<String> titleList;
    ProgressBar progressBarNote;


    public NotesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        noteList = new ArrayList<>();
        titleList = new ArrayList<>();

        // view notes
        progressBarNote = view.findViewById(R.id.progressBarNote);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AdapterForAllNote recyclerViewAdapterForAllNote = new AdapterForAllNote(getActivity(),titleList,noteList);

        // get notes for firebaseStore
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore.collection(userID)
                .orderBy("Note")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            progressBarNote.setVisibility(View.INVISIBLE);
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                noteList.add(document.getString("Note"));
                                titleList.add(document.getString("Title"));
                                recyclerViewAdapterForAllNote.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error getting documents: ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        recyclerView.setAdapter(recyclerViewAdapterForAllNote);


        // add New note function
        buttonAddNote = view.findViewById(R.id.buttonAddDay);
        buttonAddNote.setVisibility(View.VISIBLE);
        buttonAddNote.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AddNoteActivity.class));
        });

        return view;
    }
}