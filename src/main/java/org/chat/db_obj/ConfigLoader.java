package org.chat.db_obj;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    private static final Properties prop = new Properties();

    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("Configuration file not found");
            }
            prop.load(input);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    public static String getProperty(String key) {
        return prop.getProperty(key);
    }
}
