package com.example.mykeep;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListNoteAdapter extends RecyclerView.Adapter<ListNoteAdapter.NoteHolder> {
    private LayoutInflater mInflater;
    private ArrayList<Note> mList;
    public ListNoteAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_note, parent, false);
        return new NoteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        Note note = mList.get(position);
        holder.title.setText(note.getTitle());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    void setList(ArrayList<Note> list){
        mList = list;
    }
    class NoteHolder extends RecyclerView.ViewHolder{
        private final TextView title;
        private final ImageView more;
        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            more= itemView.findViewById(R.id.more);
        }
    }
}
