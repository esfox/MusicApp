package com.music.app.utils.asynctasks;

import android.os.AsyncTask;

import com.music.app.interfaces.AudioScannerTaskListener;


public class MediaScanner extends AsyncTask<Void, Void, Void>
{
    private AudioScannerTaskListener listener;
//    private ProgressDialog progressDialog;

    public MediaScanner(AudioScannerTaskListener listener)
    {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        listener.firstLaunch();
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.setMessage("\tLoading Music...");
//        progressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        listener.scan();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        super.onPostExecute(aVoid);
        listener.scanComplete();
    }
}
