package com.example.mymusic.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymusic.ActivityMusic;
import com.example.mymusic.MyService;
import com.example.mymusic.R;
import com.example.mymusic.Song;

public class MediaPlaybackFragment extends Fragment implements ActivityMusic.IshowActionBar {
    private AllSongsFragment mAllSongFragment;
    private TextView mNamePlay, mArtistPlay, mDuration,mTimePlay;
    private ImageView mPlayBtn, mImage, mBigImage,mShuffleBtn, mRepeatBtn, mNextBtn, mPreviuousBtn;
    private ActivityMusic activityMusic;
    private SeekBar mSeekBar;
    SeekBarAsyncTask asyncTask;
    public static MediaPlaybackFragment newInstance() {
        return new MediaPlaybackFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMusic = (ActivityMusic) getActivity();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyService.UP_DATE_UI);
        activityMusic.registerReceiver(receiver,intentFilter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media_playback, container, false);
        //set PlayButton;
        findView(view);
        setOnClick();
        if(activityMusic.mBound){
            Song song = activityMusic.mService.getSongPlay();
            if (activityMusic.mService.getSongPlay() != null) {
                int munute=(int) song.duration/60000;
                int second=(int) song.duration/1000 %60;
                mDuration.setText(String.format("%02d:%02d",munute,second));
                mArtistPlay.setText(song.artist);
                mNamePlay.setText(song.title);
                Bitmap albumImage= Song.getAlbumImage(song.data);
                long time = activityMusic.mService.getTime();
                mSeekBar.setProgress((int) time/ (int)song.duration );
                int munutePlay=(int) time/60000;
                int secondPlay=(int) time/1000 %60;
                mTimePlay.setText(String.format("%02d:%02d",munutePlay,secondPlay));
                if(albumImage != null){
                    mImage.setImageBitmap(albumImage);
                    mBigImage.setImageBitmap(albumImage);
                }
                else {
                    mImage.setImageResource(R.drawable.defaut_album_image);
                    mBigImage.setImageResource(R.drawable.defaut_album_image);
                }
            }
            if (activityMusic.mService.isPlaying()) {
                mPlayBtn.setImageResource(R.drawable.ic_play_orange);
                asyncTask = new SeekBarAsyncTask();
                asyncTask.execute();
            }

            if(activityMusic.mService.isShuffle()){
                mShuffleBtn.setImageResource(R.drawable.ic_play_shuffle_orange);
            }
        }

        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        showActionBar((ActivityMusic) getActivity());
        if (asyncTask != null) asyncTask.cancel(true);
    }

    @Override
    public void showActionBar(ActivityMusic activityMusic) {
        activityMusic.getSupportActionBar().show();
    }

    //set play button va trang thai isPlay
    public void setmPlayBtn(ActivityMusic activityMusic) {
        activityMusic.mService.pause();
        Song song = activityMusic.mService.getSongPlay();
        if (activityMusic.mService.isPlaying()) {
            if (song != null) {
                asyncTask = new SeekBarAsyncTask();
                asyncTask.execute();
            }
            mPlayBtn.setImageResource(R.drawable.ic_play_orange);

        } else {
            if (asyncTask != null) asyncTask.cancel(true);
            mPlayBtn.setImageResource(R.drawable.ic_pause_orange);
        }
    }

    public void setAllSongFragment(AllSongsFragment allSongFragment){
        mAllSongFragment = allSongFragment;
    }

    public void updateUi() {
        final ActivityMusic activityMusic = (ActivityMusic) getActivity();
        Song song = activityMusic.mService.getSongPlay();
        int munute=(int) song.duration/60000;
        int second=(int) song.duration/1000 %60;
        mDuration.setText(String.format("%02d:%02d",munute,second));
        mArtistPlay.setText(song.artist);
        mNamePlay.setText(song.title);
        mPlayBtn.setImageResource(R.drawable.ic_play_orange);
        Bitmap albumImage= Song.getAlbumImage(song.data);
        if(albumImage != null){
            mImage.setImageBitmap(albumImage);
            mBigImage.setImageBitmap(albumImage);
        }
        else {
            mImage.setImageResource(R.drawable.defaut_album_image);
            mBigImage.setImageResource(R.drawable.defaut_album_image);
        }
        if (mAllSongFragment != null){
            mAllSongFragment.mAdapter.notifyDataSetChanged();
        }
    }

    public void findView(View view){
        mDuration = view.findViewById(R.id.duration);
        mTimePlay= view.findViewById(R.id.time_play);
        mNamePlay = view.findViewById(R.id.name_song_playing_2);
        mArtistPlay = view.findViewById(R.id.artist_song_playing_2);
        mImage=view.findViewById(R.id.image_song_playing_2);
        mBigImage= view.findViewById(R.id.big_image_song_playing);
        mShuffleBtn= view.findViewById(R.id.set_shuffle_button);
        mPlayBtn = view.findViewById(R.id.play_button_2);
        mPreviuousBtn =view.findViewById(R.id.previous_button);
        mSeekBar =view.findViewById(R.id.seek_bar);
        mRepeatBtn= view.findViewById(R.id.set_repeat_button);
        mNextBtn= view.findViewById(R.id.next_button);
    }

    public void setOnClick(){
        mPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setmPlayBtn(activityMusic);
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Song song = activityMusic.mService.getSongPlay();
                activityMusic.mService.seek(seekBar.getProgress());
                if (asyncTask != null) {
                    asyncTask.cancel(true);
                }
                if (song != null) {
                    asyncTask = new SeekBarAsyncTask();
                }
                if ( activityMusic.mService.isPlaying()) asyncTask.execute();
            }
        });

        mShuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityMusic.mService.setShuffle();
                if (activityMusic.mService.isShuffle()){
                    mShuffleBtn.setImageResource(R.drawable.ic_play_shuffle_orange);
                } else {
                    mShuffleBtn.setImageResource(R.drawable.ic_shuffle_white);
                }
            }
        });

        mRepeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityMusic.mService.setRepeatMode();
                switch (activityMusic.mService.getRepeatMode()){
                    case 0: mRepeatBtn.setImageResource(R.drawable.ic_repeat_white);
                        break;
                    case 1: mRepeatBtn.setImageResource(R.drawable.ic_repeat_dark_selected);
                        break;
                    case 2: mRepeatBtn.setImageResource(R.drawable.ic_repeat_one_song_dark);
                        break;
                }
            }
        });


        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (asyncTask != null) asyncTask.cancel(true);
                activityMusic.mService.nextSong();
                asyncTask = new SeekBarAsyncTask();
                asyncTask.execute();
                updateUi();
            }
        });


        mPreviuousBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (asyncTask != null) asyncTask.cancel(true);
                activityMusic.mService.previousSong();
                asyncTask = new SeekBarAsyncTask();
                asyncTask.execute();
                updateUi();
            }
        });
    }

    class SeekBarAsyncTask extends AsyncTask<Void,Long,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            for(;;){
                try {
                    Thread.sleep(1000);
                    publishProgress(activityMusic.mService.getTime());
                } catch (InterruptedException e) {
                    Toast.makeText(getContext(),"Co loi say ra",Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            super.onProgressUpdate(values);
            long  munute= values[0]/60000;
            long  second= ( values[0] / 1000) % 60;
            mTimePlay.setText(String.format("%02d:%02d",munute,second));
            long percent = values[0]*100/activityMusic.mService.getSongPlay().duration;
            mSeekBar.setProgress((int) percent);
        }
    }
    BroadcastReceiver receiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT){
                updateUi();
            }
        }
    };
}
