package com.music.app.fragments;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.MenuItem;

import com.music.app.MainActivity;
import com.music.app.R;
import com.music.app.database.QueueFragment;
import com.music.app.objects.Song;
import com.music.app.utils.UIManager;
import com.music.app.views.Notice;

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
        ids.put(songDetailsTag,
                new Pair<>
                    (
                        0,
                        R.drawable.info_36dp
                    ));
    }

    public FragmentManager(Context context, UIManager uiManager)
    {
        initReferences();

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
                notYetDevelopedNotice();
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

    public void songList()
    {
        if(!songListFragment.isVisible())
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
                    .replace(R.id.parent, nowPlayingFragment, nowPlayingTag)
                    .addToBackStack(nowPlayingTag)
                    .commit();
    }

    public void queue()
    {
        if(nowPlayingFragment.isVisible())
            popBackStack();

        if(!queueFragment.isVisible())
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

        Pair currentFragmentPair = ids.get(currentFragmentName);
        if(!currentFragmentName.equals(nowPlayingTag))
            uiManager.updateAppBar(currentFragmentName, (Integer) currentFragmentPair.second);

        int currentFragmentID;
        try
        {
            currentFragmentID = (int) currentFragmentPair.first;
        }
        catch (Exception e) { currentFragmentID = 0; }

        if(currentFragmentID != -1)
            activeFragmentID = currentFragmentID;

        uiManager.updateNavigationDrawer();

        Log.d("Current Fragment Name", currentFragmentName);
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
