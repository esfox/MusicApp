package com.music.app.interfaces;

import com.music.app.utils.adapters.viewholders.SongListViewHolder;

public interface SongListListener
{
    public void onPlay(int index, SongListViewHolder viewHolder);
    public void onQueue(int index);
    public void onPlayNext(int index);
    public void onOptions(int index, boolean isOpened, SongListViewHolder viewHolder);
    public void onMoreOptions(int index, SongListViewHolder viewHolder);
}