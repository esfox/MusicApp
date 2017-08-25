package com.music.app.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.music.app.MainActivity;
import com.music.app.R;
import com.music.app.objects.PlayQueue;
import com.music.app.objects.Queue;
import com.music.app.objects.Song;
import com.music.app.objects.Sorter;
import com.music.app.utils.Dialoger;
import com.music.app.utils.adapters.SongListAdapter;

public class SongListViewHolder implements View.OnClickListener, View.OnLongClickListener
{
    public TextView title;
    public TextView artist;
    private ImageView cover;
    public RelativeLayout background;

    public View container;
    public View options;
    private View addTo;
    private View queue;
    private View more;

    public TextView sectionText;
    private View sectionBackground;

    private Song song;
    private Context context;
    private SongListAdapter adapter;
    private ListView songList;
    private Sorter.SortBy sort;

    private int position;
    private int viewType;

    public SongListViewHolder(Context pContext, View view, ListView pListView, int pViewType, Sorter.SortBy pSort)
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

        context = pContext;
        songList = pListView;
        sort = pSort;

        adapter = (SongListAdapter) songList.getAdapter();
    }

    public void setPosition(int pPosition)
    {
        position = pPosition;
        setBackgroundColor(position);
    }

    public void setBackgroundColor(int position)
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

    public void setSong(Song pSong)
    {
        song = pSong;

        title.setText(song.getTitle());
        artist.setText(song.getArtist());

//        if(sort == Sorter.SortBy.artist)
//            artist.setText(song.getAlbum());
//        else
//            artist.setText(song.getArtist());

       cover.setImageDrawable(song.getCover());

//        if(song.getCoverPath() != null)
//            Glide.with(context).load(new File(song.getCoverPath())).into(cover);
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
                onClick();
                break;

            case R.id.options:
                options();
                break;

            case R.id.song_list_options_queue:
                queue();
                break;

            case R.id.song_list_options_play_next:
                playNext();
                break;

            case R.id.song_list_options_more:
                more();
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

        options();
        return true;
    }

    private void onClick()
    {
        if(adapter.inMultiQueueMode())
        {
            ((MainActivity) context).onQueue(song.getId());
            Toast.makeText(context, song.getTitle() + " queued", Toast.LENGTH_SHORT).show();
//            PlayQueue.queue(song);
//            Snackbar.make(null, song.getTitle() + " queued", Snackbar.LENGTH_LONG).show();
        }
        else if(adapter.inSelectionMode())
        {
            adapter.selectItem(position);
            select();
        }
        else
            ((MainActivity) context).onStartAudio(song, true);
    }

    private void options()
    {
        int openedOptionsPosition = adapter.getOpenedOptionsPosition();

        if(openedOptionsPosition != -1)
        {
            if(openedOptionsPosition >= songList.getFirstVisiblePosition() &&
                    openedOptionsPosition <= songList.getLastVisiblePosition())
                ((SongListViewHolder) songList.getChildAt(openedOptionsPosition - songList.getFirstVisiblePosition()).getTag()).toggleOptions(true, false);
        }

        if(background.getTranslationX() == 0)
        {
            toggleOptions(true, true);
            adapter.setOpenedOptionsPosition(position);
        }
        else
        {
            toggleOptions(true, false);
            adapter.setOpenedOptionsPosition(-1);
        }

    }

    public void toggleOptions(boolean animate, boolean toggle)
    {
        if(animate)
        {
            ObjectAnimator swipe;

            if (toggle)
                swipe = ObjectAnimator.ofFloat(background, View.TRANSLATION_X,
                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -180, context.getResources().getDisplayMetrics()));
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
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -180, context.getResources().getDisplayMetrics())
                );
            else
                background.setTranslationX(0);
        }
    }

    private void queue()
    {
        ((MainActivity) context).onQueue(song.getId());
        Toast.makeText(context, song.getTitle() + " queued", Toast.LENGTH_SHORT).show();
//        PlayQueue.queue(song);
//        Snackbar.make(null, song.getTitle() + " queued", Snackbar.LENGTH_LONG).show();
        options();
    }

    private void playNext()
    {
        ((MainActivity) context).onPlayNext(song.getId());
        Toast.makeText(context, song.getTitle() + " to play next", Toast.LENGTH_SHORT).show();
//        PlayQueue.playNext(song);
//        Snackbar.make(null, song.getTitle() + " to play next", Snackbar.LENGTH_LONG).show();
        options();
    }

    private void more()
    {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                switch(which)
                {
                    case 0:
                        addTo();
                        break;
                    case 1:
                        toggleOptions(true, false);
                        ((MainActivity) context).fragmentManager.songDetails(song);
                        break;
                    case 2:
                        //TODO: Add action (Edit Tags)
                        break;
                    case 3:
                        //TODO: Add action (Delete)
                        delete();
                        break;
                }
            }
        };

        String title = "More Options";
        Dialoger.createDialog(context, title, R.array.options_more_texts, listener);
    }

    private void addTo()
    {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                switch(which)
                {
                    case 0:
                        //TODO: Add action (Playlist...)
                        break;
                    case 1:
                        //TODO: Add action (New Playlist)
                        break;
                }
            }
        };

        String title = "Add \"" + song.getTitle() + "\" to...";
        Dialoger.createDialog(context, title, R.array.options_add_to_texts, listener);
    }

    private void select()
    {
        if(adapter.itemIsSelected(position))
            background.setBackgroundResource(R.color.colorPrimaryDarker);
        else
            setBackgroundColor(position);
    }

    private void delete()
    {
        //TODO: Fix/Improve

        DialogInterface.OnClickListener positiveButtonListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                toggleOptions(false, false);
                adapter.deleteItem(position);
            }
        };

        String title = "Delete \"" + String.valueOf(song.getTitle()) + "\"?";

        //TODO: Delete from storage or library only
        String message = "Are you sure you want to delete this track?";

        Dialoger.createAlertDialog(context, title, message, positiveButtonListener);
    }
}