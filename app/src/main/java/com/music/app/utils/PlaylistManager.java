package com.music.app.utils;

//TODO: Query playlists and store to MediaStore!

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"WeakerAccess", "TryFinallyCanBeTryWithResources"})
public class PlaylistManager
{
    public static Map<Long, String> getPlaylists(Context context)
    {
        Map<Long, String> playlists = new HashMap<>();

        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        String[] columns =
                {
                    MediaStore.Audio.Playlists._ID,
                    MediaStore.Audio.Playlists.NAME
                };
        int idColumn = 0, nameColumn = 1;

        ContentResolver content = context.getContentResolver();
        Cursor c1 = content.query(uri, columns, null, null, null);
        try
        {
            while(c1.moveToNext())
            {
                String playlistName = c1.getString(nameColumn);
                String playlistID = c1.getString(idColumn);
                playlists.put(Long.parseLong(playlistID), playlistName);
            }
        }
        catch (Exception e) { e.printStackTrace(); }
        finally { c1.close(); }

        return playlists;
    }

    public static List<Long> getPlaylistMembers(Context context, long playlistID)
    {
        List<Long> members = new ArrayList<>();

        Uri membersURI = MediaStore.Audio.Playlists.Members
            .getContentUri("external", playlistID);
        String[] columns = { MediaStore.Audio.Playlists.Members.AUDIO_ID, };
        int idColumn = 0;

        ContentResolver content = context.getContentResolver();
        Cursor cursor = content.query(membersURI, columns, null, null, null);
        try
        {
            while(cursor.moveToNext())
                members.add(cursor.getLong(idColumn));
        }
        catch (Exception e) { e.printStackTrace(); }
        finally { cursor.close(); }

        return members;
    }

    public static void addPlaylist(Context context, String playlistName, long[] songIDs)
    {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.NAME, playlistName);
        ContentResolver content = context.getContentResolver();
        content.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);

        if(songIDs != null)
        {
            if(songIDs.length > 0)
            {
                Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
                String[] column = { MediaStore.Audio.Playlists._ID };
                String selection = MediaStore.Audio.Playlists.NAME;
                int idColumn = 0;
                Cursor c = content.query
                        (uri, column, selection + " = \'"+ playlistName + "\'", null, null);
                try
                {
                    if(c.moveToFirst())
                    {
                        Uri u = MediaStore.Audio.Playlists.Members.getContentUri
                                ("external", Long.parseLong(c.getString(idColumn)));
                        values = new ContentValues();

                        for (int i = 0; i < songIDs.length; i++)
                        {
                            values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, songIDs[i]);
                            values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, i);
                            content.insert(u, values);
                        }
                    }
                }
                finally { c.close(); }
            }
        }
    }

    public static void addToPlaylist
            (Context context, long playlistID, long songID, int songIndex)
    {
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        String[] column = { MediaStore.Audio.Playlists._ID };
        String selection = MediaStore.Audio.Playlists._ID;
        int idColumn = 0;

        ContentResolver content = context.getContentResolver();
        Cursor c = content.query
                (uri, column, selection + " = \'"+ playlistID + "\'", null, null);

        try
        {
            if(c.moveToFirst())
            {
                Uri u = MediaStore.Audio.Playlists.Members.getContentUri
                        ("external", Long.parseLong(c.getString(idColumn)));
                ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, songID);
                values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, songIndex);
                content.insert(u, values);
            }
        }
        finally { c.close(); }
    }

    public static void deletePlaylist(Context context, long playlistID)
    {
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        String id = MediaStore.Audio.Playlists._ID;
        ContentResolver content = context.getContentResolver();
        content.delete(uri, id + " = \'" + playlistID + "\'", null);
    }

//    private static final String playlistsPrefsName = "Playlists";
//    private static final String playlistCountKey = "playlistCount";
//
//    public ArrayList<Playlist> getPlaylists(Context context)
//    {
//        return null;
//    }
//
//    public static void addNewPlaylist(Context context, String playlistName, int playlistCount)
//    {
//        SharedPreferences.Editor editor = context.getSharedPreferences
//                (playlistsPrefsName, Context.MODE_PRIVATE).edit();
//        editor.putInt(playlistCountKey, playlistCount);
//
//        //TODO: Try String Set
//        for (int i = 0; i < playlists.size(); i++)
//            editor.putString(String.valueOf(i), playlists.get(i).getName());
//
//        editor.apply();
//    }
//
//    public static void addNewSongToPlaylist(Context context, long songID, String playlistName)
//    {
//
//    }
}
