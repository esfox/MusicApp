package com.music.app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.music.app.objects.Song;
import com.music.app.objects.Sorter;
import com.music.app.utils.Menuer;
import com.music.app.utils.adapters.SongListAdapter;
import com.music.app.utils.adapters.SongListFastScrollAdapter;
import com.music.app.utils.interfaces.QueueListener;
import com.music.app.utils.interfaces.ServiceListener;

import java.util.ArrayList;

public class SongListFragment extends Fragment implements View.OnClickListener
{
    private ListView songList;
    private Sorter.SortBy sort;
    private ArrayList<Song> songs;

    private SongListAdapter adapter;

    private FloatingActionButton done;

    private FragmentManager fragmentManager;

    private ServiceListener serviceListener;
    private QueueListener queueListener;

    public SongListFragment() {}

    public void setSongs(ArrayList<Song> songs)
    {
        this.songs = songs;
    }
    public void initialize(ServiceListener serviceListener,
                           QueueListener queueListener,
                           FragmentManager fragmentManager)
    {
        this.serviceListener = serviceListener;
        this.queueListener = queueListener;
        this.fragmentManager = fragmentManager;
    }

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
        done = (FloatingActionButton) getView().findViewById(R.id.song_list_done);
        done.hide();
        done.setOnClickListener(this);
        getView().findViewById(R.id.selection_toolbar_close).setOnClickListener(this);
        getView().findViewById(R.id.selection_toolbar_menu).setOnClickListener(this);

        //TODO: Adapter toggle selection mode OFF on close button click
        Toolbar toolbar = (Toolbar) getView().findViewById(R.id.song_list_toolbar);
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
            case R.id.selection_toolbar_close:
                adapter.toggleSelectionMode(false);
                adapter.toggleMultiQueueMode(false);
                done.hide();
                break;

            case R.id.selection_toolbar_menu:
                selectionMenu(v);
                break;

            case R.id.song_list_done:
                adapter.toggleSelectionMode(false);
                adapter.toggleMultiQueueMode(false);
                done.hide();
                break;
        }
    }

    public void menu(View view)
    {
        PopupMenu.OnMenuItemClickListener listener =  new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {

            switch (item.getItemId())
            {
                case R.id.multi_select:
                    adapter.toggleSelectionMode(true);
                    return true;

                case R.id.multi_queue:
                    adapter.toggleMultiQueueMode(true);

                    done.show();
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
                        adapter.deleteSelectedItems();
                        return true;

                    default:
                        return false;
                }
            }
        };

        Menuer.createMenu(getContext(), view, R.menu.selection_menu, listener);
    }

    public void sortOptions(View view)
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

//        ArrayList<Song> songs = Data.songs;

        if(sort != Sorter.SortBy.none)
            adapter = new SongListFastScrollAdapter
                (
                    getContext(),
                    Sorter.sort(songs, sort),
                    songList,
                    sort
                );
        else
            adapter = new SongListAdapter(getContext(), songList, songs, sort);

        adapter.setServiceListener(serviceListener);
        adapter.setQueueListener(queueListener);
        adapter.setFragmentManager(fragmentManager);

        songList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        
        this.adapter = adapter;
        this.sort = sort;
    }

    public ListView getSongList()
    {
        return songList;
    }
}
