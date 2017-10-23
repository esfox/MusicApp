package com.music.app.fragments;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.music.app.R;
import com.music.app.adapters.PlaylistsListAdapter;
import com.music.app.objects.Data;
import com.music.app.objects.Playlist;
import com.music.app.utils.Dialoger;
import com.music.app.utils.PlaylistManager;
import com.music.app.views.Notice;

import java.util.ArrayList;

public class PlaylistsListFragment extends Fragment
{
    private Data data;
    private FragmentManager fragmentManager;

    public PlaylistsListFragment() {}

    public void initialize(Data data, FragmentManager fragmentManager)
    {
        this.data = data;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_playlists_list, container, false);

        RecyclerView playlists = (RecyclerView) v.findViewById(R.id.playlists_list);
        playlists.setHasFixedSize(true);
        playlists.setLayoutManager(new LinearLayoutManager(getContext()));

        final PlaylistsListAdapter adapter = new PlaylistsListAdapter
                (getContext(), data, fragmentManager);
//                (temp(), data, fragmentManager);
        playlists.setAdapter(adapter);

        v.findViewById(R.id.playlists_add).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                createPlaylist(adapter);
            }
        });
        v.findViewById(R.id.playlists_add).setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                adapter.clearPlaylists(getContext());
                return true;
            }
        });

        return v;
    }

    private void createPlaylist(final PlaylistsListAdapter adapter)
    {
        @SuppressLint("InflateParams")
        View dialogLayout = getActivity().getLayoutInflater()
                .inflate(R.layout.create_new_playlist_dialog, null);
        final TextInputLayout playlistName = (TextInputLayout)
                dialogLayout.findViewById(R.id.playlist_name);

        final AlertDialog dialog = Dialoger.getDialogBuilder(getContext())
                .setView(dialogLayout)
                .setPositiveButton("Create", null)
                .setNeutralButton("Choose Songs", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener()
        {
            @Override
            public void onShow(DialogInterface dialogInterface)
            {
        ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    final String input = playlistName.getEditText().getText().toString();
                    if(input.equals(""))
                        dialogInputError("Please enter a name for the playlist", playlistName);
                    else
                    {
                        if(adapter.addPlaylist(getContext(), input))
                            dialog.dismiss();
                        else
                            dialogInputError("That playlist is already existing.", playlistName);
                    }
                }
            });
        ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_NEUTRAL)
            .setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Notice notice = new Notice(getContext());
                    notice.setNoticeText("Sorry, this part has not been developed yet");
                    notice.show();
                }
            });
            }
        });
        dialog.show();
    }

    private void dialogInputError(String errorMessage, TextInputLayout playlistName)
    {
        final EditText editText = playlistName.getEditText();
        Drawable icon = ResourcesCompat.getDrawable(getResources(),
                            R.drawable.error_24dp, null);
        icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
        editText.setError(errorMessage, icon);
        editText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged
                    (CharSequence sequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged
                    (CharSequence sequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable)
            {
                editText.setError(null);
            }
        });
    }

    private ArrayList<Playlist> temp()
    {
        long[] tempSongs = new long[40];
        for (int i = 0; i < tempSongs.length; i++)
            tempSongs[i] = data.songs().get(i).getID();

        ArrayList<Playlist> temp = new ArrayList<>();
        for (int i = 0; i < tempSongs.length; i++)
        {
            Playlist playlist = new Playlist("Playlist " + String.valueOf(i+1));
            playlist.setTempSongs(tempSongs);
            temp.add(playlist);
        }

        return temp;
    }
}
