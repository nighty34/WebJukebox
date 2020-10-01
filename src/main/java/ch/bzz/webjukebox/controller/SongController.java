package ch.bzz.webjukebox.controller;

import ch.bzz.webjukebox.utils.Database;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ch.bzz.webjukebox.model.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

@RestController
public class SongController {

    @GetMapping("/rest/allsongs")
    public Vector<Song> getAllSongs() {
        Vector<Song> songs = Database.retrieveAllSongs();
        try {
            Vector<String> files = getResourcesFiles("static/music");
            if(files.size()>0) {
                Vector<Song> removeSongs = new Vector<>();


                for (Song song : songs) {
                    if (!(files.contains(song.getFilepath()))) {
                        System.out.println("Der Song: " + song.getName() + " existiert nicht");
                        removeSongs.add(song);
                    }
                }

                for (Song song : removeSongs) {
                    songs.remove(song);
                }
            }else{
                System.out.println("Es wurden keine Files erkannt.");
            }
        }catch (IOException ex){
            ex.printStackTrace();
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
            Connection connection = Database.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE music SET streams = streams + 1 WHERE songid = ?;");
            preparedStatement.setInt(1, songID);


            new Thread(() -> {
                try {
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    if (preparedStatement != null) {
                        try {
                            preparedStatement.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).run();
        }catch (SQLException ex){
            ex.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Error when upping the stream count of song (ID: " + songID + ")"
            );
        }
    }

    private Vector<String> getResourcesFiles(String path) throws IOException {
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
            song.setArtist(artist);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "The requested Artist wasn't found."
            ); // This could also mean "Internal Server Error", but we can't really differentiate here.
        }

        try {
            Genre genre = Database.retrieveGenre(genreID);
            song.setGenre(genre);
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "The requested Genre was not found."
            );
        }

        try {
            Database.addSong(song);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Song could not be saved"
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



