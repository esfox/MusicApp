package com.music.app.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.music.app.R;
import com.music.app.adapters.viewholders.PlaylistsListViewHolder;
import com.music.app.fragments.FragmentManager;
import com.music.app.interfaces.ListItem;
import com.music.app.objects.Playlist;

import java.util.ArrayList;


public class PlaylistsListAdapter extends RecyclerView.Adapter<PlaylistsListViewHolder> implements
        ListItem.PlaylistsListItemListener
{
    private ArrayList<Playlist> playlists;

    private FragmentManager fragmentManager;

    public PlaylistsListAdapter(ArrayList<Playlist> playlists, FragmentManager fragmentManager)
    {
        this.playlists = playlists;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public PlaylistsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new PlaylistsListViewHolder
                (LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.playlists_list_item, parent, false), this);
    }

    @Override
    public void onBindViewHolder(PlaylistsListViewHolder holder, int position)
    {
        holder.bind(playlists.get(position));
    }

    @Override
    public int getItemCount()
    {
        return playlists.size();
    }

    @Override
    public void onGotoPlaylist(int index)
    {
        fragmentManager.playlist(playlists.get(index));
    }
}
