package com.example.mymusic.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import java.util.concurrent.TimeUnit;

public class MediaPlaybackFragment extends Fragment implements ActivityMusic.IshowActionBar, View.OnClickListener {
    private TextView mNamePlay, mArtistPlay, mDuration, mTimePlay;
    private ImageView mPlayBtn, mImage, mBigImage, mShuffleBtn, mRepeatBtn, mNextBtn, mPreviuousBtn;
    private ActivityMusic mActivity;
    private SeekBar mSeekBar;
    private SeekBarAsyncTask mAsyncTask;

    public static MediaPlaybackFragment newInstance() {
        return new MediaPlaybackFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (ActivityMusic) getActivity();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyService.UP_DATE_UI);
        mActivity.registerReceiver(receiver, intentFilter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media_playback, container, false);
        //set PlayButton;
        findView(view);
        setOnClick();
        if (mActivity.isBound()) {
            Song song = mActivity.getService().getSongPlay();
            if (mActivity.getService().getSongPlay() != null) {
                int minute = (int) song.duration / 60000;
                int second = (int) song.duration / 1000 % 60;
                mDuration.setText(String.format("%02d:%02d", minute, second));
                mArtistPlay.setText(song.artist);
                mNamePlay.setText(song.title);
                int time = mActivity.getService().getTime();
                mSeekBar.setMax((int) TimeUnit.MILLISECONDS.toSeconds(song.duration));
                mSeekBar.setProgress(time);
                int munutePlay = time / 60;
                int secondPlay = time % 60;
                mTimePlay.setText(String.format("%02d:%02d", munutePlay, secondPlay));
                if (mActivity.getService().getAlbumBitmap() != null) {
                    mImage.setImageBitmap(mActivity.getService().getAlbumBitmap());
                    mBigImage.setImageBitmap(mActivity.getService().getAlbumBitmap());
                } else {
                    mImage.setImageResource(R.drawable.defaut_album_image);
                    mBigImage.setImageResource(R.drawable.defaut_album_image);
                }
            }
            if (mActivity.getService().isPlaying()) {
                mPlayBtn.setImageResource(R.drawable.ic_play_orange);
                mAsyncTask = new SeekBarAsyncTask();
                mAsyncTask.execute();
            }

            if (mActivity.getService().isShuffle()) {
                mShuffleBtn.setImageResource(R.drawable.ic_play_shuffle_orange);
            }
            switch (mActivity.getService().getRepeatMode()) {
                case 1:
                    mRepeatBtn.setImageResource(R.drawable.ic_repeat_dark_selected);
                    break;
                case 2:
                    mRepeatBtn.setImageResource(R.drawable.ic_repeat_one_song_dark);
                    break;
            }
        }

        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        showActionBar((ActivityMusic) getActivity());
        if (mAsyncTask != null) mAsyncTask.cancel(true);
    }

    @Override
    public void showActionBar(ActivityMusic activityMusic) {
        activityMusic.getSupportActionBar().show();
    }

    //set play button va trang thai isPlay
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setmPlayBtn(ActivityMusic activityMusic) {
        mActivity.getService().pause();
        Song song = mActivity.getService().getSongPlay();
        if (mActivity.getService().isPlaying()) {
            if (song != null) {
                mAsyncTask = new SeekBarAsyncTask();
                mAsyncTask.execute();
            }
            mPlayBtn.setImageResource(R.drawable.ic_play_orange);

        } else {
            if (mAsyncTask != null) mAsyncTask.cancel(true);
            mPlayBtn.setImageResource(R.drawable.ic_pause_orange);
        }
    }


    public void updateUi() {
        ActivityMusic activityMusic = (ActivityMusic) getActivity();
        if (activityMusic != null && mActivity.isBound()) {
            Song song = mActivity.getService().getSongPlay();
            if (song != null) {
                int munute = (int) song.duration / 60000;
                int second = (int) song.duration / 1000 % 60;
                mDuration.setText(String.format("%02d:%02d", munute, second));
                mArtistPlay.setText(song.artist);
                mSeekBar.setMax((int) TimeUnit.MILLISECONDS.toSeconds(song.duration));
                mNamePlay.setText(song.title);
                if (mActivity.getService().isPlaying()) {
                    mPlayBtn.setImageResource(R.drawable.ic_play_orange);
                } else {
                    mPlayBtn.setImageResource(R.drawable.ic_pause_orange);
                }
                Bitmap albumImage = mActivity.getService().getAlbumBitmap();
                if (albumImage != null) {
                    mImage.setImageBitmap(albumImage);
                    mBigImage.setImageBitmap(albumImage);
                } else {
                    mImage.setImageResource(R.drawable.defaut_album_image);
                    mBigImage.setImageResource(R.drawable.defaut_album_image);
                }
            }
        }
    }

    public void findView(View view) {
        mDuration = view.findViewById(R.id.duration);
        mTimePlay = view.findViewById(R.id.time_play);
        mNamePlay = view.findViewById(R.id.name_song_playing_2);
        mArtistPlay = view.findViewById(R.id.artist_song_playing_2);
        mImage = view.findViewById(R.id.image_song_playing_2);
        mBigImage = view.findViewById(R.id.big_image_song_playing);
        mShuffleBtn = view.findViewById(R.id.set_shuffle_button);
        mPlayBtn = view.findViewById(R.id.play_button_2);
        mPreviuousBtn = view.findViewById(R.id.previous_button);
        mSeekBar = view.findViewById(R.id.seek_bar);
        mRepeatBtn = view.findViewById(R.id.set_repeat_button);
        mNextBtn = view.findViewById(R.id.next_button);
    }

    public void setOnClick() {
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Song song = mActivity.getService().getSongPlay();
                mActivity.getService().seek(seekBar.getProgress());
                if (mAsyncTask!= null) {
                    mAsyncTask.cancel(true);
                }
                if (song != null) {
                    mAsyncTask = new SeekBarAsyncTask();
                }
                if (mActivity.getService().isPlaying()) mAsyncTask.execute();
            }
        });
        mPlayBtn.setOnClickListener(this);
        mShuffleBtn.setOnClickListener(this);
        mRepeatBtn.setOnClickListener(this);
        mNextBtn.setOnClickListener(this);
        mPreviuousBtn.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.play_button_2:
                setmPlayBtn(mActivity);
                break;

            case R.id.next_button:
                if (mAsyncTask != null) mAsyncTask.cancel(true);
                mActivity.getService().nextSong();
                mAsyncTask = new SeekBarAsyncTask();
                mAsyncTask.execute();
                updateUi();
                break;

            case R.id.previous_button:
                if (mAsyncTask != null) mAsyncTask.cancel(true);
                mActivity.getService().previousSong();
                mAsyncTask = new SeekBarAsyncTask();
                mAsyncTask.execute();
                updateUi();
                break;

            case R.id.set_shuffle_button:
                mActivity.getService().setShuffle();
                if (mActivity.getService().isShuffle()) {
                    mShuffleBtn.setImageResource(R.drawable.ic_play_shuffle_orange);
                } else {
                    mShuffleBtn.setImageResource(R.drawable.ic_shuffle_white);
                }
                break;

            case R.id.set_repeat_button:
                mActivity.getService().setRepeatMode();
                switch (mActivity.getService().getRepeatMode()) {
                    case 0:
                        mRepeatBtn.setImageResource(R.drawable.ic_repeat_white);
                        break;
                    case 1:
                        mRepeatBtn.setImageResource(R.drawable.ic_repeat_dark_selected);
                        break;
                    case 2:
                        mRepeatBtn.setImageResource(R.drawable.ic_repeat_one_song_dark);
                        break;
                }
                break;
        }
    }

    class SeekBarAsyncTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            for (; ; ) {
                try {
                    Thread.sleep(1000);
                    publishProgress(mActivity.getService().getTime());
                } catch (InterruptedException e) {
                    Toast.makeText(getContext(), "Co loi say ra", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            long munute = values[0] / 60;
            long second = values[0] % 60;
            mTimePlay.setText(String.format("%02d:%02d", munute, second));
            mSeekBar.setProgress(values[0]);
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUi();
        }
    };
}
