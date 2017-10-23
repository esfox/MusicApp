package com.music.app.interfaces;

import android.view.View;

import com.music.app.adapters.viewholders.SongListViewHolder;

public interface ListItem
{
    interface SongListItemListener
    {
        void onItemClick(int index, SongListViewHolder viewHolder);
        void onQueue(int index);
        void onPlayNext(int index);
        void onOptions(int index, boolean isOpened, SongListViewHolder viewHolder);
        void onMoreOptions(int index, SongListViewHolder viewHolder);
    }

    interface PlaylistsListItemListener
    {
        void onGotoPlaylist(int index);
        void onOptions(int index, View view);
    }

    interface PlaylistItemListener
    {
        void onPlay(int index);
    }
}
