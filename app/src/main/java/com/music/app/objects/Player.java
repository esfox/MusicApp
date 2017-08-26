package com.music.app.objects;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.music.app.R;
import com.music.app.utils.interfaces.ServiceCommunicator;

import java.io.IOException;

public class Player extends Service
{
    private IBinder binder = new ServiceBinder();
    private ServiceCommunicator serviceCommunicator;
    private MediaPlayer player;
    private Data data;
    private Queue queue;
    private String path, title, artist;

    public class ServiceBinder extends Binder
    {
        public Player getService()
        {
            return Player.this;
        }
    }

    public void setServiceCommunicator(ServiceCommunicator serviceCommunicator)
    {
        this.serviceCommunicator = serviceCommunicator;
    }

    public void initialize(Data data, Queue queue)
    {
        this.data = data;
        this.queue = queue;
    }

    public void setSong(Song song)
    {
        this.path = song.getPath();
        this.title = song.getTitle();
        this.artist = song.getArtist();
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
                play();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

//            Intent notifIntent = new Intent(Data.getInstance().getContext(), MainActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifIntent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle(title)
                    .setContentText(artist)
                    .setSmallIcon(R.drawable.play_36dp)
                    //                    .setContentIntent(pendingIntent)
                    .setOngoing(true);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
                builder.setPriority(Notification.PRIORITY_HIGH);

            startForeground(1, builder.build());
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(player != null)
        {
            player.stop();
            player.release();
        }

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        player = new MediaPlayer();
        return binder;
    }

    public void updateCurrentSong(Song song, boolean fromUser)
    {
        if(fromUser)
            queue.newSong(song.getId());
//            PlayQueue.newSongPlayed(song);

        Data.currentSong = song;
        data.updateCurrentSong(song.getId());

        if(!data.currentSongIsNotNull())
            data.updateCurrentSongIsNotNull(true);
    }

    public void play()
    {
        if(data.currentSongIsNotNull())
            if(!player.isPlaying())
            {
                player.start();
                data.updateIsPlaying(true);
            }
            else
            {
                player.pause();
                data.updateIsPlaying(false);
            }
    }

    public void stop()
    {
        player.pause();
        player.seekTo(0);
        serviceCommunicator.onStopAudio();
        data.updateIsPlaying(false);
    }

    public boolean previous()
    {
        if(player.getCurrentPosition() > 3000)
        {
            player.seekTo(0);
            return false;
        }
        else
        {
            change(false, true);
            return true;
        }
    }

    public void next()
    {
        change(true, true);
    }

    private void change(boolean next, boolean fromUser)
    {
        Song song = null;
        if(next)
        {
            if(fromUser)
                song = getSongByID(queue.update(next));
//                song = PlayQueue.getNextSong();
            else
            {
                if(data.repeatState() == Data.RepeatState.ONE)
                    song = data.currentSong(this);
                else
                {
                    if(data.repeatState() == Data.RepeatState.OFF)
                    {
                        if(queue.update(next) != queue.getQueue().get(0))
                            song = getSongByID(queue.update(next));
//                            song = PlayQueue.getNextSong();
                        else
                            stop();
                    }
                    else if(data.repeatState() == Data.RepeatState.ALL)
                        song = getSongByID(queue.update(next));
//                        song = PlayQueue.getNextSong();
                }
            }
        }
        else
            song = getSongByID(queue.update(next));
//            song = PlayQueue.getPreviousSong();

//        PlayQueue.updateCurrentSongIndex(true, next);
//        PlayQueue.updateQueueStack(next);
        if(song != null)
            serviceCommunicator.onStartAudio(song, false);
    }

    private Song getSongByID(long id)
    {
        Song song = null;
        for(Song s : Data.songs)
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