package com.example.mymusic.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusic.ActivityMusic;
import com.example.mymusic.MyAdapter;
import com.example.mymusic.MyService;
import com.example.mymusic.R;
import com.example.mymusic.Song;

import java.util.ArrayList;

public class BaseSongListFragment extends Fragment {
    private ArrayList<Song> mListSong;
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private ImageView mPlayBtn1, mImage;
    private TextView mNamePlay, mArtist;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListSong = getList();

    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityMusic activity= (ActivityMusic) getActivity();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyService.UP_DATE_UI);
        activity.registerReceiver(receiver,intentFilter);

    }

    public ArrayList<Song> getList() {
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_songs, container, false);
        //intialize fake data
        final ActivityMusic activityMusic = (ActivityMusic) getActivity();
        //set Play Button
        mPlayBtn1 = view.findViewById(R.id.play_button_1);
        mPlayBtn1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                setmPlayBtn(activityMusic);
            }
        });

        //set recycleView
        mAdapter = new MyAdapter(mListSong,getContext(),activityMusic,this);
        mRecyclerView = view.findViewById(R.id.list_item_song);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        //info song
        mNamePlay = view.findViewById(R.id.name_song_playing_1);
        mArtist = view.findViewById(R.id.artist_song_playing_1);
        mImage = view.findViewById(R.id.image_song_playing_1);
        if (activityMusic.isBound()){
            if (activityMusic.getService().getSongPlay() != null) {
                Song song = ((ActivityMusic) getActivity()).getService().getSongPlay();
                Bitmap albumImage= activityMusic.getService().getmAlbumBitmap();
                mNamePlay.setText(song.title);
                mArtist.setText(song.artist);
                if(albumImage != null){
                    mImage.setImageBitmap(albumImage);
                }
                else mImage.setImageResource(R.drawable.defaut_album_image);
                mRecyclerView.smoothScrollToPosition(activityMusic.getService().getPos());
            }
            if (activityMusic.getService().isPlaying()) {
                mPlayBtn1.setImageResource(R.drawable.ic_media_pause_light);
            }
        }
        return view;
    }

    //luu trang thai

    //set play button va trang thai isPlay
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setmPlayBtn(ActivityMusic activityMusic) {
        activityMusic.getService().pause();
        if (activityMusic.getService().isPlaying()) {
            mPlayBtn1.setImageResource(R.drawable.ic_media_pause_light);
        } else {
            mPlayBtn1.setImageResource(R.drawable.ic_media_play_light);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        ActivityMusic activity= (ActivityMusic) getActivity();
        activity.unregisterReceiver(receiver);
    }

    public void updateUi() {
        ActivityMusic activityMusic = (ActivityMusic) getActivity();
        if (activityMusic != null && activityMusic.isBound()){
            Song song = activityMusic.getService().getSongPlay();
            Bitmap albumImage= activityMusic.getService().getmAlbumBitmap();
            mNamePlay.setText(song.title);
            mArtist.setText(song.artist);
            if(activityMusic.getService().isPlaying()) {
                mPlayBtn1.setImageResource(R.drawable.ic_media_pause_light);
            } else {
                mPlayBtn1.setImageResource(R.drawable.ic_media_play_light);
            }
            if(albumImage != null){
                mImage.setImageBitmap(albumImage);
            }
            else mImage.setImageResource(R.drawable.defaut_album_image);
            mAdapter.notifyDataSetChanged();
        }

    }

    public MyAdapter getAdapter(){
        return mAdapter;
    }


    BroadcastReceiver receiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(mAdapter != null)
            updateUi();
        }
    };
}
