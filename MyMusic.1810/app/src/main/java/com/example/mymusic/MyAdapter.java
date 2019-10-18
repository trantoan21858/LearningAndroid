package com.example.mymusic;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusic.FavoriteDatabase.FavoriteSongsDatabase;
import com.example.mymusic.FavoriteDatabase.FavoriteSongsProvider;
import com.example.mymusic.fragment.AllSongsFragment;
import com.example.mymusic.fragment.BaseSongListFragment;
import com.example.mymusic.fragment.MediaPlaybackFragment;

import java.util.ArrayList;
import java.util.Collection;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> implements Filterable {
    private ArrayList<Song> mList;
    private ArrayList<Song> mListAll;
    private LayoutInflater mlayoutInflater;
    private Song mSong;
    private Context mContext;
    private ActivityMusic mActivityMusic;
    private BaseSongListFragment mSongsFragment;

    public MyAdapter(ArrayList<Song> mList, Context context, ActivityMusic activityMusic, BaseSongListFragment fragments) {
        this.mActivityMusic = activityMusic;
        mContext = context;
        this.mList = mList;
        mListAll= new ArrayList<>();
        mListAll.addAll(mList);
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
                    getMenu(v,getLayoutPosition()).show();
                }
            });
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private void clicked() {
            mSong = mList.get(getLayoutPosition());
            mActivityMusic.mService.setSongPlay(mSong);
            mActivityMusic.mService.startNewSong(mSong.data);
            mActivityMusic.mService.setPosPlay(getAdapterPosition());
            mSongsFragment.updateUi();
            notifyDataSetChanged();
        }
    }

    public PopupMenu getMenu(View v, final int pos){
        PopupMenu popupMenu = new PopupMenu(mContext,v);
        popupMenu.getMenu().add(0,1,0,"Add to Favorite");
        popupMenu.getMenu().add(0,2,0,"Remove Favorite");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==1){
                    ContentValues values = new ContentValues();
                    values.put(FavoriteSongsDatabase.ID_PROVIDER,mList.get(pos).id);
                    mActivityMusic.getContentResolver().insert(
                            Uri.parse(FavoriteSongsProvider.URI_FAVORITE+"/2"),
                            values
                    );
                    return true;
                }
                return false;
            }
        });
        return popupMenu;
    }
    @Override
    public Filter getFilter() {
        return findFilter;
    }

    private Filter findFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Song> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0){
                filteredList.addAll(mListAll);
            } else {
                String fillerPattern = charSequence.toString().toLowerCase().trim();

                for (Song item : mListAll){
                    if ( item.title.toLowerCase().contains(fillerPattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mList.clear();
            mList.addAll((ArrayList<Song>) filterResults.values);
            notifyDataSetChanged();
        }
    };
}
