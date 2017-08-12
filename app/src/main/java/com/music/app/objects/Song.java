package com.music.app.objects;

import android.graphics.drawable.Drawable;

public class Song
{
    private String UUID;
    private Long id;
    private String path;
    private String filename;
    private String title;
    private String artist;
    private String albumArtist;
    private String album;
    private String releaseYear;
    private String genre;
    private String duration;
    private Long albumID;
    private Drawable cover;

    public Song() {}

    public String getUUID() { return UUID; }
    public Long getId() { return id; }
    public String getPath() { return path; }
    public String getFilename() { return filename; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAlbumArtist() { return albumArtist; }
    public String getAlbum() { return album; }
    public String getReleaseYear() { return releaseYear; }
    public String getGenre() { return genre; }
    public String getDuration() { return duration; }
    public Long getAlbumID() { return albumID; }
    public Drawable getCover() { return cover; }

    public void setUUID(String UUID) { this.UUID = UUID; }
    public void setId(Long id) { this.id = id; }
    public void setPath(String path) { this.path = path; }
    public void setFilename(String file) { this.filename = file; }
    public void setTitle(String title) { this.title = title; }
    public void setArtist(String artist) { this.artist = artist; }
    public void setAlbumArtist(String albumArtist) { this.albumArtist = albumArtist; }
    public void setAlbum(String album) { this.album = album; }
    public void setReleaseYear(String releaseYear) { this.releaseYear = releaseYear; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setDuration(String duration) { this.duration = duration; }
    public void setAlbumID(Long albumID) { this.albumID = albumID; }
    public void setCover(Drawable cover) { this.cover = cover; }
}
