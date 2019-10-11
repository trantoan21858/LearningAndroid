package com.example.mymusic;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.mymusic.fragment.AllSongsFragment;
import com.example.mymusic.fragment.MediaPlaybackFragment;

import java.util.ArrayList;

public class ActivityMusic extends AppCompatActivity {
    public static final String CHANNEL_ID="musicChannel";
    public MyService mService;
    public boolean mBound;
    private ArrayList<Song> mList= new ArrayList<>();
    public static final String MUSIC_APP_PREFERENCE ="MUSIC APP PREFERENCES";
    public static final String IS_SHUFFLE="Shuffle";
    public static final String REPEAT_MODE = "Repeat mode";
    public static final String ID_SONG = "id song last";
    SharedPreferences mPreference;
    private int idLast=0;
    private int newPos=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPreference = getSharedPreferences(MUSIC_APP_PREFERENCE,MODE_PRIVATE);
        idLast = mPreference.getInt(ID_SONG,0);
        creatList();
        Intent intent = new Intent(this,MyService.class);
        startService(intent);
        bindService(intent,connection,Context.BIND_AUTO_CREATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            creatNotificationchannel();
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        super.onDestroy();
    }

    void saveInfoSetting(){
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putInt(REPEAT_MODE,mService.getRepeatMode());
        editor.putBoolean(IS_SHUFFLE,mService.isShuffle());
        editor.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveInfoSetting();
    }

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

    ServiceConnection connection=new ServiceConnection() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyService.MyBinder binder = (MyService.MyBinder) iBinder;
            mService = binder.getService();
            mBound = true;

            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.activity_music, AllSongsFragment.newInstance()).commit();
            } else {
                AllSongsFragment songsFragment = AllSongsFragment.newInstance();
                MediaPlaybackFragment playbackFragment = MediaPlaybackFragment.newInstance();
                songsFragment.setmPlaybackFragment(playbackFragment);
                playbackFragment.setAllSongFragment(songsFragment);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fram_1, songsFragment)
                        .replace(R.id.fram_2, playbackFragment)
                        .commit();
            }
            mService.setListPlay(mList);
            mService.setRepeatMode(mPreference.getInt(REPEAT_MODE,0));
            mService.setShuffle(mPreference.getBoolean(IS_SHUFFLE,false));
            if(newPos!= -1){
                mService.setSongPlay(mList.get(newPos));
                mService.setPosPlay(newPos);
                mService.prepareLastSong();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound=false;
        }
    };

    public interface IshowActionBar {
        void showActionBar(ActivityMusic activityMusic);
    }

    public ArrayList<Song> getmList() {
        return mList;
    }
    //creat list Song
    void creatList(){
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media._ID
        };
        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);
        int i=0;
        while(cursor.moveToNext()){
            mList.add(
                    new Song(
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getLong(2),
                            cursor.getString(3),
                            cursor.getInt(4)
                    )
            );

            if(cursor.getInt(4) == idLast){
                newPos=i;
            }
            i++;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void creatNotificationchannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Music channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }
}

