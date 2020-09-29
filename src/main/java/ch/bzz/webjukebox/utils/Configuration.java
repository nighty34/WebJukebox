package ch.bzz.webjukebox.utils;

import com.github.jsixface.YamlConfig;

import java.io.InputStream;

public class Configuration {

    private static YamlConfig config;

    public static void init(String path){
        InputStream resource = new Configuration().getClass()
                .getClassLoader()
                .getResourceAsStream(path);
        config = YamlConfig.load(resource);
    }


    public static YamlConfig getConfig() {
        return config;
    }
}
