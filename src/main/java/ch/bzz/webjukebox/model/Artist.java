package ch.bzz.webjukebox.model;

public class Artist {
    private int artistID;
    private String name;

    public Artist() {

    }

    public void setArtistID(int artistID) {
        this.artistID = artistID;
    }

    public int getArtistID() {
        return artistID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
