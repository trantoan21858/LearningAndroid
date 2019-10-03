package com.example.mymusic.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymusic.ActivityMusic;
import com.example.mymusic.R;
import com.example.mymusic.Song;

public class MediaPlaybackFragment extends Fragment implements ActivityMusic.IshowActionBar {

    private TextView mNamePlay, mArtistPlay, mDuration,mTimePlay;
    private ImageView mPlayBtn, mImage, mBigImage;

    public static MediaPlaybackFragment newInstance() {
        return new MediaPlaybackFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media_playback, container, false);
        final ActivityMusic activityMusic = (ActivityMusic) getActivity();
        //set PlayButton;
        mPlayBtn = view.findViewById(R.id.play_button_2);
        mPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setmPlayBtn(activityMusic);
            }
        });
        mDuration = view.findViewById(R.id.duration);
        mNamePlay = view.findViewById(R.id.name_song_playing_2);
        mArtistPlay = view.findViewById(R.id.artist_song_playing_2);
        if (activityMusic.mService.isPlaying()) {
            mPlayBtn.setImageResource(R.drawable.ic_play_orange);
        }

        if (activityMusic.getSongPlay() != null) {
            Song song = activityMusic.getSongPlay();
            int munute=(int) song.duration/60000;
            int second=(int) song.duration/1000 %60;
            mDuration.setText(String.format("%02d:%02d",munute,second));
            mArtistPlay.setText(song.artist);
            mNamePlay.setText(song.title);
        }
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        showActionBar((ActivityMusic) getActivity());
    }

    @Override
    public void showActionBar(ActivityMusic activityMusic) {
        activityMusic.getSupportActionBar().show();
    }

    //set play button va trang thai isPlay
    public void setmPlayBtn(ActivityMusic activityMusic) {
        activityMusic.mService.pause();
        if (!activityMusic.getIsPlay()) {
            activityMusic.setPlay(true);
            mPlayBtn.setImageResource(R.drawable.ic_play_orange);

        } else {
            activityMusic.setPlay(false);
            mPlayBtn.setImageResource(R.drawable.ic_pause_orange);
        }
    }

    //only call in lanscape
    public void updateUi() {
        final ActivityMusic activityMusic = (ActivityMusic) getActivity();
        Song song = activityMusic.getSongPlay();
        int munute=(int) song.duration/60000;
        int second=(int) song.duration/1000 %60;
        mDuration.setText(String.format("%02d:%02d",munute,second));
        mArtistPlay.setText(song.artist);
        mNamePlay.setText(song.title);
        mPlayBtn.setImageResource(R.drawable.ic_play_orange);
    }
}
