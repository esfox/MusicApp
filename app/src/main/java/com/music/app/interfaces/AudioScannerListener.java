package com.music.app.interfaces;

import com.music.app.objects.Song;

import java.util.ArrayList;

public interface AudioScannerListener
{
    void onScanComplete(ArrayList<Song> songs);
    void onUpdate();
}
