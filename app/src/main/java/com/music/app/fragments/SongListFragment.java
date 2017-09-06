package com.music.app.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.music.app.R;
import com.music.app.objects.Data;
import com.music.app.objects.Song;
import com.music.app.objects.Sorter;
import com.music.app.utils.Dialoger;
import com.music.app.utils.Menuer;
import com.music.app.utils.adapters.SongListAdapter;
import com.music.app.utils.adapters.SongListFastScrollAdapter;
import com.music.app.utils.interfaces.QueueListener;
import com.music.app.utils.interfaces.ServiceListener;
import com.music.app.utils.interfaces.SongListAdapterListener;
import com.wooplr.spotlight.SpotlightView;
import com.wooplr.spotlight.utils.SpotlightListener;

import java.util.ArrayList;
import java.util.UUID;

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

        //TODO: Add menu onClick listener
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

            switch (item.getItemId())
            {
                case R.id.multi_select:
                    adapter.toggleSelectionMode(true);
                    done.setVisibility(View.VISIBLE);
                    return true;

                case R.id.multi_queue:
                    adapter.toggleMultiQueueMode(true);
                    done.setVisibility(View.VISIBLE);
                    return true;

                default:
                    return false;
            }
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

    @Override
    public void onQueuePrompt(final int index)
    {
        if(data.currentSongIsNotNull())
        {
            if(data.queuePrompt())
            {
                AlertDialog.Builder dialog = Dialoger.getDialogBuilder(getContext());
                dialog.setTitle("Multi-Queue");
                dialog.setMessage("It looks like you are trying to queue a song.\n" +
                        "I suggest you try the Multi-Queue mode.\n" +
                        "In Multi-Queue, you can just tap on the song to queue it immediately.\n\n" +
                        "You can turn on Multi-Queue automatically when you queue a song " +
                        "by enabling it in the settings." +
                        "\n(SETTINGS NOT YET MADE.)");
                dialog.setPositiveButton("Got It", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        data.updateQueuePrompt(false);
                        new SpotlightView.Builder((Activity) getContext())
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
                                .target(((Activity) getContext()).findViewById(R.id.multi_queue))
                                .usageId(String.valueOf(UUID.randomUUID()))
                                .setListener(new SpotlightListener()
                                {
                                    @Override
                                    public void onUserClicked(String s)
                                    {
                                        adapter.queue(index);
                                    }
                                })
                                .show();
                    }
                });
                dialog.show();
            }
            else
                adapter.queue(index);
        }
        else
            Toast.makeText
                (
                    getContext(),
                    "There is no currently playing song.",
                    Toast.LENGTH_SHORT
                ).show();
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
