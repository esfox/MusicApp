package com.music.app.utils.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.music.app.R;
import com.music.app.objects.Song;
import com.music.app.views.RecyclerViewFastScroller;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> implements RecyclerViewFastScroller.BubbleTextGetter
{
    private Context context;
    private ArrayList<Song> songs;

    public PlaylistAdapter(Context context, ArrayList<Song> songs)
    {
        this.context = context;
        this.songs = songs;
    }

    class PlaylistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView title,
                artist;
        ImageView cover,
                options;
        View background;

        public PlaylistViewHolder(View view)
        {
            super(view);

            title = (TextView) view.findViewById(R.id.title);
            artist = (TextView) view.findViewById(R.id.artist);

            cover = (ImageView) view.findViewById(R.id.cover);
            options = (ImageView) view.findViewById(R.id.options);

            background = view.findViewById(R.id.background);
        }

        @Override
        public void onClick(View v)
        {
//            switch(v.getId())
//            {
//                case R.id.queue_item_container:
//                    Player.playSong(queue.get(PlayQueueViewHolder.this.getAdapterPosition()));
//                    ((ActivityCommunicator) context).onSongPlayed();
//                    ((ActivityCommunicator) context).onSongChanged(PlayQueue.currentSong());
//                    PlayQueueFragment.updateNowPlayingBar();
//                    break;
//                case R.id.queue_item_drag_handle:
//                    Log.d("Drag", String.valueOf(PlayQueueViewHolder.this.getAdapterPosition()));
//                    break;
//                case R.id.queue_item_options:
//                    break;
//            }
        }
    }

    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_list_item, parent, false);
        return new PlaylistViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PlaylistViewHolder holder, int position)
    {
        if(position % 2 != 0)
            holder.background.setBackgroundResource(R.color.background_alternate);
        else
            holder.background.setBackgroundResource(R.color.background_primary);

        Song song = songs.get(position);
        holder.title.setText(song.getTitle());
        holder.artist.setText(song.getArtist());
        holder.cover.setImageDrawable(song.getCover());
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
}
