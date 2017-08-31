package com.music.app.views;

import android.animation.ObjectAnimator;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.music.app.R;
import com.music.app.objects.Song;
import com.music.app.objects.Sorter;
import com.music.app.utils.adapters.SongListAdapter;
import com.music.app.utils.interfaces.SongListViewHolderListener;

public class SongListViewHolder implements View.OnClickListener, View.OnLongClickListener
{
    public TextView title;
    public TextView artist;
    private ImageView cover;
    public RelativeLayout background;

    private View container;
    private View options;
    private View addTo;
    private View queue;
    private View more;

    public TextView sectionText;
    private View sectionBackground;

    private Sorter.SortBy sort;

    private int position;
    private int viewType;

    private SongListViewHolderListener songListViewHolderListener;

    public SongListViewHolder
            (View view,
             int pViewType,
             Sorter.SortBy pSort,
             SongListViewHolderListener songListViewHolderListener)
    {
        viewType = pViewType;
        if(viewType == SongListAdapter.type_item)
        {
            title = (TextView) view.findViewById(R.id.title);
            artist = (TextView) view.findViewById(R.id.artist);
            cover = (ImageView) view.findViewById(R.id.cover);
            background = (RelativeLayout) view.findViewById(R.id.background);

            container = view.findViewById(R.id.container);
            options = view.findViewById(R.id.options);
            addTo = view.findViewById(R.id.song_list_options_play_next);
            queue = view.findViewById(R.id.song_list_options_queue);
            more = view.findViewById(R.id.song_list_options_more);
        }
        else
        {
            sectionText = (TextView) view.findViewById(R.id.section_header_text);
            sectionBackground = view.findViewById(R.id.section_header_background);
        }

        sort = pSort;

        this.songListViewHolderListener = songListViewHolderListener;
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

//        if(sort == Sorter.SortBy.artist)
//            artist.setText(song.getAlbum());
//        else
//            artist.setText(song.getArtist());

       cover.setImageDrawable(song.getCover());

//        if(song.getCoverPath() != null)
//            Glide.with(container.getContext()).load(new File(song.getCoverPath())).into(cover);
//        else
//            cover.setImageResource(R.drawable.library_music_48dp);
    }

    public void setClickListeners()
    {
        container.setOnClickListener(this);
        options.setOnClickListener(this);
        addTo.setOnClickListener(this);
        queue.setOnClickListener(this);
        more.setOnClickListener(this);

        container.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.container:
                songListViewHolderListener.onClick(position, this);
                break;

            case R.id.options:
                onOptions();
                break;

            case R.id.song_list_options_queue:
                songListViewHolderListener.onQueue(position);
                onOptions();
                break;

            case R.id.song_list_options_play_next:
                songListViewHolderListener.onPlayNext(position);
                onOptions();
                break;

            case R.id.song_list_options_more:
                songListViewHolderListener.onMoreOptions(position, this);
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
        songListViewHolderListener.onOptions
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
                            container.getContext()
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
                        container.getContext().getResources().getDisplayMetrics())
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
//            container.getContext(),
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
//            container.getContext(),
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
//            public void onClick(DialogInterface dialog, int which)
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
//            container.getContext(),
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
//            public void onClick(DialogInterface dialog, int which)
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
//            container.getContext(),
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
//            public void onClick(DialogInterface dialog, int which)
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
//            container.getContext(),
//            title,
//            message,
//            positiveButtonListener
//        );
//    }
}