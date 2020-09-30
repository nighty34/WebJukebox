package ch.bzz.webjukebox.utils;

import com.github.jsixface.YamlConfig;

import java.io.*;

public class Configuration {

    private static YamlConfig config;
    private static String path = Configuration.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "config.yml";;


    public static void init() {
        createIfNotExists();

        try{
        InputStream resource = new FileInputStream(path);
            System.out.println(resource.available());
            config = YamlConfig.load(resource);
        }catch (IOException ex){
            ex.printStackTrace();
            System.out.println("Config konnte nicht gelesen werden.");
        }
        //TODO: Replace try catch with throws
    }

    public static void createIfNotExists() {

        System.out.println(path);
        File configFile = new File(path);

        if (configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("The File could not be created at " + path);
            }
        }

        try {
            writeStandardConfig(configFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not write standard config information to config file");
        }
    }

    private static void writeStandardConfig(File configFile) throws IOException {

        FileWriter fileWriter = new FileWriter(configFile);

        String contents = "MySQL:\n" +
                "  database: jukebox\n" +
                "  host: localhost\n" +
                "  port: 3306\n" +
                "  username: root\n" +
                "  password: \"\"\n" +
                "  poolsize: 100";

        fileWriter.write(contents);
        System.out.println("WRITE");
        fileWriter.close();
    }


    public static YamlConfig getConfig() {
        return config;
    }
}
