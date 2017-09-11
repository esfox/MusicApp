package com.music.app.interfaces;

public interface UIUpdatesListener
{
    void onStartAudio();
    void onPlayOrPause();
    void onStopAudio();
    void onCurrentTimeUpdate(int time);
    void onShuffle();
    void onRepeat();
}
