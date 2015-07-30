package org.bravo.garden;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Volker Braun, MaibornWolff GmbH on 27.07.15.
 */
public class Config {

    private static String CONFIG_FILE = "garden.properties";

    public Properties loadConfig() {
        Properties config = new Properties();
        try {
            InputStream stream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE);
            if (stream != null) {
                config.load(stream);
            } else {
                throw new FileNotFoundException("Config file " + CONFIG_FILE + " not found in the classpath.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return config;
    }
}
