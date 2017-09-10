package com.music.app.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.music.app.R;
import com.music.app.fragments.FragmentManager;
import com.music.app.interfaces.AudioListener;
import com.music.app.interfaces.QueueListener;
import com.music.app.interfaces.UIListener;
import com.music.app.objects.Data;
import com.music.app.objects.Player;
import com.music.app.objects.Song;

import java.io.File;
import java.util.Calendar;

public class UIManager implements AudioListener, UIListener
{
    private Activity views;
    private FragmentManager fragmentManager;
    private Data data;

    private ImageButton playButton, shuffle, repeat;
    private NavigationView navigationDrawer;
    private TextView title, artist, currentTime, appBarTitle;
    private ImageView cover, appBarIcon;

    private AudioListener[] audioListeners;

    public UIManager(Activity activity)
    {
        views = activity;
    }

    public FragmentManager fragmentManager()
    {
        return fragmentManager;
    }

    public void initUI
        (
            Context context,
            Data data,
            Player player,
            NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener,
            QueueListener queueListener
        )
    {
        this.data = data;

        fragmentManager = new FragmentManager(context, this);
        fragmentManager.nowPlayingFragment.initialize(data, player, this, queueListener);
        fragmentManager.playQueueFragment.initialize(data, player, queueListener);
        fragmentManager.queueFragment.initialize(data, player);

        audioListeners = new AudioListener[]
        {
            this,
            fragmentManager.nowPlayingFragment,
            fragmentManager.queueFragment
        };

        //Initialize App Bar
        appBarTitle = (TextView) views.findViewById(R.id.toolbar_title);
        appBarIcon = (ImageView) views.findViewById(R.id.toolbar_icon);

        //Initialize Now Playing Bar
        title = (TextView) views.findViewById(R.id.now_playing_bar_title);
        artist = (TextView) views.findViewById(R.id.now_playing_bar_artist);
        cover = (ImageView)  views.findViewById(R.id.now_playing_bar_cover);
        currentTime = (TextView)
                views.findViewById(R.id.now_playing_bar_current_time);

        updateNowPlayingBar(context);

        playButton = (ImageButton)
                views.findViewById(R.id.play_button);
        shuffle = (ImageButton)
                views.findViewById(R.id.shuffle);
        repeat = (ImageButton)
                views.findViewById(R.id.repeat);

        shuffle(context, data.isShuffled());
        repeat(context, data);

        //Initialize Navigation Drawer
        navigationDrawer = ((NavigationView)
                views.findViewById(R.id.navigation_drawer));
        navigationDrawer.setItemIconTintList(null);
        navigationDrawer.setNavigationItemSelectedListener(navigationItemSelectedListener);
        navigationDrawer.setTag(views.findViewById(R.id.drawer_layout));
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
        views.findViewById(R.id.multi_queue).setOnClickListener(onClickListener);
        views.findViewById(R.id.no_action_yet).setOnClickListener(onClickListener);
    }

    @Override
    public void updateUI(Player.Event event)
    {
        for(AudioListener listener : audioListeners)
        {
            switch (event)
            {
                case onStartAudio:
                    listener.onStartAudio();
                    break;

                case onStopAudio:
                    listener.onStopAudio();
                    break;
            }
        }
    }


    @Override
    public void updateTime(int time)
    {
        for(AudioListener listener : audioListeners)
        {
            if(listener instanceof Fragment)
            {
                if (((Fragment) listener).isVisible())
                    listener.onCurrentTimeUpdate(time);
            }
            else
                listener.onCurrentTimeUpdate(time);
        }
    }

    @Override
    public void onStartAudio()
    {
        togglePlayButtonIcon(true);
        updateNowPlayingBar(views);

        //TODO: More efficient current art loading
        new CurrentAlbumArtScanner().execute();
    }

    @Override
    public void onStopAudio()
    {
        togglePlayButtonIcon(false);
    }

    @Override
    public void onCurrentTimeUpdate(int time)
    {
        updateCurrentTime(time);
    }

    public void updateAppBar(String title, int icon)
    {
        appBarTitle.setText(title);
        appBarIcon.setImageResource(icon);
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
//        if(navigationDrawer.getMenu().findItem(R.id.navigation_drawer_songs).isChecked())
//            navigationDrawer.getMenu().findItem(R.id.navigation_drawer_songs).setChecked(false);
//
//        navigationDrawer.getMenu().findItem(fragmentManager.activeFragment).setChecked(true);
    }

    public void openNowPlayingBar()
    {
        fragmentManager.nowPlaying();
//        toggleControlButtons(false);
    }

    private void updateNowPlayingBar(Context context)
    {
        Song song = data.currentSong(context);

        if (data.currentSongIsNotNull())
        {
            cover.setVisibility(View.VISIBLE);
            currentTime.setVisibility(View.VISIBLE);
            currentTime.setText(Timestamper.getTimestamp(data.currentTime()));

            if(song.getCover() != null)
                cover.setImageDrawable(song.getCover());
            else
            {
                Glide.with(context)
                    .load((song.getCoverPath() != null)?
                            new File(song.getCoverPath()) :
                            R.drawable.album_art_placeholder)
                    .into(cover);
            }

            title.setText(song.getTitle());
            artist.setText(song.getArtist());

//            shuffle(context, data.isShuffled(), );
        }
        else
        {
            title.setText(greetings());
            artist.setText("Select a song to play.");
            cover.setVisibility(View.GONE);
            currentTime.setVisibility(View.GONE);
        }
    }

    public void updateCurrentTime(int time)
    {
        currentTime.setText(Timestamper.getTimestamp(time));
    }

    public void togglePlayButtonIcon(boolean isPlaying)
    {
        if (isPlaying)
            playButton.setImageResource(R.drawable.pause_24dp);
        else
            playButton.setImageResource(R.drawable.play_24dp);
    }

    public void shuffle(Context context, boolean toggle)
    {
        if(toggle)
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

    public void repeat(Context context, Data data)
    {
        switch(data.repeatState())
        {
            case OFF:
                repeat.setImageResource(R.drawable.repeat_24dp);
                repeat.setColorFilter(ContextCompat.getColor(context, R.color.toggle_off),
                        PorterDuff.Mode.SRC_ATOP);
                break;

            case ALL:
                repeat.setColorFilter(ContextCompat.getColor(context, R.color.toggle_on),
                        PorterDuff.Mode.SRC_ATOP);
                break;

            case ONE:
                repeat.setImageResource(R.drawable.repeat_one_24dp);
                repeat.setColorFilter(ContextCompat.getColor(context, R.color.toggle_on),
                        PorterDuff.Mode.SRC_ATOP);
                break;
        }
    }

//    public void setScrubAmount(Context context, final Data data)
//    {
//        AlertDialog.Builder dialog = Dialoger.getDialogBuilder(context);
//        dialog.setTitle("Scrub Amount");
//        dialog.setMessage("Fast forward or rewind by how many seconds?");
//
//        final NumberPicker numberPicker = new NumberPicker
//                (new ContextThemeWrapper(context, R.style.AppTheme));
//        numberPicker.setWrapSelectorWheel(true);
//        numberPicker.setMinValue(1);
//        numberPicker.setMaxValue(30);
//        numberPicker.setValue(data.scrubAmount());
//        dialog.setView(numberPicker);
//        dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener()
//        {
//            @Override
//            public void onPlay(DialogInterface dialog, int which)
//            {
//                data.updateScrubAmount(numberPicker.getValue());
//            }
//        });
//        dialog.setNegativeButton("Cancel", null);
//        dialog.show();
//    }

    private class CurrentAlbumArtScanner extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
//                data.updateCurrentAlbumArt
//                        (Drawable.createFromPath(data.currentSong(MainActivity.this)
//                                .getCoverPath()));

                data.updateCurrentAlbumArt(Glide
                        .with(views)
                        .load(new File(data.currentSong(views).getCoverPath()))
                        .into(700, 700).get());
            }
            catch (Exception e)
            {
                data.updateCurrentAlbumArt(ResourcesCompat
                        .getDrawable(views.getResources(), R.drawable.album_art_placeholder, null));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            if(fragmentManager.nowPlayingFragment.isVisible())
                fragmentManager.nowPlayingFragment.update(true);
        }
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
