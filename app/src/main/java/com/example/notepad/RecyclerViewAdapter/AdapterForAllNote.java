package com.example.notepad.RecyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notepad.AddNoteActivity;
import com.example.notepad.R;

import java.util.ArrayList;

public class AdapterForAllNote extends RecyclerView.Adapter<AdapterForAllNote.MyViewHolder>{

    private final Context context;
    private final ArrayList<String> titleList,noteList;

    public AdapterForAllNote(Context context, ArrayList<String> titleList, ArrayList<String> noteList){
        this.context = context;
        this.titleList = titleList;
        this.noteList = noteList;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.notes_row_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String title = titleList.get(position);
        String note = noteList.get(position);

        holder.textViewTitle.setText(title);
        holder.textViewNote.setText(note);



        holder.cardView.setOnClickListener(view -> {
            Intent intent = new Intent(context, AddNoteActivity.class);
            intent.putExtra("Title" ,title);
            intent.putExtra("Note",note);
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return titleList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textViewNote, textViewTitle ;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNote = itemView.findViewById(R.id.textViewNote);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            cardView = itemView.findViewById(R.id.cardViewExpense);
        }
    }
}