package com.music.app.fragments;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.music.app.R;
import com.music.app.interfaces.UIUpdatesListener;
import com.music.app.objects.Data;
import com.music.app.objects.Player;
import com.music.app.objects.Song;
import com.music.app.utils.Timestamper;
import com.music.app.utils.UIManager;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.File;

public class NowPlayingFragment extends Fragment implements
        View.OnClickListener,
        UIUpdatesListener
{
    private Data data;
    private Player player;
    private UIManager uiManager;

    private DiscreteSeekBar progress;
    private TextView time;
    private ImageView shuffle, repeat;
    private FloatingActionButton play;


    public NowPlayingFragment() {}

    public void initialize(Data data, Player player, UIManager uiManager)
    {
        this.data = data;
        this.player = player;
        this.uiManager = uiManager;
    }

    @Override
    public View onCreateView
            (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_now_playing, container, false);

        play = (FloatingActionButton) view.findViewById(R.id.now_playing_play);
        FloatingActionButton next = (FloatingActionButton)
                view.findViewById(R.id.now_playing_next);
        FloatingActionButton previous = (FloatingActionButton)
                view.findViewById(R.id.now_playing_previous);
        shuffle = (ImageView) view.findViewById(R.id.now_playing_shuffle);
        repeat = (ImageView) view.findViewById(R.id.now_playing_repeat);

        view.findViewById(R.id.now_playing_title).setSelected(true);
        view.findViewById(R.id.now_playing_artist).setSelected(true);
        view.findViewById(R.id.now_playing_album).setSelected(true);

        togglePlayButtonIcon();
        shuffle();
        repeat();

        Toolbar header = ((Toolbar) view.findViewById(R.id.now_playing_toolbar));
        header.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                uiManager.fragmentManager().popBackStack();
            }
        });
        header.inflateMenu(R.menu.menu_now_playing);
        header.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                switch(item.getItemId())
                {
                    case R.id.action_play_queue:
                        uiManager.fragmentManager().queue();
                        break;
                }
                return false;
            }
        });

        shuffle.setOnClickListener(this);
        repeat.setOnClickListener(this);
        play.setOnClickListener(this);
        next.setOnClickListener(this);
        previous.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        update(false);
    }

    @Override
    public void onStartAudio()
    {
        if(isVisible())
        {
            update(false);
            togglePlayButtonIcon();
        }
    }

    @Override public void onPlayOrPause() { if(isVisible()) togglePlayButtonIcon(); }
    @Override public void onStopAudio() { if(isVisible()) togglePlayButtonIcon(); }
    @Override public void onCurrentTimeUpdate(int time) { if(isVisible()) updateProgress(time); }
    @Override public void onShuffle() { if(isVisible()) shuffle(); }
    @Override public void onRepeat() { if(isVisible()) repeat(); }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.now_playing_play:
                player.play();
                break;

            case R.id.now_playing_next:
                resetProgress();
                player.next();
                break;

            case R.id.now_playing_previous:
                resetProgress();
                player.previous();
                break;

            case R.id.now_playing_shuffle:
                player.shuffle();
                break;

            case R.id.now_playing_repeat:
                player.repeat();
                break;
        }
    }

    public void update(boolean updateCoverOnly)
    {
        if(data.currentSongIsNotNull())
        {
            final Song song = data.currentSong(getContext());
            ImageView cover = ((ImageView) getView().findViewById(R.id.now_playing_cover));

            if(!updateCoverOnly)
            {
                ((ViewGroup) getView()).removeView(getView()
                        .findViewById(R.id.now_playing_start_message));

                ((TextView) getView()
                        .findViewById(R.id.now_playing_title)).setText(song.getTitle());
                ((TextView) getView()
                        .findViewById(R.id.now_playing_artist)).setText(song.getArtist());
                ((TextView) getView()
                        .findViewById(R.id.now_playing_album)).setText(song.getAlbum());
                ((TextView) getView()
                        .findViewById(R.id.now_playing_end_time)).setText
                        (Timestamper.getTimestamp(song.getDuration()));
                updateCover(cover, song);

                final MediaPlayer mediaPlayer = player.getPlayer();

                progress = (DiscreteSeekBar)
                        getView().findViewById(R.id.now_playing_progress_bar);
                time = (TextView)
                        getView().findViewById(R.id.now_playing_start_time);

                final int currentTime = (int) data.currentTime();
                final boolean currentTimeIsNotNull = currentTime != -1;
                time.setText(Timestamper
                        .getTimestamp((currentTimeIsNotNull) ? currentTime : 0));

                progress.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        progress.setMax((int) song.getDuration());
                        progress.setProgress((currentTimeIsNotNull) ? currentTime : 0);
                    }
                });

                progress.setOnProgressChangeListener
                        (new DiscreteSeekBar.OnProgressChangeListener()
                {
                    private boolean notPlaying;

                    @Override
                    public void onProgressChanged
                            (DiscreteSeekBar seekBar, int value, boolean fromUser)
                    {
                        if(fromUser)
                            seekBar.setIndicatorFormatter(Timestamper
                                    .getIndicatorTimestamp(value));
                    }

                    @Override
                    public void onStartTrackingTouch(DiscreteSeekBar seekBar)
                    {
                        if(player.getPlayer().isPlaying())
                        {
                            player.play();
                            notPlaying = false;
                        }
                        else
                            notPlaying = true;

                    }

                    @Override
                    public void onStopTrackingTouch(DiscreteSeekBar seekBar)
                    {
                        int value = seekBar.getProgress();
                        int duration = (int) song.getDuration();
                        boolean exceedingDuration = value >= duration;

                        mediaPlayer.seekTo(exceedingDuration ? duration : value);
                        time.setText(Timestamper
                                .getTimestamp(exceedingDuration ? duration : value));

                        data.updateCurrentTime(value);
                        uiManager.updateCurrentTime(value);

                        if(!notPlaying)
                            player.play();
                    }
                });
            }
            else
                updateCover(cover, song);
//            progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
//            {
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
//                {
//                    if(fromUser)
//                    {
//                        if(progress >= mediaPlayer.getDuration())
//                            mediaPlayer.seekTo(mediaPlayer.getDuration());
//                        else
//                            mediaPlayer.seekTo(progress);
//                    }
//                }
//
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar)
//                {
//                    player.play();
//                }
//
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar)
//                {
//                    player.play();
//                }
//            });
        }
        else
            getView().findViewById(R.id.now_playing_start_message).setVisibility(View.VISIBLE);
    }

    private void updateCover(ImageView cover, Song song)
    {
        Drawable currentAlbumArt = data.currentAlbumArt();
        if(currentAlbumArt != null)
            cover.setImageDrawable(currentAlbumArt);
        else Glide.with(getContext())
                .load((song.getCoverPath() != null)?
                        new File(song.getCoverPath()) :
                        R.drawable.album_art_placeholder)
                .into(cover);
    }

    public void updateProgress(int currentTime)
    {
        progress.setProgress(currentTime);
        time.setText(Timestamper.getTimestamp(currentTime));
    }

    private void resetProgress()
    {
        progress.setMax(player.getPlayer().getDuration());
        progress.setProgress(0);
        time.setText(Timestamper.getTimestamp(0));
    }

    private void shuffle()
    {
        if(data.isShuffled())
            shuffle.setColorFilter
                (
                    ContextCompat.getColor(getContext(),
                    R.color.toggle_on),
                    PorterDuff.Mode.SRC_ATOP
                );
        else
            shuffle.setColorFilter
                (
                    ContextCompat.getColor(getContext(),
                    R.color.toggle_off),
                    PorterDuff.Mode.SRC_ATOP
                );
    }

    private void repeat()
    {
        switch(data.repeatState())
        {
            case OFF:
                repeat.setImageResource(R.drawable.repeat_36dp);
                repeat.setColorFilter(ContextCompat.getColor(getContext(), R.color.toggle_off),
                        PorterDuff.Mode.SRC_ATOP);
                break;

            case ALL:
                repeat.setColorFilter(ContextCompat.getColor(getContext(), R.color.toggle_on),
                        PorterDuff.Mode.SRC_ATOP);
                break;

            case ONE:
                repeat.setImageResource(R.drawable.repeat_one_36dp);
                repeat.setColorFilter(ContextCompat.getColor(getContext(), R.color.toggle_on),
                        PorterDuff.Mode.SRC_ATOP);
                break;
        }
    }

    public void togglePlayButtonIcon()
    {
        if(data.isPlaying())
            play.setImageResource(R.drawable.pause_36dp);
        else
            play.setImageResource(R.drawable.play_36dp);
    }
}


//TODO: Try Chronometer
//    private Chronometer time;
//time = (Chronometer) getView().findViewById(R.id.now_playing_start_time);
//                time.setBase((SystemClock.elapsedRealtime() -
//                        player.getPlayer().getDuration()) +
//                        (player.getPlayer().getDuration() -
//                        player.getPlayer().getCurrentPosition()));
//                time.start();