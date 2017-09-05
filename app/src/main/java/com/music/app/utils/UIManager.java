package com.music.app.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
    private Activity views;
    private FragmentManager fragmentManager;

    private ImageButton playButton, shuffle, repeat;
    private NavigationView navigationDrawer;
    private TextView title, artist, currentTime;
    private ImageView cover;

    public UIManager(Activity activity)
    {
        views = activity;
    }

    public FragmentManager fragmentManager()
    {
        return fragmentManager;
    }

    public void initUI(
            Context context,
            Data data,
            Player player,
            NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener,
            QueueListener queueListener)
    {
        fragmentManager = new FragmentManager(context);
        fragmentManager.nowPlayingFragment.initialize(data, player, this, queueListener);
        fragmentManager.playQueueFragment.initialize(data, player, queueListener);

        //Initialize Now Playing Bar
        title = (TextView) views.findViewById(R.id.now_playing_bar_title);
        artist = (TextView) views.findViewById(R.id.now_playing_bar_artist);
        cover = (ImageView)  views.findViewById(R.id.now_playing_bar_cover);
        currentTime = (TextView)
                views.findViewById(R.id.now_playing_bar_current_time);

        updateNowPlayingBar(context, data.currentSong(context), data.currentSongIsNotNull());

        playButton = (ImageButton)
                views.findViewById(R.id.play_button);
        shuffle = (ImageButton)
                views.findViewById(R.id.shuffle);
        repeat = (ImageButton)
                views.findViewById(R.id.repeat);

        shuffle(context, false, queueListener, data);
        repeat(context, false, data);

        //Initialize Navigation Drawer
        navigationDrawer = ((NavigationView)
                views.findViewById(R.id.navigation_drawer));
        navigationDrawer.setItemIconTintList(null);
        navigationDrawer.setNavigationItemSelectedListener(navigationItemSelectedListener);
        navigationDrawer.setTag(views.findViewById(R.id.drawer_layout));

        updateNavigationDrawer();
    }

    public void setClickListeners(View.OnClickListener onClickListener,
                                  View.OnLongClickListener onLongClickListener)
    {
        playButton.setOnClickListener(onClickListener);
        playButton.setOnLongClickListener(onLongClickListener);
        views.findViewById(R.id.next_button).setOnClickListener(onClickListener);
        views.findViewById(R.id.previous_button).setOnClickListener(onClickListener);

//        View scrubForward = views.findViewById(R.id.scrub_forward_button);
//        View scrubBackward = views.findViewById(R.id.scrub_backward_button);
//        scrubForward.setOnClickListener(onClickListener);
//        scrubForward.setOnLongClickListener(onLongClickListener);
//        scrubBackward.setOnClickListener(onClickListener);
//        scrubBackward.setOnLongClickListener(onLongClickListener);

        shuffle.setOnClickListener(onClickListener);
        repeat.setOnClickListener(onClickListener);

        views.findViewById(R.id.no_action_yet).setOnClickListener(onClickListener);

        views.findViewById(R.id.toolbar_icon).setOnClickListener(onClickListener);
        views.findViewById(R.id.toolbar_menu).setOnClickListener(onClickListener);
        views.findViewById(R.id.toolbar_sort).setOnClickListener(onClickListener);

        views.findViewById(R.id.now_playing_bar).setOnClickListener(onClickListener);
        views.findViewById(R.id.play_queue_button).setOnClickListener(onClickListener);
        views.findViewById(R.id.no_action_yet).setOnClickListener(onClickListener);
    }

    public void openNowPlayingBar()
    {
        fragmentManager.nowPlaying();
//        toggleControlButtons(false);
    }

    public void updateNowPlayingBar(Context context, Song song, boolean currentSongIsNotNull)
    {
        if (currentSongIsNotNull)
        {
            if(cover.getVisibility() == View.GONE)
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
            cover.setVisibility(View.GONE);
            title.setText(greetings());
            artist.setText("Select a song to play.");
        }
    }

    public void initializeCurrentTime(long time, boolean isNew)
    {
        if(!isNew)
        {
            if(currentTime.getVisibility() == View.GONE)
                currentTime.setVisibility(View.VISIBLE);

            currentTime.setText(Timestamper.getTimestamp(time));
        }
        else
            currentTime.setVisibility(View.GONE);
    }

    public void updateCurrentTime(int time)
    {
        currentTime.setText(Timestamper.getTimestamp(time));
    }

    public void updateNowPlayingFragment(Song song)
    {
        if (fragmentManager.nowPlayingFragment.isVisible())
            fragmentManager.nowPlayingFragment.update(song, true);
    }

    public void updateNowPlayingFragmentCurrentTime(int time)
    {
        if (fragmentManager.nowPlayingFragment.isVisible())
            fragmentManager.nowPlayingFragment.updateProgress(time);
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

    public void togglePlayButtonIcon(boolean isPlaying)
    {
        if(fragmentManager.nowPlayingFragment.isVisible())
            fragmentManager.nowPlayingFragment.togglePlayButtonIcon();

        if (isPlaying)
            playButton.setImageResource(R.drawable.pause_24dp);
        else
            playButton.setImageResource(R.drawable.play_24dp);
    }

    public void shuffle(Context context, boolean toggle, QueueListener queueListener, Data data)
    {
        if(toggle)
            queueListener.onShuffle();

        if(data.isShuffled())
            shuffle.setColorFilter
                (
                    ContextCompat.getColor(context,
                            R.color.toggle_on),
                    PorterDuff.Mode.SRC_ATOP
                );
        else
            shuffle.setColorFilter
                (
                    ContextCompat.getColor(context,
                            R.color.toggle_off),
                    PorterDuff.Mode.SRC_ATOP
                );
    }

    public void repeat(Context context, boolean toggle, Data data)
    {
    if(toggle)
        data.updateRepeatState();

    switch(data.repeatState())
    {
        case OFF:
            repeat.setImageResource(R.drawable.repeat_24dp);
            repeat.setColorFilter(ContextCompat.getColor(context, R.color.toggle_off), PorterDuff.Mode.SRC_ATOP);
            break;

        case ALL:
            repeat.setColorFilter(ContextCompat.getColor(context, R.color.toggle_on), PorterDuff.Mode.SRC_ATOP);
            break;

        case ONE:
            repeat.setImageResource(R.drawable.repeat_one_24dp);
            repeat.setColorFilter(ContextCompat.getColor(context, R.color.toggle_on), PorterDuff.Mode.SRC_ATOP);
            break;
    }
}

//    public void setScrubAmount(Context context, final Data data)
//    {
//        AlertDialog.Builder dialog = Dialoger.getDialogBuilder(context);
//        dialog.setTitle("Scrub Amount");
//        dialog.setMessage("Fast forward or rewind by how many seconds?");
//
//        final NumberPicker numberPicker = new NumberPicker(context);
//        numberPicker.setWrapSelectorWheel(true);
//        numberPicker.setMinValue(1);
//        numberPicker.setMaxValue(30);
//        numberPicker.setValue(data.scrubAmount());
//        dialog.setView(numberPicker);
//        dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener()
//        {
//            @Override
//            public void onClick(DialogInterface dialog, int which)
//            {
//                data.updateScrubAmount(numberPicker.getValue());
//            }
//        });
//        dialog.setNegativeButton("Cancel", null);
//        dialog.show();
//    }

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
