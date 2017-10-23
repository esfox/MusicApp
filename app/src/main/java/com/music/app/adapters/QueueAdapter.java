package com.music.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.mobeta.android.dslv.DragSortListView;
import com.music.app.R;
import com.music.app.adapters.viewholders.QueueListViewHolder;
import com.music.app.objects.Data;
import com.music.app.objects.Player;
import com.music.app.objects.Song;

import java.util.ArrayList;

public class QueueAdapter extends BaseAdapter implements
        AdapterView.OnItemClickListener,
        DragSortListView.DropListener
{
    private Data data;
    private Player player;

    private ArrayList<Long> queue;

    public QueueAdapter(Data data, Player player)
    {
        this.data = data;
        this.player = player;

        queue = player.queue().getQueueList();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        QueueListViewHolder viewHolder;
        View view = convertView;

        if(view == null)
        {
            LayoutInflater inflater = (LayoutInflater)
                    parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.queue_list_item, parent, false);

            viewHolder = new QueueListViewHolder(view);
            view.setTag(viewHolder);
        }
        else
            viewHolder = (QueueListViewHolder) view.getTag();

        viewHolder.setSongDetails((Song) getItem(position));
        if(position == data.currentQueueIndex())
            viewHolder.setPlaying();

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        player.queue().newSongFromQueue(data.currentQueueIndex(), position);
        data.updateCurrentQueueIndex(position);
        player.startSong(Song.getSongByID(queue.get(position), data.songs()), false);
        notifyDataSetChanged();
    }

    @Override
    public void drop(int from, int to)
    {
        player.queue().reorderQueue(from, to);
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position)
    {
        return Song.getSongByID(queue.get(position), data.songs());
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getCount()
    {
        return queue.size();
    }
}
