package com.music.app.utils.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.mobeta.android.dslv.DragSortListView;
import com.music.app.R;
import com.music.app.objects.Data;
import com.music.app.objects.Player;
import com.music.app.objects.Song;
import com.music.app.utils.adapters.viewholders.QueueListViewHolder;

import java.util.ArrayList;

public class QueueAdapter extends BaseAdapter implements
        AdapterView.OnItemClickListener,
        DragSortListView.DropListener
{
    private Context context;

    private Data data;
    private Player player;

    private ArrayList<Long> queue;

    public QueueAdapter(Context context, Data data, Player player)
    {
        this.context = context;
        this.data = data;
        this.player = player;

        queue = data.queue().getQueue();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        QueueListViewHolder viewHolder;
        View view = convertView;

        if(view == null)
        {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        data.updateCurrentQueueIndex(position);
        player.startSong(getSongByID(queue.get(position)), true);
        notifyDataSetChanged();
    }

    @Override
    public void drop(int from, int to)
    {
        data.queue().reorderQueue(from, to);
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position)
    {
        return getSongByID(queue.get(position));
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

    private Song getSongByID(long id)
    {
        Song song = null;
        for(Song s : data.songs())
            if(s.getId() == id)
                song = s;
        return song;
    }
}
