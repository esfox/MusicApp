package com.music.app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.music.app.MainActivity;
import com.music.app.R;
import com.music.app.database.SongDatabaseHelper;
import com.music.app.interfaces.AudioScannerListener;
import com.music.app.interfaces.AudioScannerTaskListener;
import com.music.app.objects.Data;
import com.music.app.objects.Song;
import com.music.app.utils.asynctasks.AlbumArtScanner;
import com.music.app.utils.asynctasks.MediaQuery;
import com.music.app.utils.asynctasks.MediaScanner;
import com.music.app.utils.asynctasks.MediaStorer;
import com.music.app.views.FirstLaunch;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class AudioScanner implements AudioScannerTaskListener
{
    private Context context;
    private ArrayList<Song> songs;
    private Data data;
    private AudioScannerListener audioScannerListener;

    private Intent loading;

    private static WeakReference<Activity> firstLaunch;

    public static void firstLaunch(Activity firstLaunch)
    {
        AudioScanner.firstLaunch = new WeakReference<>(firstLaunch);
    }

    public AudioScanner(Context pContext, AudioScannerListener pAudioScannerListener, Data pData)
    {
        context = pContext;
        audioScannerListener = pAudioScannerListener;
        data = pData;
        songs = new ArrayList<>();

        loading = new Intent(context, FirstLaunch.class);
        loading.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    public void scanAudio()
    {
        if(!data.stored())
            new MediaScanner(this).execute();
        else
            new MediaQuery(new WeakReference<>(context), this).execute();
    }

    /*
    * TODO
    *
    * Fix code
    * Send albumID as parameter instead in scanCovers
    * */

    @Override
    public void firstLaunch()
    {
        context.startActivity(loading);
    }

    @Override
    public void scan()
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
            cursor = context.getContentResolver()
                    .query(external, projection, selection, null, sortOrder);
            if(cursor != null)
            {
                cursor.moveToFirst();
                while(!cursor.isAfterLast())
                {
                    String filename = cursor.getString(2);
                    if(filename.endsWith(".mp3"))
                    {
                        Song song = new Song();
                        song.setId(Long.parseLong(cursor.getString(0)));
                        song.setPath(cursor.getString(1));
                        song.setFilename(filename);
                        song.setTitle(cursor.getString(3));
                        song.setArtist(cursor.getString(4));
                        song.setAlbum(cursor.getString(5));
                        song.setReleaseYear(cursor.getString(6));
                        song.setDuration(cursor.getLong(7));
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

    @Override
    public void scanComplete()
    {
        if(data.stored())
            audioScannerListener.onScanComplete(songs);

        new AlbumArtScanner(new WeakReference<>(context), this).execute();
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
                    Uri genreURI = MediaStore.Audio.Genres.Members
                            .getContentUri("external", cursor.getLong(0));
                    String[] gfilename = { MediaStore.Audio.Media.DISPLAY_NAME };
                    Cursor c = null;
                    try
                    {
                        c = context.getContentResolver()
                                .query(genreURI, gfilename, null, null, null);
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

    @Override
    public void scanCovers()
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
//                            BitmapFactory.Options options = new BitmapFactory.Options();
//                            options.inScaled = true;
//                            options.inSampleSize = 2;
//                            options.inDensity = 100;
//                            options.inTargetDensity = 100 * options.inSampleSize;
//
//                            cover = new BitmapDrawable(context.getResources(),
//                                    BitmapFactory.decodeFile(cursor.getString(2), options));

                            File f = new File(cursor.getString(2));
                            if (f.exists() && f.isFile())
                            {
                                cover = Glide.with(context)
                                        .load(new File(cursor.getString(2)))
                                        .skipMemoryCache(true)
                                        .into(100, 100)
                                        .get();
                            }
                            else
                                cover = ResourcesCompat.getDrawable(context.getResources(),
                                        R.drawable.album_art_placeholder, null);
                        }
                        catch(OutOfMemoryError e)
                        {
                            e.printStackTrace();
                            cover = ResourcesCompat.getDrawable(context.getResources(),
                                            R.drawable.album_art_placeholder, null);
                        }
                    }
                    else
                        cover = ResourcesCompat.getDrawable(context.getResources(),
                                R.drawable.album_art_placeholder, null);

                    for(Song song: songs)
                    {
                        if(song.getAlbumID().equals(cursor.getLong(0)))
                        {
                            song.setAlbumArtist(cursor.getString(1));
                            song.setCover(cover);
                            song.setCoverPath(cursor.getString(2));
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
    }

    @Override
    public void initializeSongs(ArrayList<Song> songs)
    {
        this.songs = songs;
    }

    @Override
    public void updateAudioScannerListener()
    {
        audioScannerListener.onUpdate();
    }

    @Override
    public void startMediaStorer()
    {
        if(!data.stored())
            new MediaStorer(this).execute();
    }

    @Override
    public void storeMedia()
    {
        SongDatabaseHelper databaseHelper = new SongDatabaseHelper(context);
        for (Song song : songs)
            databaseHelper.add(song);
        data.updateStored(true);
    }

    @Override
    public void finishedStoring()
    {
        Log.d("Database", "Data saved");
        firstLaunch.get().finish();

        Intent back = new Intent(firstLaunch.get(), MainActivity.class);
        context.startActivity(back);
    }

    //    private class BackgroundScanner extends AsyncTask<Void, Void, Void>
//    {
//        private ProgressDialog progressDialog =
//                new ProgressDialog(context, R.style.AppTheme_ProgressDialog);
//
//        @Override
//        protected void onPreExecute()
//        {
//            super.onPreExecute();
//            progressDialog.setCanceledOnTouchOutside(false);
//            progressDialog.setMessage("\tLoading Music...");
//            progressDialog.show();
//        }
//
//        @Override
//        protected Void doInBackground(Void... params)
//        {
//            scan();
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid)
//        {
//            super.onPostExecute(aVoid);
//            progressDialog.dismiss();
//            scanComplete();
//        }
//    }

//    private class AlbumCoverScanner extends AsyncTask<Void, Void, Void>
//    {
//        private Notice notice;
//
//        @Override
//        protected void onPreExecute()
//        {
//            super.onPreExecute();
//            notice = new Notice(context);
//            notice.setNoticeText("Loading album covers...");
//            notice.setNotClickable();
//            notice.doNotDismiss();
//            notice.show();
//        }
//
//        @Override
//        protected Void doInBackground(Void... params)
//        {
//            scanCovers();
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid)
//        {
//            super.onPostExecute(aVoid);
//            notice.dismiss();
//            audioScannerListener.onUpdate();
//
//            if(!data.stored())
//                new Store().execute();
//        }
//    }

//    private class Store extends AsyncTask<Void, Void, Void>
//    {
//        @Override
//        protected Void doInBackground(Void... params)
//        {
//            store();
//            return null;
//        }
//    }

//    private class BackgroundQuery extends AsyncTask<Void, Void, Void>
//    {
//        SongDatabaseHelper sd;
//
//        @Override
//        protected Void doInBackground(Void... params)
//        {
//            sd = new SongDatabaseHelper(context);
//            songs = sd.getSongs();
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid)
//        {
//            super.onPostExecute(aVoid);
//            sd.close();         // fixes leaky database
//            scanComplete();
//        }
//    }
}