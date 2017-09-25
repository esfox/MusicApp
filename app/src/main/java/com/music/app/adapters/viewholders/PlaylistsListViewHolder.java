package com.music.app.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.music.app.R;
import com.music.app.interfaces.ListItem;
import com.music.app.objects.Playlist;

public class PlaylistsListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    private TextView name, tracksCount;
    private ImageView[] covers;
    private ListItem.PlaylistsListItemListener listener;

    public PlaylistsListViewHolder(View itemView, ListItem.PlaylistsListItemListener listener)
    {
        super(itemView);
        itemView.setOnClickListener(this);

        name = (TextView) findView(R.id.playlists_list_item_name);
        tracksCount = (TextView) findView(R.id.playlist_item_tracks_count);
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
        listener.onGotoPlaylist(getAdapterPosition());
    }

    public void bind(Playlist playlist)
    {
        name.setText(playlist.getName());
        int trackCount = playlist.getTempSongs().length;
        tracksCount.setText(trackCount + ((trackCount > 1)? " tracks" : " track"));

        if(trackCount >= 5)
        {
            for (int i = 0; i < 5; i++)
            {
                covers[i].setVisibility((i <= trackCount)? View.VISIBLE : View.GONE);
                covers[i].setImageDrawable(playlist.getTempSongs()[i].getCover());
            }
        }
        else
        {
            for (int i = 0; i < trackCount; i++)
            {
                covers[i].setVisibility((i <= trackCount)? View.VISIBLE : View.GONE);
                covers[i].setImageDrawable(playlist.getTempSongs()[i].getCover());
            }
        }

//            Glide.with(itemView.getContext())
//                    .load(playlist.getTempCovers()[i])
//                    .placeholder(R.drawable.album_art_placeholder)
//                    .into(covers[i]);
    }
}
