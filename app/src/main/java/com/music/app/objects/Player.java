package com.music.app.objects;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.music.app.R;
import com.music.app.fragments.PlayQueueFragment;
import com.music.app.utils.UIManager;

import java.io.IOException;

public class Player extends Service
{
    public static MediaPlayer player = new MediaPlayer();

    private static Player instance;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
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
    public IBinder onBind(Intent intent) { return null; }

    public static void playSong(Song song, boolean fromUser)
    {
        if(fromUser)
        {
            if(Data.currentSong != null)
                PlayQueue.newSongPlayed(song);

            if(Data.currentSongQueueIndex != -1)
                PlayQueue.updateCurrentSongIndex(!song.equals(Data.currentSong), true);
            else
                PlayQueue.newCurrentSongIndex(song);
        }

        Data.currentSong = song;
        Player.instance.startService(new Intent(Player.instance, Player.class));

        UIManager.instance.togglePlayButtonIcon(true);
        UIManager.instance.updateNowPlayingBar();
        PlayQueueFragment.update();
    }

	public static void play()
	{
        if(Data.currentSong != null)
            if(!player.isPlaying())
            {
                player.start();
                Data.isPlaying = true;
            }
            else
            {
                player.pause();
                Data.isPlaying = false;
            }
	}

    public static void stop()
    {
        player.pause();
        player.seekTo(0);
    }

    public static boolean previous()
    {
        if(player.getCurrentPosition() > 3000)
        {
            playSong(Data.currentSong, false);
            return false;
        }
        else
        {
            changeSong(false, true);
            return true;
        }
    }

    public static void next()
    {
        changeSong(true, true);
    }

    private static void changeSong(boolean next, boolean fromUser)
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

        playSong(song, false);

        UIManager.instance.updateNowPlayingFragment();
        UIManager.instance.updatePlayQueueFragment();
        UIManager.instance.updateNowPlayingBar();
    }

    private void onFinish()
    {
        changeSong(true, false);
    }
}