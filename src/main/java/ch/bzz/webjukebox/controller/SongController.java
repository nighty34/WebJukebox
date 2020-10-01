package ch.bzz.webjukebox.controller;

import ch.bzz.webjukebox.utils.Database;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ch.bzz.webjukebox.model.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Vector;

@RestController
public class SongController {

    @GetMapping("/rest/allsongs")
    public Vector<Song> getAllSongs() {
        Vector<Song> songs = Database.retrieveAllSongs();

        Vector<String> files = getResourcesFiles("static/music");
        if(files.size()>0) {
            Vector<Song> removeSongs = new Vector<>();

            for (Song song : songs) {
                if (!(files.contains(song.getFilepath()))) {
                    System.out.println("The song: " + song.getName() + " does not exist.");
                    removeSongs.add(song);
                }
            }

            for (Song song : removeSongs) {
                songs.remove(song);
            }
        }else{
            System.out.println("No files were recognised.");
        }

        return songs;
    }

    @GetMapping("/rest/allartists")
    public Vector<Artist> getAllArtists() {
        Vector<Artist> artists = Database.retrieveAllArtists();

        return artists;
    }

    @GetMapping("/rest/allgenres")
    public Vector<Genre> getAllGenres() {
        Vector<Genre> genres = Database.retrieveAllGenres();

        return genres;
    }

    @PostMapping("/rest/plusonestream")
    public void plusOneStream(@RequestParam(value = "songID") int songID) {
        try {
        Database.plusOneStream(songID);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Error when upping the stream count of song (ID: " + songID + ")", ex
            );
        }
    }

    private Vector<String> getResourcesFiles(String path){
        Vector<String> filenames = new Vector<>();

        try {
            InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String resource;
            while((resource = reader.readLine())!=null){
                filenames.add(resource);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return filenames;
    }

    @PostMapping("/rest/add/song")
    public void addSong(@RequestParam(value = "songName") String songName,
                        @RequestParam(value = "filePath") String filePath,
                        @RequestParam(value = "coverPath") String coverPath,
                        @RequestParam(value = "artistID") int artistID,
                        @RequestParam(value = "genreID") int genreID) {

        Song song = new Song();
        song.setName(songName);
        song.setFilepath(filePath);
        song.setCoverpath(coverPath);
        song.setStreams(0);
        try {
            Artist artist = Database.retrieveArtist(artistID);
            if (artist != null) {
                song.setArtist(artist);
            } else {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "The requested artist was not found in the database."
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Error while retrieving artist information", e
            );
        }

        try {
            Genre genre = Database.retrieveGenre(genreID);
            if (genre != null) {
                song.setGenre(genre);
            } else {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "The requested Genre was not found."
                );
            }
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Error while retrieving genre.", e
            );
        }

        try {
            Database.addSong(song);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Song could not be saved", e
            );
        }
    }


    @PostMapping("/rest/add/artist")
    public void addArtist(@RequestParam(value = "artistName") String artistName) {
        Artist artist = new Artist();
        artist.setName(artistName);
        try {
            Database.addArtist(artist);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Error when trying to write to DB, artist not saved."
            );
        }
    }

    @PostMapping("/rest/add/genre")
    public void addGenre(@RequestParam(value = "genreName") String genreName) {
        Genre genre = new Genre();
        genre.setName(genreName);

        try {
            Database.addGenre(genre);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Error when trying to write to DB, genre not saved."
            );
        }
    }

}



