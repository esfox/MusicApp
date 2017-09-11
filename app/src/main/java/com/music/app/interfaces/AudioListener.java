package com.music.app.interfaces;

import com.music.app.objects.Player;

public interface AudioListener
{
    void updateUI(Player.Event event);
    void updateTime(int time);
}
