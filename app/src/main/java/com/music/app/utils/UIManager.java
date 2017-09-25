package com.music.app.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.music.app.R;
import com.music.app.adapters.SongListAdapter;
import com.music.app.fragments.FragmentManager;
import com.music.app.interfaces.AudioListener;
import com.music.app.interfaces.CurrentAlbumArtScannerListener;
import com.music.app.interfaces.UIUpdatesListener;
import com.music.app.objects.Data;
import com.music.app.objects.Player;
import com.music.app.objects.Song;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.File;
import java.util.Calendar;

public class UIManager implements
        UIUpdatesListener,
        AudioListener,
        CurrentAlbumArtScannerListener,
        View.OnClickListener,
        View.OnLongClickListener
{
    private Activity views;
    private Data data;
    private Player player;

    private Song song;

    private FragmentManager fragmentManager;

    private ImageButton playButton, shuffle, repeat, actionButton;
    private NavigationView navigationDrawer;
    private TextView title, artist;
    private TextView currentTime, appBarTitle;
    private ImageView cover, appBarIcon;
    private DiscreteSeekBar seekBar;
    private View seekBarLayout;

    //TODO: Update fragments only when visible
    private UIUpdatesListener[] UIUpdatesListeners;

    public UIManager(Activity activity)
    {
        views = activity;
    }

    public FragmentManager fragmentManager() { return fragmentManager; }

    private void updateCurrentSong()
    {
        if(data.currentSongIsNotNull())
            song = data.currentSong();

        if(data.currentAlbumArt() == null)
            new CurrentAlbumArtScanner(this).execute(views, song);
    }

    public void initUI(Context context, Data data, Player player)
    {
        this.data = data;
        this.player = player;

        updateCurrentSong();

        fragmentManager = new FragmentManager(context, this, data, player);
        UIUpdatesListeners = fragmentManager.getUIUpdatesListeners();

        //Initialize App Bar
        appBarTitle = (TextView) views.findViewById(R.id.toolbar_title);
        appBarIcon = (ImageView) views.findViewById(R.id.toolbar_icon);

        //Initialize Now Playing Bar
        title = (TextView) views.findViewById(R.id.now_playing_bar_title);
        artist = (TextView) views.findViewById(R.id.now_playing_bar_artist);
        cover = (ImageView)  views.findViewById(R.id.now_playing_bar_cover);
        currentTime = (TextView)
                views.findViewById(R.id.now_playing_bar_current_time);
        updateNowPlayingBar();

        playButton = (ImageButton) views.findViewById(R.id.play_button);
        shuffle = (ImageButton) views.findViewById(R.id.shuffle);
        repeat = (ImageButton) views.findViewById(R.id.repeat);
        actionButton = (ImageButton) views.findViewById(R.id.action_button);
        seekBarLayout = views.findViewById(R.id.seekbar_layout);
        seekBar = (DiscreteSeekBar) views.findViewById(R.id.seekbar);
        updateNowPlayingBarFunctionButton(R.id.navigation_drawer_songs);
        initializeSeekbar();

        shuffle();
        repeat();

        //Initialize Navigation Drawer
        navigationDrawer = ((NavigationView)
                views.findViewById(R.id.navigation_drawer));
        navigationDrawer.setItemIconTintList(null);
        navigationDrawer.setTag(views.findViewById(R.id.drawer_layout));

        updateNavigationDrawer();

        setClickListeners();
    }

    private void setClickListeners()
    {
        navigationDrawer.setNavigationItemSelectedListener(fragmentManager);

        playButton.setOnClickListener(this);
        playButton.setOnLongClickListener(this);

        View nextButton = views.findViewById(R.id.next_button);
        View previousButton = views.findViewById(R.id.previous_button);
        nextButton.setOnClickListener(this);
        nextButton.setOnLongClickListener(this);
        previousButton.setOnClickListener(this);
        previousButton.setOnLongClickListener(this);

        //        View scrubForward = views.findViewById(R.id.scrub_forward_button);
//        View scrubBackward = views.findViewById(R.id.scrub_backward_button);
//        scrubForward.setOnClickListener(onClickListener);
//        scrubForward.setOnLongClickListener(onLongClickListener);
//        scrubBackward.setOnClickListener(onClickListener);
//        scrubBackward.setOnLongClickListener(onLongClickListener);

        shuffle.setOnClickListener(this);
        repeat.setOnClickListener(this);

        views.findViewById(R.id.no_function_yet).setOnClickListener(this);
        views.findViewById(R.id.close_seekbar).setOnClickListener(this);

        views.findViewById(R.id.toolbar_icon).setOnClickListener(this);
        views.findViewById(R.id.toolbar_menu).setOnClickListener(this);
//        views.findViewById(R.id.toolbar_sort).setOnClickListener(this);

        views.findViewById(R.id.now_playing_bar).setOnClickListener(this);
        actionButton.setOnClickListener(this);
        views.findViewById(R.id.no_function_yet).setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.play_button:
                player.play();
                break;

            case R.id.next_button:
                player.next();
                break;

            case R.id.previous_button:
                player.previous();
                break;

//            case R.id.scrub_forward_button:
//                player.scrub(true);
//                uiManager.updateCurrentTime(false);
//                break;
//
//            case R.id.scrub_backward_button:
//                player.scrub(false);
//                uiManager.updateCurrentTime(false);
//                break;

            case R.id.shuffle:
                player.shuffle();
                break;

            case R.id.repeat:
                player.repeat();
                break;

            case R.id.action_button:
                actionButton();
                break;

            case R.id.no_function_yet:
//                Notice notice = new Notice(views);
//                notice.setNoticeText("This button has no use yet.");
//                notice.show();
                break;

            case R.id.close_seekbar:
                toggleSeekbar(true);
                break;

            case R.id.now_playing_bar:
                toggleSeekbar(false);
                openNowPlayingBar();
                break;

            case R.id.toolbar_icon:
                toggleNavigationDrawer(true);
                break;

            case R.id.toolbar_menu:
                fragmentManager.getSongListFragment().menu(v);
//                lol(v);
                break;

//            case R.id.toolbar_sort:
//                fragmentManager.getSongListFragment().sortOptions(v);
        }
    }

    @SuppressLint("InflateParams")
    @Override
    public boolean onLongClick(View v)
    {
        switch(v.getId())
        {
            case R.id.play_button:
                player.stop();
                break;

            case R.id.next_button:
            case R.id.previous_button:
                toggleSeekbar(true);
                break;

//            case R.id.scrub_forward_button:
//            case R.id.scrub_backward_button:
//                uiManager.setScrubAmount(this, data);
//                break;
        }
        return true;
    }

    @Override
    public void updateUI(Player.Event event)
    {
        for(UIUpdatesListener listener : UIUpdatesListeners)
        {
            switch (event)
            {
                case onStartAudio:
                    listener.onStartAudio();
                    break;

                case onPlayOrPause:
                    listener.onPlayOrPause();
                    break;

                case onStopAudio:
                    listener.onStopAudio();
                    break;

                case onShuffle:
                    listener.onShuffle();
                    break;

                case onRepeat:
                    listener.onRepeat();
                    break;
            }
        }
    }

    @Override
    public void updateTime(int time)
    {
        for(UIUpdatesListener listener : UIUpdatesListeners)
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
        updateCurrentSong();
        togglePlayButtonIcon();
        updateNowPlayingBar();

        //TODO: More efficient current art loading
//        new CurrentAlbumArtScanner(new WeakReference<>(views), this, song);
        new CurrentAlbumArtScanner(this).execute(views, song);
    }

    @Override public void onPlayOrPause() { togglePlayButtonIcon(); }
    @Override public void onStopAudio() { togglePlayButtonIcon(); }
    @Override public void onCurrentTimeUpdate(int time) { updateCurrentTime(time); }
    @Override public void onShuffle() { shuffle(); }
    @Override public void onRepeat() { repeat(); }

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
            ((DrawerLayout) navigationDrawer.getTag()).closeDrawer(GravityCompat.START);
    }

    public void updateNavigationDrawer()
    {
        final int menuID = fragmentManager.getActiveFragmentID();
        navigationDrawer.post(new Runnable()
        {
            @Override
            public void run()
            {
                if(menuID == 0)
                    navigationDrawer.setCheckedItem(R.id.navigation_drawer_invisible);
                else
                    navigationDrawer.setCheckedItem(menuID);
            }
        });
    }

    private void openNowPlayingBar() { fragmentManager.nowPlaying(); }

    private void updateNowPlayingBar()
    {
        if (data.currentSongIsNotNull())
        {
            cover.setVisibility(View.VISIBLE);
            currentTime.setVisibility(View.VISIBLE);
            currentTime.setText(Timestamper.getTimestamp(data.currentTime()));

            if(song.getCover() != null)
                cover.setImageDrawable(song.getCover());
            else
                if(song.getCoverPath() != null)
                    Glide.with(views)
                        .load(new File(song.getCoverPath()))
                        .placeholder(R.drawable.album_art_placeholder)
                        .error(R.drawable.album_art_placeholder)
                        .into(cover);

            title.setTag(song.getTitle());
            artist.setTag(song.getArtist());

            for(final TextView textView : new TextView[] {title, artist})
            {
                textView.setText(textView.getTag().toString());
                textView.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                        textView.setSingleLine(true);
                    }
                });
            }

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

    private void actionButton()
    {
        SongListAdapter adapter = (SongListAdapter) fragmentManager.getSongListFragment()
                .getSongList().getAdapter();

        int fragmentID = (int) actionButton.getTag();

        if(fragmentID == R.id.navigation_drawer_songs)
            adapter.toggleMultiQueueMode(!adapter.isInMultiQueueMode());
        else
            fragmentManager.popBackStack();
    }

    public void updateNowPlayingBarFunctionButton(int activeFragmentID)
    {
        if(activeFragmentID != R.id.navigation_drawer_songs)
            actionButton.setImageResource(R.drawable.back_arrow_24dp);
        else
            actionButton.setImageResource(R.drawable.playlist_add_24dp);

        actionButton.setTag(activeFragmentID);
    }

    private void initializeSeekbar()
    {
        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener()
        {
            private boolean notPlaying;

            @Override
            public void onProgressChanged
                    (DiscreteSeekBar seekBar, int value, boolean fromUser)
            {
                String time = Timestamper.getSeekbarTimestamp(value);
                if (fromUser) seekBar.setIndicatorFormatter(time);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar)
            {
                Player player = UIManager.this.player;
                if (player.getPlayer().isPlaying())
                {
                    player.play();
                    notPlaying = false;
                } else notPlaying = true;

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar)
            {
                Player player = UIManager.this.player;

                int value = seekBar.getProgress();
                int duration = (int) song.getDuration();
                boolean exceedingDuration = value >= duration;

                player.getPlayer().seekTo(exceedingDuration ? duration : value);
                String time = Timestamper.getTimestamp
                        (exceedingDuration ? duration : value);
                currentTime.setText(time);

                UIManager.this.data.updateCurrentTime(value);
                updateCurrentTime(value);

                if (!notPlaying) player.play();
            }
        });
    }

    private void toggleSeekbar(boolean toggle)
    {
        if(data.currentSongIsNotNull())
        {
            if(toggle)
            {
                if(seekBarLayout.getVisibility() == View.GONE)
                {
                    seekBarLayout.setVisibility(View.VISIBLE);
                    if(data.currentSongIsNotNull())
                    {
                        seekBar.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                seekBar.setProgress(player.getPlayer().getCurrentPosition());
                                seekBar.setMax((int) song.getDuration());
                            }
                        });
                    }
                }
                else
                    seekBarLayout.setVisibility(View.GONE);
            }
            else
                seekBarLayout.setVisibility(View.GONE);
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
//            public void onItemClick(DialogInterface dialog, int which)
//            {
//                data.updateScrubAmount(numberPicker.getValue());
//            }
//        });
//        dialog.setNegativeButton("Cancel", null);
//        dialog.show();
//    }

    private void updateCurrentTime(int time)
    {
        currentTime.setText(Timestamper.getTimestamp(time));
        if(seekBarLayout.getVisibility() == View.VISIBLE)
            seekBar.setProgress(time);
    }

    private void togglePlayButtonIcon()
    {
        if (data.isPlaying())
            playButton.setImageResource(R.drawable.pause_24dp);
        else
            playButton.setImageResource(R.drawable.play_24dp);
    }

    private void shuffle()
    {
        if(data.isShuffled())
            shuffle.setColorFilter
                (
                    ContextCompat.getColor(views,
                            R.color.toggle_on),
                    PorterDuff.Mode.SRC_ATOP
                );
        else
            shuffle.setColorFilter
                (
                    ContextCompat.getColor(views,
                            R.color.toggle_off),
                    PorterDuff.Mode.SRC_ATOP
                );
    }

    private void repeat()
    {
        switch(data.repeatState())
        {
            case OFF:
                repeat.setImageResource(R.drawable.repeat_24dp);
                repeat.setColorFilter(ContextCompat.getColor(views, R.color.toggle_off),
                        PorterDuff.Mode.SRC_ATOP);
                break;

            case ALL:
                repeat.setColorFilter(ContextCompat.getColor(views, R.color.toggle_on),
                        PorterDuff.Mode.SRC_ATOP);
                break;

            case ONE:
                repeat.setImageResource(R.drawable.repeat_one_24dp);
                repeat.setColorFilter(ContextCompat.getColor(views, R.color.toggle_on),
                        PorterDuff.Mode.SRC_ATOP);
                break;
        }
    }

    @Override
    public void onScanComplete(Drawable cover)
    {
        data.updateCurrentAlbumArt(cover);
        new Handler(Looper.getMainLooper()).post(new Runnable()
        {
            @Override
            public void run()
            {
                fragmentManager.updateNowPlayingCover();
            }
        });
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


//    private void lol(View button)
//    {
//        final boolean[] mBooleanIsPressed = {false};
//        final Handler handler = new Handler();
//        final Runnable runnable = new Runnable()
//        {
//            public void run()
//            {
//                if(mBooleanIsPressed[0])
//                {
//                    ((ViewStub) views.findViewById(R.id.lol)).inflate();
//                    new Handler().postDelayed(new Runnable()
//                    {
//                        @Override
//                        public void run()
//                        {
//                            AlertDialog.Builder builder = Dialoger.getDialogBuilder(views);
//                            builder.setMessage("WHAT DID YOU DO?");
//                            builder.setPositiveButton("I didn't do anything!",
//                                    new DialogInterface.OnClickListener()
//                            {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which)
//                                {
//                                    throw null;
//                                }
//                            });
//                            AlertDialog dialog = builder.create();
//                            dialog.setCanceledOnTouchOutside(false);
//                            dialog.show();
//                        }
//                    }, 4000);
//                }
//            }
//        };
//
//        button.setOnTouchListener(new View.OnTouchListener()
//        {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event)
//            {
//                if(event.getAction() == MotionEvent.ACTION_DOWN)
//                {
//                    handler.postDelayed(runnable, 5000);
//                    mBooleanIsPressed[0] = true;
//                }
//
//                return false;
//            }
//        });
//    }
}
