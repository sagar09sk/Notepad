package com.example.notepad;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

public class CustomAdapterForAllNote extends RecyclerView.Adapter<CustomAdapterForAllNote.MyViewHolder>{

    private final Context context;
    private final ArrayList<String> titleList,noteList;

    CustomAdapterForAllNote(Context context , ArrayList<String> titleList, ArrayList<String> noteList){
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
        String encryptNote = noteList.get(position);
        try {
            holder.textViewTitle.setText(title);
            holder.textViewNote.setText(CryptoUtils.decrypt(encryptNote));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        holder.cardView.setOnClickListener(view -> {
            Intent intent = new Intent(context,AddNoteActivity.class);
            intent.putExtra("title" ,title);
            intent.putExtra("encryptNote",encryptNote);
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
            cardView = itemView.findViewById(R.id.cardView);

        }
    }
}
