package com.example.mykeep;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Note_table")
public class Note {

    @PrimaryKey
    @NonNull
    @ColumnInfo (name = "title")
    private String mTitle;
    private String content;

    public Note (@NonNull String title){
        this.mTitle= title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getTitle() {
        return mTitle;
    }
}
