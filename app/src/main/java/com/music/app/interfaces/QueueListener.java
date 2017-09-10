package com.music.app.interfaces;

public interface QueueListener
{
    public void onQueue(long id);
    public void onPlayNext(long id);
    public void onShuffle();
    public void onRepeat();
}
