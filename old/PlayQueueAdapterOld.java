package com.music.app.unused;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.music.app.R;
import com.music.app.objects.Data;
import com.music.app.objects.Song;
import com.music.app.interfaces.DragListener;
import com.music.app.views.RecyclerViewFastScroller;

import java.util.ArrayList;

public class PlayQueueAdapterOld
        extends RecyclerView.Adapter<PlayQueueAdapterOld.PlayQueueViewHolder>
        implements RecyclerViewFastScroller.BubbleTextGetter/*, ItemTouchListener*/
{
    private long[] songIDs;
//    private OnStartDragListener dragListener;

    private Data data;

    public PlayQueueAdapterOld(Data data/*, OnStartDragListener dragListener*/)
    {
        this.data = data;
//        this.dragListener = dragListener;

        ArrayList<Long> ids = data.queue().getQueueList();
        songIDs = new long[ids.size()];
        for(int i = 0; i < songIDs.length; i++)
            songIDs[i] = ids.get(i);
    }

    private Song getSongByID(long id)
    {
        Song song = null;
        for(Song s : data.songs())
            if(s.getId() == id)
                song = s;
        return song;
    }

    @Override
    public PlayQueueViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.play_queue_item, parent, false);
        return new PlayQueueViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final PlayQueueViewHolder holder, int position)
    {
        Song song = getSongByID(songIDs[position]);

        holder.title.setText(song.getTitle());
        holder.artist.setText(song.getArtist());
        holder.cover.setImageDrawable(song.getCover());

        if(position == data.currentQueueIndex())
            holder.background.setBackgroundResource(R.color.colorPrimaryDarker);
        else
            holder.background.setBackgroundResource(R.color.background_primary);
    }

    @Override
    public int getItemCount()
    {
        return songIDs.length;
    }

    @Override
    public String getTextToShowInBubble(int pos)
    {
        return Character.toString(getSongByID(songIDs[pos])
                .getTitle().charAt(0));
    }

    public void update()
    {
        ArrayList<Long> idsList = data.queue().getQueueList();
        songIDs = new long[idsList.size()];
        for(int i = 0; i < songIDs.length; i++)
            songIDs[i] = idsList.get(i);
        notifyDataSetChanged();
    }

//    @Override
//    public void onItemMove(int fromPosition, int toPosition)
//    {
//        if (fromPosition < toPosition)
//        {
//            for (int i = fromPosition; i < toPosition; i++)
//            {
//                Collections.swap(songIDs, i, i + 1);
//            }
//        }
//        else
//        {
//            for (int i = fromPosition; i > toPosition; i--)
//            {
//                Collections.swap(songIDs, i, i - 1);
//            }
//        }
//
//        notifyItemMoved(fromPosition, toPosition);
//    }

    class PlayQueueViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,
            DragListener
    {
        public TextView title;
        public TextView artist;
        public ImageView cover;
        public View background;
        View drag;

        PlayQueueViewHolder(View view)
        {
            super(view);

            title = (TextView) view.findViewById(R.id.queue_item_title);
            artist = (TextView) view.findViewById(R.id.queue_item_artist);
            cover = (ImageView) view.findViewById(R.id.queue_item_cover);
            background = view.findViewById(R.id.queue_item_background);
            drag = view.findViewById(R.id.queue_item_drag_handle);


            view.findViewById(R.id.queue_item_container).setOnClickListener(this);
            view.findViewById(R.id.queue_item_drag_handle).setOnClickListener(this);
            view.findViewById(R.id.queue_item_options).setOnClickListener(this);
            view.findViewById(R.id.queue_item_drag_handle).setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
//                    if(MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN)
//                        dragListener.onStartDrag(PlayQueueViewHolder.this);
                    return false;
                }
            });
        }

        @Override
        public void onClick(View v)
        {
            switch(v.getId())
            {
                case R.id.queue_item_container:
//                    Player.updateCurrentSong(context, queue
//                            .get(PlayQueueViewHolder.this.getAdapterPosition()));
//                    ((ActivityCommunicator) context).onSongPlayed();
//                    ((ActivityCommunicator) context).onSongChanged();
                    break;
                case R.id.queue_item_options:
                    break;
            }
        }

        @Override
        public void onStartDrag()
        {
            background.setBackgroundResource(R.color.colorPrimaryDarker);
        }

        @Override
        public void onStopDrag()
        {
            background.setBackgroundResource(R.color.background_primary);
        }
    }
}


