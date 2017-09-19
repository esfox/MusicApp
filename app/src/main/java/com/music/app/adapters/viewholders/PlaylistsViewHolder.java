package com.music.app.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.music.app.R;
import com.music.app.objects.Playlist;
import com.music.app.views.Notice;


public class PlaylistsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    private TextView name, tracksCount;
    private ImageView[] covers;
    private View background;

    public PlaylistsViewHolder(View itemView)
    {
        super(itemView);
        itemView.setOnClickListener(this);

        background = findView(itemView, R.id.playlist_item_background);
        name = (TextView) findView(itemView, R.id.playlist_item_name);
        tracksCount = (TextView) findView(itemView, R.id.playlist_item_tracks_count);
        covers = new ImageView[]
        {
            (ImageView) findView(itemView, R.id.playlists_item_cover_1),
            (ImageView) findView(itemView, R.id.playlists_item_cover_2),
            (ImageView) findView(itemView, R.id.playlists_item_cover_3),
            (ImageView) findView(itemView, R.id.playlists_item_cover_4),
            (ImageView) findView(itemView, R.id.playlists_item_cover_5)
        };
    }

    private View findView(View view, int id)
    {
        return view.findViewById(id);
    }

    @Override
    public void onClick(View v)
    {
        Notice notice = new Notice(v.getContext());
        notice.setNoticeText("Clicked at " + String.valueOf(getAdapterPosition()+1));
        notice.show();
    }

    public void bind(Playlist playlist, int position, int coverCount)
    {
//        background.setBackgroundResource((position % 2 == 0)?
//                R.color.background_alternate :
//                R.color.background_material);

        name.setText(playlist.getName());
        tracksCount.setText(String.valueOf(playlist.getTempTracksCount()) + " tracks");

        for (int i = 0; i < 5; i++)
            covers[i].setVisibility((i <= coverCount)? View.VISIBLE : View.GONE);

        for (int i = 0; i < coverCount; i++)
            Glide.with(itemView.getContext())
                    .load(playlist.getTempCovers()[i])
                    .placeholder(R.drawable.album_art_placeholder)
                    .into(covers[i]);
    }
}
