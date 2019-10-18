package com.example.mymusic.fragment;

import android.content.BroadcastReceiver;
import android.content.ContentProvider;
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
import com.example.mymusic.FavoriteDatabase.FavoriteSongsProvider;
import com.example.mymusic.MyAdapter;
import com.example.mymusic.MyService;
import com.example.mymusic.R;
import com.example.mymusic.Song;

import java.util.ArrayList;

public class BaseSongListFragment extends Fragment {
    private ArrayList<Song> mListSong;
    private RecyclerView recyclerView;
    public MyAdapter mAdapter;
    private ImageView mPlayBtn1, mImage;
    private TextView mNamePlay, mArtist;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMusic activity= (ActivityMusic) getActivity();
        mListSong = getList();
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
        if (activityMusic.mBound){
            if (activityMusic.mService.isPlaying()) {
                mPlayBtn1.setImageResource(R.drawable.ic_media_pause_light);
            }
        }

        //set recycleView
        mAdapter = new MyAdapter(mListSong,getContext(),activityMusic,this);
        recyclerView = view.findViewById(R.id.list_item_song);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
        //info song
        mNamePlay = view.findViewById(R.id.name_song_playing_1);
        mArtist = view.findViewById(R.id.artist_song_playing_1);
        mImage = view.findViewById(R.id.image_song_playing_1);
        if (activityMusic.mBound){
            if (activityMusic.mService.getSongPlay() != null) {
                Song song = ((ActivityMusic) getActivity()).mService.getSongPlay();
                Bitmap albumImage= activityMusic.mService.getAlbumBitmap();
                mNamePlay.setText(song.title);
                mArtist.setText(song.artist);
                if(albumImage != null){
                    mImage.setImageBitmap(albumImage);
                }
                else mImage.setImageResource(R.drawable.defaut_album_image);
                recyclerView.smoothScrollToPosition(activityMusic.mService.getPos());
            }
        }
        return view;
    }

    //luu trang thai

    //set play button va trang thai isPlay
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setmPlayBtn(ActivityMusic activityMusic) {
        activityMusic.mService.pause();
        if (activityMusic.mService.isPlaying()) {
            mPlayBtn1.setImageResource(R.drawable.ic_media_pause_light);
        } else {
            mPlayBtn1.setImageResource(R.drawable.ic_media_play_light);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void updateUi() {
        ActivityMusic activityMusic = (ActivityMusic) getActivity();
        if (activityMusic != null && activityMusic.mBound){
            Song song = activityMusic.mService.getSongPlay();
            Bitmap albumImage= activityMusic.mService.getAlbumBitmap();
            mNamePlay.setText(song.title);
            mArtist.setText(song.artist);
            mPlayBtn1.setImageResource(R.drawable.ic_media_pause_light);
            if(albumImage != null){
                mImage.setImageBitmap(albumImage);
            }
            else mImage.setImageResource(R.drawable.defaut_album_image);
            mAdapter.notifyDataSetChanged();
        }
    }


    BroadcastReceiver receiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(mAdapter != null)
            updateUi();
        }
    };
}
