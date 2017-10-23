package com.music.app.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import com.music.app.interfaces.RemoteControlReceiverListener;

public class RemoteControlReceiver extends BroadcastReceiver
{
    private static RemoteControlReceiverListener listener;

    public RemoteControlReceiver() {}

    public static void setRemoteControlReceiverListener(RemoteControlReceiverListener listener)
    {
        RemoteControlReceiver.listener = listener;
    }

    private static long currentClick = System.currentTimeMillis();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON))
        {
            Log.d("time", String.valueOf(System.currentTimeMillis()));

            KeyEvent mediaAction = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

            if(mediaAction.getAction() == KeyEvent.ACTION_DOWN)
            {
                switch(mediaAction.getKeyCode())
                {
                    case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                        listener.onPlayPause();
                        break;

                    case KeyEvent.KEYCODE_MEDIA_NEXT:
                        listener.onNext();
                        break;

                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                        listener.onPrevious();
                        break;

                    case KeyEvent.KEYCODE_HEADSETHOOK:
                        long firstClick = currentClick;
                        currentClick = System.currentTimeMillis();

                        if(currentClick - firstClick < 500)
                            listener.onNext();
                        else
                            listener.onPlayPause();

                        break;
                }
            }
        }
    }
}
