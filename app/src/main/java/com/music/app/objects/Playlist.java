package com.music.app.objects;

import java.util.ArrayList;

public class Playlist
{
    private String[] tempCovers;
    private int tempTracksCount;

    public void setTempCovers(String[] covers)
    {
        tempCovers = covers;
    }

    public String[] getTempCovers()
    {
        return tempCovers;
    }

    public void setTempTracksCount(int count)
    {
        tempTracksCount = count;
    }

    public int getTempTracksCount()
    {
        return tempTracksCount;
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
