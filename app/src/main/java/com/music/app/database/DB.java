package com.music.app.database;

import android.provider.BaseColumns;

public class DB implements BaseColumns
{
    public DB() {}

    public static final String DATABASE_NAME = "MAIN_APP_DB";

    public static final String SONG_TABLE           = "tracks";
    public static final String SONG_DB_ID           = "_id";
    public static final String SONG_UUID            = "uuid";
    public static final String SONG_ID              = "song_id";
    public static final String SONG_PATH            = "path";
    public static final String SONG_FILENAME        = "filename";
    public static final String SONG_TITLE           = "title";
    public static final String SONG_ARTIST          = "artist";
    public static final String SONG_ALBUM_ARTIST    = "album_artist";
    public static final String SONG_ALBUM           = "album";
    public static final String SONG_RELEASE_YEAR    = "release_year";
    public static final String SONG_GENRE           = "genre";
    public static final String SONG_DURATION        = "duration";
    public static final String SONG_ALBUM_ID        = "album_id";
    public static final String SONG_COVER           = "album_cover";
}
