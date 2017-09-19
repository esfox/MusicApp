package com.music.app.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.HandlerThread;
import android.support.v4.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.music.app.R;
import com.music.app.interfaces.CurrentAlbumArtScannerListener;
import com.music.app.objects.Song;

import java.io.File;
import java.lang.ref.WeakReference;

class CurrentAlbumArtScanner extends AsyncTask<Object, Drawable, Drawable>
{
    private CurrentAlbumArtScannerListener listener;

    CurrentAlbumArtScanner(CurrentAlbumArtScannerListener listener)
    {
        this.listener = listener;
    }

    @Override
    protected Drawable doInBackground(Object... params)
    {
        Context context = ((Context) params[0]);
        Drawable cover = ResourcesCompat.getDrawable(context.getResources(),
                R.drawable.album_art_placeholder, null);
        try
        {
//                data.updateCurrentAlbumArt
//                        (Drawable.createFromPath(data.getCurrentSongFromDB(MainActivity.this)
//                                .getCoverPath()));

            if(context != null)
                cover = Glide
                        .with(context)
                        .load(new File(((Song) params[1]).getCoverPath()))
                        .into(700, 700).get();
        }
        catch (Exception e)
        {
            cover = ResourcesCompat.getDrawable(context.getResources(),
                    R.drawable.album_art_placeholder, null);
        }
        return cover;
    }

    @Override
    protected void onPostExecute(Drawable cover)
    {
        super.onPostExecute(cover);
        listener.onScanComplete(cover);
    }
}
