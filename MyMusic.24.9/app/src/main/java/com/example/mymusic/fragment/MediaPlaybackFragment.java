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

import com.example.mymusic.ActivityMusic;
import com.example.mymusic.R;
import com.example.mymusic.Song;

public class MediaPlaybackFragment extends Fragment implements ActivityMusic.IshowActionBar {

    private TextView mNamePlay, mArtist, mTime;
    private ImageView mPlayBtn2, mMiniImage, mBigImage;

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
        mPlayBtn2 = view.findViewById(R.id.play_btn_2);
        mPlayBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setmPlayBtn(activityMusic);
            }
        });

        mTime = view.findViewById(R.id.show_total_time);
        mNamePlay = view.findViewById(R.id.show_name_2);
        mArtist = view.findViewById(R.id.show_artist_2);
        if (activityMusic.getIsPlay()) {
            mPlayBtn2.setImageResource(R.drawable.ic_play_orange);
        }

        if (activityMusic.getSongPlay() != null) {
            Song song = activityMusic.getSongPlay();
            mTime.setText(song.mTime);
            mArtist.setText(song.mArtist);
            mNamePlay.setText(song.mName);
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
        if (!activityMusic.getIsPlay()) {
            activityMusic.setPlay(true);
            mPlayBtn2.setImageResource(R.drawable.ic_play_orange);

        } else {
            activityMusic.setPlay(false);
            mPlayBtn2.setImageResource(R.drawable.ic_pause_orange);
        }
    }

    //only call in lanscape
    public void updateUi() {
        final ActivityMusic activityMusic = (ActivityMusic) getActivity();
        Song song = activityMusic.getSongPlay();
        mTime.setText(song.mTime);
        mArtist.setText(song.mArtist);
        mNamePlay.setText(song.mName);
        mPlayBtn2.setImageResource(R.drawable.ic_play_orange);
    }
}
