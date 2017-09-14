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

    private boolean isRunning = true;

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
            Log.d("TimeUpdater", "Running.");
            timeUpdate();
        }
    };

    private void timeUpdate()
    {
        if(isRunning)
            audioListener.updateTime(player.getCurrentPosition());
        else
            audioListener.updateTime(0);

        timeUpdater.postDelayed(timeUpdaterRunnable, 500);
    }

    public void restart()
    {
        Log.d("TimeUpdater", "Restart");
        isRunning = false;
        timeUpdater.removeCallbacks(timeUpdaterRunnable);

        new WeakHandler().post(new Runnable()
        {
            @Override
            public void run()
            {
                isRunning = true;
            }
        });
        timeUpdate();
    }
    public void pause()
    {
        Log.d("TimeUpdater", "Pause");
        isRunning = false;
        timeUpdater.removeCallbacks(timeUpdaterRunnable);
    }

    public void resume()
    {
        Log.d("TimeUpdater", "Resume");
        isRunning = true;
        timeUpdate();
    }
}
