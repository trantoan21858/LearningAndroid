package com.example.mymusic;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mymusic.fragment.AllSongsFragment;
import com.example.mymusic.fragment.BaseSongListFragment;
import com.example.mymusic.fragment.FavoriteSongsFragment;
import com.example.mymusic.fragment.MediaPlaybackFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class ActivityMusic extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String CHANNEL_ID="musicChannel";
    private MyService mService;
    private  boolean mBound;
    private  boolean isShowFavorite;
    private ArrayList<Song> mList= new ArrayList<>();
    public static final String MUSIC_APP_PREFERENCE ="MUSIC APP PREFERENCES";
    public static final String IS_SHUFFLE="Shuffle";
    public static final String REPEAT_MODE = "Repeat mode";
    public static final String ID_SONG = "id song last";
    private SharedPreferences mPreference;
    private int mIdLast=0;
    private int mNewPos=-1;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;
    private BaseSongListFragment mSongsFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawer = findViewById(R.id.drawer_layout);
        mPreference = getSharedPreferences(MUSIC_APP_PREFERENCE,MODE_PRIVATE);
        mIdLast = mPreference.getInt(ID_SONG,0);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(mDrawerToggle);
        mNavigationView=findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.getMenu().getItem(0).setChecked(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        creatList();
        Intent intent = new Intent(this,MyService.class);
        if(savedInstanceState!=null){
            isShowFavorite = savedInstanceState.getBoolean("is_show_favorite");
        }
        if (isShowFavorite){
            mNavigationView.getMenu().getItem(1).setChecked(true);
        }
        startService(intent);
        bindService(intent,connection,Context.BIND_AUTO_CREATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            creatNotificationchannel();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_show_favorite",isShowFavorite);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public MyService getService() {
        return mService;
    }
    public boolean isBound(){
        return mBound;
    }
    public boolean getShowFavorite(){
        return isShowFavorite;
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
        editor.apply();
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
        MenuItem searchItem = menu.findItem(R.id.search_button);
        SearchView searchView =(SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mSongsFragment.getAdapter().getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void changeFragment(View view) {
        getSupportActionBar().hide();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.activity_music, MediaPlaybackFragment.newInstance()).commit();
        mNavigationView.setVisibility(View.INVISIBLE);
    }

    public void backToList(View view) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        getSupportFragmentManager().popBackStack();
        transaction.replace(R.id.activity_music, mSongsFragment)
                .commit();
    }

    ServiceConnection connection=new ServiceConnection() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyService.MyBinder binder = (MyService.MyBinder) iBinder;
            mService = binder.getService();
            mBound = true;
            mService.setIdSongPlay(mIdLast);
            if (isShowFavorite){
                mSongsFragment=FavoriteSongsFragment.newInstance();
            } else {
                mSongsFragment =AllSongsFragment.newInstance();
            }
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.activity_music, mSongsFragment).commit();
            } else {
                MediaPlaybackFragment playbackFragment = MediaPlaybackFragment.newInstance();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fram_1, mSongsFragment)
                        .replace(R.id.fram_2, playbackFragment)
                        .commit();
            }
            if(!mService.isStarted()){
                mService.setListPlay(mList);
                mService.setRepeatMode(mPreference.getInt(REPEAT_MODE,0));
                mService.setShuffle(mPreference.getBoolean(IS_SHUFFLE,false));
                if(mNewPos!= -1){
                    mService.setPosPlay(mNewPos);
                    mService.setSongPlay(mList.get(mNewPos));
                    mService.prepareLastSong();
                }
                mService.setStarted(true);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound=false;
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int orientation = getResources().getConfiguration().orientation;
        switch (menuItem.getItemId()){
            case R.id.nav_allsong:
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                getSupportFragmentManager().popBackStack();
                mSongsFragment = AllSongsFragment.newInstance();
                if(orientation == Configuration.ORIENTATION_PORTRAIT){
                    transaction.replace(R.id.activity_music, mSongsFragment).commit();
                } else {
                    transaction.replace(R.id.fram_1,mSongsFragment).commit();
                }
                mNavigationView.getMenu().getItem(0).setChecked(true);
                isShowFavorite=false;
                break;
            case R.id.nav_favorite:
                getSupportFragmentManager().popBackStack();
                FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                mSongsFragment = FavoriteSongsFragment.newInstance();
                if(orientation == Configuration.ORIENTATION_PORTRAIT){
                    transaction1.replace(R.id.activity_music, mSongsFragment).commit();
                } else {
                    transaction1.replace(R.id.fram_1,mSongsFragment).commit();
                }
                mNavigationView.getMenu().getItem(1).setChecked(true);
                isShowFavorite=true;
                break;
                default:
                    Toast.makeText(ActivityMusic.this,"Cảm ơn bạn đã nghe nhạc!",Toast.LENGTH_SHORT).show();
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

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

            if(cursor.getInt(4) == mIdLast){
                mNewPos=i;
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

