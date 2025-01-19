package org.chat.db_obj;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

public class ChatJDBC {

    // db config
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/chat_db";
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "postgres";


    // If the username and password present in DB, returns an instance of UserProfile, else null
    // For the future: find a way to keep the same connection until the app is closed.
    public static UserSession validateLogin(String username, String password, UUID sessionToken) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            PreparedStatement ps = conn.prepareStatement("select * from users where username = ? and password = ?");

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            // valid user
            if (rs.next()) {

                long userId = rs.getLong("id");
                if (isValidSession(conn, userId, sessionToken)) {
                    insertNewSession(conn, userId, sessionToken);
                    return new UserSession(sessionToken, new UserProfile(username, userId));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // not valid user
        return null;
    }

    public static boolean isValidSession(Connection conn, long userId, UUID sessionToken) {

        try {
            PreparedStatement ps = conn.prepareStatement("" +
                    "select token, last_access " +
                    "from session " +
                    "where user_id = ? and last_access > current_timestamp - interval '5 minutes';");
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("token").equals(sessionToken.toString());
            } else {
                return true;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void insertNewSession(Connection conn, long userId, UUID sessionToken) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "insert into session(user_id, token, last_access) " +
                            "values(?, ?::UUID, current_timestamp)");
            ps.setLong(1, userId);
            ps.setString(2, sessionToken.toString());
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
