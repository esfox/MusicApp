package com.music.app.fragments;

import android.content.Context;
import android.widget.Toast;

import com.music.app.MainActivity;
import com.music.app.R;
import com.music.app.objects.Song;

public class FragmentManager
{
    private android.support.v4.app.FragmentManager fragmentManager;
    private MainActivity mainActivity;

    public SongListFragment songListFragment;
    public NowPlayingFragment nowPlayingFragment;
    public PlaylistsFragment playlistsFragment;
    public FavoritesFragment favoritesFragment;
    public PlayQueueFragment playQueueFragment;
    public SongDetailsFragment songDetailsFragment;

    public int activeFragment = R.id.navigation_drawer_songs;

    public FragmentManager(Context context)
    {
        mainActivity = ((MainActivity) context);
        fragmentManager = ((MainActivity) context).getSupportFragmentManager();

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
                .popBackStack("Song Details", android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);

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
                .setCustomAnimations(R.anim.slide_down, R.anim.slide_up, R.anim.slide_down, R.anim.slide_up)
                .replace(R.id.fragment_area_parent, nowPlayingFragment, "Now Playing")
                .addToBackStack(null)
                .commit();
    }

    public void playQueue()
    {
        activeFragment = R.id.navigation_drawer_play_queue;

        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right)
                .add(R.id.fragment_area_parent, playQueueFragment, "Play Queue")
                .addToBackStack("Play Queue")
                .commit();
    }

    public void settings()
    {
        activeFragment = R.id.navigation_drawer_settings;

        Toast.makeText(mainActivity, "Settings", Toast.LENGTH_SHORT).show();
    }

    public void songDetails(Song song)
    {
        songDetailsFragment.setSong(song);

        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                .add(R.id.fragment_area, songDetailsFragment, "Song Details")
                .addToBackStack("Song Details")
                .commit();
    }

    void popBackStack()
    {
        fragmentManager.popBackStack();
    }
}
