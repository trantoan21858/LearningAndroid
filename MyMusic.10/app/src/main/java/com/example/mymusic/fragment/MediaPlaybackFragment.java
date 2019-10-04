package com.example.mymusic.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    private TextView mNamePlay, mArtistPlay, mDuration,mTimePlay;
    private ImageView mPlayBtn, mImage, mBigImage;
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media_playback, container, false);
        //set PlayButton;
        mPlayBtn = view.findViewById(R.id.play_button_2);
        mPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setmPlayBtn(activityMusic);
            }
        });
        mSeekBar =view.findViewById(R.id.seek_bar);
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
                    asyncTask = new SeekBarAsyncTask(activityMusic.mService.getTime(),song.duration);
                }
                if ( activityMusic.mService.isPlaying()) asyncTask.execute();
            }
        });
        mDuration = view.findViewById(R.id.duration);
        mTimePlay= view.findViewById(R.id.time_play);
        mNamePlay = view.findViewById(R.id.name_song_playing_2);
        mArtistPlay = view.findViewById(R.id.artist_song_playing_2);
        mImage=view.findViewById(R.id.image_song_playing_2);
        mBigImage= view.findViewById(R.id.big_image_song_playing);

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
                asyncTask = new SeekBarAsyncTask(activityMusic.mService.getTime(),song.duration);
                asyncTask.execute();
            }
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        showActionBar((ActivityMusic) getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
                asyncTask = new SeekBarAsyncTask(activityMusic.mService.getTime(),song.duration);
                asyncTask.execute();
            }
            mPlayBtn.setImageResource(R.drawable.ic_play_orange);

        } else {
            if (asyncTask != null) asyncTask.cancel(true);
            mPlayBtn.setImageResource(R.drawable.ic_pause_orange);
        }
    }

    //only call in lanscape
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
    }

    class SeekBarAsyncTask extends AsyncTask<Void,Long,Void> {
        long startTime;
        long duration;
        SeekBarAsyncTask( long startTime, long duration){
            this.startTime= startTime;
            this.duration= duration;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for(;;){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Toast.makeText(getContext(),"Co loi say ra",Toast.LENGTH_SHORT).show();
                }
                startTime += 1000;
                publishProgress(startTime);
                if (startTime >= duration) startTime =0;
            }
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            super.onProgressUpdate(values);
            long  munute= values[0]/60000;
            long  second= ( values[0] / 1000) % 60;
            mTimePlay.setText(String.format("%02d:%02d",munute,second));
            long percent = values[0]*100/duration;
            mSeekBar.setProgress((int) percent);
        }
    }
}
