package com.music.app.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;


public class PlaylistAdapter extends BaseAdapter
{
    private ArrayList<Long> songIDs;

    public PlaylistAdapter(ArrayList<Long> songIDSs)
    {
        this.songIDs = songIDSs;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {


        return null;
    }

    @Override
    public int getCount()
    {
        return 0;
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }
}
