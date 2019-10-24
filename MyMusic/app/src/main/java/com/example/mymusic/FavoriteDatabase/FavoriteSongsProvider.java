package com.example.mymusic.FavoriteDatabase;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import static com.example.mymusic.FavoriteDatabase.FavoriteSongsDatabase.DB_NAME;
import static com.example.mymusic.FavoriteDatabase.FavoriteSongsDatabase.DB_VERSION;
import static com.example.mymusic.FavoriteDatabase.FavoriteSongsDatabase.ID_PROVIDER;

public class FavoriteSongsProvider extends ContentProvider {
    private static final String AUTHORITY = "com.example.mymusic.FavoriteDatabase.FavoriteSongsProvider";
    private static UriMatcher uriMatcher;
    private static final String TABLE_FAVORITE="favorite_songs";
    public static final String URI_FAVORITE ="content://"+AUTHORITY+"/"+TABLE_FAVORITE;
    private SQLiteDatabase mDatabase;

    @Override
    public boolean onCreate() {
        FavoriteSongsDatabase favoriteSongsDatabase= new FavoriteSongsDatabase(getContext(),DB_NAME,null,DB_VERSION);
        mDatabase=favoriteSongsDatabase.getWritableDatabase();
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY,TABLE_FAVORITE,1);
        uriMatcher.addURI(AUTHORITY,TABLE_FAVORITE+"/#",2);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        Cursor cursor;
        if(uriMatcher.match(uri) == 1){
            String sql= "SELECT * FROM "+ TABLE_FAVORITE;
            cursor = mDatabase.rawQuery(sql,null);
            return cursor;
        } else {
            String sql = "SELECT * FROM " + TABLE_FAVORITE + " WHERE " + ID_PROVIDER +" LIKE " + s;
            cursor = mDatabase.rawQuery(
                    sql,
                    null
            );
            return cursor;
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        mDatabase.insert(TABLE_FAVORITE,null,contentValues);
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        mDatabase.update(
                TABLE_FAVORITE,
                contentValues,
                s,
                strings
        );
        return 0;
    }
}
