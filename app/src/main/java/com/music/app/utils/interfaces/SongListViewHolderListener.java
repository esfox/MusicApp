package com.music.app.utils.interfaces;

import com.music.app.views.SongListViewHolder;

public interface SongListViewHolderListener
{
    public void onClick(int index, SongListViewHolder viewHolder);
    public void onQueue(int index);
    public void onPlayNext(int index);
    public void onOptions(int index, boolean isOpened, SongListViewHolder viewHolder);
    public void onMoreOptions(int index, SongListViewHolder viewHolder);
}