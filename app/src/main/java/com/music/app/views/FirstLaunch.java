package com.music.app.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.music.app.R;
import com.music.app.utils.AudioScanner;

public class FirstLaunch extends AppCompatActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_launch);
        AudioScanner.firstLaunch(this);
    }
}
