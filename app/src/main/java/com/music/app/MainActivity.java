package com.music.app;

import android.*;
import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.music.app.fragments.FragmentManager;
import com.music.app.interfaces.AudioScannerListener;
import com.music.app.objects.Data;
import com.music.app.objects.Player;
import com.music.app.objects.Queue;
import com.music.app.objects.Song;
import com.music.app.utils.AudioScanner;
import com.music.app.utils.UIManager;
import com.music.app.adapters.SongListAdapter;

import java.util.ArrayList;

/*

TODO CURRENT ACTIVITY

Playlists

TODO: Remember to do these
Load next and previous album cover in background
Scrolling TextView in Now Playing Fragment
Not shuffled on new song played
MediaSession, MediaBrowserCompat blabla

REMEMBER:
Timer onResume (When no playing song)

*/

public class MainActivity extends AppCompatActivity implements AudioScannerListener
{
    private Player player;
    private Queue queue;
    private Data data;

    private FragmentManager fragmentManager;

    private Intent serviceIntent;
    private ServiceConnection connection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            Player.ServiceBinder binder = (Player.ServiceBinder) service;
            player = binder.getService();
            player.initialize(serviceIntent, data, queue);

            UIManager uiManager = new UIManager(MainActivity.this);
            uiManager.initUI
                (
                    MainActivity.this,
                    data,
                    player
                );
            fragmentManager = uiManager.fragmentManager();

            player.setAudioListener(uiManager);

            if(data.currentSongIsNotNull())
                player.resumeSong();
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (grantResults[1] != PackageManager.PERMISSION_GRANTED && grantResults[0] != PackageManager.PERMISSION_GRANTED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 2562);
            }
        }
//        else
//        {
//            setContentView(R.layout.activity_main);
//
//            data = new Data(this);
//
//            serviceIntent = new Intent(this, Player.class);
//            bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
//
//            uiManager = new UIManager(this);
//            uiManager.initUI(data, player, this);
//            fragmentManager = uiManager.fragmentManager();
//
//            queue = new Queue(data);
//
//            //Scan audio
//            AudioScanner audioFileScanner = new AudioScanner(this, this, data);
//            audioFileScanner.scanAudio();
////        temp();
//
//            if(data.currentSongIsNotNull())
//                uiManager.updateNowPlayingBar(data.getCurrentSongFromDB(this));
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)  != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]
                    {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ?
                                    Manifest.permission.READ_EXTERNAL_STORAGE :
                                    "")
                    }, 2562);
        } else {
            // TODO: FIX NOTHING SHOWS UP
            data = new Data(this);
            queue = new Queue(data);

            serviceIntent = new Intent(this, Player.class);
            bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);

            //Scan audio
            new AudioScanner(this, this, data).scanAudio();
//        temp();
        }

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(player != null && data.currentSongIsNotNull())
        {
            if (player.getPlayer().isPlaying())
                player.toggleTimeUpdater(true);
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if(player != null)
            player.toggleTimeUpdater(false);
        if (queue != null)
            queue.save(this);
//        if (data != null && data.currentSongIsNotNull())
//        {

        if (data != null && data.currentSongIsNotNull())
            data.updateCurrentSong(data.currentSong().getId());

        data.updateCurrentTime(player.getPlayer().getCurrentPosition());
        data.updateCurrentSongIsNotNull(data.currentSongIsNotNull());
//        }

//        System.gc();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        stopService(serviceIntent);
        unbindService(connection);
    }

    //    private void temp()
    //    {
    //        TempSongs tempSong = new TempSongs();
    //        ArrayList<TempSongs.TempSong> tempsongs = tempSong.getSongs();
    //
    //        ArrayList<Song> queue = new ArrayList<>();
    //
    //        for(int i = 1; i <= tempsongs.size(); i++)
    //        {
    //            String n = String.valueOf(i);
    //
    //            Song s = new Song();
    ////            s.setTitle(letters[i - 1] + " Song " + n);
    ////            s.setArtist("Artist " + n);
    ////            s.setAlbum("Album " + n);
    //            s.setTitle(tempsongs.get(i - 1).getTitle());
    //            s.setArtist(tempsongs.get(i - 1).getArtist());
    //            s.setAlbum(tempsongs.get(i - 1).getAlbum());
    //
    //            s.setUUID(UUID.randomUUID().toString());
    //            s.setId((long) i);
    //            s.setPath("Path " + n);
    //            s.setFilename("song" + n + ".mp3");
    //            s.setAlbumArtist("Album Artist " + n);
    //            s.setReleaseYear(String.valueOf(2000 + i));
    //            s.setGenre("Genre " + n);
    //            s.setDuration(n);
    //            s.setAlbumID((long) 10000 + i);
    //            queue.add(s);
    //        }
    //
    //        songListFragment = new SongListFragment();
    //        songListFragment.initialize(queue);
    //        fragmentManager.beginTransaction()
    //                .add(R.id.fragment_area, songListFragment, "Song List")
    //                .commit();
    //
    //        new PlayQueue(queue);
    //    }

    @Override
    public void onScanComplete(ArrayList<Song> songs)
    {
        data.setSongs(songs);

        if(data.queueIsSaved())
            queue.load(this);
        else
        {
            long[] ids = new long[songs.size()];
            for(int i = 0; i < ids.length; i++)
                ids[i] = songs.get(i).getId();

            queue.initialize(ids);
        }

        if(fragmentManager != null)
        {
            fragmentManager.songListFragment.setSongs(songs);
            fragmentManager.songListFragment.initialize(data, player, fragmentManager);
            fragmentManager.songList();
        }
        else
        {
            Log.d("Music App", "FragmentManager was null. (onScanComplete)");
            restart();
        }
    }

    @Override
    public void onUpdate()
    {
        try
        {
            ((SongListAdapter) fragmentManager.songListFragment
                    .getSongList().getAdapter()).notifyDataSetChanged();
        }
        catch (Exception e)
        {
            Log.d("Music App", "FragmentManager was null. (updateAudioScannerListener)");
            restart();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            if(getSupportFragmentManager().getBackStackEntryCount() <= 1)
                moveTaskToBack(true);
            else
                super.onBackPressed();

            return true;
        }
        else
            return super.onKeyDown(keyCode, event);
    }

//    @Override
//    public void onBackPressed()
//    {
//        super.onBackPressed();
        //TODO: Manage backstack
//
//        if (!nowPlayingFragment.isVisible())
//        {
//            togglePlayButtonIcon(Player.getPlayer().isPlaying());
//            playButton.show();
//            ((FloatingActionButton) controlButtons.findViewById(R.id.previous_button)).show();
//            ((FloatingActionButton) controlButtons.findViewById(R.id.next_button)).show();
//        }
//    }

    private void restart()
    {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
