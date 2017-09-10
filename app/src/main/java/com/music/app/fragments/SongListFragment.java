package com.music.app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.music.app.R;
import com.music.app.objects.Data;
import com.music.app.objects.Song;
import com.music.app.objects.Sorter;
import com.music.app.utils.Menuer;
import com.music.app.utils.adapters.SongListAdapter;
import com.music.app.utils.adapters.SongListFastScrollAdapter;
import com.music.app.interfaces.QueueListener;
import com.music.app.interfaces.ServiceListener;
import com.music.app.interfaces.SongListAdapterListener;
import com.music.app.views.Notice;

import java.util.ArrayList;

public class SongListFragment extends Fragment implements
        View.OnClickListener,
        SongListAdapterListener
{
    private ListView songList;
    private FloatingActionButton done;
    private View toolbar;
    private TextView toolbarText;

    private ArrayList<Song> songs;
    private Sorter.SortBy sort;

    private SongListAdapter adapter;

    private Data data;
    private ServiceListener serviceListener;
    private QueueListener queueListener;
    private FragmentManager fragmentManager;

    public SongListFragment() {}

    public void setSongs(ArrayList<Song> songs)
    {
        this.songs = songs;
    }
    public void initialize(Data data,
                           ServiceListener serviceListener,
                           QueueListener queueListener,
                           FragmentManager fragmentManager)
    {
        this.data = data;
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
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);

        //ListView
        songList = (ListView) view.findViewById(R.id.song_list_view);
        songList.setDividerHeight(0);
        songList.setFastScrollEnabled(true);

        sort(sort);

        //TODO: Add Swipe Actions

        //TODO: Add menu onPlay listener
        done = (FloatingActionButton) view.findViewById(R.id.song_list_done);
        done.setOnClickListener(this);
        done.hide();
        view.findViewById(R.id.song_list_toolbar_close).setOnClickListener(this);
        view.findViewById(R.id.song_list_toolbar_menu).setOnClickListener(this);

        //TODO: Adapter toggle selection mode OFF on close button click
        toolbar = view.findViewById(R.id.song_list_toolbar);
        toolbarText = (TextView) toolbar.findViewById(R.id.song_list_toolbar_title);
        toolbar.setTag(view.findViewById(R.id.song_list_toolbar_shadow));
        songList.setTag(toolbarText);

        return view;
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
            case R.id.song_list_toolbar_close:
                adapter.toggleSelectionMode(false);
                adapter.toggleMultiQueueMode(false);
                break;

            case R.id.song_list_toolbar_menu:
                selectionMenu(v);
                break;

            case R.id.song_list_done:
                adapter.toggleSelectionMode(false);
                adapter.toggleMultiQueueMode(false);
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
                boolean handled = false;

                switch (item.getItemId())
                {
                    case R.id.multi_select:
                        adapter.toggleSelectionMode(true);
                        done.setVisibility(View.VISIBLE);
                        handled = true;
                        break;

                    case R.id.multi_queue:
                        adapter.toggleMultiQueueMode(true);
                        done.setVisibility(View.VISIBLE);
                        handled = true;
                        break;

                    default:
                        handled = false;
                        break;
                }

                return handled;
            }
        };

        Menuer.createMenu(getContext(), view, R.menu.song_list_menu, listener);
    }

    @Override
    public void onToggleToolbar(boolean toggle, boolean toggleMultiQueue, String toolbarText)
    {
        int visibility = (toggle)? View.VISIBLE : View.GONE;

        this.toolbarText.setText(toolbarText);

        toolbar.setVisibility(visibility);
        ((View) toolbar.getTag()).setVisibility(visibility);

        if(toggle) done.show();
        else done.hide();


        if(toggleMultiQueue)
            toolbar.findViewById(R.id.song_list_toolbar_menu)
                    .setVisibility((toggle)? View.GONE : View.VISIBLE);
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

                Notice notice = new Notice(getContext());
                notice.setNoticeText("Debug this!");
                notice.show();

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
                    data,
                    sort
                );
        else
            adapter = new SongListAdapter(getContext(), songList, songs, data, sort);

        adapter.setSongListAdapterListener(this);
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
