package ch.bzz.webjukebox.utils;

import com.github.jsixface.YamlConfig;

import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Path;

public class Configuration {

    private static YamlConfig config;
    private static String path = Configuration.class.getProtectionDomain().getCodeSource().getLocation().getPath() + File.separator + "config.yml";;


    public static void init() {

        createIfNotExists();

        InputStream resource = new Configuration().getClass()
                .getClassLoader()
                .getResourceAsStream(path);
        config = YamlConfig.load(resource);
    }

    public static void createIfNotExists() {

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
    }


    public static YamlConfig getConfig() {
        return config;
    }
}
