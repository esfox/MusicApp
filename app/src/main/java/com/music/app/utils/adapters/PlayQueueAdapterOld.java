package com.music.app.utils.adapters;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.music.app.MainActivity;
import com.music.app.R;
import com.music.app.objects.Data;
import com.music.app.objects.Song;
import com.music.app.utils.interfaces.ItemTouchListener;
import com.music.app.utils.interfaces.DragListener;
import com.music.app.utils.interfaces.OnStartDragListener;
import com.music.app.views.RecyclerViewFastScroller;

import java.util.ArrayList;
import java.util.Collections;

public class PlayQueueAdapterOld extends RecyclerView.Adapter<PlayQueueAdapterOld.PlayQueueViewHolder> implements RecyclerViewFastScroller.BubbleTextGetter, ItemTouchListener
{
    private ArrayList<Song> songs;
    private OnStartDragListener dragListener;

    private Context context;

    public PlayQueueAdapterOld(Context pContext, ArrayList<Song> pSongs, OnStartDragListener pDragListener)
    {
        songs = pSongs;
        dragListener = pDragListener;
        context = pContext;
    }

    @Override
    public PlayQueueViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.play_queue_item, parent, false);
        return new PlayQueueViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final PlayQueueViewHolder holder, int position)
    {
        Song song = songs.get(position);

        holder.title.setText(song.getTitle());
        holder.artist.setText(song.getArtist());
        holder.cover.setImageDrawable(song.getCover());

        if(position == ((MainActivity) context).data.currentSongQueueIndex())
            holder.background.setBackgroundResource(R.color.colorPrimaryDarker);
        else
            holder.background.setBackgroundResource(R.color.background_primary);
    }

    @Override
    public int getItemCount()
    {
        return songs.size();
    }

    @Override
    public String getTextToShowInBubble(int pos)
    {
        return Character.toString(songs.get(pos).getTitle().charAt(0));
    }

    public void update(ArrayList<Song> pSongs)
    {
        songs = pSongs;
        notifyDataSetChanged();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition)
    {
        if (fromPosition < toPosition)
        {
            for (int i = fromPosition; i < toPosition; i++)
            {
                Collections.swap(songs, i, i + 1);
            }
        }
        else
        {
            for (int i = fromPosition; i > toPosition; i--)
            {
                Collections.swap(songs, i, i - 1);
            }
        }

        notifyItemMoved(fromPosition, toPosition);
    }

    class PlayQueueViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, DragListener
    {
        public TextView title;
        public TextView artist;
        public ImageView cover;
        public View background;
        public View drag;

        public PlayQueueViewHolder(View view)
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
                    if(MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN)
                        dragListener.onStartDrag(PlayQueueViewHolder.this);
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
//                    Player.updateCurrentSong(context, queue.get(PlayQueueViewHolder.this.getAdapterPosition()));
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


