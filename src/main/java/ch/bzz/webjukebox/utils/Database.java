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

    /**
     * Opens connection to db
     */
    private static void openConnection() {
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


    /**
     * Initilizes database
     */
    public static void init(){
        openConnection();

        try {
            String sqlCreateMusic = "CREATE TABLE IF NOT EXISTS music (" +
                    "songid INT NOT NULL AUTO_INCREMENT," +
                    "title VARCHAR(45) NULL," +
                    "filepath VARCHAR(45) NOT NULL," +
                    "coverpath VARCHAR(45) NULL," +
                    "streams INT NOT NULL," +
                    "artistid INT NOT NULL," +
                    "genreid INT NOT NULL," +
                    "PRIMARY KEY (songid)," +
                    "FOREIGN KEY (artistid) REFERENCES artist(artistid)," +
                    "FOREIGN KEY (artistid) REFERENCES genre(genreid));";

            String sqlCreateArtist = "CREATE TABLE IF NOT EXISTS artist (" +
                    "artistid INT NOT NULL AUTO_INCREMENT," +
                    "artistname VARCHAR(45) NULL," +
                    "PRIMARY KEY (artistid));";

            String sqlCreateGenres = "CREATE TABLE IF NOT EXISTS genre (" +
                    "genreid INT NOT NULL AUTO_INCREMENT," +
                    "genrename VARCHAR(45) NULL," +
                    "PRIMARY KEY (genreid));";


            Connection conn = getConnection();
            PreparedStatement pstMusic = createPreparedStatement(conn, sqlCreateMusic);
            PreparedStatement pstArtist = createPreparedStatement(conn, sqlCreateArtist);
            PreparedStatement pstGenres = createPreparedStatement(conn, sqlCreateGenres);


            pstArtist.execute();
            pstGenres.execute();
            pstMusic.execute();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Creates a preparedstatement
     * @param conn db connection
     * @param sqlString sql command
     * @return preparedstatement
     * @throws SQLException
     */
    public static PreparedStatement createPreparedStatement(Connection conn, String sqlString) throws SQLException {
        return conn.prepareStatement(sqlString);
    }

    /**
     * Closes and reopens db connection/conenctionpool
     */
    public static void reloadDB(){
        readConfig();
        hikari.close();
        init();
    }

    /**
     * Returns connection
     * @return hikariconnection
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException{
        return hikari.getConnection();
    }

    /**
     * Reads and saves configuration file
     */
    private static void readConfig(){
        host = Configuration.getConfig().getString("MySQL.host");
        database = Configuration.getConfig().getString("MySQL.database");
        username = Configuration.getConfig().getString("MySQL.username");
        password = Configuration.getConfig().getString("MySQL.password");
        poolSize = Configuration.getConfig().getInt("MySQL.poolsize");
        port = Configuration.getConfig().getInt("MySQL.port");
    }

    // Retrieving methods from here onwards

    public static Vector<Song> retrieveAllSongs() {

        String selectStatement = "SELECT * FROM music";

        Vector<Song> songs = null;
        Vector<Artist> artists = null;
        Vector<Genre> genres = null;

        try {
            Connection conn = getConnection();
            PreparedStatement pstMusic = conn.prepareStatement(selectStatement);
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
                song.setSongID(results.getInt("songid"));
                song.setName(results.getString("title"));
                song.setCoverpath(results.getString("coverpath"));
                song.setFilepath(results.getString("filepath"));
                song.setStreams(results.getInt("streams"));

                song.setGenre(genres.get(results.getInt("genreid") - 1));
            }

            closeStatement(pstMusic, conn);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throwables.getMessage();
        }

        return songs;

    }

    public static Song retrieveSong(int songID) {
        String selectStatement = "SELECT * FROM music WHERE songid=" + songID;

        Song song = null;


        try {
            Connection conn = getConnection();
            PreparedStatement pstMusic = conn.prepareStatement(selectStatement);
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

            closeStatement(pstMusic, conn);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println(throwables.getMessage());
        }

        return song;
    }

    public static Vector<Artist> retrieveAllArtists() {

        String selectStatement = "SELECT * FROM artist";

        Vector<Artist> artists = null;

        try {
            Connection conn = getConnection();
            PreparedStatement pstArtist = conn.prepareStatement(selectStatement);
            ResultSet results = pstArtist.executeQuery();

            artists = new Vector<>();
            Artist artist;

            while (results.next()) {
                artist = new Artist();
                artist.setArtistID(results.getInt("artistid"));
                artist.setName(results.getString("artistname"));
                artists.add(artist);
            }

            closeStatement(pstArtist, conn);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println(throwables.getMessage());
        }

        return artists;
    }

    public static Artist retrieveArtist(int artistID) {
        String selectStatement = "SELECT * FROM artist WHERE artistid=" + artistID;

        Artist artist = null;

        try {
            Connection conn = getConnection();
            PreparedStatement pstArtist = conn.prepareStatement(selectStatement);
            ResultSet result = pstArtist.executeQuery();

            while (result.next()) {
                artist = new Artist();
                artist.setArtistID(result.getInt("artistid"));
                artist.setName(result.getString("artistname"));
            }

            closeStatement(pstArtist, conn);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println(throwables.getMessage());
        }

        return artist;
    }

    public static Genre retrieveGenre(int genreID) {
        String selectStatement = "SELECT * FROM genre WHERE genreid=" + genreID;

        Genre genre = null;

        try {
            Connection conn = getConnection();
            PreparedStatement pstGenre = conn.prepareStatement(selectStatement);
            ResultSet result = pstGenre.executeQuery();

            while (result.next()) {
                genre = new Genre();
                genre.setGenreID(result.getInt("genreid"));
                genre.setName(result.getString("genrename"));
            }

            closeStatement(pstGenre, conn);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println(throwables.getMessage());
        }

        return genre;
    }

    public static Vector<Genre> retrieveAllGenres() {
        String selectStatement = "SELECT * FROM genre";

        Genre genre = null;
        Vector<Genre> genres = null;

        try {
            Connection conn = getConnection();
            PreparedStatement pstGenre = conn.prepareStatement(selectStatement);
            ResultSet result = pstGenre.executeQuery();

            genres = new Vector<>();

            while (result.next()) {
                genre = new Genre();
                genre.setGenreID(result.getInt("genreid"));
                genre.setName(result.getString("genrename"));
                genres.add(genre);
            }

            closeStatement(pstGenre, conn);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println(throwables.getMessage());
        }

        return genres;
    }

    public static void addSong(Song song) {

        String insertStatement = "INSERT INTO music (title, filepath, coverpath, streams, artistid, genreid)" +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = getConnection();
            PreparedStatement pstMusic = conn.prepareStatement(insertStatement);

            pstMusic.setString(1, song.getName());
            pstMusic.setString(2, song.getFilepath());
            pstMusic.setString(3, song.getCoverpath());
            pstMusic.setInt(4, song.getStreams());
            pstMusic.setInt(5, song.getArtist().getArtistID());
            pstMusic.setInt(6, song.getGenre().getGenreID());

            pstMusic.executeUpdate();

            closeStatement(pstMusic, conn);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println(throwables.getMessage());
        }

    }

    public static void addGenre(Genre genre) {

        String insertStatement = "INSERT INTO genre (genrename)" +
                "VALUES (?)";

        simpleInsert(insertStatement, genre.getName());

        /*try {
            Connection conn = getConnection();
            PreparedStatement pstGenre = conn.prepareStatement(insertStatement);

            pstGenre.setString(1, genre.getName());

            pstGenre.executeUpdate();

            closeStatement(pstGenre, conn);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println(throwables.getMessage());
        }*/
    }

    public static void addArtist(Artist artist) {

        String insertStatement = "INSERT INTO artist (artistname)" +
                "VALUES (?)";

        simpleInsert(insertStatement, artist.getName());
        /*try {
            Connection conn = getConnection();
            PreparedStatement pstArtist = conn.prepareStatement(isnsertStatement);

            pstArtist.setString(1, artist.getName());

            pstArtist.executeUpdate();

            closeStatement(pstArtist, conn);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println(throwables.getMessage());
        }*/
    }


    public static void simpleInsert(String insertStatement, String argument){
        try{
            Connection conn = getConnection();
            PreparedStatement pst = conn.prepareStatement(insertStatement);
            pst.setString(1, argument);

            pst.execute();

            closeStatement(pst, conn);
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }



    public static void closeStatement(PreparedStatement statement, Connection conn){
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
    }


}
