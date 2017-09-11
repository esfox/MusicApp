package com.music.app.fragments;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.util.Log;
import android.view.MenuItem;

import com.music.app.MainActivity;
import com.music.app.R;
import com.music.app.database.QueueFragment;
import com.music.app.objects.Song;
import com.music.app.utils.UIManager;

import java.util.HashMap;

public class FragmentManager implements
        OnBackStackChangedListener,
        NavigationView.OnNavigationItemSelectedListener
{
    private UIManager uiManager;
    private android.support.v4.app.FragmentManager fragmentManager;

    public SongListFragment songListFragment;
    public NowPlayingFragment nowPlayingFragment;
    public QueueFragment queueFragment;
    private PlaylistsFragment playlistsFragment;
    private FavoritesFragment favoritesFragment;
    private SongDetailsFragment songDetailsFragment;

//    public PlayQueueFragment playQueueFragment;

    private String allSongsTag = "All Songs";
    private String queueTag = "Play Queue";
    private String songDetailsTag = "Song Details";
    private String nowPlayingTag = "Now Playing";

    private HashMap<String, Integer> icons;

    private void initIcons()
    {
        icons = new HashMap<>();

        icons.put(allSongsTag, R.drawable.all_songs_36dp);
        icons.put(queueTag, R.drawable.queue_list_36dp);
        icons.put(songDetailsTag, R.drawable.info_36dp);
    }

    public FragmentManager(Context context, UIManager uiManager)
    {
        initIcons();

        fragmentManager = ((MainActivity) context).getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);

        songListFragment = new SongListFragment();
        nowPlayingFragment = new NowPlayingFragment();
        queueFragment = new QueueFragment();
        playlistsFragment = new PlaylistsFragment();
        favoritesFragment = new FavoritesFragment();
        songDetailsFragment = new SongDetailsFragment();

//        playQueueFragment = new PlayQueueFragment();

        this.uiManager = uiManager;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.navigation_drawer_songs:
                songList();
                break;

            case R.id.navigation_drawer_playlists:
                playlists();
                break;

            case R.id.navigation_drawer_favorites:
                favorites();
                break;

            case R.id.navigation_drawer_play_queue:
                queue();
                break;

            case R.id.navigation_drawer_settings:
                settings();
                break;
        }

        uiManager.toggleNavigationDrawer(false);

        return true;
    }

    public void songList()
    {
        fragmentManager
                .popBackStack(songDetailsTag,
                        android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);

        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_area, songListFragment, allSongsTag)
                .addToBackStack(allSongsTag)
                .commit();
    }

    private void playlists()
    {
        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_area, playlistsFragment, "Playlist")
                .addToBackStack(null)
                .commit();
    }

    private void favorites()
    {
        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_area, favoritesFragment, "Favorites")
                .addToBackStack(null)
                .commit();
    }

    public void nowPlaying()
    {
        fragmentManager
                .beginTransaction()
                .setCustomAnimations
                    (
                        R.anim.slide_up, R.anim.slide_down,
                        R.anim.slide_up, R.anim.slide_down
                    )
                .replace(R.id.parent, nowPlayingFragment, nowPlayingTag)
                .addToBackStack(nowPlayingTag)
                .commit();
    }

    public void queue()
    {
        if(nowPlayingFragment.isVisible())
            popBackStack();

        fragmentManager
                .beginTransaction()
                .setCustomAnimations
                    (
                        R.anim.slide_in_left, R.anim.slide_out_left,
                        R.anim.slide_in_right, R.anim.slide_out_right

                    )
                .replace(R.id.fragment_area, queueFragment, queueTag)
                .addToBackStack(queueTag)
                .commit();
    }

    private void settings()
    {
        Log.d("Fragment", "Settings");
    }

    public void songDetails(Song song)
    {
        songDetailsFragment.setSong(song);

        fragmentManager
                .beginTransaction()
                .setCustomAnimations
                    (
                        R.anim.fade_in, R.anim.fade_out,
                        R.anim.fade_in, R.anim.fade_out
                    )
                .add(R.id.fragment_area, songDetailsFragment, songDetailsTag)
                .addToBackStack(songDetailsTag)
                .commit();
    }

    void popBackStack()
    {
        fragmentManager.popBackStack();
    }

    @Override
    public void onBackStackChanged()

    {
        String currentFragmentName
                = fragmentManager.getBackStackEntryAt
                (
                    fragmentManager.getBackStackEntryCount() - 1
                ).getName();

        if(!currentFragmentName.equals(nowPlayingTag) && icons.containsKey(currentFragmentName))
            uiManager.updateAppBar(currentFragmentName, icons.get(currentFragmentName));
    }

//    public void playQueue()
//    {
//        fragmentManager
//                .beginTransaction()
//                .setCustomAnimations
//                    (
//                        R.anim.slide_in_left, R.anim.slide_out_left,
//                        R.anim.slide_in_right, R.anim.slide_out_right
//                    )
//                .replace(R.id.parent, playQueueFragment, "Play Queue")
//                .addToBackStack("Play Queue")
//                .commit();
//    }
}
