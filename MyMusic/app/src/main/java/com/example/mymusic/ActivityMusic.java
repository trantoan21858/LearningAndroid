package com.example.mymusic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceFragment;
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
import com.example.mymusic.fragment.SettingFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class ActivityMusic extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String CHANNEL_ID="musicChannel";
    private MyService mService;
    private  boolean mBound;
    private  boolean mIsShowFavorite;
    private ArrayList<Song> mList= new ArrayList<>();
    public static final String MUSIC_APP_PREFERENCE ="MUSIC APP PREFERENCES";
    public static final String IS_SHUFFLE="Shuffle";
    public static final String REPEAT_MODE = "Repeat mode";
    public static final String ID_SONG = "id song last";
    private static final String NAME_CHANNEL="Music channel";
    private static final String IS_SHOW_FAVORITE= "is_show_favorite";
    private SharedPreferences mPreference;
    private int mIdLast=0;
    private int mNewPos=-1;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;
    private BaseSongListFragment mSongsFragment;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
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

        String[] premission= {
          Manifest.permission.READ_EXTERNAL_STORAGE
                ,Manifest.permission.READ_PHONE_STATE
        };

        if(checkReadExternalPermission()){
            creatList();
        } else {
            ActivityCompat.requestPermissions(ActivityMusic.this,
                    premission,
                    1);
        }

        Intent intent = new Intent(this,MyService.class);
        if(savedInstanceState!=null){
            mIsShowFavorite = savedInstanceState.getBoolean(IS_SHOW_FAVORITE);
        }
        if (mIsShowFavorite){
            mNavigationView.getMenu().getItem(1).setChecked(true);
        }
        startService(intent);
        bindService(intent, mConnection,Context.BIND_AUTO_CREATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            creatNotificationchannel();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_SHOW_FAVORITE, mIsShowFavorite);
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
    public boolean getmIsShowFavorite(){
        return mIsShowFavorite;
    }

    @Override
    protected void onDestroy() {
        unbindService(mConnection);
        super.onDestroy();
    }

    void saveInfoSetting(){
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putInt(REPEAT_MODE,mService.getRepeatMode());
        editor.putBoolean(IS_SHUFFLE,mService.isShuffle());
        editor.apply();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkReadExternalPermission()
    {
        String permission = android.Manifest.permission.READ_EXTERNAL_STORAGE;
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
                if(requestCode == 1){
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        creatList();
                        mSongsFragment= AllSongsFragment.newInstance();
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

                    }

                    return;
                }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mService!=null){
            saveInfoSetting();
        }
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
        transaction.
                replace(R.id.activity_music, MediaPlaybackFragment.newInstance())
                .addToBackStack(null)
                .commit();
        mNavigationView.setVisibility(View.INVISIBLE);
    }

    public void backToList(View view) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        getSupportFragmentManager().popBackStack();
        transaction.replace(R.id.activity_music, mSongsFragment)
                .commit();
    }

    private ServiceConnection mConnection =new ServiceConnection() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyService.MyBinder binder = (MyService.MyBinder) iBinder;
            mService = binder.getService();
            mBound = true;
            mService.setIdSongPlay(mIdLast);
            if (mIsShowFavorite){
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
                mIsShowFavorite =false;
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
                mIsShowFavorite =true;
                break;
            case R.id.nav_setting:
                FragmentTransaction transaction12 = getSupportFragmentManager().beginTransaction();
                int orientaion = getResources().getConfiguration().orientation;
                if(orientaion==Configuration.ORIENTATION_PORTRAIT){
                    transaction12.replace(R.id.activity_music, new SettingFragment()).addToBackStack(null)
                            .commit();
                } else {
                    transaction12.replace(R.id.fram_2, new SettingFragment()).addToBackStack(null)
                            .commit();
                }
                break;
                default:
                    Toast.makeText(ActivityMusic.this,"Cảm ơn bạn đã nghe nhạc!",Toast.LENGTH_SHORT).show();
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openSetting(View view) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        int orientaion = getResources().getConfiguration().orientation;
        if(orientaion==Configuration.ORIENTATION_PORTRAIT){
            transaction.replace(R.id.activity_music, new SettingFragment()).addToBackStack(null)
                    .commit();
        } else {
            transaction.replace(R.id.fram_2, new SettingFragment()).addToBackStack(null)
                    .commit();
        }
    }

    public interface IShowActionBar {
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
                NAME_CHANNEL,
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.enableVibration(false);
        channel.enableLights(false);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

}

