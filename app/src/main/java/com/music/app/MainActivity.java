package com.music.app;

import android.annotation.SuppressLint;
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
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.music.app.fragments.FragmentManager;
import com.music.app.objects.Data;
import com.music.app.objects.Player;
import com.music.app.objects.Queue;
import com.music.app.objects.Song;
import com.music.app.utils.AudioFileScanner;
import com.music.app.utils.UIManager;
import com.music.app.utils.adapters.SongListAdapter;
import com.music.app.utils.interfaces.AudioScanListener;
import com.music.app.utils.interfaces.QueueListener;
import com.music.app.utils.interfaces.ServiceListener;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/*

TODO CURRENT ACTIVITY

Remove SongListAdapter casting on SongListFragment

TODO: Remember to do these
Modify NowPlayingFragment layout (Put album name under cover)
*/

public class MainActivity extends AppCompatActivity implements
        ServiceListener,
        AudioScanListener,
        QueueListener,
        View.OnClickListener,
        View.OnLongClickListener,
        NavigationView.OnNavigationItemSelectedListener
{
    private Player player;
    private Queue queue;
    private Data data;

    private UIManager uiManager;
    private FragmentManager fragmentManager;

    private Intent serviceIntent;
    private ServiceConnection connection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            Player.ServiceBinder binder = (Player.ServiceBinder) service;
            player = binder.getService();
            player.initialize(data, queue);
            player.setServiceListener(MainActivity.this);

            uiManager = new UIManager(MainActivity.this);
            uiManager.initUI
                (
                    MainActivity.this,
                    data,
                    player,
                    MainActivity.this,
                    MainActivity.this
                );
            uiManager.setClickListeners(MainActivity.this, MainActivity.this);
            fragmentManager = uiManager.fragmentManager();

            boolean currentSongIsNotNull = data.currentSongIsNotNull();
            if(currentSongIsNotNull)
            {
                player.resumeSong(MainActivity.this);
                uiManager.initializeCurrentTime(data.currentTime(), !currentSongIsNotNull);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {

        }
    };

    //      TODO: IMPLEMENT PERMISSION REQUESTS
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
//    {
//        if (grantResults[1] != PackageManager.PERMISSION_GRANTED && grantResults[0] != PackageManager.PERMISSION_GRANTED)
//        {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
//            {
//                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        android.Manifest.permission.READ_EXTERNAL_STORAGE}, 2562);
//            }
//        }
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
//            AudioFileScanner audioFileScanner = new AudioFileScanner(this, this, data);
//            audioFileScanner.scanAudio();
////        temp();
//
//            if(data.currentSongIsNotNull())
//                uiManager.updateNowPlayingBar(data.currentSong(this));
//        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
//        {
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    android.Manifest.permission.READ_EXTERNAL_STORAGE}, 2562);
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data = new Data(this);

        serviceIntent = new Intent(this, Player.class);
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);

        queue = new Queue(data);

        //Scan audio
        new AudioFileScanner(this, this, data).scanAudio();
//        temp();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(player != null)
            player.toggleTimeUpdater(true);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        player.toggleTimeUpdater(false);
        queue.save(this);
        data.updateCurrentTime(player.getPlayer().getCurrentPosition());
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
    public void onStartAudio(Song song, boolean newSong, boolean resume)
    {
        player.toggleResumed(resume);

        player.setSong(song);
        startService(serviceIntent);
        player.updateCurrentSong(song, newSong);

        uiManager.togglePlayButtonIcon(!resume);
        uiManager.updateNowPlayingFragment(song);
        uiManager.updateNowPlayingBar(this, song, true);
        uiManager.updatePlayQueueFragmentNowPlaying(song);
        uiManager.updatePlayQueueAdapter();

        //TODO: More efficient current art loading
        new CurrentAlbumArtScanner().execute();
    }

    @Override
    public void onStopAudio()
    {
//        stopService(serviceIntent);
//        player.stopForeground(true);
        uiManager.togglePlayButtonIcon(false);
    }

    @Override
    public void onCurrentTimeUpdate(int time)
    {
        uiManager.updateCurrentTime(time);
        uiManager.updateNowPlayingFragmentCurrentTime(time);
    }

    @Override
    public void onQueue(long id)
    {
        queue.queue(id);
        fragmentManager.playQueueFragment.updateAdapter();
    }

    @Override
    public void onPlayNext(long id)
    {
        queue.playNext(id);
    }

    @Override
    public void onShuffle()
    {
        data.updateIsShuffled(!data.isShuffled());
        queue.shuffle(this);
    }

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

        data.setQueue(queue);

        if(fragmentManager != null)
        {
            fragmentManager.songListFragment.setSongs(songs);
            fragmentManager.songListFragment.initialize(this, this, fragmentManager);
            fragmentManager.songList();
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
            e.printStackTrace();
        }
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

            case R.id.next_button:
                player.next();
                break;

            case R.id.previous_button:
                player.previous();
                break;

//            case R.id.scrub_forward_button:
//                player.scrub(true);
//                uiManager.updateCurrentTime(false);
//                break;
//
//            case R.id.scrub_backward_button:
//                player.scrub(false);
//                uiManager.updateCurrentTime(false);
//                break;

            case R.id.shuffle:
                uiManager.shuffle(this, true, this, data);
                break;

            case R.id.repeat:
                uiManager.repeat(this, true, data);
                break;

            case R.id.play_queue_button:
                fragmentManager.playQueue();
                break;

            case R.id.no_action_yet:
                Toast.makeText(this, "Add Action Here", Toast.LENGTH_SHORT).show();
                break;

            case R.id.now_playing_bar:
                uiManager.openNowPlayingBar();
                break;

            case R.id.toolbar_icon:
                uiManager.toggleNavigationDrawer(true);
                break;

            case R.id.toolbar_menu:
                fragmentManager.songListFragment.menu(v);
                break;

            case R.id.toolbar_sort:
                fragmentManager.songListFragment.sortOptions(v);
        }
    }

    @SuppressLint("InflateParams")
    @Override
    public boolean onLongClick(View v)
    {
        switch(v.getId())
        {
            case R.id.play_button:
                player.stop();
                break;

//            case R.id.scrub_forward_button:
//            case R.id.scrub_backward_button:
//                uiManager.setScrubAmount(this, data);
//                break;
        }
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
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            if(getSupportFragmentManager().getBackStackEntryCount() == 0)
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

    private class CurrentAlbumArtScanner extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                data.updateCurrentAlbumArt(Glide
                        .with(MainActivity.this)
                        .load(new File(data.currentSong(MainActivity.this).getCoverPath()))
                        .into(500, 500).get());
            }
            catch (NullPointerException | InterruptedException | ExecutionException e)
            {
                data.updateCurrentAlbumArt(ResourcesCompat
                        .getDrawable(getResources(), R.drawable.library_music_48dp, null));
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
