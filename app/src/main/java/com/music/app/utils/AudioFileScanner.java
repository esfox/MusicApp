package com.music.app.utils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;

import com.music.app.MainActivity;
import com.music.app.R;
import com.music.app.database.SongDatabaseHelper;
import com.music.app.fragments.SongListFragment;
import com.music.app.objects.Data;
import com.music.app.objects.PlayQueue;
import com.music.app.objects.Song;
import com.music.app.utils.adapters.SongListAdapter;

import java.util.ArrayList;
import java.util.UUID;

public class AudioFileScanner
{
    private Context context;
    private ArrayList<Song> songs;
    private String filename;

    private boolean stored = false;

    public AudioFileScanner(Context pContext)
    {
        context = pContext;
        songs = new ArrayList<>();
    }

    public void scanAudio()
    {
        if(!stored)
        {
            BackgroundScanner backgroundProcess = new BackgroundScanner();
            backgroundProcess.execute();
        }
        else
        {
            //Query DB
        }
    }

    /*
    * TODO
    *
    * Fix code
    * Send albumID as parameter instead in setCovers
    * */

    private void scan()
    {
        Uri external = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        final String sortOrder = MediaStore.Audio.AudioColumns.TITLE + " COLLATE LOCALIZED ASC";

        Cursor cursor = null;
        try
        {
            cursor = context.getContentResolver().query(external, projection, selection, null, sortOrder);
            if(cursor != null)
            {
                cursor.moveToFirst();
                while(!cursor.isAfterLast())
                {
                    this.filename = cursor.getString(2);
                    if(filename.endsWith(".mp3"))
                    {
                        Song song = new Song();
                        song.setUUID(UUID.randomUUID().toString());
                        song.setId(Long.parseLong(cursor.getString(0)));
                        song.setPath(cursor.getString(1));
                        song.setFilename(filename);
                        song.setTitle(cursor.getString(3));
                        song.setArtist(cursor.getString(4));
                        song.setAlbum(cursor.getString(5));
                        song.setReleaseYear(cursor.getString(6));
                        song.setDuration(getDuration(cursor.getString(7)));
                        song.setAlbumID(cursor.getLong(8));
                        songs.add(song);
                    }

                    cursor.moveToNext();
                }
            }
        }
        catch(Exception e) { e.printStackTrace(); }
        finally
        {
            if(cursor != null)
                cursor.close();
        }

        setGenres();
    }

    private void setGenres()
    {
        Uri uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Genres._ID,
                MediaStore.Audio.Genres.NAME
        };

        Cursor cursor = null;
        try
        {
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if(cursor != null)
            {
                cursor.moveToFirst();
                while(!cursor.isAfterLast())
                {
                    Uri genreURI = MediaStore.Audio.Genres.Members.getContentUri("external", cursor.getLong(0));
                    String[] gfilename = { MediaStore.Audio.Media.DISPLAY_NAME };
                    Cursor c = null;
                    try
                    {
                        c = context.getContentResolver().query(genreURI, gfilename, null, null, null);
                        if(c != null)
                        {
                            c.moveToFirst();
                            while(!c.isAfterLast())
                            {
                                for(Song song: songs)
                                {
                                    if(song.getFilename().equals(c.getString(0)))
                                        song.setGenre(cursor.getString(1));
                                }

                                c.moveToNext();
                            }
                        }
                    }
                    catch(Exception e) { e.printStackTrace(); }
                    finally
                    {
                        if(c != null)
                            c.close();
                    }
                    cursor.moveToNext();
                }
            }
        }
        catch(Exception e) { e.printStackTrace(); }
        finally
        {
            if(cursor != null)
                cursor.close();
        }
    }

    private void setCovers()
    {
        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.ALBUM_ART
        };

        Cursor cursor = null;
        try
        {
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if(cursor != null)
            {
                cursor.moveToFirst();
                while(!cursor.isAfterLast())
                {
                    Drawable cover;
                    if(cursor.getString(2) != null)
                    {
                        try
                        {
                            cover = Drawable.createFromPath(cursor.getString(2));
                        }
                        catch(OutOfMemoryError e)
                        {
                            e.printStackTrace();
                            cover = context.getResources().getDrawable(R.drawable.library_music_48dp);
                        }
                    }
                    else
                        cover = context.getResources().getDrawable(R.drawable.library_music_48dp);

                    for(Song song: songs)
                    {
                        if(song.getAlbumID().equals(cursor.getLong(0)))
                        {
                            song.setAlbumArtist(cursor.getString(1));
                            song.setCover(cover);
                        }
                    }

                    cursor.moveToNext();
                }
            }
        }
        catch(Exception e) { e.printStackTrace(); }
        finally
        {
            if(cursor != null)
                cursor.close();
        }

        store();
    }

    private void store()
    {
        SongDatabaseHelper databaseHelper = new SongDatabaseHelper(context);
        for(Song song: songs)
            databaseHelper.add(song);
    }

    @SuppressLint("DefaultLocale")
    private String getDuration(String value)
    {
        Long length = Long.parseLong(value);
        return String.valueOf(length / 60000) + ":" + String.format("%02d", (length % 60000) / 1000);
    }

    private class BackgroundScanner extends AsyncTask<Void, Void, Void>
    {
        private ProgressDialog progressDialog = new ProgressDialog(context);

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog.setMessage("\tLoading Music...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            scan();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

            Data.songs = songs;
            PlayQueue.queue = songs;

            ((MainActivity) context).fragmentManager.songListFragment = new SongListFragment();
            ((MainActivity) context).fragmentManager.songList();

            AlbumCoverScanner albumCoverScanner = new AlbumCoverScanner();
            albumCoverScanner.execute();
        }
    }

    private class AlbumCoverScanner extends AsyncTask<Void, Void, Void>
    {
        private Snackbar snackbar;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            snackbar = Snackbar.make(((MainActivity) context).uiManager.playButton, "Loading album covers...", Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            setCovers();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            snackbar.dismiss();

            ((SongListAdapter) ((MainActivity) context).fragmentManager.songListFragment.getSongList().getAdapter()).notifyDataSetChanged();
        }
    }
}