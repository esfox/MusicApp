package com.music.app.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;

import com.music.app.database.SongDatabaseHelper;

import java.util.ArrayList;

public class Data
{
    private final SharedPreferences sharedPreferences;

    public Data(Context context)
    {
        sharedPreferences = context.getSharedPreferences("Data",
                Context.MODE_PRIVATE);
    }

    private ArrayList<Song> songs;
    private Queue queue;
    private Drawable currentAlbumArt;

    public ArrayList<Song> songs() { return songs; }
    public void setSongs(ArrayList<Song> songs) { this.songs = songs; }
    public Queue queue() { return queue; }
    public void setQueue(Queue queue) { this.queue = queue; }
    public Drawable currentAlbumArt() { return currentAlbumArt; }
    public void updateCurrentAlbumArt(Drawable albumArt) { currentAlbumArt = albumArt; }

    private final String currentSongIDKey = "currentSongID";
    private final String currentSongIsNotNullKey = "currentSongIsNotNull";
    private final String currentTimeKey = "currentTimeKey";
    private final String currentQueueIndexKey = "currentSongQueueIndex";
    private final String isPlayingKey = "isPlaying";
    private final String isShuffledKey = "isShuffled";
    private final String repeatStateKey = "repeatState";
    private final String scrubAmountKey = "scrubAmount";
    private final String promptQueueKey = "queuePrompt";
    private final String queueIsSavedKey = "queueIsSaved";
    private final String storedKey = "stored";

    @SuppressWarnings("WeakerAccess")
    public enum RepeatState
    {
        OFF, //0
        ALL, //1
        ONE  //2
    }

    public Song currentSong(Context context)
    {
//        if(songs != null)
//        {
//            for (Song song : songs)
//                if (song.getUUID().equals(sharedPreferences
//                        .getString(currentSongIDKey, "")))
//                    s = song;
//        }
//        else s = null;
        return new SongDatabaseHelper(context)
                .getCurrentSong(sharedPreferences
                        .getLong(currentSongIDKey, -1));
    }

    public boolean currentSongIsNotNull()
    {
        return sharedPreferences.getBoolean(currentSongIsNotNullKey, false);
    }

    public long currentTime()
    {
        return sharedPreferences.getLong(currentTimeKey, -1);
    }

    public int currentQueueIndex()
    {
        return sharedPreferences.getInt(currentQueueIndexKey, -1);
    }

    public boolean isPlaying()
    {
        return sharedPreferences
                .getBoolean(isPlayingKey, false);
    }

    public boolean isShuffled()
    {
        return sharedPreferences
                .getBoolean(isShuffledKey, false);
    }

    public RepeatState repeatState()
    {
        int repeatStateNumber = sharedPreferences.getInt(repeatStateKey, 0);
        if(repeatStateNumber == 1)
            return RepeatState.ALL;
        else if(repeatStateNumber == 2)
            return RepeatState.ONE;
        else
            return RepeatState.OFF;
    }

    public int scrubAmount()
    {
        return sharedPreferences.getInt(scrubAmountKey, 5000);
    }

    public boolean queuePrompt() { return sharedPreferences.getBoolean(promptQueueKey, true); }

    public boolean queueIsSaved()
    {
        return sharedPreferences
                .getBoolean(queueIsSavedKey, false);
    }

    public boolean stored()
    {
        return sharedPreferences
                .getBoolean(storedKey, false);
    }

    public void updateCurrentSong(long id)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(currentSongIDKey, id);
        editor.apply();
    }

    public void updateCurrentSongIsNotNull(boolean currentSongIsNotNull)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(currentSongIsNotNullKey, currentSongIsNotNull);
        editor.apply();
    }

    public void updateCurrentTime(long currentTime)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(currentTimeKey, currentTime);
        editor.apply();
    }

    public void updateCurrentQueueIndex(int index)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(currentQueueIndexKey, index);
        editor.apply();
    }

    public void updateIsPlaying(boolean isPlaying)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isPlayingKey, isPlaying);
        editor.apply();
    }

    public void updateIsShuffled(boolean isShuffled)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isShuffledKey, isShuffled);
        editor.apply();
    }

    public void updateRepeatState()
    {
        if(repeatState() == RepeatState.OFF)
            saveRepeatState(RepeatState.ALL);
        else if(repeatState() == RepeatState.ALL)
            saveRepeatState(RepeatState.ONE);
        else if(repeatState() == RepeatState.ONE)
            saveRepeatState(RepeatState.OFF);
    }

    private void saveRepeatState(RepeatState repeatState)
    {
        int repeatStateNumber = 0;
        if(repeatState == RepeatState.OFF)
            repeatStateNumber = 0;
        else if(repeatState == RepeatState.ALL)
            repeatStateNumber = 1;
        else if(repeatState == RepeatState.ONE)
            repeatStateNumber = 2;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(repeatStateKey, repeatStateNumber);
        editor.apply();
    }

    public void updateScrubAmount(int amount)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(scrubAmountKey, amount);
        editor.apply();
    }

    public void updateQueuePrompt(boolean prompt)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(promptQueueKey, prompt);
        editor.apply();
    }

    public void updateQueueIsSaved(boolean queueIsSaved)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(queueIsSavedKey, queueIsSaved);
        editor.apply();
    }

    public void updateStored(boolean stored)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(storedKey, stored);
        editor.apply();
    }
}
