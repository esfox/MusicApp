package com.music.app.utils;

import android.content.Context;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.music.app.MainActivity;
import com.music.app.R;
import com.music.app.fragments.FragmentManager;
import com.music.app.objects.Data;
import com.music.app.objects.Player;
import com.music.app.objects.Song;
import com.music.app.utils.interfaces.QueueListener;

import java.io.File;
import java.util.Calendar;

public class UIManager
{
    private Context context;
    private FragmentManager fragmentManager;
    private Player player;

    private ImageButton playButton, previousButton, nextButton;
    private NavigationView navigationDrawer;
    private TextView title, artist, currentTime;
    private ImageView cover;

    public UIManager(Context context)
    {
        this.context = context;
    }

    public FragmentManager fragmentManager()
    {
        return fragmentManager;
    }

    public void initUI(Data data, Player player, QueueListener queueListener)
    {
        this.player = player;

        fragmentManager = new FragmentManager(context);
        fragmentManager.nowPlayingFragment.initialize(data, player, this, queueListener);
        fragmentManager.playQueueFragment.initialize(data, player, queueListener);

        //Initialize Now Playing Bar
        title = (TextView) ((MainActivity) context).findViewById(R.id.now_playing_bar_title);
        artist = (TextView) ((MainActivity) context).findViewById(R.id.now_playing_bar_artist);
        cover = (ImageView)  ((MainActivity) context).findViewById(R.id.now_playing_bar_cover);
        currentTime = (TextView)
                ((MainActivity) context).findViewById(R.id.now_playing_bar_current_time);

        updateNowPlayingBar(data.currentSong(context));

        //Initialize control buttons
        previousButton = (ImageButton)
                ((MainActivity) context).findViewById(R.id.previous_button);
        nextButton = (ImageButton)
                ((MainActivity) context).findViewById(R.id.next_button);
        playButton = (ImageButton)
                ((MainActivity) context).findViewById(R.id.play_button);

        //Initialize Navigation Drawer
        navigationDrawer = ((NavigationView)
                ((MainActivity) context).findViewById(R.id.navigation_drawer));
        navigationDrawer.setItemIconTintList(null);
        navigationDrawer.setNavigationItemSelectedListener((MainActivity) context);
        navigationDrawer.setTag(((MainActivity) context).findViewById(R.id.drawer_layout));
        updateNavigationDrawer();

        //Set Click Listeners
    }

    public void setClickListeners(View.OnClickListener onClickListener,
                                  View.OnLongClickListener onLongClickListener)
    {
        ((MainActivity) context)
                .findViewById(R.id.now_playing_bar)
                .setOnClickListener(onClickListener);
        ((MainActivity) context)
                .findViewById(R.id.toolbar_icon)
                .setOnClickListener(onClickListener);
        ((MainActivity) context)
                .findViewById(R.id.toolbar_menu)
                .setOnClickListener(onClickListener);
        ((MainActivity) context)
                .findViewById(R.id.toolbar_sort)
                .setOnClickListener(onClickListener);
        previousButton.setOnClickListener(onClickListener);
        nextButton.setOnClickListener(onClickListener);
        playButton.setOnClickListener(onClickListener);
        playButton.setOnLongClickListener(onLongClickListener);
    }

    public void openNowPlayingBar()
    {
        fragmentManager.nowPlaying();
//        toggleControlButtons(false);
    }

    public void updateNowPlayingBar(Song song)
    {
        if (song != null)
        {
            cover.setVisibility(View.VISIBLE);

            if(song.getCover() != null)
                cover.setImageDrawable(song.getCover());
            else
            {
                Glide.with(context)
                        .load((song.getCoverPath() != null)?
                                new File(song.getCoverPath()) :
                                R.drawable.library_music_48dp)
                        .into(cover);
            }

            title.setText(song.getTitle());
            artist.setText(song.getArtist());
        }
        else
        {
            cover.setVisibility(View.INVISIBLE);
            title.setText(greetings());
            artist.setText("Select a song to play.");
        }

        timeUpdater = new Handler();
        updateCurrentTime();
    }

    private Handler timeUpdater;

    private void updateCurrentTime()
    {
        this.currentTime.setText(getDuration(player.getPlayer().getCurrentPosition()));
        timeUpdater.postDelayed(timeUpdaterRunnable, 1000);
    }

    private Runnable timeUpdaterRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            updateCurrentTime();
        }
    };

    private String getDuration(long milliseconds)
    {
        String duration = "";
        String secondsString;

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        // Add hours if there
        if(hours > 0)
            duration = hours + ":";

        // Prepending 0 to seconds if it is ONE digit
        if(seconds < 10)
            secondsString = "0" + seconds;
        else
            secondsString = "" + seconds;

        duration += minutes + ":" + secondsString;

        return duration;
    }

    public void updateNowPlayingFragment(Song song)
    {
        if (fragmentManager.nowPlayingFragment.isVisible())
            fragmentManager.nowPlayingFragment.update(song, true);
    }

    public void updatePlayQueueAdapter()
    {
        fragmentManager.playQueueFragment.updateAdapter();
    }

    public void updatePlayQueueFragmentNowPlaying(Song song)
    {
        if (fragmentManager.playQueueFragment.isVisible())
            fragmentManager.playQueueFragment.updateNowPlaying(song);
    }

    public void toggleNavigationDrawer(boolean open)
    {
        if(open)
            ((DrawerLayout) navigationDrawer.getTag()).openDrawer(GravityCompat.START);
        else
        {
            ((DrawerLayout) navigationDrawer.getTag()).closeDrawer(GravityCompat.START);
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
        if(fragmentManager.nowPlayingFragment.isVisible())
            fragmentManager.nowPlayingFragment.togglePlayButtonIcon(isPlaying);

        if (isPlaying)
            playButton.setImageResource(R.drawable.pause_24dp);
        else
            playButton.setImageResource(R.drawable.play_24dp);
    }

    private String greetings()
    {
        String greeting = "Good ";

        Calendar calendar = Calendar.getInstance();
        int time = calendar.get(Calendar.HOUR_OF_DAY);

        if (time >= 0 && time < 12)
            greeting += "Morning";
        else if (time >= 12 && time < 18)
            greeting += "Afternoon";
        else if (time >= 18 && time < 24)
            greeting += "Evening";
        else
            greeting += "Day";

        //TODO: Get user's name
        greeting += ", " + "John!";

        return greeting;
    }
}
