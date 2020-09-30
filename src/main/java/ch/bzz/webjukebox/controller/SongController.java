package ch.bzz.webjukebox.controller;

import ch.bzz.webjukebox.utils.Database;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ch.bzz.webjukebox.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Vector;

@RestController
public class SongController {

    @GetMapping("/rest/allsongs")
    public Vector<Song> getAllSongs() {

        return Database.retrieveAllSongs();
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

}
