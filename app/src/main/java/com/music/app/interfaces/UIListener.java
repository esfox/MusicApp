package com.music.app.interfaces;

import com.music.app.objects.Player;

public interface UIListener
{
    public void updateUI(Player.Event event);
    public void updateTime(int time);
}
