package com.example.mymusic;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static com.example.mymusic.ActivityMusic.CHANNEL_ID;

public class MyService extends Service {
    private final IBinder binder = new MyBinder();
    private ArrayList<Song> mListPlay;
    private int mPosPlay = -1;
    private Song mSongPlay;
    private MediaPlayer mPlayer;
    private boolean mIsShuffle;
    private int mRepeatMode = 0;
    public static final String UP_DATE_UI = "UP_DATE_UI";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        mPlayer.release();
    }

    public class MyBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startNewSong(final String data) {
        if (mPlayer != null) mPlayer.release();
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(data);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Exception", Toast.LENGTH_SHORT);
        }
        setOnCompletePlay();
        startMyServiceForrground();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyServiceForrground() {
        Intent intent = new Intent(this, ActivityMusic.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        /*RemoteViews defaultNotification =
                new RemoteViews(getPackageName(), R.layout.default_notification);
        RemoteViews bigNotification =
                new RemoteViews(getPackageName(), R.layout.big_notification);
        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setCustomContentView(defaultNotification)
                .setSmallIcon(R.drawable.ic_small_notification)
                .setCustomBigContentView(bigNotification)
                .setContentIntent(pendingIntent)
                .setStyle(new Notification.DecoratedCustomViewStyle())
                .build();*/
        Notification notification= new Notification.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_small_notification)
                .setContentText(mSongPlay.title)
                .build();
        startForeground(11, notification);
    }

    public void pause() {
        if (mPlayer == null) Toast.
                makeText(getBaseContext(), "Ban chua chon bai hat !", Toast.LENGTH_SHORT).
                show();

        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
            } else mPlayer.start();
        }
    }

    public boolean isPlaying() {
        if (mPlayer != null) {
            return mPlayer.isPlaying();
        } else return false;
    }

    public void seek(int percent) {
        if (mPlayer != null) {
            mPlayer.seekTo(mPlayer.getDuration() * percent / 100);
        }
    }

    public void setPosPlay(int pos) {
        mPosPlay = pos;
    }

    public int getPos() {
        return mPosPlay;
    }

    public Song getSongPlay() {
        return mSongPlay;
    }

    public void setSongPlay(Song mSongPlay) {
        this.mSongPlay = mSongPlay;
    }

    public void setListPlay(ArrayList<Song> list) {
        mListPlay = list;
    }

    public long getTime() {
        if (mPlayer == null) return 0;
        else return mPlayer.getCurrentPosition();
    }

    public void setRepeatMode() {
        if (mRepeatMode >= 0 && mRepeatMode < 3) {
            mRepeatMode++;
        } else mRepeatMode = 0;
    }

    public int getRepeatMode() {
        return mRepeatMode;
    }

    public void setShuffle(){
        if (mIsShuffle){
            mIsShuffle = false;
        } else {
            mIsShuffle = true;
        }
    }

    public boolean isShuffle() {
        return mIsShuffle;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setOnCompletePlay(){
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                switch (mRepeatMode) {
                    case 0:
                        nextSong();
                        break;
                    case 1:
                        mPlayer.start();
                        break;
                    case 2:
                        mPlayer.start();
                        break;
                }
                updateUi();
            }
        });
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void nextSong(){
        if(mIsShuffle) {
            Random random =new Random();
            mPosPlay = random.nextInt( mListPlay.size());
        } else {
            if( mPosPlay < mListPlay.size()){
                mPosPlay ++;
            } else {
                mPosPlay = 0;
            }

        }
        mSongPlay= mListPlay.get(mPosPlay);
        startNewSong(mSongPlay.data);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void previousSong(){
        if(mIsShuffle) {
            Random random =new Random();
            mPosPlay = random.nextInt( mListPlay.size());
        } else {
            if( mPosPlay > 0){
                mPosPlay --;
            } else {
                mPosPlay = mListPlay.size();
            }

        }
        mSongPlay= mListPlay.get(mPosPlay);
        startNewSong(mSongPlay.data);
    }

    void updateUi(){
        Intent intent = new Intent();
        intent.setAction(UP_DATE_UI);
        sendBroadcast(intent);
    }

}
