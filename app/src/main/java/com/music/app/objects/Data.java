package com.music.app.objects;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class Data
{
    private Context context;
    private final SharedPreferences sharedPreferences;

    public Data(Context context)
    {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("Data", Context.MODE_PRIVATE);
    }

    public Context getContext()
    {
        return context;
    }

    //TODO: STORE ALL THESE TO SHARED PREFS OR TO STORAGE

    public static ArrayList<Song> songs;
    public static Song currentSong;
    public static int currentSongQueueIndex = -1;
    public static boolean isShuffled = false;
    public static boolean isPlaying = false;

    public enum RepeatState
    {
        OFF,
        ALL,
        ONE
    }

    public static RepeatState repeatState = RepeatState.OFF;

    public static void repeat()
    {
        if(repeatState == RepeatState.OFF)
            repeatState = RepeatState.ALL;
        else if(repeatState == RepeatState.ALL)
            repeatState = RepeatState.ONE;
        else if(repeatState == RepeatState.ONE)
            repeatState = RepeatState.OFF;
    }

    //Convert to XML
    private final String currentSongQueueIndexKey = "CurrentSongQueueIndex";
    private final String isShuffledKey = "isShuffled";
    private final String isPlayingKey = "isPlaying";

    public int currentSongQueueIndex()
    {
        return sharedPreferences.getInt(currentSongQueueIndexKey, 0);
    }

    public boolean isShuffled()
    {
        return sharedPreferences.getBoolean(isShuffledKey, false);
    }

    public boolean isPlaying()
    {
        return sharedPreferences.getBoolean(isPlayingKey, false);
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
}
