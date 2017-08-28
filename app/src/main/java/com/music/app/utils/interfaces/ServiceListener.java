package com.music.app.utils.interfaces;

import com.music.app.objects.Song;

public interface ServiceListener
{
    public void onStartAudio(Song song, boolean fromUser);
    public void onStopAudio();
}