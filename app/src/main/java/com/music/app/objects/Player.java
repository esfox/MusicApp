package com.music.app.objects;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.music.app.R;
import com.music.app.interfaces.AudioListener;

import java.io.IOException;

public class Player extends Service
{
    private Intent intent;
    private IBinder binder = new ServiceBinder();

    private AudioListener audioListener;

    public enum Event
    {
        onStartAudio,
        onPlayOrPause,
        onStopAudio,
        onShuffle,
        onRepeat
    }

    private MediaPlayer player;
    private Data data;
    private Queue queue;

    private Song currentSong;

    private boolean resumed;

    public class ServiceBinder extends Binder
    {
        public Player getService()
        {
            return Player.this;
        }
    }

    public void initialize(Intent intent, Data data, Queue queue)
    {
        this.intent = intent;
        this.data = data;
        this.queue = queue;

        timeUpdater = new Handler();
    }

    public void setAudioListener(AudioListener audioListener)
    {
        this.audioListener = audioListener;
    }

    public void startSong(Song song, boolean fromUser)
    {
        resumed = false;
        currentSong = song;
        startService(intent);
        updateCurrentSong(song, fromUser);

        audioListener.updateUI(Event.onStartAudio);
        timeUpdate();
    }

    private void updateCurrentSong(Song song, boolean fromUser)
    {
        if(fromUser)
            queue.newSong(song.getId());

            data.updateCurrentSong(song.getId());

        if(!data.currentSongIsNotNull())
            data.updateCurrentSongIsNotNull(true);
    }

    public void resumeSong()
    {
        resumed = true;
        currentSong = data.currentSong(this);
        startService(intent);
        updateCurrentSong(currentSong, false);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if(data.currentSongIsNotNull())
        {
            try
            {
                player.reset();
                player.setDataSource(currentSong.getPath());
                player.prepare();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
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

            final NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle(currentSong.getTitle())
                    .setContentText(currentSong.getArtist())
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

    public void play()
    {
        if(data.currentSongIsNotNull())
        {
            boolean isPlaying = player.isPlaying();
            if(!isPlaying) player.start();
            else player.pause();

            data.updateIsPlaying(!isPlaying);
            data.updateCurrentTime(player.getCurrentPosition());

            audioListener.updateUI(Event.onPlayOrPause);
            toggleTimeUpdater(!isPlaying);
        }
    }

    public void stop()
    {
        player.pause();
        player.seekTo(0);
        audioListener.updateUI(Event.onStopAudio);
        data.updateIsPlaying(false);
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
                    song = currentSong;    //song = data.currentSong(this);
                else
                {
                    if (data.repeatState() == Data.RepeatState.OFF)
                    {
                        long nextID = queue.update(next);
                        if (nextID != queue.getQueueList().get(0))
                            song = getSongByID(nextID);
                        else
                        {
                            data.updateCurrentQueueIndex(queue.getQueueList().size() - 1);
                            stop();
                        }
                    } else if (data.repeatState() == Data.RepeatState.ALL)
                        song = getSongByID(queue.update(next));
                }
            }
        } else
            song = getSongByID(queue.update(next));

        if (song != null)
            startSong(song, false);
    }

    public Queue queue()
    {
        return queue;
    }

    public void shuffle()
    {
        queue.shuffle(data.currentSong(this));
        audioListener.updateUI(Event.onShuffle);
//        queue.shuffle(currentSong);
    }

    public void repeat()
    {
        data.updateRepeatState();
        audioListener.updateUI(Event.onRepeat);
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
        audioListener.updateTime(player.getCurrentPosition());
        timeUpdater.postDelayed(timeUpdaterRunnable, 100);
    }

    public void toggleTimeUpdater(boolean toggle)
    {
        if(toggle)
            timeUpdate();
        else
            timeUpdater.removeCallbacks(timeUpdaterRunnable);
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