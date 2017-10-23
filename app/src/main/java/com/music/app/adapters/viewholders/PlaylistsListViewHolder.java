package com.music.app.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.music.app.R;
import com.music.app.interfaces.ListItem;
import com.music.app.objects.Data;
import com.music.app.objects.Playlist;
import com.music.app.objects.Song;

import java.util.ArrayList;

public class PlaylistsListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    private TextView name, tracksCount;
    private ImageView[] covers;
    private ListItem.PlaylistsListItemListener listener;

    public PlaylistsListViewHolder(View itemView, ListItem.PlaylistsListItemListener listener)
    {
        super(itemView);
        itemView.setOnClickListener(this);
        findView(R.id.playlist_list_item_options).setOnClickListener(this);

        name = (TextView) findView(R.id.playlists_list_item_name);
        tracksCount = (TextView) findView(R.id.playlist_list_item_tracks_count);
        covers = new ImageView[]
        {
            (ImageView) findView(R.id.playlists_list_item_cover_1),
            (ImageView) findView(R.id.playlists_list_item_cover_2),
            (ImageView) findView(R.id.playlists_list_item_cover_3),
            (ImageView) findView(R.id.playlists_list_item_cover_4),
            (ImageView) findView(R.id.playlists_list_item_cover_5)
        };

        this.listener = listener;
    }

    private View findView(int id)
    {
        return itemView.findViewById(id);
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.playlist_list_item_options:
                listener.onOptions(getAdapterPosition(), v);
                break;

            default:
                listener.onGotoPlaylist(getAdapterPosition());
                break;
        }
    }

    public void bind(Playlist playlist, Data data)
    {
        name.setText(playlist.getName());

        long[] songIDs = playlist.getSongs();
        int trackCount = songIDs.length;
        tracksCount.setText(trackCount + ((trackCount != 1)? " tracks" : " track"));

        Log.d("trackCount", String.valueOf(trackCount));

        ArrayList<Song> songs = data.songs();

        if(trackCount >= 5)
        {
            for (int i = 0; i < 5; i++)
            {
                covers[i].setVisibility((i <= trackCount)? View.VISIBLE : View.GONE);
                covers[i].setImageDrawable
                        (Song.getSongByID(songIDs[i], songs).getCover());
            }
        }
        else
        {
            for (int i = 0; i < trackCount; i++)
            {
                covers[i].setVisibility((i <= trackCount)? View.VISIBLE : View.GONE);
                covers[i].setImageDrawable
                        (Song.getSongByID(songIDs[i], songs).getCover());
            }
        }

//        Glide.with(itemView.getContext())
//                .load(playlist.getTempCovers()[i])
//                .placeholder(R.drawable.album_art_placeholder)
//                .into(covers[i]);
    }
}
