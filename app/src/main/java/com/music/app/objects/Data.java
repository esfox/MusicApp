package com.music.app.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class Data
{
    private final SharedPreferences sharedPreferences;

    public Data(Context context)
    {
        sharedPreferences = context.getSharedPreferences("Data", Context.MODE_PRIVATE);
    }

    //TODO: STORE ALL THESE TO SHARED PREFS OR TO STORAGE

    public static ArrayList<Song> songs;
    public static Song currentSong;
    public static Drawable currentAlbumArt;

    //Convert to XML
    private final String currentSongQueueIndexKey = "CurrentSongQueueIndex";
    private final String isShuffledKey = "isShuffled";
    private final String repeatStateKey = "repeatState";
    private final String isPlayingKey = "isPlaying";
    private final String storedKey = "stored";

    @SuppressWarnings("WeakerAccess")
    public enum RepeatState
    {
        OFF, //0
        ALL, //1
        ONE  //2
    }

    public int currentSongQueueIndex()
    {
        return sharedPreferences.getInt(currentSongQueueIndexKey, 0);
    }

    public boolean isShuffled()
    {
        return sharedPreferences.getBoolean(isShuffledKey, false);
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

    public boolean isPlaying()
    {
        return sharedPreferences.getBoolean(isPlayingKey, false);
    }

    public boolean stored()
    {
        return sharedPreferences.getBoolean(storedKey, false);
    }

    public void updateCurrentSongQueueIndex(int index)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(currentSongQueueIndexKey, index);
        editor.apply();
    }

    public void updateIsShuffled(boolean isShuffled)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isShuffledKey, isShuffled);
        editor.apply();
    }

    public void updateIsPlaying(boolean isPlaying)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isPlayingKey, isPlaying);
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

    public void updateStored(boolean stored)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(storedKey, stored);
        editor.apply();
    }
}
