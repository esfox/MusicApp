package com.music.app.fragments;


import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.music.app.MainActivity;
import com.music.app.R;
import com.music.app.objects.Data;
import com.music.app.objects.PlayQueue;
import com.music.app.utils.ItemTouchHelperCallback;
import com.music.app.utils.adapters.PlayQueueAdapterOld;
import com.music.app.utils.interfaces.OnStartDragListener;
import com.music.app.views.RecyclerViewFastScroller;

public class PlayQueueFragment extends Fragment implements OnStartDragListener
{
    private View view;
    private Menu menu;
    private static PlayQueueAdapterOld playQueueAdapterOld;
    private ItemTouchHelper touchHelper;
    private RecyclerView playQueue;

    private Data data;

    public PlayQueueFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_play_queue, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        data = ((MainActivity) getContext()).data;

        assert getView() != null;
        view = getView();
        Toolbar toolbar = (Toolbar) getView().findViewById(R.id.play_queue_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        toolbar.inflateMenu(R.menu.menu_play_queue);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                switch(item.getItemId())
                {
                    case R.id.action_shuffle:
                        data.updateIsShuffled(!data.isShuffled());
                        PlayQueue.shuffle();
                        update();
                        scrollToPlaying();
                        shuffle();
                        //TODO: Update NowPlayingFragment
                        // Data.updateNowPlayingFragment();
                        break;
                    case R.id.action_repeat:
                        data.updateRepeatState();
                        repeat();
                        //TODO: Update NowPlayingFragment
                        //Data.updateNowPlayingFragment();
                        break;
                }
                return false;
            }
        });

        view.findViewById(R.id.queue_list_play_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((MainActivity) getContext()).player.play();
                togglePlayButtonIcon();
            }
        });

        menu = toolbar.getMenu();
        updateNowPlaying();
        shuffle();
        repeat();

        togglePlayButtonIcon();

        //TODO: Replace with DragListView

        playQueue = (RecyclerView) getView().findViewById(R.id.play_queue);
        playQueue.setHasFixedSize(true);
        playQueue.setLayoutManager(new LinearLayoutManager(getContext()));

        RecyclerViewFastScroller fastScroller = (RecyclerViewFastScroller) getView().findViewById(R.id.play_queue_fast_scroller);
        fastScroller.setRecyclerView(playQueue);
        fastScroller.setViewsToUse(R.layout.recycler_view_fast_scroller, R.id.fastscroller_bubble, R.id.fastscroller_handle);

        playQueueAdapterOld = new PlayQueueAdapterOld(getContext(), PlayQueue.queue, this);
        playQueue.setAdapter(playQueueAdapterOld);

        touchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(playQueueAdapterOld));
        touchHelper.attachToRecyclerView(playQueue);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        scrollToPlaying();
    }

    public void updateNowPlaying()
    {
        if(Data.currentSong != null)
        {
            ((TextView) view.findViewById(R.id.play_queue_title)).setText(Data.currentSong.getTitle());
            ((TextView) view.findViewById(R.id.play_queue_artist)).setText(Data.currentSong.getArtist());
            ((ImageView) view.findViewById(R.id.play_queue_cover)).setImageDrawable(Data.currentSong.getCover());
        }
    }

    public static void update()
    {
        if(playQueueAdapterOld != null)
            playQueueAdapterOld.update(PlayQueue.queue);
    }

    private void shuffle()
    {
        if(data.isShuffled())
            menu.findItem(R.id.action_shuffle)
                    .getIcon()
                    .setColorFilter(ContextCompat.getColor(getContext(), R.color.toggle_on), PorterDuff.Mode.SRC_ATOP);
        else
            menu.findItem(R.id.action_shuffle)
                    .getIcon()
                    .setColorFilter(ContextCompat.getColor(getContext(), R.color.toggle_off_light), PorterDuff.Mode.SRC_ATOP);
    }

    private void repeat()
    {
        switch(data.repeatState())
        {
            case OFF:
                menu.findItem(R.id.action_repeat).setIcon(R.drawable.repeat_36dp);
                menu.findItem(R.id.action_repeat).getIcon().setColorFilter(ContextCompat.getColor(getContext(), R.color.toggle_off_light), PorterDuff.Mode.SRC_ATOP);
                break;

            case ALL:
                menu.findItem(R.id.action_repeat).getIcon().setColorFilter(ContextCompat.getColor(getContext(), R.color.toggle_on), PorterDuff.Mode.SRC_ATOP);
                break;

            case ONE:
                menu.findItem(R.id.action_repeat).setIcon(R.drawable.repeat_one_36dp);
                menu.findItem(R.id.action_repeat).getIcon().setColorFilter(ContextCompat.getColor(getContext(), R.color.toggle_on), PorterDuff.Mode.SRC_ATOP);
                break;
        }
    }

    public void togglePlayButtonIcon()
    {
//        FloatingActionButton playButton = (FloatingActionButton) view.findViewById(R.id.queue_list_play_button);
//
//        if(Player.getPlayer().isPlaying())
//            playButton.setImageResource(R.drawable.pause_36dp);
//        else
//            playButton.setImageResource(R.drawable.play_36dp);
//
//        ((MainActivity) getActivity()).togglePlayButtonIcon();
    }

    private void scrollToPlaying()
    {
        if(data.currentSongQueueIndex() != -1)
            playQueue.scrollToPosition(data.currentSongQueueIndex());
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder holder)
    {
        touchHelper.startDrag(holder);
    }
}