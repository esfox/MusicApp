package com.music.app.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.music.app.R;
import com.music.app.adapters.viewholders.PlaylistsViewHolder;
import com.music.app.objects.Playlist;

import java.util.ArrayList;
import java.util.Random;


public class PlaylistsAdapter extends RecyclerView.Adapter<PlaylistsViewHolder>
{
    private ArrayList<Playlist> playlists;
    private int[] contents;

    public PlaylistsAdapter(ArrayList<Playlist> playlists)
    {
        this.playlists = playlists;

        contents = new int[playlists.size()];
        for (int i = 0; i < contents.length; i++)
        {
            Random random = new Random();
            contents[i] = random.nextInt(5);
        }
    }

    @Override
    public PlaylistsViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new PlaylistsViewHolder
                (LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.playlist_item, parent, false));
    }

    @Override
    public void onBindViewHolder(PlaylistsViewHolder holder, int position)
    {
        holder.bind(playlists.get(position), position, contents[position]);
    }

    @Override
    public int getItemCount()
    {
        return playlists.size();
    }
}
