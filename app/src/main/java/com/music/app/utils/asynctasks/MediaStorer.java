package com.music.app.utils.asynctasks;

import android.os.AsyncTask;

import com.music.app.interfaces.AudioScannerTaskListener;


public class MediaStorer extends AsyncTask<Void, Void, Void>
{
    private AudioScannerTaskListener listener;

    public MediaStorer(AudioScannerTaskListener listener)
    {
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        listener.storeMedia();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        super.onPostExecute(aVoid);
        listener.finishedStoring();

    }
}
