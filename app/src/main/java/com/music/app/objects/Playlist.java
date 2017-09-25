package com.music.app.objects;

import java.util.ArrayList;

public class Playlist
{
    private Song[] tempSongs;

    public void setTempSongs(Song[] tempSongs)
    {
        this.tempSongs = tempSongs;
    }

    public Song[] getTempSongs()
    {
        return tempSongs;
    }

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

    public Playlist(String name, ArrayList<Long> songIDs)
    {
        initialize();
        this.name = name;
        this.songIDs = songIDs;
    }

    private void initialize()
    {
    }

    public ArrayList<Long> getSongsIDs()
    {
        return songIDs;
    }

    public void rename(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void add(Long songID)
    {
        songIDs.add(songID);
    }

    public void remove(Song song)
    {
        songIDs.remove(songIDs.indexOf(song));
    }

    public void addMany(ArrayList<Long> songIDs)
    {
        this.songIDs.addAll(songIDs);
    }

    public void removeMany(ArrayList<Long> songIDs)
    {
        this.songIDs.removeAll(songIDs);
    }

    public void delete()
    {
        songIDs = new ArrayList<>();
    }
}
