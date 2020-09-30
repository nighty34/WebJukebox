package ch.bzz.webjukebox.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ch.bzz.webjukebox.model.*;

import java.util.Vector;

@RestController
public class ArtistController {
    @GetMapping("/allsongs")
    public Vector<Artist> getAllSongs() {

        // Implement logic

        return null;
    }

    @PostMapping("/plusonestream")
    public void plusOneStream(@RequestParam(value = "songID") int songID) {

        // implement logic
    }

}
