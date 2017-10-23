package com.music.app.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.music.app.R;
import com.music.app.adapters.viewholders.PlaylistsListViewHolder;
import com.music.app.fragments.FragmentManager;
import com.music.app.interfaces.ListItem;
import com.music.app.objects.Data;
import com.music.app.objects.Playlist;
import com.music.app.objects.Song;
import com.music.app.utils.Dialoger;
import com.music.app.utils.Menuer;
import com.music.app.utils.PlaylistManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class PlaylistsListAdapter extends RecyclerView.Adapter<PlaylistsListViewHolder> implements
        ListItem.PlaylistsListItemListener
{
    private ArrayList<Playlist> playlists;

    private Data data;
    private FragmentManager fragmentManager;

    //TODO: Sort Playlists
    public PlaylistsListAdapter(ArrayList<Playlist> playlists,
                                Data data,
                                FragmentManager fragmentManager)
    {
        this.playlists = playlists;
        this.data = data;
        this.fragmentManager = fragmentManager;
    }

    public PlaylistsListAdapter(Context context, Data data, FragmentManager fragmentManager)
    {
        this.data = data;
        this.fragmentManager = fragmentManager;
        loadPlaylists(context);
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
        holder.bind(playlists.get(position), data);
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

    @Override
    public void onOptions(final int index, final View view)
    {
        Menuer.createMenu(view.getContext(), view, R.menu.playlist_item_menu,
        new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem)
            {
                switch(menuItem.getItemId())
                {
                    case R.id.delete_playlist:
                        Playlist playlist = playlists.get(index);
                        Dialoger.getDialogBuilder(view.getContext())
                                .setTitle("Delete Playlist")
                                .setMessage("Are you sure you want to delete \"" +
                                            playlist.getName() + "\" from your playlists?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i)
                                    {
                                        PlaylistManager.deletePlaylist
                                                (view.getContext(), playlists.get(index).getID());
                                        playlists.remove(index);
                                        notifyItemRemoved(index);
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                        break;
                }
                return true;
            }
        });
    }

    //TODO: Load in background
    private void loadPlaylists(Context context)
    {
        playlists = new ArrayList<>();

        Map<Long, String> playlistMap = PlaylistManager.getPlaylists(context);
        for(Map.Entry<Long, String> map : playlistMap.entrySet())
        {
            List<Long> songIDs = new ArrayList<>();
            for(long id : PlaylistManager.getPlaylistMembers(context, map.getKey()))
            {
                Song song = Song.getSongByID(id, data.songs());
                if(song != null) songIDs.add(song.getID());
            }

            playlists.add(new Playlist(map.getKey(), map.getValue(), songIDs));
        }

        Collections.sort(playlists, new Comparator<Playlist>()
        {
            @Override
            public int compare(Playlist p1, Playlist p2)
            {
                return p1.getName().compareTo(p2.getName());
            }
        });
        notifyDataSetChanged();
    }

    public boolean addPlaylist(Context context, String playlistName)
    {
        List<String> existingPlaylists = new ArrayList<>();
        for(Playlist playlist : playlists)
            existingPlaylists.add(playlist.getName());

        boolean isExisting = existingPlaylists.contains(playlistName);
        if(!isExisting)
        {
            PlaylistManager.addPlaylist(context, playlistName, null);
            playlists.add(new Playlist(playlistName));

            Collections.sort(playlists, new Comparator<Playlist>()
            {
                @Override
                public int compare(Playlist p1, Playlist p2)
                {
                    return p1.getName().compareTo(p2.getName());
                }
            });

            notifyDataSetChanged();
        }

        return !isExisting;
    }

    public void clearPlaylists(Context context)
    {
        playlists.clear();
        notifyDataSetChanged();

        String playlistsPrefsName = "Playlists";
        SharedPreferences.Editor editor = context.getSharedPreferences
                (playlistsPrefsName, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }
}
