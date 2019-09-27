package com.example.mymusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.mymusic.fragment.AllSongsFragment;
import com.example.mymusic.fragment.MediaPlaybackFragment;

public class ActivityMusic extends AppCompatActivity {
    private Song songPlay;
    private boolean isPlay;
    private int mPos = -1;
    private MyService mService;
    private boolean mBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isPlay=mBound;
        if (savedInstanceState != null) {
            String time = savedInstanceState.getString("time");
            String name = savedInstanceState.getString("name");
            String artist = savedInstanceState.getString("artist");
            songPlay = new Song(name, time, artist);
            isPlay = savedInstanceState.getBoolean("isPlay");
            mPos = savedInstanceState.getInt("position");
        }
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.activity_music, AllSongsFragment.newInstance()).commit();
        } else {
            AllSongsFragment songsFragment = AllSongsFragment.newInstance();
            MediaPlaybackFragment playbackFragment = MediaPlaybackFragment.newInstance();
            songsFragment.setmPlaybackFragment(playbackFragment);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fram_1, songsFragment)
                    .replace(R.id.fram_2, playbackFragment)
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            MyService.MyBinder binder = (MyService.MyBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isPlay", mBound);
        outState.putInt("position", mPos);
        if (songPlay != null) {
            outState.putString("name", songPlay.mName);
            outState.putString("time", songPlay.mTime);
            outState.putString("artist", songPlay.mArtist);
        }
    }

    public void changeFragment(View view) {
        getSupportActionBar().hide();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.activity_music, MediaPlaybackFragment.newInstance())
                .addToBackStack(null).commit();
    }

    public void backToList(View view) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        getSupportFragmentManager().popBackStack();
        transaction.replace(R.id.activity_music, AllSongsFragment.newInstance())
                .commit();
    }

    public MyService getmService() {
        return mService;
    }

    public interface IshowActionBar {
        void showActionBar(ActivityMusic activityMusic);
    }

    //getter setter song playing
    public void setSongPlay(Song songPlay) {
        this.songPlay = songPlay;
    }

    public Song getSongPlay() {
        return songPlay;
    }

    //is play
    public void setPlay(boolean play) {
        isPlay = play;
    }

    public boolean getIsPlay() {
        return isPlay;
    }

    public void setmPos(int mPos) {
        this.mPos = mPos;
    }

    //position in list
    public int getmPos() {
        return mPos;
    }
    public void play(){
        bindService (new Intent(ActivityMusic.this,MyService.class),connection,Context.BIND_AUTO_CREATE);
    }
    public void pause(){
        unbindService(connection);
    }
}

