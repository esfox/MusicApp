package com.music.app.adapters;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mobeta.android.dslv.DragSortListView;
import com.music.app.R;
import com.music.app.adapters.viewholders.SongListViewHolder;
import com.music.app.fragments.FragmentManager;
import com.music.app.interfaces.ListItem;
import com.music.app.interfaces.SongListAdapterListener;
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

public class SongListAdapter extends BaseAdapter implements ListItem.SongListItemListener
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

    private int openedIndex = -1,
                selectedCount = 0;
    private boolean alternateBackgroundColor = true,
                    inMultiQueueMode = false,
                    inSortMode = false,
                    inSelectionMode = false;

    private SongListAdapterListener listener;
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

//    TODO: Add more sort options
//    TODO: Implement views show certain details on different sort (e.g. album is shown when sorted by album)
//    TODO: Add Swipe Effect to open options (Optional)

    public SongListAdapter
    (
        ListView songList,
        ArrayList<Song> songs,
        Sorter.SortBy sort,
        SongListAdapterListener listener,
        Data data,
        Player player,
        FragmentManager fragmentManager
    )
    {
        this.songList = songList;
        this.songs = songs;
        this.sort = sort;
        this.listener = listener;
        this.data = data;
        this.player = player;
        this.fragmentManager = fragmentManager;

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
    }

    public void doNotAlternateBackgroundColor()
    {
        alternateBackgroundColor = false;
    }

    private Song getSongByIndex(int index)
    {
        return items.get(index).getSong();
    }

    @Override
    public View getView(final int index, View convertView, ViewGroup parent)
    {
        int type = getItemViewType(index);

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

        viewHolder.setIndex(index, alternateBackgroundColor);

        if(type == type_item)
        {
            viewHolder.setSongDetails((Song) getItem(index));

            if(openedIndex != -1) viewHolder.checkIfOpen(index == openedIndex);
            if(inSortMode) viewHolder.toggleDragMode(inSortMode);
            checkIfIndexIsSelected(index, viewHolder);
        }
        else
            viewHolder.sectionText.setText(items.get(index).getSection());

        return view;
    }

    private void checkIfIndexIsSelected(int index, SongListViewHolder viewHolder)
    {
        if(inSelectionMode)
        {
            viewHolder.toggleOptionsVisibility(false);
            viewHolder.setBackgroundColor
                    (index, alternateBackgroundColor, selectedFlags.get(index));
        }
        else
            viewHolder.toggleOptionsVisibility(true);
    }

    @Override
    public void onItemClick(int index, SongListViewHolder viewHolder)
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
            viewHolder.setBackgroundColor
                    (index, alternateBackgroundColor, itemIsSelected(index));
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
                                .target(((Activity) songList.getContext()).findViewById(R.id.action_button))
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
        int openedOptionsIndex = openedIndex;

        if(openedOptionsIndex != -1)
            if(openedOptionsIndex >= songList.getFirstVisiblePosition() &&
                    openedOptionsIndex <= songList.getLastVisiblePosition())
                toggleOptions(openedOptionsIndex, true, false);

        toggleOptions(index, true, !isOpened);
//            viewHolder.checkIfOpen(true, !isOpened);
    }

    private void toggleOptions(int index, boolean animate, final boolean toggle)
    {
        final View listChild = songList.getChildAt(index - songList.getFirstVisiblePosition()),
                  background = listChild.findViewById(R.id.song_list_background),
               optionsLayout = listChild.findViewById(R.id.song_list_options_layout);

        if(animate)
        {
            ObjectAnimator swipe;

            if(toggle)
            {
                swipe = ObjectAnimator.ofFloat(background, View.TRANSLATION_X,
                        TypedValue.applyDimension
                        (
                            TypedValue.COMPLEX_UNIT_DIP,
                            -180,
                            background.getContext().getResources().getDisplayMetrics()
                        ));
            }
            else
                swipe = ObjectAnimator.ofFloat(background, View.TRANSLATION_X, 0);


            swipe.addListener(new Animator.AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {
                    if(toggle) optionsLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    if(!toggle) optionsLayout.setVisibility(View.GONE);
                }

                @Override public void onAnimationCancel(Animator animation) {}
                @Override public void onAnimationRepeat(Animator animation) {}
            });
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
                            TypedValue.COMPLEX_UNIT_DIP, -180,
                            background.getContext().getResources().getDisplayMetrics()
                        )
                    );
            else
                background.setTranslationX(0);
        }

        openedIndex = toggle? index : -1;
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
//                    viewHolder.checkIfOpen(false, false);
                    toggleOptions(index, true, false);
                    fragmentManager.songDetails(song);
                    break;
                case 2:
                    //TODO: Add action (Edit Tags)
                    break;
                case 3:
                    //TODO: Add action (Delete)
                    delete(index, song);
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

    private void delete(final int index, Song song)
    {
        //TODO: Fix/Improve

        DialogInterface.OnClickListener positiveButtonListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
//                viewHolder.checkIfOpen(false, false);
                toggleOptions(index, false, false);
                openedIndex = -1;
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

    public boolean isInSortMode()
    {
        return inSortMode;
    }

    public void toggleMultiQueueMode(boolean toggleMultiQueueMode)
    {
        inMultiQueueMode = toggleMultiQueueMode;
        toggleOptionsVisibility(!toggleMultiQueueMode);

        listener.onToggleToolbar(toggleMultiQueueMode, true,  "Multi-Queue");
    }

    public void toggleSortMode(boolean toggleSortMode)
    {
        inSortMode = toggleSortMode;

        if(songList instanceof DragSortListView)
        {
            Log.d("songlist", "is a DSLV");
            ((DragSortListView) songList).setDragEnabled(inSortMode);
            Log.d("drag", String.valueOf(((DragSortListView) songList).isDragEnabled()));
        }

        toggleDragVisibility();
    }

    public void toggleSelectionMode(boolean toggleSelectionMode)
    {
        //TODO: Fix entering into selection mode on other sort

        inSelectionMode = toggleSelectionMode;
        toggleOptionsVisibility(!toggleSelectionMode);

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
                            .setBackgroundColor(i, alternateBackgroundColor, false);
                }
            }
            else
                songList.setAdapter
                (new SongListFastScrollAdapter
                        (songList, songs, sort, listener, data, player, fragmentManager)
                );
        }

        listener.onToggleToolbar(toggleSelectionMode, false, "0 items selected");
    }

    private void selectItem(int index)
    {
        selectedFlags.set(index, !selectedFlags.get(index));

        if(selectedFlags.get(index))
            selectedCount++;
        else
            selectedCount--;

        ((TextView) songList.getTag()).setText(String.valueOf(selectedCount) + " items selected");
    }

    private boolean itemIsSelected(int index)
    {
        return selectedFlags.get(index);
    }

    private void toggleOptionsVisibility(boolean toggle)
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

    private void toggleDragVisibility()
    {
        for(int i = 0;
            i <= songList.getLastVisiblePosition() - songList.getFirstVisiblePosition();
            i++)
        {
            if(items.get(i).isItem())
            {
//                ((SongListViewHolder) songList.getChildAt(i).getTag())
//                        .toggleDragMode(inSortMode);
                ImageView options = (ImageView) songList.getChildAt(i)
                        .findViewById(R.id.song_list_options);
                options.setColorFilter(inSortMode? Color.WHITE : Color.parseColor("#707070"),
                        PorterDuff.Mode.SRC_ATOP);
                options.setImageResource(inSortMode?
                        R.drawable.drag_vertical_24dp :
                        R.drawable.more_vertical_24dp);
            }
        }
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
    public Object getItem(int index)
    {
        if(getItemViewType(index) == type_item)
            return items.get(index).getSong();
        else
            return items.get(index).getSection();
    }

    @Override
    public long getItemId(int index)
    {
        return index;
    }

    @Override
    public int getItemViewType(int index)
    {
        return (items.get(index).isItem())? type_item : type_section;
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