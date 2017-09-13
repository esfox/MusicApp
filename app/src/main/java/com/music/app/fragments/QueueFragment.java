package com.music.app.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobeta.android.dslv.DragSortListView;
import com.music.app.R;
import com.music.app.interfaces.UIUpdatesListener;
import com.music.app.objects.Data;
import com.music.app.objects.Player;
import com.music.app.adapters.QueueAdapter;

public class QueueFragment extends Fragment implements
        UIUpdatesListener
{
    private Data data;
    private Player player;

    private DragSortListView queueList;
    private QueueAdapter adapter;

    public QueueFragment() {}

    public void initialize(Data data, Player player)
    {
        this.data = data;
        this.player = player;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        adapter = new QueueAdapter(data, player);

        View view = inflater.inflate(R.layout.fragment_queue, container, false);
        queueList = (DragSortListView) view.findViewById(R.id.queue_list);
        queueList.setDividerHeight(0);
        queueList.setAdapter(adapter);
        queueList.setDropListener(adapter);
        queueList.setOnItemClickListener(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        scrollToPlaying();
    }

    @Override
    public void onStartAudio()
    {
        if(isVisible())
            updateAdapter();
    }

    @Override
    public void onShuffle()
    {
        if(isVisible())
            updateAdapter();
    }

    @Override public void onPlayOrPause() {}
    @Override public void onStopAudio() {}
    @Override public void onCurrentTimeUpdate(int time) {}
    @Override public void onRepeat() {}

    private void updateAdapter()
    {
        adapter.notifyDataSetChanged();
        scrollToPlaying();
    }

    private void scrollToPlaying()
    {
        final int playing = data.currentQueueIndex();
        if(playing != -1)
        {
            queueList.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if(playing < queueList.getFirstVisiblePosition() ||
                       playing > queueList.getLastVisiblePosition())
                    {
                        queueList.setSelection(playing);
                    }
                }
            });
        }
    }
}
