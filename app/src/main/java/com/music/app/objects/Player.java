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
import com.music.app.fragments.PlayQueueFragment;
import com.music.app.utils.interfaces.ServiceCommunicator;

import java.io.IOException;

public class Player extends Service
{
    private IBinder binder = new ServiceBinder();
    private ServiceCommunicator serviceCommunicator;
    private MediaPlayer player;
    private Data data;

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

    public void setData(Data data)
    {
        this.data = data;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d("Service", "Called");
        if(Data.currentSong != null)
        {
            try
            {
                player.reset();
                player.setDataSource(Data.currentSong.getPath());
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
                    .setContentTitle(Data.currentSong.getTitle())
                    .setContentText(Data.currentSong.getArtist())
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
        {
            if(Data.currentSong != null)
            {
                PlayQueue.newSongPlayed(song);
                PlayQueueFragment.update();
            }

            if(Data.currentSongQueueIndex != -1)
                PlayQueue.updateCurrentSongIndex(!song.equals(Data.currentSong), true);
            else
                PlayQueue.newCurrentSongIndex(song);
        }

        Data.currentSong = song;
    }

    public void play()
    {
        if(Data.currentSong != null)
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
    }

    public boolean previous()
    {
        if(player.getCurrentPosition() > 3000)
        {
            updateCurrentSong(Data.currentSong, false);
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
                song = PlayQueue.getNextSong();
            else
            {
                if(Data.repeatState == Data.RepeatState.ONE)
                    song = Data.currentSong;
                else
                {
                    if(Data.repeatState == Data.RepeatState.OFF)
                    {
                        if(PlayQueue.queue.indexOf(Data.currentSong) < PlayQueue.queue.size() - 1)
                            song = PlayQueue.getNextSong();
                        else
                            stop();
                    }
                    else if(Data.repeatState == Data.RepeatState.ALL)
                        song = PlayQueue.getNextSong();
                }
            }
        }
        else
            song = PlayQueue.getPreviousSong();

        PlayQueue.updateCurrentSongIndex(true, next);
        PlayQueue.updateQueueStack(next);
//        PlayQueue.resetQueueStack();

        updateCurrentSong(song, false);
        serviceCommunicator.onStartAudio(song, false);
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