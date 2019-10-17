//package com.example.mykeep;
//
//import android.content.Context;
//
//import androidx.annotation.NonNull;
//import androidx.room.Database;
//import androidx.room.DatabaseConfiguration;
//import androidx.room.InvalidationTracker;
//import androidx.room.Room;
//import androidx.room.RoomDatabase;
//import androidx.sqlite.db.SupportSQLiteOpenHelper;
//
//@Database(entities = Note.class ,version = 1, exportSchema = false)
//public class NoteRoomDatabase extends RoomDatabase {
//
//    public abstract NoteDao noteDao();
//    private static NoteRoomDatabase INSTANCE;
//    static NoteRoomDatabase getDatabase(final Context context) {
//        if (INSTANCE == null) {
//            synchronized (NoteRoomDatabase.class) {
//                if (INSTANCE == null) {
//                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
//                            NoteRoomDatabase.class, "note_database")
//                            // Wipes and rebuilds instead of migrating
//                            // if no Migration object.
//                            // Migration is not part of this practical.
//                            .fallbackToDestructiveMigration()
//                            .build();
//                }
//            }
//        }
//        return INSTANCE;
//    }
//}
