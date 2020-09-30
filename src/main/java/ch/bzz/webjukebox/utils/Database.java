package ch.bzz.webjukebox.utils;

import ch.bzz.webjukebox.model.*;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class Database {
    private static String host, database, username, password;
    private static int port, poolSize;
    private static HikariDataSource hikari;


    private static void openConection(){
        readConfig();
        hikari = new HikariDataSource();

        hikari.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
        hikari.addDataSourceProperty("serverName", host);
        hikari.addDataSourceProperty("port", port);
        hikari.addDataSourceProperty("databaseName", database);
        hikari.addDataSourceProperty("user", username);
        hikari.addDataSourceProperty("password", password);
        hikari.setMaximumPoolSize(poolSize);

    }

    public static void init(){
        openConection();

        try {
            String sqlCreateMusic = "CREATE TABLE IF NOT EXISTS music (" +
                    "songid INT NOT NULL AUTO_INCREMENT," +
                    "title VARCHAR(45) NULL," +
                    "filepath VARCHAR(45) NOT NULL," +
                    "coverpath VARCHAR(45) NULL," +
                    "streams INT NOT NULL," +
                    "artistid INT NOT NULL," +
                    "genreid INT NOT NULL," +
                    "PRIMARY KEY (titleid),";
            //TODO: FOREIGN KEYS

            String sqlCreateArtist = "CREATE TABLE IF NOT EXISTS artist (" +
                    "artistid INT NOT NULL AUTO_INCREMENT," +
                    "artistname VARCHAR(45) NULL," +
                    "RIMARY KEY (artistid))";

            String sqlCreateGenres = "CREATE TABLE IF NOT EXISTS genres (" +
                    "genreid INT NOT NULL," +
                    "genrename VARCHAR(45) NULL," +
                    "PRIMARY KEY (genreid))";


            Connection con = getConnection();
            PreparedStatement pstMusic = createPreparedStatement(con, sqlCreateMusic);
            PreparedStatement pstArtist = createPreparedStatement(con, sqlCreateArtist);
            PreparedStatement pstGenres = createPreparedStatement(con, sqlCreateGenres);

            pstMusic.execute();
            pstArtist.execute();
            pstGenres.execute();
        }catch (SQLException e){
            //TODO: ERROR
        }
    }


    public static PreparedStatement createPreparedStatement(Connection con, String sqlString) throws SQLException {
        return con.prepareStatement(sqlString);
    }

    public static void reloadDB(){
        readConfig();
        hikari.close();
    }

    public static Connection getConnection() throws SQLException{
        return hikari.getConnection();
    }

    private static void readConfig(){
        host = Configuration.getConfig().getString("MySQL.host");
        port = Configuration.getConfig().getInt("MySQL.port");
        database = Configuration.getConfig().getString("MySQL.database");
        username = Configuration.getConfig().getString("MySQL.username");
        password = Configuration.getConfig().getString("MySQL.password");
        poolSize = Configuration.getConfig().getInt("MySQL.poolsize");
    }

    // Retrieving methods from here onwards

    public static Vector<Song> retrieveAllSongs() {

        String selectStatement = "select * from music";

        Vector<Song> songs = null;
        Vector<Artist> artists = null;
        Vector<Genre> genres = null;

        try {
            Connection con = getConnection();
            PreparedStatement pstMusic = con.prepareStatement(selectStatement);
            ResultSet results = pstMusic.executeQuery();

            songs = new Vector<>();
            artists = retrieveAllArtists();
            genres = retrieveAllGenres();

            Song song;

            while (results.next()) {
                song = new Song();
                song.setArtist(artists.get(results.getInt("artistid") - 1)); // TODO write code that retrieves the corresponding artist.
                // Either do that by first getting a vector of all artists or by only getting the required artist.
                // --> decided to go for retrieveAllArtists in order to keep sql queries to a minimum.
                song.setSongID(results.getInt("titleid"));
                song.setName(results.getString("title"));
                song.setCoverpath(results.getString("coverpath"));
                song.setFilepath(results.getString("filepath"));
                song.setStreams(results.getInt("streams"));

                song.setGenre(genres.get(results.getInt("genreid") - 1));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return songs;

    }

    public static Song retrieveSong(int songID) {
        String selectStatement = "select * from music where songid=" + songID;

        Song song = null;


        try {
            Connection con = getConnection();
            PreparedStatement pstMusic = con.prepareStatement(selectStatement);
            ResultSet result = pstMusic.executeQuery();

            while (result.next()) {
                song = new Song();
                song.setArtist(retrieveArtist(result.getInt("artistid")));
                song.setSongID(result.getInt("titleid"));
                song.setName(result.getString("title"));
                song.setCoverpath(result.getString("coverpath"));
                song.setFilepath(result.getString("filepath"));
                song.setStreams(result.getInt("streams"));
                song.setGenre(retrieveGenre(result.getInt("genreid")));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return song;
    }

    public static Vector<Artist> retrieveAllArtists() {

        String selectStatement = "select * from artist";

        Vector<Artist> artists = null;

        try {
            Connection con = getConnection();
            PreparedStatement pstArtist = con.prepareStatement(selectStatement);
            ResultSet results = pstArtist.executeQuery();

            artists = new Vector<>();
            Artist artist;

            while (results.next()) {
                artist = new Artist();
                artist.setArtistID(results.getInt("artistid"));
                artist.setName(results.getString("artistname"));
                artists.add(artist);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return artists;
    }

    public static Artist retrieveArtist(int artistID) {
        String selectStatement = "select * from artist where artistid=" + artistID;

        Artist artist = null;

        try {
            Connection con = getConnection();
            PreparedStatement pstArtist = con.prepareStatement(selectStatement);
            ResultSet result = pstArtist.executeQuery();

            while (result.next()) {
                artist = new Artist();
                artist.setArtistID(result.getInt("artistid"));
                artist.setName(result.getString("artistname"));
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return artist;
    }

    public static Genre retrieveGenre(int genreID) {
        String selectStatement = "select * from genre where genreid=" + genreID;

        Genre genre = null;

        try {
            Connection con = getConnection();
            PreparedStatement pstGenre = con.prepareStatement(selectStatement);
            ResultSet result = pstGenre.executeQuery();

            while (result.next()) {
                genre = new Genre();
                genre.setGenreID(result.getInt("genreid"));
                genre.setName(result.getString("genrename"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return genre;
    }

    public static Vector<Genre> retrieveAllGenres() {
        String selectStatement = "select * from genre";

        Genre genre = null;
        Vector<Genre> genres = null;

        try {
            Connection con = getConnection();
            PreparedStatement pstGenre = con.prepareStatement(selectStatement);
            ResultSet result = pstGenre.executeQuery();

            genres = new Vector<>();

            while (result.next()) {
                genre = new Genre();
                genre.setGenreID(result.getInt("genreid"));
                genre.setName(result.getString("genrename"));
                genres.add(genre);
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return genres;
    }
}
