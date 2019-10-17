package com.example.mymusic;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusic.FavoriteDatabase.FavoriteSongsProvider;
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
    private FavoriteSongsProvider provider;

    public MyAdapter(ArrayList<Song> mList, Context context, ActivityMusic activityMusic, AllSongsFragment fragments) {
        this.mActivityMusic = activityMusic;
        mContext = context;
        this.mList = mList;
        this.mSongsFragment = fragments;
        this.mActivityMusic = activityMusic;
        mlayoutInflater = LayoutInflater.from(context);
        provider = new FavoriteSongsProvider();
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
        if (mActivityMusic.mBound){
            if (position == mActivityMusic.mService.getPos()) {
                holder.index.setText("");
                holder.index.setBackgroundResource(R.mipmap.ic_launcher);
                holder.name.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                holder.index.setText(Integer.toString(position + 1));
                holder.name.setTypeface(Typeface.DEFAULT);
                holder.index.setBackgroundResource(0);
            }
            holder.name.setText(song.title);
            int munute=(int) song.duration/60000;
            int second=(int) song.duration/1000 %60;
            holder.duration.setText(String.format("%02d:%02d",munute,second));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    //Class MyViewHolder
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, duration, index;
        ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name_item);
            name.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View view) {
                    clicked();
                }
            });
            index = itemView.findViewById(R.id.index_item);
            index.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View view) {
                    clicked();
                }
            });
            duration = itemView.findViewById(R.id.duration_item);
            duration.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View view) {
                    clicked();
                }
            });
            imageView = itemView.findViewById(R.id.menu_popup);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(mContext,v);
                    popupMenu.getMenu().add(0,1,0,"Add to Favorite");
                    popupMenu.getMenu().add(0,2,0,"Remove Favorite");
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(item.getItemId()==1){
                                provider.insert(
                                        Uri.parse(FavoriteSongsProvider.URI_FAVORITE+"/2"),
                                        null
                                );
                                return true;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private void clicked() {
            mSong = mList.get(getAdapterPosition());
            mActivityMusic.mService.setSongPlay(mSong);
            mActivityMusic.mService.startNewSong(mSong.data);
            mActivityMusic.mService.setPosPlay(getAdapterPosition());
            mSongsFragment.updateUi();
            notifyDataSetChanged();
        }
    }
}
