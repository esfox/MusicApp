package com.music.app.utils.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.music.app.database.DatabaseHelper;
import com.music.app.interfaces.AudioScannerTaskListener;

import java.lang.ref.WeakReference;

public class MediaQuery extends AsyncTask<Void, Void, Void>
{
    private AudioScannerTaskListener listener;
    private DatabaseHelper databaseHelper;

    public MediaQuery(WeakReference<Context> context, AudioScannerTaskListener listener)
    {
        this.listener = listener;
        databaseHelper = new DatabaseHelper(context.get());
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        listener.initializeSongs(databaseHelper.getSongs());
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        super.onPostExecute(aVoid);
        databaseHelper.close();         // fixes leaky database
        listener.scanComplete();
    }
}
