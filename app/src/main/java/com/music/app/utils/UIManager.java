package com.music.app.utils;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.music.app.MainActivity;
import com.music.app.R;
import com.music.app.objects.Data;
import com.music.app.objects.Player;

public class UIManager
{
    public static UIManager instance;

    private MainActivity mainActivity;

    public UIManager(Context context)
    {
        instance = this;

        mainActivity = (MainActivity) context;
        initUI();
    }

    private void initUI()
    {
        //Initialize Now Playing Bar
        mainActivity.ui.toolbar = (Toolbar) mainActivity.findViewById(R.id.toolbar);
        mainActivity.ui.toolbar.setTitle("Hello, User!");
        mainActivity.ui.toolbar.setSubtitle("Select a song to play.");

        //Initialize control buttons
        mainActivity.ui.previousButton = (ImageButton) mainActivity.findViewById(R.id.previous_button);
        mainActivity.ui.nextButton = (ImageButton) mainActivity.findViewById(R.id.next_button);
        mainActivity.ui.playButton = (ImageButton) mainActivity.findViewById(R.id.play_button);

        //Initialize Navigation Drawer
        mainActivity.ui.navigationDrawer = ((NavigationView) mainActivity.findViewById(R.id.navigation_drawer));
        mainActivity.ui.navigationDrawer.setItemIconTintList(null);
        mainActivity.ui.navigationDrawer.setNavigationItemSelectedListener(mainActivity);
        mainActivity.ui.navigationDrawer.setTag(mainActivity.findViewById(R.id.drawer_layout));
        updateNavigationDrawer();

        //Set Click Listeners
        mainActivity.findViewById(R.id.now_playing_bar).setOnClickListener(mainActivity);
        mainActivity.findViewById(R.id.now_playing_navigation).setOnClickListener(mainActivity);
        mainActivity.ui.previousButton.setOnClickListener(mainActivity);
        mainActivity.ui.nextButton.setOnClickListener(mainActivity);
        mainActivity.ui.playButton.setOnClickListener(mainActivity);
        mainActivity.ui.playButton.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                Player.stop();
                togglePlayButtonIcon(Data.isPlaying);
                return true;
            }
        });
    }

    public void updateNowPlayingBar()
    {
        ImageView nowPlayingCover = (ImageView) mainActivity.findViewById(R.id.now_playing_bar_cover);

        if (Data.currentSong != null)
        {
            nowPlayingCover.setVisibility(View.VISIBLE);
            nowPlayingCover.setImageDrawable(Data.currentSong.getCover());
            mainActivity.ui.toolbar.setTitle(Data.currentSong.getTitle());
            mainActivity.ui.toolbar.setSubtitle(Data.currentSong.getArtist());
        }
        else
        {
            nowPlayingCover.setVisibility(View.INVISIBLE);
            mainActivity.ui.toolbar.setTitle("Hello, User!");
            mainActivity.ui.toolbar.setSubtitle("Select a song to play.");
        }
    }

    public void updateNowPlayingFragment()
    {
        if (mainActivity.fragmentManager.nowPlayingFragment.isVisible())
            mainActivity.fragmentManager.nowPlayingFragment.update(true);
    }

    public void updatePlayQueueFragment()
    {
        if (mainActivity.fragmentManager.playQueueFragment.isVisible())
            mainActivity.fragmentManager.playQueueFragment.updateNowPlaying();
    }

    public void updateNavigationDrawer()
    {
        if(mainActivity.ui.navigationDrawer.getMenu().findItem(R.id.navigation_drawer_songs).isChecked())
            mainActivity.ui.navigationDrawer.getMenu().findItem(R.id.navigation_drawer_songs).setChecked(false);

        mainActivity.ui.navigationDrawer.getMenu().findItem(mainActivity.fragmentManager.activeFragment).setChecked(true);
    }

    public void toggleControlButtons(boolean toggle)
    {
        if(toggle)
        {
//            mainActivity.ui.playButton.show();
//            mainActivity.ui.previousButton.show();
//            mainActivity.ui.nextButton.show();
        }
        else
        {
//            mainActivity.ui.playButton.hide();
//            mainActivity.ui.previousButton.hide();
//            mainActivity.ui.nextButton.hide();
        }
    }

    public void togglePlayButtonIcon(boolean isPlaying)
    {
        if(mainActivity.fragmentManager.nowPlayingFragment.isVisible())
            mainActivity.fragmentManager.nowPlayingFragment.togglePlayButtonIcon(isPlaying);

        if (isPlaying)
        {
            Log.d("com.music.app", "Button: Pause");
            mainActivity.ui.playButton.setImageResource(R.drawable.pause_24dp);
        }
        else
        {
            Log.d("com.music.app", "Button: Play");
            mainActivity.ui.playButton.setImageResource(R.drawable.play_24dp);
        }
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
