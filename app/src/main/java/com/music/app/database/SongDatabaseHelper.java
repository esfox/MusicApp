package com.music.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.music.app.objects.Song;

import java.util.ArrayList;

public class SongDatabaseHelper extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private Context context;

    public SongDatabaseHelper(Context pContext)
    {
        super(pContext, DB.DATABASE_NAME, null, DATABASE_VERSION);
        context = pContext;
    }

    public void add(Song song)
    {
        this.getWritableDatabase().insert(DB.SONG_TABLE, null, getContentValues(song));
    }

    public void update(Song song)
    {
        this.getWritableDatabase().update(DB.SONG_TABLE, getContentValues(song), DB.SONG_UUID + " = " + song.getUUID(), null);
    }

    public void delete(Song song)
    {
        this.getWritableDatabase().delete(DB.SONG_TABLE, DB.SONG_UUID + " = ?", new String[] { song.getUUID() });
    }

    public Song getCurrentSong(long id)
    {
        Song song = new Song();
        Cursor cursor = this.getReadableDatabase().query(DB.SONG_TABLE, null,
                DB.SONG_ID + " = ?", new String[] { String.valueOf(id) },
                null, null, null);

        if(cursor.moveToFirst())
        {
            song.setUUID(cursor.getString(cursor.getColumnIndex(DB.SONG_UUID)));
            song.setId(Long.parseLong(cursor.getString(cursor.getColumnIndex(DB.SONG_ID))));
            song.setPath(cursor.getString(cursor.getColumnIndex(DB.SONG_PATH)));
            song.setFilename(cursor.getString(cursor.getColumnIndex(DB.SONG_FILENAME)));
            song.setTitle(cursor.getString(cursor.getColumnIndex(DB.SONG_TITLE)));
            song.setArtist(cursor.getString(cursor.getColumnIndex(DB.SONG_ARTIST)));
            song.setAlbumArtist(cursor.getString(cursor.getColumnIndex(DB.SONG_ALBUM_ARTIST)));
            song.setAlbum(cursor.getString(cursor.getColumnIndex(DB.SONG_ALBUM)));
            song.setReleaseYear(cursor.getString(cursor.getColumnIndex(DB.SONG_RELEASE_YEAR)));
            song.setGenre(cursor.getString(cursor.getColumnIndex(DB.SONG_GENRE)));
            song.setDuration(cursor.getString(cursor.getColumnIndex(DB.SONG_DURATION)));
            song.setAlbumID(Long.parseLong(cursor.getString(cursor.getColumnIndex(DB.SONG_ALBUM_ID))));
            song.setCoverPath(cursor.getString(cursor.getColumnIndex(DB.SONG_COVER_PATH)));
        }

        cursor.close();

        return song;
    }

    public ArrayList<Song> getSongs()
    {
        ArrayList<Song> songs = new ArrayList<>();

        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT count(*) FROM " + DB.SONG_TABLE, null);
        cursor.moveToFirst();
        if(cursor.getInt(0) > 0)
        {
            cursor = this.getReadableDatabase().rawQuery("SELECT * FROM " + DB.SONG_TABLE, null);

            if(cursor.moveToFirst())
            {
                do
                {
                    final Song song = new Song();
                    song.setUUID(cursor.getString(cursor.getColumnIndex(DB.SONG_UUID)));
                    song.setId(Long.parseLong(cursor.getString(cursor.getColumnIndex(DB.SONG_ID))));
                    song.setPath(cursor.getString(cursor.getColumnIndex(DB.SONG_PATH)));
                    song.setFilename(cursor.getString(cursor.getColumnIndex(DB.SONG_FILENAME)));
                    song.setTitle(cursor.getString(cursor.getColumnIndex(DB.SONG_TITLE)));
                    song.setArtist(cursor.getString(cursor.getColumnIndex(DB.SONG_ARTIST)));
                    song.setAlbumArtist(cursor.getString(cursor.getColumnIndex(DB.SONG_ALBUM_ARTIST)));
                    song.setAlbum(cursor.getString(cursor.getColumnIndex(DB.SONG_ALBUM)));
                    song.setReleaseYear(cursor.getString(cursor.getColumnIndex(DB.SONG_RELEASE_YEAR)));
                    song.setGenre(cursor.getString(cursor.getColumnIndex(DB.SONG_GENRE)));
                    song.setDuration(cursor.getString(cursor.getColumnIndex(DB.SONG_DURATION)));
                    song.setAlbumID(Long.parseLong(cursor.getString(cursor.getColumnIndex(DB.SONG_ALBUM_ID))));

//                    byte[] coverBytes = cursor.getBlob(cursor.getColumnIndex(DB.SONG_COVER_PATH));
//                    Bitmap cover = BitmapFactory.decodeByteArray(coverBytes, 0, coverBytes.length);
//                    song.setCover(new BitmapDrawable(context.getResources(), cover));

                    songs.add(song);

                } while(cursor.moveToNext());
            }
        }

        cursor.close();

        return songs;
    }

    private ContentValues getContentValues(final Song song)
    {
        final ContentValues values = new ContentValues();
        values.put(DB.SONG_UUID, song.getUUID());
        values.put(DB.SONG_ID, song.getId());
        values.put(DB.SONG_PATH, song.getPath());
        values.put(DB.SONG_FILENAME, song.getFilename());
        values.put(DB.SONG_TITLE, song.getTitle());
        values.put(DB.SONG_ARTIST, song.getArtist());
        values.put(DB.SONG_ALBUM_ARTIST, song.getAlbumArtist());
        values.put(DB.SONG_ALBUM, song.getAlbum());
        values.put(DB.SONG_RELEASE_YEAR, song.getReleaseYear());
        values.put(DB.SONG_GENRE, song.getGenre());
        values.put(DB.SONG_DURATION, song.getDuration());
        values.put(DB.SONG_ALBUM_ID, song.getAlbumID());
        values.put(DB.SONG_COVER_PATH, song.getCoverPath());

//        Bitmap coverBitmap = ((BitmapDrawable) song.getCover()).getBitmap();
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        coverBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] coverBytes = stream.toByteArray();
//
//        values.put(DB.SONG_COVER_PATH, coverBytes);
//
//        try { stream.close(); }
//        catch(IOException e) { e.printStackTrace(); }

        return values;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String createSongsQuery = "CREATE TABLE "
                + DB.SONG_TABLE
                + " ("
                + DB.SONG_DB_ID + " INTEGER PRIMARY KEY, "
                + DB.SONG_UUID + " TEXT, "
                + DB.SONG_ID + " TEXT, "
                + DB.SONG_PATH + " TEXT, "
                + DB.SONG_FILENAME + " TEXT, "
                + DB.SONG_TITLE + " TEXT, "
                + DB.SONG_ARTIST + " TEXT, "
                + DB.SONG_ALBUM_ARTIST + " TEXT, "
                + DB.SONG_ALBUM + " TEXT, "
                + DB.SONG_RELEASE_YEAR + " TEXT, "
                + DB.SONG_GENRE + " TEXT, "
                + DB.SONG_DURATION + " TEXT, "
                + DB.SONG_ALBUM_ID + " TEXT, "
                + DB.SONG_COVER_PATH + " TEXT"
                + " );";

        db.execSQL(createSongsQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        String dropSongsQuery = "DROP TABLE IF EXISTS " + DB.SONG_TABLE;
        db.execSQL(dropSongsQuery);
        onCreate(db);
    }
}
