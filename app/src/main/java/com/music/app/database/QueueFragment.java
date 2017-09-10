package com.music.app.database;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobeta.android.dslv.DragSortListView;
import com.music.app.R;
import com.music.app.objects.Data;
import com.music.app.utils.adapters.QueueAdapter;
import com.music.app.interfaces.ServiceListener;

public class QueueFragment extends Fragment
{
    private Data data;

    private DragSortListView queueList;
    private QueueAdapter adapter;

    private ServiceListener serviceListener;

    public QueueFragment() {}

    public void initialize(Data data)
    {
        this.data = data;
    }

    public void setServiceListener(ServiceListener serviceListener)
    {
        this.serviceListener = serviceListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        adapter = new QueueAdapter(getContext(), data);
        adapter.setServiceListener(serviceListener);

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

    public void onSongChanged()
    {
        adapter.notifyDataSetChanged();
    }

    private void scrollToPlaying()
    {
        final int playing = data.currentQueueIndex();
        if(playing != -1)
            queueList.post(new Runnable()
            {
                @Override
                public void run()
                {
                    queueList.setSelection(playing);
                }
            });
    }
}
