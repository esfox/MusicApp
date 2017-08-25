package com.music.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.music.app.fragments.FragmentManager;
import com.music.app.fragments.SongListFragment;
import com.music.app.objects.Data;
import com.music.app.objects.PlayQueue;
import com.music.app.objects.Player;
import com.music.app.objects.Queue;
import com.music.app.objects.Song;
import com.music.app.utils.AudioFileScanner;
import com.music.app.utils.UIManager;
import com.music.app.utils.adapters.SongListAdapter;
import com.music.app.utils.interfaces.AudioScanListener;
import com.music.app.utils.interfaces.QueueListener;
import com.music.app.utils.interfaces.ServiceCommunicator;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/*

TODO CURRENT ACTIVITY

Multi-Queue



TODO: Remember to do these

Modify NowPlayingFragment layout (Put album name under cover)
APPLY SINGLETON (SAVE OBJECTS)



WHEN DEACTIVATING MEDIA PLAYING FEATURE

Comment the following:

Class               line number

MainActivity        114, 439-442
SongListViewHolder  117
Player              23-36, 78-84, 87
NowPlayingFragment  83-84, 157-162

*/

public class MainActivity extends AppCompatActivity implements
        ServiceCommunicator,
        AudioScanListener,
        View.OnClickListener,
        View.OnLongClickListener,
        QueueListener,
        NavigationView.OnNavigationItemSelectedListener
{
    public UIManager uiManager;
    public FragmentManager fragmentManager;

    public Player player;
    public Queue queue;
    public Data data;

    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data = new Data(this);

        serviceIntent = new Intent(this, Player.class);
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);

        fragmentManager = new FragmentManager(this);
        uiManager = new UIManager(this, fragmentManager);
        queue = new Queue(data);
//        PlayQueue.setData(data);

        //Scan audio
        AudioFileScanner audioFileScanner = new AudioFileScanner(this, data);
        audioFileScanner.scanAudio();
//        temp();

        if(data.currentSongIsNotNull())
            uiManager.updateNowPlayingBar(data.currentSong(this));
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        stopService(serviceIntent);
        unbindService(connection);
        queue.save(this);
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
    public void onStartAudio(Song song, boolean newSong)
    {
        player.setSong(song);
        startService(serviceIntent);
        player.updateCurrentSong(song, newSong);

        uiManager.togglePlayButtonIcon(true);
        uiManager.updateNowPlayingFragment(song);
        uiManager.updateNowPlayingBar(song);
        uiManager.updatePlayQueueFragmentNowPlaying(song);
        uiManager.updatePlayQueueAdapter();

        //TODO: More efficient current art loading
        new CurrentAlbumArtScanner().execute();
    }

    @Override
    public void onStopAudio()
    {
        stopService(serviceIntent);
        player.stopForeground(true);
    }

    @Override
    public void onQueue(long id)
    {
        queue.queue(id);
    }

    @Override
    public void onPlayNext(long id)
    {
        queue.playNext(id);
    }

    @Override
    public void onScanComplete(ArrayList<Song> songs)
    {
        Data.songs = songs;
//        PlayQueue.queue = songs;

        if(data.queueIsSaved())
            queue.load(this);
        else
        {
            long[] ids = new long[songs.size()];
            for(int i = 0; i < ids.length; i++)
                ids[i] = songs.get(i).getId();

            queue.initialize(ids);
        }

        fragmentManager.songListFragment = new SongListFragment();
        fragmentManager.songListFragment.setSongs(songs);
        fragmentManager.songList();
    }

    @Override
    public void onUpdate()
    {
        ((SongListAdapter) fragmentManager.songListFragment
                .getSongList().getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.play_button:
                player.play();
                uiManager.togglePlayButtonIcon(data.isPlaying());
                break;

            case R.id.previous_button:
                player.previous();
                break;

            case R.id.next_button:
                player.next();
                break;

            case R.id.now_playing_bar:
                uiManager.openNowPlayingBar();
                break;

            case R.id.now_playing_navigation:
                uiManager.toggleNavigationDrawer(true);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v)
    {
        player.stop();
        uiManager.togglePlayButtonIcon(false);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        //TODO: Do other fragments
        //TODO: Update currently selected

        switch(item.getItemId())
        {
            case R.id.navigation_drawer_songs:
                fragmentManager.songList();
                break;

            case R.id.navigation_drawer_playlists:
                fragmentManager.playlists();
                break;

            case R.id.navigation_drawer_favorites:
                fragmentManager.favorites();
                break;

            case R.id.navigation_drawer_play_queue:
                fragmentManager.playQueue();
                break;

            case R.id.navigation_drawer_settings:
                fragmentManager.settings();
                break;
        }

        uiManager.toggleNavigationDrawer(false);

        return true;
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        //TODO: Manage backstack

//        if (!nowPlayingFragment.isVisible())
//        {
//            togglePlayButtonIcon(Player.getPlayer().isPlaying());
//            playButton.show();
//            ((FloatingActionButton) controlButtons.findViewById(R.id.previous_button)).show();
//            ((FloatingActionButton) controlButtons.findViewById(R.id.next_button)).show();
//        }
    }

    private ServiceConnection connection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            Player.ServiceBinder binder = (Player.ServiceBinder) service;
            player = binder.getService();
            player.setServiceCommunicator(MainActivity.this);
            player.initialize(data, queue);
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {

        }
    };

    private class CurrentAlbumArtScanner extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                data.updateCurrentAlbumArt(Glide.with(MainActivity.this).load(new File(data.currentSong(MainActivity.this).getCoverPath())).into(500, 500).get());
            }
            catch (NullPointerException | InterruptedException | ExecutionException e)
            {
                data.updateCurrentAlbumArt(ResourcesCompat.getDrawable(getResources(), R.drawable.library_music_48dp, null));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            uiManager.updateNowPlayingFragment(data.currentSong(MainActivity.this));
        }
    }
}
