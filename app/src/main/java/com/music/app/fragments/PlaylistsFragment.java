package com.music.app.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.music.app.R;
import com.music.app.adapters.PlaylistsAdapter;
import com.music.app.objects.Data;
import com.music.app.objects.Playlist;

import java.util.ArrayList;
import java.util.Random;

public class PlaylistsFragment extends Fragment
{
    private Data data;

    public PlaylistsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_playlist, container, false);

        RecyclerView playlists = (RecyclerView) v.findViewById(R.id.playlists_list);
        playlists.setHasFixedSize(true);
        playlists.setAdapter(new PlaylistsAdapter(temp()));
        playlists.setLayoutManager(new LinearLayoutManager(getContext()));
        return v;
    }

    private ArrayList<Playlist> temp()
    {
        ArrayList<Playlist> temp = new ArrayList<>();
        for (int i = 0; i < 20; i++)
        {
            String[] covers = new String[5];
            for (int j = 0; j < covers.length; j++)
                covers[j] = data.songs().get(new Random().nextInt(100)).getCoverPath();

            Playlist playlist = new Playlist("Playlist " + String.valueOf(i+1));
            playlist.setTempCovers(covers);
            playlist.setTempTracksCount(new Random().nextInt(20) + 1);
            temp.add(playlist);
        }

        return temp;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    public void initialize(Data data)
    {
        this.data = data;
    }
}
