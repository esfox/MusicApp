package com.music.app;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.music.app.fragments.FragmentManager;
import com.music.app.objects.Data;
import com.music.app.objects.Player;
import com.music.app.objects.UI;
import com.music.app.utils.AudioFileScanner;
import com.music.app.utils.UIManager;

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

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener
{
    public UI ui;
    public UIManager uiManager;
    public FragmentManager fragmentManager;
    public Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data = new Data(this);

        //Initialize UI
        ui = new UI();

        //Initialize Fragments
        fragmentManager = new FragmentManager(this);

        //Initialize UI Manager
        uiManager = new UIManager(this);

        //Scan audio
        AudioFileScanner audioFileScanner = new AudioFileScanner(this);
        audioFileScanner.scanAudio();

//        temp();
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
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.play_button:
                Player.play();
                uiManager.togglePlayButtonIcon(Data.isPlaying);
                break;

            case R.id.previous_button:
                Player.previous();
                break;

            case R.id.next_button:
                Player.next();
                break;

            case R.id.now_playing_bar:
                fragmentManager.nowPlaying();
                break;

            case R.id.now_playing_navigation:
                ((DrawerLayout) ui.navigationDrawer.getTag()).openDrawer(GravityCompat.START);
                break;
        }
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

        ((DrawerLayout) ui.navigationDrawer.getTag()).closeDrawer(GravityCompat.START);
        uiManager.updateNavigationDrawer();

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
}
