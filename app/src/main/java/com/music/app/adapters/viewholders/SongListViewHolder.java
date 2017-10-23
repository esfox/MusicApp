package com.music.app.adapters.viewholders;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.music.app.R;
import com.music.app.adapters.SongListAdapter;
import com.music.app.interfaces.ListItem;
import com.music.app.objects.Song;
import com.music.app.objects.Sorter;

public class SongListViewHolder implements View.OnClickListener, View.OnLongClickListener
{
    private View background,
                 clickable,
                 optionsLayout,
                 addTo,
                 queue,
                 more,
                 sectionBackground;
    private TextView title, artist;
    private ImageView cover, options;

    public TextView sectionText;

    private Sorter.SortBy sort;

    private int index,
                viewType;

    private ListItem.SongListItemListener listener;

    public SongListViewHolder
            (View view,
             int pViewType,
             Sorter.SortBy pSort,
             ListItem.SongListItemListener listener)
    {
        viewType = pViewType;
        if(viewType == SongListAdapter.type_item)
        {
            background = view.findViewById(R.id.song_list_background);
            clickable = view.findViewById(R.id.song_list_clickable);
            options = (ImageView) view.findViewById(R.id.song_list_options);

            optionsLayout = view.findViewById(R.id.song_list_options_layout);
            addTo = view.findViewById(R.id.song_list_options_play_next);
            queue = view.findViewById(R.id.song_list_options_queue);
            more = view.findViewById(R.id.song_list_options_more);

            title = (TextView) view.findViewById(R.id.song_list_title);
            artist = (TextView) view.findViewById(R.id.song_list_artist);
            cover = (ImageView) view.findViewById(R.id.song_list_cover);
        }
        else
        {
            sectionText = (TextView) view.findViewById(R.id.section_header_text);
            sectionBackground = view.findViewById(R.id.section_header_background);
        }

        sort = pSort;

        this.listener = listener;
    }

    public void setIndex(int index, boolean alternateBackgroundColor)
    {
        this.index = index;
        setBackgroundColor(this.index, alternateBackgroundColor, false);
    }

    public void setBackgroundColor
            (int index, boolean alternateBackgroundColor, boolean isSelected)
    {
        if(!isSelected)
        {
            if(alternateBackgroundColor)
                if(sort == Sorter.SortBy.title)
                    background.setBackgroundResource
                            ((index % 2 == 0)?
                            R.color.background_primary :
                            R.color.background_alternate);

            else
            {
                if(viewType == SongListAdapter.type_item)
                    background.setBackgroundResource(R.color.background_primary);
                else
                    sectionBackground.setBackgroundResource(R.color.background_alternate);
            }
        }
        else
            background.setBackgroundResource(R.color.colorPrimaryDarker);
    }

    public void setSongDetails(Song song)
    {
        title.setText(song.getTitle());
        artist.setText(song.getArtist());
        cover.setImageDrawable(song.getCover());

//        title.setTag(song.getTitle());
//        artist.setTag(song.getArtist());
//
//        for(final TextView textView : new TextView[] {title, artist})
//        {
//            textView.post(new Runnable()
//            {
//                @Override
//                public void run()
//                {
//                    textView.setText(textView.getTag().toString());
//                    textView.setEllipsize(TextUtils.TruncateAt.END);
//                    textView.setSingleLine(true);
//                }
//            });
//        }

//        if(sort == Sorter.SortBy.artist)
//            artist.setText(song.getAlbum());
//        else
//            artist.setText(song.getArtist());

//        if(song.getCoverPath() != null)
//            Glide.with(clickable.getContext()).load(new File(song.getCoverPath())).into(cover);
//        else
//            cover.setImageResource(R.drawable.library_music_48dp);
    }

    public void makeClickable()
    {
        clickable.setOnClickListener(this);
        options.setOnClickListener(this);
        addTo.setOnClickListener(this);
        queue.setOnClickListener(this);
        more.setOnClickListener(this);

        clickable.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.song_list_clickable:
                listener.onItemClick(index, this);
                break;

            case R.id.song_list_options:
                onOptions();
                break;

            case R.id.song_list_options_queue:
                listener.onQueue(index);
                onOptions();
                break;

            case R.id.song_list_options_play_next:
                listener.onPlayNext(index);
                onOptions();
                break;

            case R.id.song_list_options_more:
                listener.onMoreOptions(index, this);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v)
    {
//        if(viewType == SongListAdapter.type_item)
//        {
//            if(adapter.getOpenedOptionsPosition() != -1)
//            {
//                checkIfOpen(false, false);
//                adapter.setOpenedOptionsPosition(-1);
//            }
//
//            adapter.toggleSelectionMode(true);
//            adapter.selectItem(index);
//            select();
//
//            return true;
//        }
//        else return false;
        onOptions();
        return true;
    }

    private void onOptions()
    {
        listener.onOptions
        (
            index,
            background.getTranslationX() != 0,
            this
        );
    }

    public void checkIfOpen(final boolean toggle)
    {
        optionsLayout.setVisibility(toggle? View.VISIBLE : View.GONE);

        if(toggle)
            background.setTranslationX
            (
                TypedValue.applyDimension
                (
                    TypedValue.COMPLEX_UNIT_DIP, -180,
                    background.getContext().getResources().getDisplayMetrics()
                )
            );
        else
            background.setTranslationX(0);
    }

    public void toggleOptionsVisibility(boolean isVisible)
    {
        options.setVisibility(isVisible? View.VISIBLE : View.GONE);
    }

    public void toggleDragMode(boolean toggle)
    {
        Log.d("toggle", String.valueOf(toggle));
        options.setColorFilter(toggle? Color.WHITE : Color.parseColor("#707070"),
                PorterDuff.Mode.SRC_ATOP);
        options.setImageResource(toggle?
                R.drawable.drag_vertical_24dp :
                R.drawable.more_vertical_24dp);
    }
}