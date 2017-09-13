package com.music.app.utils;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.HandlerThread;
import android.support.v4.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.music.app.R;
import com.music.app.interfaces.CurrentAlbumArtScannerListener;
import com.music.app.objects.Song;

import java.io.File;
import java.lang.ref.WeakReference;

public class CurrentAlbumArtScanner
{
    public CurrentAlbumArtScanner
            (
                final WeakReference<Activity> views,
                final CurrentAlbumArtScannerListener listener,
                final Song song
            )
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Drawable cover = ResourcesCompat.getDrawable(views.get().getResources(),
                                 R.drawable.album_art_placeholder, null);
                try
                {
//                data.updateCurrentAlbumArt
//                        (Drawable.createFromPath(data.getCurrentSongFromDB(MainActivity.this)
//                                .getCoverPath()));

                    if(views != null)
                        cover = Glide
                                .with(views.get())
                                .load(new File(song.getCoverPath()))
                                .into(700, 700).get();
                }
                catch (Exception e)
                {
                    cover = ResourcesCompat.getDrawable(views.get().getResources(),
                            R.drawable.album_art_placeholder, null);
                }

                listener.onScanComplete(cover);
            }
        }).start();
    }
}
