package com.example.mymusic.FavoriteDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


public class FavoriteSongsDatabase extends SQLiteOpenHelper {
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
}
