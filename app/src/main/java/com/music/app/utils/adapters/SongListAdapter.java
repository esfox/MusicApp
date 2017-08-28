package com.music.app.utils.adapters;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.music.app.R;
import com.music.app.objects.Song;
import com.music.app.objects.Sorter;
import com.music.app.views.SongListViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class SongListAdapter extends BaseAdapter {
    public static final int type_item = 0;
    private static final int type_section = 1;
    private Context context;
    private ListView songList;
    private ArrayList<Song> songs;
    private Sorter.SortBy sort;
    private ArrayList<Item> items;
    private int openedOptionsPosition = -1;
    private boolean inMultiQueueMode = false;
    private boolean inSelectionMode = false;

    private ArrayList<Boolean> selectedFlags;
    private int selectedCount = 0;

    public SongListAdapter(Context pContext, ListView listView, ArrayList<Song> pSongs, Sorter.SortBy pSort) {
        context = pContext;
        songList = listView;
        songs = pSongs;
        sort = pSort;

        items = new ArrayList<>();

        HashMap<Integer, String> sections = new HashMap<>();

        selectedFlags = new ArrayList<>();

        try {
            for (int i = 0; i < songs.size(); i++) {
                selectedFlags.add(false);

                if (pSort != Sorter.SortBy.title) {
                    String section = "";

                    if (pSort == Sorter.SortBy.artist)
                        section = songs.get(i).getArtist();
                    else if (pSort == Sorter.SortBy.album)
                        section = songs.get(i).getAlbum();

                    if (!sections.containsValue(section)) {
                        sections.put(i, section);
                        items.add(new Item(section));
                    }
                }

                items.add(new Item(songs.get(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
        TODO: Add more sort options
        TODO: Implement views show certain details on different sort (e.g. album is shown when sorted by album
        TODO: Add Swipe Effect to open options (Optional)
        */
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);

        final SongListViewHolder viewHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (type == type_item)
                view = inflater.inflate(R.layout.song_list_item, parent, false);
            else
                view = inflater.inflate(R.layout.song_list_section_header, parent, false);

            viewHolder = new SongListViewHolder(view, songList, type, sort);
            view.setTag(viewHolder);

            if (type == type_item)
                viewHolder.setClickListeners();
        } else
            viewHolder = (SongListViewHolder) view.getTag();

        viewHolder.setPosition(position);

        if (type == type_item) {
            viewHolder.setSong(items.get(position).getSong());

            checkIfPositionIsOpened(position, viewHolder);
            checkIfPositionIsSelected(position, viewHolder);
        } else
            viewHolder.sectionText.setText(items.get(position).getSection());

        return view;
    }

    private void checkIfPositionIsOpened(int position, SongListViewHolder viewHolder) {
        if (position == openedOptionsPosition)
            viewHolder.toggleOptions(false, true);
        else
            viewHolder.toggleOptions(false, false);

//        if(openedOptionsPosition <= songList.getFirstVisiblePosition() &&
//           openedOptionsPosition >= songList.getLastVisiblePosition())
//            openedOptionsPosition = -1;
    }

    private void checkIfPositionIsSelected(int position, SongListViewHolder viewHolder) {
        if (inSelectionMode) {
            viewHolder.options.setVisibility(View.INVISIBLE);
            if (selectedFlags.get(position))
                viewHolder.background.setBackgroundResource(R.color.colorPrimaryDarker);
            else
                viewHolder.setBackgroundColor(position);
        } else
            viewHolder.options.setVisibility(View.VISIBLE);
    }

    public int getOpenedOptionsPosition() {
        return openedOptionsPosition;
    }

    public void setOpenedOptionsPosition(int pOpenedPosition) {
        openedOptionsPosition = pOpenedPosition;
    }

    @SuppressWarnings("SameParameterValue")
    public void toggleMultiQueueMode(boolean toggleMultiQueueMode) {
        //TODO: Untoggle when going to other fragments (Do after managing fragment navigation)

        inMultiQueueMode = toggleMultiQueueMode;

        Toolbar toolbar = (Toolbar) songList.getTag();

        if (inMultiQueueMode) {
            toolbar.setVisibility(View.VISIBLE);
            ObjectAnimator fade = ObjectAnimator.ofFloat(toolbar, View.ALPHA, 1f);
            fade.setDuration(150);
            fade.start();

            for (int i = 0; i <= songList.getLastVisiblePosition() - songList.getFirstVisiblePosition(); i++) {
                if (items.get(i).isItem())
                    ((SongListViewHolder) songList.getChildAt(i).getTag()).toggleOptions(true, false);
            }

            ((TextView) toolbar.getTag()).setText("Multi Queue");
        } else {
            toolbar.setAlpha(0);
            toolbar.setVisibility(View.GONE);
        }
    }

    public void toggleSelectionMode(boolean toggleSelectionMode) {
        //TODO: Untoggle when going to other fragments (Do after managing fragment navigation)

        //TODO: Fix entering into selection mode on other sort

        inSelectionMode = toggleSelectionMode;

        Toolbar toolbar = (Toolbar) songList.getTag();

        if (inSelectionMode) {
            toolbar.setVisibility(View.VISIBLE);
            ObjectAnimator fade = ObjectAnimator.ofFloat(toolbar, View.ALPHA, 1f);
            fade.setDuration(150);
            fade.start();

            ((TextView) toolbar.getTag()).setText(String.valueOf(selectedCount) + " items selected");

            for (int i = 0; i <= songList.getLastVisiblePosition() - songList.getFirstVisiblePosition(); i++) {
                if (items.get(i).isItem()) {
                    ((SongListViewHolder) songList.getChildAt(i).getTag()).options.setVisibility(View.INVISIBLE);
                    ((SongListViewHolder) songList.getChildAt(i).getTag()).toggleOptions(true, false);
                }
            }
        } else {
            if (sort == Sorter.SortBy.title) {
                selectedCount = 0;

                for (int i = 0; i < selectedFlags.size(); i++)
                    selectedFlags.set(i, false);

                for (int i = 0; i <= songList.getLastVisiblePosition() - songList.getFirstVisiblePosition(); i++) {
                    SongListViewHolder viewHolder = (SongListViewHolder) songList.getChildAt(i).getTag();
                    viewHolder.setBackgroundColor(i);
                    viewHolder.options.setVisibility(View.VISIBLE);
                }
            } else
                songList.setAdapter(new SongListFastScrollAdapter(context, songs, songList, sort));

            toolbar.setAlpha(0);
            toolbar.setVisibility(View.GONE);
        }
    }

    public boolean inMultiQueueMode() {
        return inMultiQueueMode;
    }

    public boolean inSelectionMode() {
        return inSelectionMode;
    }

    public void selectItem(int position) {
        selectedFlags.set(position, !selectedFlags.get(position));

        if (selectedFlags.get(position))
            selectedCount++;
        else
            selectedCount--;

        ((TextView) ((Toolbar) songList.getTag()).getTag()).setText(String.valueOf(selectedCount) + " items selected");
    }

    public boolean itemIsSelected(int position) {
        return selectedFlags.get(position);
    }

    // TODO: FIX DELETION
    public void deleteSelectedItems() {
        //TODO: Delete from storage
        //TODO: Deletion in other sort

        final ArrayList<Item> selectedItems = new ArrayList<>();
        for (int i = 0; i < selectedFlags.size(); i++)
            if (selectedFlags.get(i)) selectedItems.add(items.get(i));

        final StringBuilder[] str = {new StringBuilder()};
        str[0].append("");
        for (Item item : selectedItems) {
            File file = new File(item.getSong().getPath());
            if (file.exists() && file.isFile())
                if (!file.delete()) str[0].append(item.getSong().getFilename()).append("\n\n");
        }
        new AlertDialog.Builder(context).setTitle("Deletion")
                .setMessage("Are you sure you want to delete selected items?\n" + str[0] +
                        "\n\nDelete: Deletes files from storage!!!\nRemove: Removes files from list.")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        str[0] = new StringBuilder();
                        for (Item item : selectedItems) {
                            File file = new File(item.getSong().getPath());
                            if (file.exists() && file.isFile())
                                if (!file.delete()) str[0].append(item.getSong().getFilename()).append("\n");
                        }
                        str[0].append("\nCould not be deleted.");
                        Toast.makeText(context, str[0], Toast.LENGTH_LONG).show();
                        items.removeAll(selectedItems);
                        notifyDataSetChanged();
                    }
                })
                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        items.removeAll(selectedItems);
                        notifyDataSetChanged();
                    }
                }).show();

        toggleSelectionMode(false);
    }

    // TODO: FIX DELETION
    public void deleteItem(final int position) {
        setOpenedOptionsPosition(-1);

        new AlertDialog.Builder(context).setTitle("Deletion")
                .setMessage("Are you sure you want to delete " + songs.get(position).getFilename()
                        + "?\nPath: " + songs.get(position).getPath() + "\n\nDelete: Deletes file from storage!!!\nRemove: Removes file from list.")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        File file = new File(songs.get(position).getPath());
                        if (file.exists() && file.isFile())
                            if (!file.delete())
                                Toast.makeText(context, songs.get(position).getFilename() + " couldn't be deleted.", Toast.LENGTH_LONG).show();

                        songs.remove(position);
                        notifyDataSetChanged();
                    }
                })
                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        songs.remove(position);
                        notifyDataSetChanged();
                    }
                }).show();
    }

    ArrayList<Item> getItems() {
        return items;
    }

    @Override
    public Object getItem(int position) {
        if (getItemViewType(position) == type_item)
            return items.get(position).getSong();
        else
            return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return (items.get(position).isItem()) ? type_item : type_section;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    class Item {
        private int type;
        private Song song = null;
        private String section = "";

        Item(Song pSong) {
            song = pSong;
            type = type_item;
        }

        Item(String pSection) {
            section = pSection;
            type = type_section;
        }

        Song getSong() {
            return song;
        }

        String getSection() {
            return section;
        }

        boolean isItem() {
            return (type == type_item);
        }
    }
}