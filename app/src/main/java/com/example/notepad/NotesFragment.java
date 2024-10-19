package com.example.notepad;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class NotesFragment extends Fragment {

    RecyclerView recyclerView;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ImageButton buttonAddNote;
    String userID;
    ArrayList<String> noteList;
    ArrayList<String> titleList;

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
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AdapterForAllNote recyclerViewAdapterForAllNote = new AdapterForAllNote(getActivity(),titleList,noteList);

        // get notes for firebaseStore
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore.collection(userID).get().addOnCompleteListener(task -> {
            if(task.isComplete()){
                recyclerView.setVisibility(View.VISIBLE);
                for(QueryDocumentSnapshot document: task.getResult()){
                    noteList.add(document.getString("Note"));
                    titleList.add(document.getString("Title"));
                    recyclerViewAdapterForAllNote.notifyDataSetChanged();

                }
            }else{
                Log.d(TAG, "failed to get Notes "+task.getException());
                Toast.makeText(getActivity(), "failed to get Notes "+task.getException(), Toast.LENGTH_SHORT).show();
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