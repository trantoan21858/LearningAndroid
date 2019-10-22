package com.example.mymusic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.example.mymusic.fragment.BaseSongListFragment;

import java.util.ArrayList;

import es.claucookie.miniequalizerlibrary.EqualizerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> implements Filterable {
    static final int TASK_ADD_FAVORITE = 1;
    static final int TASK_REMOVE_FAVORITE = 2;
    static final int TASK_COUNT = 3;
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
        mListAll = new ArrayList<>();
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
        if (mActivityMusic.mBound) {
            if (song.id == mActivityMusic.mService.getSongPlay().id) {
                holder.index.setVisibility(View.INVISIBLE);
                holder.equalizerView.setVisibility(View.VISIBLE);
                if (mActivityMusic.mService.isPlaying()) {
                    holder.equalizerView.animateBars();
                } else {
                    if (!holder.equalizerView.isAnimating()) {
                        holder.equalizerView.stopBars();
                    }
                }
                holder.name.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                holder.index.setVisibility(View.VISIBLE);
                holder.equalizerView.setVisibility(View.INVISIBLE);
                holder.index.setText(Integer.toString(position + 1));
                holder.name.setTypeface(Typeface.DEFAULT);
            }
            holder.name.setText(song.title);
            int munute = (int) song.duration / 60000;
            int second = (int) song.duration / 1000 % 60;
            holder.duration.setText(String.format("%02d:%02d", munute, second));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    //Class MyViewHolder
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, duration, index;
        ImageView mini_menu;
        EqualizerView equalizerView;

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
            mini_menu = itemView.findViewById(R.id.menu_popup);
            equalizerView = itemView.findViewById(R.id.equalizer);
            equalizerView.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View view) {
                    clicked();
                }
            });
            mini_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getMenu(v, getLayoutPosition()).show();
                }
            });
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private void clicked() {
            mSong = mList.get(getLayoutPosition());
            mActivityMusic.mService.setSongPlay(mSong);
            mActivityMusic.mService.startNewSong(mSong.data);
            mActivityMusic.mService.setPosPlay(getLayoutPosition());
            mSongsFragment.updateUi();
            int pos = getLayoutPosition();
            new DataAsyncTask(TASK_COUNT,mListAll.get(pos).id,pos).execute();
            notifyDataSetChanged();
        }
    }

    public PopupMenu getMenu(View v, final int pos) {
        PopupMenu popupMenu = new PopupMenu(mContext, v);
        if(!mActivityMusic.isShowFavorite){
            popupMenu.getMenu().add(0, 1, 0, "Yêu thích!");
        }
        popupMenu.getMenu().add(0, 2, 0, "Bỏ thích.");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() ==1 ){
                    new DataAsyncTask(TASK_ADD_FAVORITE,mListAll.get(pos).id,pos).execute();
                } else {
                    new DataAsyncTask(TASK_REMOVE_FAVORITE,mListAll.get(pos).id,pos).execute();
                }
                notifyDataSetChanged();
                return true;
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

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(mListAll);
            } else {
                String fillerPattern = charSequence.toString().toLowerCase().trim();

                for (Song item : mListAll) {
                    if (item.title.toLowerCase().contains(fillerPattern)) {
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

    class DataAsyncTask extends AsyncTask<Void, Void, Void> {
        private int task;
        private int id;
        private int pos;
        DataAsyncTask(int task, int id,int pos) {
            this.task = task;
            this.id = id;
            this.pos=pos;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(!(getData().moveToFirst()) || getData().getCount() ==0){
                insertAsync();
                return null;
            }
            switch (task) {
                case TASK_ADD_FAVORITE:
                    addFavoriteAsync();
                    break;
                case TASK_REMOVE_FAVORITE:
                    removeFavoriteAsync();
                    break;
                case TASK_COUNT:
                    countAsync();
                    break;
            }
            return null;
        }


        private void addFavoriteAsync() {

            ContentValues values = new ContentValues();
            values.put(FavoriteSongsDatabase.IS_FAVORITE,2);
            mActivityMusic.getContentResolver().update(
                    Uri.parse(FavoriteSongsProvider.URI_FAVORITE),
                    values,
                    FavoriteSongsDatabase.ID_PROVIDER + " = " + id,
                    null

            );

        }

        private void removeFavoriteAsync() {
            ContentValues values = new ContentValues();
            values.put(FavoriteSongsDatabase.IS_FAVORITE,1);
            mActivityMusic.getContentResolver().update(
                    Uri.parse(FavoriteSongsProvider.URI_FAVORITE),
                    values,
                    FavoriteSongsDatabase.ID_PROVIDER + " = " + id,
                    null

            );
            if(mActivityMusic.isShowFavorite){
                mList.remove(pos);
                notifyDataSetChanged();
            }
        }

        private void countAsync() {
            ContentValues values = new ContentValues();
            Cursor cursor = getData();
            cursor.moveToNext();
            int count = cursor.getInt(3);
            count ++;
            values.put(FavoriteSongsDatabase.COUNT_OF_PLAY,count);

            if (count == 3 && cursor.getInt(2) == 0){
                values.put(FavoriteSongsDatabase.IS_FAVORITE,2);
            }
            mActivityMusic.getContentResolver().update(
                    Uri.parse(FavoriteSongsProvider.URI_FAVORITE),
                    values,
                    FavoriteSongsDatabase.ID_PROVIDER + " = " + id,
                    null
            );
        }

        private void insertAsync(){
            ContentValues values = new ContentValues();
            values.put(FavoriteSongsDatabase.ID_PROVIDER,id);
            switch (task){
                case TASK_ADD_FAVORITE: values.put(FavoriteSongsDatabase.IS_FAVORITE,2);
                    break;
                case TASK_REMOVE_FAVORITE: values.put(FavoriteSongsDatabase.IS_FAVORITE,1);
                    break;
                case TASK_COUNT: values.put(FavoriteSongsDatabase.IS_FAVORITE,0);
                    break;
            }
            if (task == TASK_COUNT){
                values.put(FavoriteSongsDatabase.COUNT_OF_PLAY,1);
            } else {
                values.put(FavoriteSongsDatabase.COUNT_OF_PLAY,0);
            }
            mActivityMusic.getContentResolver().insert(
                    Uri.parse(FavoriteSongsProvider.URI_FAVORITE),
                    values
            );
        }

        private Cursor getData() {
            Cursor cursor = mActivityMusic.getContentResolver().query(
                    Uri.parse(FavoriteSongsProvider.URI_FAVORITE + "/2"),
                    null,
                    Integer.toString(id),
                    null,
                    null
            );
            return cursor;
        }
    }
}

