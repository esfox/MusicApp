package com.music.app.utils.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.music.app.R;
import com.music.app.interfaces.AudioScannerTaskListener;

import java.lang.ref.WeakReference;


public class MediaScanner extends AsyncTask<Void, Void, Void>
{
    private AudioScannerTaskListener listener;
    private ProgressDialog progressDialog;

    public MediaScanner
        (
            WeakReference<Context> context,
            AudioScannerTaskListener listener
        )
    {
        this.listener = listener;
        progressDialog = new ProgressDialog(context.get(), R.style.AppTheme_ProgressDialog);
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("\tLoading Music...");
        progressDialog.show();
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
        progressDialog.dismiss();
        listener.scanComplete();
    }
}
