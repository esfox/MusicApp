package com.music.app.interfaces;

import com.music.app.objects.Song;

import java.util.ArrayList;

public interface AudioScanListener
{
    public void onScanComplete(ArrayList<Song> songs);
    public void onUpdate();
}
