package com.music.app.utils;

import android.content.Context;
import android.view.MenuInflater;
import android.view.View;
import android.widget.PopupMenu;

public class Menuer
{
    public static void createMenu(Context context, View view, int menuResource, PopupMenu.OnMenuItemClickListener listener)
    {
        PopupMenu menu = new PopupMenu(context, view);
        MenuInflater inflater = menu.getMenuInflater();
        inflater.inflate(menuResource, menu.getMenu());
        menu.setOnMenuItemClickListener(listener);

        menu.show();
    }
}
