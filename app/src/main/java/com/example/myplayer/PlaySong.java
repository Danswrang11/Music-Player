package com.example.myplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlaySong extends AppCompatActivity {
        TextView textView,textstart,textstop;
        ImageView previous, play, next,list;
        ArrayList<File> songs;
        static  MediaPlayer mediaPlayer;
        String sname;
        int position;
        SeekBar seekBar;
        Thread updateSeekbar;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_play_song);
            textView = findViewById(R.id.textView);
            previous = findViewById(R.id.previous);
            play = findViewById(R.id.play);
            next = findViewById(R.id.next);
            seekBar = findViewById(R.id.seekBar);
            textstop =findViewById(R.id.textstart);
            textstart= findViewById(R.id.textstop);
            list = findViewById(R.id.list);

            if(mediaPlayer != null)
            {
                mediaPlayer.stop();
                mediaPlayer.release();
            }

            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            songs = (ArrayList) bundle.getParcelableArrayList("songList");
            sname = intent.getStringExtra("currentSong");
            position = intent.getIntExtra("position", 0);
            textView.setSelected(true);
            Uri uri = Uri.parse(songs.get(position).toString());
            sname = songs.get(position).getName();
            textView.setText(sname);
            mediaPlayer = MediaPlayer.create(this, uri);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration());
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            });

            updateSeekbar = new Thread(){
                @Override
                public void run(){
                    int totalDuration = mediaPlayer.getDuration();
                    int currentPosition = 0;
                    while(currentPosition<totalDuration)
                    {
                         try
                         {
                             sleep(800);
                             currentPosition = mediaPlayer.getCurrentPosition();
                             seekBar.setProgress(currentPosition);
                        }
                         catch (Exception e){
                             e.printStackTrace();
                         }
                    }
                }
            };

            String endTime = createTime(mediaPlayer.getDuration());
            textstop.setText(endTime);
            final Handler handler = new Handler();
            final int delay = 1000;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String currentTime = createTime(mediaPlayer.getCurrentPosition());
                    textstart.setText(currentTime);
                    handler.postDelayed(this,delay);
                }
            },delay);

            seekBar.setMax(mediaPlayer.getDuration());
            updateSeekbar.start();
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            });

            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mediaPlayer.isPlaying()){
                        play.setImageResource(R.drawable.play);
                        mediaPlayer.pause();
                    }
                    else{
                        play.setImageResource(R.drawable.pause);
                        mediaPlayer.start();
                    }
                }
            });
            //play next song automatically or can be loop a single song
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    next.performClick();
                }
            });

            previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    position = ((position-1)<0)?(songs.size()-1):(position-1);
                    Uri uri = Uri.parse(songs.get(position).toString());
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                    mediaPlayer.start();
                    String endTime = createTime(mediaPlayer.getDuration());
                    textstop.setText(endTime);
                    play.setImageResource(R.drawable.pause);
                    seekBar.setMax(mediaPlayer.getDuration());
                    sname = songs.get(position).getName().toString();
                    textView.setText(sname);
                }
            });
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    position = ((position+1)% songs.size());
                    Uri uri = Uri.parse(songs.get(position).toString());
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                    mediaPlayer.start();
                    String endTime = createTime(mediaPlayer.getDuration());
                    textstop.setText(endTime);
                    play.setImageResource(R.drawable.pause);
                    seekBar.setMax(mediaPlayer.getDuration());
                    sname = songs.get(position).getName().toString();
                    textView.setText(sname);
                }
            });
            list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            });
    }
    //As music time is given in milli-second so to covert the into minute and second we call this method
    public String createTime(int duration)
    {
        String time="";
        int min = duration/1000/60;
        int sec = duration/1000%60;
        time+=min+":";
        if(sec<10)
        {
            time+="0";
        }
        time+=sec;
        return time;
    }
}