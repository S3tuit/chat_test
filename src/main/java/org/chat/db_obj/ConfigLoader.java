package org.chat.db_obj;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    private static final Properties prop = new Properties();

    static {
        try (InputStream in = ConfigLoader.class.getResourceAsStream("application.properties")) {
            if (in == null) {
                throw new RuntimeException("Configuration file not found");
            }
            prop.load(in);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    public static String getProperty(String key) {
        return prop.getProperty(key);
    }
}
