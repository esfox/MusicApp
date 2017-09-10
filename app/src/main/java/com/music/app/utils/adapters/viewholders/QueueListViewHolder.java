package com.music.app.utils.adapters.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.music.app.R;
import com.music.app.objects.Song;

public class QueueListViewHolder
{
    private TextView title,
                     artist;
    private ImageView cover;
    private View background;

    public QueueListViewHolder(View view)
    {
        title = (TextView) view.findViewById(R.id.queue_list_title);
        artist = (TextView) view.findViewById(R.id.queue_list_artist);
        cover = (ImageView) view.findViewById(R.id.queue_list_cover);
        background = view.findViewById(R.id.queue_list_background);
    }

    public void setSongDetails(Song song)
    {
        title.setText(song.getTitle());
        artist.setText(song.getArtist());
        cover.setImageDrawable(song.getCover());
        background.setBackgroundResource(android.R.color.transparent);
    }

    public void setPlaying()
    {
        background.setBackgroundResource(R.color.colorPrimaryDarker);
    }
}
