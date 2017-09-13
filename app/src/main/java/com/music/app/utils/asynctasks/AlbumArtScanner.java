package com.music.app.utils.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.music.app.interfaces.AudioScannerTaskListener;
import com.music.app.views.Notice;

import java.lang.ref.WeakReference;

public class AlbumArtScanner extends AsyncTask<Void, Void, Void>
{
    private AudioScannerTaskListener listener;
    private Notice notice;

    public AlbumArtScanner(WeakReference<Context> context, AudioScannerTaskListener listener)
    {
        this.listener = listener;

        notice = new Notice(context.get());
        notice.setNoticeText("Loading album covers...");
        notice.setNotClickable();
        notice.doNotDismiss();
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        notice.show();
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        listener.scanCovers();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        super.onPostExecute(aVoid);
        notice.dismiss();
        listener.updateAudioScannerListener();
        listener.startMediaStorer();
    }
}
