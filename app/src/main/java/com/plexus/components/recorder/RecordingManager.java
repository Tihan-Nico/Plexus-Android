package com.plexus.components.recorder;

import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.plexus.R;
import com.plexus.model.posts.Post;

public class RecordingManager {

    SeekBar seekBar;
    ImageView playPreviousBtn, playRecordBtn, playNextBtn;

    private boolean playing = false;

    private MediaPlayer mediaPlayer;
    Runnable runnable;
    private Handler handler;


    private String formatTime(int millisecond) {
        int sec = Integer.valueOf(millisecond) / 1000;
        if (sec > 59) {
            int minute = (sec / 60);
            int minuteSec = sec - (minute * 60);
            return minute + "m" + " " + minuteSec + "s";

        } else {
            return sec + "s";
        }
    }

    private String formatSize(int sizeByte) {

        if (sizeByte > 1023) {
            int kb = sizeByte / 1024;
            if (kb > 1023) {
                int mb = kb / 1024;
                int kbMb = kb - mb * 1024;
                return mb + "." + kbMb + " Mb";
            } else {
                return kb + " Kb";
            }
        } else {
            return sizeByte + " Byte";
        }
    }

    private void playRecord(Post post) {
        //Play Music

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(post.getPostimage());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                seekBar.setMax(mediaPlayer.getDuration());
                mediaPlayer.start();
                changeSeekBar();
            });
        } catch (Exception ignored) {

        }
        //Change Layout

        playing = true;
        playRecordBtn.setBackgroundResource(R.drawable.pause);

        mediaPlayer.setOnCompletionListener(mp -> {
            stopPlaySong();
            seekBar.setProgress(0);
        });
    }

    private void changeSeekBar() {

        if (playing) {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            runnable = new Runnable() {
                @Override
                public void run() {
                    changeSeekBar();
                }
            };
            handler.postDelayed(runnable, 1000);
        }
    }

    private void stopPlaySong() {
        mediaPlayer.stop();
        mediaPlayer.release();
        playing = false;
        playRecordBtn.setBackgroundResource(R.drawable.play);
    }

}
