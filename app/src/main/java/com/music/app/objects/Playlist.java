package com.music.app.objects;

import java.util.ArrayList;

public class Playlist
{
    private String name = "Untitled Playlist";
    private ArrayList<Song> songs = new ArrayList<>();

    public Playlist() {}

    public ArrayList<Song> getSongs()
    {
        return songs;
    }

    public void rename(String pName)
    {
        name = pName;
    }

    public String getName()
    {
        return name;
    }

    public void add(Song song)
    {
        songs.add(song);
    }

    public void remove(Song song)
    {
        songs.remove(songs.indexOf(song));
    }

    public void addMany(ArrayList<Song> pSongs)
    {
        songs.addAll(pSongs);
    }

    public void removeMany(ArrayList<Song> pSongs)
    {
        songs.removeAll(pSongs);
    }

    public void delete()
    {
        songs = new ArrayList<>();
    }
}
