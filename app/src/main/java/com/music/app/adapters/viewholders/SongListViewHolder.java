package com.music.app.adapters.viewholders;

import android.animation.ObjectAnimator;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.music.app.R;
import com.music.app.objects.Song;
import com.music.app.objects.Sorter;
import com.music.app.adapters.SongListAdapter;
import com.music.app.interfaces.SongListListener;

public class SongListViewHolder implements View.OnClickListener, View.OnLongClickListener
{
    private View background,
                 clickable,
                 options,
                 addTo,
                 queue,
                 more,
                 sectionBackground;
    private TextView title, artist;
    private ImageView cover;

    public TextView sectionText;

    private Sorter.SortBy sort;

    private int position,
                viewType;

    private SongListListener songListListener;

    public SongListViewHolder
            (View view,
             int pViewType,
             Sorter.SortBy pSort,
             SongListListener songListListener)
    {
        viewType = pViewType;
        if(viewType == SongListAdapter.type_item)
        {
            background = view.findViewById(R.id.song_list_background);
            clickable = view.findViewById(R.id.song_list_clickable);
            options = view.findViewById(R.id.song_list_options);
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

        this.songListListener = songListListener;
    }

    public void setPosition(int pPosition)
    {
        position = pPosition;
        setBackgroundColor(position, false);
    }

    public void setBackgroundColor(int position, boolean isSelected)
    {
        if(!isSelected)
        {
            if(sort == Sorter.SortBy.title)
            {
                int color;

                if(position % 2 == 0)
                    color = R.color.background_primary;
                else
                    color = R.color.background_alternate;

                background.setBackgroundResource(color);
            }
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

       cover.setImageDrawable(song.getCover());

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
                songListListener.onPlay(position, this);
                break;

            case R.id.song_list_options:
                onOptions();
                break;

            case R.id.song_list_options_queue:
                songListListener.onQueue(position);
                onOptions();
                break;

            case R.id.song_list_options_play_next:
                songListListener.onPlayNext(position);
                onOptions();
                break;

            case R.id.song_list_options_more:
                songListListener.onMoreOptions(position, this);
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
//                toggleOptions(false, false);
//                adapter.setOpenedOptionsPosition(-1);
//            }
//
//            adapter.toggleSelectionMode(true);
//            adapter.selectItem(position);
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
        songListListener.onOptions
        (
            position,
            optionsIsOpened(),
            this
        );
    }

    private boolean optionsIsOpened()
    {
        return background.getTranslationX() == 0;
    }

    public void toggleOptions(boolean animate, boolean toggle)
    {
        if(animate)
        {
            ObjectAnimator swipe;

            if (toggle)
                swipe = ObjectAnimator.ofFloat(background, View.TRANSLATION_X,
                        TypedValue.applyDimension
                        (
                            TypedValue.COMPLEX_UNIT_DIP,
                            -180,
                            background.getContext()
                                     .getResources()
                                     .getDisplayMetrics()
                        ));
            else
                swipe = ObjectAnimator.ofFloat(background, View.TRANSLATION_X, 0);

            swipe.setDuration(250);
            swipe.setInterpolator(new AccelerateDecelerateInterpolator());
            swipe.start();
        }
        else
        {
            if(toggle)
                background.setTranslationX
                (
                    TypedValue.applyDimension
                    (
                        TypedValue.COMPLEX_UNIT_DIP,
                        -180,
                        background.getContext().getResources().getDisplayMetrics())
                );
            else
                background.setTranslationX(0);
        }
    }

    public void toggleOptionsVisibility(boolean isVisible)
    {
        if(isVisible)
            options.setVisibility(View.VISIBLE);
        else
            options.setVisibility(View.INVISIBLE);
    }

//    private void queue()
//    {
//        queueListener.onQueue(song.getId());
//        Toast.makeText
//        (
//            clickable.getContext(),
//            song.getTitle() + " queued",
//            Toast.LENGTH_SHORT
//        ).show();
//    }
//
//    private void playNext()
//    {
//        queueListener.onPlayNext(song.getId());
//        Toast.makeText
//        (
//            clickable.getContext(),
//            song.getTitle() + " to play next",
//            Toast.LENGTH_SHORT
//        ).show();
//    }
//
//    private void options()
//    {
//        int openedOptionsPosition = adapter.getOpenedOptionsPosition();
//
//        if(openedOptionsPosition != -1)
//        {
//            if(openedOptionsPosition >= songList.getFirstVisiblePosition() &&
//                    openedOptionsPosition <= songList.getLastVisiblePosition())
//                ((SongListViewHolder) songList.getChildAt
//                        (openedOptionsPosition - songList.getFirstVisiblePosition()).getTag())
//                        .toggleOptions(true, false);
//        }
//
//        if(background.getTranslationX() == 0)
//        {
//            toggleOptions(true, true);
//            adapter.setOpenedOptionsPosition(position);
//        }
//        else
//        {
//            toggleOptions(true, false);
//            adapter.setOpenedOptionsPosition(-1);
//        }
//    }
//
//    private void more()
//    {
//        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
//        {
//            @Override
//            public void onPlay(DialogInterface dialog, int which)
//            {
//                switch(which)
//                {
//                    case 0:
//                        addTo();
//                        break;
//                    case 1:
//                        toggleOptions(true, false);
//                        adapter.songDetails(song);
//                        break;
//                    case 2:
//                        //TODO: Add action (Edit Tags)
//                        break;
//                    case 3:
//                        //TODO: Add action (Delete)
//                        delete();
//                        break;
//                }
//            }
//        };
//
//        String title = "More Options";
//        Dialoger.createDialog
//        (
//            clickable.getContext(),
//            title,
//            R.array.options_more_texts, listener
//        );
//    }
//
//    private void addTo()
//    {
//        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
//        {
//            @Override
//            public void onPlay(DialogInterface dialog, int which)
//            {
//                switch(which)
//                {
//                    case 0:
//                        //TODO: Add action (Playlist...)
//                        break;
//                    case 1:
//                        //TODO: Add action (New Playlist)
//                        break;
//                }
//            }
//        };
//
//        String title = "Add \"" + song.getTitle() + "\" to...";
//        Dialoger.createDialog
//        (
//            clickable.getContext(),
//            title,
//            R.array.options_add_to_texts,
//            listener
//        );
//    }
//
//    private void delete()
//    {
//        //TODO: Fix/Improve
//
//        DialogInterface.OnClickListener positiveButtonListener = new DialogInterface.OnClickListener()
//        {
//            @Override
//            public void onPlay(DialogInterface dialog, int which)
//            {
//                toggleOptions(false, false);
//                adapter.deleteItem(position);
//            }
//        };
//
//        String title = "Delete \"" + String.valueOf(song.getTitle()) + "\"?";
//
//        //TODO: Delete from storage or library only
//        String message = "Are you sure you want to delete this track?";
//
//        Dialoger.createAlertDialog
//        (
//            clickable.getContext(),
//            title,
//            message,
//            positiveButtonListener
//        );
//    }
}