package com.example.mymusic.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mymusic.ActivityMusic;
import com.example.mymusic.MyAdapter;
import com.example.mymusic.MyService;
import com.example.mymusic.R;
import com.example.mymusic.Song;

import java.util.ArrayList;

public class AllSongsFragment extends Fragment {

    public static AllSongsFragment newInstance() {
        return new AllSongsFragment();
    }

    private ArrayList<Song> mListSong = new ArrayList<>();
    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private ImageView mPlayBtn1, mImage;
    private TextView mNamePlay, mArtist;
    private MediaPlaybackFragment mPlaybackFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        for (int i = 0; i < 20; i++) {
            mListSong.add(new Song("Bai hat " + (i + 1), "00:00", "UnKnown"));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_songs, container, false);
        //intialize fake data

        final ActivityMusic activityMusic = (ActivityMusic) getActivity();

        //set Play Button
        mPlayBtn1 = view.findViewById(R.id.play_btn);
        mPlayBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setmPlayBtn(activityMusic);
            }
        });
        if (activityMusic.getIsPlay()) {
            mPlayBtn1.setImageResource(R.drawable.ic_media_pause_light);
        }

        //set recycleView
        mAdapter = new MyAdapter(mListSong, getContext(), (ActivityMusic) getActivity(), this);
        recyclerView = view.findViewById(R.id.list_item_song);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
        //info song
        mNamePlay = view.findViewById(R.id.show_name);
        mArtist = view.findViewById(R.id.show_artist);
        mImage = view.findViewById(R.id.show_image);
        if (activityMusic.getSongPlay() != null) {
            Song song;
            song = ((ActivityMusic) getActivity()).getSongPlay();
            mNamePlay.setText(song.mName);
            mArtist.setText(song.mArtist);
        }

        return view;
    }

    //luu trang thai

    //set play button va trang thai isPlay
    public void setmPlayBtn(ActivityMusic activityMusic) {
        if (!activityMusic.getIsPlay()) {
            activityMusic.setPlay(true);
            mPlayBtn1.setImageResource(R.drawable.ic_media_pause_light);
            activityMusic.play();
        } else {
            activityMusic.pause();
            activityMusic.setPlay(false);
            mPlayBtn1.setImageResource(R.drawable.ic_media_play_light);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void setmPlaybackFragment(MediaPlaybackFragment mPlaybackFragment) {
        this.mPlaybackFragment = mPlaybackFragment;
    }

    public void updateUi() {
        final ActivityMusic activityMusic = (ActivityMusic) getActivity();
        Song song = activityMusic.getSongPlay();
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == getResources().getConfiguration().ORIENTATION_PORTRAIT) {
            mNamePlay.setText(song.mName);
            mArtist.setText(song.mArtist);
            mPlayBtn1.setImageResource(R.drawable.ic_media_pause_light);
        } else {
            mPlaybackFragment.updateUi();
        }
    }
}


