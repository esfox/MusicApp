package com.music.app.fragments;

import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.music.app.MainActivity;
import com.music.app.R;
import com.music.app.objects.Data;
import com.music.app.objects.PlayQueue;
import com.music.app.objects.Player;
import com.music.app.objects.Song;

public class NowPlayingFragment extends Fragment implements View.OnClickListener
{
    private Player player;
    private Data data;

    private SeekBar progress;
    private TextView time;
    private Handler updater;
    private ImageView shuffle, repeat;
    private FloatingActionButton play;

    public NowPlayingFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_now_playing, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        player = ((MainActivity) getContext()).player;
        data = ((MainActivity) getContext()).data;

        play = (FloatingActionButton) getView().findViewById(R.id.now_playing_play);
        FloatingActionButton next = (FloatingActionButton) getView().findViewById(R.id.now_playing_next);
        FloatingActionButton previous = (FloatingActionButton) getView().findViewById(R.id.now_playing_previous);
        shuffle = (ImageView) getView().findViewById(R.id.now_playing_shuffle);
        repeat = (ImageView) getView().findViewById(R.id.now_playing_repeat);

        getView().findViewById(R.id.now_playing_title).setSelected(true);
        getView().findViewById(R.id.now_playing_artist).setSelected(true);
        getView().findViewById(R.id.now_playing_album).setSelected(true);

        shuffle(false);
        repeat(false);

        Toolbar header = ((Toolbar) getView().findViewById(R.id.header));
        header.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((MainActivity) getContext()).fragmentManager.popBackStack();
//                ((MainActivity) getContext()).uiManager.toggleControlButtons(true);
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
                        ((MainActivity) getContext()).fragmentManager.playQueue();
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

        updater = new Handler();
        initProgress();
        update(true);

//        Data.setNowPlayingFragment(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        togglePlayButtonIcon(data.isPlaying());
    }

    private void initProgress()
    {
        final MediaPlayer mediaPlayer = player.getPlayer();

        progress = (SeekBar) getView().findViewById(R.id.now_playing_progress_bar);
        time = (TextView) getView().findViewById(R.id.now_playing_start_time);

        progress.setPadding(0,0,0,0);
        progress.setProgress(mediaPlayer.getCurrentPosition());
        progress.setMax(mediaPlayer.getDuration());
        progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if(fromUser)
                {
                    Log.d("Duration", String.valueOf(mediaPlayer.getDuration()));
                    Log.d("Progress", String.valueOf(progress));

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
                updateProgress();
            }
        });

        updateProgress();
    }

    private void updateProgress()
    {
        if(Data.currentSong != null)
        {
            progress.setProgress(player.getPlayer().getCurrentPosition());
            time.setText(getDuration(player.getPlayer().getCurrentPosition()));
        }
        updater.postDelayed(update, 1000);
    }

    Runnable update = new Runnable()
    {
        @Override
        public void run()
        {
            updateProgress();
        }
    };

    public void update(boolean updateProgress)
    {
        Song song = Data.currentSong;
        if(song != null)
        {
            ((TextView) getView().findViewById(R.id.now_playing_title)).setText(song.getTitle());
            ((TextView) getView().findViewById(R.id.now_playing_artist)).setText(song.getArtist());
            ((TextView) getView().findViewById(R.id.now_playing_album)).setText(song.getAlbum());
            ((ImageView) getView().findViewById(R.id.now_playing_cover)).setImageDrawable(Data.currentAlbumArt);
            ((TextView) getView().findViewById(R.id.now_playing_end_time)).setText(song.getDuration());

            if(updateProgress)
            {
                progress.setMax(player.getPlayer().getDuration());
                updater.removeCallbacks(this.update);
                updateProgress();
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.now_playing_play:
                player.play();
                togglePlayButtonIcon(data.isPlaying());
                ((MainActivity) getContext()).uiManager.togglePlayButtonIcon(data.isPlaying());
                break;

            case R.id.now_playing_next:
                player.next();
                break;

            case R.id.now_playing_previous:
                player.previous();
                break;

            case R.id.now_playing_shuffle:
                shuffle(true);
                break;

            case R.id.now_playing_repeat:
                repeat(true);
                break;
        }
    }

    public void shuffle(boolean toggle)
    {
        if(toggle)
        {
            data.updateIsShuffled(!data.isShuffled());
            PlayQueue.shuffle();
        }

        if(data.isShuffled())
            shuffle.setColorFilter(ContextCompat.getColor(getContext(), R.color.toggle_on), PorterDuff.Mode.SRC_ATOP);
        else
            shuffle.setColorFilter(ContextCompat.getColor(getContext(), R.color.toggle_off), PorterDuff.Mode.SRC_ATOP);
    }

    public void repeat(boolean toggle)
    {
        if(toggle)
            data.updateRepeatState();

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

    public void togglePlayButtonIcon(boolean isPlaying)
    {
        if(isPlaying)
            play.setImageResource(R.drawable.pause_36dp);
        else
            play.setImageResource(R.drawable.play_36dp);
    }

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
        {
            duration = hours + ":";
        }

        // Prepending 0 to seconds if it is ONE digit
        if(seconds < 10)
            secondsString = "0" + seconds;
        else
            secondsString = "" + seconds;

        duration = duration + minutes + ":" + secondsString;

        return duration;
    }
}
