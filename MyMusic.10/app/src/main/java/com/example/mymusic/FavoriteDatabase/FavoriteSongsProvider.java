package com.example.mymusic.FavoriteDatabase;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mymusic.Song;

import static com.example.mymusic.FavoriteDatabase.FavoriteSongsDatabase.DB_NAME;
import static com.example.mymusic.FavoriteDatabase.FavoriteSongsDatabase.DB_VERSION;

public class FavoriteSongsProvider extends ContentProvider {
    private static final String AUTHORITY = "com.example.demofavoritesongs.Database.FavoriteSongsProvider";
    private static UriMatcher uriMatcher;
    private static final String TABLE_FAVORITE="favorite_songs";
    public static final String URI_FAVORITE ="content://"+AUTHORITY+"/"+TABLE_FAVORITE;
    private FavoriteSongsDatabase mFavoriteSongsDatabase;
    private Song song;

    public void setSong(Song song) {
        this.song = song;
    }

    @Override
    public boolean onCreate() {
        mFavoriteSongsDatabase = new FavoriteSongsDatabase(getContext(),DB_NAME,null,DB_VERSION);
        mFavoriteSongsDatabase.open();
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY,TABLE_FAVORITE,1);//cursor
        uriMatcher.addURI(AUTHORITY,TABLE_FAVORITE+"/#",2);//add
        uriMatcher.addURI(AUTHORITY,TABLE_FAVORITE+"/#",3);//add favorite
        uriMatcher.addURI(AUTHORITY,TABLE_FAVORITE+"/#",4);//remove Favorite
        uriMatcher.addURI(AUTHORITY,TABLE_FAVORITE+"/#",5);//update count
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        if(uriMatcher.match(uri) == 1){
            return mFavoriteSongsDatabase.getCursor();
        } else return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        if (uriMatcher.match(uri) == 2) {
            mFavoriteSongsDatabase.add(song);
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        if(uriMatcher.match(uri) ==3){
            mFavoriteSongsDatabase.addFavorite(song);
        } else if (uriMatcher.match(uri)==4)
            mFavoriteSongsDatabase.removeFavorite(song);
        else if (uriMatcher.match(uri)==5)
            mFavoriteSongsDatabase.updateCountClicked(song);
        return 0;
    }
}
