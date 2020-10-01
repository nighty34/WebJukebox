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
            System.out.println(Thread.currentThread().getContextClassLoader().getResource("static").getPath());
            Vector<String> files = getResourcesFiles("static/music");

            for (Song song:songs) {
                if(!(files.contains(song.getFilepath()))){
                    System.out.println("Der Song: " + song.getName() + " existiert nicht");
                    songs.removeElement(song);
                }
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }



        return songs;
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
                System.out.println(resource);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }





        return filenames;
    }

}



