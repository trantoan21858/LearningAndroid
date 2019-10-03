package com.example.mymusic;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mymusic.fragment.AllSongsFragment;
import com.example.mymusic.fragment.MediaPlaybackFragment;

import java.util.ArrayList;

public class ActivityMusic extends AppCompatActivity {
    public static final String CHANNEL_ID="musicChannel";
    public MyService mService;
    private Song songPlay;
    private boolean isPlay=false;
    private int mPos = -1;
    private boolean mBound;
    private ArrayList<Song> mList= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        creatList();
        Intent intent = new Intent(this,MyService.class);
        startService(intent);
        bindService(intent,connection,Context.BIND_AUTO_CREATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            creatNotificationchannel();
        }
        /*if (savedInstanceState != null) {
            String time = savedInstanceState.getString("time");
            String name = savedInstanceState.getString("name");
            String artist = savedInstanceState.getString("artist");
            int id=savedInstanceState.getInt("id");
            songPlay = new Song(name,artist, 1,"");
            isPlay = savedInstanceState.getBoolean("isPlay");
            mPos = savedInstanceState.getInt("position");
        }*/
        if (mBound){
            isPlay=mService.isPlaying();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isPlay", isPlay);
        outState.putInt("position", mPos);
        if (songPlay != null) {
            outState.putString("name", songPlay.title);
            outState.putString("time", songPlay.title);
            outState.putString("artist", songPlay.artist);
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

    ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyService.MyBinder binder = (MyService.MyBinder) iBinder;
            mService = binder.getService();
            mBound = true;
            Toast.makeText(getApplicationContext(),"connection",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound=false;
            Toast.makeText(getApplicationContext(),"disconnect",Toast.LENGTH_SHORT).show();
        }
    };

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
        };
        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);

        while(cursor.moveToNext()){
            mList.add(
                    new Song(
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getLong(2),
                            cursor.getString(3)
                    )
            );
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void creatNotificationchannel(){
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Music channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

}

