package ch.bzz.webjukebox.model;

public class Song {

    private int songID;
    private String name;
    private String filepath;
    private String coverpath;
    private int streams;
    private Genre genre;
    private Artist artist;

    public Song() {

    }

    public void setSongID(int songID) {
        this.songID = songID;
    }

    public int getSongID() {
        return songID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getCoverpath() {
        return coverpath;
    }

    public void setCoverpath(String coverpath) {
        this.coverpath = coverpath;
    }

    public int getStreams() {
        return streams;
    }

    public void setStreams(int streams) {
        this.streams = streams;
    }

    public void plusOneStream() {

    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }
}
