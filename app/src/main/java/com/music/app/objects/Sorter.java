package com.music.app.objects;

import android.util.Log;

import com.music.app.objects.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Sorter
{
    public enum SortBy
    {
        title,
        artist,
        album,
        none
    }

    public static ArrayList<Song> sort(ArrayList<Song> songs, final SortBy sort)
    {
        if(sort != SortBy.none)
        {
            try
            {
                Collections.sort(songs, new Comparator<Song>()
                {
                    @Override
                    public int compare(Song lhs, Song rhs)
                    {
                        if(sort == SortBy.artist)
                            return lhs.getArtist().compareTo(rhs.getArtist());
                        else if(sort == SortBy.album)
                            return lhs.getAlbum().compareTo(rhs.getAlbum());
                        else
                            return lhs.getTitle().compareTo(rhs.getTitle());
                    }
                });
            }
            catch (NullPointerException e)
            {
                e.printStackTrace();
            }
        }

        return songs;
    }

//    private void showTestSort()
//    {
//        for(Song song: queue)
//        {
//            Log.d("APP", " ");
//            Log.d("APP", "Title: " + song.getTitle());
//            Log.d("APP", "Artist: " + song.getArtist());
//            Log.d("APP", "Album: " + song.getAlbum());
//        }
//    }
}
