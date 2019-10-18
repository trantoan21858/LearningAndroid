package com.example.mymusic.FavoriteDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.mymusic.Song;

public class FavoriteSongsDatabase extends SQLiteOpenHelper {
    private SQLiteDatabase mDatabase;
    public static final int DB_VERSION= 1;
    public static final String DB_NAME= "favorite_songs_data";
    public static final String TABLE_FAVORITE = "favorite_songs";
    public static final String ID = "_id";
    public static final String ID_PROVIDER = "id_songs";
    public static final String IS_FAVORITE = "favorite_status";
    public static final String COUNT_OF_PLAY =" clicked";


    private static final String CREAT_TABLE_FAVORITE =
            "CREATE TABLE " + TABLE_FAVORITE + " ( " + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    ID_PROVIDER + " INTEGER, " + IS_FAVORITE + " INTEGER, " +COUNT_OF_PLAY + " INTEGER);";
    private static final String UPGRADE_TABLE_FAVORITE =
            "DROP TABLE IF EXISTS " +TABLE_FAVORITE;
    public FavoriteSongsDatabase(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREAT_TABLE_FAVORITE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(UPGRADE_TABLE_FAVORITE);
        onCreate(sqLiteDatabase);
    }

    public void open(){
        Log.d("ajksdhajksd","sdnasjkdhasjk");
        mDatabase= this.getWritableDatabase();
    }

    public void close(){
        this.close();
    }

    public void add(ContentValues values){
        DataAsyncTask asyncTask = new DataAsyncTask(DataAsyncTask.ADD,values);
        asyncTask.execute();

    }

//    public boolean remove(Song song){
//        mDatabase.delete(TABLE_FAVORITE,ID_PROVIDER +" = " + song.id,null);
//        return false;
//    }

    public void updateCountClicked(ContentValues values){
        DataAsyncTask asyncTask = new DataAsyncTask(DataAsyncTask.UPDATE_COUNT,values);
        asyncTask.execute();
    }

    public void addFavorite(ContentValues values){
        DataAsyncTask asyncTask = new DataAsyncTask(DataAsyncTask.ADD_FAVORITE,values);
        asyncTask.execute();
    }

    public void removeFavorite(ContentValues values){
        DataAsyncTask asyncTask = new DataAsyncTask(DataAsyncTask.REMOVE_FAVORITE,values);
        asyncTask.execute();
    }

//    public void cleanData(){
//        DataAsyncTask asyncTask = new DataAsyncTask(DataAsyncTask.CLEAN_DATA,null,nu);
//    }

    public Cursor getCursor(){
        Cursor cursor = mDatabase.query(TABLE_FAVORITE,null,null,null,null,null,null);
        return cursor;
    }
//Asynctask for update data
    class DataAsyncTask extends AsyncTask<Void,Void,Void>{
        public static final int UPDATE_COUNT =1;
        public static final int ADD_FAVORITE = 2;
        public static final int REMOVE_FAVORITE= 3;
        public static final int ADD= 4;
        int task;
        ContentValues values;
        DataAsyncTask(int requestCode,ContentValues values){
            task = requestCode;
            this.values= values;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            switch (task){
//
//                case  UPDATE_COUNT: updateCountAsync(values);
//                    break;
                case ADD_FAVORITE :
                    break;
                case REMOVE_FAVORITE:
                    break;
                case ADD: addAsync(values);
                    break;
            }
            return null;
        }

        void addAsync(ContentValues values){
            mDatabase.insert(TABLE_FAVORITE,null,values);
        }

//        void updateCountAsync(ContentValues values){
//            int ckeck=mDatabase.update(TABLE_FAVORITE,values,ID_PROVIDER+" = " + song.id,null);
//            if(ckeck == 0) addAsync(values);
//        }

        void addFavoriteAsync(Song song){

        }

        void removeFavoriteAsync(Song song){

        }
    }

}
