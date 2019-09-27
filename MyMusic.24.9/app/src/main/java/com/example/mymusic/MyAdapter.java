package com.example.mymusic;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusic.fragment.AllSongsFragment;
import com.example.mymusic.fragment.MediaPlaybackFragment;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<Song> mList;
    private LayoutInflater mlayoutInflater;
    private Song mSong;
    private Context mContext;
    private ActivityMusic mActivityMusic;
    private AllSongsFragment mSongsFragment;

    public MyAdapter(ArrayList<Song> mList, Context context, ActivityMusic activityMusic, AllSongsFragment fragments) {
        this.mActivityMusic = activityMusic;
        mContext = context;
        this.mList = mList;
        this.mSongsFragment = fragments;
        this.mActivityMusic = activityMusic;
        mlayoutInflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItem = mlayoutInflater.inflate(R.layout.item_song, parent, false);
        return new MyViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Song song = mList.get(position);
        if (position == mActivityMusic.getmPos()) {
            holder.index.setText("");
            holder.index.setBackgroundResource(R.mipmap.ic_launcher);
            holder.name.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            holder.index.setText(Integer.toString(position + 1));
            holder.name.setTypeface(Typeface.DEFAULT);
            holder.index.setBackgroundResource(0);
        }
        holder.name.setText(song.mName);
        holder.time.setText(song.mTime);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    //Class MyViewHolder
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, time, index;
        ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.show_name_in_list);
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clicked();
                }
            });
            index = itemView.findViewById(R.id.index_song);
            index.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clicked();
                }
            });
            time = itemView.findViewById(R.id.show_time_in_list);
            time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clicked();
                }
            });
            imageView = itemView.findViewById(R.id.menu_popup);
        }

        private void clicked() {
            mSong = mList.get(getAdapterPosition());
            mActivityMusic.setSongPlay(mSong);
            mActivityMusic.setPlay(true);
            mActivityMusic.setmPos(getAdapterPosition());
            mSongsFragment.updateUi();
            notifyDataSetChanged();
        }
    }
}
