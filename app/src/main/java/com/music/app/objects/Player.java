package com.music.app.objects;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.RemoteViews;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.music.app.R;
import com.music.app.interfaces.AudioListener;
import com.music.app.interfaces.RemoteControlReceiverListener;
import com.music.app.utils.RemoteControlReceiver;
import com.music.app.utils.TimeUpdater;

import java.io.IOException;
import java.util.ArrayList;

public class Player extends Service implements RemoteControlReceiverListener
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
    private TimeUpdater timeUpdater;
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

        if (data.currentSongIsNotNull())
            data.setCurrentSong(data.getCurrentSongFromDB(this));
    }

    public void setAudioListener(AudioListener audioListener)
    {
        this.audioListener = audioListener;
        timeUpdater = new TimeUpdater(audioListener, player);
    }

    public void startSong(Song song, boolean fromUser)
    {
        if (!data.currentSongIsNotNull())
            data.updateCurrentSongIsNotNull(true);

        resumed = false;
        currentSong = song;
//        if(stopService(intent))
        startService(intent);
        data.setCurrentSong(song);
        if (fromUser) queue.newSong(song.getID());

        audioListener.updateUI(Event.onStartAudio);
        timeUpdater.restart();

        queue.save(this);
    }

    public void startSongFromPlaylist(Song song, Playlist playlist)
    {
        if (!data.currentSongIsNotNull())
            data.updateCurrentSongIsNotNull(true);

        resumed = false;
        currentSong = song;
//        if(stopService(intent))
        startService(intent);
        data.setCurrentSong(song);
        queue.newSongFromPlaylist(song.getID(), playlist);

        audioListener.updateUI(Event.onStartAudio);
        timeUpdater.restart();

        queue.save(this);
    }

    public void resumeSong()
    {
        resumed = true;
        currentSong = data.currentSong();
        startService(intent);
        data.setCurrentSong(currentSong);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (data.currentSongIsNotNull())
        {
            try
            {
                player.reset();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                startMediaSession();

                player.setDataSource(currentSong.getPath());
                player.prepare();
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                {
                    @Override
                    public void onCompletion(MediaPlayer mp)
                    {
                        onFinish();
                    }
                });

                if (resumed)
                {
                    long currentTime = data.currentTime();
                    if (currentTime != -1)
                        player.seekTo((int) currentTime);
                } else
                    start(false);

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            showNotification();
        }

        return START_NOT_STICKY;
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
        if (player != null)
        {
            player.stop();
            player.release();
        }

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1);
        return super.onUnbind(intent);
    }

    private void startMediaSession()
    {
        Intent openAudioEffect = new Intent
                (AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION);
        openAudioEffect.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
        openAudioEffect.putExtra
                (AudioEffect.EXTRA_AUDIO_SESSION, player.getAudioSessionId());
        sendBroadcast(openAudioEffect);

        ComponentName mediaButtonReceiver = new ComponentName(this, RemoteControlReceiver.class);
        MediaSessionCompat mediaSession = new MediaSessionCompat
                (this, "MediaSession", mediaButtonReceiver, null);

        mediaSession.setFlags
                (
                    MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
                );
        mediaSession.setActive(true);

        RemoteControlReceiver.setRemoteControlReceiverListener(this);
    }

    //TODO: Expanded RemoteView with cancel button
    public void showNotification()
    {
        int playPauseIcon = player.isPlaying() ? R.drawable.pause_24dp : R.drawable.play_24dp;

        RemoteViews notificationControls = new RemoteViews
                (getPackageName(), R.layout.notification_controls);

        notificationControls.setOnClickPendingIntent
                (R.id.notification_controls_play,
                        MediaButtonReceiver.buildMediaButtonPendingIntent
                        (this, PlaybackStateCompat.ACTION_PLAY_PAUSE));

        notificationControls.setOnClickPendingIntent
                (R.id.notification_controls_previous,
                        MediaButtonReceiver.buildMediaButtonPendingIntent
                                (this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));

        notificationControls.setOnClickPendingIntent
                (R.id.notification_controls_next,
                        MediaButtonReceiver.buildMediaButtonPendingIntent
                                (this, PlaybackStateCompat.ACTION_SKIP_TO_NEXT));

        notificationControls.setImageViewResource(R.id.notification_controls_play, playPauseIcon);
        notificationControls.setTextViewText
                (R.id.notification_controls_title, currentSong.getTitle());
        notificationControls.setTextViewText
                (R.id.notification_controls_artist, currentSong.getArtist());

        Drawable coverDrawable = (currentSong.getCover() != null)?
                currentSong.getCover() :
                data.currentAlbumArt();

        if(coverDrawable != null)
        {
            Bitmap cover = null;
            try { cover = ((GlideBitmapDrawable) coverDrawable).getBitmap(); }
            catch(ClassCastException e) { cover = ((BitmapDrawable) coverDrawable).getBitmap(); }
            notificationControls.setImageViewBitmap(R.id.notification_controls_cover, cover);
        }


        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setOngoing(true)
                .setCustomBigContentView(notificationControls)
                .setSmallIcon(R.drawable.play_36dp);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
            builder.setPriority(Notification.PRIORITY_MAX);

        startForeground(1, builder.build());
    }

    @Override
    public void onPlayPause()
    {
        play();
    }

    @Override
    public void onNext()
    {
        next();
    }

    @Override
    public void onPrevious()
    {
        previous();
    }

    public void play()
    {
        start(true);
        showNotification();
    }

    public void start(boolean fromUser)
    {
        if (data.currentSongIsNotNull())
        {
            boolean isPlaying = player.isPlaying();
            if (!isPlaying) player.start();
            else player.pause();

            data.updateIsPlaying(!isPlaying);
            data.updateCurrentTime(player.getCurrentPosition());

            audioListener.updateUI(Event.onPlayOrPause);

            if (fromUser)
                toggleTimeUpdater(!isPlaying);
        }
    }

    public void stop()
    {
        player.pause();
        player.seekTo(0);
        timeUpdater.pause();
        data.updateIsPlaying(false);
        audioListener.updateUI(Event.onStopAudio);
    }

    public void next()
    {
        change(true, true);
    }

    public boolean previous()
    {
        if (player.getCurrentPosition() > 3000)
        {
            player.seekTo(0);
            return false;
        } else
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
        ArrayList<Song> songs = data.songs();
        Song song = null;
        if (next)
        {
            if (fromUser)
                song = Song.getSongByID(queue.update(next), songs);
            else
            {
                if (data.repeatState() == Data.RepeatState.ONE)
                    song = currentSong;    //song = data.getCurrentSongFromDB(this);
                else
                {
                    if (data.repeatState() == Data.RepeatState.OFF)
                    {
                        long nextID = queue.update(next);
                        if (nextID != queue.getQueueList().get(0))
                            song = Song.getSongByID(nextID, songs);
                        else
                        {
                            data.updateCurrentQueueIndex(queue.getQueueList().size() - 1);
                            stop();
                        }
                    } else if (data.repeatState() == Data.RepeatState.ALL)
                        song = Song.getSongByID(queue.update(next), songs);
                }
            }
        } else
            song = Song.getSongByID(queue.update(next), songs);

        if (song != null)
            startSong(song, false);
    }

    public Queue queue()
    {
        return queue;
    }

    public void shuffle()
    {
        queue.shuffle(data.currentSong(), !data.isShuffled());
        audioListener.updateUI(Event.onShuffle);
//        queue.shuffle(getCurrentSongFromDB);
    }

    public void repeat()
    {
        data.updateRepeatState();
        audioListener.updateUI(Event.onRepeat);
    }

    public void toggleTimeUpdater(boolean toggle)
    {
        if (toggle)
            timeUpdater.resume();
        else
            timeUpdater.pause();
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