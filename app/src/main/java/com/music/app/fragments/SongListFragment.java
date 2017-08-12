package com.music.app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.music.app.R;
import com.music.app.objects.Data;
import com.music.app.utils.Menuer;
import com.music.app.objects.Sorter;
import com.music.app.utils.adapters.SongListAdapter;
import com.music.app.utils.adapters.SongListFastScrollAdapter;

public class SongListFragment extends Fragment implements View.OnClickListener
{
    private Data data;

    private ListView songList;
    private Sorter.SortBy sort;

    public SongListFragment() {}

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        sort = Sorter.SortBy.title;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_song_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        View view = getView();

        //ListView
        songList = (ListView) view.findViewById(R.id.song_list_view);
        songList.setDividerHeight(0);
        songList.setFastScrollEnabled(true);

        sort(sort);

        //TODO: Add Swipe Actions

        //TODO: Add menu onClick listener
        getView().findViewById(R.id.song_list_toolbar_sort).setOnClickListener(this);
        getView().findViewById(R.id.song_list_toolbar_menu).setOnClickListener(this);
        getView().findViewById(R.id.selection_toolbar_close).setOnClickListener(this);
        getView().findViewById(R.id.selection_toolbar_menu).setOnClickListener(this);

        //TODO: Adapter toggle selection mode OFF on close button click
        Toolbar toolbar = (Toolbar) getView().findViewById(R.id.songlist_toolbar);
        toolbar.setTag(getView().findViewById(R.id.selection_toolbar_title));
        songList.setTag(toolbar);
    }

    @Override
    public void onResume()
    {
        super.onResume();
//        Messager.showMessage("YOW. RESUMED.", Snackbar.LENGTH_SHORT);
    }

    //TODO: Show Control Buttons on resume if on last index (Do when finished managing fragment navigation)

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.song_list_toolbar_sort:
                sortOptions(v);
                break;

            case R.id.song_list_toolbar_menu:
                menu(v);
                break;

            case R.id.selection_toolbar_close:
                ((SongListAdapter) songList.getAdapter()).toggleSelectionMode(false);
                break;

            case R.id.selection_toolbar_menu:
                selectionMenu(v);
                break;
        }
    }

    private void menu(View view)
    {
        PopupMenu.OnMenuItemClickListener listener =  new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {

                switch (item.getItemId())
                {
                    case R.id.multi_select:
                        ((SongListAdapter) songList.getAdapter()).toggleSelectionMode(true);
                        return true;

                    case R.id.multi_queue:
                        ((SongListAdapter) songList.getAdapter()).toggleMultiQueueMode(true);
                        return true;

                    default:
                        return false;
                }
            }
        };

        Menuer.createMenu(getContext(), view, R.menu.song_list_menu, listener);
    }

    private void selectionMenu(View view)
    {
        PopupMenu.OnMenuItemClickListener listener = new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.delete:
                        ((SongListAdapter) songList.getAdapter()).deleteSelectedItems();
                        return true;

                    default:
                        return false;
                }
            }
        };

        Menuer.createMenu(getContext(), view, R.menu.selection_menu, listener);
    }

    private void sortOptions(View view)
    {
        //TODO: Revise and Debug

        PopupMenu.OnMenuItemClickListener listener = new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                Toast.makeText(getContext(), "Debug this", Toast.LENGTH_SHORT).show();

                switch (item.getItemId())
                {
                    case R.id.sort_by_title:
//                        sort(Sorter.SortBy.title);
                        return true;

                    case R.id.sort_by_artist:
//                        sort(Sorter.SortBy.artist);
                        return true;

                    case R.id.sort_by_album:
//                        sort(Sorter.SortBy.album);
                        return true;

                    default:
                        return false;
                }
            }
        };

        Menuer.createMenu(getContext(), view, R.menu.sort_by_menu, listener);
    }

    private void sort(final Sorter.SortBy sort)
    {
        SongListAdapter adapter;

        if(sort != Sorter.SortBy.none)
            adapter = new SongListFastScrollAdapter(getContext(), Sorter.sort(Data.songs, sort), songList, sort);
        else
            adapter = new SongListAdapter(getContext(), songList, Data.songs, sort);

        songList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        this.sort = sort;
    }

    public ListView getSongList()
    {
        return songList;
    }
}
