package com.example.mymusic;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import java.util.concurrent.TimeUnit;

import static com.example.mymusic.ActivityMusic.CHANNEL_ID;
import static com.example.mymusic.ActivityMusic.ID_SONG;
import static com.example.mymusic.ActivityMusic.MUSIC_APP_PREFERENCE;

public class MyService extends Service {
    private boolean isForeGround;
    private final IBinder binder = new MyBinder();
    private ArrayList<Song> mListPlay;
    private int mPosPlay = -1;
    private Song mSongPlay;
    private MediaPlayer mPlayer;
    private boolean mIsShuffle;
    private boolean isStarted;
    private int mRepeatMode = 0;
    RemoteViews defaultNotification;
    RemoteViews bigNotification;
    Bitmap albumBitmap;
    public static final String UP_DATE_UI = "UP_DATE_UI";
    public static final String ACTION_PLAY = "PLAY";
    public static final String ACTION_NEXT = "NEXT";
    public static final String ACTION_PREVIOUS = "PREVIOUS";
    public static final int NOTIFY_ID = 2812;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter= new IntentFilter();
        filter.addAction(ACTION_NEXT);
        filter.addAction(ACTION_PLAY);
        filter.addAction(ACTION_PREVIOUS);
        registerReceiver(receiver,filter);
    }

    void saveInfo(){
        SharedPreferences preferences =
                getSharedPreferences(MUSIC_APP_PREFERENCE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(ID_SONG,getSongPlay().id);
        editor.commit();
    }

    public class MyBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startNewSong(final String data) {
        albumBitmap = Song.getAlbumImage(data);
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
        if(isForeGround) {
            updateNotification();
        } else startMusicServiceForrground();
        saveInfo();
        updateUi();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMusicServiceForrground() {
        isForeGround=true;
        startForeground(NOTIFY_ID, getMusicnotification());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateNotification(){
        NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(NOTIFY_ID,getMusicnotification());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private  Notification getMusicnotification(){
        Intent intent = new Intent(this, ActivityMusic.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Intent intentPlay = new Intent(MyService.ACTION_PLAY);
        Intent intentNext = new Intent(MyService.ACTION_NEXT);
        Intent intentPrevious = new Intent(MyService.ACTION_PREVIOUS);
        //default notification
        defaultNotification =
                new RemoteViews(getPackageName(), R.layout.default_notification);
        defaultNotification.setTextViewText(R.id.name_song_notification_default,mSongPlay.title);
        defaultNotification.setTextViewText(R.id.artist_song_notification_default,mSongPlay.artist);
        defaultNotification.
                setOnClickPendingIntent(
                        R.id.next_button_notification_default,
                        PendingIntent.getBroadcast(MyService.this,0,intentNext,0)
                );
        defaultNotification.
                setOnClickPendingIntent(
                        R.id.play_button_notification_default,
                        PendingIntent.getBroadcast(MyService.this,0,intentPlay,0)
                );
        defaultNotification.
                setOnClickPendingIntent(
                        R.id.previous_button_notification_default,
                        PendingIntent.getBroadcast(MyService.this,0,intentPrevious,0)
                );

        //Big notification
        bigNotification =
                new RemoteViews(getPackageName(),R.layout.big_notification);
        bigNotification.setTextViewText(R.id.name_song_notification_big,mSongPlay.title);
        bigNotification.setTextViewText(R.id.artist_song_notification_big,mSongPlay.artist);
        bigNotification.
                setOnClickPendingIntent(
                        R.id.next_button_notification_big,
                        PendingIntent.getBroadcast(MyService.this,0,intentNext,0)
                );
        bigNotification.
                setOnClickPendingIntent(
                        R.id.play_button_notification_big,
                        PendingIntent.getBroadcast(MyService.this,0,intentPlay,0)
                );
        bigNotification.
                setOnClickPendingIntent(
                        R.id.previous_button_notification_big,
                        PendingIntent.getBroadcast(MyService.this,0,intentPrevious,0)
                );
        if ( albumBitmap != null){
            bigNotification.setImageViewBitmap(
                    R.id.image_song_notification_big
                    , albumBitmap
            );

            defaultNotification.setImageViewBitmap(
                    R.id.image_song_notification_default
                    , albumBitmap
            );

        } else {
            bigNotification.setImageViewResource(
                    R.id.image_song_notification_big
                    , R.drawable.defaut_album_image
            );
            defaultNotification.setImageViewResource(
                    R.id.image_song_notification_default
                    , R.drawable.defaut_album_image
            );
        }

        if(isPlaying()){
            bigNotification.setImageViewResource(
                    R.id.play_button_notification_big,
                    R.drawable.ic_play_orange);
            defaultNotification.setImageViewResource(
                    R.id.play_button_notification_default,
                    R.drawable.ic_play_orange);
        }


        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setCustomContentView(defaultNotification)
                .setSmallIcon(R.drawable.ic_small_notification)
                .setCustomBigContentView(bigNotification)
                .setStyle(new Notification.DecoratedCustomViewStyle())
                .setContentIntent(pendingIntent)
                .build();
        return notification;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void pause() {
        if (mPlayer == null){
            Toast.makeText(getBaseContext(), "Ban chua chon bai hat !", Toast.LENGTH_SHORT).
                    show();
            return;
        }

        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
            } else {
                mPlayer.start();
            }
        }
        updateNotification();
    }

    public boolean isPlaying() {
        if (mPlayer != null) {
            return mPlayer.isPlaying();
        } else return false;
    }

    public void seek(int second) {
        if (mPlayer != null) {
            mPlayer.seekTo(second*1000);
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

    public int getTime() {
        if (mPlayer == null) return 0;
        else return (int) TimeUnit.MILLISECONDS.toSeconds(mPlayer.getCurrentPosition());
    }

    public void setRepeatMode() {
        if (mRepeatMode >= 0 && mRepeatMode < 3) {
            mRepeatMode++;
        } else mRepeatMode = 0;
    }
    public void setRepeatMode(int mode){
        mRepeatMode=mode;
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

    public void setShuffle( boolean isShuffle){
        mIsShuffle=isShuffle;
    }

    public boolean isShuffle() {
        return mIsShuffle;
    }

    public boolean isStarted() {
        return isStarted;
    }
    public void setStarted(boolean started){
        isStarted=started;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setOnCompletePlay(){
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(isShuffle()){
                    if(mRepeatMode == 2){
                        startNewSong(mSongPlay.data);
                    } else {
                        nextSong();
                        updateUi();
                        updateNotification();
                    }
                } else {
                    switch (mRepeatMode) {
                        case 0:
                            if (mPosPlay == (mListPlay.size() -1))
                            {
                                nextSong();
                                pause();
                            } else {
                                nextSong();
                            }
                            updateNotification();
                            updateUi();
                            break;
                        case 1:
                            nextSong();
                            updateNotification();
                            updateUi();
                            break;
                        case 2:
                            startNewSong(mSongPlay.data);
                            break;
                    }
                }
            }
        });
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void nextSong(){
        if(mPlayer==null) {
            Toast.makeText(getBaseContext(), "Ban chua chon bai hat", Toast.LENGTH_SHORT).show();
            return;
        }
        if(mIsShuffle) {
            Random random =new Random();
            mPosPlay = random.nextInt( mListPlay.size()-1);
        } else {
            if( mPosPlay < mListPlay.size()-1){
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
        if(mPlayer==null) {
            Toast.makeText(getBaseContext(), "Ban chia chon bai hat", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mPlayer.getCurrentPosition() > 3000){
            startNewSong(mSongPlay.data);
            return;
        }
        if(mIsShuffle) {
            Random random =new Random();
            mPosPlay = random.nextInt( mListPlay.size()-1);
        } else {
            if( mPosPlay > 0){
                mPosPlay --;
            } else {
                mPosPlay = mListPlay.size() -1;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    void prepareLastSong(){
        albumBitmap = Song.getAlbumImage(mSongPlay.data);
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mSongPlay.data);
            mPlayer.prepare();
            setOnCompletePlay();
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Exception", Toast.LENGTH_SHORT);
        }
    }

    public Bitmap getAlbumBitmap(){
        return albumBitmap;
    }

   BroadcastReceiver receiver = new BroadcastReceiver() {
       @RequiresApi(api = Build.VERSION_CODES.O)
       @Override
       public void onReceive(Context context, Intent intent) {
           String action= intent.getAction();
           switch (action){
               case ACTION_NEXT: nextSong();
               break;
               case ACTION_PLAY: pause();
               break;
               case ACTION_PREVIOUS: previousSong();
               break;
           }
           updateUi();
       }
   };
}
