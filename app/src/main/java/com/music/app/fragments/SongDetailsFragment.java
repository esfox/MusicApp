package com.music.app.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.music.app.R;
import com.music.app.objects.Song;
import com.music.app.utils.Timestamper;

import java.io.File;

public class SongDetailsFragment extends Fragment
{
    private Song song;

    public SongDetailsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_song_details, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        try
        {
            Glide.with(getContext()).load(new File(song.getCoverPath()))
                    .into(((ImageView) getView().findViewById(R.id.album_cover)));
        } catch (Exception ignored) {}
        ((TextView) getView().findViewById(R.id.queue_item_title)).setText(song.getTitle());
        ((TextView) getView().findViewById(R.id.queue_item_artist)).setText(song.getArtist());
        ((TextView) getView().findViewById(R.id.album_artist)).setText(song.getAlbumArtist());
        ((TextView) getView().findViewById(R.id.album)).setText(song.getAlbum());
        ((TextView) getView().findViewById(R.id.year)).setText(song.getReleaseYear());
        ((TextView) getView().findViewById(R.id.genre)).setText(song.getGenre());
        ((TextView) getView().findViewById(R.id.duration)).setText(Timestamper.getTimestamp
                (song.getDuration()));
    }

    public void setSong(Song pSong)
    {
        song = pSong;
    }
}
