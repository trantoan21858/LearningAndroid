package com.example.mymusic;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.mymusic.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static com.example.mymusic.ActivityMusic.CHANNEL_ID;

public class MyService extends Service {
    private  final IBinder binder= new MyBinder();
    private ArrayList<Song> mListPlay;
    MediaPlayer mPlayer;
    private boolean mIsShuffle;
    private int mRepeat=0;

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

    /*public void setList(ArrayList<Song> songs){
        mListPlay=songs;
    }*/

    public class MyBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startNewSong(final String data){
        if (mPlayer!=null) mPlayer.release();
        mPlayer= new MediaPlayer();
        try {
            mPlayer.setDataSource(data);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Toast.makeText(getBaseContext(),"Exception",Toast.LENGTH_SHORT);
        }
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                startNewSong(data);
            }
        });
        startMyServiceForrground();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyServiceForrground(){
        Intent intent = new Intent(this,ActivityMusic.class);
        PendingIntent pendingIntent= PendingIntent.getActivity(this,0,intent,0);
        RemoteViews defaultNotification=
                new RemoteViews(getPackageName(),R.layout.default_notification);
        RemoteViews bigNotification=
                new RemoteViews(getPackageName(),R.layout.big_notification);
        Notification notification = new Notification.Builder(this,CHANNEL_ID)
                .setCustomContentView(defaultNotification)
                .setSmallIcon(R.drawable.ic_small_notification)
                .setCustomBigContentView(bigNotification)
                .setContentIntent(pendingIntent)
                .setStyle(new Notification.DecoratedCustomViewStyle())
                .build();
        startForeground(11,notification);
    }

    public void pause(){
        if(mPlayer!=null){
            if(mPlayer.isPlaying()){
                mPlayer.pause();
            }
            else mPlayer.start();
        }
    }

    public boolean isPlaying(){
        if (mPlayer!=null)
        {
            return mPlayer.isPlaying();
        }
        else return false;
    }
}
