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
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.music.app.R;
import com.music.app.objects.Data;
import com.music.app.objects.Player;
import com.music.app.objects.Song;
import com.music.app.utils.Timestamper;
import com.music.app.utils.UIManager;
import com.music.app.utils.interfaces.QueueListener;

import java.io.File;

public class NowPlayingFragment extends Fragment implements View.OnClickListener
{
    private Data data;
    private Player player;
    private UIManager uiManager;

    private SeekBar progress;
    private TextView time;
    private ImageView shuffle, repeat;
    private FloatingActionButton play;

    private QueueListener queueListener;

    public NowPlayingFragment() {}

    public void initialize(Data data,
                           Player player,
                           UIManager uiManager,
                           QueueListener queueListener)
    {
        this.data = data;
        this.player = player;
        this.uiManager = uiManager;
        this.queueListener = queueListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
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

        shuffle();
        repeat();

        Toolbar header = ((Toolbar) view.findViewById(R.id.header));
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
                        uiManager.fragmentManager().playQueue();
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
        initProgress();

        update(data.currentSong(getContext()), true);

//        Data.setNowPlayingFragment(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        togglePlayButtonIcon();
    }

    public void update(Song song, boolean updateProgress)
    {
        if(song != null)
        {
            ((TextView) getView()
                    .findViewById(R.id.now_playing_title)).setText(song.getTitle());
            ((TextView) getView()
                    .findViewById(R.id.now_playing_artist)).setText(song.getArtist());
            ((TextView) getView()
                    .findViewById(R.id.now_playing_album)).setText(song.getAlbum());
            ((TextView) getView()
                    .findViewById(R.id.now_playing_end_time)).setText(song.getDuration());

            ImageView cover = ((ImageView) getView().findViewById(R.id.now_playing_cover));
            Drawable currentAlbumArt = data.currentAlbumArt();
            if(currentAlbumArt != null)
                cover.setImageDrawable(currentAlbumArt);
            else Glide.with(getContext())
                    .load((song.getCoverPath() != null)?
                            new File(song.getCoverPath()) :
                            R.drawable.library_music_48dp)
                    .into(cover);

            if(updateProgress)
                resetProgress();
        }
    }

    private void initProgress()
    {
        //TODO: Better progressbar performance

        final MediaPlayer mediaPlayer = player.getPlayer();

        progress = (SeekBar) getView().findViewById(R.id.now_playing_progress_bar);
        time = (TextView) getView().findViewById(R.id.now_playing_start_time);

        progress.setPadding(0,0,0,0);
        progress.setProgress(0);
        progress.setMax(player.getPlayer().getDuration());
        progress.setProgress(0);
        progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if(fromUser)
                {
                    if(progress >= mediaPlayer.getDuration())
                        mediaPlayer.seekTo(mediaPlayer.getDuration());
                    else
                        mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
                player.play();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                player.play();
            }
        });
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

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.now_playing_play:
                player.play();
                togglePlayButtonIcon();
                uiManager.togglePlayButtonIcon(data.isPlaying());
                break;

            case R.id.now_playing_next:
                player.next();
                resetProgress();
                break;

            case R.id.now_playing_previous:
                player.previous();
                resetProgress();
                break;

            case R.id.now_playing_shuffle:
                uiManager.shuffle(getContext(), true, queueListener, data);
                shuffle();
                break;

            case R.id.now_playing_repeat:
                uiManager.repeat(getContext(), true, data);
                repeat();
                break;
        }
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
                repeat.setColorFilter(ContextCompat.getColor(getContext(), R.color.toggle_off), PorterDuff.Mode.SRC_ATOP);
                break;

            case ALL:
                repeat.setColorFilter(ContextCompat.getColor(getContext(), R.color.toggle_on), PorterDuff.Mode.SRC_ATOP);
                break;

            case ONE:
                repeat.setImageResource(R.drawable.repeat_one_36dp);
                repeat.setColorFilter(ContextCompat.getColor(getContext(), R.color.toggle_on), PorterDuff.Mode.SRC_ATOP);
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
