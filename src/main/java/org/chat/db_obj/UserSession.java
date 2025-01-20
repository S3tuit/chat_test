package org.chat.db_obj;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

// config for db connection
import static org.chat.db_obj.ChatJDBC.*;

public class UserSession {

    private UUID token;
    private UserProfile profile;
    Thread threadUpdLastAccess = null;

    public UserSession(UUID token, UserProfile userProfile) {
        this.token = token;
        this.profile = userProfile;
    }

    public UserSession() {}

    public String getUsername() {
        return profile.getUsername();
    }

    public long getUserId() {
        return profile.getUserID();
    }

    // If the username and password present in DB, assigns them to the instance variables and return true, else false.
    public boolean validateLogin(String username, String password, UUID sessionToken) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            PreparedStatement ps = conn.prepareStatement("select id from users where username = ? and password = ?");

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            // valid user
            if (rs.next()) {

                long userId = rs.getLong("id");
                int sessionValidity = isValidSession(conn, userId, sessionToken);
                if (sessionValidity == 1) {
                    insertNewSession(conn, userId, sessionToken);
                    this.setSession(sessionToken, new UserProfile(username, userId));
                    return true;

                } else if (sessionValidity == 2) {
                    updateSession(conn, userId);
                    this.setSession(sessionToken, new UserProfile(username, userId));
                    return true;
                }
                // To do: handle when there's a session with different token and recent last_access
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // not valid user
        return false;
    }

    // return 0 if there already is a session with a different token and a last_access in 5 minutes ago.
    // return 1 if session with different token and a last_access older than 5 minutes ago... or no session.
    // return 2 if there already is a session with the same token.
    public int isValidSession(Connection conn, long userId, UUID sessionToken) {

        try {
            PreparedStatement ps = conn.prepareStatement("" +
                    "select token " +
                    "from session " +
                    "where user_id = ? and last_access > current_timestamp - interval '5 minutes';");
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                if (rs.getString("token").equals(sessionToken.toString())) {
                    return 2;
                };
                return 0;
            } else {
                return 1;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void insertNewSession(Connection conn, long userId, UUID sessionToken) {
        try {
            PreparedStatement ps = conn.prepareStatement("delete from session where user_id = ?;");
            ps.setLong(1, userId);
            ps.executeUpdate();

            ps = conn.prepareStatement(
                    "insert into session(user_id, token, last_access) " +
                            "values(?, ?::UUID, current_timestamp)");
            ps.setLong(1, userId);
            ps.setString(2, sessionToken.toString());
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateSession(Connection conn, long userId) {
        try {
            PreparedStatement ps = conn.prepareStatement("" +
                    "update session " +
                    "set last_access = current_timestamp " +
                    "where user_id = ?;");
            ps.setLong(1, userId);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setSession(UUID sessionToken, UserProfile userProfile) {
        this.token = sessionToken;
        this.profile = userProfile;

        if (threadUpdLastAccess == null || !threadUpdLastAccess.isAlive()) {
            threadUpdLastAccess = new Thread(new UpdateLastAccess());
            threadUpdLastAccess.start();
        }
    }

    // returns true if db has a session with that userId and token
    public boolean authenticate() {
        if (token == null || profile == null) {
            return false;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            PreparedStatement ps = conn.prepareStatement("" +
                    "select 1 " +
                    "from session " +
                    "where user_id = ? " +
                    "  and token = ?::UUID;");
            ps.setLong(1, profile.getUserID());
            ps.setString(2, token.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public class UpdateLastAccess implements Runnable {

        @Override
        public void run() {
            while (true) {
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                    updateSession(conn, getUserId());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    // Sleep for 4 minutes and 30 seconds
                    Thread.sleep(4 * 60 * 1000 + 30 * 1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupted status
                    break;
                }
            }
        }
    }
}
