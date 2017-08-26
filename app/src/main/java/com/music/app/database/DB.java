package com.music.app.database;

import android.provider.BaseColumns;

class DB implements BaseColumns
{
    public DB() {}

    static final String DATABASE_NAME = "MAIN_APP_DB";

    static final String SONG_TABLE           = "tracks";
    static final String SONG_DB_ID           = "_id";
    static final String SONG_ID              = "song_id";
    static final String SONG_PATH            = "path";
    static final String SONG_FILENAME        = "filename";
    static final String SONG_TITLE           = "title";
    static final String SONG_ARTIST          = "artist";
    static final String SONG_ALBUM_ARTIST    = "album_artist";
    static final String SONG_ALBUM           = "album";
    static final String SONG_RELEASE_YEAR    = "release_year";
    static final String SONG_GENRE           = "genre";
    static final String SONG_DURATION        = "duration";
    static final String SONG_ALBUM_ID        = "album_id";
    static final String SONG_COVER_PATH      = "album_cover_path";
}
