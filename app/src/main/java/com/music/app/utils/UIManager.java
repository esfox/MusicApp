package com.music.app.utils;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.music.app.MainActivity;
import com.music.app.R;
import com.music.app.fragments.FragmentManager;
import com.music.app.objects.Data;

public class UIManager
{
    private Context context;
    private FragmentManager fragmentManager;

    public Toolbar toolbar;
    public ImageButton playButton;
    private NavigationView navigationDrawer;

    public UIManager(Context context, FragmentManager fragmentManager)
    {
        this.context = context;
        this.fragmentManager = fragmentManager;
        initUI();
    }

    private void initUI()
    {
        //Initialize Now Playing Bar
        toolbar = (Toolbar) ((MainActivity) context).findViewById(R.id.toolbar);
        toolbar.setTitle("Hello, User!");
        toolbar.setSubtitle("Select a song to play.");

        //Initialize control buttons
        ImageButton previousButton = (ImageButton) ((MainActivity) context).findViewById(R.id.previous_button);
        ImageButton nextButton = (ImageButton) ((MainActivity) context).findViewById(R.id.next_button);
        playButton = (ImageButton) ((MainActivity) context).findViewById(R.id.play_button);

        //Initialize Navigation Drawer
        navigationDrawer = ((NavigationView) ((MainActivity) context).findViewById(R.id.navigation_drawer));
        navigationDrawer.setItemIconTintList(null);
        navigationDrawer.setNavigationItemSelectedListener((MainActivity) context);
        navigationDrawer.setTag(((MainActivity) context).findViewById(R.id.drawer_layout));
        updateNavigationDrawer();

        //Set Click Listeners
        ((MainActivity) context).findViewById(R.id.now_playing_bar).setOnClickListener((MainActivity) context);
        ((MainActivity) context).findViewById(R.id.now_playing_navigation).setOnClickListener((MainActivity) context);
        previousButton.setOnClickListener((MainActivity) context);
        nextButton.setOnClickListener((MainActivity) context);
        playButton.setOnClickListener((MainActivity) context);
        playButton.setOnLongClickListener((MainActivity) context);
    }

    public void openNowPlayingBar()
    {
        fragmentManager.nowPlaying();
//        toggleControlButtons(false);
    }

    public void updateNowPlayingBar()
    {
        ImageView nowPlayingCover = (ImageView) ((MainActivity) context).findViewById(R.id.now_playing_bar_cover);

        if (Data.currentSong != null)
        {
            nowPlayingCover.setVisibility(View.VISIBLE);
            nowPlayingCover.setImageDrawable(Data.currentSong.getCover());
            toolbar.setTitle(Data.currentSong.getTitle());
            toolbar.setSubtitle(Data.currentSong.getArtist());
        }
        else
        {
            nowPlayingCover.setVisibility(View.INVISIBLE);
            toolbar.setTitle("Hello, User!");
            toolbar.setSubtitle("Select a song to play.");
        }
    }

    public void updateNowPlayingFragment()
    {
        if (fragmentManager.nowPlayingFragment.isVisible())
            fragmentManager.nowPlayingFragment.update(true);
    }

    public void updatePlayQueueFragment()
    {
        if (fragmentManager.playQueueFragment.isVisible())
            fragmentManager.playQueueFragment.updateNowPlaying();
    }

    public void toggleNavigationDrawer(boolean open)
    {
        if(open)
            ((DrawerLayout) navigationDrawer.getTag()).openDrawer(GravityCompat.START);
        else
        {
            updateNavigationDrawer();
        }
    }

    private void updateNavigationDrawer()
    {
        if(navigationDrawer.getMenu().findItem(R.id.navigation_drawer_songs).isChecked())
            navigationDrawer.getMenu().findItem(R.id.navigation_drawer_songs).setChecked(false);

        navigationDrawer.getMenu().findItem(fragmentManager.activeFragment).setChecked(true);
    }

//    public void toggleControlButtons(boolean toggle)
//    {
//        if(toggle)
//        {
//            playButton.show();
//            previousButton.show();
//            nextButton.show();
//        }
//        else
//        {
//            playButton.hide();
//            previousButton.hide();
//            nextButton.hide();
//        }
//    }

    public void togglePlayButtonIcon(boolean isPlaying)
    {
        Log.d("isPlaying", String.valueOf(isPlaying));

        if(fragmentManager.nowPlayingFragment.isVisible())
            fragmentManager.nowPlayingFragment.togglePlayButtonIcon(isPlaying);

        if (isPlaying)
            playButton.setImageResource(R.drawable.pause_24dp);
        else
            playButton.setImageResource(R.drawable.play_24dp);
    }

//    private void showGreetings()
//    {
//        String greeting = "Good ";
//
//        Calendar calendar = Calendar.getInstance();
//        int time = calendar.get(Calendar.HOUR_OF_DAY);
//
//        if (time >= 0 && time < 12)
//            greeting += "Morning";
//        else if (time >= 12 && time < 18)
//            greeting += "Afternoon";
//        else if (time >= 18 && time < 24)
//            greeting += "Evening";
//        else
//            greeting += "Day";
//    }
}
