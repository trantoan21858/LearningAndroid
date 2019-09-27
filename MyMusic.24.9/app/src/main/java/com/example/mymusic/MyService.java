package com.example.mymusic;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.mymusic.R;

import java.util.Random;

public class MyService extends Service {
    MediaPlayer mPlayer;
    int mId;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mPlayer.start();
        return null;
    }

    // Binder given to clients
    private final IBinder binder = new MyBinder();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class MyBinder extends Binder {
        MyService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MyService.this;
        }
    }

    @Override
    public void onCreate() {
        Toast.makeText(getBaseContext(),"oncreat",Toast.LENGTH_SHORT).show();
        mPlayer=MediaPlayer.create(getBaseContext(),R.raw.dem_trang_tinh_yeu);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mPlayer.start();
        return START_NOT_STICKY;

    }

    @Override
    public void onDestroy() {
        mPlayer.release();
    }
}
