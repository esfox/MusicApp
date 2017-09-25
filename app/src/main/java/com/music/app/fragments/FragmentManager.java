package com.music.app.fragments;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.util.Pair;
import android.view.MenuItem;

import com.music.app.MainActivity;
import com.music.app.R;
import com.music.app.interfaces.UIUpdatesListener;
import com.music.app.objects.Data;
import com.music.app.objects.Player;
import com.music.app.objects.Playlist;
import com.music.app.objects.Song;
import com.music.app.utils.UIManager;
import com.music.app.views.Notice;

import java.util.ArrayList;
import java.util.HashMap;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

public class FragmentManager implements
        OnBackStackChangedListener,
        NavigationView.OnNavigationItemSelectedListener
{
    private UIManager uiManager;
    private android.support.v4.app.FragmentManager fragmentManager;

    private SongListFragment songListFragment;
    private NowPlayingFragment nowPlayingFragment;
    private QueueFragment queueFragment;

    private Data data;
    private Player player;

//    public PlayQueueFragment playQueueFragment;

    private final String allSongsTag = "All Songs";
    private final String queueTag = "Play Queue";
    private final String playlistsTag = "Playlists";

    private HashMap<String, Pair<Integer, Integer>> ids;
    private int activeFragmentID = R.id.navigation_drawer_songs;

    private void initReferences()
    {
        ids = new HashMap<>();

        ids.put(allSongsTag,
                new Pair<>
                    (
                        R.id.navigation_drawer_songs,
                        R.drawable.all_songs_36dp
                    ));
        ids.put(queueTag,
                new Pair<>
                    (
                        R.id.navigation_drawer_queue,
                        R.drawable.queue_list_36dp
                    ));
        ids.put(playlistsTag,
                new Pair<>
                    (
                        R.id.navigation_drawer_playlists,
                        R.drawable.playlist_36dp
                    ));
    }

    public FragmentManager(Context context, UIManager uiManager, Data data, Player player)
    {
        initReferences();

        fragmentManager = ((MainActivity) context).getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);

        songListFragment = new SongListFragment();
        nowPlayingFragment = new NowPlayingFragment();
        queueFragment = new QueueFragment();

        initFragments(uiManager, data, player);

        this.uiManager = uiManager;
    }

    private void initFragments(UIManager uiManager, Data data, Player player)
    {
        this.data = data;
        this.player = player;

        nowPlayingFragment.initialize(uiManager, data, player);
        queueFragment.initialize(data, player);
    }

    public UIUpdatesListener[] getUIUpdatesListeners()
    {
        return new UIUpdatesListener[]
        {
            uiManager,
            nowPlayingFragment,
            queueFragment
        };
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.navigation_drawer_songs:
                songList();
                break;

            case R.id.navigation_drawer_playlists:
//                notYetDevelopedNotice();
                playlistsList();
                break;

            case R.id.navigation_drawer_favorites:
                notYetDevelopedNotice();
                break;

            case R.id.navigation_drawer_queue:
                queue();
                break;

            case R.id.navigation_drawer_settings:
                notYetDevelopedNotice();
                break;

            case R.id.navigation_drawer_help:
                notYetDevelopedNotice();
                break;

            case R.id.navigation_drawer_about:
                notYetDevelopedNotice();
                break;
        }

        uiManager.toggleNavigationDrawer(false);

        return true;
    }

    private void notYetDevelopedNotice()
    {
        Notice notice = new Notice(songListFragment.getContext());
        notice.setNoticeText("Sorry! This part has not yet been developed.");
        notice.show();

        uiManager.updateNavigationDrawer();
    }

    public int getActiveFragmentID()
    {
        return activeFragmentID;
    }

    public void initializeSongList(ArrayList<Song> songs, Data data, Player player)
    {
        songListFragment.setSongs(songs);
        songListFragment.initialize(data, player, this);
        songList();
    }

    private void songList()
    {
        if(!songListFragment.isVisible())
        {
            hideRemainingFragments();

            fragmentManager
            .beginTransaction()
            .replace(R.id.fragment_area, songListFragment, allSongsTag)
            .commit();
        }
    }

    public SongListFragment getSongListFragment()
    {
        return songListFragment;
    }

    public void nowPlaying()
    {
        if(!nowPlayingFragment.isVisible())
            fragmentManager
            .beginTransaction()
            .setCustomAnimations
                (
                    R.anim.slide_up, R.anim.slide_down,
                    R.anim.slide_up, R.anim.slide_down
                )
            .replace(R.id.parent, nowPlayingFragment, "Now Playing")
            .addToBackStack("Now Playing")
            .commit();
    }

    public void updateNowPlayingCover()
    {
        if(nowPlayingFragment.isVisible())
            nowPlayingFragment.updateCover();
    }

    public void queue()
    {
        if(!queueFragment.isVisible())
        {
            hideRemainingFragments();

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

            updateUI(queueTag);
        }
    }

    private void playlistsList()
    {
        PlaylistsListFragment playlistsListFragment = new PlaylistsListFragment();
        playlistsListFragment.initialize(data, this);
        if(!playlistsListFragment.isVisible())
        {
            hideRemainingFragments();

            fragmentManager.beginTransaction()
            .replace(R.id.fragment_area, playlistsListFragment, playlistsTag)
            .addToBackStack(playlistsTag)
            .commit();

            updateUI(playlistsTag);
        }
    }

    public void playlist(Playlist playlist)
    {
        PlaylistFragment playlistFragment = new PlaylistFragment();
        playlistFragment.initialize(playlist, data, player, this);
        fragmentManager.beginTransaction()
        .replace(R.id.fragment_area, playlistFragment, "Playlist")
        .addToBackStack(null)
        .commit();
    }

    public void songDetails(Song song)
    {
        SongDetailsFragment songDetailsFragment = new SongDetailsFragment();
        songDetailsFragment.setSong(song);

        fragmentManager
        .beginTransaction()
        .setCustomAnimations
            (
                R.anim.fade_in, R.anim.fade_out,
                R.anim.fade_in, R.anim.fade_out
            )
        .add(R.id.song_details_area, songDetailsFragment, "Song Details")
        .addToBackStack(null)
        .commit();
    }

    public void popBackStack()
    {
        fragmentManager.popBackStack();
    }

    private void updateUI(final String tag)
    {
        new Handler().post(new Runnable()
        {
            @Override
            public void run()
            {
                Pair current = ids.get(tag);
                activeFragmentID = (int) current.first;
                uiManager.updateAppBar(tag, (Integer) current.second);
                uiManager.updateNavigationDrawer();
                uiManager.updateNowPlayingBarFunctionButton(activeFragmentID);
            }
        });
    }

    private void hideRemainingFragments()
    {
        fragmentManager.popBackStack(null, POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onBackStackChanged()
    {
        if(songListFragment.isVisible())
        {
            Pair currentFragmentPair = ids.get(allSongsTag);
            activeFragmentID = (int) currentFragmentPair.first;
            uiManager.updateAppBar(allSongsTag, (Integer) currentFragmentPair.second);
            uiManager.updateNavigationDrawer();
            uiManager.updateNowPlayingBarFunctionButton(activeFragmentID);
        }
    }
}
