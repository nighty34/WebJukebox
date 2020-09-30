package ch.bzz.webjukebox.controller;

import ch.bzz.webjukebox.utils.Database;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ch.bzz.webjukebox.model.*;

import java.util.Vector;

@RestController
public class SongController {

    @GetMapping("/rest/allsongs")
    public Vector<Song> getAllSongs() {

        return Database.retrieveAllSongs();
    }

    @PostMapping("/rest/plusonestream")
    public void plusOneStream(@RequestParam(value = "songID") int songID) {

        // implement logic
    }

}
