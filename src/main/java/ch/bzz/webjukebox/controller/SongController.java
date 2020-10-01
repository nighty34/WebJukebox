package ch.bzz.webjukebox.controller;

import ch.bzz.webjukebox.utils.Database;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ch.bzz.webjukebox.model.*;

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
        try {
            Vector<String> files = getResourcesFiles("static/music");
            if(files.size()>0) {

                for (Song song : songs) {
                    if (!(files.contains(song.getFilepath()))) {
                        System.out.println("Der Song: " + song.getName() + " existiert nicht");
                        songs.removeElement(song);
                    }
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

    @GetMapping("/rest/allGenres")
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
    public void addSong(@RequestParam(value = "songName")String songName,
                        @RequestParam(value = "filePath")String filePath,
                        @RequestParam(value = "coverPath") String coverPath,
                        @RequestParam(value = "artistID") int artistID,
                        @RequestParam(value = "genreID") int genreID) {

        Song song = new Song();
        song.setName(songName);
        song.setFilepath(filePath);
        song.setCoverpath(coverPath);
        song.setStreams(0);

        Artist artist = Database.retrieveArtist(artistID);
        song.setArtist(artist);

        Genre genre = Database.retrieveGenre(genreID);
        song.setGenre(genre);

        Database.addSong(song);
    }


    @PostMapping("/rest/add/artist")
    public void addArtist(@RequestParam(value = "artistName") String artistName) {
        Artist artist = new Artist();
        artist.setName(artistName);

        Database.addArtist(artist);
    }

    @PostMapping("/rest/add/genre")
    public void addGenre(@RequestParam(value = "genreName") String genreName) {
        Genre genre = new Genre();
        genre.setName(genreName);

        Database.addGenre(genre);
    }

}



