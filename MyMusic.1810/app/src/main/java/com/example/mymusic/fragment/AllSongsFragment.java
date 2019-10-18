package com.example.mymusic.fragment;

import com.example.mymusic.ActivityMusic;
import com.example.mymusic.Song;

import java.util.ArrayList;

public class AllSongsFragment extends BaseSongListFragment {


    public static AllSongsFragment newInstance(){
        return  new AllSongsFragment();
    }

    @Override
    public ArrayList<Song> getList() {
        ActivityMusic activityMusic = (ActivityMusic) getActivity();
        return activityMusic.getmList();
    }
}


