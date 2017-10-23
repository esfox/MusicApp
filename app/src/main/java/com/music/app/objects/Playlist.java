package com.music.app.objects;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class Playlist
{
    public void setTempSongs(long[] ids)
    {
        arrayToList(ids);
    }

    private long id;
    private String name = "Untitled Playlist";
    private ArrayList<Long> songIDs;

    public Playlist()
    {
        initialize();
    }

    public Playlist(String name)
    {
        initialize();
        this.name = name;
    }

    public Playlist(long id, String name)
    {
        initialize();
        this.id = id;
        this.name = name;
    }

    public Playlist(long id, String name, List<Long> songIDs)
    {
        initialize();
        this.id = id;
        this.name = name;
        this.songIDs.addAll(songIDs);
    }

    private void initialize()
    {
        songIDs = new ArrayList<>();
    }

    public long[] getSongs()
    {
        if(!songIDs.isEmpty())
        {
            long[] ids = new long[songIDs.size()];
            for (int i = 0; i < ids.length; i++)
                ids[i] = songIDs.get(i);

            return ids;
        }
        else return new long[0];
    }

    public long getID()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public void rename(String name)
    {
        this.name = name;
    }

    public void add(long songID, Context context)
    {
        songIDs.add(songID);
//        save(context);
    }

    public void remove(Song song)
    {
        songIDs.remove(songIDs.indexOf(song.getID()));
    }

    public void delete()
    {
        songIDs = new ArrayList<>();
    }

    private void arrayToList(long[] ids)
    {
        for(long id : ids) songIDs.add(id);
    }

    public static final String playlistTrackCountKey = "trackCount";

//    private void save(Context context)
//    {
//        SharedPreferences.Editor editor = context.getSharedPreferences
//                (name, Context.MODE_PRIVATE).edit();
//
//        for(int i = 0; i < songIDs.size(); i++)
//        {
//            Log.d("id", String.valueOf(songIDs.get(i)));
//            editor.putLong(String.valueOf(i), songIDs.get(i));
//        }
//
//        editor.putInt(playlistTrackCountKey, songIDs.size());
//        editor.apply();
//    }
}
