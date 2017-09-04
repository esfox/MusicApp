package com.music.app.fragments;

import android.content.Context;
import android.util.Log;

import com.music.app.MainActivity;
import com.music.app.R;
import com.music.app.objects.Song;

public class FragmentManager
{
    private android.support.v4.app.FragmentManager fragmentManager;

    public SongListFragment songListFragment;
    public NowPlayingFragment nowPlayingFragment;
    private PlaylistsFragment playlistsFragment;
    private FavoritesFragment favoritesFragment;
    public PlayQueueFragment playQueueFragment;
    private SongDetailsFragment songDetailsFragment;

    public int activeFragment = R.id.navigation_drawer_songs;

    public FragmentManager
            (Context context)
    {
        fragmentManager = ((MainActivity) context).getSupportFragmentManager();

        songListFragment = new SongListFragment();
        nowPlayingFragment = new NowPlayingFragment();
        playQueueFragment = new PlayQueueFragment();
        playlistsFragment = new PlaylistsFragment();
        favoritesFragment = new FavoritesFragment();
        songDetailsFragment = new SongDetailsFragment();
    }

//    private void onFragmentChanged()
//    {
//    }

    public void songList()
    {
        activeFragment = R.id.navigation_drawer_songs;

        fragmentManager
                .popBackStack("Song Details",
                        android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);

        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_area, songListFragment, "Song List")
                .commit();
    }

    public void playlists()
    {
        activeFragment = R.id.navigation_drawer_playlists;

        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_area, playlistsFragment, "Playlist")
                .addToBackStack(null)
                .commit();
    }

    public void favorites()
    {
        activeFragment = R.id.navigation_drawer_favorites;

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
                .replace(R.id.fragment_area_parent, nowPlayingFragment,
                        "Now Playing")
                .addToBackStack(null)
                .commit();
    }

    public void playQueue()
    {
        activeFragment = R.id.navigation_drawer_play_queue;

        fragmentManager
                .beginTransaction()
                .setCustomAnimations
                    (
                        R.anim.slide_in_left, R.anim.slide_out_left,
                        R.anim.slide_in_right, R.anim.slide_out_right
                    )
                .replace(R.id.fragment_area_parent, playQueueFragment, "Play Queue")
                .addToBackStack("Play Queue")
                .commit();
    }

    public void settings()
    {
        activeFragment = R.id.navigation_drawer_settings;
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
                .add(R.id.fragment_area, songDetailsFragment, "Song Details")
                .addToBackStack("Song Details")
                .commit();
    }

    void popBackStack()
    {
        fragmentManager.popBackStack();
    }
}
