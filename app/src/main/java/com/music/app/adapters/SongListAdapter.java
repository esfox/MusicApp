package com.music.app.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.music.app.R;
import com.music.app.adapters.viewholders.SongListViewHolder;
import com.music.app.fragments.FragmentManager;
import com.music.app.interfaces.SongListAdapterListener;
import com.music.app.interfaces.SongListListener;
import com.music.app.objects.Data;
import com.music.app.objects.Player;
import com.music.app.objects.Song;
import com.music.app.objects.Sorter;
import com.music.app.utils.Dialoger;
import com.music.app.views.Notice;
import com.wooplr.spotlight.SpotlightView;
import com.wooplr.spotlight.utils.SpotlightListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class SongListAdapter extends BaseAdapter implements SongListListener
{
    private ListView songList;

    private Data data;
    private Player player;

    private Sorter.SortBy sort;

    private ArrayList<Song> songs;
    private ArrayList<Item> items;
    private ArrayList<Boolean> selectedFlags;

    @SuppressWarnings("WeakerAccess")
    public static final int type_item = 0,
                            type_section = 1;

    private int openedOptionsPosition = -1,
                selectedCount = 0;
    private boolean inMultiQueueMode = false,
                    inSelectionMode = false;


    private SongListAdapterListener songListAdapterListener;
    private FragmentManager fragmentManager;

    class Item
    {
        private int type;
        private Song song = null;
        private String section = "";

        Item(Song pSong)
        {
            song = pSong;
            type = type_item;
        }

        Item(String pSection)
        {
            section = pSection;
            type = type_section;
        }

        Song getSong()
        {
            return song;
        }

        String getSection()
        {
            return section;
        }

        boolean isItem()
        {
            return (type == type_item);
        }
    }

    public SongListAdapter
    (
        ListView songList,
        ArrayList<Song> songs,
        Data data,
        Player player,
        Sorter.SortBy sort
    )
    {
        this.songList = songList;
        this.data = data;
        this.player = player;
        this.songs = songs;
        this.sort = sort;

        items = new ArrayList<>();

        HashMap<Integer, String> sections = new HashMap<>();

        selectedFlags = new ArrayList<>();

        try
        {
            for(int i = 0; i < this.songs.size(); i++)
            {
                selectedFlags.add(false);

                if(sort != Sorter.SortBy.title)
                {
                    String section = "";

                    if(sort == Sorter.SortBy.artist)
                        section = this.songs.get(i).getArtist();
                    else if(sort == Sorter.SortBy.album)
                        section = this.songs.get(i).getAlbum();

                    if(!sections.containsValue(section))
                    {
                        sections.put(i, section);
                        items.add(new Item(section));
                    }
                }

                items.add(new Item(this.songs.get(i)));
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        /*
        TODO: Add more sort options
        TODO: Implement views show certain details on different sort
         (e.g. album is shown when sorted by album)
        TODO: Add Swipe Effect to open options (Optional)
        */
    }

    public void setSongListAdapterListener(SongListAdapterListener songListAdapterListener)
    {
        this.songListAdapterListener = songListAdapterListener;
    }

    public void setFragmentManager(FragmentManager fragmentManager)
    {
        this.fragmentManager = fragmentManager;
    }

    private Song getSongByIndex(int index)
    {
        return items.get(index).getSong();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        int type = getItemViewType(position);

        final SongListViewHolder viewHolder;
        View view = convertView;

        if(view == null)
        {
            LayoutInflater inflater = (LayoutInflater)
                    parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if(type == type_item)
                view = inflater.inflate(R.layout.song_list_item, parent, false);
            else
                view = inflater.inflate(R.layout.song_list_section_header, parent, false);

            viewHolder = new SongListViewHolder
                    (view, type, sort, this);
            view.setTag(viewHolder);

            if(type == type_item)
                viewHolder.makeClickable();
        }
        else
            viewHolder = (SongListViewHolder) view.getTag();

        viewHolder.setPosition(position);

        if(type == type_item)
        {
            viewHolder.setSongDetails(items.get(position).getSong());

            checkIfPositionIsOpened(position, viewHolder);
            checkIfPositionIsSelected(position, viewHolder);
        }
        else
            viewHolder.sectionText.setText(items.get(position).getSection());

        return view;
    }

    private void checkIfPositionIsOpened(int position, SongListViewHolder viewHolder)
    {
        if(position == openedOptionsPosition)
            viewHolder.toggleOptions(false, true);
        else
            viewHolder.toggleOptions(false, false);

//        if(openedOptionsPosition <= songList.getFirstVisiblePosition() &&
//           openedOptionsPosition >= songList.getLastVisiblePosition())
//            openedOptionsPosition = -1;
    }

    private void checkIfPositionIsSelected(int position, SongListViewHolder viewHolder)
    {
        if(inSelectionMode)
        {
            viewHolder.toggleOptionsVisibility(false);
            viewHolder.setBackgroundColor(position, selectedFlags.get(position));
        }
        else
            viewHolder.toggleOptionsVisibility(true);
    }

    @Override
    public void onPlay(int index, SongListViewHolder viewHolder)
    {
        Song song = getSongByIndex(index);
        if (inMultiQueueMode)
        {
            if(data.currentSongIsNotNull())
                queue(index);
            else
                noPlayingSongNotice();
        }
        else if (inSelectionMode)
        {
            selectItem(index);
            viewHolder.setBackgroundColor(index, itemIsSelected(index));
        }
        else
            player.startSong(song, true);
    }

    @Override
    public void onQueue(final int index)
    {
        if(data.currentSongIsNotNull())
        {
            if(data.queuePrompt())
            {
                AlertDialog.Builder dialog = Dialoger.getDialogBuilder(songList.getContext());
                dialog.setTitle("Multi-Queue");
                dialog.setMessage("It looks like you are trying to queue a song.\n" +
                        "I suggest you try the Multi-Queue mode.\n" +
                        "In Multi-Queue, you can just tap on the " +
                        "song to queue it immediately.\n\n" +
                        "You can turn on Multi-Queue automatically when you queue a song " +
                        "by enabling it in the settings." +
                        "\n(SETTINGS NOT YET MADE.)");
                dialog.setPositiveButton("Got It", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        data.updateQueuePrompt(false);
                        new SpotlightView.Builder((Activity) songList.getContext())
                                .introAnimationDuration(300)
                                .enableRevealAnimation(true)
                                .fadeinTextDuration(200)
                                .headingTvText("Multi-Queue")
                                .headingTvSize(30)
                                .headingTvColor(Color.parseColor("#FF6060"))
                                .subHeadingTvText("Press this button to enable Multi-Queue. " +
                                        "If you press it again while Multi-Queue is enabled," +
                                        " it will disable Multi-Queue.")
                                .subHeadingTvSize(15)
                                .subHeadingTvColor(Color.WHITE)
                                .maskColor(Color.parseColor("#dc000000"))
                                .lineAnimDuration(200)
                                .lineAndArcColor(Color.parseColor("#F44336"))
                                .dismissOnBackPress(true)
                                .dismissOnTouch(true)
                                .enableDismissAfterShown(true)
                                .performClick(true)
                                .target(((Activity) songList.getContext()).findViewById(R.id.multi_queue))
                                .usageId(String.valueOf(UUID.randomUUID()))
                                .setListener(new SpotlightListener()
                                {
                                    @Override
                                    public void onUserClicked(String s)
                                    {
                                        queue(index);
                                    }
                                })
                                .show();
                    }
                });
                dialog.show();
            }
            else
                queue(index);
        }
        else
            noPlayingSongNotice();
    }

    public void queue(int index)
    {
        Song song = getSongByIndex(index);
        player.queue().queue(song.getId());

        Notice notice = new Notice(songList.getContext());
        notice.setNoticeText(song.getTitle() + " queued");
        notice.setNoticeIcon(R.drawable.queue_24dp);
        notice.show();
    }

    @Override
    public void onPlayNext(int index)
    {
        if(data.currentSongIsNotNull())
        {
            Song song = getSongByIndex(index);
            player.queue().playNext(song.getId());

            Notice notice = new Notice(songList.getContext());
            notice.setNoticeText(song.getTitle() + " to play next");
            notice.setNoticeIcon(R.drawable.play_next_24dp);
            notice.show();
        }
        else
            noPlayingSongNotice();
    }

    @Override
    public void onOptions(int index, boolean isOpened, SongListViewHolder viewHolder)
    {
        int openedOptionsPosition = this.openedOptionsPosition;

        if(openedOptionsPosition != -1)
        {
            if(openedOptionsPosition >= songList.getFirstVisiblePosition() &&
                    openedOptionsPosition <= songList.getLastVisiblePosition())
                ((SongListViewHolder) songList.getChildAt
                        (openedOptionsPosition - songList.getFirstVisiblePosition()).getTag())
                        .toggleOptions(true, false);
        }

        if(isOpened)
        {
            viewHolder.toggleOptions(true, true);
            this.openedOptionsPosition = index;
        }
        else
        {
            viewHolder.toggleOptions(true, false);
            this.openedOptionsPosition = -1;
        }
    }

    @Override
    public void onMoreOptions(final int index, final SongListViewHolder viewHolder)
    {
        final Song song = getSongByIndex(index);
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
            switch(which)
            {
                case 0:
                    addTo(song);
                    break;
                case 1:
                    viewHolder.toggleOptions(false, false);
                    fragmentManager.songDetails(song);
                    break;
                case 2:
                    //TODO: Add action (Edit Tags)
                    break;
                case 3:
                    //TODO: Add action (Delete)
                    delete(index, song, viewHolder);
                    break;
            }
            }
        };

        String title = "More Options";
        Dialoger.createDialog
        (
            songList.getContext(),
            title,
            R.array.options_more_texts, listener
        );
    }

    private void addTo(Song song)
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
        Dialoger.createDialog
        (
            songList.getContext(),
            title,
            R.array.options_add_to_texts,
            listener
        );
    }

    private void delete(final int index, Song song, final SongListViewHolder viewHolder)
    {
        //TODO: Fix/Improve

        DialogInterface.OnClickListener positiveButtonListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                viewHolder.toggleOptions(false, false);
                openedOptionsPosition = -1;
                songs.remove(index);
                notifyDataSetChanged();
            }
        };

        String title = "Delete \"" + String.valueOf(song.getTitle()) + "\"?";

        //TODO: Delete from storage or library only
        String message = "Are you sure you want to delete this track?";

        Dialoger.createAlertDialog
            (
                songList.getContext(),
                title,
                message,
                positiveButtonListener
            );
    }

    public void deleteSelectedItems()
    {
        //TODO: Delete from storage
        //TODO: Deletion in other sort

//        ArrayList<Item> selectedItems = new ArrayList<>();
//
//        for(int i = 0; i < selectedFlags.size(); i++)
//            if(selectedFlags.get(i))
//                selectedItems.add(items.get(i));
//
//        for(int i = 0; i < selectedItems.size(); i++)
//            items.remove(selectedItems.get(i));
//
//        notifyDataSetChanged();


        Notice notice = new Notice(songList.getContext());
        notice.setNoticeText("Delete Selected Items");
        notice.show();

        toggleSelectionMode(false);
    }

    public boolean isInMultiQueueMode() { return inMultiQueueMode; }

    public void toggleMultiQueueMode(boolean toggleMultiQueueMode)
    {
        //TODO: Untoggle when going to other fragments (Do after managing fragment navigation)

        inMultiQueueMode = toggleMultiQueueMode;
        toggleOptions(!toggleMultiQueueMode);

        songListAdapterListener.onToggleToolbar(toggleMultiQueueMode, true,  "Multi-Queue");
    }

    public void toggleSelectionMode(boolean toggleSelectionMode)
    {
        //TODO: Untoggle when going to other fragments (Do after managing fragment navigation)

        //TODO: Fix entering into selection mode on other sort

        inSelectionMode = toggleSelectionMode;
        toggleOptions(!toggleSelectionMode);

        if(!inSelectionMode)
        {
            if(sort == Sorter.SortBy.title)
            {
                selectedCount = 0;

                for(int i = 0; i < selectedFlags.size(); i++)
                    selectedFlags.set(i, false);

                for(int i = 0;
                    i <= songList.getLastVisiblePosition() - songList.getFirstVisiblePosition();
                    i++)
                {
                    ((SongListViewHolder) songList.getChildAt(i).getTag())
                            .setBackgroundColor(i, false);
                }
            }
            else
                songList.setAdapter
                (new SongListFastScrollAdapter
                    (
                        songs,
                        songList,
                        data,
                        player,
                        sort
                    )
                );
        }

        songListAdapterListener.onToggleToolbar(toggleSelectionMode, false, "0 items selected");
    }

    private void toggleOptions(boolean toggle)
    {
        for(int i = 0;
            i <= songList.getLastVisiblePosition() - songList.getFirstVisiblePosition();
            i++)
        {
            if(items.get(i).isItem())
                ((SongListViewHolder) songList.getChildAt(i).getTag())
                        .toggleOptionsVisibility(toggle);
        }
    }

    private void selectItem(int position)
    {
        selectedFlags.set(position, !selectedFlags.get(position));

        if(selectedFlags.get(position))
            selectedCount++;
        else
            selectedCount--;

        ((TextView) songList.getTag()).setText(String.valueOf(selectedCount) + " items selected");
    }

    private boolean itemIsSelected(int position)
    {
        return selectedFlags.get(position);
    }

    private void noPlayingSongNotice()
    {
        Notice notice = new Notice(songList.getContext());
        notice.setNoticeText("There is no currently playing song.");
        notice.show();
    }

    ArrayList<Item> getItems()
    {
        return items;
    }

    @Override
    public Object getItem(int position)
    {
        if(getItemViewType(position) == type_item)
            return items.get(position).getSong();
        else
            return null;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getItemViewType(int position)
    {
        return (items.get(position).isItem())? type_item : type_section;
    }

    @Override
    public int getViewTypeCount()
    {
        return 2;
    }

    @Override
    public int getCount()
    {
        return items.size();
    }
}