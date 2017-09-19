package com.music.app.interfaces;

import com.music.app.objects.Song;

import java.util.ArrayList;

public interface AudioScannerTaskListener
{
    void firstLaunch();
    void scan();
    void scanComplete();
    void scanCovers();
    void initializeSongs(ArrayList<Song> songs);
    void updateAudioScannerListener();
    void startMediaStorer();
    void storeMedia();
    void finishedStoring();
}
