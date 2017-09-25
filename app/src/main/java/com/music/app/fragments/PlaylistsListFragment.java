package com.music.app.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.music.app.R;
import com.music.app.adapters.PlaylistsListAdapter;
import com.music.app.objects.Data;
import com.music.app.objects.Playlist;
import com.music.app.objects.Song;

import java.util.ArrayList;

public class PlaylistsListFragment extends Fragment
{
    private Data data;
    private FragmentManager fragmentManager;

    public PlaylistsListFragment() {}

    public void initialize(Data data, FragmentManager fragmentManager)
    {
        this.data = data;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_playlists_list, container, false);

        RecyclerView playlists = (RecyclerView) v.findViewById(R.id.playlists_list);
        playlists.setHasFixedSize(true);
        playlists.setAdapter(new PlaylistsListAdapter(temp(), fragmentManager));
        playlists.setLayoutManager(new LinearLayoutManager(getContext()));
        return v;
    }

    private ArrayList<Playlist> temp()
    {
        Song[] tempSongs = new Song[80];
        for (int i = 0; i < tempSongs.length; i++)
            tempSongs[i] = data.songs().get(i);

        ArrayList<Playlist> temp = new ArrayList<>();
        for (int i = 0; i < tempSongs.length; i++)
        {
            Playlist playlist = new Playlist("Playlist " + String.valueOf(i+1));
            playlist.setTempSongs(tempSongs);
            temp.add(playlist);
        }

        return temp;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }
}
