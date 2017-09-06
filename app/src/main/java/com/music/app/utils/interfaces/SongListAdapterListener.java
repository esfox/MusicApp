package com.music.app.utils.interfaces;

public interface SongListAdapterListener
{
    public void onToggleToolbar(boolean toggle, boolean toggleMultiQueue, String toolbarText);
    public void onQueuePrompt(int index);
}
