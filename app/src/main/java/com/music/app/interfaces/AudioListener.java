package com.music.app.interfaces;

public interface AudioListener
{
    public void onStartAudio();
    public void onStopAudio();
    public void onCurrentTimeUpdate(int time);
}
