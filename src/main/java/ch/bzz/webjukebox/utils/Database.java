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
                    "titleid INT NOT NULL AUTO_INCREMENT," +
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

    public static Vector<Song> retrieveSongsFromDB() {

        String selectStatement = "select * from music";

        Vector<Song> songs = null;

        try {
            Connection con = getConnection();
            PreparedStatement pstMusic = con.prepareStatement(selectStatement);
            ResultSet results = pstMusic.executeQuery();

            songs = new Vector<>();
            Song song;
            Artist artist;

            while (results.next()) {
                song = new Song();
                song.setArtist(new Artist()); // TODO write code that retrieves the corresponding artist.
                // Either do that by first getting a vector of all artists or by only getting the required artist.
                song.setSongID(results.getInt("titleid"));
                song.setName(results.getString("title"));
                song.setCoverpath(results.getString("coverpath"));
                song.setFilepath(results.getString("filepath"));
                song.setStreams(results.getInt("streams"));

                song.setGenre(Genre.POP); //TODO implement genre queries
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return songs;

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

    public static Artist getArtist(int artistID) {
        String selectStatement = "select * from artist where artistid=" + artistID;

        Artist artist = null;

        try {
            Connection con = getConnection();
            PreparedStatement pstArtist = con.prepareStatement(selectStatement);
            ResultSet results = pstArtist.executeQuery();

            while (results.next()) {
                artist = new Artist();
                artist.setArtistID(results.getInt("artistid"));
                artist.setName(results.getString("artistname"));
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return artist;
    }
}
