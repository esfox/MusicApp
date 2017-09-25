package com.music.app.fragments;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.mobeta.android.dslv.DragSortListView;
import com.music.app.R;
import com.music.app.adapters.SongListFastScrollAdapter;
import com.music.app.interfaces.SongListAdapterListener;
import com.music.app.objects.Data;
import com.music.app.objects.Player;
import com.music.app.objects.Playlist;
import com.music.app.objects.Sorter;
import com.music.app.utils.Menuer;

import java.util.ArrayList;
import java.util.Arrays;

public class PlaylistFragment extends Fragment implements
        View.OnClickListener,
        SongListAdapterListener
{
    private Playlist playlist;
    private Data data;
    private Player player;
    private FragmentManager fragmentManager;

    private View toolbar;
    private TextView toolbarText;
    private FloatingActionButton done;
    private SongListFastScrollAdapter adapter;

    public PlaylistFragment() {}

    public void initialize
        (
            Playlist playlist,
            Data data,
            Player player,
            FragmentManager fragmentManager
        )
    {
        this.playlist = playlist;
        this.data = data;
        this.player = player;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        int trackCount = playlist.getTempSongs().length;

        //TODO: Add sort button in toolbar
        Toolbar details = ((Toolbar) view.findViewById(R.id.playlist_details));
        details.setTitle(playlist.getName());
        details.setSubtitle(String.valueOf(trackCount) +
                ((trackCount > 1)? " tracks" : " track"));
        details.inflateMenu(R.menu.menu_playlist);
        details.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.playlist_action_sort:
                        adapter.toggleSortMode(!adapter.isInSortMode());
                        break;
                }
                return false;
            }
        });

        DragSortListView playlistview = (DragSortListView) view.findViewById(R.id.playlist_view);
        playlistview.setDividerHeight(0);

        adapter = new SongListFastScrollAdapter
                (playlistview, new ArrayList<>(Arrays.asList(playlist.getTempSongs())),
                        Sorter.SortBy.title, this, data, player, fragmentManager);
        adapter.doNotAlternateBackgroundColor();
        playlistview.setAdapter(adapter);

        toolbar = view.findViewById(R.id.song_list_toolbar);
        toolbarText = (TextView) toolbar.findViewById(R.id.song_list_toolbar_title);
        toolbar.setTag(view.findViewById(R.id.song_list_toolbar_shadow));
        playlistview.setTag(toolbarText);

        done = (FloatingActionButton) view.findViewById(R.id.song_list_done);
        done.setOnClickListener(this);
        done.hide();
        view.findViewById(R.id.song_list_toolbar_close).setOnClickListener(this);
        view.findViewById(R.id.song_list_toolbar_menu).setOnClickListener(this);

        return view;
    }

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
}
