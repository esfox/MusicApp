package com.music.app.utils;

import android.media.MediaPlayer;
import android.util.Log;

import com.badoo.mobile.util.WeakHandler;
import com.music.app.interfaces.AudioListener;

public class TimeUpdater
{
    private WeakHandler timeUpdater;
    private AudioListener audioListener;
    private MediaPlayer player;

    public TimeUpdater(AudioListener audioListener, MediaPlayer player)
    {
        timeUpdater = new WeakHandler();
        this.audioListener = audioListener;
        this.player = player;
    }

    private Runnable timeUpdaterRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            Log.d("TimeUpdater", "I am running.");
            timeUpdate();
        }
    };

    private void timeUpdate()
    {
        audioListener.updateTime(player.getCurrentPosition());
        timeUpdater.postDelayed(timeUpdaterRunnable, 500);
    }

    public void restart()
    {
        timeUpdater.removeCallbacks(timeUpdaterRunnable);
        timeUpdate();
    }

    public void toggle()
    {
        Log.d("TimeUpdater", "I'm toggled.");
        if(player.isPlaying())
            timeUpdate();
        else
            timeUpdater.removeCallbacks(timeUpdaterRunnable);
    }
}
