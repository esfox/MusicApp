package com.music.app.objects;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.music.app.R;
import com.music.app.utils.interfaces.ServiceListener;

import java.io.IOException;

public class Player extends Service
{
    private IBinder binder = new ServiceBinder();

    private ServiceListener serviceListener;

    private MediaPlayer player;
    private Data data;
    private Queue queue;

    private String path, title, artist;
    private boolean resumed;

    public class ServiceBinder extends Binder
    {
        public Player getService()
        {
            return Player.this;
        }
    }

    public void setServiceListener(ServiceListener serviceListener)
    {
        this.serviceListener = serviceListener;
    }

    public void initialize(Data data, Queue queue)
    {
        this.data = data;
        this.queue = queue;

        timeUpdater = new Handler();
    }

    public void setSong(Song song)
    {
        this.path = song.getPath();
        this.title = song.getTitle();
        this.artist = song.getArtist();
    }

    public void toggleResumed(boolean resume)
    {
        resumed = resume;
    }

    public void resumeSong(Context context)
    {
        serviceListener.onStartAudio(data.currentSong(context), false, true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if(data.currentSongIsNotNull())
        {
            try
            {
                player.reset();
                player.setDataSource(path);
                player.prepare();
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                {
                    @Override
                    public void onCompletion(MediaPlayer mp)
                    {
                        onFinish();
                    }
                });

                if(resumed)
                {
                    long currentTime = data.currentTime();
                    if(currentTime != -1)
                        player.seekTo((int) currentTime);
                }
                else
                    play();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }


            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle(title)
                    .setContentText(artist)
                    .setSmallIcon(R.drawable.play_36dp)
                    .setOngoing(true);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
                builder.setPriority(Notification.PRIORITY_MAX);

            startForeground(1, builder.build());
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        player = new MediaPlayer();
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        if(player != null)
        {
            player.stop();
            player.release();
        }

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1);
        return super.onUnbind(intent);
    }

    public void updateCurrentSong(Song song, boolean fromUser)
    {
        if(fromUser)
            queue.newSong(song.getId());

        data.updateCurrentSong(song.getId());

        if(!data.currentSongIsNotNull())
            data.updateCurrentSongIsNotNull(true);
    }

    public void play()
    {
        if(data.currentSongIsNotNull())
        {
            boolean isPlaying = player.isPlaying();
            if(!isPlaying)
            {
                player.start();
                data.updateIsPlaying(true);
            }
            else
            {
                player.pause();
                data.updateIsPlaying(false);
            }

            toggleTimeUpdater(!isPlaying);
        }
    }

    public void stop()
    {
        player.pause();
        player.seekTo(0);
        serviceListener.onStopAudio();
        data.updateIsPlaying(false);
    }

    private Handler timeUpdater;

    private Runnable timeUpdaterRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            timeUpdate();
        }
    };

    private void timeUpdate()
    {
        serviceListener.onCurrentTimeUpdate(player.getCurrentPosition());
        timeUpdater.postDelayed(timeUpdaterRunnable, 100);
    }

    public void toggleTimeUpdater(boolean toggle)
    {
        if(toggle)
            timeUpdate();
        else
            timeUpdater.removeCallbacks(timeUpdaterRunnable);
    }

    public void next()
    {
        change(true, true);
    }

    public boolean previous()
    {
        if(player.getCurrentPosition() > 3000)
        {
            player.seekTo(0);
            timeUpdate();
            return false;
        }
        else
        {
            change(false, true);
            return true;
        }
    }

//    public void scrub(boolean forward)
//    {
//        int scrubAmount = (forward)? data.scrubAmount() : -data.scrubAmount();
//        int scrubTo = player.getCurrentPosition() + (scrubAmount * 1000);
//        if(scrubTo < 0)
//            scrubTo = 0;
//        else if(scrubTo >= player.getDuration())
//            scrubTo = player.getDuration();
//
//        player.seekTo(scrubTo);
//    }

    private void change(boolean next, boolean fromUser)
    {
        Song song = null;
        if (next)
        {
            if (fromUser)
                song = getSongByID(queue.update(next));
            else
            {
                if (data.repeatState() == Data.RepeatState.ONE)
                    song = data.currentSong(this);
                else
                {
                    if (data.repeatState() == Data.RepeatState.OFF)
                    {
                        long nextID = queue.update(next);
                        if (nextID != queue.getQueue().get(0))
                            song = getSongByID(nextID);
                        else
                        {
                            data.updateCurrentQueueIndex(queue.getQueue().size() - 1);
                            stop();
                        }
                    } else if (data.repeatState() == Data.RepeatState.ALL)
                        song = getSongByID(queue.update(next));
                }
            }
        } else
            song = getSongByID(queue.update(next));

        if (song != null)
            serviceListener.onStartAudio(song, false, false);

        timeUpdate();
    }

    private Song getSongByID(long id)
    {
        Song song = null;
        for(Song s : data.songs())
            if(s.getId() == id)
                song = s;
        return song;
    }

    private void onFinish()
    {
        change(true, false);
    }

    public MediaPlayer getPlayer()
    {
        return player;
    }
}