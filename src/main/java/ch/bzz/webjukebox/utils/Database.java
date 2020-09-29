package ch.bzz.webjukebox.utils;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database {
    private static String host, database, username, password;
    private static int port, poolSize;
    private static HikariDataSource hikari;


    private static void openConection(){
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
        //TODO: Set host, etc.
        try {
            String sqlCreateMusic = "CREATE TABLE IF NOT EXISTS music (" +
                    "titleid INT NOT NULL AUTO_INCREMENT," +
                    "title VARCHAR(45) NULL," +
                    "filepath VARCHAR(45) NOT NULL," +
                    "coverpath VARCHAR(45) NULL," +
                    "streams INT NOT NULL," +
                    "artistid INT NOT NULL," +
                    "genreid INT NOT NULL," +
                    "PRIMARY KEY (`titleid`),";
            //TODO: FOREIGN KEY

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
        hikari.close();
    }

    public static Connection getConnection() throws SQLException{
        return hikari.getConnection();
    }
}
