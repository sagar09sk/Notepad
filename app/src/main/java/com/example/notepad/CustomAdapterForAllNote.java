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
        String title = (String) titleList.get(position);
        holder.textViewTitle.setText(String.valueOf(titleList.get(position)));
        String note = (String) noteList.get(position);
        holder.textViewNote.setText(String.valueOf(noteList.get(position)));

        holder.cardView.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setMessage(note);
            builder.setPositiveButton(" Edit ", (dialogInterface, i) -> {
                Intent intent = new Intent(context,AddNoteActivity.class);
                intent.putExtra("title" ,title);
                intent.putExtra("note",note);
                context.startActivity(intent);
            });
            builder.setNegativeButton(" Delete ", (dialogInterface, i) -> {
                String userID  = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseFirestore.getInstance().collection(userID).document(title).delete().addOnSuccessListener(unused -> {
                    context.startActivity(new Intent(context,MainActivity.class));
                    Toast.makeText(context, "deletion is successful", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e ->
                        Toast.makeText(context, " failed " +e , Toast.LENGTH_SHORT).show()
                );

            });
            builder.setNeutralButton(" OK ", (dialogInterface, i) -> {

            });
            builder.create().show();
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
